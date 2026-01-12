package com.digitalxc.secretsanta.io;

import com.digitalxc.secretsanta.model.Participant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class MappingLoader {

    private MappingLoader() {}

    public static Map<Participant, Set<Participant>> loadMapping(Path mappingCsv, List<Participant> participants) throws IOException {
        if (!Files.exists(mappingCsv)) return Collections.emptyMap();

        Map<String, Participant> byEmail = participants.stream()
                .collect(Collectors.toMap(p -> p.getEmail().toLowerCase(), p -> p));

        List<String> lines = Files.readAllLines(mappingCsv);
        Map<Participant, Set<Participant>> result = new HashMap<>();

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            String low = line.toLowerCase();
            if (low.startsWith("employee_name") || low.startsWith("employee_name,")) continue;

            String[] cols = splitCsv(line);
            if (cols.length < 4) continue;
            String giverEmail = cols[1].trim().toLowerCase();
            String childEmail = cols[3].trim().toLowerCase();
            if (giverEmail.isEmpty() || childEmail.isEmpty()) continue;
            if (giverEmail.equals("employee_emailid") || childEmail.equals("employee_emailid")) continue;

            Participant giver = byEmail.get(giverEmail);
            Participant child = byEmail.get(childEmail);
            if (giver == null || child == null) continue;

            result.computeIfAbsent(giver, k -> new HashSet<>()).add(child);
        }

        // make immutable copies
        return result.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.unmodifiableSet(e.getValue())));
    }

    private static String[] splitCsv(String line) {
        List<String> cols = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cols.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        cols.add(cur.toString());
        return cols.stream().map(String::trim).toArray(String[]::new);
    }
}
