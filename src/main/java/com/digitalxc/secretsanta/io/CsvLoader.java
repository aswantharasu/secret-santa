package com.digitalxc.secretsanta.io;

import com.digitalxc.secretsanta.model.Participant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class CsvLoader {

    private CsvLoader() {
    }

    public static final class InputData {
        public final List<Participant> participants;
        public final Map<Participant, Set<Participant>> exclusions;

        public InputData(List<Participant> participants, Map<Participant, Set<Participant>> exclusions) {
            this.participants = participants;
            this.exclusions = exclusions;
        }
    }

    private static String[] splitCsvLine(String line) {
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

    public static InputData load(Path path) throws IOException {
        List<String> raw = Files.readAllLines(path);
        List<String[]> rows = raw.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.toLowerCase().startsWith("name,"))
                .map(CsvLoader::splitCsvLine)
                .collect(Collectors.toList());

        List<Participant> participants = new ArrayList<>();
        Map<String, Participant> byEmail = new HashMap<>();

        for (String[] cols : rows) {
            String name = cols.length > 0 ? stripQuotes(cols[0]) : "";
            String email = cols.length > 1 ? stripQuotes(cols[1]).toLowerCase() : "";
            Participant p = new Participant(name, email);
            participants.add(p);
            byEmail.put(email, p);
        }

        Map<Participant, Set<Participant>> exclusions = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            String[] cols = rows.get(i);
            if (cols.length < 3) continue;
            String ex = stripQuotes(cols[2]);
            if (ex.isEmpty()) continue;
            String[] tokens = ex.split("\\|", -1);
            Participant giver = participants.get(i);
            Set<Participant> set = new HashSet<>();
            for (String t : tokens) {
                String e = t.trim().toLowerCase();
                Participant p = byEmail.get(e);
                if (p != null) set.add(p);
            }
            if (!set.isEmpty()) exclusions.put(giver, Collections.unmodifiableSet(set));
        }

        return new InputData(Collections.unmodifiableList(participants), Collections.unmodifiableMap(exclusions));
    }

    private static String stripQuotes(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1).replace("\"\"", "\"");
        }
        return t;
    }
}
