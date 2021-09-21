package flashcards;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {

        boolean startExport = false;
        boolean startImport = false;

        String filenameImport = "";
        String filenameExport = "";

        for(int i=0; i < args.length ; i+=2) {
            if(args[i].equals("-import")) {
                startImport = true;
                filenameImport = args[i+1];
            }

            if(args[i].equals("-export")) {
                startExport = true;
                filenameExport = args[i+1];
            }
        }

        final Map<String, String> cardMap = new LinkedHashMap<>();
        final Scanner scanner = new Scanner(System.in);


        FlashCardController start = new FlashCardController(cardMap, scanner,startExport,startImport,filenameImport,filenameExport);




        start.init();
        scanner.close();
    }

}

