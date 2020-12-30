package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Elections {
    private final Map<String, List<String>> electorsByDistrict;
    private final List<Elector> electors = new ArrayList<>();
    private final Voting votingStrategy;

    public Elections(Map<String, List<String>> electorsByDistrict, boolean withDistrict) {
        this.electorsByDistrict = electorsByDistrict;

        if (!withDistrict) {
            votingStrategy = new AllVoting();
        } else {
            votingStrategy = new DistrictVoting(electorsByDistrict);
        }
        electorsByDistrict.forEach(
                (district, names) -> names.forEach(name -> electors.add(new Elector(name, district)))
        );
    }

    public void addCandidate(String candidate) {
        votingStrategy.addCandidate(candidate);
    }

    public void voteFor(Elector elector, String candidate) {
       votingStrategy.addVote(elector, candidate);
    }

    public Map<String, String> results() {
        return votingStrategy.results(electorsByDistrict);
    }

}
