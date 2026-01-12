package com.digitalxc.secretsanta;

import com.digitalxc.secretsanta.io.CsvLoader;
import com.digitalxc.secretsanta.io.CsvWriter;
import com.digitalxc.secretsanta.io.MappingLoader;
import com.digitalxc.secretsanta.model.Assignment;
import com.digitalxc.secretsanta.model.Participant;
import com.digitalxc.secretsanta.service.SecretSantaService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Path input = Paths.get("E:\\repos\\secret-santa\\resources\\input.csv");
        Path outDir = Paths.get("E:\\repos\\secret-santa\\resources");

        try {
            CsvLoader.InputData data = CsvLoader.load(input);
            List<Participant> participants = data.participants;
            Map<Participant, Set<Participant>> exclusions = new HashMap<>(data.exclusions);

            Path mapping = Paths.get("E:\\repos\\secret-santa\\resources\\secret_santa_mapping_2025.csv");
            Map<Participant, Set<Participant>> mappingExclusions = MappingLoader.loadMapping(mapping, participants);
            mappingExclusions.forEach((giver, set) -> exclusions.merge(giver, set, (a, b) -> {
                Set<Participant> merged = new HashSet<>(a);
                merged.addAll(b);
                return Collections.unmodifiableSet(merged);
            }));

            Optional<List<Assignment>> assignments = SecretSantaService.assign(participants, exclusions);

            if (assignments.isPresent()) {
                Path out = CsvWriter.writeAssignments(assignments.get(), outDir);
                LOG.info("Assignments written to: " + out.toAbsolutePath());
            } else {
                LOG.warning("Unable to produce valid assignments with given constraints.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Fatal error: " + e.getMessage(), e);
        }
    }
}
