import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        /************ VARIABLES ************ */
        // Cords for game objects, init to null
        Cords pit1 = null;
        Cords pit2 = null;
        Cords wumpus = null;
        Cords paradise = null;

        char command; // stores user input command (m, kb, q)
        int moveCount = 0; // tracks num moves made
        int movesLeft = 10; // tracks num moves left
        Scanner userInput = new Scanner(System.in); // scanner for user input
        boolean useDynamicMovement = true;  // TOGGLE: T=Agent logic, F=hardcoded path


        /************FILE READER ************/
        System.out.println("****** WUMPUS WORLD AGENT *****\n");
        System.out.println("Reading cave cords from cords.txt...");

        // reads cords.txt to set up the game
        try (Scanner fileScanner = new Scanner(new FileReader("cords"))) {
            // loop through each line of file
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                if (line.isBlank()) { // skips empty lines
                    continue;
                }

                // scanner for the current line
                try (Scanner lineScanner = new Scanner(line)) {
                    String type = lineScanner.next().toLowerCase(); // gets object type (p, w, g)
                    int x = lineScanner.nextInt(); // gets x and y cords
                    int y = lineScanner.nextInt();

                    // logic to place the two pits
                    if (type.equalsIgnoreCase("p") || type.equalsIgnoreCase("pit")) {
                        if (pit1 == null) {
                            pit1 = new Cords(x, y);
                            System.out.println("  Pit set at (" + x + "," + y + ")");
                        } else {
                            pit2 = new Cords(x, y);
                            System.out.println("  Pit set at (" + x + "," + y + ")");
                        }
                        // logic to place wumpus
                    } else if (type.equalsIgnoreCase("w") || type.equalsIgnoreCase("wumpus")) {
                        wumpus = new Cords(x, y);
                        System.out.println("  Wumpus set at (" + x + "," + y + ")");
                        // logic to place paradise/gold
                    } else if (type.equalsIgnoreCase("g") || type.equalsIgnoreCase("paradise") || type.equalsIgnoreCase("gold")) {
                        paradise = new Cords(x, y);
                        System.out.println("  Paradise set at (" + x + "," + y + ")");
                    }
                }
            }
            // error handling if file not found or other issues
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: cords.txt file not found!");
            return;
        } catch (Exception e) {
            System.out.println("ERROR reading file: " + e);
            return;
        }

        // validates that all game objects were loaded from file
        if (pit1 == null || pit2 == null || wumpus == null || paradise == null) {
            System.out.println("ERROR: Incomplete cave configuration!");
            return;
        }

        /************* Initialize Agent GameBoard ************* */
        Agent agent = new Agent(); // creates the agent
        Gameboard gBoard = new Gameboard(pit1, pit2, wumpus, paradise); // creates the actual game board with object locations

        System.out.println("\nAgent starting at " + agent);

        // agent makes initial perception of starting cell (1,1)
        agent.updateKB(gBoard);

        /************* debug planned route ************* */
        ArrayStack<Cords> moveStack = null; // stack to hold the pre-planned moves
        if (!useDynamicMovement) { // if dynamic movement is OFF
            moveStack = planSafeMoves(agent); // load the hardcoded safe path
        }

        System.out.println("\nCommands: m (move), q (query), kb (print currKnowledgeBase)");
        System.out.println("OBJ: move 10x safely or win.\n");

        /************* MAIN GAME LOOP *************/
        // loop runs for 10 moves or until agent dies
        while (moveCount < 10 && agent.isAlive()) {
            System.out.print("Enter command (m, kb, q): ");
            String input = userInput.next().toLowerCase();
            command = input.charAt(0);

            // handles user command
            switch (command) {
                case 'm':
                    System.out.println("\n***Move " + (moveCount + 1) + " ***");

                    Cords dest; // destination cord for the move

                    if (useDynamicMovement) {
                        // let the agent decide the best move
                        dest = agent.chooseBestMove();

                        // handles case where agent has no moves
                        if (dest == null) {
                            System.out.println("No available moves! Game ending...");
                            moveCount = 10;
                            break;
                        }
                    } else {
                        // if using pre-planned route, get next move from the stack
                        if (moveStack.isEmpty()) {
                            System.out.println("No more pre-planned moves!");
                            moveCount = 10;
                            break;
                        }
                        dest = moveStack.pop();
                    }

                    // move the agent to destination
                    agent.AgentMoveto(dest.getX(), dest.getY());

                    // get the TRUE state of the new cell
                    Cords currPos = gBoard.getChamber(agent.getxPOS(), agent.getyPOS());

                    // checks if agent moved into a pit
                    if (currPos.hasPit()) {
                        agent.kill();
                        System.out.println("Agent has fallen into pit, Dead");
                        break;
                        // checks if agent moved into wumpus
                    } else if (currPos.hasWumpus()) {
                        agent.kill();
                        System.out.println("Agent run into Wumpus, Dead");
                        break;
                        // checks if agent found paradise (WIN condition)
                    } else if (currPos.isParadise()) {
                        System.out.println("Agent found paradise, win");
                        System.out.println("\nFinal Knowledge Base:");
                        agent.printAgentKB();
                        userInput.close();
                        return;
                    }

                    // if agent survived the move...
                    if (agent.isAlive()) {
                        agent.updateKB(gBoard); // update knowledge based on new cell's percepts
                        moveCount++;
                        movesLeft = 10 - moveCount;
                        System.out.println("Moves remaining: " + movesLeft);
                    }
                    break;

                case 'q':
                    // handles user query for a specific cell
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
                    // prints the agent's knowledge base (b/c kb is 2 letters)
                    System.out.println();
                    agent.printAgentKB();
                    break;

                default:
                    System.out.println("Invalid command. Use: m (move), q (query), kb (knowledge base), quit");
            }
        }

        // --- Game end summary ---
        System.out.println("*******GAME OVER*****");

        // prints final stats
        System.out.println("Total moves made: " + moveCount);
        System.out.println("Agent status: " + (agent.isAlive() ? "ALIVE ✓" : "DEAD ✗"));

        // prints reason for game end if agent survived
        if (agent.isAlive() && moveCount >= 10) {
            System.out.println("Reason: Maximum moves reached");
        }

        // prints final KB and the actual answer key/layout
        System.out.println("\nFinal Knowledge Base:");
        agent.printAgentKB();
        System.out.println("\nActual Cave Layout:");
        System.out.println(gBoard);

        userInput.close();
    }


    /*** DEBUG METHOD, CREATES A HARDCODED SAFE PATH FOR TESTING ***/
    private static ArrayStack<Cords> planSafeMoves(Agent agent){
        ArrayStack<Cords> moves = new ArrayStack<>(20); // stack to hold moves

        // the pre-planned path
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

        // loops backwards to push onto stack in correct order (LIFO)
        for(int i = safePath.length - 1; i >= 0; i--) {
            moves.push(safePath[i]);
        }
        return moves;
    }
}