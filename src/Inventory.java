import java.util.ArrayList;

public class Inventory extends ConsoleProgram
{
    // Player's hotbar
    private Weapon equippedWeapon; // The weapon the player attacks with
    private Armour[] equippedArmour = new Armour[5];
    
    private int playerCoins = 0;
    
    private ArrayList<Item> items;
    
    public Inventory(ArrayList<Item> items)
    {
        // Default items in the new inventory
        this.items = items;
    }
    
    public Inventory() 
    {
        this.items = new ArrayList<Item>();
        
        // Give the player a default weapon
        Weapon defaultWeapon = new Weapon("Hands", 0, 1, 1);
        addItem(defaultWeapon);
        equippedWeapon = defaultWeapon;
    }
    
    public void equipWeapon()
    {
        printEquippedWeapon();
        System.out.println("Options:");
        System.out.println("=======================================");
        int number = 1; // Used to label each weapon
        
        ArrayList<Weapon> weaponList = new ArrayList<Weapon>();
        
        for (Item i : items)
        {
            if (i.isWeapon)
            {
                System.out.println("(" + number + ") " + i);
                weaponList.add((Weapon)i);
                number++;
            }
        }
        
        // Get action from player
        int answer = readInt("\nWhich weapon would you like to equip? -1 to cancel. ");
        
        try {
            equippedWeapon = weaponList.get(answer-1);
            System.out.println("Equip Successful!");
            System.out.println("Equipped [" + equippedWeapon.getName() + "]");
            
        } catch (Exception e) {
            System.out.println("Cancelled");
        }
        
        readLine("Press enter to continue...");
    }
    
    private void printEquippedWeapon()
    {
        System.out.println("=========== Equipped Weapon ===========");
        System.out.println(equippedWeapon);
        System.out.println("=======================================\n");
    }
    
    public void seeUtility()
    {
        System.out.println("========== Utility inventory ==========");
        
        for (Item i : items)
        {
            if (i.isUtility)
            {
                Utility u = (Utility)i;
                System.out.println(u.getPotionDetails());
            }
        }
        
        System.out.println("=======================================\n");
        readLine("Press enter to continue...");
    }
    
    public int getPlayerDefense()
    {
        int totalArmour = 0;
        
        for (int i = 0; i < 5; i++)
        {
            if (equippedArmour[i] != null)
            {
                totalArmour += equippedArmour[i].getDefense();
            }
        }
        
        return totalArmour;
    }
    
