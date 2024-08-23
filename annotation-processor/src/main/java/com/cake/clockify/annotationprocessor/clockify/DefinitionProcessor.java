package com.cake.clockify.annotationprocessor.clockify;

import com.cake.clockify.annotationprocessor.Constants;
import com.cake.clockify.annotationprocessor.NodeConstants;
import com.cake.clockify.annotationprocessor.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.cake.clockify.annotationprocessor.Constants.CLOCKIFY_MANIFEST_INTERFACE;
import static com.cake.clockify.annotationprocessor.Constants.CLOCKIFY_MODEL_PACKAGE;
import static com.cake.clockify.annotationprocessor.Constants.CLOCKIFY_PATH_INTERFACE;

class DefinitionProcessor {

    private static final String TEMPLATE_INTERFACE_NAME = "Builder%1$sStep";
    private static final String TEMPLATE_DEFAULT_METHOD = "return %1$s (\"%2$s\")";

    private static final String STEP_BUILDER = "Build";
    private static final String STEP_OPTIONAL = "Optional";
    private static final String PROPERTY_SCHEMA_VERSION = "schemaVersion";

    private final JsonNode manifest;
    private final JsonNode propertiesNode;

    private final String packageName;
    private final String className;
    private final String definition;

    private final List<String> properties;
    private final List<String> requiredProperties;
    private final List<String> optionalProperties;

    public DefinitionProcessor(JsonNode manifest, String className, @Nullable String definition) {
        this.manifest = manifest;

        this.packageName = Utils.getVersionedPackageName(manifest, CLOCKIFY_MODEL_PACKAGE);
        this.className = className;
        this.definition = definition;

        this.propertiesNode = readProperties(NodeConstants.PROPERTIES);

        this.properties = Utils.getFieldNamesFromNode(propertiesNode);
        this.requiredProperties = Utils.getStringValuesFromNode(readProperties(NodeConstants.REQUIRED));
        this.optionalProperties = properties.stream()
                .filter(p -> !requiredProperties.contains(p))
                .toList();
    }

    public JsonNode readProperties(String key) {
        if (definition == null) {
            return manifest.get(key);
        }

        return manifest.get(NodeConstants.DEFINITIONS).get(definition).get(key);
    }

    public List<JavaFile> process() {
        List<TypeSpec> interfaces = getBuilderStepClasses();

        List<TypeSpec> specs = new LinkedList<>();
        specs.add(getModelClass(interfaces));
        specs.addAll(interfaces);
        specs.add(getModelBuilderClass(interfaces));

        return specs.stream().map(t -> JavaFile.builder(packageName, t).build()).toList();
    }

    private List<TypeSpec> getBuilderStepClasses() {
        List<TypeSpec> types = new LinkedList<>();

        boolean optionalStep = requiredProperties.size() != properties.size();
        String lastStep = optionalStep ? STEP_OPTIONAL : STEP_BUILDER;

        ClassName lastStepClass = getInterfaceStepClassName(lastStep);

        // each required property will have its own interface to guide the user through
        // the build process for the object
        for (int i = 0; i < requiredProperties.size(); i++) {
            String property = requiredProperties.get(i);

            ClassName nextInterface = i == requiredProperties.size() - 1
                    ? lastStepClass
                    : getInterfaceStepClassName(requiredProperties.get(i + 1));

            List<MethodSpec> methods = new LinkedList<>();
            // adding primary setter method
            methods.add(getPropertySetterMethod(property, nextInterface));
            // for enum nodes, adding default helper methods
            methods.addAll(getEnumSetterMethods(property, nextInterface));

            types.add(getInterfaceClass(property, methods));
        }

        // if there are optional properties, they will be included in the same optional step
        // otherwise, a build step will be used
        if (optionalStep) {
            types.add(getOptionalStepClass(lastStep));

        } else {
            types.add(getInterfaceClass(lastStep, List.of(getBuildMethod())));
        }

        return types;
    }

    private TypeSpec getModelClass(List<TypeSpec> interfaces) {
        List<FieldSpec> fields = properties.stream()
                .map(p -> PROPERTY_SCHEMA_VERSION.equals(p) ? getSchemaVersionField() : getModelPropertyField(p))
                .toList();

        List<MethodSpec> methods = new LinkedList<>();

        methods.add(getModelConstructor(fields));
        methods.add(getModelBuilderMethod(interfaces.get(0)));

        for (FieldSpec field : fields) {
            methods.add(getModelGetterMethod(field));
            if (isManifestDefinition() && "settings".equals(field.name)) {
                methods.add(getModelSetterMethod(field));
            }
        }

        return TypeSpec.classBuilder(ClassName.get(packageName, className))
                .addSuperinterfaces(getModelSuperInterfaces())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addFields(fields)
                .addMethods(methods)
                .build();
    }

