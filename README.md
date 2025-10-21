# Programming Project #1
**ACO/MAT 494 - FALL 2025**
**Instructor: Dr. Miller-Edwards**

## Overview
This project implements an intelligent agent that navigates a 4x4 Wumpus World by reading a .txt file (cords). The board contains hidden Wumpus and pit locations, which the agent must infer using percepts (breeze/stench). It maintains a knowledge base (KB) to track visited, safe, and potentially dangerous cells.
NOTE: Make sure the cords.txt file is located **outside** the source code folder.


## Agent Decision Logic

The agent initializes at (1,1) during the start of every game. Information regarding where the Wumpus and pitfalls are on display. Alongside it, there is information of the commands the player can use:

**m - Move: Moves the agent to a specific cell on the board. It updates the agent’s position and prints a message showing where the agent went.**

**q - Query: Ask the agent about a specific cell. It will tell you if the cell is safe, visited, breezy, stinky, or possibly dangerous with probabilities for pits and the Wumpus. It’s basically checking what the agent knows about that spot.**

**kb - Knowledge Base: Helps the agent make decisions. It also tracks which cells are safe, visited, breezy, stinky, or possibly dangerous, and helps the agent make decisions about where to move next**

The agent chooses its next move based on the following priority:
1. Move to an adjacent safe and unvisited cell.

2. Move to an adjacent unknown cell with 0% danger.

3. Backtrack to the closest previously visited safe cell.

4. Take the unknown cell with the lowest danger probability.

5. Stop if no moves are available.

Updates knowledge base as it explores, marking cells as visited, safe, breezy, or stinky.

Can query its KB to see the status of any cell.
Additionally, here is a video demonstration of the program in use: https://youtu.be/_IgXp2dXFW0?si=wQQsA0gGUpLU6WZE



 
