import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {
    private static int MIN_FILE_LEN = 20;

    public static void main(String[] args) {
        HashSet<String> keyWords = initKeyWords("src/KeyWords.txt");
        List<String> content1 = parseFile("data/1.c", keyWords);
        List<String> content2 = parseFile("data/2.c", keyWords);
        boolean foundPlagiat = areSameListings(content1, content2);
        // System.out.println("Это" + (foundPlagiat ? " очень похоже на плагиат" : " не похоже на плагиат"));
        // System.out.println(calcDist("if ( a % a = = b ) return a ;", "a or ( a = b ; a < a ; a + + )"));
        // System.out.println(calcDist("if ( a % a = = b ) return a ;", "a + = ( b [ a ] * b [ a ] ) ;"));
        // System.out.println(calcDist("if ( a % a = = b ) return a ;", "a or ( a = b ; a < a ; a + + )"));
        // System.out.println(calcDist("if ( a % a = = b ) return a ;", "a int a ( \" % a \" , a [ a ] )"));
        // System.out.println(calcDist("int a ;", "break;"));
        // System.out.println(calcDist("int a ;", "return a ;"));
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
        for (String token : splittedLine) {
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
        List<String> la = splitLineBySpace(a);
        List<String> lb = splitLineBySpace(b);
        if (la.size() > lb.size()) {
            List<String> tmp = new ArrayList<>(la);
            la.clear();
            la.addAll(lb);
            lb.clear();
            lb.addAll(tmp);
        }
        // now a.length() <= b.length()

        int dist[][] = new int[2][1 + la.size()];
        int cur = 1;
        for (int i = 0; i <= lb.size(); ++i) {
            cur ^= 1;
            for (int j = 0; j <= la.size(); ++j) {
                if (i == 0 || j == 0) {
                    dist[cur][j] = i > j ? i : j;
                } else {
                    dist[cur][j] = Math.min(dist[cur ^ 1][j - 1] + (lb.get(i - 1).equals(la.get(j - 1)) ? 0 : 1), 1 + Math.min(dist[cur][j - 1], dist[cur ^ 1][j]));
                }
            }
        }

        return dist[cur][la.size()];
    }

    static boolean areSameStrings(String a, String b) {
        int d = calcDist(a, b);
        System.out.println(a);
        System.out.println(b);
        System.out.println(1.0 * d / Math.max(a.length(), b.length()));
        return 1.0 * d / Math.max(a.length(), b.length()) <= 0.2;
    }

    static boolean areSuspiciousBlocks(List<String> a, List<String> b) {
        int sameStringsCount = 0;
        for (int i = 0; i < a.size(); ++i) {
            if (areSameStrings(a.get(i), b.get(i))) {
                ++sameStringsCount;
            }
        }
        return sameStringsCount >= 0.75 * a.size();
    }

    static double calcPlagiatFactor(List<String> content1, List<String> content2) {
        double plagiatFactor = 0;
        if (content1.size() > MIN_FILE_LEN && content2.size() > MIN_FILE_LEN) {
            int suspiciousAmount = 0;
            int totalAmount = 0;
            for (int i = 0; i < content1.size() - 8; ++i) {
                List<String> block1 = new ArrayList<>();
                for (int it = 0; it < 8; ++it) {
                    block1.add(content1.get(i + it));
                }
                for (int j = 0; j < content2.size() - 8; ++j) {
                    List<String> block2 = new ArrayList<>();
                    for (int it = 0; it < 8; ++it) {
                        block2.add(content2.get(j + it));
                    }
                    ++totalAmount;
                    if (areSuspiciousBlocks(block1, block2)) {
                        ++suspiciousAmount;
                    }
                }
            }

            System.out.println("result: " + suspiciousAmount + " from " + totalAmount);
            plagiatFactor = 1.0 * suspiciousAmount / totalAmount;
        }
        return plagiatFactor;
    }

    static boolean areSameListings(List<String> c1, List<String> c2) {
        return calcPlagiatFactor(c1, c2) >= 0.45;
    }
}
