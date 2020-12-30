package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllVotes implements Voting {
    private final List<Integer> votesWithoutDistricts = new ArrayList<>();
    private final List<String> candidates = new ArrayList<>();
    private final List<String> officialCandidates = new ArrayList<>();

    @Override
    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);

        this.votesWithoutDistricts.add(0);
    }

    @Override
    public void addVote(String candidate, String district) {
        if (candidates.contains(candidate)) {
            int index = this.candidates.indexOf(candidate);
            this.votesWithoutDistricts.set(index, this.votesWithoutDistricts.get(index) + 1);
        } else {
            this.candidates.add(candidate);
            this.votesWithoutDistricts.add(1);
        }
    }

    @Override
    public Map<String, String> results(Map<String, List<String>> electorsByDistrict) {
        final Map<String, String> results = new HashMap<>();
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        int nbValidVotes = 0;

        Integer nbVotes = this.votesWithoutDistricts.stream().reduce(0, Integer::sum);
        for (String officialCandidate : officialCandidates) {
            int index = candidates.indexOf(officialCandidate);
            nbValidVotes += this.votesWithoutDistricts.get(index);
        }

        for (int i = 0; i < this.votesWithoutDistricts.size(); i++) {
            String candidate = candidates.get(i);
            Integer currentCandidateVotes = this.votesWithoutDistricts.get(i);
            if (officialCandidates.contains(candidate)) {
                results.put(candidate, frenchFormattedResult(votingResult(currentCandidateVotes, nbValidVotes)));
            } else {
                if (candidate.isEmpty()) {
                    blankVotes += currentCandidateVotes;
                } else {
                    nullVotes += currentCandidateVotes;
                }
            }
        }
        results.put("Blank", frenchFormattedResult(votingResult(blankVotes, nbVotes)));
        results.put("Null", frenchFormattedResult(votingResult(nullVotes, nbVotes)));

        int nbElectors = electorsByDistrict.values().stream().map(List::size).reduce(0, Integer::sum);
        results.put("Abstention", frenchFormattedResult(100 - (votingResult(nbVotes, nbElectors))));

        return results;
    }

}
