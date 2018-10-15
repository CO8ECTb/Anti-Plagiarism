import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {
    private static int LIMIT = 3;

    public static void main(String[] args) {
        HashSet<String> keyWords = initKeyWords("src/KeyWords.txt");
        List<String> content1 = parseFile("data/1.c", keyWords);
        List<String> content2 = parseFile("data/2.c", keyWords);
    }

    private static HashSet<String> initKeyWords(String filename) {
        HashSet<String> result = new HashSet<>();
        Scanner sc;
        File file = new File(filename);
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                result.add(line);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<String> parseFile(String filename, HashSet<String> keyWords) {
        List<String> result = new ArrayList<>();
        Scanner sc;
        File file = new File(filename);
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.contains("{") || line.contains("}")) {
                    line = line.replace("{", "");
                    line = line.replace("}", "");
                }
                if (!line.isEmpty() && !line.contains("#include")) {
                    List<String> list = splitLineBySpace(splitByKey(line, keyWords));
                    StringBuilder newLine = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        if (!keyWords.contains(list.get(i))) {
                            if (!list.get(i).matches("[a-zA-Z]+")) {
                                list.remove(i);
                                list.add(i, "b");
                            } else {
                                list.remove(i);
                                list.add(i, "a");
                            }
                        }
                        if (list.size() != (i + 1)) newLine.append(list.get(i)).append(" ");
                        else newLine.append(list.get(i));
                        //System.out.println(i+" "+list.get(i));
                    }
                    //for (int i=0; i< list.size();i++){

                    System.out.println(newLine.toString());
                    int y = calcDist(newLine.toString(), "");
                    System.out.println("dist = " + y);

                    //}
                    System.out.println("--------------------");

                    result.add(newLine.toString());
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    static List<String> splitLineBySpace(String line) {
        String[] splittedLine = line.split("\\s+");
        List<String> list = new LinkedList<>();
        for (String token : list) {
            if (!token.isEmpty()) {
                list.add(token);
            }
        }
        return list;
    }


    static String splitByKey(String line, HashSet<String> keyWords) {
        System.out.println(line);

        for (String key : keyWords) {
            if (line.contains(key)) {
                //System.out.println("key = " +key);
                line = line.replace(key, " " + key + " ");

            }
        }
        return line;
    }


    static int calcDist(String a, String b) {
        if (a.length() > b.length()) {
            a = a + b;
            b = a.substring(0, (a.length() - b.length()));
            a = a.substring(b.length());
        }
        // now a.length() <= b.length()

        int dist[][] = new int[2][1 + a.length()];
        int cur = 1;
        for (int i = 0; i <= b.length(); ++i) {
            cur ^= 1;
            for (int j = 0; j <= a.length(); ++j) {
                if (i == 0 || j == 0) {
                    dist[cur][j] = i > j ? i : j;
                } else {
                    dist[cur][j] = Math.min(dist[cur ^ 1][j - 1] + (b.charAt(i - 1) == a.charAt(j - 1) ? 1 : 0), 1 + Math.min(dist[cur][j - 1], dist[cur ^ 1][j]));
                }
            }
        }

        return dist[cur][a.length()];
    }

    static boolean areSameStrings(String a, String b) {
        return calcDist(a, b) <= LIMIT;
    }

}
