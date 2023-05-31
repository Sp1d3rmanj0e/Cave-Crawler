public class Buff
{
    private int impact;
    private String buffType;
    private int value;
    
    public Buff(String buffType, int impact, int value)
    {
        this.impact = impact;
        this.buffType = buffType;
        this.value = value;
    }
    
    public String getBuffType()
    {
        return buffType;
    }
    
    public int getImpact()
    {
        return impact;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public String toString()
    {
        if (impact >= 0)
        {
            return buffType + ": +" + impact;
        }
        
        return buffType + ": " + impact;
    }
}