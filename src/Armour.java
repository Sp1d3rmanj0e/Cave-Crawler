public class Armour extends Item
{
    private int baseDef;
    private String armourType;
    
    public Armour(String name, int baseValue, int baseDef, String armourType)
    {
        super(name, baseValue);
        isArmour = true;
        
        this.baseDef = baseDef;
        this.armourType = armourType;
    }
    
    public int getDefense()
    {
        return baseDef + defenseBoost;
    }
    
    public String getArmourType()
    {
        return armourType;
    }
    
    public String toString()
    {
        return super.getSpecialName() 
            + "|💵  " + super.getValue() 
            + "|🛡️  " + getDefense() + "|";
    }
}