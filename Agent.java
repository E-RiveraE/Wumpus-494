public class Agent {

    private int xPOS;
    private int yPOS;

    Cords POS = new Cords(xPOS,yPOS);

    public Agent (){
        this.xPOS = 0;
        this.yPOS = 0;
    }


    public void AgentMove(){


    }


    public String toString() {
        return "Current POS (" + "xPOS=" + xPOS + ", yPOS=" + yPOS + ')';
    }
}
