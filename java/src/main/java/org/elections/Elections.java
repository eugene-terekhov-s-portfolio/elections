package org.elections;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Elections {
    private final Map<String, List<String>> electorsByDistrict;
    private final Set<Elector> registeredElectors = new HashSet<>();
    private final Set<Elector> votedElectors;
    private final Voting votingStrategy;

    public Elections(List<String> candidates, Map<String, List<String>> electorsByDistrict, boolean withDistrict) {
        this.electorsByDistrict = electorsByDistrict;

        if (!withDistrict) {
            votingStrategy = new AllVoting();
        } else {
            votingStrategy = new DistrictVoting(electorsByDistrict);
        }
        electorsByDistrict.forEach(
                (district, names) -> names.forEach(name -> registeredElectors.add(new Elector(name, district)))
        );
        votedElectors = new HashSet<>(registeredElectors.size());

        for (String candidate : candidates) {
            addCandidate(candidate);
        }
    }

    private void addCandidate(String candidate) {
        votingStrategy.addCandidate(candidate);
    }

    public void voteFor(Elector elector, String candidate) {
        if (!registeredElectors.contains(elector)) {
            throw new VotingException(String.format("%s is not registered as Elector", elector.getName()));
        }
        if (votedElectors.contains(elector)) {
            throw new VotingException(String.format("%s voted already", elector.getName()));
        }
        votingStrategy.addVote(elector, candidate);
        votedElectors.add(elector);
    }

    public Map<String, String> results() {
        return votingStrategy.results(electorsByDistrict);
    }
}
