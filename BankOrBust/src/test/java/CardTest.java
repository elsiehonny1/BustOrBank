public class CardTest extends student.TestCase {

    private Card ace;
    private Card five;

    public void setUp() {
        ace = new Card("A", 11, false);
        five = new Card("5", 5, true);
    }

    public void testConstructorAndGetters() {
        assertEquals("A", ace.getRank());
        assertEquals(11, ace.getValue(new Player("p")));
        assertFalse(ace.isHidden());
        assertTrue(five.isHidden());
    }

    public void testCopyConstructor() {
        Card copy = new Card(five);
        assertEquals("5", copy.getRank());
        assertEquals(5, copy.getValue(new Player("p")));
        assertTrue(copy.isHidden());
        copy.setIsHidden(false);
        assertTrue(five.isHidden());
    }

    public void testSetValue() {
        ace.setValue(1);
        assertEquals(1, ace.getValue(new Player("p")));
    }

    public void testHiddenSymbol() {
        assertEquals("A", ace.getSymbol());
        assertEquals("?", five.getSymbol());
        assertEquals("5", five.getRank());
        five.setIsHidden(false);
        assertEquals("5", five.getSymbol());
    }

    public void testIsAce() {
        assertTrue(ace.isAce());
        assertFalse(five.isAce());
    }

    public void testSetAllHidden() {
        Player p = new Player("p");
        p.setActiveHand(new Card[]{ace, five});
        Card.setAllHidden(p, true);
        assertTrue(ace.isHidden());
        assertTrue(five.isHidden());
        Card.setAllHidden(p, false);
        assertFalse(ace.isHidden());
        assertFalse(five.isHidden());
    }
}
