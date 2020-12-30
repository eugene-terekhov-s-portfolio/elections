package org.elections;

import java.util.List;
import java.util.Map;

public interface Voting {
    void addCandidate(String candidate);

    void addVote(Elector elector, String candidate);

    Map<String, String> results(Map<String, List<String>> electorsByDistrict);

}
