public class Gameboard {

    private Cords[][] grid;

    public Gameboard() {

        this.grid = new Cords[4][4];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                this.grid[i][j] = new Cords(grid.length - i, j+1);
            }
        }
    }





    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                stringBuilder.append(grid[i][j].toString());
                stringBuilder.append("\t"); // Add a tab for spacing
            }
            stringBuilder.append("\n"); // Add a newline after each row
        }
        // Return the final, complete string
        return stringBuilder.toString();
    }
}