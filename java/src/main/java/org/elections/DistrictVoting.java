package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DistrictVoting extends BaseVoting implements Voting {
    private final Map<String, List<Integer>> votesWithDistricts;

    public DistrictVoting(Map<String, List<String>> electorsByDistrict) {
        this.votesWithDistricts = electorsByDistrict.keySet().stream()
                .collect(Collectors.toMap(k -> k, v -> new ArrayList<>()));
    }

    @Override
    public void addCandidate(String candidate) {
        addCandidateToLists(candidate);
        this.votesWithDistricts.forEach((k, v) -> v.add(0));
    }

    @Override
    public void addVote(Elector elector, String candidate) {
        String electorDistrict = elector.getDistrict();
        if (this.votesWithDistricts.containsKey(electorDistrict)) {
            List<Integer> districtVotes = this.votesWithDistricts.get(electorDistrict);
            if (candidates.contains(candidate)) {
                int index = candidates.indexOf(candidate);
                districtVotes.set(index, districtVotes.get(index) + 1);
            } else {
                candidates.add(candidate);
                this.votesWithDistricts.forEach((district, votes) -> votes.add(0));
                districtVotes.set(candidates.size() - 1, districtVotes.get(candidates.size() - 1) + 1);
            }
        }
    }

    @Override
    public Map<String, String> results(Map<String, List<String>> electorsByDistrict) {
        for (Map.Entry<String, List<Integer>> entry : this.votesWithDistricts.entrySet()) {
            List<Integer> districtVotes = entry.getValue();
            nbVotes += districtVotes.stream().reduce(0, Integer::sum);
        }

        for (String officialCandidate : this.officialCandidates) {
            int index = this.candidates.indexOf(officialCandidate);
            for (Map.Entry<String, List<Integer>> entry : this.votesWithDistricts.entrySet()) {
                List<Integer> districtVotes = entry.getValue();
                nbValidVotes += districtVotes.get(index);
            }
        }

        Map<String, Integer> officialCandidatesResult = new HashMap<>();
        for (int i = 0; i < this.officialCandidates.size(); i++) {
            officialCandidatesResult.put(this.candidates.get(i), 0);
        }
        for (Map.Entry<String, List<Integer>> entry : this.votesWithDistricts.entrySet()) {
            ArrayList<Float> districtResult = new ArrayList<>();
            List<Integer> districtVotes = entry.getValue();
            for (int i = 0; i < districtVotes.size(); i++) {
                String candidate = this.candidates.get(i);
                if (this.officialCandidates.contains(candidate)) {
                    districtResult.add(votingResult(districtVotes.get(i), nbValidVotes));
                } else {
                    updateBlankOrNullVotesForCandidate(candidate, districtVotes.get(i));
                }
            }
            int districtWinnerIndex = 0;
            for (int i = 1; i < districtResult.size(); i++) {
                if (districtResult.get(districtWinnerIndex) < districtResult.get(i))
                    districtWinnerIndex = i;
            }
            officialCandidatesResult.put(this.candidates.get(districtWinnerIndex),
                    officialCandidatesResult.get(this.candidates.get(districtWinnerIndex)) + 1);
        }
        for (int i = 0; i < officialCandidatesResult.size(); i++) {
            results.put(this.candidates.get(i), getFormattedResult(
                    officialCandidatesResult.get(this.candidates.get(i)), officialCandidatesResult.size()));
        }
        addBlankAndNullAndAbstentVotesToResult(electorsByDistrict);
        return results;
    }

}
