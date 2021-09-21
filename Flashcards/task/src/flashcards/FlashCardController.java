package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FlashCardController {
    private final Map<String, String> cardMap;
    private final Scanner scanner;
    private final Set<FlashCard> mistakeCounter;
    private final List<String> log;
    private final boolean exportOnEnd;
    private final boolean importOnStart;
    private final String startImportFile;
    private final String endExportFile;


    public FlashCardController(Map<String, String> cardMap, Scanner scanner, boolean exportOnEnd, boolean importOnStart,
                               String startImportFile, String endExportFile) {
        this.cardMap = cardMap;
        this.scanner = scanner;
        this.mistakeCounter = new HashSet<>();
        this.log = new ArrayList<>();
        this.exportOnEnd = exportOnEnd;
        this.importOnStart = importOnStart;
        this.startImportFile = startImportFile;
        this.endExportFile = endExportFile;
    }


    public void init() {
        if (this.importOnStart) {
            printAndAddToLog(this.importCards(this.startImportFile));
        }


        boolean flag = true;
        while (flag) {
            printAndAddToLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String selection = logAndReturn(scanner.nextLine().toLowerCase());
            switch (selection) {
                case "add":
                    printAndAddToLog(addCard());
                    break;
                case "remove":
                    printAndAddToLog(removeCard());
                    break;
                case "import":
                    printAndAddToLog(importCards());
                    break;
                case "export":
                    printAndAddToLog(exportCards());
                    break;
                case "ask":
                    ask();
                    break;
                case "log":
                    printAndAddToLog(log());
                    break;
                case "hardest card":
                    printAndAddToLog(hardestCard());
                    break;
                case "reset stats":
                    printAndAddToLog(resetStats());
                    break;
                case "exit":
                    flag = false;
                    if (exportOnEnd) {
                        this.exportCards(this.endExportFile);
                    }
                    printAndAddToLog("Bye bye!");
                    break;
            }
        }
        scanner.close();
    }

    private String addCard() {
        printAndAddToLog("The card:");
        String term = logAndReturn(scanner.nextLine());
        if (cardMap.containsKey(term)) {
            return "The card \"" + term + "\" already exists.";
        }
        printAndAddToLog("The definition of the card:");
        String definition = logAndReturn(scanner.nextLine());
        if (cardMap.containsValue(definition)) {
            return "The definition \"" + definition + "\" already exists. \n";
        }
        cardMap.put(term, definition);
        mistakeCounter.add(new FlashCard(term, definition));

        return "The pair (\"" + term + "\":\"" + definition + "\") has been added\n";
    }

    private String removeCard() {
        printAndAddToLog("Which card?");
        String input = logAndReturn(scanner.nextLine());
        boolean removed = false;

        if (cardMap.containsKey(input)) {
            removed = cardMap.remove(input) != null;
            FlashCard card = this.getFlashCard(input);
            if (card != null) {
                mistakeCounter.remove(card);
            }
        }

        return removed ? "The card has been removed.\n" : "Can't remove \"" + input + "\": there is no such card\n";
    }

    private String importCards() {
        printAndAddToLog("File name:");
        String fileName = logAndReturn(scanner.nextLine());
        return importCards(fileName);
    }

    private String importCards(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);
        try (Scanner fileScanner = new Scanner(file)) {
            int counter = 0;
            while (fileScanner.hasNext()) {
                String[] inputArr = fileScanner.nextLine().split("\t");
                try {
                    int errors = Integer.parseInt(inputArr[2]);
                    cardMap.put(inputArr[0], inputArr[1]);
                    mistakeCounter.add(new FlashCard(inputArr[0], inputArr[1], errors));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return "";
                }
                counter++;
            }
            sb.append(counter);
            sb.append(" cards have been loaded.\n");


        } catch (FileNotFoundException e) {
            sb.append("File Not Found");
        }
        return sb.toString();
    }

    private String exportCards() {
        printAndAddToLog("File name:");
        String fileName = logAndReturn(scanner.nextLine());
        return exportCards(fileName);
    }

    private String exportCards(String fileName) {
        File file = new File(fileName);

        try (FileWriter fw = new FileWriter(file)) {
            for (FlashCard card : mistakeCounter) {
                fw.write(card.getTerm() + "\t" + card.getDescription() + "\t" + card.getFailureCounter() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cardMap.size() + " cards have been saved\n";
    }

    private void ask() {

        printAndAddToLog("How many times to ask?");

        int number = logAndReturn(scanner.nextInt());
        scanner.nextLine();
        int counter = 1;
        for (var entry : cardMap.entrySet()) {
            if (counter > number) {
                break;
            }
            printAndAddToLog("Print the definition of \"" + entry.getKey() + "\":");
            String input = logAndReturn(scanner.nextLine());
            if (input.equals(entry.getValue())) {
                printAndAddToLog("Correct!");
            } else {
                if (cardMap.containsValue(input)) {
                    String key = getKey(input);
                    if (!key.equals("")) {
                        printAndAddToLog("Wrong. The right answer is \"" + entry.getValue() + "\", but your definition is correct for \"" + key + "\".");
                    }
                } else {
                    printAndAddToLog("Wrong. The right answer is \"" + entry.getValue() + "\".");
                }

                FlashCard card = getFlashCard(entry.getKey());
                if (card != null) {
                    card.incrementFailureCounter();
                }
            }
            counter++;
        }
        printAndAddToLog("");

    }

    private String log() {
        printAndAddToLog("File name:");
        String fileName = logAndReturn(scanner.nextLine());

        File file = new File(fileName);

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (String s : log) {
                fileWriter.write(s + "\n");
            }
        } catch (IOException e) {
            return "Not written to file\n";

        }

        return "The log has been saved.\n";
    }

    private String hardestCard() {


        int highest = 0;
        int counter = 1;

        for (FlashCard card : mistakeCounter) {
            if (card.getFailureCounter() > highest) {
                highest = card.getFailureCounter();
                counter = 1;
            } else if (card.getFailureCounter() == highest) {
                counter++;
            }
        }

        if (highest != 0) {
            StringBuilder sb = new StringBuilder();

            if (counter > 1) {
                sb.append("The hardest cards are");
            } else {
                sb.append("The hardest card is");
            }


            for (FlashCard card : mistakeCounter) {
                if (card.getFailureCounter() == highest) {
                    sb.append(" \"" + card.getTerm() + "\"");
                }
            }

            if (counter > 1) {
                sb.append(". You have " + highest + " errors answering them.");
            } else {
                sb.append(". You have " + highest + " errors answering it.");
            }

            return sb.toString() + "\n";
        } else {
            return "There are no cards with errors.\n";
        }
    }

    private String resetStats() {

        for (FlashCard card : mistakeCounter) {
            card.resetFailureCounter();
        }

        return "Card statistics have been reset.\n";

    }

    private String getKey(String value) {
        for (var entry : cardMap.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return "";
    }

    private FlashCard getFlashCard(String term) {
        for (FlashCard fc : this.mistakeCounter) {
            if (fc.getTerm().equals(term)) {
                return fc;
            }
        }
        return null;
    }

    private void printAndAddToLog(String s) {
        System.out.println(s);
        log.add(s);
    }

    private String logAndReturn(String s) {
        log.add(s);
        return s;
    }

    private int logAndReturn(Integer i) {
        log.add(i.toString());
        return i;
    }

}
