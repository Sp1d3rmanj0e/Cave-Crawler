public class Weapon extends Item
{
    private int baseDmg;
    private int baseAtkSpd;
    private int numTraits = 3;
    
    public Weapon(String name, int baseValue, int baseDmg, int baseAtkSpd)
    {
        super(name, baseValue);
        isWeapon = true;
        
        this.baseDmg = baseDmg;
        this.baseAtkSpd = baseAtkSpd;
    }
    
    public int getDamage()
    {
        return baseDmg + dmgBoost;
    }
    
    public int getAttackSpeed()
    {
        return baseAtkSpd + atkSpdBoost;
    }
    
    public String toString()
    {
        return super.getSpecialName() 
        + "|💵  " + super.getValue() 
        + "|⚔️  " + getDamage() 
        + "|⚡️  " + getAttackSpeed() + "|";
    }
}