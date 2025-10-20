import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        /************ VARIABLES ************ */
        Cords pit1 = null;
        Cords pit2 = null;
        Cords wumpus = null;
        Cords paradise = null;

        char command;
        int moveCount = 0;
        int movesLeft = 10;
        Scanner userInput = new Scanner(System.in);
        boolean useDynamicMovement = true;  //  Toggle for dynamic vs pre-planned


        /************FILE READER ************/
        System.out.println("****** WUMPUS WORLD AGENT *****\n");
        System.out.println("Reading cave cords from cords.txt...");

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

                    if (type.equalsIgnoreCase("p") || type.equalsIgnoreCase("pit")) {
                        if (pit1 == null) {
                            pit1 = new Cords(x, y);
                            System.out.println("  Pit set at (" + x + "," + y + ")");
                        } else {
                            pit2 = new Cords(x, y);
                            System.out.println("  Pit set at (" + x + "," + y + ")");
                        }
                    } else if (type.equalsIgnoreCase("w") || type.equalsIgnoreCase("wumpus")) {
                        wumpus = new Cords(x, y);
                        System.out.println("  Wumpus set at (" + x + "," + y + ")");
                    } else if (type.equalsIgnoreCase("g") || type.equalsIgnoreCase("paradise") || type.equalsIgnoreCase("gold")) {
                        paradise = new Cords(x, y);
                        System.out.println("  Paradise set at (" + x + "," + y + ")");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: cords.txt file not found!");
            return;
        } catch (Exception e) {
            System.out.println("ERROR reading file: " + e);
            return;
        }

        // Validate configuration
        if (pit1 == null || pit2 == null || wumpus == null || paradise == null) {
            System.out.println("ERROR: Incomplete cave configuration!");
            return;
        }

        /************* Initialize Agent GameBoard ************* */
        Agent agent = new Agent();
        Gameboard gBoard = new Gameboard(pit1, pit2, wumpus, paradise);

        System.out.println("\nAgent starting at " + agent);

        // Initial perception
        agent.updateKB(gBoard);

        /************* debug planned route ************* */
        ArrayStack<Cords> moveStack = null;
        if (!useDynamicMovement) {
            moveStack = planSafeMoves(agent);
        }

        System.out.println("\nCommands: m (move), q (query), kb (print currKnowledgeBase)");
        System.out.println("OBJ: move 10x safely or win.\n");

        /************* MAIN GAME LOOP *************/

        while (moveCount < 10 && agent.isAlive()) {
            System.out.print("Enter command (m/kb/q/): ");
            String input = userInput.next().toLowerCase();
            command = input.charAt(0);

            switch (command) {
                case 'm':
                    System.out.println("\n***Move " + (moveCount + 1) + " ***");

                    Cords dest;

                    if (useDynamicMovement) {
                        // chooses best move
                        dest = agent.chooseBestMove();

                        if (dest == null) {
                            System.out.println("No available moves! Game ending...");
                            moveCount = 10;
                            break;
                        }
                    } else {
                        // PRE-PLANNED: Pop from stack
                        if (moveStack.isEmpty()) {
                            System.out.println("No more pre-planned moves!");
                            moveCount = 10;
                            break;
                        }
                        dest = moveStack.pop();
                    }

                    // Move agent
                    agent.AgentMoveto(dest.getX(), dest.getY());

                    // Check current position for dangers
                    Cords currPos = gBoard.getChamber(agent.getxPOS(), agent.getyPOS());

                    if (currPos.hasPit()) {
                        agent.kill();
                        System.out.println("Agent has fallen into pit, Dead");
                        break;
                    } else if (currPos.hasWumpus()) {
                        agent.kill();
                        System.out.println("Agent run into Wumpus, Dead");
                        break;
                    } else if (currPos.isParadise()) {
                        System.out.println("Agent found paradise, win");
                        System.out.println("\nFinal Knowledge Base:");
                        agent.printAgentKB();
                        userInput.close();
                        return;
                    }

                    // Agent survived - update knowledge
                    if (agent.isAlive()) {
                        agent.updateKB(gBoard);
                        moveCount++;
                        movesLeft = 10 - moveCount;
                        System.out.println("Moves remaining: " + movesLeft);
                    }
                    break;

                case 'q':
                    System.out.print("Enter coordinates to query (x y): ");
                    try {
                        int qx = userInput.nextInt();
                        int qy = userInput.nextInt();
                        agent.query(qx, qy);
                    } catch (Exception e) {
                        System.out.println("Invalid cords enter in format: 1 2.");
                        userInput.nextLine(); // Clear buffer
                    }
                    break;

                case 'k':
                    System.out.println();
                    agent.printAgentKB();
                    break;

                default:
                    System.out.println("Invalid command. Use: m (move), q (query), kb (knowledge base), quit");
            }
        }

        // Game end summary
        System.out.println("*******GAME OVER*****");

        System.out.println("Total moves made: " + moveCount);
        System.out.println("Agent status: " + (agent.isAlive() ? "ALIVE ✓" : "DEAD ✗"));

        if (agent.isAlive() && moveCount >= 10) {
            System.out.println("Reason: Maximum moves reached");
        }

        System.out.println("\nFinal Knowledge Base:");
        agent.printAgentKB();

        System.out.println("\nActual Cave Layout:");
        System.out.println(gBoard);

        userInput.close();
    }


    /************ SAFE MOVES (bruteforce for debug) ************ */
    private static ArrayStack<Cords> planSafeMoves(Agent agent){
        ArrayStack<Cords> moves = new ArrayStack<>(20);

        Cords[] safePath = {
                new Cords(1, 2),
                new Cords(2, 2),
                new Cords(2, 1),
                new Cords(1, 1),
                new Cords(2, 1),
                new Cords(2, 2),
                new Cords(1, 2),
                new Cords(1, 3),
                new Cords(2, 3),
                new Cords(3, 2)
        };

        for(int i = safePath.length - 1; i >= 0; i--) {
            moves.push(safePath[i]);
        }
        return moves;
    }
}