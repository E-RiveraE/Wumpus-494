public class BoardKnowledge {

    private boolean visited;
    private boolean safe;
    private boolean isBreezy;

    private boolean isStinky;


    public BoardKnowledge() {
        this.visited = false;
        this.safe = false;
        this.isBreezy = false;
        this.isStinky = false;
    }

    public boolean isVisted() {
        return visited;
    }

    public boolean isBreezy() {
        return isBreezy;
    }

    public boolean isSafe() {
        return safe;
    }

    public boolean isStinky() {
        return isStinky;
    }

    //setters
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setBreezy(boolean breezy) {
        isBreezy = breezy;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public void setStinky(boolean stinky) {
        isStinky = stinky;
    }
}
