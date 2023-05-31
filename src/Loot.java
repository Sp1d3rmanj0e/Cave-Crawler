import java.io.*;
import java.util.*;

public class Loot extends ConsoleProgram
{
    private int rarity;
    
    // Stores the loot pool of the chest in question
    private ArrayList<Utility> utilities        = new ArrayList<Utility>();
    private ArrayList<Weapon> weapons           = new ArrayList<Weapon>();
    private ArrayList<Armour> armours           = new ArrayList<Armour>();
    private ArrayList<String> utilitiesTraits    = new ArrayList<String>();
    private ArrayList<String> weaponsTraits      = new ArrayList<String>();
    private ArrayList<String> armoursTraits      = new ArrayList<String>();
    
    // Stores every trait
    private ArrayList<Trait> traitsList = new ArrayList<Trait>();
    
    public Loot(int rarity)
    {
        this.rarity = rarity;
        
        getLootData();
        getTraitData();
    }
    
    public Item lootRandom()
    {   
        Random rand = new Random();
        int randType = rand.nextInt(3); // 0 = Weapon, 1 = Armour, 2 = Utility
        
        switch(randType)
        {
            case 0: return lootWeapon();
            case 1: return lootArmour();
            case 2: return lootUtility();
            default: return new Item("Error", -1);
        }
    }
    
    public Item lootWeapon()
    {
        return addItemsWithTraits(weapons, weaponsTraits);
    }
    
    public Item lootArmour()
    {
        return addItemsWithTraits(armours, armoursTraits);
    }
    
    public Item lootUtility()
    {
        return addItemsWithTraits(utilities, utilitiesTraits);
    }
    
    public ArrayList<Item> loot() 
    {
        Random rand = new Random();
        
        ArrayList<Item> lootedItems = new ArrayList<Item>();
        
        getLootData();
        getTraitData();
        
        // Find how many of each item there will be
        int numWeapons = rand.nextInt(2); // 0-1 weapons
        int numArmours = rand.nextInt(3); // 0-2 armous
        int numUtilities = rand.nextInt(3); // 0-2 utilities
        
        // A chest must have at least one item in it
        if (numWeapons + numArmours + numUtilities == 0) numWeapons = 1;
        
        System.out.println("Looted the Chest!");
        
        // Get the loot
        if (weapons.size() >= numWeapons)
        {
            for (int w = 0; w < numWeapons; w++)
            {
                lootedItems.add(lootWeapon());
            }
        }
        
        if (armours.size() >= numArmours)
        {
            for (int a = 0; a < numArmours; a++)
            {
                lootedItems.add(lootArmour());
            }
        }
        
        if (utilities.size() > numUtilities)
        {
            for (int u = 0; u < numUtilities; u++)
            {
                lootedItems.add(lootUtility());
            }
        }
        
        //System.out.println("looted size: " + lootedItems.size());
        
        return lootedItems;
    }
    
    public Item addItemsWithTraits(ArrayList<?> itemList, ArrayList<?> traitList)
    {
        // Get item
        Item item = (Item)itemList.get(0); // Get the top item in the list
        itemList.remove(0);     // Remove said item from the list
        
        // Get traits from the pool
        for (int i = 0; i < item.getNumTraits(); i++)
        {
            // Get the name of a random trait from the pool
            String randomTrait = String.valueOf(traitList.get(0));
            traitList.remove(0);
                    
            // Get the trait object based on the name given
            Trait chosenTrait = getTraitByName(randomTrait); 
            
            item.addTrait(chosenTrait);
            //System.out.println("Chosen trait: " + chosenTrait);
        }
        
        // Push the item into the loot pool
        return item;
    }
    
    // Returns a trait object based on the name passed through
    // Checks the traitsList ArrayList for any matches
    // If not, it sends an error Trait and error message
    private Trait getTraitByName(String name)
    {
        // Loop through every logged trait and try to find a match
        for (Trait trait : traitsList)
        {
            String tName = trait.getRawName();
            
            //System.out.println("Comparing: " + tName + " with: " + name);
            if (name.equals(tName))
            {
                return trait;
            }
        }
        System.out.println("Error: No trait found");
        return new Trait("No Trait", "Error: ", "Found");
    }
    
    public int lootCoins()
    {
        Random rand = new Random();
        
        int numCoins = (rand.nextInt(3)) * (50 + rarity); // 0-2 * 50 + rarity
        System.out.println("Added: " + numCoins + " Coins!");
        
        return numCoins;
    }

