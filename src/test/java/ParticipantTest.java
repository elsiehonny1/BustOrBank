public class ParticipantTest extends student.TestCase {

    private Participant part;

    private static Card c(String rank, int value) {
        return new Card(rank, value, false);
    }

    private void give(Card... cards) {
        part.setActiveHand(cards);
    }

    public void setUp() {
        part = new Participant("Bob");
    }

    public void testInitialState() {
        assertEquals("Bob", part.name);
        assertEquals(0, part.getCardCount());
        assertEquals(0, part.getHand().length);
        assertEquals(0, part.getHandValue());
    }

    public void testReceiveCard() {
        part.receiveCard();
        part.receiveCard();
        assertEquals(2, part.getCardCount());
        assertEquals(2, part.getHand().length);
        assertNotNull(part.getHand()[1]);
    }

    public void testDealerSecondCardHidden() {
        Dealer dealer = new Dealer();
        dealer.receiveCard();
        dealer.receiveCard();
        assertFalse(dealer.getHand()[0].isHidden());
        assertTrue(dealer.getHand()[1].isHidden());
    }

    public void testHandValue() {
        give(c("10", 10), c("7", 7));
        assertEquals(17, part.getHandValue());
    }

    public void testAceCountsAsOneWhenNeeded() {
        give(c("A", 11), c("9", 9), c("5", 5));
        assertEquals(15, part.getHandValue());
        give(c("A", 11), c("A", 11));
        assertEquals(12, part.getHandValue());
    }

    public void testBust() {
        give(c("10", 10), c("9", 9), c("5", 5));
        assertTrue(part.isBust());
        give(c("10", 10), c("9", 9));
        assertFalse(part.isBust());
    }

    public void testBlackJack() {
        give(c("A", 11), c("K", 10));
        assertTrue(part.hasBlackJack());
        give(c("7", 7), c("7", 7), c("7", 7));
        assertFalse(part.hasBlackJack());
        give(c("10", 10), c("9", 9));
        assertFalse(part.hasBlackJack());
    }

    public void testSetActiveHand() {
        give(c("2", 2), c("3", 3), c("4", 4));
        assertEquals(3, part.getCardCount());
    }

    public void testPrintCards() {
        give(new Card("A", 11, false), new Card("5", 5, true));
        part.printCards();
        // Strip the ANSI color codes so the card shapes can be compared
        String out = systemOut().getHistory().replaceAll("\u001B\\[[0-9;]*m", "");
        assertTrue(out.contains("+-----+"));
        assertTrue(out.contains("|A    |"));
        assertTrue(out.contains("|?    |"));
        assertTrue(out.contains("| ??? |"));
    }
}
