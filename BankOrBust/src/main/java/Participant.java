public class Participant {

    private final String name;
    private int cardCount;
    private Card[] hand;

    
    private final Card[] possibleCards = {
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
        this.hand = new Card[0];
        this.cardCount = 0;
    }

    public void receiveCard() {
        int randomIndex = (int)(Math.random() * possibleCards.length);
        Card newCard = new Card(possibleCards[randomIndex]);

        if (this instanceof Dealer && cardCount == 1) {
            newCard.setIsHidden(true);
        }

        Card[] newHand = new Card[hand.length + 1];
        System.arraycopy(hand, 0, newHand, 0, hand.length);
        newHand[hand.length] = newCard;
        hand = newHand;
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
    
    public String printCards() {
        StringBuilder top = new StringBuilder();
        StringBuilder rankLine = new StringBuilder();
        StringBuilder midLine = new StringBuilder();
        StringBuilder rankLineFlipped = new StringBuilder();
        StringBuilder bottom = new StringBuilder();

        for (int i = 0; i < cardCount; i++) {
            boolean hidden = hand[i].isHidden();
            String rank = hand[i].getSymbol();

            top.append("+-----+ ");
            rankLine.append(String.format("|%-5s| ", rank));
            midLine.append(hidden ? "| ??? | " : "|     | ");
            rankLineFlipped.append(String.format("|%5s| ", rank));
            bottom.append("+-----+ ");
        }
        
        return top + "\n" + rankLine + "\n" + midLine + "\n" + rankLineFlipped + "\n" + bottom;
    }
}