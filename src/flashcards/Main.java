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
            if (card.equals(check)) {
                return 1;
            }
        }
        return 0;
    }

    private static int checkDefinition(String definition, Map<String, String> cards) {
        for (String check : cards.values()){
            if (definition.equals(check)) {
                return 1;
            }
        }
        return 0;
    }

    private static void addCards(Map<String, String> cards, Scanner sc, Logging log) {
        String tempCard = null;
        String tempDefinition = null;
        System.out.println("The card:");
        log.saveData("The card:");
        tempCard = sc.nextLine();
        log.saveData(tempCard);
        if(checkCard(tempCard, cards) == 1) {
            System.out.println("The card \"" + tempCard + "\" already exists.");
            log.saveData("The card \"" + tempCard + "\" already exists.");
            return ;
        }
        System.out.println("The definition of the card:");
        log.saveData("The definition of the card:");
        tempDefinition = sc.nextLine();
        log.saveData(tempDefinition);
        if(checkDefinition(tempDefinition, cards) == 1) {
            System.out.println("The definition \"" + tempDefinition + "\" already exists.");
            log.saveData("The definition \"" + tempDefinition + "\" already exists.");
            return ;
        }
        cards.put(tempCard, tempDefinition);
        System.out.println("The pair (" + tempCard + ":" + tempDefinition + ") has been added.");
        log.saveData("The pair (" + tempCard + ":" + tempDefinition + ") has been added.");
    }

    private static int checkAnswer(String answer, String response, Map<String, String> cards, Logging log) {
        for(var entry : cards.entrySet()) {
            if (entry.getValue().equals(response)) {
                System.out.println("Wrong answer. The correct one is \"" + answer + "\", you've just written the definition of \"" + entry.getKey() + "\".");;
                log.saveData("Wrong answer. The correct one is \"" + answer + "\", you've just written the definition of \"" + entry.getKey() + "\".");
                return 1;
            }
        }
        return 0;
    }

    static private void removeCard(Map<String, String> cards, Scanner sc, Logging log, Mistakes counter) {
        System.out.println("Card:");
        log.saveData("Card:");
        String key = sc.nextLine();
        log.saveData(key);
        if(cards.containsKey(key)) {
            cards.remove(key);
            counter.deleteCard(key);
            System.out.println("The card has been removed.");
            log.saveData("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + key + "\": there is no such card.");
            log.saveData("Can't remove \"" + key + "\": there is no such card.");
        }
    }

    private static void askCards(Map<String, String> cards, Scanner sc, Logging log, Mistakes counter) {
        System.out.println("How many times to ask?");
        log.saveData("How many times to ask?");
        int count = 0;
        try {
            count = sc.nextInt();
            log.saveData(String.valueOf(count));
        } catch(Exception e) {
            System.out.println("You must print number" + e.getMessage());
            log.saveData("You must print number" + e.getMessage());
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
            log.saveData("Print the definition of \"" + keys[card] + "\":");
            response = sc.nextLine();
            answer = cards.get(keys[card]);
            if (answer.equals(response)) {
                System.out.println("Correct answer.");
                log.saveData("Correct answer.");
            } else {
                counter.addCount(keys[card]);
                if (checkAnswer(answer, response, cards, log) != 1) {
                    System.out.println("Wrong answer. The correct one is \"" + answer + "\".");
                    log.saveData("Wrong answer. The correct one is \"" + answer + "\".");
                }
            }
        }
    }

    private static void exportCards(Map<String, String> cards,  Scanner sc, Logging log, Mistakes counter, String path) throws IOException {
        if(path == null) {
            System.out.println("File name:");
            log.saveData("File name:");
            path = sc.nextLine();
            log.saveData(path);
        }
        File file = new File(path);
        FileWriter writer = new FileWriter(file);
        for (var entry : cards.entrySet()) {
            writer.write(entry.getKey() + "\n");
            writer.write(entry.getValue() + "\n");
            writer.write(counter.getNumberOfMistakes(entry.getKey()) + "\n");
        }
        writer.close();
        System.out.println(cards.size() + " cards have been saved.");
        log.saveData(cards.size() + " cards have been saved.");
    }

    private static void importCards(Map<String, String> cards, Scanner sc, Logging log, Mistakes counter, String path) throws IOException {
        if(path == null) {
            System.out.println("File name:");
            log.saveData("File name:");
            path = sc.nextLine();
            log.saveData(path);
        }
        List<String> list = Files.readAllLines(Paths.get(path));
        String tempKey = null;
        String tempValue = null;
        int tempMistakes = 0;
        int sum = 0;
        for(int i = 0; i < list.size(); i = i + 3) {
            tempKey = list.get(i);
            tempValue = list.get(i + 1);
            tempMistakes = Integer.valueOf(list.get(i + 2));
            if(cards.putIfAbsent(tempKey, tempValue) != null) {
                cards.remove(tempKey);
                cards.put(tempKey, tempValue);
            }
            if (tempMistakes != 0) {
                counter.updateAfterImport(tempKey, tempMistakes);
            }
            sum++;
        }
        System.out.println(sum + " cards have been loaded.");
        log.saveData(sum + " cards have been loaded.");
    }

    private static void exportLog(Logging log, Scanner sc) throws IOException {
        System.out.println("File name:");
        log.saveData("File name:");
        String path = sc.nextLine();
        log.saveData(path);
        log.exportData(path);
        System.out.println("The log has been saved.");
        log.saveData("The log has been saved.");
    }

    private static void findHardTest(Logging log, Mistakes counter) {
        ArrayList<String> keys = counter.hardestCard();
        if (keys == null) {
            System.out.println("There are no cards with errors.");
            log.saveData("There are no cards with errors.");
        } else if (keys.size() == 1){
            System.out.println("The hardest card is \"" + keys.get(0) + "\". You have " + counter.getNumberOfMistakes(keys.get(0)) + " errors answering it.");
            log.saveData("The hardest card is \"" + keys.get(0) + "\". You have " + counter.getNumberOfMistakes(keys.get(0)) + " errors answering it.");
        } else {
            System.out.print("The hardest cards are ");
            log.saveData("The hardest cards are ");
            int i = 0;
            while(i < keys.size())
            {
                System.out.print("\"" + keys.get(i) + "\"");
                log.saveData("\"" + keys.get(i) + "\"");
                i++;
            }
            System.out.println(". You have " + counter.getNumberOfMistakes(keys.get(0)) + " errors answering them.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, String> cards = new LinkedHashMap<>();
        Mistakes counter = new Mistakes();
        Logging log = new Logging();
        String com = null;
        if(args.length != 0) {
            try {
                if("-import".equals(args[0])) {
                    importCards(cards, sc, log, counter, args[1]);
                } else if(args.length > 2 && "-import".equals(args[2])) {
                    importCards(cards, sc, log, counter, args[3]);
                }

            } catch(IOException e) {
                System.out.println("not found");
            }
        }
        while (true) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            log.saveData("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            com = sc.nextLine();
            log.saveData(com);
            if ("add".equals(com)) {
                addCards(cards, sc, log);
            } else if("remove".equals(com)) {
                removeCard(cards, sc, log, counter);
            } else if("import".equals(com)) {
                try {
                    importCards(cards, sc, log, counter, null);
                } catch(IOException e) {
                    System.out.println("not found");
                }
            } else if("export".equals(com)) {
                try {
                    exportCards(cards, sc, log, counter, null);
                } catch(IOException e) {
                    System.out.println("not found");
                }
            } else if("ask".equals(com)) {
                askCards(cards, sc, log, counter);
            } else if("exit".equals(com)) {
                if(args.length != 0) {
                    try {
                        System.out.println("Bye bye!");
                        if("-export".equals(args[0])) {
                            exportCards(cards, sc, log, counter, args[1]);
                        } else if(args.length > 2 && "-export".equals(args[2])){
                            exportCards(cards, sc, log, counter, args[3]);
                        }
                    } catch(IOException e) {
                        System.out.println("not found");
                    }
                    break ;
                }
                System.out.println("Bye bye!");
                break ;
            } else if("log".equals(com)) {
                try {
                    exportLog(log, sc);
                } catch(IOException e) {
                    System.out.println("not found");
                    log.saveData("not found");
                }
            }  else if("hardest card".equals(com)) {
                findHardTest(log, counter);
            }  else if("reset stats".equals(com)) {
                counter.reset();
                System.out.println("Card statistics has been reset.");
                log.saveData("Card statistics has been reset.");
            }
            else {
                System.out.println("Commande \"" + com + "\" doesn't exist");
                log.saveData("Commande \"" + com + "\" doesn't exist");
            }
        }
    }
}