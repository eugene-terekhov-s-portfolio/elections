package org.elections;

import java.util.*;
import java.util.stream.Collectors;

public class Elections {
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    ArrayList<Integer> votesWithoutDistricts;
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private final Map<String, List<String>> electorsByDistrict;
    private final boolean withDistrict;

    public Elections(Map<String, List<String>> electorsByDistrict, boolean withDistrict) {
        this.electorsByDistrict = electorsByDistrict;
        this.withDistrict = withDistrict;

        if (!this.withDistrict) {
            votesWithoutDistricts = new ArrayList<>();
        } else {
            votesWithDistricts = electorsByDistrict.keySet().stream()
                    .collect(Collectors.toMap(k -> k, v -> new ArrayList<>()));
        }
    }

    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);

        if (!this.withDistrict) {
            votesWithoutDistricts.add(0);
        } else {
            votesWithDistricts.forEach((k,v) -> v.add(0));
        }
    }

    public void voteFor(String candidate, String electorDistrict) {
        if (!withDistrict) {
            if (candidates.contains(candidate)) {
                int index = candidates.indexOf(candidate);
                votesWithoutDistricts.set(index, votesWithoutDistricts.get(index) + 1);
            } else {
                candidates.add(candidate);
                votesWithoutDistricts.add(1);
            }
        } else {
            if (votesWithDistricts.containsKey(electorDistrict)) {
                ArrayList<Integer> districtVotes = votesWithDistricts.get(electorDistrict);
                if (candidates.contains(candidate)) {
                    int index = candidates.indexOf(candidate);
                    districtVotes.set(index, districtVotes.get(index) + 1);
                } else {
                    candidates.add(candidate);
                    votesWithDistricts.forEach((district, votes) -> votes.add(0));
                    districtVotes.set(candidates.size() - 1, districtVotes.get(candidates.size() - 1) + 1);
                }
            }
        }
    }

    public Map<String, String> results() {
        final Map<String, String> results = new HashMap<>();
        Integer nbVotes = 0;
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        int nbValidVotes = 0;

        if (!withDistrict) {
            nbVotes = votesWithoutDistricts.stream().reduce(0, Integer::sum);
            for (String officialCandidate : officialCandidates) {
                int index = candidates.indexOf(officialCandidate);
                nbValidVotes += votesWithoutDistricts.get(index);
            }

            for (int i = 0; i < votesWithoutDistricts.size(); i++) {
                String candidate = candidates.get(i);
                Integer currentCandidateVotes = votesWithoutDistricts.get(i);
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
        } else {
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtVotes = entry.getValue();
                nbVotes += districtVotes.stream().reduce(0, Integer::sum);
            }

            for (String officialCandidate : officialCandidates) {
                int index = candidates.indexOf(officialCandidate);
                for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                    ArrayList<Integer> districtVotes = entry.getValue();
                    nbValidVotes += districtVotes.get(index);
                }
            }

            Map<String, Integer> officialCandidatesResult = new HashMap<>();
            for (int i = 0; i < officialCandidates.size(); i++) {
                officialCandidatesResult.put(candidates.get(i), 0);
            }
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Float> districtResult = new ArrayList<>();
                ArrayList<Integer> districtVotes = entry.getValue();
                for (int i = 0; i < districtVotes.size(); i++) {
                    float candidateResult = 0;
                    if (nbValidVotes != 0)
                        candidateResult = votingResult(districtVotes.get(i), nbValidVotes);
                    String candidate = candidates.get(i);
                    if (officialCandidates.contains(candidate)) {
                        districtResult.add(candidateResult);
                    } else {
                        Integer tempVotes = districtVotes.get(i);
                        if (candidate.isEmpty()) {
                            blankVotes += tempVotes;
                        } else {
                            nullVotes += tempVotes;
                        }
                    }
                }
                int districtWinnerIndex = 0;
                for (int i = 1; i < districtResult.size(); i++) {
                    if (districtResult.get(districtWinnerIndex) < districtResult.get(i))
                        districtWinnerIndex = i;
                }
                officialCandidatesResult.put(candidates.get(districtWinnerIndex), officialCandidatesResult.get(candidates.get(districtWinnerIndex)) + 1);
            }
            for (int i = 0; i < officialCandidatesResult.size(); i++) {
                float ratioCandidate = ((float) officialCandidatesResult.get(candidates.get(i))) / officialCandidatesResult.size() * 100;
                results.put(candidates.get(i), frenchFormattedResult(ratioCandidate));
            }
        }

        results.put("Blank", frenchFormattedResult(votingResult(blankVotes, nbVotes)));
        results.put("Null", frenchFormattedResult(votingResult(nullVotes, nbVotes)));

        int nbElectors = electorsByDistrict.values().stream().map(List::size).reduce(0, Integer::sum);
        results.put("Abstention", frenchFormattedResult(100 - (votingResult(nbVotes, nbElectors))));

        return results;
    }

    private float votingResult(float currentCandidateVotes, int totalVotes) {
        return (currentCandidateVotes * 100) / totalVotes;
    }

    private String frenchFormattedResult(float rawResultValue) {
        return String.format(Locale.FRENCH, "%.2f%%", rawResultValue);
    }
}
