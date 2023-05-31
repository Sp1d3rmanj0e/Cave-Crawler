public class Coin
{
    private int value;
    
    public Coin(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public void addValue(int addValue)
    {
        value += addValue;
    }
    
    public String toString()
    {
        return "Coins: " + value;
    }
}