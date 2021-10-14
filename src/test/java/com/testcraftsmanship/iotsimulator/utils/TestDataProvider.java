package com.testcraftsmanship.iotsimulator.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public interface TestDataProvider {
    static Stream<Arguments> jsonAsNull() {
        return Stream.of(
                Arguments.of("{}", null),
                Arguments.of(null, "{}"),
                Arguments.of(null, null),
                Arguments.of("", "{}"),
                Arguments.of("{}", "")
        );
    }

    static Stream<Arguments> jsonAndParamsNull() {
        return Stream.of(
                Arguments.of("{}", null),
                Arguments.of(null, new HashMap<>()),
                Arguments.of(null, null)
        );
    }

    static Stream<Arguments> illegalJsonPair() {
        return Stream.of(
                Arguments.of("{}", "{"),
                Arguments.of("}", "{}"),
                Arguments.of("{'id':1}", "{'id':'{as}}"),
                Arguments.of("{}", "{}'id':1}"),
                Arguments.of("id", "{}"),
                Arguments.of("id:1", "{}"),
                Arguments.of("'id':a", "{}"),
                Arguments.of("'id':[10, 12", "{}")

        );
    }

    static Stream<Arguments> illegalJsoStructure() {
        return Stream.of(
                Arguments.of("{"),
                Arguments.of("}"),
                Arguments.of("{}'id':1}"),
                Arguments.of("id"),
                Arguments.of("id:1"),
                Arguments.of("{'id':[10, 12, 'a': []}"),
                Arguments.of("{'id':[10}]")
        );
    }

    static Stream<Arguments> illegalUpdaterJson() {
        return Stream.of(
                Arguments.of("{"),
                Arguments.of("{}'id':1}"),
                Arguments.of("{'id':[10, 12}")
        );
    }

    static Stream<Arguments> jsonValueMatchingRegexMask() {
        return Stream.of(
                Arguments.of("{'id':'Dev10'}", "{'id':'{[A-Za-z0-9]+}'}"),
                Arguments.of("{'id':10}", "{'id':'{[0-9]+}'}"),
                Arguments.of("{'id':10}", "{'id':10}"),
                Arguments.of("{id:10}", "{'id':10}"),
                Arguments.of("{'id':10.99}", "{'id':'{[0-9\\\\.]+}'}"),
                Arguments.of("{'temp':'10.99 Celcius'}", "{'temp':'{[A-Za-z0-9\\\\. ]+}'}"),
                Arguments.of("{'temp':'10.99 Celcius'}", "{'temp':'{[\\\\w\\\\. ]+}'}"),
                Arguments.of("{'temp':10}", "{'temp':'{[0-9]{2}}'}"),
                Arguments.of("{'temp':'AZ'}", "{'temp':'{[A-Z]{2}}'}"),
                Arguments.of("{'temp':'A'}", "{'temp':'{[A-Z]{0,2}}'}"),
                Arguments.of("{'temp':''}", "{'temp':'{[A-Z]{0,2}}'}"),
                Arguments.of("{'temp':'az$#10'}", "{'temp':'{.*}'}"),
                Arguments.of("{'val':'az$\"\\'1#'}", "{'val':'az$\"\\'1#'}"),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Jan',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{[0-9]+}',\n"
                                + "'name':'{[A-Za-z]+}',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{[A-Z]{2}}',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}")
        );
    }

    static Stream<Arguments> jsonValueDoesNotMatchRegexpMask() {
        return Stream.of(
                Arguments.of("{'id':'Dev10'}", "{'id':'{[a-z0-9]+}'}"),
                Arguments.of("{'id':\"Az\"}", "{'id':'{[0-9]+}'}"),
                Arguments.of("{'id':10.99}", "{'id':'{[0-9]+}'}"),
                Arguments.of("{'temp':'10.99 Celcius'}", "{'temp':'{[A-Za-z0-9\\\\.]+}'}"),
                Arguments.of("{'temp':100}", "{'temp':'{[0-9]{2}}'}"),
                Arguments.of("{'temp':'ABC'}", "{'temp':'{[A-Z]{0,2}}'}"),
                Arguments.of("{'temp':''}", "{'temp':'{[A-Z]{1,2}}'}"),
                Arguments.of("{'temp':''}", "{'temp':'{}'}")
        );
    }

    static Stream<Arguments> jsonMatchByValue() {
        return Stream.of(
                Arguments.of("{'id':'Dev10'}", "{'id':'Dev10'}"),
                Arguments.of("{'id': -1}", "{'id': -1}"),
                Arguments.of("{'id':10.99}", "{'id':10.99}"),
                Arguments.of("{'temp':'10.99 Celcius'}", "{'temp':'10.99 Celcius'}")
        );
    }

    static Stream<Arguments> jsonMatchByValueNoStrict() {
        return Stream.of(
                Arguments.of("{'id':'Dev10', 'ver': 10.20}", "{'ver':10.20}"),
                Arguments.of("{'dev': {'id': -1, 'ver': 'hw10.20'}}", "{'dev': {'id': -1}}"),
                Arguments.of("{'id':10.99, 'name': 'John'}", "{'name': 'John'}")
        );
    }

    static Stream<Arguments> jsonNotMatchByValueNoStrict() {
        return Stream.of(
                Arguments.of("{'id':'Dev10', 'ver': 10.20}", "{'ver':10.30}"),
                Arguments.of("{'dev': {'id': -1, 'ver': 'hw10.20'}}", "{'dev': {'id': 1}}"),
                Arguments.of("{'id':10.99, 'name': 'John'}", "{'name': 'Cash'}")
        );
    }

    static Stream<Arguments> jsonNotMatchByValue() {
        return Stream.of(
                Arguments.of("{'id':'Dev10 '}", "{'id':'Dev10'}"),
                Arguments.of("{'id': 1}", "{'id': -1}"),
                Arguments.of("{'id':11}", "{'id':10.9999999999}"),
                Arguments.of("{'temp':'10.99 Celcius'}", "{'temp':'10.99    Celcius'}"),
                Arguments.of("{'temp':[1, 0, -1]}", "{'temp':[1, 0]}"),
                Arguments.of("{'temp':[1, 0, -1]}", "{'temp':[-1, 0, 1]}"),
                Arguments.of("{'month':['April', 'June']}", "{'month':['June','April']}")
        );
    }

    static Stream<Arguments> maskJsonThrowingMappingException() {
        return Stream.of(
                Arguments.of("{'id': 10}", "{'name': '{name-par}'}", false),
                Arguments.of("{'id': 10}", "{'name': '{}'}", false),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'name': '{name-par}'}", true),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'name': '{}'}", true)
        );
    }

    static Stream<Arguments> masksJsonWithNoParamsValues() {
        return Stream.of(
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': 10, 'name': 'Jon'}"),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': 10, 'name': \"Jon\"}"),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{'id': [10, 14], 'name': 'Jon'}"),
                Arguments.of("{}", "{}"),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}")
        );
    }

    static Stream<Arguments> strictMasksJsonStructureWithParams() {
        return Stream.of(
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': '{param1}', 'name': '{param2}'}", true),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': 10, 'name': '{param}'}", true),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': -10, 'name': '{param}'}", false),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{'id': ['{param1}', '{param2}'], 'name': 'Jon'}", true),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{'id': ['{param1}', '{param2}']}", false),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{param1}',\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{param2}',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}", true),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{param1}',\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{param2}'"
                                + "   }\n"
                                + "}", false),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{param1}',\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{param2}',\n"
                                + "   'coordinates': [24.10, 0.23]\n"
                                + "   }\n"
                                + "}", false)
        );
    }

    static Stream<Arguments> noStrictMasksJsonStructureWithParams() {
        return Stream.of(
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': '{param1}', 'name': '{param2}'}", true),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': 10, 'name': '{param}'}", true),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': -10, 'name': '{param}'}", false),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{'id': ['{param1}', '{param2}'], 'name': 'Jon'}", true),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{'id': ['{param1}', '{param2}']}", true),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{param1}',\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{param2}',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}", true),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{param1}',\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{param2}'"
                                + "   }\n"
                                + "}", true),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{param1}',\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{param2}',\n"
                                + "   'coordinates': [24.10, 0.23]\n"
                                + "   }\n"
                                + "}", false)
        );
    }

    static Stream<Arguments> positiveExtractedParamsFromStrictMasks() {
        return Stream.of(
                Arguments.of("{'id': 10, 'names': ['Jakub', 'Artur', 'Lisa']}",
                        "{'id': '{idVal}', 'names': ['{name1}', '{name2}', '{name3}']}",
                        Map.of("idVal", "10", "name1", "\"Jakub\"", "name2", "\"Artur\"", "name3", "\"Lisa\"")),
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': '{idVal}', 'name': '{nameVal}'}",
                        Map.of("idVal", "10", "nameVal", "\"Jon\"")),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{'id': '{idVal}', 'name': '{nameVal}'}",
                        Map.of("idVal", "[10,14]", "nameVal", "\"Jon\"")),
                Arguments.of("{\"id\": [0,-1], \"name\": \"Jon\"}", "{'id': '{idVal}', 'name': '{nameVal}'}",
                        Map.of("idVal", "[0,-1]", "nameVal", "\"Jon\"")),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{id-val}',\n"
                                + "'name':'{name-val}',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': '{country-val}',\n"
                                + "   'coordinates': '{coordinates-val}'\n"
                                + "   }\n"
                                + "}",
                        Map.of("id-val", "10", "name-val", "\"Eachainn Jewelle\"",
                                "country-val", "\"PL\"", "coordinates-val", "[24.1,130.23]"))
        );
    }

   static Stream<Arguments> positiveExtractedParamsFromNotStrictMasks() {
        return Stream.of(
                Arguments.of("{'id': 10, 'name': 'Jon'}", "{'id': '{idVal}'}",
                        Map.of("idVal", "10")),
                Arguments.of("{\"id\": [0,-1], \"name\": \"Jon\"}", "{'id': '{idVal}', 'name': '{nameVal}'}",
                        Map.of("idVal", "[0,-1]", "nameVal", "\"Jon\"")),
                Arguments.of("{'id': [10, 14], 'name': 'Jon'}", "{}", Map.of()),
                Arguments.of("{\n"
                                + "'id':10,\n"
                                + "'name':'Eachainn Jewelle',\n"
                                + "'address': {\n"
                                + "   'street':'Main Street 3',\n"
                                + "   'country': 'PL',\n"
                                + "   'coordinates': [24.10, 130.23]\n"
                                + "   }\n"
                                + "}",
                        "{\n"
                                + "'id':'{id-val}',\n"
                                + "'address': {\n"
                                + "   'country': '{country-val}'\n"
                                + "   }\n"
                                + "}",
                        Map.of("id-val", "10", "country-val", "\"PL\""))
        );
    }

    static Stream<Arguments> positiveParamsWithUpdatedJson() {
        return Stream.of(
                Arguments.of(Map.of("name", "\"Jon\""), "{'id': 10, 'name': '{name}'}",
                        "{'id': 10, 'name': 'Jon'}"),
                Arguments.of(Map.of("id", "10"), "{'id': '{id}', 'name': 'Jon'}",
                        "{'id': 10, 'name': 'Jon'}"),
                Arguments.of(Map.of("id", "10"), "{'id':'{id}','name':'Jon','series':'{id}','data':{'s_id':'{id}'}}",
                        "{'id':10,'name':'Jon','series':10,'data':{'s_id':10}}"),
                Arguments.of(Map.of("id", "[10, 20, 50]"), "{'id': '{id}', 'name': 'Jon'}",
                        "{'id': [10,20,50], 'name': 'Jon'}")
        );
    }

    static Stream<Arguments> jsonAndParamThrowingMappingException() {
        return Stream.of(
                Arguments.of("{'id': 10, 'name': '{firstname}'}", Map.of("name", "\"Jon\""))
        );
    }
}
