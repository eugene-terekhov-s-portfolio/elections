package org.elections;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ElectionsTest {

    private static final String DISTRICT_1 = "District 1";
    private static final Elector JESS = new Elector("Jess", DISTRICT_1);
    private static final Elector ANNA = new Elector("Anna", DISTRICT_1);
    private static final Elector BOB = new Elector("Bob", DISTRICT_1);
    private static final Elector JULY = new Elector("July", DISTRICT_1);
    private static final String DISTRICT_2 = "District 2";
    private static final Elector JERRY = new Elector("Jerry", DISTRICT_2);
    private static final Elector SIMON = new Elector("Simon", DISTRICT_2);
    private static final String DISTRICT_3 = "District 3";
    private static final Elector JOHNNY = new Elector("Johnny", DISTRICT_3);
    private static final Elector CAROLE = new Elector("Carole", DISTRICT_3);
    private static final Elector MATT = new Elector("Matt", DISTRICT_3);
    private static final Map<String, List<String>> ELECTORS_BY_DISTRICT = Map.of(
            DISTRICT_1, Arrays.asList("Bob", "Anna", "Jess", "July"),
            DISTRICT_2, Arrays.asList("Jerry", "Simon"),
            DISTRICT_3, Arrays.asList("Johnny", "Matt", "Carole")
    );
    private static final List<String> CANDIDATES = List.of("Michel", "Jerry", "Johnny");

    @Test
    void electionWithoutDistricts() {
        Elections elections = new Elections(CANDIDATES, ELECTORS_BY_DISTRICT, false);

        elections.voteFor(BOB, "Jerry");
        elections.voteFor(ANNA, "Johnny");
        elections.voteFor(JESS, "Joe");
        elections.voteFor(SIMON, "Jerry");
        elections.voteFor(JERRY, "");
        elections.voteFor(MATT, "Johnny");
        elections.voteFor(CAROLE, "Donald");
        elections.voteFor(JOHNNY, "");

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
        Elections elections = new Elections(CANDIDATES, ELECTORS_BY_DISTRICT, true);

        elections.voteFor(BOB,"Jerry");
        elections.voteFor(ANNA, "Johnny");
        elections.voteFor(JESS, "Joe");
        elections.voteFor(JULY, "Jerry");
        elections.voteFor(SIMON, "Jerry");
        elections.voteFor(JERRY, "");
        elections.voteFor(CAROLE,"Johnny");
        elections.voteFor(MATT, "Donald");
        elections.voteFor(JOHNNY, "");

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
