import java.util.Random;
public class Participant {

    private String name;
    private int cardCount = 0;
    Card[] hand;

    public Participant(String name, Card[] hand, int cardCount) {
        this.name = name;
        this.hand = hand;
        this.cardCount = cardCount;
    }


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
        
    }
       
}