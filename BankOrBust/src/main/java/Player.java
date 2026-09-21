public class Player extends Participant {
    // Constructor 
    public Player() {
        super()
    }


    public String getCoach(int[] playerHand, int dealerUpCard) {
        int[] formatedHand;
        for (int i = 0; i < playerHand.length; i++) {

        }



        return result;
    }

    public boolean hit(String input) {
        if (input.toLowerCase().equals("hit")) {
            this.recieveCard();
            return true;
        }
        return false;
    }

    public boolean stand(String input) {
        return input.toLowerCase().equals("stand");
    }

    public boolean double() {
        
    }

    public boolean split() {

    }
}