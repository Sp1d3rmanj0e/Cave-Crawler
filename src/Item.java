import java.util.ArrayList; // import the ArrayList class
import java.util.HashMap;

public class Item extends ConsoleProgram
{
    private int baseValue;
    private String name;
    private String subtext = "";
    private ArrayList<Trait> traits = new ArrayList<Trait>();
    
    public boolean isWeapon;
    public boolean isArmour;
    public boolean isUtility;
    
    // Stats that can be buffed
        // Weapon
    public int dmgBoost = 0;
    public int atkSpdBoost = 0;
        // Armour
    public int defenseBoost = 0;
        // Utility
    // None ATM
    
    private void resetBoosts()
    {
        dmgBoost = 0;
        atkSpdBoost = 0;
        defenseBoost = 0;
    }
    
    public int numTraits = 2;
    
    // Constructor
    public Item(String name, int baseValue)
    {
        this.name = name;
        this.baseValue = baseValue;
        
        isWeapon = false;
        isArmour = false;
        isUtility= false;
    }

    public String getName()
    {
        return name;
    }
    
    public int getBaseValue()
    {
        return baseValue;
    }

    // Gets the name modifiers from each trait and adds it to the name
    public String getSpecialName()
    {
           
        /*
            Traits will be sorted into 4 categories
            The first is where all traits are placed to evalutate.
            If 1 duplicate is found, both are removed from the first category
            and are added into the "Very" category
            If 2 duplicates are found > "Exceptionally"
            If 3+ duplicates are found > "Extremely"
        */
        
        // Initialize ArrayList to store prefixes and suffixes
        ArrayList<String> prefixes = new ArrayList<String>();
        ArrayList<String> suffixes = new ArrayList<String>();
        
        String prefix = "";
        String suffix = "";
        
        // Put all of the prefixes and suffixes into
        // their respective ArrayLists
        for (Trait t : traits)
        {
            String[] nameModifier = t.getNameModifier(); // [Prefix, Suffix]
            
            prefix = nameModifier[0];
            if (!prefix.equals(" ")
            && (!prefix.equals("")))
            {
                prefixes.add(prefix);
            }
            
            suffix = nameModifier[1];
            if (!suffix.equals("")
            && (!suffix.equals(" ")))
            {
                suffixes.add(suffix);
            }
        }
        
        // Reset these vars because they will 
        // be used to store the final prefix
        // and suffix of the item
        prefix = "";
        suffix = "";
        
        prefix = getPrefixesAndSuffixes(prefixes, true);
        suffix = getPrefixesAndSuffixes(suffixes, false);
        
        String result = prefix + name + " " + suffix;
        
        return result.replaceAll("_", " ");
    }
    
    private void updateBuffs()
    {
        // Reset all current boosts
        resetBoosts();
        
        // Loop through all equipped traits
        for (Trait t : traits)
        {
            HashMap<String, Integer> buffsEffects = t.getBuffsEffects();
            dmgBoost        += addIfNotNull(buffsEffects.get("Damage"));
            atkSpdBoost     += addIfNotNull(buffsEffects.get("Attack_Speed"));
            defenseBoost    += addIfNotNull(buffsEffects.get("Defense"));
        }
    }
    
    private int addIfNotNull(Object value)
    {
        if (value != null)
        {
            return (int)value;
        }
        else
        {
            return 0;
        }
    }
    
    private String getPrefixesAndSuffixes(ArrayList<String> list, boolean isPrefix)
    {
        /*
            Traits will be sorted into 4 categories
            The first is where all traits are placed to evalutate.
            If 1 duplicate is found, both are removed from the first category
            and are added into the "Very" category
            If 2 duplicates are found > "Exceptionally"
            If 3+ duplicates are found > "Extremely"
        */
        
        String result = "";
        int repeats = 0; // Stores how many times a prefix repeats
        
        
        // Add each trait following the rules mentioned
        // above
        while (list.size() > 0)
        {
            repeats = 0; // Reset repeats before evaluating next item
            
            // Get a new word to compare
            String curWord = list.get(0);
            
            // Find how many times that prefix pops up
            // in the prefixes list
            for (String selWord : list)
            {
                if (curWord.trim().equals(selWord.trim()))
                {
                    repeats++;
                }
            }
            
            // Add an "and " if it isn't the first trait to add to the weapon
            if (result.length() != 0)
            {
                result += "and ";
            }
            
            if (isPrefix)
            {
                switch(repeats)
                {
                    case 1: // Default
                        result += curWord;
                        break;
                    case 2: // Very
                        result += "Very " + curWord;
                        break;
                    case 3: // Exceptionally
                        result += "Exceptionally " + curWord;
                        break;
                    case 4: // Extremely
                        result += "Extremely " + curWord;
                        break;
                }
            }
            else // Suffix
            {
                // Separates (Ex.) Of Absorbtion to [Of, Absorbtion]
                // This is done to get the format [Of] [Intensity] [Trait]
                // Instead of [Intensity] [Of Trait]
                String[] suffixSplit = curWord.split(" ");
                result += suffixSplit[0] + " ";
                
                switch(repeats)
                {
                    case 1: // Default
                        result += suffixSplit[1];
                        break;
                    case 2: // Very
                        result += "Much " + suffixSplit[1];
                        break;
                    case 3: // Exceptionally
                        result += "Exceptional " + suffixSplit[1];
                        break;
                    case 4: // Extremely
                        result += "Extreme " + suffixSplit[1];
                        break;
                }
            }
            
            result += " "; // Add a space at the end
            
            // Remove all other occurrences of that trait from the list
            int listSize = list.size();
            for (int i = 0; i < listSize; i++)
            {
                String selWord = list.get(i); // Get a word from the list
                
                // If the word matches, remove it from the list
                if (selWord.trim().equals(curWord.trim()))
                {
                    list.remove(i);
                    
                    // Push back the search by one because
                    // an item from the list was moved
                    listSize--;
                    i--;
                    
                    //System.out.println("removed duplicate instance");
                }
            }
        }
        return result;
    }
    
    public ArrayList<Trait> getTraitsList()
    {
        return traits;
    }
    
    public int getValue()
    {
        int value = baseValue;
        
        for (Trait t : traits)
        {
            value += t.getValue();
        }
        
        //System.out.println("Total trait value: $" + value);
        
        return Math.max(1, value);
    }
    
    public int getNumTraits()
    {
        return numTraits;
    }
    
    public void addTrait(Trait trait)
    {
        traits.add(trait);
        updateBuffs();
    }
    
    public ArrayList<Trait> getTraits()
    {
        return traits;
    }
    
    public String toString()
    {
        System.out.println("==========\n");
        
        System.out.println("Name: " + getName());
        System.out.println("Value: $" + getValue());
        
        if (!subtext.equals(""))
        {
            System.out.println(subtext);
        }
        
        System.out.println("\nTraits\n==========");
        
        // Print all the traits the item has
        for (Trait i : traits)
        {
            System.out.println(i);
        }
        
        System.out.println("\n==========");
        
        return "";
    }
    
    public boolean isWeapon()
    {
        return isWeapon;
    }
    
    public boolean isArmour()
    {
        return isArmour;
    }
    
    public boolean isUtility()
    {
        return isUtility;
    }
}