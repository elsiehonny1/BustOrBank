public class Card {
    
    private final String symbol;
    private int value; 
    private boolean isHidden;

    public Card(String symbol, int value, boolean isHidden) {
        this.symbol = symbol;
        this.value = value;
        this.isHidden = isHidden;
    }

    public Card(Card other) {
        this.symbol = other.symbol;
        this.value = other.value;
        this.isHidden = other.isHidden;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public static void setAllHidden(Participant participant, boolean isHidden) {
        for (Card card : participant.getHand()) {
            card.setIsHidden(isHidden);
        }
    }

    public int getValue(Participant participant) {
        return this.value;
    }

    public boolean isAce() {
        return this.symbol.equals("A");
    }

    public String getRank() {
        return this.symbol;
    }

    public String getSymbol() {
        if (this.isHidden) {
            return "?";
        }
        return this.symbol;
    }
    
}