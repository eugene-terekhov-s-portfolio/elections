package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllVoting extends BaseVoting implements Voting {
    private final List<Integer> votesWithoutDistricts = new ArrayList<>();

    @Override
    public void addCandidate(String candidate) {
        addCandidateToLists(candidate);
        this.votesWithoutDistricts.add(0);
    }

    @Override
    public void addVote(String candidate, String electorDistrict) {
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
        nbVotes = this.votesWithoutDistricts.stream().reduce(0, Integer::sum);
        for (String officialCandidate : officialCandidates) {
            int index = candidates.indexOf(officialCandidate);
            nbValidVotes += this.votesWithoutDistricts.get(index);
        }

        for (int i = 0; i < this.votesWithoutDistricts.size(); i++) {
            String candidate = candidates.get(i);
            Integer currentCandidateVotes = this.votesWithoutDistricts.get(i);
            if (officialCandidates.contains(candidate)) {
                results.put(candidate, getFormattedResult(currentCandidateVotes, nbValidVotes));
            } else {
                updateBlankOrNullVotesForCandidate(candidate, currentCandidateVotes);
            }
        }
        addBlankAndNullAndAbstentVotesToResult(electorsByDistrict);
        return results;
    }
}
