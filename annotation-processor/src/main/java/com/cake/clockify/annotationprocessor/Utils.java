package com.cake.clockify.annotationprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.lang.model.type.DeclaredType;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.cake.clockify.annotationprocessor.Constants.CLOCKIFY_PREFIX;
import static com.cake.clockify.annotationprocessor.Constants.REGEX_METHOD_NAME_SPLIT;
import static com.cake.clockify.annotationprocessor.Constants.REGEX_UPPER_CASE_SPLIT;
import static java.util.Collections.emptyList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    private static final Locale LOCALE = Locale.US;

    private static final Map<String, String> DEFINITION_CLASSNAME_MAPPINGS = Map.of(
            "lifecycle", "lifecycleEvent"
    );

    @SneakyThrows
    public static JsonNode readManifestDefinition(ObjectMapper mapper, String manifestPath) {
        InputStream is = Utils.class.getClassLoader().getResourceAsStream(manifestPath);
        return mapper.readTree(is);
    }

    public static String normalizeUppercaseOnlyValue(String value) {
        if (value.toUpperCase(LOCALE).equals(value)) {
            return value.toLowerCase(LOCALE);
        }
        return value;
    }

    public static String toMethodName(String value) {
        List<String> parts = new LinkedList<>();
        for (String part : value.split(REGEX_METHOD_NAME_SPLIT)) {
            // all uppercase values should not be split
            part = normalizeUppercaseOnlyValue(part);

            // split words on uppercase values
            for (String s : part.split(REGEX_UPPER_CASE_SPLIT)) {
                parts.add(s.toLowerCase(LOCALE));
            }
        }

        if (parts.size() <= 1) {
            return normalizeUppercaseOnlyValue(value);
        }

        StringBuilder sb = new StringBuilder(value.length());

        Iterator<String> iterator = parts.iterator();
        sb.append(iterator.next().toLowerCase(LOCALE));
        iterator.forEachRemaining(v -> sb.append(capitalize(v)));

        return sb.toString();
    }

    public static String toClassName(String value) {
        return capitalize(toMethodName(value));
    }

    public static String capitalize(String value) {
        if (value == null) {
            return null;
        }

        if (value.length() == 1) {
            return value.toUpperCase(Locale.US);
        }

        return value.substring(0, 1).toUpperCase(Locale.US) + value.substring(1);
    }

    public static List<String> getStringValuesFromNode(JsonNode node) {
        List<String> values = new LinkedList<>();
        node.forEach(v -> values.add(v.asText()));
        return values;
    }

    public static List<String> getFieldNamesFromNode(JsonNode node) {
        List<String> values = new LinkedList<>();
        node.fieldNames().forEachRemaining(values::add);
        return values;
    }

    public static List<String> getEnumValuesFromNode(JsonNode node) {
        if (!node.has(NodeConstants.ENUM)) {
            return emptyList();
        }

        List<String> values = new LinkedList<>();
        node.get(NodeConstants.ENUM).forEach(n -> values.add(n.asText()));
        return values;
    }

    public static boolean hasDefinitionRef(JsonNode node) {
        if (node == null) {
            return false;
        }
        return node.has(NodeConstants.REF);
    }

    public static String getDefinitionRef(JsonNode node) {
        String definition = node.get(NodeConstants.REF).asText();
        return definition.substring(definition.lastIndexOf("/") + 1);
    }

    public static JsonNode getDefinitionNode(JsonNode manifest, JsonNode node) {
        return manifest.get(NodeConstants.DEFINITIONS).get(getDefinitionRef(node));
    }

    public static String getNodeType(JsonNode node, JsonNode definitions) {
        if (node.has(NodeConstants.TYPE)) {
            return node.get(NodeConstants.TYPE).asText();
        }

        if (hasDefinitionRef(node)) {
            String ref = getDefinitionRef(node);
            if ("url".equals(ref)) {
                return NodeConstants.STRING;
            }

            if (definitions.has(ref)) {
                return getNodeType(definitions.get(ref), definitions);
            }
        }

        return NodeConstants.STRING;
    }

    public static String[] getPackageAndClassNames(DeclaredType type) {
        String qualifiedName = type.asElement().toString();
        int lastDot = qualifiedName.lastIndexOf(".");

        String packageName = qualifiedName.substring(0, lastDot);
        String className = qualifiedName.substring(lastDot + 1);
        return new String[] {packageName, className};
    }

    /**
     * @param packageName
     * @param definition
     * @return the typename for the given definition, after applying name mappings
     */
    public static TypeName getDefinitionTypeName(String packageName, String definition) {
        return ClassName.get(packageName, getDefinitionSimpleClassName(definition));
    }

    /**
     * @param definition
     * @return the class name for the definition, after applying name mappings
     */
    public static String getDefinitionSimpleClassName(String definition) {
        String classNameSuffix = DEFINITION_CLASSNAME_MAPPINGS.getOrDefault(definition, definition);
        return Utils.toClassName(CLOCKIFY_PREFIX + Constants.DELIMITER_NAME_PARTS + classNameSuffix);
    }

    public static String getPropertyDescription(JsonNode node) {
        if (node.has(NodeConstants.DESCRIPTION)) {
            return node.get(NodeConstants.DESCRIPTION).asText();
        }
        return "";
    }

    public static String getVersionedPackageName(JsonNode manifest, String packageName) {
        String version = manifest.get("version").asText();
        String versionSubpackage = "v" + version.replace(".", "_");

        return packageName + "." + versionSubpackage;
    }
}
