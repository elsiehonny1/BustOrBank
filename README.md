# Bust or Bank

A command-line Blackjack game written in Java for CS 2114 (Lab 1). Play against
a dealer with real casino rules — hit, stand, double down, and split — and
optionally turn on a built-in strategy coach that tells you whether your move
matches basic strategy.

## Features

- **Full hand logic** — automatic soft/hard ace handling (`Ace = 11` unless
  that busts the hand, in which case it drops to `1`).
- **Betting and bank** — start with a bank of $1000, bet $1 up to your bank
  each hand, and carry your bank across hands. The game ends when you quit or
  run out of money.
- **Natural blackjack** — a two-card 21 ends the hand on the deal and pays
  3:2. A natural beats any other 21; if both sides have one it's a push.
- **Splitting** — split matching pairs into two hands, each with its own bet,
  and play them one at a time.
- **Doubling down** — double your bet on a two-card hand for one final card.
- **Dealer rules** — the dealer hits below 17 and stands on 17 or higher
  (including soft 17). The dealer's second card stays hidden (`???`) until
  the dealer's turn.
- **ASCII card rendering** — hands are drawn to the console as boxed cards,
  in color.
- **Session tally** — wins / losses / pushes and your bank are shown after
  every hand.
- **Strategy coach** — optionally compares each move you make against a
  basic-strategy chart (hard totals, soft totals, and pairs) and prints
  green for right, red for wrong.

## Project structure

```
BustOrBank/
├── lib/
│   └── student.jar               # JUnit 4 + student.TestCase (used by the tests)
├── src/
│   ├── main/java/
│   │   ├── BustOrBank.java       # Game loop, betting, settlement, session tally, main()
│   │   ├── Card.java             # A single playing card (rank, value, hidden state)
│   │   ├── Participant.java      # Base class: hand, drawing cards, hand value, printing
│   │   ├── Player.java           # Human player: bank, bets, hit/stand/double/split, coach
│   │   ├── Dealer.java           # Dealer rules (hit below 17), extends Participant
│   │   └── Coach.java            # Basic-strategy tables and recommendation logic
│   └── test/java/
│       └── *Test.java            # One test class per source class
└── README.md
```

### Class overview

| Class | Responsibility |
|---|---|
| `BustOrBank` | Entry point and game controller. Asks for a name and coach preference, runs each hand (bet, deal, player turn, dealer turn, payout), keeps the win/loss/push tally, and holds the shared ANSI color codes and input `Scanner`. `determineWinner` is a static method so outcomes can be tested without playing a hand. |
| `Participant` | Shared behavior for anyone holding cards: drawing a random card, computing hand value (with ace adjustment), checking for bust/blackjack, and printing the hand. |
| `Player` | Extends `Participant` with a bank, bets, and support for multiple hands (via splitting). Exposes the interactive `runAction` menu (Hit / Stand / Double Down / Split) and the coach feedback. |
| `Dealer` | Extends `Participant`; hits below 17, and the second card dealt is automatically hidden. |
| `Card` | A card with a rank symbol, numeric value, and hidden flag. `getSymbol()` returns `"?"` while hidden. |
| `Coach` | Static basic-strategy tables for hard totals, soft totals, and pairs. `determineOptimalAction` returns the recommended move for a given hand and dealer up-card. |

## Requirements

- JDK 17 recommended. The game itself runs on any recent JDK, but the test
  library in `lib/student.jar` installs a Java security manager, which newer
  JDKs (24+) no longer allow — run the tests on JDK 17–21.

## Building & running

From the project root:

```bash
mkdir -p out
javac -d out src/main/java/*.java
java -cp out BustOrBank
```

**The game uses ANSI colors, so use a terminal that supports them (VS Code's
terminal, Windows Terminal, macOS/Linux terminals). Eclipse's built-in console
doesn't render ANSI codes by default. Install the "ANSI Escape in Console"
plugin from the Eclipse Marketplace, or run from an external terminal.**

## How to play

1. Enter your name and choose whether to enable the coach.
2. Place a bet from your bank.
3. You and the dealer are each dealt two cards; one of the dealer's cards
   stays hidden. A natural blackjack on either side ends the hand right away.
4. On your turn, pick an action by number or name (unavailable options are
   shown in red):
   - **1 / hit** — take another card.
   - **2 / stand** — end your turn with your current total.
   - **3 / doubledown** — double your bet, take exactly one more card, then
     stand. Needs a two-card hand and enough bank to cover the extra bet.
   - **4 / split** — if your first two cards match, split them into two hands
     and play each separately (one split per round).
5. If you enabled the coach, you'll see whether your choice matched basic
   strategy after every move.
6. Once every hand is finished, the dealer reveals their hidden card and plays
   out their hand (unless you busted every hand). Closest to 21 without
   busting wins. Wins pay 1:1, blackjack pays 3:2, and a push returns your bet.
7. Choose whether to play another hand.

## Testing

Tests use JUnit 4 through `student.TestCase` from `lib/student.jar`, and
cover every class. Hands that depend on the random deck are checked for
invariants (for example, every hand ends with exactly one recorded result), and
user input is scripted with `setIn(...)`.

From the project root, using JDK 17–21:

```bash
mkdir -p out
javac -cp lib/student.jar -d out src/main/java/*.java src/test/java/*.java
java -cp "out:lib/student.jar" org.junit.runner.JUnitCore \
  CardTest CoachTest DealerTest ParticipantTest PlayerTest BustOrBankTest
```

On Windows, use `;` instead of `:` in the classpath. The tests also run
directly from VS Code's Java test runner.

## Basic strategy coach

`Coach.determineOptimalAction` implements a standard basic-strategy chart
indexed by the dealer's up-card:

- **Hard totals** (no ace, or an ace that must count as 1) for player totals
  5–21.
- **Soft totals** (a hand where an ace can safely count as 11) for totals
  13–21.
- **Pairs** for all ten possible pairs (2s through Aces).

Enable it at the start of the game; after every action, the game prints
whether it matched the chart's recommendation.

## Authors

Miles D'Antonio
