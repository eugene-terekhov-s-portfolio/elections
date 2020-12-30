package org.elections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class ElectionsTest {

    private static final String DISTRICT_1 = "District 1";
    private static final String DISTRICT_2 = "District 2";
    private static final String DISTRICT_3 = "District 3";
    private static final String DISTRICT_4 = "District 4";
    private static final Map<String, List<String>> ELECTORS_BY_DISTRICT = Map.of(
            DISTRICT_1, Arrays.asList("Bob", "Anna", "Jess", "July"),
            DISTRICT_2, Arrays.asList("Jerry", "Simon"),
            DISTRICT_3, Arrays.asList("Johnny", "Matt", "Carole")
    );

    @Test
    void electionWithoutDistricts() {
        Elections elections = new Elections(ELECTORS_BY_DISTRICT, false);
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Jerry", "District 1");
        elections.voteFor("Jerry", "District 2");
        elections.voteFor("Johnny", "District 1");
        elections.voteFor("Johnny", "District 3");
        elections.voteFor("Donald", "District 3");
        elections.voteFor("Joe", "District 1");
        elections.voteFor("", "District 2");
        elections.voteFor("", "District 3");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "50,00%",
                "Johnny", "50,00%",
                "Michel", "0,00%",
                "Blank", "25,00%",
                "Null", "25,00%",
                "Abstention", "11,11%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    void electionWithDistricts() {
        Elections elections = new Elections(ELECTORS_BY_DISTRICT, true);
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Jerry", "District 1");
        elections.voteFor("Jerry", "District 2");
        elections.voteFor("Johnny", "District 1");
        elections.voteFor("Johnny", "District 3");
        elections.voteFor("Donald", "District 3");
        elections.voteFor("Joe", "District 1");
        elections.voteFor("Jerry", "District 1");
        elections.voteFor("", "District 2");
        elections.voteFor("", "District 3");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "66,67%",
                "Johnny", "33,33%",
                "Michel", "0,00%",
                "Blank", "22,22%",
                "Null", "22,22%",
                "Abstention", "0,00%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
