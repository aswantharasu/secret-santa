package com.digitalxc.secretsanta.service;

import com.digitalxc.secretsanta.model.Assignment;
import com.digitalxc.secretsanta.model.Participant;
import com.digitalxc.secretsanta.util.RandomUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class SecretSantaService {

    private SecretSantaService() {
    }

    public static Optional<List<Assignment>> assign(List<Participant> participants,
                                                    Map<Participant, Set<Participant>> exclusions) {
        Objects.requireNonNull(participants, "participants");
        if (participants.size() < 2) return Optional.empty();

        List<Participant> givers = new ArrayList<>(participants);
        RandomUtils.shuffle(givers);

        int attempts = Math.max(50, participants.size() * 10);
        for (int attempt = 0; attempt < attempts; attempt++) {
            List<Participant> receivers = new ArrayList<>(participants);
            RandomUtils.shuffle(receivers);

            List<Assignment> result = new ArrayList<>();
            boolean ok = backtrack(0, givers, receivers, exclusions, new boolean[receivers.size()], result);
            if (ok) return Optional.of(result);
            // small random perturbation between attempts
            Collections.shuffle(givers, ThreadLocalRandom.current());
        }

        return Optional.empty();
    }

    private static boolean backtrack(int index,
                                     List<Participant> givers,
                                     List<Participant> receivers,
                                     Map<Participant, Set<Participant>> exclusions,
                                     boolean[] used,
                                     List<Assignment> out) {
        if (index == givers.size()) return true;

        Participant giver = givers.get(index);

        for (int i = 0; i < receivers.size(); i++) {
            if (used[i]) continue;
            Participant candidate = receivers.get(i);
            if (giver.equals(candidate)) continue;
            if (isExcluded(giver, candidate, exclusions)) continue;

            used[i] = true;
            out.add(new Assignment(giver, candidate));

            if (backtrack(index + 1, givers, receivers, exclusions, used, out)) return true;

            used[i] = false;
            out.remove(out.size() - 1);
        }

        return false;
    }

    private static boolean isExcluded(Participant giver, Participant candidate, Map<Participant, Set<Participant>> exclusions) {
        if (exclusions == null || exclusions.isEmpty()) return false;
        Set<Participant> forbidden = exclusions.get(giver);
        return forbidden != null && forbidden.contains(candidate);
    }
}
