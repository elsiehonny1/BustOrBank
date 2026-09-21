public class Participant {

    private String name;
    private int cardCount;
    private Card[] hand;

    
    private Card[] possibleCards = {
        new Card("A", 11, false),
        new Card("2", 2, false),
        new Card("3", 3, false),
        new Card("4", 4, false),
        new Card("5", 5, false),
        new Card("6", 6, false),
        new Card("7", 7, false),
        new Card("8", 8, false),
        new Card("9", 9, false),
        new Card("10", 10, false),
        new Card("J", 10, false),
        new Card("Q", 10, false),
        new Card("K", 10, false)
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

        for (int i = 0; i < cardCount; i++) {
            total += hand[i].getValue(this);
        }

        return total;
    }

    public boolean isBust() {
        return this.getHandValue() > 21;
    }

    public boolean hasBlackJack() {
        return this.getHandValue() == 21 && this.getCardCount() == 2;
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