public class Dealer extends Participant {

    public Dealer(String name) {
        super("Dealer");
    }

    public boolean canHit()
    {
        return getHandValue() < 17;
    }

    public boolean getHard17()
    {
        if (getHandValue() != 17)
        {
            return false;
        }

        int total = 0;
        int aceCount = 0;

        for (int i = 0; i < getCardCount(); i++)
        {
            total += getHand()[i].getValue(this);

            if (getHand()[i].isAce())
            {
                aceCount++;
            }
        }
        while (total > 21 && aceCount > 0)
        {
            total -= 10;
            aceCount--;
        }
        return aceCount == 0;

    }


}