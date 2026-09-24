public class DealerTest extends student.TestCase {

    private Dealer dealer;

    private static Card c(String rank, int value) {
        return new Card(rank, value, false);
    }

    private void give(Card... cards) {
        dealer.setActiveHand(cards);
    }

    public void setUp() {
        dealer = new Dealer();
    }

    public void testInitialState() {
        assertEquals("Dealer", dealer.name);
        assertEquals(0, dealer.getCardCount());
        assertEquals(0, dealer.getHandValue());
    }

    public void testCanHitWithEmptyHand() {
        assertTrue(dealer.canHit());
    }

    public void testCanHitBelow17() {
        give(c("10", 10), c("6", 6));
        assertTrue(dealer.canHit());
    }

    public void testCannotHitOn17OrMore() {
        give(c("10", 10), c("7", 7));
        assertFalse(dealer.canHit());
        give(c("10", 10), c("9", 9));
        assertFalse(dealer.canHit());
    }

    public void testCannotHitWhenBust() {
        give(c("10", 10), c("6", 6), c("9", 9));
        assertFalse(dealer.canHit());
    }

    public void testSoft17Stands() {
        give(c("A", 11), c("6", 6));
        assertEquals(17, dealer.getHandValue());
        assertFalse(dealer.canHit());
    }

    public void testHard17() {
        give(c("10", 10), c("7", 7));
        assertTrue(dealer.getHard17());
    }

    public void testHard17WithAceCountedAsOne() {
        give(c("A", 11), c("6", 6), c("10", 10));
        assertEquals(17, dealer.getHandValue());
        assertTrue(dealer.getHard17());
    }

    public void testSoft17IsNotHard17() {
        give(c("A", 11), c("6", 6));
        assertFalse(dealer.getHard17());
    }

    public void testNotHard17WhenTotalIsNot17() {
        give(c("10", 10), c("8", 8));
        assertFalse(dealer.getHard17());
    }

    public void testSecondCardDealtHidden() {
        dealer.receiveCard();
        dealer.receiveCard();
        dealer.receiveCard();
        assertFalse(dealer.getHand()[0].isHidden());
        assertTrue(dealer.getHand()[1].isHidden());
        assertFalse(dealer.getHand()[2].isHidden());
    }

    public void testCardsPrintedInRed() {
        give(c("K", 10));
        dealer.printCards();
        String out = systemOut().getHistory();
        assertTrue(out.contains(BustOrBank.COLOR_RED + "+-----+"));
        assertTrue(out.contains(BustOrBank.COLOR_YELLOW + "K    "));
    }

    public void testRevealHiddenCard() {
        dealer.receiveCard();
        dealer.receiveCard();
        Card.setAllHidden(dealer, false);
        assertFalse(dealer.getHand()[1].isHidden());
    }
}
