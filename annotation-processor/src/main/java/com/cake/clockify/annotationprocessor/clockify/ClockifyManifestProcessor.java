package com.cake.clockify.annotationprocessor.clockify;

import com.cake.clockify.annotationprocessor.NodeConstants;
import com.cake.clockify.annotationprocessor.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.squareup.javapoet.JavaFile;

import javax.lang.model.type.DeclaredType;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ClockifyManifestProcessor {
    private final JsonNode manifest;

    private final String packageName;
    private final String className;

    public ClockifyManifestProcessor(DeclaredType type, String manifestPath) {
        this.manifest = Utils.readManifestDefinition(new ObjectMapper(), manifestPath);

        String[] qualifiedNames = Utils.getPackageAndClassNames(type);
        this.packageName = qualifiedNames[0];
        this.className = qualifiedNames[1];
    }

    public List<JavaFile> process() {
        return Stream.concat(getObjectDefinitions().stream(), getManifestDefinition().stream()).toList();
    }

    private List<JavaFile> getObjectDefinitions() {
        List<JavaFile> javaFiles = new LinkedList<>();

        manifest.get(NodeConstants.DEFINITIONS).fields().forEachRemaining(entry -> {
            String definition = entry.getKey();
            JsonNode node = entry.getValue();

            if (isObjectDefinition(node)) {
                DefinitionProcessor processor = new DefinitionProcessor(
                        manifest,
                        Utils.getDefinitionSimpleClassName(definition),
                        definition
                );

                javaFiles.addAll(processor.process());
            } else if (isEnumDefinition(node)) {
                EnumConstantsProcessor processor = new EnumConstantsProcessor(
                        manifest,
                        definition
                );

                javaFiles.addAll(processor.process());
            }
        });

        return javaFiles;
    }

    private List<JavaFile> getManifestDefinition() {
        return new DefinitionProcessor(manifest, className, null).process();
    }

    private boolean isObjectDefinition(JsonNode node) {
        return NodeConstants.OBJECT.equals(Utils.getNodeType(node, manifest.get(NodeConstants.DEFINITIONS)));
    }

    private boolean isEnumDefinition(JsonNode node) {
        String type = Utils.getNodeType(node, manifest.get(NodeConstants.DEFINITIONS));
        return NodeConstants.STRING.equals(type) && node.get(NodeConstants.ENUM) instanceof ArrayNode;
    }
}
