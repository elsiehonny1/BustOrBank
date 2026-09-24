import java.util.ArrayList;
import java.util.Scanner;

/**
 * BustOrBank runs and controls the overall blackjack game. It creates the
 * Player and Dealer, controls whose turn it is, takes bets, settles each
 * hand, and keeps a running session tally.
 */
public class BustOrBank {
    /** Possible outcomes of a hand. */
    public enum Result {
        PLAYER_WIN, DEALER_WIN, PUSH
    }

    /** Shared input source. Not final so tests can substitute scripted input. */
    public static Scanner scanner = new Scanner(System.in);

    public static final String COLOR_RESET = "\u001B[0m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_ORANGE = "\u001B[38;5;214m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_BLUE = "\u001B[34m";
    public static final String COLOR_PURPLE = "\u001B[35m";
    public static final String COLOR_PINK = "\u001B[38;5;206m";
    public static final String COLOR_BROWN = "\u001B[38;5;94m";

    private static final int STARTING_BANK = 1000;

    private final Player player;
    private Dealer dealer;

    // One entry per player hand, recorded as each hand finishes
    private final ArrayList<Integer> finishedValues;
    private final ArrayList<Integer> finishedBets;
    private boolean playerNatural;

    private int wins;
    private int losses;
    private int pushes;

    /**
     * Creates a new game session.
     *
     * @param playerName the name shown for the player
     */
    public BustOrBank(String playerName) {
        this.player = new Player(playerName);
        this.dealer = new Dealer();
        this.finishedValues = new ArrayList<>();
        this.finishedBets = new ArrayList<>();
    }

    /**
     * Entry point. Plays hands until the player quits or runs out of money.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        banner("WELCOME TO BUST OR BANK", COLOR_PURPLE);
        BustOrBank game = new BustOrBank(askName());
        System.out.println(COLOR_GREEN + "\nGood luck, " + game.player.name + "!"
            + COLOR_RESET);
        game.askCoach();

        boolean keepPlaying = true;
        while (keepPlaying) {
            keepPlaying = game.playGame();
            game.printTally();
            if (keepPlaying && game.player.getBank() <= 0) {
                System.out.println(COLOR_RED + game.player.name + ", you're out of money!" + COLOR_RESET);
                keepPlaying = false;
            }
            else if (keepPlaying) {
                keepPlaying = game.askPlayAgain();
            }
        }
        System.out.println("Thanks for playing, " + game.player.name
            + "! You leave with $" + game.player.getBank() + ".");
        scanner.close();
    }

    /**
     * Plays one complete hand: bet, deal, player turn, dealer turn, payout.
     *
     * @return false if input ended before the hand could be played
     */
    public boolean playGame() {
        resetTable();

        int bet = askBet();
        if (bet < 0) {
            return false;
        }
        player.placeBet(bet);
        player.addBank(-bet);

        // Deal in casino order: player, dealer, player, dealer.
        // Participant hides the dealer's second card automatically.
        player.receiveCard();
        dealer.receiveCard();
        player.receiveCard();
        dealer.receiveCard();

        banner("NEW HAND", COLOR_PURPLE);
        showTable();

        // A natural blackjack on either side ends the hand immediately
        if (player.hasBlackJack() || dealer.hasBlackJack()) {
            System.out.println(COLOR_PINK + "Natural blackjack on the deal!" + COLOR_RESET);
            playerNatural = player.hasBlackJack();
            recordFinishedHand();
        }
        else {
            playPlayerTurn();
            if (anyHandAlive()) {
                playDealerTurn();
            }
        }

        Card.setAllHidden(dealer, false);
        banner("FINAL HANDS", COLOR_BLUE);
        System.out.println(COLOR_RED + "Dealer (" + dealer.getHandValue() + ")" + COLOR_RESET);
        dealer.printCards();
        System.out.println();
        System.out.println(COLOR_BLUE + player.name + " (" + player.getHandValue() + ")" + COLOR_RESET);
        player.printCards();
        System.out.println();

        settleHands();
        return true;
    }

    /**
     * Runs the player's turn across every hand (a split creates more than one).
     * Each hand ends on stand, double down, bust, or 21.
     */
    public void playPlayerTurn() {
        Card dealerUpCard = dealer.getHand()[0];
        boolean done = false;
        boolean firstLook = true;

        while (!done) {
            // The opening table was already shown when the hand was dealt
            if (!firstLook) {
                showTable();
            }
            firstLook = false;

            if (player.getHandValue() == 21) {
                System.out.println(COLOR_GREEN + "21! Standing automatically." + COLOR_RESET);
                player.stand();
            }
            else {
                player.runAction(dealerUpCard);
            }

            if (!player.canHit()) {
                if (player.isBust()) {
                    System.out.println(COLOR_RED + "Bust! " + player.name + " went over 21 with "
                        + player.getHandValue() + "." + COLOR_RESET);
                    player.printCards();
                }
                recordFinishedHand();

                if (player.hasNextHand()) {
                    banner("NEXT HAND", COLOR_PURPLE);
                    player.advanceToNextHand();
                }
                else {
                    done = true;
                }
            }
        }
    }

    /**
     * Runs the dealer's turn: hit below 17, stand on 17 or higher.
     */
    public void playDealerTurn() {
        Card.setAllHidden(dealer, false);
        banner("DEALER'S TURN", COLOR_RED);
        System.out.println(COLOR_RED + "Dealer reveals (" + dealer.getHandValue() + ")" + COLOR_RESET);
        dealer.printCards();

        while (dealer.canHit()) {
            dealer.receiveCard();
            System.out.println(COLOR_RED + "\nDealer draws -> " + dealer.getHandValue() + COLOR_RESET);
            dealer.printCards();
        }

        if (dealer.isBust()) {
            System.out.println(COLOR_GREEN + "\nDealer busts with " + dealer.getHandValue() + "!" + COLOR_RESET);
        }
        else {
            System.out.println(COLOR_ORANGE + "\nDealer stands on " + dealer.getHandValue() + "." + COLOR_RESET);
        }
    }

    /**
     * Determines the outcome of a finished hand. Static and takes plain values
     * so it can be unit tested without running the game loop.
     *
     * @param playerValue   the player's final hand value
     * @param playerNatural true if the player has a natural blackjack
     * @param dealerValue   the dealer's final hand value
     * @param dealerNatural true if the dealer has a natural blackjack
     * @return PLAYER_WIN, DEALER_WIN, or PUSH
     */
    public static Result determineWinner(int playerValue, boolean playerNatural,
            int dealerValue, boolean dealerNatural) {
        // Player bust loses even if the dealer would also bust
        if (playerValue > 21) {
            return Result.DEALER_WIN;
        }
        if (dealerValue > 21) {
            return Result.PLAYER_WIN;
        }

        // A natural blackjack beats any other 21
        if (playerNatural && dealerNatural) {
            return Result.PUSH;
        }
        if (playerNatural) {
            return Result.PLAYER_WIN;
        }
        if (dealerNatural) {
            return Result.DEALER_WIN;
        }

        if (playerValue > dealerValue) {
            return Result.PLAYER_WIN;
        }
        if (playerValue < dealerValue) {
            return Result.DEALER_WIN;
        }
        return Result.PUSH;
    }

    /**
     * Prints the running win/loss/push tally and the player's bank.
     */
    public void printTally() {
        System.out.println(COLOR_PURPLE + "Session: " + wins + " W / " + losses + " L / "
            + pushes + " P" + COLOR_RESET + "  |  " + COLOR_GREEN + "Bank: $"
            + player.getBank() + COLOR_RESET);
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getPushes() {
        return pushes;
    }

    /**
     * Clears the previous hand. resetPlayer() also resets the bank, so the
     * bank is restored afterward to keep it across hands.
     */
    private void resetTable() {
        int bank = player.getBank();
        player.resetPlayer();
        player.addBank(bank - STARTING_BANK);

        dealer = new Dealer();
        finishedValues.clear();
        finishedBets.clear();
        playerNatural = false;
    }

    /**
     * Remembers the active hand's value and bet so it can be settled once the
     * dealer is done.
     */
    private void recordFinishedHand() {
        finishedValues.add(player.getHandValue());
        finishedBets.add(player.getBet());
    }

    private boolean anyHandAlive() {
        for (int value : finishedValues) {
            if (value <= 21) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pays out each finished hand and updates the session tally.
     */
    private void settleHands() {
        boolean dealerNatural = dealer.hasBlackJack();

        for (int i = 0; i < finishedValues.size(); i++) {
            int bet = finishedBets.get(i);
            boolean natural = playerNatural && finishedValues.size() == 1;
            Result result = determineWinner(finishedValues.get(i), natural,
                dealer.getHandValue(), dealerNatural);

            String label = finishedValues.size() > 1 ? "Hand " + (i + 1) + ": " : "";
            switch (result) {
                case PLAYER_WIN:
                    wins++;
                    int payout = natural ? bet + bet * 3 / 2 : bet * 2;
                    player.addBank(payout);
                    System.out.println(COLOR_GREEN + label
                        + (natural ? "Blackjack! " : "") + player.name + " beat the house! +$"
                        + (payout - bet) + COLOR_RESET);
                    break;
                case DEALER_WIN:
                    losses++;
                    System.out.println(COLOR_RED + label
                        + "The house wins this one. -$" + bet + COLOR_RESET);
                    break;
                default:
                    pushes++;
                    player.addBank(bet);
                    System.out.println(COLOR_YELLOW + label
                        + "Push. It's a tie." + COLOR_RESET);
                    break;
            }
        }
    }

    /**
     * Prints a colored section banner with blank lines around it.
     */
    private static void banner(String title, String color) {
        String line = "==============================";
        System.out.println();
        System.out.println(color + line);
        System.out.println("  " + title);
        System.out.println(line + COLOR_RESET);
    }

    /**
     * Prints the dealer's visible cards and the player's active hand.
     */
    private void showTable() {
        System.out.println();
        System.out.println(COLOR_RED + "Dealer" + COLOR_RESET);
        dealer.printCards();
        System.out.println();
        System.out.println(COLOR_BLUE + player.name + "'s hand (" + player.getHandValue() + ")" + COLOR_RESET);
        player.printCards();
        System.out.println();
    }

    /**
     * Asks for the player's name, reprompting on empty input.
     *
     * @return the name, or "Player" if input ended
     */
    private static String askName() {
        while (true) {
            System.out.print("What's your name? ");
            if (!scanner.hasNextLine()) {
                return "Player";
            }
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                return name;
            }
            System.out.println(COLOR_ORANGE + "Please enter a name." + COLOR_RESET);
        }
    }

    /**
     * Asks whether to turn on the coach, reprompting on bad input.
     */
    private void askCoach() {
        if (askYesNo("Enable the coach? (y/n): ")) {
            player.toggleCoach();
        }
    }

    /**
     * Asks whether to play another hand.
     *
     * @return true to play again
     */
    private boolean askPlayAgain() {
        return askYesNo("Play another hand? (y/n): ");
    }

    /**
     * Asks a yes/no question, reprompting on bad or empty input.
     *
     * @return true for yes, false for no or closed input
     */
    private boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return false; // input stream closed
            }
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println(COLOR_ORANGE + "Please enter y or n." + COLOR_RESET);
        }
    }

    /**
     * Asks for a bet between 1 and the player's bank.
     *
     * @return the bet, or -1 if input ended
     */
    private int askBet() {
        while (true) {
            System.out.print("Place your bet ($1-$" + player.getBank() + "): ");
            if (!scanner.hasNextLine()) {
                return -1;
            }
            try {
                int bet = Integer.parseInt(scanner.nextLine().trim());
                if (bet >= 1 && bet <= player.getBank()) {
                    return bet;
                }
            }
            catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println(COLOR_ORANGE + "Invalid bet, try again." + COLOR_RESET);
        }
    }
}
