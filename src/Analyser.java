import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

public class Analyser {
    public static final String TOTAL_COUNT = "totalCount";
    public static Map<String, Integer> read(File file) throws IOException {
        TreeMap<String, Integer> table = new TreeMap<String, Integer>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        int characterCounter = 0;
        String line;
        while ((line = br.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                String character = line.substring(i, i+1);
                incrementValue(table, character);
                characterCounter++;
            }
        }
        table.put(TOTAL_COUNT, characterCounter);
        br.close();
        fr.close();
        return table;

    }
    public static Map<String, Double> openDif(File file) throws IOException {
        TreeMap<String, Double> difmap = new TreeMap<>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
           String[] keyPair =  line.split(":");
           if (keyPair.length != 2) throw new IOException("Damaged File");
           difmap.put(keyPair[0], Double.parseDouble(keyPair[1]));
        }
        br.close();
        fr.close();
        return difmap;
    }
    public static Map<String, Integer> open(File file) throws IOException {
        TreeMap<String, Integer> table = new TreeMap<>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            String[] keyPair =  line.split(":");
            if (keyPair.length != 2) throw new IOException("Damaged File");
            table.put(keyPair[0], Integer.parseInt(keyPair[1]));
        }
        br.close();
        fr.close();
        return table;
    }
    public static Map<String, Integer> read(String s) {
        TreeMap<String, Integer> table = new TreeMap<String, Integer>();
        for (int i = 0; i < s.length(); i++) {
            String character = s.substring(i, i+1);
            incrementValue(table, character);
        }
        table.put(TOTAL_COUNT, s.length());
        return table;
    }

    public static void write(File file, Map data) throws IOException {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for (Object key : data.keySet()) {
            bw.write(key.toString() + ":" + data.get(key) + "\n");
        }
        bw.flush();
        bw.close();
        fw.close();
    }
    public static Map<String, Double> difference(Map<String, Integer> table1, Map<String, Integer> table2) {
        TreeMap<String, Double> difmap = new TreeMap<>();
        for (String key1 : table1.keySet()) {
            if (key1.equals(TOTAL_COUNT)) continue;
            if (table2.containsKey(key1)) {
                difmap.put(key1, getRelativeCount(table1, key1) -getRelativeCount(table2, key1));
            } else {
                difmap.put(key1, getRelativeCount(table1, key1));
            }
        }
        for (String key2 : table2.keySet()) {
            if (key2.equals(TOTAL_COUNT)) continue;
            if (!difmap.containsKey(key2)) {
                difmap.put(key2, -getRelativeCount(table2, key2));
            }
        }
        return difmap;
    }
    public static int analyse(Map<String, Double> difmap) {
        double totalDifCount = 0;
        for (String key: difmap.keySet()) {
            totalDifCount += Math.pow(difmap.get(key), 2);
        }
        totalDifCount = Math.sqrt(totalDifCount);
        return (int) (100 - (totalDifCount * 50)); //100 is the best score, meaning the files are equivalent, 0 means they share nothing
    }
    public static int analyse(File file) throws IOException {
        return analyse(openDif(file));
    }

    //Helper Functions
    private static void incrementValue(Map<String, Integer> table,String key) {
        if (table.containsKey(key)) {
            table.put(key, table.get(key) +1);
        } else {
            table.put(key, 1);
        }
    }
    private static double getRelativeCount(Map<String, Integer> table, String key) {
        if (!table.containsKey(key)) return 0;
        return (double) table.get(key) / table.get(TOTAL_COUNT);
    }
    public static void main(String[] args) {
        try {
            Map<String, Integer> table = read(new File("Text/text1.txt"));
            Map<String, Integer> table2 = read(new File("Text/text2.txt"));
            write(new File("Text/text1.dat"), table);
            write(new File("Text/text2.dat"), table2);
            Map<String, Double> difmap = difference(table, table2);
            write(new File("Text/difText1Text2.dat"), difmap);
            System.out.println(analyse(difmap));
        } catch (IOException ex) {

        }

    }
}
