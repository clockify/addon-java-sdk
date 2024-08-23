package com.cake.clockify.annotationprocessor.clockify;

import com.cake.clockify.annotationprocessor.NodeConstants;
import com.cake.clockify.annotationprocessor.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.cake.clockify.annotationprocessor.Constants.CLOCKIFY_MODEL_PACKAGE;

class EnumConstantsProcessor {

    private final String packageName;
    private final String definition;

    private final List<String> values = new LinkedList<>();

    public EnumConstantsProcessor(JsonNode manifest, String definition) {
        this.packageName = Utils.getVersionedPackageName(manifest, CLOCKIFY_MODEL_PACKAGE);
        this.definition = definition;

        JsonNode enumNode = manifest.get(NodeConstants.DEFINITIONS).get(definition).get(NodeConstants.ENUM);

        Objects.requireNonNull(enumNode);
        if (!enumNode.isArray()) {
            throw new IllegalArgumentException("The provided definition is not of enum type.");
        }

        enumNode.forEach(node -> values.add(node.asText()));
    }

    public List<JavaFile> process() {
        return List.of(
                JavaFile.builder(packageName, TypeSpec.interfaceBuilder(getInterfaceClassName(definition))
                                .addModifiers(Modifier.PUBLIC)
                                .addFields(values.stream()
                                        .map(enumValue -> FieldSpec.builder(
                                                        String.class,
                                                        enumValue.replace(' ', '_'),
                                                        Modifier.PUBLIC,
                                                        Modifier.STATIC,
                                                        Modifier.FINAL
                                                )
                                                .initializer("\"" + enumValue + "\"")
                                                .build()).toList()
                                )
                                .build())
                        .build()
        );
    }

    private ClassName getInterfaceClassName(String step) {
        return ClassName.get(packageName, Utils.getDefinitionSimpleClassName(step));
    }
}
