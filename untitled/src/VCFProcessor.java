import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VCFProcessor {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Adjon meg egy könyvtárat");
            return;
        }
        File directory = new File(args[0]);

        if (!directory.isDirectory()) {
            System.out.println(args[0] + "nem egy könyvtár");
            return;
        }

        Map<String, Eredmeny> map = new HashMap<>();

        for (File file : Objects.requireNonNull(directory.listFiles((d, name) -> name.endsWith(".vcf")))) {
            processFile(file, map);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.csv"))) {
            writer.write("TRANSCRIPT;HGVS.C;VARIANT_TYPE;TOTAL_COUNT\n");
            for (Eredmeny e : map.values())
                writer.write(e.toString());
        }
    }

    static void processFile(File file, Map<String, Eredmeny> map) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] cols = line.split("\t");
                if (cols.length < 8) continue;

                String ref = cols[3], alt = cols[4], info = cols[7];
                if (ref.length() != 1 || alt.length() != 1 || alt.contains(",")) continue;

                String ann = null;
                for (String field : info.split(";")) {
                    if (field.startsWith("ANN=")) {
                        ann = field;
                        break;
                    }
                }

                if (ann == null) continue;

                String[] annFields = ann.split("\\|");
                if (annFields.length < 10) continue;

                String transcript = annFields[6], hgvsC = annFields[9], variantType = annFields[1];

                String key = transcript + "|" + hgvsC + "|" + variantType;

                map.compute(key, (k, v) -> v == null
                        ? new Eredmeny(transcript, hgvsC, variantType)
                        : v.inc());
            }
        } catch (IOException e) {
            System.err.println("Error reading " + file.getName() + ": " + e.getMessage());
        }
    }
}