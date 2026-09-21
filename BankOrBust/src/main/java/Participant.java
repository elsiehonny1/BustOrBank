public class Participant {

    private String name;
    private int cardCount;
    private Card[] hand;

    
    private Card[] possibleCards = {
        new Card("A", 11),
        new Card("2", 2),
        new Card("3", 3),
        new Card("4", 4),
        new Card("5", 5),
        new Card("6", 6),
        new Card("7", 7),
        new Card("8", 8),
        new Card("9", 9),
        new Card("10", 10),
        new Card("J", 10),
        new Card("Q", 10),
        new Card("K", 10)
    };


    public Participant(String name) {
        this.name = name;
        this.hand = new Card[22];
        this.cardCount = 0;
    }

    public void receiveCard() {
        int randomIndex = (int)(Math.random() * possibleCards.length);

        hand[cardCount] = possibleCards[randomIndex];
        cardCount++;
    }

    public Card[] getHand() {
        return this.hand;
    }

    public int getCardCount() {
        return this.cardCount;
    }

    public int getHandValue() {
        int total = 0;
        int aceCount = 0;

        for (int i = 0; i < cardCount; i++) {
            total += hand[i].getValue();

            if (hand[i].getSymbol().equals("A")) {
                aceCount++;
            }
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    public boolean isBust() {
        if (this.getHandValue() > 21) {
            return true;
        }

        return false;
    }

    public boolean hasBlackJack() {
        if (this.getHandValue() == 21 && this.getCardCount() == 2) {
            return true;
        }

        return false;
    }
    
    public String printCards(boolean player) {
        String cards = "";

        for (int i = 0; i < cardCount; i++) {

            if (player == false && i == 1) {
                cards += "[Hidden] ";
            }
            else {
                cards += hand[i].getSymbol() + " ";
            }
        }
        
        return cards;
    }
}