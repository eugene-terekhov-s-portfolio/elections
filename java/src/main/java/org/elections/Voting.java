package org.elections;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface Voting {
    void addCandidate(String candidate);

    void addVote(String candidate, String district);

    Map<String, String> results(Map<String, List<String>> electorsByDistrict);

    default float votingResult(float currentCandidateVotes, int totalVotes) {
        return (currentCandidateVotes * 100) / totalVotes;
    }

    default String frenchFormattedResult(float rawResultValue) {
        return String.format(Locale.FRENCH, "%.2f%%", rawResultValue);
    }
}