    public void equipArmour()
    {
        
        // Filter and store only Armour objects
        ArrayList<Armour> armourList = new ArrayList<Armour>();
        for (Item i : items)
        {
            if (i.isArmour)
            {
                armourList.add((Armour)i);
            }
        }
        
        // Sort armours by armour type
        ArrayList<Armour> hats = new ArrayList<Armour>();
        ArrayList<Armour> gloves = new ArrayList<Armour>();
        ArrayList<Armour> shirts = new ArrayList<Armour>();
        ArrayList<Armour> pants = new ArrayList<Armour>();
        ArrayList<Armour> boots = new ArrayList<Armour>();
        for (Armour a : armourList)
        {
            String armourType = a.getArmourType();
            
            switch(armourType)
            {
                case "HAT":
                    hats.add(a);
                    break;
                case "GLOVES":
                    gloves.add(a);
                    break;
                case "SHIRT":
                    shirts.add(a);
                    break;
                case "PANTS":
                    pants.add(a);
                    break;
                case "BOOTS":
                    boots.add(a);
                    break;
            }
        }
        
        String selArmourCategory = "none";
        char responseChar = '0'; // Folder selection and leaving
        int responseInt = 0; // Item selection
        ArrayList<Armour> selFolder = new ArrayList<Armour>();
        
        // Search and equip armours
        while(true)
        {
            printLines(30);
               
            // Print currently equipped armour
            printEquippedArmour();
            
            // Choose which armour piece to equip
            System.out.println("Options:");
            System.out.println("=======================================");
            System.out.println("(a) HATS   [" + hats.size() + "]");
            if (selArmourCategory.equals("HATS")) {printLabeledArmours(hats);}
            System.out.println("(b) GLOVES [" + gloves.size() + "]");
            if (selArmourCategory.equals("GLOVES")) {printLabeledArmours(gloves);}
            System.out.println("(c) SHIRTS [" + shirts.size() + "]");
            if (selArmourCategory.equals("SHIRTS")) {printLabeledArmours(shirts);}
            System.out.println("(d) PANTS  [" + pants.size() + "]");
            if (selArmourCategory.equals("PANTS")) {printLabeledArmours(pants);}
            System.out.println("(e) BOOTS  [" + boots.size() + "]");
            if (selArmourCategory.equals("BOOTS")) {printLabeledArmours(boots);}
            System.out.println("=======================================");
            
            responseInt = 0;  // Reset item selection between folder changes
            String response = readLine("Which armour piece would you like to equip? (l) to leave: ");
            
            // Attempt to turn the response into a number (Item selection)
            // Otherwise, turn it into a character        (Folder selection)
            try {
                responseInt = Integer.parseInt(response); // Turn response to a number
            } catch(Exception e) {
                if (response.length() != 0)
                {
                    responseChar = response.charAt(0);
                }
            }

            // Get the correct armour folder based on the player's input
            // And get the ArrayList of said folder items
            switch(responseChar)
            {
                case 'a': selArmourCategory = "HATS";   
                        selFolder = hats; break;
                case 'b': selArmourCategory = "GLOVES"; 
                        selFolder = gloves; break;
                case 'c': selArmourCategory = "SHIRTS"; 
                        selFolder = shirts; break;
                case 'd': selArmourCategory = "PANTS";  
                        selFolder = pants; break;
                case 'e': selArmourCategory = "BOOTS";  
                        selFolder = boots; break;
                default: selArmourCategory = "none";
            }
            
            // Quit if player pressed 'l'
            if (responseChar == 'l')
            {
                break;
            }
            
            // If player selected an item, 
            // confirm it is valid, then equip it
            if (responseInt != 0)
            {
                try {
                    
                    // Attempt to retrieve item from selection
                    Armour selItem = selFolder.get(responseInt-1);
                    
                    // Assuming the previous step worked, add the item to it's
                    // correct spot in the equippedArmour array
                    switch(selArmourCategory)
                    {
                        case "HATS":    equippedArmour[0] = selItem; break;
                        case "GLOVES":  equippedArmour[1] = selItem; break;
                        case "SHIRTS":  equippedArmour[2] = selItem; break;
                        case "PANTS":   equippedArmour[3] = selItem; break;
                        case "BOOTS":   equippedArmour[4] = selItem; break;
                    }
                    
                    System.out.println("\nEquipped: " + selItem + "\n");
                    readLine("Press enter to continue...");
                    
                } catch (Exception e) {
                    System.out.println("Invalid number");
                }
            }
        }
    }
    
    private void printLines(int numLines)
    {
        for (int i = 0; i < numLines; i++)
        {
            System.out.println();
        }
    }
    
    private void printLabeledArmours(ArrayList<Armour> armourCat)
    {
        int number = 1;
        
        for (Armour a : armourCat)
        {
            System.out.println("     (" + number + ") " + a);
            number++;
        }
    }
    
    private void printEquippedArmour()
    {
        System.out.println("=========== Equipped Armour ===========");
        printNoneNotNull("Head:   "  , equippedArmour[0]);
        printNoneNotNull("Gloves: ", equippedArmour[1]);
        printNoneNotNull("Shirt:  " , equippedArmour[2]);
        printNoneNotNull("Pants:  " , equippedArmour[3]);
        printNoneNotNull("Boots:  " , equippedArmour[4]);
        System.out.println("Total Defense: " + getPlayerDefense());
        System.out.println("=======================================\n");
    }
    
    private void printNoneNotNull(String string, Armour armour)
    {
        if (armour == null)
        {
            System.out.println(string + "None");
        }
        else
        {
            System.out.println(string + armour);
        }
    }
    
    public Weapon getEquippedWeapon()
    {
        return equippedWeapon;
    }
    
    public void addCoins(int coins)
    {
        playerCoins += coins;
    }
    
    public int getCoins()
    {
        return playerCoins;
    }
    
    public void addItems(ArrayList<Item> newItems)
    {
        
        // Add new items to the inventory
        for (Item i : newItems)
        {
            System.out.println("Added: " + i);
            items.add(i);
        }
        
        //System.out.println("Added items");
        readLine("Press enter to continue...");
    }
    
    public void addItem(Item newItem)
    {
        items.add(newItem);
    }
    
    public String toString()
    {
        System.out.println();
        printEquippedWeapon();
        printEquippedArmour();
        
        System.out.println("Inventory:");
        for (Item i : items)
        {
            System.out.println(i);
        }
        
        System.out.println("Coins: $" + playerCoins);
        
        return "";
    }
}