    private void getTraitData()
    {
        BufferedReader objReader = null;
        try {
        String line;
        
        objReader = new BufferedReader(new FileReader("RPGame/src/TraitTable.txt"));
            
            // Variables to log a new trait
            String name = "noone";
            ArrayList<Buff> buffsList = new ArrayList<Buff>();
            String prefix = "error: no prefix";
            String suffix = "error: no suffix";
            
            while ((line = objReader.readLine()) != null) 
            {
                
                // Get the command
                char command = line.charAt(0);
                
                // Get the string without the command
                if (command != '/')
                {
                    line = line.substring(2); // removes '# ' with # being the command
                }
                
                // Do action based on command
                // = Trait name
                // > Buff [Name, Effect, Value]
                // + Prefix
                // - Suffix
                
                switch(command)
                {
                    case '=': // Trait name (And trait creation)
                        
                        // If this isn't the first trait to be made (no details to make a trait)
                        if (!name.equals("noone"))
                        {
                            Trait newTrait = new Trait(name, prefix, suffix, buffsList);
                            traitsList.add(newTrait);
                        }
                    
                        name = line;
                        buffsList = new ArrayList<Buff>(); // Get a new buffslist
                        break;
                    case '>': // Buff creation
                        // Get all features of a buff
                        String[] buffDetails = line.split(" "); // [Name, Effect, Value]
                        
                        String buffName = buffDetails[0];
                        int buffEffect  = Integer.parseInt(buffDetails[1]);
                        int buffValue   = Integer.parseInt(buffDetails[2]);
                        
                        // Create the buff with the details above
                        Buff newBuff = new Buff(buffName, buffEffect, buffValue);
                        buffsList.add(newBuff);
                        break;
                    case '+': // Prefix
                        prefix = line;
                        break;
                    case '-': // Suffix
                        suffix = line;
                        break;
                }
            }
            
        } catch (IOException e) {
        
            e.printStackTrace();
        
        } finally {
        
            try 
            {
                if (objReader != null) objReader.close();
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        }
        
        
        
        
        
    }

    private void getLootData()
    {
        BufferedReader objReader = null;
        
        try {
            String command;
            String category = ">EMPTY";
            
            String line;
            objReader = new BufferedReader(new FileReader("RPGame/src/LootTable.txt"));

            while((line = objReader.readLine()) != null)
            {
                // Get the command (if any)
                char commandToken = line.charAt(0);
                
                switch(commandToken)
                {
                    case '/': command = "Empty Token"; break;
                    case '+': command = "Rarity Token"; break;
                    case '>': command = "Category Token"; break;
                    default: command = "Item token"; break;
                }
                
                boolean doneScanning = false;
                
                switch(command)
                {
                    case "Rarity Token":// Get the rarity of the table data.
                                        // If the rarity is too high, don't
                                        // evaluate the stats
                        int tableRarity = Integer.parseInt(line.split(" ")[0]);
                        if (tableRarity > rarity)
                        {
                            doneScanning = true;   // Leave the lootTable if tableRarity 
                                            // exceeds the chest's rarity
                        }
                        
                        //System.out.println("Rarity Token Evaluated: " + tableRarity);
                        //System.out.println("Table is within rarity: " + (!doneScanning));
                    break;
                    case "Category Token": // Get the category of the following
                                           // Item tokens.  All item tokens
                                           // Evaluated after a category token
                                           // Is declared will go into that category
                        category = line;
                    break;
                    case "Item token":
                        
                        String[] item = line.split(" "); 
                        // [Weight, name, value] - Default
                        // [Weight, name] - Traits
                        // [Weight, name, value, damage, atkSpd] - Weapons
                        // [Weight, name, value, defense] - Armour
                        // [Weight, name, value] - Utility
                        
                        for (int i = 0; i < Integer.valueOf(item[0]); i++)
                        {
                            switch(category)
                            {
                                case ">WEAPON": weapons.add(new Weapon(
                                                item[1],                    // Name
                                                Integer.valueOf(item[2]),   // Value
                                                Integer.valueOf(item[3]),   // Damage
                                                Integer.valueOf(item[4]))); // Attack Speed
                                    break;
                                case ">WEAPON TRAITS": weaponsTraits.add(item[1]);  // Name
                                    break;
                                case ">ARMOUR": armours.add(new Armour(
                                                item[1],                    // Name
                                                Integer.valueOf(item[2]),   // Value
                                                Integer.valueOf(item[3]),   // Defense
                                                item[4]));                  // ArmourType
                                    break;
                                case ">ARMOUR TRAITS": armoursTraits.add(item[1]);   // Name
                                    break;
                                case ">UTILITY":  
                                    utilities.add(new Utility(
                                    item[1],                    // Name
                                    Integer.valueOf(item[2]))); // Value
                                    break;
                                case ">UTILITY TRAITS":  utilitiesTraits.add(item[1]); // Name
                                    break;
                            }
                        }
                    
                }
                
                if (doneScanning)
                {
                    break;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
            try {
                if (objReader != null)
                {
                    objReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        // Shuffle the data
        Collections.shuffle(weapons);
        Collections.shuffle(weaponsTraits);
        Collections.shuffle(armours);
        Collections.shuffle(armoursTraits);
        Collections.shuffle(utilities);
        Collections.shuffle(utilitiesTraits);
    }
    
    public String toString()
    {
        return Integer.toString(rarity);
    }
    
}