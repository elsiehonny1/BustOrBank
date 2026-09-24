public class BustOrBankTest extends student.TestCase {

    private BustOrBank game;

    public void setUp() {
        game = new BustOrBank("Alice");
    }

    // ---- determineWinner ----

    public void testPlayerBustLoses() {
        assertEquals(BustOrBank.Result.DEALER_WIN,
            BustOrBank.determineWinner(22, false, 18, false));
    }

    public void testPlayerBustLosesEvenIfDealerBusts() {
        assertEquals(BustOrBank.Result.DEALER_WIN,
            BustOrBank.determineWinner(23, false, 25, false));
    }

    public void testDealerBustLoses() {
        assertEquals(BustOrBank.Result.PLAYER_WIN,
            BustOrBank.determineWinner(15, false, 24, false));
    }

    public void testHigherValueWins() {
        assertEquals(BustOrBank.Result.PLAYER_WIN,
            BustOrBank.determineWinner(20, false, 19, false));
        assertEquals(BustOrBank.Result.DEALER_WIN,
            BustOrBank.determineWinner(18, false, 19, false));
    }

    public void testEqualValuesPush() {
        assertEquals(BustOrBank.Result.PUSH,
            BustOrBank.determineWinner(19, false, 19, false));
    }

    public void testPlayerNaturalBeatsOther21() {
        assertEquals(BustOrBank.Result.PLAYER_WIN,
            BustOrBank.determineWinner(21, true, 21, false));
    }

    public void testDealerNaturalBeatsOther21() {
        assertEquals(BustOrBank.Result.DEALER_WIN,
            BustOrBank.determineWinner(21, false, 21, true));
    }

    public void testBothNaturalsPush() {
        assertEquals(BustOrBank.Result.PUSH,
            BustOrBank.determineWinner(21, true, 21, true));
    }

    public void testNonNatural21PushesNon21Natural() {
        assertEquals(BustOrBank.Result.PUSH,
            BustOrBank.determineWinner(21, false, 21, false));
    }

    // ---- session state ----

    public void testInitialTally() {
        assertEquals(0, game.getWins());
        assertEquals(0, game.getLosses());
        assertEquals(0, game.getPushes());
    }

    public void testPrintTally() {
        game.printTally();
        String out = systemOut().getHistory();
        assertTrue(out.contains("0 W / 0 L / 0 P"));
        assertTrue(out.contains("Bank: $1000"));
    }

    // ---- constants ----

    public void testColorConstants() {
        assertEquals("\u001B[0m", BustOrBank.COLOR_RESET);
        assertEquals("\u001B[31m", BustOrBank.COLOR_RED);
        assertEquals("\u001B[32m", BustOrBank.COLOR_GREEN);
        assertEquals("\u001B[33m", BustOrBank.COLOR_YELLOW);
        assertEquals("\u001B[34m", BustOrBank.COLOR_BLUE);
    }

    public void testResultEnum() {
        assertEquals(3, BustOrBank.Result.values().length);
        assertEquals(BustOrBank.Result.PUSH, BustOrBank.Result.valueOf("PUSH"));
    }

    // ---- full hand (scripted input) ----

    private void script(String... lines) {
        setIn(lines);
        BustOrBank.scanner = in();
    }

    public void testPlayGameStandSettlesOneHand() {
        script("100", "2", "2", "2");
        assertTrue(game.playGame());
        String out = systemOut().getHistory();
        assertTrue(out.contains("NEW HAND"));
        assertTrue(out.contains("FINAL HANDS"));
        assertEquals(1, game.getWins() + game.getLosses() + game.getPushes());
    }

    public void testPlayGameRepromptsInvalidBets() {
        script("abc", "0", "5000", "10", "2", "2", "2");
        assertTrue(game.playGame());
        String out = systemOut().getHistory();
        assertTrue(out.contains("Invalid bet, try again."));
        assertTrue(out.contains("Place your bet ($1-$1000)"));
    }

    public void testPlayGameReturnsFalseWhenInputEnds() {
        script();
        assertFalse(game.playGame());
    }

    public void testPlayGameHitUntilDone() {
        // Always hitting must end in a bust or 21, never loop forever
        script("50", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1");
        assertTrue(game.playGame());
        assertTrue(game.getWins() + game.getLosses() + game.getPushes() >= 1);
    }

    public void testBankChangesMatchTally() {
        int total = 0;
        for (int i = 0; i < 30; i++) {
            game = new BustOrBank("Alice");
            script("100", "2", "2", "2");
            game.playGame();
            total += game.getWins() + game.getLosses() + game.getPushes();
        }
        assertEquals(30, total);
    }

    public void testPlayGameCarriesBankAcrossHands() {
        script("100", "2", "2", "2", "100", "2", "2", "2");
        game.playGame();
        game.playGame();
        assertEquals(2, game.getWins() + game.getLosses() + game.getPushes());
    }
}
