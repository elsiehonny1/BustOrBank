public class Card {
    
    private final String symbol;
    private int value; 

    public Card(String symbol, int value) {
        this.symbol = symbol;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
    
    public String getSymbol() {
        return this.symbol; 
    }
    
}