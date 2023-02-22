package annotationprocessor.clockify;

import annotationprocessor.Processor;
import annotationprocessor.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static annotationprocessor.Constants.DELIMITER_NAME_PARTS;

public class ClockifyManifestProcessor implements Processor {

    private static final String TEMPLATE_INTERFACE_NAME = "Builder%1$sStep";
    private static final String TEMPLATE_DEFAULT_METHOD = "return %1$s (\"%2$s\")";

    private static final String STEP_BUILDER = "Build";
    private static final String STEP_OPTIONAL = "Optional";

    private static final String CLOCKIFY_PREFIX = "Clockify";

    private static final Map<String, Map<String, String>> propertyPrefixes = Map.of(
            "component", Map.of(
                    "accessLevel", "allow"
            ),
            "lifecycle", Map.of(
                    "type", "on"
            ),
            "webhook", Map.of(
                    "event", "on"
            ),
            "setting", Map.of(
                    "type", "as"
            )
    );

    protected JsonNode manifest;

    protected String packageName;
    protected String className;
    protected String definition;

    public ClockifyManifestProcessor(DeclaredType type, String definition) {
        this.definition = definition;
        this.manifest = Utils.readManifestDefinition();

        String[] qualifiedNames = Utils.getPackageAndClassNames(type);
        this.packageName = qualifiedNames[0];
        this.className = qualifiedNames[1];
    }

    @Override
    public Class<?> getSupportedAnnotation() {
        return ExtendClockifyManifest.class;
    }

    @Override
    public List<JavaFile> process() {
        List<JavaFile> javaFiles = new LinkedList<>();

        JsonNode propertiesNode = getProperties();
        JsonNode requiredPropertiesNode = getRequiredProperties();

        List<String> requiredProperties = Utils.getStringValuesFromNode(requiredPropertiesNode);
        List<String> allProperties = Utils.getFieldNamesFromNode(propertiesNode);

        boolean hasOptionalStep = requiredProperties.size() != allProperties.size();
        ClassName lastStepClassName = getLastStepClassName(hasOptionalStep);

        // each required property will have its own interface to guide the user through
        // the build process for the object
        for (int i = 0; i < requiredProperties.size(); i++) {
            List<MethodSpec> methods = new LinkedList<>();
            String property = requiredProperties.get(i);

            ClassName currentInterfaceName = getInterfaceClassName(property);
            ClassName nextInterfaceName = i == requiredProperties.size() - 1
                    ? lastStepClassName
                    : getInterfaceClassName(requiredProperties.get(i + 1));

            JsonNode propertyNode = propertiesNode.get(property);
            TypeName propertyType = getTypeNameFromPropertyNode(propertyNode);

            // adding primary setter method
            methods.add(getPropertySetterMethod(property, propertyType, nextInterfaceName));

            // for enum nodes, adding default helper methods
            methods.addAll(getEnumSetterMethods(propertyNode, property, nextInterfaceName));

            TypeSpec clazz = getInterfaceClass(currentInterfaceName, methods);
            javaFiles.add(JavaFile.builder(packageName, clazz).build());
        }

        // if there are optional properties, they will be included in the same optional step
        // otherwise, a build step will be used
        if (hasOptionalStep) {
            javaFiles.add(getOptionalStepFile(lastStepClassName,
                    Utils.getOptionalProperties(requiredProperties, allProperties),
                    propertiesNode));
        } else {

            javaFiles.add(getBuildStepFile(lastStepClassName));
        }

        return javaFiles;
    }

    private List<MethodSpec> getEnumSetterMethods(JsonNode node, String property,
                                                  ClassName nextInterfaceName) {
        return Utils.getEnumValuesFromNode(node).stream().map(value -> {

            String methodName = getEnumSetterMethodName(property, value);
            String statement = TEMPLATE_DEFAULT_METHOD.formatted(property, value);

            return MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                    .returns(nextInterfaceName)
                    .addStatement(statement)
                    .build();
        }).toList();
    }

    private ClassName getLastStepClassName(boolean hasOptionalStep) {
        return getInterfaceClassName(hasOptionalStep ? STEP_OPTIONAL : STEP_BUILDER);
    }

    private String getEnumSetterMethodName(String property, String propertyValue) {
        String value = propertyValue;
        if (propertyPrefixes.containsKey(definition)) {
            Map<String, String> prefixes = propertyPrefixes.get(definition);
            if (prefixes.containsKey(property)) {
                value = prefixes.get(property) + DELIMITER_NAME_PARTS + propertyValue;
            }
        }

        return Utils.toMethodName(value);
    }

    private JavaFile getOptionalStepFile(ClassName interfaceName, List<String> optionalProperties,
                                         JsonNode properties) {
        List<MethodSpec> methods = new LinkedList<>();

        for (String property : optionalProperties) {
            JsonNode propertyNode = properties.get(property);
            TypeName propertyType = getTypeNameFromPropertyNode(propertyNode);

            methods.add(getPropertySetterMethod(property, propertyType, interfaceName));
        }

        methods.add(getBuildMethod());

        TypeSpec clazz = getInterfaceClass(interfaceName, methods);
        return JavaFile.builder(packageName, clazz).build();
    }

    private JavaFile getBuildStepFile(ClassName interfaceName) {
        TypeSpec clazz = getInterfaceClass(interfaceName, List.of(getBuildMethod()));
        return JavaFile.builder(packageName, clazz).build();
    }

    private MethodSpec getPropertySetterMethod(String property, TypeName propertyType,
                                               ClassName returnType) {

        return MethodSpec.methodBuilder(Utils.toMethodName(property))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(returnType)
                .addParameter(propertyType, "value")
                .build();
    }

    private TypeName getTypeNameFromPropertyNode(JsonNode node) {
        return switch (Utils.getNodeType(node)) {
            case "boolean" -> TypeName.get(Boolean.class);
            case "string" -> TypeName.get(String.class);

            case "object" -> Utils.hasDefinitionRef(node)
                    ? getDefinitionTypeName(Utils.getDefinitionRef(node))
                    : ParameterizedTypeName.get(Map.class, String.class, Object.class);

            case "array" -> Utils.hasDefinitionRef(node.get("items"))
                    ? ParameterizedTypeName.get(ClassName.get(List.class),
                    getDefinitionTypeName(Utils.getDefinitionRef(node.get("items"))))
                    : ParameterizedTypeName.get(List.class, String.class);

            default -> TypeName.get(Object.class);
        };
    }

    private JsonNode getProperties() {
        return manifest.get("definitions")
                .get(definition)
                .get("properties");
    }

    private JsonNode getRequiredProperties() {
        return manifest.get("definitions")
                .get(definition)
                .get("required");
    }

    private ClassName getInterfaceClassName(String property) {
        return ClassName.get(packageName,
                className + TEMPLATE_INTERFACE_NAME.formatted(Utils.toClassName(property)));
    }

    private MethodSpec getBuildMethod() {
        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ClassName.get(packageName, className))
                .build();
    }

    private TypeSpec getInterfaceClass(ClassName className, List<MethodSpec> methods) {
        return TypeSpec.interfaceBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methods)
                .build();
    }

    private TypeName getDefinitionTypeName(String definition) {
        return ClassName.get(packageName,
                Utils.toClassName(CLOCKIFY_PREFIX + DELIMITER_NAME_PARTS + definition));
    }
}