    private List<ClassName> getModelSuperInterfaces() {
        return Stream.of(
                        isManifestDefinition() ? ClassName.get(CLOCKIFY_MODEL_PACKAGE, CLOCKIFY_MANIFEST_INTERFACE) : null,
                        shouldImplementPathInterface() ? ClassName.get(CLOCKIFY_MODEL_PACKAGE, CLOCKIFY_PATH_INTERFACE) : null
                )
                .filter(Objects::nonNull)
                .toList();
    }

    private TypeSpec getModelBuilderClass(List<TypeSpec> interfaces) {
        ClassName modelName = ClassName.get(packageName, className);
        ClassName builderName = ClassName.get(packageName, className + "Builder");

        List<FieldSpec> fields = new LinkedList<>();
        List<MethodSpec> methods = new LinkedList<>();

        for (String property : properties.stream().filter(p -> !PROPERTY_SCHEMA_VERSION.equals(p)).toList()) {
            fields.add(getModelPropertyField(property));
        }

        for (FieldSpec field : fields) {
            methods.add(getModelBuilderSetterMethod(builderName, field));
        }

        methods.add(MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(modelName)
                .addStatement("return new $L($L)", modelName.simpleName(), CodeBlock.join(
                        fields.stream().map(f -> CodeBlock.of("$N", f)).toList(),
                        ", "
                ))
                .build()
        );

        return TypeSpec.classBuilder(builderName)
                .addSuperinterfaces(interfaces.stream()
                        .map(spec -> ClassName.get("", spec.name))
                        .toList()
                )
                .addFields(fields)
                .addMethods(methods)
                .build();
    }

    private MethodSpec getModelConstructor(List<FieldSpec> fields) {
        fields = fields.stream()
                .filter(f -> !PROPERTY_SCHEMA_VERSION.equals(f.name))
                .toList();

        var constructorBuilder = MethodSpec.constructorBuilder()
                .addParameters(fields.stream()
                        .map(f -> ParameterSpec.builder(f.type, f.name).build())
                        .toList()
                );

        for (String property : requiredProperties) {
            constructorBuilder.addStatement("$T.requireNonNull($N)", Objects.class, property);
        }

        for (FieldSpec field : fields) {
            constructorBuilder.addStatement("this.$N = $N", field, field);
        }

        return constructorBuilder.build();
    }

