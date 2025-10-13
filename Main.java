import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        ArrayStack <Cords> moveStack = new ArrayStack<>(15);

        Cords pit1 = null;
        Cords pit2 = null;
        Cords wumpus = null;
        Cords goal = null;
        Cords alwaysSafe1 = new Cords(2,1);
        Cords alwaysSafe2 = new Cords(2,2);
        Cords alwaysSafe3 = new Cords(1,2);

        moveStack.push(alwaysSafe3);
        moveStack.push(alwaysSafe2);
        moveStack.push(alwaysSafe1);



        try (Scanner fileScanner = new Scanner(new FileReader("C:\\Users\\Despi\\OneDrive\\Desktop\\cords.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                if (line.isBlank()) {
                    continue;
                }

                try (Scanner lineScanner = new Scanner(line)) {
                    lineScanner.useDelimiter("[(),]");

                    String type = lineScanner.next();
                    int x = lineScanner.nextInt();
                    int y = lineScanner.nextInt();

                    if (type.equals("P")) {
                        if (pit1 == null) {
                            pit1 = new Cords(x, y);
                        } else {
                            pit2 = new Cords(x, y);
                        }
                    } else if (type.equals("W")) {
                        wumpus = new Cords(x, y);
                    } else if (type.equals("G")) {
                        goal = new Cords(x, y);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Pit 1 = " + pit1.toString() + " pushing pit1 to stack");
        moveStack.push(pit1);
        System.out.println("Pit 2 = " + pit2.toString()  + " pushing pit2 to stack");
        moveStack.push(pit2);
        System.out.println("Wumpus = " + wumpus.toString() + " pushing wumpus to stack");
        moveStack.push(wumpus);
        System.out.println("Goal = " + goal.toString() + " pushing goal to stack");
        moveStack.push(goal);

        System.out.println("\nPeeking stack" + moveStack.toString());

        Agent agent = new Agent();

        System.out.println(agent);
    }
}