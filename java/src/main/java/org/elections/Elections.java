package org.elections;

import java.util.*;

public class Elections {
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    ArrayList<Integer> votesWithoutDistricts = new ArrayList<>();
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private final Map<String, List<String>> list;
    private final boolean withDistrict;

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        this.list = list;
        this.withDistrict = withDistrict;

        votesWithDistricts = new HashMap<>();
        votesWithDistricts.put("District 1", new ArrayList<>());
        votesWithDistricts.put("District 2", new ArrayList<>());
        votesWithDistricts.put("District 3", new ArrayList<>());
    }

    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
        votesWithoutDistricts.add(0);
        votesWithDistricts.get("District 1").add(0);
        votesWithDistricts.get("District 2").add(0);
        votesWithDistricts.get("District 3").add(0);
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
        return new ResultsCalculator().invoke();
    }

    private class ResultsCalculator {
        private final Map<String, String> results;
        private Integer nbVotes;
        private Integer nullVotes;
        private Integer blankVotes;
        private int nbValidVotes;

        public ResultsCalculator() {
            this.results = new HashMap<>();
            this.nbVotes = 0;
            this.nullVotes = 0;
            this.blankVotes = 0;
            this.nbValidVotes = 0;
        }

        public Map<String, String> invoke() {
            if (!withDistrict) {
                nbVotes = votesWithoutDistricts.stream().reduce(0, Integer::sum);
                for (String officialCandidate : officialCandidates) {
                    int index = candidates.indexOf(officialCandidate);
                    nbValidVotes += votesWithoutDistricts.get(index);
                }

                for (int i = 0; i < votesWithoutDistricts.size(); i++) {
                    String candidate = candidates.get(i);
                    if (officialCandidates.contains(candidate)) {
                        results.put(candidate, frenchFormattedResult(((float) votesWithoutDistricts.get(i) * 100) / nbValidVotes));
                    } else {
                        countBlankOrNullVotes(candidate, votesWithoutDistricts.get(i));
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
                            candidateResult = ((float) districtVotes.get(i) * 100) / nbValidVotes;
                        String candidate = candidates.get(i);
                        if (officialCandidates.contains(candidate)) {
                            districtResult.add(candidateResult);
                        } else {
                            countBlankOrNullVotes(candidate, districtVotes.get(i));
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

            results.put("Blank", frenchFormattedResult(((float) blankVotes * 100) / nbVotes));

            results.put("Null", frenchFormattedResult(((float) nullVotes * 100) / nbVotes));

            int nbElectors = list.values().stream().map(List::size).reduce(0, Integer::sum);
            results.put("Abstention", frenchFormattedResult(100 - ((float) nbVotes * 100 / nbElectors)));

            return results;
        }

        private String frenchFormattedResult(float rawResultValue) {
            return String.format(Locale.FRENCH, "%.2f%%", rawResultValue);
        }

        private void countBlankOrNullVotes(String candidate, Integer tempVotes) {
            if (candidate.isEmpty()) {
                this.blankVotes += tempVotes;
            } else {
                this.nullVotes += tempVotes;
            }
        }
    }
}