    private MethodSpec getModelBuilderMethod(TypeSpec firstStep) {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("", firstStep.name))
                .addStatement("return new $N()", ClassName.get(packageName, className + "Builder").simpleName())
                .build();
    }

    private FieldSpec getSchemaVersionField() {
        return FieldSpec.builder(String.class, PROPERTY_SCHEMA_VERSION)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .initializer("$S", manifest.get("version").asText())
                .build();
    }

    private FieldSpec getModelPropertyField(String property) {
        JsonNode node = propertiesNode.get(property);
        TypeName type = getTypeNameFromPropertyNode(node);

        FieldSpec.Builder builder = FieldSpec.builder(type, property);
        if (type instanceof ParameterizedTypeName parametrizedType
                && parametrizedType.rawType.equals(ClassName.get(List.class))) {
            builder.initializer("new $T()", LinkedList.class);
        }
        return builder
                .addModifiers(Modifier.PRIVATE)
                .addJavadoc(Utils.getPropertyDescription(node))
                .build();
    }

    private MethodSpec getModelGetterMethod(FieldSpec field) {
        String propertyMethodName = Utils.toMethodName(field.name);
        String getterName = "get" + Utils.capitalize(propertyMethodName);

        return MethodSpec.methodBuilder(getterName)
                .addModifiers(Modifier.PUBLIC)
                .returns(field.type)
                .addStatement("return $N", field.name)
                .build();
    }

    private MethodSpec getModelSetterMethod(FieldSpec field) {
        String propertyMethodName = Utils.toMethodName(field.name);
        String setterName = "set" + Utils.capitalize(propertyMethodName);

        return MethodSpec.methodBuilder(setterName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "settings")
                .returns(TypeName.VOID)
                .addStatement("this.$L = $L", field.name, field.name)
                .build();
    }

    private MethodSpec getModelBuilderSetterMethod(TypeName builder, FieldSpec field) {
        return MethodSpec.methodBuilder(Utils.toMethodName(field.name))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(builder)
                .addParameter(field.type, field.name)
                .addStatement("this.$N = $N", field.name, field.name)
                .addStatement("return this")
                .build();
    }

    private boolean isManifestDefinition() {
        return properties.contains(PROPERTY_SCHEMA_VERSION);
    }

    private boolean shouldImplementPathInterface() {
        return definition != null && List.of("lifecycle", "webhook", "component").contains(definition);
    }

    private boolean shouldSkipProperty(String property) {
        return PROPERTY_SCHEMA_VERSION.equals(property);
    }

    private TypeSpec getOptionalStepClass(String step) {
        ClassName interfaceName = getInterfaceStepClassName(step);

        List<MethodSpec> methods = Stream.concat(
                optionalProperties.stream()
                        .filter(p -> !shouldSkipProperty(p))
                        .map(p -> getPropertySetterMethod(p, interfaceName)),
                Stream.of(getBuildMethod())
        ).toList();

        return getInterfaceClass(step, methods);
    }

    private List<MethodSpec> getEnumSetterMethods(String property, ClassName nextInterfaceName) {
        JsonNode node = propertiesNode.get(property);
        if (Utils.hasDefinitionRef(node)) {
            node = Utils.getDefinitionNode(manifest, node);
        }

        return Utils.getEnumValuesFromNode(node)
                .stream()
                .map(value ->
                        MethodSpec.methodBuilder(getEnumSetterMethodName(property, value))
                                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                                .returns(nextInterfaceName)
                                .addStatement(TEMPLATE_DEFAULT_METHOD.formatted(property, value))
                                .build())
                .toList();
    }

    private MethodSpec getPropertySetterMethod(String property, ClassName returnType) {
        JsonNode node = propertiesNode.get(property);
        TypeName type = getTypeNameFromPropertyNode(node);

        return MethodSpec.methodBuilder(Utils.toMethodName(property))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(returnType)
                .addParameter(type, "value")
                .addJavadoc(Utils.getPropertyDescription(node))
                .build();
    }

    private TypeName getTypeNameFromPropertyNode(JsonNode node) {
        if (node.has(NodeConstants.ANY_OF)) {
            return TypeName.get(Object.class);
        }

        return switch (Utils.getNodeType(node, manifest.get(NodeConstants.DEFINITIONS))) {
            case "integer" -> TypeName.get(Integer.class);
            case "boolean" -> TypeName.get(Boolean.class);
            case "string" -> TypeName.get(String.class);

            case "object" -> Utils.hasDefinitionRef(node)
                    ? Utils.getDefinitionTypeName(packageName, Utils.getDefinitionRef(node))
                    : ParameterizedTypeName.get(Map.class, String.class, Object.class);

            case "array" -> Utils.hasDefinitionRef(node.get("items"))
                    ? ParameterizedTypeName.get(ClassName.get(List.class),
                    getTypeNameFromPropertyNode(node.get("items")))
                    : ParameterizedTypeName.get(List.class, String.class);

            default -> TypeName.get(Object.class);
        };
    }

    private MethodSpec getBuildMethod() {
        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ClassName.get(packageName, className))
                .build();
    }

    private TypeSpec getInterfaceClass(String property, List<MethodSpec> methods) {
        return TypeSpec.interfaceBuilder(getInterfaceStepClassName(property))
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methods)
                .build();
    }

    private ClassName getInterfaceStepClassName(String step) {
        String name = className + TEMPLATE_INTERFACE_NAME.formatted(Utils.toClassName(step));
        return ClassName.get(packageName, name);
    }

    private String getEnumSetterMethodName(String property, String value) {
        if ("component".equals(definition)) {
            if ("accessLevel".equals(property)) {
                value = "allow" + Constants.DELIMITER_NAME_PARTS + value;
            }

        } else if ("lifecycle".equals(definition)) {
            if ("type".equals(property)) {
                value = "on" + Constants.DELIMITER_NAME_PARTS + value;
            }

        } else if ("webhook".equals(definition)) {
            if ("event".equals(property)) {
                value = "on" + Constants.DELIMITER_NAME_PARTS + value;
            }

        } else if ("setting".equals(definition)) {
            if ("type".equals(property)) {
                value = "as" + Constants.DELIMITER_NAME_PARTS + value;
            } else if ("accessLevel".equals(property)) {
                value = "allow" + Constants.DELIMITER_NAME_PARTS + value;
            }

        } else if (definition == null) {
            if ("minimalSubscriptionPlan".equals(property)) {
                value = "require" + Constants.DELIMITER_NAME_PARTS + value + Constants.DELIMITER_NAME_PARTS + "plan";
            }
        }

        return Utils.toMethodName(value);
    }
}
