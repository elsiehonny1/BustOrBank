public class PlayerTest extends student.TestCase {

    private Player player;

    private static Card c(String rank, int value) {
        return new Card(rank, value, false);
    }

    /** Puts the given cards into the active hand, keeping hands[] in sync. */
    private void give(Card... cards) {
        player.getHands()[player.getActiveHandIndex()] = cards;
        player.setActiveHand(cards);
    }

    public void setUp() {
        player = new Player("Alice");
    }

    private void script(String... lines) {
        setIn(lines);
        BustOrBank.scanner = in();
    }

    public void testInitialState() {
        assertEquals("Alice", player.name);
        assertEquals(1, player.getNumOfHands());
        assertEquals(0, player.getActiveHandIndex());
        assertEquals(1000, player.getBank());
        assertEquals(0, player.getBet());
        assertFalse(player.getStood());
        assertFalse(player.isCoachEnabled());
        assertEquals(1, player.getHands().length);
    }

    public void testBank() {
        player.addBank(-200);
        assertEquals(800, player.getBank());
        player.addBank(50);
        assertEquals(850, player.getBank());
    }

    public void testBet() {
        player.placeBet(100);
        assertEquals(100, player.getBet());
    }

    public void testToggleCoach() {
        player.toggleCoach();
        assertTrue(player.isCoachEnabled());
        player.toggleCoach();
        assertFalse(player.isCoachEnabled());
    }

    public void testGetCoach() {
        give(c("10", 10), c("7", 7));
        assertEquals(Coach.STAND, player.getCoach(player.getHand(), c("6", 6)));
    }

    public void testReceiveCardUpdatesHands() {
        player.receiveCard();
        assertEquals(1, player.getHands()[0].length);
        assertSame(player.getHand(), player.getHands()[0]);
    }

    public void testHitAndStand() {
        give(c("2", 2), c("3", 3));
        player.hit();
        assertEquals(3, player.getCardCount());
        player.stand();
        assertTrue(player.getStood());
        player.hit();
        assertEquals(3, player.getCardCount());
    }

    public void testCanHit() {
        give(c("10", 10), c("9", 9), c("5", 5));
        assertFalse(player.canHit());
        give(c("10", 10), c("9", 9));
        assertTrue(player.canHit());
        assertTrue(player.canStand());
    }

    public void testDoubleDown() {
        give(c("5", 5), c("6", 6));
        player.placeBet(100);
        assertTrue(player.canDoubleDown());
        player.doubleDown();
        assertEquals(200, player.getBet());
        assertEquals(900, player.getBank());
        assertEquals(3, player.getCardCount());
        assertTrue(player.getStood());
        assertFalse(player.canDoubleDown());
    }

    public void testDoubleDownRejected() {
        give(c("5", 5), c("6", 6));
        player.placeBet(2000);
        assertFalse(player.canDoubleDown());
        player.doubleDown();
        assertEquals(2, player.getCardCount());
        assertEquals(1000, player.getBank());

        give(c("2", 2), c("3", 3), c("4", 4));
        player.placeBet(10);
        assertFalse(player.canDoubleDown());
    }

    public void testCanSplit() {
        give(c("8", 8), c("8", 8));
        player.placeBet(100);
        assertTrue(player.canSplit());
        give(c("8", 8), c("9", 9));
        assertFalse(player.canSplit());
        give(c("8", 8), c("8", 8));
        player.placeBet(5000);
        assertFalse(player.canSplit());
    }

    public void testSplit() {
        give(c("8", 8), c("8", 8));
        player.placeBet(100);
        player.split();
        assertEquals(2, player.getNumOfHands());
        assertEquals(2, player.getHands().length);
        assertEquals(900, player.getBank());
        assertEquals(100, player.getBet());
        assertEquals(2, player.getCardCount());
        assertEquals("8", player.getHand()[0].getRank());
        assertEquals(1, player.getHands()[1].length);
        assertFalse(player.canSplit());
    }

    public void testSplitRejected() {
        give(c("8", 8), c("9", 9));
        player.placeBet(100);
        player.split();
        assertEquals(1, player.getNumOfHands());
        assertEquals(1000, player.getBank());
    }

    public void testAdvanceToNextHand() {
        give(c("8", 8), c("8", 8));
        player.placeBet(100);
        player.split();
        assertTrue(player.hasNextHand());
        player.advanceToNextHand();
        assertEquals(0, player.getActiveHandIndex());

        player.stand();
        player.advanceToNextHand();
        assertEquals(1, player.getActiveHandIndex());
        assertEquals(2, player.getCardCount());
        assertEquals(100, player.getBet());
        assertFalse(player.hasNextHand());
        player.advanceToNextHand();
        assertEquals(1, player.getActiveHandIndex());
    }

    public void testBlackJackOnFirstHand() {
        give(c("A", 11), c("K", 10));
        assertTrue(player.hasBlackJack());
    }

    public void testResetPlayer() {
        give(c("8", 8), c("8", 8));
        player.placeBet(100);
        player.split();
        player.resetPlayer();
        assertEquals(1, player.getNumOfHands());
        assertEquals(0, player.getActiveHandIndex());
        assertEquals(1000, player.getBank());
        assertEquals(0, player.getBet());
        assertEquals(0, player.getCardCount());
        assertFalse(player.getStood());
        assertEquals(1, player.getHands().length);
    }

    // ---- runAction (scripted input) ----

    public void testRunActionHit() {
        give(c("2", 2), c("3", 3));
        script("1");
        player.runAction(c("6", 6));
        assertEquals(3, player.getCardCount());
    }

    public void testRunActionStand() {
        give(c("10", 10), c("7", 7));
        script("stand");
        player.runAction(c("6", 6));
        assertTrue(player.getStood());
    }

    public void testRunActionDoubleDown() {
        give(c("5", 5), c("6", 6));
        player.placeBet(100);
        script("3");
        player.runAction(c("6", 6));
        assertEquals(200, player.getBet());
        assertTrue(player.getStood());
    }

    public void testRunActionSplit() {
        give(c("8", 8), c("8", 8));
        player.placeBet(100);
        script("split");
        player.runAction(c("6", 6));
        assertEquals(2, player.getNumOfHands());
    }

    public void testRunActionRejectsInvalidThenAccepts() {
        give(c("8", 8), c("9", 9));
        // "4" (split) is unavailable, "banana" is nonsense, then a valid stand
        script("4", "banana", "2");
        player.runAction(c("6", 6));
        assertTrue(player.getStood());
        String out = systemOut().getHistory();
        assertTrue(out.contains("Invalid Response, Try Again."));
    }

    public void testCoachFeedbackCorrect() {
        give(c("10", 10), c("7", 7));
        player.toggleCoach();
        script("2");
        player.runAction(c("6", 6));
        assertTrue(systemOut().getHistory().contains("Coach: Correct!"));
    }

    public void testCoachFeedbackIncorrect() {
        give(c("10", 10), c("7", 7));
        player.toggleCoach();
        script("1");
        player.runAction(c("6", 6));
        String out = systemOut().getHistory();
        assertTrue(out.contains("Coach: Not quite."));
        assertTrue(out.contains("Stand"));
    }

    public void testCoachSilentWhenDisabled() {
        give(c("10", 10), c("7", 7));
        script("1");
        player.runAction(c("6", 6));
        assertFalse(systemOut().getHistory().contains("Coach:"));
    }
}
