import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        ArrayStack <Cords> moveStack = new ArrayStack<>(20);
        Cords pit1 = null;
        Cords pit2 = null;
        Cords wumpus = null;
        Cords paradise = null;
        Cords alwaysSafe1 = new Cords(2,1);
        Cords alwaysSafe2 = new Cords(2,2);


        Cords alwaysSafe3 = new Cords(1,2);
        moveStack.push(alwaysSafe3);
        moveStack.push(alwaysSafe2);
        moveStack.push(alwaysSafe1);

        char command;
        int moveCount = 0;
        int movesLeft = 10;
        Scanner userInput = new Scanner(System.in);


        /************FILE READER ************ (make func)*/
        try (Scanner fileScanner = new Scanner(new FileReader("cords"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                if (line.isBlank()) {
                    continue;
                }

                try (Scanner lineScanner = new Scanner(line)) {

                    String type = lineScanner.next().toLowerCase();
                    int x = lineScanner.nextInt();
                    int y = lineScanner.nextInt();

                    if (type.equalsIgnoreCase("pit")) {
                        if (pit1 == null) {
                            pit1 = new Cords(x, y);
                        } else {
                            pit2 = new Cords(x, y);
                        }
                    } else if (type.equalsIgnoreCase("wumpus")) {
                        wumpus = new Cords(x, y);
                    } else if (type.equalsIgnoreCase("paradise")) {
                        paradise = new Cords(x, y);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }


        /**************** TESTING STACK ************* */
       /* System.out.println("Pit 1 = " + pit1.toString() + " pushing pit1 to stack");
        moveStack.push(pit1);
        System.out.println("Pit 2 = " + pit2.toString()  + " pushing pit2 to stack");
        moveStack.push(pit2);
        System.out.println("Wumpus = " + wumpus.toString() + " pushing wumpus to stack");
        moveStack.push(wumpus);
        System.out.println("Paradise = " + paradise.toString() + " pushing paradise to stack");
        moveStack.push(paradise);*/
        System.out.println("\nPeeking stack" + moveStack.toString());


        /************* TESTING AGENT ************* */
        Agent agent = new Agent();
        System.out.println(agent);



        /************* TESTING GBOARD ************* */
        System.out.println("\n************ TESTING GBOARD *************");
        Gameboard gBoard = new Gameboard();

        System.out.println(gBoard);



        /************* TESTING GBOARD ************* */

        System.out.println("\nGame Started" + "\nAgent Pos currently at" + agent +
                "\nUse 'm' to move and 'q' to query");

/************* MAIN GAME ************* */
        do{
            command = userInput.next().charAt(0);
            switch (command){
                case 'm':
                    Cords dest = moveStack.pop();
                    agent.AgentMoveto(dest.getX(), dest.getY());
                    moveCount++;
                    movesLeft--;
                    System.out.println(agent);
                    System.out.println(movesLeft + " moves left");
                    break;

                case 'q':
                    break;

                default:
                    System.out.println("Invalid command, use 'm' to move and 'q' to query");
            }
        }while (moveCount <= 10 && agent.agentDied() == true);


    }
}