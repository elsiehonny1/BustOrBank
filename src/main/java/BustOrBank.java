// Participant class intended set-up

// Player player = new Player("Player");
// Dealer dealer = new Dealer("Dealer");

// player.receiveCard();
// dealer.receiveCard();

// player.receiveCard();
// dealer.receiveCard();

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * BustOrBank runs and controls the overall blackjack game. It owns the deck,
 * creates the Player and Dealer for each hand, controls whose turn it is,
 * determines the result, and keeps a running session tally.
 */
public class BustOrBank {
    /** Possible outcomes of a hand. Replaces the boolean so a push is representable. */
    public enum Result {
        PLAYER_WIN, DEALER_WIN, PUSH
    }

    /** Reshuffle a fresh 52-card deck when fewer than this many cards remain. */
    private static final int RESHUFFLE_THRESHOLD = 15;

    private final Scanner scanner;
    private final ArrayList<Integer> deck;
    private Player player;
    private Dealer dealer;

    private int wins;
    private int losses;
    private int pushes;
    
    public static final String COLOR_RESET = "\u001B[0m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_ORANGE = "\u001B[38;5;214m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_BLUE = "\u001B[34m";
    public static final String COLOR_PURPLE = "\u001B[35m";
    public static final String COLOR_PINK = "\u001B[38;5;206m";
    public static final String COLOR_BROWN = "\u001B[38;5;94m";


    /**
     * Creates a new game session with a freshly shuffled deck.
     *
     * @param scanner the single Scanner reading System.in, shared with Player
     */
    public BustOrBank(Scanner scanner) {
        this.scanner = scanner;
        this.deck = new ArrayList<>();
        buildAndShuffleDeck();
    }

    /**
     * Entry point. Plays hands until the player chooses to quit.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BustOrBank game = new BustOrBank(scanner);

        System.out.println("Welcome to Bust or Bank!");
        boolean keepPlaying = true;
        while (keepPlaying) {
            game.playGame();
            game.printTally();
            keepPlaying = game.askPlayAgain();
        }
        System.out.println("Thanks for playing!");
        scanner.close();
    }

    /**
     * Plays one complete hand: deal, player turn, dealer turn, result.
     */
    public void playGame() {
        if (deck.size() < RESHUFFLE_THRESHOLD) {
            System.out.println("Reshuffling the deck...");
            buildAndShuffleDeck();
        }

        // Fresh participants each hand, so no hand-reset method is needed
        player = new Player(scanner);
        dealer = new Dealer();

        // Deal in casino order: player, dealer, player, dealer
        player.receiveCard(drawCard());
        dealer.receiveCard(drawCard());
        player.receiveCard(drawCard());
        dealer.receiveCard(drawCard());

        System.out.println("\n=== New Hand ===");
        showTable(true);

        // A natural blackjack on either side ends the hand immediately
        if (player.hasBlackJack() || dealer.hasBlackJack()) {
            System.out.println("Natural blackjack on the deal!");
        }
        else {
            playPlayerTurn();
            if (!player.isBust()) {
                playDealerTurn();
            }
        }

        System.out.println("\n--- Final Hands ----");
        showTable(false);

        Result result = determineWinner(player, dealer);
        recordResult(result);
    }

    /**
     * Runs the player's turn. The loop exits on stand, bust, or 21, so the
     * player can never act after busting or standing.
     */
    public void playPlayerTurn() {
        while (!player.isBust() && player.getHandValue() < 21) {
            String action = player.getAction(); // validated: "hit" or "stand"

            if (action.equals("hit")) {
                int card = drawCard();
                player.receiveCard(card);
                System.out.println("You drew " + cardName(card) + ".");
                showTable(true);
            }
            else {
                System.out.println("You stand on " + player.getHandValue() + ".");
                return;
            }
        }

        if (player.isBust()) {
            System.out.println("Bust! You went over 21 with "
                + player.getHandValue() + ".");
        }
        else {
            System.out.println("21! Standing automatically.");
        }
    }

    /**
     * Runs the dealer's turn: hit below 17, stand on 17 or higher.
     */
    public void playDealerTurn() {
        System.out.println("\nDealer reveals: " + dealer.printCards(true)
            + " (" + dealer.getHandValue() + ")");

        while (dealer.canHit()) {
            int card = drawCard();
            dealer.receiveCard(card);
            System.out.println("Dealer draws " + cardName(card)
                + " -> " + dealer.getHandValue());
        }

        if (dealer.isBust()) {
            System.out.println("Dealer busts with " + dealer.getHandValue() + "!");
        }
        else {
            System.out.println("Dealer stands on " + dealer.getHandValue() + ".");
        }
    }

    /**
     * Determines the outcome of a finished hand. Static and parameterized so
     * it can be unit tested without running the game loop.
     *
     * @param player the player's finished hand
     * @param dealer the dealer's finished hand
     * @return PLAYER_WIN, DEALER_WIN, or PUSH
     */
    public static Result determineWinner(Participant player, Participant dealer) {
        // Player bust loses even if the dealer would also bust
        if (player.isBust()) {
            return Result.DEALER_WIN;
        }
        if (dealer.isBust()) {
            return Result.PLAYER_WIN;
        }

        // A natural blackjack beats any other 21
        boolean playerNatural = player.hasBlackJack();
        boolean dealerNatural = dealer.hasBlackJack();
        if (playerNatural && dealerNatural) {
            return Result.PUSH;
        }
        if (playerNatural) {
            return Result.PLAYER_WIN;
        }
        if (dealerNatural) {
            return Result.DEALER_WIN;
        }

        int playerValue = player.getHandValue();
        int dealerValue = dealer.getHandValue();
        if (playerValue > dealerValue) {
            return Result.PLAYER_WIN;
        }
        if (playerValue < dealerValue) {
            return Result.DEALER_WIN;
        }
        return Result.PUSH;
    }

    /**
     * Updates the session tally and announces the result.
     */
    private void recordResult(Result result) {
        switch (result) {
            case PLAYER_WIN:
                wins++;
                System.out.println(player.hasBlackJack()
                    ? "Blackjack! You beat the house!"
                    : "You beat the house!");
                break;
            case DEALER_WIN:
                losses++;
                System.out.println("The house wins this one.");
                break;
            default:
                pushes++;
                System.out.println("Push. It's a tie.");
                break;
        }
    }

    /**
     * Prints both hands.
     *
     * @param hideHoleCard true while the player is still acting
     */
    private void showTable(boolean hideHoleCard) {
        System.out.println("Your hand:   " + player.printCards(true)
            + " (" + player.getHandValue() + ")");
        if (hideHoleCard) {
            System.out.println("Dealer shows: " + dealer.printCards(false));
        }
        else {
            System.out.println("Dealer hand: " + dealer.printCards(true)
                + " (" + dealer.getHandValue() + ")");
        }
    }

    /**
     * Prints the running win/loss/push tally.
     */
    public void printTally() {
        System.out.println("Session: " + wins + " W / " + losses + " L / "
            + pushes + " P");
    }

    /**
     * Asks whether to play another hand, reprompting on bad or empty input.
     *
     * @return true to play again
     */
    private boolean askPlayAgain() {
        while (true) {
            System.out.print("Play another hand? (y/n): ");
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
            System.out.println("Please enter y or n.");
        }
    }

    /**
     * Builds a standard 52-card deck as blackjack values and shuffles it.
     * 2-9 at face value, 10/J/Q/K as 10 (16 cards), Ace as 11.
     */
    private void buildAndShuffleDeck() {
        deck.clear();
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 2; rank <= 9; rank++) {
                deck.add(rank);
            }
            for (int i = 0; i < 4; i++) {
                deck.add(10); // 10, J, Q, K
            }
            deck.add(11); // Ace
        }
        Collections.shuffle(deck);
    }

    /**
     * Removes and returns the top card of the deck.
     */
    private int drawCard() {
        return deck.remove(deck.size() - 1);
    }

    /**
     * Readable name for a card value.
     */
    private static String cardName(int card) {
        return card == 11 ? "an Ace" : "a " + card;
    }

    // Getters for testing the tally
    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getPushes() {
        return pushes;
    }
}
