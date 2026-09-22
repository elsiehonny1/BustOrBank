public class Participant {

    public final String name;
    public int cardCount;
    public Card[] hand;

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

    protected void setActiveHand(Card[] newHand) {
        this.hand = newHand;
        this.cardCount = newHand.length;
    }

    public int getHandValue() {
        int total = 0;
        int aceCount = 0;

        for (int i = 0; i < cardCount; i++) {
            if (hand[i].isAce()) {
                aceCount++;
            }
            total += hand[i].getValue(this);
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    public boolean isBust() {
        return this.getHandValue() > 21;
    }

    public boolean hasBlackJack() {
        return this.getHandValue() == 21 && this.getCardCount() == 2;
    }
    
    public void printCards() {
        String top = "";
        String rankLine = "";
        String midLine = "";
        String rankLineFlipped = "";
        String bottom = "";

        for (int i = 0; i < cardCount; i++) {
            boolean hidden = hand[i].isHidden();
            String rank = hand[i].getSymbol();

            top += "+-----+ ";
            rankLine += String.format("|%-5s| ", rank);
            midLine += hidden ? "| ??? | " : "|     | ";
            rankLineFlipped += String.format("|%5s| ", rank);
            bottom += "+-----+ ";
        }

        System.out.println(top);
        System.out.println(rankLine);
        System.out.println(midLine);
        System.out.println(rankLineFlipped);
        System.out.println(bottom);
    }
}