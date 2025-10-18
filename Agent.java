public class Agent {

    private int xPOS;
    private int yPOS;

    //Cords POS = new Cords(xPOS,yPOS);

    public Agent (){
        this.xPOS = 1;
        this.yPOS = 1;
    }

    boolean agentDied(){
        return true;
    }


    public void AgentMoveto(int xPOS, int yPOS){

        this.xPOS = xPOS;
        this.yPOS =yPOS;
    }


    public String toString() {
        return "Current POS (" + xPOS + "," + yPOS + ')';
    }
}
