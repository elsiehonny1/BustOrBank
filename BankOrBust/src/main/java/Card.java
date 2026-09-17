public class Card {
    
    private final String symbol;
    private int value; 

    public Card(String symbol, int value) {
        this.symbol = symbol;
        this.value = value;
    }
    
    // Strictly Used for Ace 
    public void setValue(int value) {
        if (this.getSymbol().equals("A")) {
            this.value = value;
        }
    }

    public int getValue() {
        return this.value;
    }
    
    public String getSymbol() {
        return this.symbol; 
    }
    
}