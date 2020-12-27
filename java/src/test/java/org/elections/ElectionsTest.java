package org.elections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class ElectionsTest {

    @Test
    void electionWithoutDistricts() {
        Map<String, List<String>> list = Map.of(
                "District 1", Arrays.asList("Bob", "Anna", "Jess", "July"),
                "District 2", Arrays.asList("Jerry", "Simon"),
                "District 3", Arrays.asList("Johnny", "Matt", "Carole"),
                "District 4", Collections.singletonList("Eugene")
        );
        Elections elections = new Elections(list, false);
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "District 1");
        elections.voteFor("Jerry", "Jerry", "District 2");
        elections.voteFor("Anna", "Johnny", "District 1");
        elections.voteFor("Johnny", "Johnny", "District 3");
        elections.voteFor("Matt", "Donald", "District 3");
        elections.voteFor("Jess", "Joe", "District 1");
        elections.voteFor("Simon", "", "District 2");
        elections.voteFor("Carole", "", "District 3");
        elections.voteFor("Eugene", "", "District 4");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "50,00%",
                "Johnny", "50,00%",
                "Michel", "0,00%",
                "Blank", "33,33%",
                "Null", "22,22%",
                "Abstention", "10,00%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    void electionWithDistricts() {
        Map<String, List<String>> list = Map.of(
                "District 1", Arrays.asList("Bob", "Anna", "Jess", "July"),
                "District 2", Arrays.asList("Jerry", "Simon"),
                "District 3", Arrays.asList("Johnny", "Matt", "Carole"),
                "District 4", Collections.singletonList("Eugene")
        );
        Elections elections = new Elections(list, true);
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "District 1");
        elections.voteFor("Anna", "Johnny", "District 1");
        elections.voteFor("Jess", "Joe", "District 1");
        elections.voteFor("July", "Jerry", "District 1");
        elections.voteFor("Jerry", "Jerry", "District 2");
        elections.voteFor("Simon", "", "District 2");
        elections.voteFor("Johnny", "Johnny", "District 3");
        elections.voteFor("Matt", "Michel", "District 3");
        elections.voteFor("Carole", "", "District 3");
        elections.voteFor("Eugene", "Michel", "District 4");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "66,67%",
                "Johnny", "0,00%",
                "Michel", "33,33%",
                "Blank", "22,22%",
                "Null", "11,11%",
                "Abstention", "10,00%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
