package com.digitalxc.secretsanta.io;

import com.digitalxc.secretsanta.model.Assignment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CsvWriter {

    private CsvWriter() {
    }

    public static Path writeAssignments(List<Assignment> assignments, Path outDir) throws IOException {
        if (!Files.exists(outDir)) Files.createDirectories(outDir);
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path out = outDir.resolve("output_" + ts + ".csv");

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("giverName,giverEmail,receiverName,receiverEmail");
            w.newLine();
            for (Assignment a : assignments) {
                String[] fields = new String[]{
                        a.getGiver().getName(),
                        a.getGiver().getEmail(),
                        a.getReceiver().getName(),
                        a.getReceiver().getEmail()
                };
                w.write(joinCsv(fields));
                w.newLine();
            }
        }

        return out;
    }

    private static String joinCsv(String[] fields) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) b.append(',');
            b.append(escapeField(fields[i]));
        }
        return b.toString();
    }

    private static String escapeField(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
