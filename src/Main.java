import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        HashSet<String> keyWords = new HashSet<>();
        Scanner sc, sc1;
        String line2 = "/Users/CO8ECTb/Desktop/Labs/SPL/3lab/3lab/main.c";
        //Scanner in = new Scanner(System.in);
        //String line1 = in.nextLine();
        //char[] charArray = line1.toCharArray();

//        if(charArray[charArray.length-2] != '.' && charArray[charArray.length-1] != 'c'){
//            System.out.println("not .c file");
//            System.exit(1);
//        }
        File file1 = new File("src/KeyWords.txt");
        File file = new File(line2);

        try {
            sc1 = new Scanner(file1);
            while (sc1.hasNextLine()) {
                String line = sc1.nextLine().trim();
                keyWords.add(line);
            }
            sc1.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(line.contains("{") || line.contains("}")){
                    line = line.replace("{","");
                    line = line.replace("}","");
                }
                if (!line.isEmpty() && !line.contains("#include")) {
                    List<String> list = splitLineBySpace(splitByKey(line, keyWords));
                    StringBuilder newLine = new StringBuilder();
                    for (int i=0; i< list.size();i++){
                        if(!keyWords.contains(list.get(i))){
                            if(!list.get(i).matches("[a-zA-Z]+")){
                                list.remove(i);
                                list.add(i,"b");
                            } else{
                                list.remove(i);
                                list.add(i,"a");
                            }
                        }
                        if(list.size() != (i+1)) newLine.append(list.get(i)).append(" ");
                        else newLine.append(list.get(i));
                        //System.out.println(i+" "+list.get(i));
                    }
                    System.out.println(newLine);
                    for (int i=0; i< list.size();i++){
                        int y = calcDist(list.get(i),"")
                    }
                    System.out.println("--------------------");

                }
            }

            sc.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static List<String> splitLineBySpace(String line){
                    String[] splittedLine = line.split("\\s+");
                    List<String> list = new LinkedList<>();
                    for(int i = 0; i < splittedLine.length; i++){
                        if(!splittedLine[i].isEmpty()) {
                            list.add(splittedLine[i]);
                        }
                    }
                    return list;
                }

        static String splitByKey(String line, HashSet<String> keyWords){
            System.out.println(line);

            for (String key : keyWords) {
                if (line.contains(key)) {
                    //System.out.println("key = " +key);
                    line = line.replace(key," "+key+" ");

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
            for (int j = 0; j < a.length(); ++j) {
                if (i == 0 || j == 0) {
                    dist[cur][j] = i > j ? i : j;
                } else {
                    dist[cur][j] = Math.min(dist[cur ^ 1][j - 1] + (b.charAt(i - 1) == a.charAt(j - 1) ? 1 : 0), 1 + Math.min(dist[cur][j - 1], dist[cur ^ 1][j]));
                }
            }
        }

        return dist[cur][a.length()];
    }
}
