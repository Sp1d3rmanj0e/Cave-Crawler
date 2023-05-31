import java.util.ArrayList; // import the ArrayList class
import java.util.HashMap;

public class Trait
{
    private String trait;
    private String[] nameModifier = new String[2]; // [Prefix, Suffix] Can leave blank with ["",""]
    private ArrayList<Buff> buffs = new ArrayList<Buff>();
    private HashMap<String, Integer> buffsEffects = new HashMap<String, Integer>();
    
    public Trait(String trait, String prefix, String suffix)
    {
        this.trait = trait;
        nameModifier[0] = prefix;
        nameModifier[1] = suffix;
    }
    
    public Trait(String trait, String prefix, String suffix, ArrayList<Buff> buffList)
    {
        this.trait = trait;
        nameModifier[0] = prefix;
        nameModifier[1] = suffix;
        
        for (int i = 0; i < buffList.size(); i++)
        {
            addBuff(buffList.get(i));
        }
        
        updateBuffsEffects();
    }
    
    public HashMap<String, Integer> getBuffsEffects()
    {
        return buffsEffects;
    }
    
    public String getRawName()
    {
        return trait;
    }
    
    public int getValue()
    {
        int value = 0;
        
        for (Buff b : buffs)
        {
            value += b.getValue();
        }
        
        //System.out.println("Trait value: $" + value);
        return value;
    }

    public void addBuff(Buff buff)
    {
        buffs.add(buff);
        updateBuffsEffects();
    }
    
    // Will look at all current buffs and put all of the effects
    // of them into a single hashmap
    private void updateBuffsEffects()
    {
        // Reset all values to 0
        for (String key: buffsEffects.keySet())
        {
            buffsEffects.replace(key, 0);
        }
        
        // Loop through every buff and add their
        // effects to the hashmap
        for (Buff buff : buffs)
        {
            // Get the buff details
            String name = buff.getBuffType();
            int impact = buff.getImpact();
            
            // Each buff has a name and an impact, 
            // we will store them accordingly
            Integer curImpact = buffsEffects.get(name);
            
            // Add the new buff data into the hashmap
            if (curImpact == null) // (No current key exists)
            {
                // Create a new hashmap value with impact
                buffsEffects.put(name, impact);
            }
            else
            {
                // Add the additional impact then replace the old
                // hashmap value with the updated one
                buffsEffects.replace(name, curImpact + impact);
            }
        }
    }
    
    public String[] getNameModifier()
    {
        return nameModifier;
    }
    
    public String toString()
    {
        String result = trait + " > ";
        for (Buff b : buffs)
        {
            result += "[" + b + "] ";
        }
        
        return result;
    }
}