public class CoachTest extends student.TestCase {

    private static Card c(String rank) {
        return new Card(rank, 0, false);
    }

    private static String act(Card[] hand, String dealer, boolean dbl, boolean split) {
        return Coach.determineOptimalAction(hand, c(dealer), dbl, split);
    }

    public void testHardTotals() {
        assertEquals(Coach.HIT, act(new Card[]{c("10"), c("2")}, "10", true, false));
        assertEquals(Coach.STAND, act(new Card[]{c("10"), c("7")}, "A", true, false));
        assertEquals(Coach.STAND, act(new Card[]{c("10"), c("3")}, "4", true, false));
        assertEquals(Coach.HIT, act(new Card[]{c("10"), c("6")}, "10", true, false));
    }

    public void testHardDouble() {
        Card[] eleven = {c("6"), c("5")};
        assertEquals(Coach.DOUBLE_DOWN, act(eleven, "6", true, false));
        assertEquals(Coach.HIT, act(eleven, "6", false, false));
    }

    public void testFaceCardsCountAsTen() {
        assertEquals(Coach.STAND, act(new Card[]{c("K"), c("Q")}, "J", true, false));
    }

    public void testSoftTotals() {
        Card[] a2 = {c("A"), c("2")};
        assertEquals(Coach.DOUBLE_DOWN, act(a2, "5", true, false));
        assertEquals(Coach.HIT, act(a2, "5", false, false));
        assertEquals(Coach.HIT, act(a2, "9", true, false));
        assertEquals(Coach.STAND, act(new Card[]{c("A"), c("9")}, "6", true, false));
    }

    public void testSoftEighteen() {
        Card[] a7 = {c("A"), c("7")};
        assertEquals(Coach.DOUBLE_DOWN, act(a7, "3", true, false));
        assertEquals(Coach.STAND, act(a7, "3", false, false));
        assertEquals(Coach.STAND, act(a7, "2", true, false));
        assertEquals(Coach.HIT, act(a7, "10", true, false));
    }

    public void testSoftHandBecomesHard() {
        // A,9,5 = hard 15
        assertEquals(Coach.STAND, act(new Card[]{c("A"), c("9"), c("5")}, "4", true, false));
    }

    public void testPairs() {
        assertEquals(Coach.SPLIT, act(new Card[]{c("8"), c("8")}, "10", true, true));
        assertEquals(Coach.SPLIT, act(new Card[]{c("A"), c("A")}, "A", true, true));
        assertEquals(Coach.STAND, act(new Card[]{c("10"), c("10")}, "5", true, true));
        assertEquals(Coach.HIT, act(new Card[]{c("2"), c("2")}, "9", true, true));
    }

    public void testPairFiveDoublesAsTen() {
        Card[] fives = {c("5"), c("5")};
        assertEquals(Coach.DOUBLE_DOWN, act(fives, "6", true, true));
        assertEquals(Coach.HIT, act(fives, "6", false, true));
    }

    public void testNineNinePair() {
        Card[] nines = {c("9"), c("9")};
        assertEquals(Coach.STAND, act(nines, "7", true, true));
        assertEquals(Coach.SPLIT, act(nines, "8", true, true));
    }

    public void testPairIgnoredWhenCannotSplit() {
        assertEquals(Coach.STAND, act(new Card[]{c("8"), c("8")}, "6", true, false));
    }

    public void testDealerAceAndFaceIndexes() {
        Card[] hand = {c("10"), c("2")};
        assertEquals(Coach.HIT, act(hand, "A", true, false));
        assertEquals(Coach.HIT, act(hand, "K", true, false));
    }

    public void testExtremeTotalsClamped() {
        assertEquals(Coach.HIT, act(new Card[]{c("2"), c("2")}, "5", true, false));
        assertEquals(Coach.STAND, act(new Card[]{c("10"), c("10"), c("5")}, "5", true, false));
    }
}
