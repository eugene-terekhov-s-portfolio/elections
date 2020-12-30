package org.elections;

import java.util.List;
import java.util.Map;

public class Elections {
    private final Map<String, List<String>> electorsByDistrict;
    private final Voting votingStrategy;

    public Elections(Map<String, List<String>> electorsByDistrict, boolean withDistrict) {
        this.electorsByDistrict = electorsByDistrict;

        if (!withDistrict) {
            votingStrategy = new AllVotes();
        } else {
            votingStrategy = new DistrictVotes(electorsByDistrict);
        }
    }

    public void addCandidate(String candidate) {
        votingStrategy.addCandidate(candidate);
    }

    public void voteFor(String candidate, String electorDistrict) {
        votingStrategy.addVote(candidate, electorDistrict);
    }

    public Map<String, String> results() {
        return votingStrategy.results(electorsByDistrict);
    }

}
