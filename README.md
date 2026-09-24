# Bust or Bank

A command-line Blackjack game written in Java for CS 2114 (Lab 1). Play against
a dealer with real casino rules — hit, stand, double down, and split — and
optionally turn on a built-in strategy coach that tells you whether your move
matches basic strategy.

## Features

- **Full hand logic** — automatic soft/hard ace handling (`Ace = 11` unless
  that busts the hand, in which case it drops to `1`).
- **Splitting** — split matching pairs into separate hands, each with its own
  bet, and play them one at a time.
- **Doubling down** — double your bet on a two-card hand for one final card.
- **Hidden dealer card** — the dealer's second card is hidden (`???`) until
  it's revealed, just like at a real table.
- **ASCII card rendering** — hands are drawn to the console as simple boxed
  cards.
- **Strategy coach** — toggle a coach that compares your chosen action
  against a basic-strategy chart (hard totals, soft totals, and pairs) and
  tells you if it agrees, printed in green/red for right/wrong.
- **Bank tracking** — start with a bank of 1000 and place bets each round.

## Project structure

```
BankOrBust/
└── src/
    └── main/
        └── java/
            ├── BustOrBank.java   # Shared constants (ANSI colors, input scanner)
            ├── Card.java         # A single playing card (rank, value, hidden state)
            ├── Participant.java  # Base class: hand, drawing cards, hand value, printing
            ├── Player.java       # Human player: betting, hit/stand/double/split, coach
            ├── Dealer.java       # Dealer, extends Participant
            └── Coach.java        # Basic-strategy lookup tables and recommendation logic
```

### Class overview

| Class | Responsibility |
|---|---|
| `Participant` | Shared behavior for anyone holding cards: drawing a random card, computing hand value (with ace adjustment), checking for bust/blackjack, and printing the hand. |
| `Player` | Extends `Participant` with betting, a bank balance, and support for multiple hands (via splitting). Exposes the interactive `runAction` menu (Hit / Stand / Double Down / Split). |
| `Dealer` | Extends `Participant`; the second card dealt to the dealer is automatically hidden. |
| `Card` | An immutable-ish card with a rank symbol, numeric value, and hidden flag. `getSymbol()` returns `"?"` while hidden. |
| `Coach` | Static basic-strategy tables for hard totals, soft totals, and pairs. `determineOptimalAction` returns the recommended move for a given hand and dealer up-card. |
| `BustOrBank` | Holds shared constants (ANSI color codes for terminal output) and the shared `Scanner` used for input. |

## Requirements

- Java 8 or later (JDK)

No external dependencies or build tool are required — the project is plain
`.java` source files under `BankOrBust/src/main/java`.

## Building & running

From the `BankOrBust/src/main/java` directory:

```bash
javac *.java
java BustOrBank
```

> **Note:** `BustOrBank` currently holds shared setup (colors, `Scanner`) and
> is still being wired up with a `main` method and game loop. Once that's in
> place, running it will start an interactive game in your terminal.

## Testing

Unit tests for `Card`, `Coach`, `Participant`, and `Player` live under
`BankOrBust/src/test/java` and use JUnit 5. A `pom.xml` at `BankOrBust/`
manages the JUnit dependency and test discovery. From the `BankOrBust/`
directory:

```bash
mvn test
```

## How to play (once the game loop is wired up)

1. Place a bet from your bank.
2. You and the dealer are each dealt two cards; one of the dealer's cards
   stays hidden.
3. On your turn, choose an action each round:
   - **Hit** — take another card.
   - **Stand** — end your turn with your current total.
   - **Double Down** — double your bet, take exactly one more card, then stand.
   - **Split** — if your first two cards match, split them into two hands
     and play each separately.
4. If you enabled the **coach**, you'll see whether your choice matched
   basic strategy after every move.
5. Once you stand or bust on every hand, the dealer reveals their hidden
   card and plays out their hand. Closest to 21 without busting wins.

## Basic strategy coach

`Coach.determineOptimalAction` implements a standard basic-strategy chart
indexed by the dealer's up-card:

- **Hard totals** (no ace, or an ace that must count as 1) for player totals
  5–21.
- **Soft totals** (a hand where an ace can safely count as 11) for totals
  13–21.
- **Pairs** for all ten possible pairs (2s through Aces).

Enable it via `Player.toggleCoach()`; after every action, the game prints
whether it matched the chart's recommendation.

## Authors

Miles D'Antonio
