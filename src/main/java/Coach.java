public class Coach {

    public static final String HIT = "Hit";
    public static final String STAND = "Stand";
    public static final String DOUBLE_DOWN = "Double Down";
    public static final String SPLIT = "Split";

    // Rows are dealer up card 2,3,4,5,6,7,8,9,10,A (columns 0-9).
    // H = Hit, S = Stand, P = Split
    // D = Double if allowed, otherwise Hit
    // d = Double if allowed, otherwise Stand

    // Hard totals, rows for player total 5 through 21.
    private static final char[][] HARD_TOTALS = {
        {'H','H','H','H','H','H','H','H','H','H'}, // 5
        {'H','H','H','H','H','H','H','H','H','H'}, // 6
        {'H','H','H','H','H','H','H','H','H','H'}, // 7
        {'H','H','H','H','H','H','H','H','H','H'}, // 8
        {'H','D','D','D','D','H','H','H','H','H'}, // 9
        {'D','D','D','D','D','D','D','D','H','H'}, // 10
        {'D','D','D','D','D','D','D','D','D','H'}, // 11
        {'H','H','S','S','S','H','H','H','H','H'}, // 12
        {'S','S','S','S','S','H','H','H','H','H'}, // 13
        {'S','S','S','S','S','H','H','H','H','H'}, // 14
        {'S','S','S','S','S','H','H','H','H','H'}, // 15
        {'S','S','S','S','S','H','H','H','H','H'}, // 16
        {'S','S','S','S','S','S','S','S','S','S'}, // 17
        {'S','S','S','S','S','S','S','S','S','S'}, // 18
        {'S','S','S','S','S','S','S','S','S','S'}, // 19
        {'S','S','S','S','S','S','S','S','S','S'}, // 20
        {'S','S','S','S','S','S','S','S','S','S'}  // 21
    };

    // Soft totals (hand contains an Ace counted as 11), rows for player total 13 through 21.
    private static final char[][] SOFT_TOTALS = {
        {'H','H','H','D','D','H','H','H','H','H'}, // A,2 (13)
        {'H','H','H','D','D','H','H','H','H','H'}, // A,3 (14)
        {'H','H','D','D','D','H','H','H','H','H'}, // A,4 (15)
        {'H','H','D','D','D','H','H','H','H','H'}, // A,5 (16)
        {'H','D','D','D','D','H','H','H','H','H'}, // A,6 (17)
        {'S','d','d','d','d','S','S','H','H','H'}, // A,7 (18)
        {'S','S','S','S','S','S','S','S','S','S'}, // A,8 (19)
        {'S','S','S','S','S','S','S','S','S','S'}, // A,9 (20)
        {'S','S','S','S','S','S','S','S','S','S'}  // A,10 (21)
    };

    // Pairs, rows for pair rank 2 through 9, 10, A.
    private static final char[][] PAIRS = {
        {'P','P','P','P','P','P','H','H','H','H'}, // 2,2
        {'P','P','P','P','P','P','H','H','H','H'}, // 3,3
        {'H','H','H','P','P','H','H','H','H','H'}, // 4,4
        {'D','D','D','D','D','D','D','D','H','H'}, // 5,5 (never split, play as hard 10)
        {'P','P','P','P','P','H','H','H','H','H'}, // 6,6
        {'P','P','P','P','P','P','H','H','H','H'}, // 7,7
        {'P','P','P','P','P','P','P','P','P','P'}, // 8,8
        {'P','P','P','P','P','S','P','P','S','S'}, // 9,9
        {'S','S','S','S','S','S','S','S','S','S'}, // 10,10 (never split)
        {'P','P','P','P','P','P','P','P','P','P'}  // A,A
    };

    public static String determineOptimalAction(Card[] playerHand, Card dealerUpCard,
            boolean canDoubleDown, boolean canSplit) {
        int dealerIndex = rankIndex(dealerUpCard.getRank());

        if (canSplit && playerHand.length == 2
                && playerHand[0].getRank().equals(playerHand[1].getRank())) {
            return pairAction(playerHand[0], dealerIndex, canDoubleDown);
        }

        int[] evaluation = evaluateHand(playerHand);
        int total = evaluation[0];
        boolean isSoft = evaluation[1] == 1;

        if (isSoft) {
            return softAction(total, dealerIndex, canDoubleDown);
        }

        return hardAction(total, dealerIndex, canDoubleDown);
    }

    private static String hardAction(int total, int dealerIndex, boolean canDoubleDown) {
        int index = total - 5;
        if (index < 0) {
            index = 0;
        }
        if (index >= HARD_TOTALS.length) {
            index = HARD_TOTALS.length - 1;
        }
        return resolveAction(HARD_TOTALS[index][dealerIndex], canDoubleDown);
    }

    private static String softAction(int total, int dealerIndex, boolean canDoubleDown) {
        int index = total - 13;
        if (index < 0) {
            index = 0;
        }
        if (index >= SOFT_TOTALS.length) {
            index = SOFT_TOTALS.length - 1;
        }
        return resolveAction(SOFT_TOTALS[index][dealerIndex], canDoubleDown);
    }

    private static String pairAction(Card pairCard, int dealerIndex, boolean canDoubleDown) {
        int rankRow = rankIndex(pairCard.getRank());
        return resolveAction(PAIRS[rankRow][dealerIndex], canDoubleDown);
    }

    private static String resolveAction(char code, boolean canDoubleDown) {
        if (code == 'P') {
            return SPLIT;
        }
        if (code == 'S') {
            return STAND;
        }
        if (code == 'D') {
            return canDoubleDown ? DOUBLE_DOWN : HIT;
        }
        if (code == 'd') {
            return canDoubleDown ? DOUBLE_DOWN : STAND;
        }
        return HIT;
    }

    private static int[] evaluateHand(Card[] hand) {
        int total = 0;
        int aceCount = 0;

        for (Card card : hand) {
            if (card.isAce()) {
                aceCount++;
            }
            total += rawValue(card);
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        int softFlag = (aceCount > 0) ? 1 : 0;
        return new int[]{total, softFlag};
    }

    private static int rawValue(Card card) {
        if (card.isAce()) {
            return 11;
        }

        String rank = card.getRank();
        if (rank.equals("J") || rank.equals("Q") || rank.equals("K")) {
            return 10;
        }

        return Integer.parseInt(rank);
    }

    private static int rankIndex(String rank) {
        if (rank.equals("A")) {
            return 9;
        }
        if (rank.equals("10") || rank.equals("J") || rank.equals("Q") || rank.equals("K")) {
            return 8;
        }
        return Integer.parseInt(rank) - 2;
    }
}