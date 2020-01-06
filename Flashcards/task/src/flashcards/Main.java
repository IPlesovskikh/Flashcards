package flashcards;
import java.util.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static int checkCard(String card, Map<String, String> cards) {
        for (String check : cards.keySet()){
            if (card.equals(check) == true) {
                return 1;
            }
        }
        return 0;
    }

    private static int checkDefinition(String definition, Map<String, String> cards) {
        for (String check : cards.values()){
            if (definition.equals(check) == true) {
                return 1;
            }
        }
        return 0;
    }

    private static void addCards(Map<String, String> cards, Scanner sc) {
        String tempCard = null;
        String tempDefinition = null;
        System.out.println("The card:");
        tempCard = sc.nextLine();
        if(checkCard(tempCard, cards) == 1) {
            System.out.println("The card \"" + tempCard + "\" already exists.");
            return ;
        }
        System.out.println("The definition of the card:");
        tempDefinition = sc.nextLine();
        if(checkDefinition(tempDefinition, cards) == 1) {
            System.out.println("The definition \"" + tempDefinition + "\" already exists.");
            return ;
        }
        cards.put(tempCard, tempDefinition);
        System.out.println("The pair (" + tempCard + ":" + tempDefinition + ") has been added.");
    }

    private static int checkAnswer(String answer, String response, Map<String, String> cards) {
        for(var entry : cards.entrySet()) {
            if (entry.getValue().equals(response)) {
                System.out.println("Wrong answer. The correct one is \"" + answer + "\", you've just written the definition of \"" + entry.getKey() + "\".");;
                return 1;
            }
        }
        return 0;
    }

    static private void removeCard(Map<String, String> cards, Scanner sc) {
        System.out.println("Card:");
        String key = sc.nextLine();
        if(cards.containsKey(key)) {
            cards.remove(key);
            System.out.println("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + key + "\": there is no such card.");
        }
    }

    private static void askCards(Map<String, String> cards, Scanner sc) {
        System.out.println("How many times to ask?");
        int count = 0;
        try {
            count = sc.nextInt();
        } catch(Exception e) {
            System.out.println("You must print number" + e.getMessage());
        }
        sc.nextLine();
        String answer = null;
        String response = null;
        String[] keys = cards.keySet().toArray(new String[cards.size()]);
        Random random = new Random();
        int card = 0;
        for (int i = 0; i < count; i++) {
            card = random.nextInt(keys.length);
            System.out.println("Print the definition of \"" + keys[card] + "\":");
            response = sc.nextLine();
            answer = cards.get(keys[card]);
            if (answer.equals(response)) {
                System.out.println("Correct answer.");
            } else {
                if (checkAnswer(answer, response, cards) != 1) {
                    System.out.println("Wrong answer. The correct one is \"" + answer + "\".");
                }
            }
        }
    }

    private static void exportCards(Map<String, String> cards,  Scanner sc) throws IOException {
        System.out.println("File name:");
        String path = sc.nextLine();
        File file = new File(path);
        FileWriter writer = new FileWriter(file);
        for (var entry : cards.entrySet()) {
            writer.write(entry.getKey() + "\n");
            //writer.write("\n");
            writer.write(entry.getValue() + "\n");
            //writer.write("\n");
        }
        writer.close();
        System.out.println(cards.size() + " cards have been saved.");
    }

    private static void importCards(Map<String, String> cards,  Scanner sc) throws IOException {
        System.out.println("File name:");
        String path = sc.nextLine();
        List<String> list = Files.readAllLines(Paths.get(path));
        String tempKey = null;
        String tempValue = null;
        int counter = 0;
        for(int i = 0; i < list.size(); i = i + 2) {
            tempKey = list.get(i);
            tempValue = list.get(i + 1);
            if(cards.putIfAbsent(tempKey, tempValue) != null) {
                cards.remove(tempKey);
                cards.put(tempKey, tempValue);
            }
            counter++;
        }
        System.out.println(counter + " cards have been loaded.");
        /*
        while(scanner.hasNext()){
            tempKey = scanner.nextLine();
            tempValue = scanner.nextLine();
            if(cards.putIfAbsent(tempKey, tempValue) != null) {
                cards.remove(tempKey);
                cards.put(tempKey, tempValue);
            }
            counter++;
        }


            } catch(IOException e) {
                System.out.println("not found");
            }
            */


    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, String> cards = new LinkedHashMap<>();
        String com = null;
        while (true) {
            System.out.println("Input the action (add, remove, import, export, ask, exit):");
            com = sc.nextLine();
            if ("add".equals(com)) {
                addCards(cards, sc);
            } else if("remove".equals(com)) {
                removeCard(cards, sc);
            } else if("import".equals(com)) {
                try {
                    importCards(cards, sc);
                } catch(IOException e) {
                    System.out.println("not found");
                }
            } else if("export".equals(com)) {
                try {
                    exportCards(cards, sc);
                } catch(IOException e) {
                    System.out.println("not found");
                }
            } else if("ask".equals(com)) {
                askCards(cards, sc);
            } else if("exit".equals(com)) {
                System.out.println("Commande \"" + com + "\"doesn't exist");
                break;
            } else {
                System.out.println("Commande \"" + com + "\" doesn't exist");
            }
        }
    }
}