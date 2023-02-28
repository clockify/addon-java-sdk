package annotationprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.lang.model.type.DeclaredType;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static annotationprocessor.Constants.MANIFEST_FILE;
import static annotationprocessor.Constants.REGEX_METHOD_NAME_SPLIT;
import static annotationprocessor.Constants.REGEX_UPPER_CASE_SPLIT;
import static java.util.Collections.emptyList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    private static final Locale LOCALE = Locale.US;

    @SneakyThrows
    public static JsonNode readManifestDefinition() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = Utils.class.getClassLoader().getResourceAsStream(MANIFEST_FILE);
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
        for (String part: value.split(REGEX_METHOD_NAME_SPLIT)) {
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
        if (!node.has("enum")) {
            return emptyList();
        }

        List<String> values = new LinkedList<>();
        node.get("enum").forEach(n -> values.add(n.asText()));
        return values;
    }

    public static List<String> getOptionalProperties(List<String> requiredProperties,
                                                     List<String> allProperties) {
        return allProperties.stream()
                .filter(p -> !requiredProperties.contains(p))
                .toList();
    }

    public static boolean hasDefinitionRef(JsonNode node) {
        if (node == null) {
            return false;
        }
        return node.has("$ref");
    }

    public static String getDefinitionRef(JsonNode node) {
        String definition = node.get("$ref").asText();
        return definition.substring(definition.lastIndexOf("/") + 1);
    }

    public static String getNodeType(JsonNode node) {
        return node.has("type")
                ? node.get("type").asText()
                : "object";
    }

    public static String[] getPackageAndClassNames(DeclaredType type) {
        String qualifiedName = type.asElement().toString();
        int lastDot = qualifiedName.lastIndexOf(".");

        String packageName = qualifiedName.substring(0, lastDot);
        String className = qualifiedName.substring(lastDot + 1);
        return new String[] {packageName, className};
    }
}
