import java.util.*;

public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        boolean error = false;
        List<Integer> list = new ArrayList<>();

        String input = scanner.nextLine();
        String[] strArr = input.trim().split(" ");
        for (String s : strArr) {
            try {
                int x = Integer.parseInt(s);
                list.add(x);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                error = true;
                break;
            }
        }

        if (!error) {
            Collections.sort(list);
            int search = scanner.nextInt();
            scanner.close();


            int distance = -1;
            for (Integer i : list) {
                if (distance == -1) {
                    distance = Math.abs(search - i);
                } else if (Math.abs(search - i) < distance) {
                    distance = Math.abs(search - i);
                }
            }

            for (Integer i : list) {
                if (distance == Math.abs(search - i)) {
                    System.out.print(i + " ");
                }
            }
        }


    }
}