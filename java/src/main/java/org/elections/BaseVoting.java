package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseVoting {
    protected final List<String> candidates = new ArrayList<>();
    protected final List<String> officialCandidates = new ArrayList<>();
    protected final Map<String, String> results = new HashMap<>();
    protected Integer nullVotes = 0;
    protected Integer blankVotes = 0;
    protected Integer nbValidVotes = 0;
    protected Integer nbVotes = 0;

    protected void addCandidateToLists(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
    }

    protected void addBlankAndNullAndAbstentVotesToResult(Map<String, List<String>> electorsByDistrict) {
        results.put("Blank", frenchFormattedResult(votingResult(blankVotes, nbVotes)));
        results.put("Null", frenchFormattedResult(votingResult(nullVotes, nbVotes)));

        int nbElectors = electorsByDistrict.values().stream().map(List::size).reduce(0, Integer::sum);
        results.put("Abstention", frenchFormattedResult(100 - (votingResult(nbVotes, nbElectors))));
    }

    protected void updateBlankOrNullVotesForCandidate(String candidate, Integer candidateVotes) {
        if (candidate.isEmpty()) {
            blankVotes += candidateVotes;
        } else {
            nullVotes += candidateVotes;
        }
    }

    float votingResult(float currentCandidateVotes, int totalVotes) {
        if (totalVotes == 0) {
            return 0;
        }
        return (currentCandidateVotes * 100) / totalVotes;
    }

    String frenchFormattedResult(float rawResultValue) {
        return String.format(Locale.FRENCH, "%.2f%%", rawResultValue);
    }

    protected String getFormattedResult(Integer currentCandidateVotes, Integer totalVotes) {
        return frenchFormattedResult(votingResult(currentCandidateVotes, totalVotes));
    }
}
