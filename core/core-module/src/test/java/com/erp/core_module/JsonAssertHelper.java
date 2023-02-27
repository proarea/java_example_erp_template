package com.erp.core_module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonAssertHelper {

    public static String readJsonFromResources(String pathToFileInClassPath) throws IOException {
        Resource resource = new ClassPathResource(pathToFileInClassPath);
        JsonNode jsonNode = new ObjectMapper().readTree(resource.getURL());
        log.info("Node: {}", jsonNode.toString());
        return jsonNode.toString();
    }

    public static List<String> readJsonFromResource(String pathToFileInClassPath) throws IOException {
        Resource resource = new ClassPathResource(pathToFileInClassPath);
        JsonNode jsonNode = new ObjectMapper().readTree(resource.getURL());
        log.info("Node: {}", jsonNode.toString());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < jsonNode.size(); i++) {
            result.add(jsonNode.get(i).asText());
        }
        return result;
    }

    public static String readJsonFromResources(String pathToFileInClassPath, Map<String, Object> placeholders) throws IOException {
        Resource resource = new ClassPathResource(pathToFileInClassPath);
        JsonNode jsonNode = new ObjectMapper().readTree(resource.getURL());
        log.info("Node: {}", jsonNode.toString());
        String res = jsonNode.toString();
        for (String key : placeholders.keySet()) {
            res = res.replaceAll("\\$\\{" + key + "}", String.valueOf(placeholders.get(key)));
        }
        return res;
    }

    public static void assertResponseAndJsonFile(ResultActions currentExtensions, String jsonFilePath, List<Pair<String, Matcher>> matchers, String... ignoredField) throws Exception {
        JSONAssertCustomComparatorHelper helper = new JSONAssertCustomComparatorHelper();
        for (Pair<String, Matcher> matcher : matchers) {
            helper.addFieldWithMatcher(matcher);
        }
        for (String field : ignoredField) {
            helper.addIgnoredField(field);
        }
        assertResponseAndJsonFile(currentExtensions, helper, jsonFilePath);
    }

    public static void assertResponseAndJsonFile(ResultActions currentExtensions, String jsonFilePath, String... ignoredField) throws Exception {
        JSONAssertCustomComparatorHelper helper = new JSONAssertCustomComparatorHelper();
        for (String field : ignoredField) {
            helper.addIgnoredField(field);
        }
        assertResponseAndJsonFile(currentExtensions, helper, jsonFilePath);
    }

    public static void assertJsons(String firstJson, String secondJson, String... ignoredField) throws JSONException {
        JSONAssertCustomComparatorHelper helper = new JSONAssertCustomComparatorHelper();
        for (String field : ignoredField) {
            helper.addIgnoredField(field);
        }
        JSONAssert.assertEquals(firstJson, secondJson, helper.getComparator());
    }

    public static void assertResultAndJsonPageable(ResultActions currentExtensions, String secondJson, String... ignoredField) throws Exception {
        String[] pageableIgnoredFields = getPageableIgnoreFields();

        String[] result = Arrays.copyOf(ignoredField, ignoredField.length + pageableIgnoredFields.length);
        System.arraycopy(pageableIgnoredFields, 0, result, ignoredField.length, pageableIgnoredFields.length);

        assertResponseAndJsonFile(currentExtensions, secondJson, result);
    }

    private static String[] getPageableIgnoreFields() {
        return new String[]{
                "pageable",
                "totalPages",
                "totalElements",
                "last",
                "numberOfElements",
                "first",
                "number",
                "sort",
                "size",
                "empty"
        };
    }

    public static void assertResponseAndJsonFile(ResultActions currentExtensions, JSONAssertCustomComparatorHelper customComparatorHelper, String jsonFilePath) throws Exception {
        Resource resource = new ClassPathResource(jsonFilePath);
        JsonNode jsonNode = new ObjectMapper().readTree(resource.getURL());
        log.info("Node: {}", jsonNode.toString());
        String expectedJson = jsonNode.toString();
        String requestResponse = currentExtensions.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, requestResponse, customComparatorHelper.getComparator());
    }

    public static void assertResponseAndJsonString(ResultActions currentExtensions, String expectedJson, String... ignoredField) throws Exception {
        JSONAssertCustomComparatorHelper helper = new JSONAssertCustomComparatorHelper();
        for (String field : ignoredField) {
            helper.addIgnoredField(field);
        }
        assertResponseAndJsonString(currentExtensions, helper, expectedJson);
    }

    public static void assertResponseAndJsonString(ResultActions currentExtensions, JSONAssertCustomComparatorHelper customComparatorHelper, String expectedJson) throws Exception {
        String requestResponse = currentExtensions.andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals(requestResponse, expectedJson, customComparatorHelper.getComparator());
    }

    public static class JSONAssertCustomComparatorHelper {
        private List<Customization> customizationList;

        JSONAssertCustomComparatorHelper() {
            customizationList = new ArrayList<>();
        }

        CustomComparator getComparator() {
            return new CustomComparator(JSONCompareMode.STRICT, customizationList.toArray(new Customization[0]));
        }


        public JSONAssertCustomComparatorHelper addIgnoredField(String field) {
            customizationList.add(new Customization("[*]." + field, ignore()));
            customizationList.add(new Customization(field, ignore()));
            return this;
        }

        public JSONAssertCustomComparatorHelper addIgnoredButNotNullField(String field) {
            customizationList.add(new Customization("[*]." + field, (o, t1) -> !t1.equals("null") && !t1.toString().isEmpty()));
            customizationList.add(new Customization(field, (o, t1) -> !t1.equals("null") && !t1.toString().isEmpty()));
            return this;
        }

        public JSONAssertCustomComparatorHelper addFieldWithValueMatcher(String field, ValueMatcher valueMatcher) {
            customizationList.add(new Customization("[*]." + field, valueMatcher));
            customizationList.add(new Customization(field, valueMatcher));
            return this;
        }

        public JSONAssertCustomComparatorHelper addFieldWithMatcher(Pair<String, Matcher> matcher) {
            customizationList.add(new Customization("[*]." + matcher.getKey(), (o, t1) -> matcher.getValue().matches(t1)));
            customizationList.add(new Customization(matcher.getKey(), (o, t1) -> matcher.getValue().matches(t1)));
            return this;
        }

        private ValueMatcher ignore() {
            return (o, t1) -> true;
        }
    }
}
