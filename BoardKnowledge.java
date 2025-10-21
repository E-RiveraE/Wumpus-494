/*** DATA CLASS, HOLDS ALL KNOWLEDGE ABOUT A SINGLE CELL ***/
public class BoardKnowledge {

    // --- CELL STATE BOOLS ---
    private boolean visited;  // T if agent has been here
    private boolean safe;     // T if cell is known/inferred to be safe
    private boolean isBreezy; // T if a breeze was felt here
    private boolean isStinky; // T if a stink was smelled here


    /****** CONSTRUCTOR, INITS ALL BOOLS TO F ******/
    public BoardKnowledge() {
        this.visited = false;
        this.safe = false;
        this.isBreezy = false;
        this.isStinky = false;
    }

    /*** GETTERS FOR CELL KNOWLEDGE ***/
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

    /*** SETTERS FOR CELL KNOWLEDGE ***/
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