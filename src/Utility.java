public class Utility extends Item
{
    
    public Utility(String name, int baseValue)
    {
        super(name, baseValue);
        isUtility = true;
    }
    
    public String getPotionDetails()
    {
        // Print the name
        String result = super.getSpecialName();
        
        // Print the value
        result += "\n" + "|💵  " + super.getValue();
                
        // Print all the traits the item has
        for (Trait i : super.getTraits())
        {
            result += "\n|" + i.toString().replaceAll("_", " ");
        }
        
        return result;
    }
    
    public String toString()
    {
        return super.getSpecialName() + "|💵  " + super.getValue();
    }
}