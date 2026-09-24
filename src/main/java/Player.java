public class Player extends Participant {
    // Constructor
    private int numOfHands;
    private int activeHand;
    private boolean hasSplit;
    private boolean[] stood;
    private int bank;
    private int[] bets;
    private Card[][] hands;
    private boolean coachEnabled;

    public Player(String name) {
        super(name);
        this.numOfHands = 1;
        this.activeHand = 0;
        this.hasSplit = false;
        this.stood = new boolean[]{false};
        this.bank = 1000;
        this.bets = new int[]{0};
        this.hands = new Card[][]{this.hand};
        this.coachEnabled = false;
    }

    public int getNumOfHands() {
        return this.numOfHands;
    }

    public int getActiveHandIndex() {
        return this.activeHand;
    }

    public Card[][] getHands() {
        return this.hands;
    }

    public boolean getStood() {
        return this.stood[activeHand];
    }

    public int getBet() {
        return this.bets[activeHand];
    }

    public void placeBet(int value) {
        this.bets[activeHand] = value;
    }

    public int getBank() {
        return this.bank;
    }

    public void addBank(int value) {
        this.bank += value;
    }

    public boolean isCoachEnabled() {
        return this.coachEnabled;
    }

    public void toggleCoach() {
        this.coachEnabled = !this.coachEnabled;
    }

    public String getCoach(Card[] playerHand, Card dealerUpCard) {
        return Coach.determineOptimalAction(playerHand, dealerUpCard, this.canDoubleDown(), this.canSplit());
    }

    private void giveCoachFeedback(Card dealerUpCard, String chosenAction) {
        if (!this.coachEnabled) {
            return;
        }

        String recommended = this.getCoach(this.getHand(), dealerUpCard);
        boolean correct = recommended.equals(chosenAction);
        String color = correct ? BustOrBank.COLOR_GREEN : BustOrBank.COLOR_RED;
        String message = correct
            ? "Coach: Correct! " + chosenAction + " is the right move here."
            : "Coach: Not quite. Basic strategy says you should " + recommended + " here.";

        System.out.println(color + message + BustOrBank.COLOR_RESET);
    }

    public void runAction(Card dealerUpCard) {
        System.out.println(BustOrBank.COLOR_YELLOW + "What would you like to do, " + this.name + "?" + BustOrBank.COLOR_RESET);
        String response;
        boolean responseValid = false;

        while (!responseValid) {
            printOption("1. Hit", canHit());
            printOption("2. Stand", canStand());
            printOption("3. Double Down", canDoubleDown());
            printOption("4. Split", canSplit());
            System.out.print("> ");

            response = BustOrBank.scanner.nextLine();

            if ((response.equals("1") || response.toLowerCase().equals("hit"))
                && canHit()) {
                    giveCoachFeedback(dealerUpCard, Coach.HIT);
                    this.hit();
                    responseValid = true;
            }
            else if ((response.equals("2") || response.toLowerCase().equals("stand"))
                && canStand()) {
                    giveCoachFeedback(dealerUpCard, Coach.STAND);
                    this.stand();
                    responseValid = true;
            }
            else if ((response.equals("3") || response.toLowerCase().equals("doubledown"))
                && canDoubleDown()) {
                    giveCoachFeedback(dealerUpCard, Coach.DOUBLE_DOWN);
                    this.doubleDown();
                    responseValid = true;
            }
            else if ((response.equals("4") || response.toLowerCase().equals("split"))
                && canSplit()) {
                    giveCoachFeedback(dealerUpCard, Coach.SPLIT);
                    this.split();
                    responseValid = true;
            }
            else {
                System.out.println(BustOrBank.COLOR_ORANGE + "Invalid Response, Try Again." + BustOrBank.COLOR_RESET);
            }
        }
    }

    private void printOption(String label, boolean available) {
        String color = available ? BustOrBank.COLOR_GREEN : BustOrBank.COLOR_RED;
        System.out.println(color + label + BustOrBank.COLOR_RESET);
    }

    public void resetPlayer() {
        this.numOfHands = 1;
        this.activeHand = 0;
        this.hasSplit = false;
        this.stood = new boolean[]{false};
        this.bank = 1000;
        this.bets = new int[]{0};
        this.setActiveHand(new Card[0]);
        this.hands = new Card[][]{this.hand};
    }

    @Override
    public void receiveCard() {
        super.receiveCard();
        this.hands[activeHand] = this.hand;
    }

    public void hit() {
        if (!canHit()) {
            return;
        }

        this.receiveCard();
    }

    public void stand() {
        this.stood[activeHand] = true;
    }

    public void doubleDown() {
        if (!canDoubleDown()) {
            return;
        }

        this.addBank(-1 * this.getBet());
        this.placeBet(this.getBet() * 2);
        this.hit();
        this.stood[activeHand] = true;
    }

    public void split() {
        if (!canSplit()) {
            return;
        }

        Card firstCard = this.hands[activeHand][0];
        Card secondCard = this.hands[activeHand][1];
        int currentBet = this.getBet();

        Card[][] newHands = new Card[hands.length + 1][];
        boolean[] newStood = new boolean[stood.length + 1];
        int[] newBets = new int[bets.length + 1];
        System.arraycopy(hands, 0, newHands, 0, hands.length);
        System.arraycopy(stood, 0, newStood, 0, stood.length);
        System.arraycopy(bets, 0, newBets, 0, bets.length);

        newHands[activeHand] = new Card[]{firstCard};
        newHands[hands.length] = new Card[]{secondCard};
        newBets[activeHand] = currentBet;
        newBets[hands.length] = currentBet;

        this.hands = newHands;
        this.stood = newStood;
        this.bets = newBets;
        this.numOfHands++;
        this.hasSplit = true;

        this.addBank(-1 * currentBet);
        this.setActiveHand(this.hands[activeHand]);
        this.hit();
    }

    public boolean hasNextHand() {
        return this.activeHand < this.hands.length - 1;
    }

    public void advanceToNextHand() {
        if (!hasNextHand() || this.canHit()) {
            return;
        }

        this.activeHand++;
        this.setActiveHand(this.hands[activeHand]);
        this.hit();
    }

    public boolean canHit() {
        return (!this.isBust() && !this.getStood());
    }

    public boolean canStand() {
        return true;
    }

    public boolean canDoubleDown() {
        return (this.getCardCount() == 2) &&
            !getStood() &&
            bank >= getBet();
    }

    @Override
    public boolean hasBlackJack() {
        return this.activeHand == 0 && super.hasBlackJack();
    }

    public boolean canSplit() {
        return !this.hasSplit &&
            !this.getStood() &&
            this.getCardCount() == 2 &&
            this.getHand()[0].getRank().equals(this.getHand()[1].getRank()) &&
            this.bank >= this.getBet();
    }
}