import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.*;

public class QuestionCsvParser {
    private static final Pattern FRAGE_PATTERN = Pattern.compile("^Frage (\\d+)(Richtig|Falsch)$");
    private static final String DEINE_ANTWORT_RICHTIG = "Deine Antwort ist richtig.";
    private static final String DEINE_ANTWORT_FALSCH = "Deine Antwort ist falsch.";
    private static final String RICHTIGE_ANTWORT = "Richtige Antwort";
    private static final String GESAMTERKLAERUNG = "Gesamterklärung";
    private static final String THEMENBEREICH = "Themenbereich";

    public static void main(String[] args) throws IOException {
        String input = /* Hier Textinput als String rein */
        "Frage 17Richtig\n"
        + "What should be included in every service level agreement?\n"
        + "Partners contractual obligations\n"
        + "Deine Antwort ist richtig.\n"
        + "Clearly defined service outcomes\n"
        + "Suppliers contractual obligations\n"
        + "Detailed metrics to capture availability of the system\n"
        + "Gesamterklärung\n"
        + "They should relate to defined outcomes and not simply operational metrics. This can be achieved with balanced ‘bundles’ of metrics.\n"
        + "Themenbereich\n"
        + "7.1.g\n"
        + "Frage 18Falsch\n"
        + "How does 'service level management' contribute to the 'obtain/build' value chain activity?\n"
        + "Richtige Antwort\n"
        + "Provides objectives for component and service performance for products and services\n"
        + "Deine Antwort ist falsch.\n"
        + "Collects feedback during interactions and communicates service performance objectives to the operations and support teams\n"
        + "Provides feedback from interactions with customers into new or changed services\n"
        + "Provides information about the actual service performance and trends\n"
        + "Gesamterklärung\n"
        + "The 'obtain/build' activity in the service level management practice provides objectives for component and service performance for products and services\n"
        + "Themenbereich\n"
        + "7.1.g\n";
        
        if(args.length>0) {
        	input = new String(Files.readAllBytes(Paths.get(args[0])));
        }

        BufferedReader reader = new BufferedReader(new StringReader(input));
        String line;
        List<String[]> rows = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            Matcher m = FRAGE_PATTERN.matcher(line);
            if (!m.matches()) continue;

            // Neue Frage beginnen
            String nummer = m.group(1) + ".1";
            String question = reader.readLine();
            List<String> options = new ArrayList<>();
            int correctIndex = -1;
            int detected = 0;
            for (int i = 0; i < 4; ) {
                String optionLine = reader.readLine();
                if (optionLine == null) break;
                if (optionLine.equals(RICHTIGE_ANTWORT)) {
                    // Richtige Antwort folgt
                    String answer = reader.readLine();
                    options.add(answer);
                    correctIndex = i + 1; // 1-basiert
                    i++;
                } else if (optionLine.equals(DEINE_ANTWORT_RICHTIG) || optionLine.equals(DEINE_ANTWORT_FALSCH)) {
                    // Diese Zeile gehört zu vorheriger Option
                    if (optionLine.equals(DEINE_ANTWORT_RICHTIG)) {
                        correctIndex = i;
                    }
                } else if (optionLine != null && !optionLine.trim().isEmpty()) {
                    options.add(optionLine);
                    i++;
                }
            }
            // Fehlende Optionen auffüllen
            while (options.size() < 4) {
                options.add("");
            }

            // Bis Gesamterklärung finden
            while ((line = reader.readLine()) != null && !line.equals(GESAMTERKLAERUNG));
            StringBuilder erklaerung = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.equals(THEMENBEREICH)) {
                if (erklaerung.length() > 0) erklaerung.append(" ");
                erklaerung.append(line.trim());
            }
            String topic = reader.readLine();

            rows.add(new String[] {
                nummer,
                escapeCsv(question),
                escapeCsv(options.get(0)),
                escapeCsv(options.get(1)),
                escapeCsv(options.get(2)),
                escapeCsv(options.get(3)),
                Integer.toString(correctIndex > 0 ? correctIndex : 1),
                escapeCsv(erklaerung.toString()),
                escapeCsv(topic),
                "0",
                "0"
            });
        }

        // Ausgabe CSV-Header
        String header = "\"id\",\"question\",\"option1\",\"option2\",\"option3\",\"option4\",\"correctIndex\",\"explanation\",\"topic\",\"wrongCount\",\"rightCount\"";
        System.out.println(header);
        List<String> lines2Write = new ArrayList<>();
        lines2Write.add(header);
        
        for (String[] row : rows) {
            System.out.println("\"" + String.join("\",\"", row) + "\"");
            lines2Write.add("\"" + String.join("\",\"", row) + "\"");
        }
        if(args.length>1) {
        	Files.write(Paths.get(args[1]), lines2Write, StandardOpenOption.CREATE_NEW);
        }
    }

    private static String escapeCsv(String value) {
        return value.replace("\"", "\"\"");
    }
}
