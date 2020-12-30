package org.elections;

import java.util.List;
import java.util.Map;

public interface Voting {
    void addCandidate(String candidate);

    void addVote(String candidate, String electorDistrict);

    Map<String, String> results(Map<String, List<String>> electorsByDistrict);

}
