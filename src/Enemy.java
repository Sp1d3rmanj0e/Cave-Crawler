import java.io.*;
import java.util.*;

public class Enemy extends ConsoleProgram
{
    private int rarity;
    private int playerHealth;
    private int playerDefense;
    
    private ArrayList<Object[]> enemyTableList = new ArrayList<Object[]>();
    
    // Makeshift enums until I learn how they work in Java
    private final static int WEIGHT                 = 0;
    private final static int SPRITE                 = 1;
    private final static int LIFE                   = 2;
    private final static int DAMAGE                 = 3;
    private final static int ARMOUR_PENETRATION     = 4;
    private final static int SPECIAL_DROP_WEAPON    = 5;
    private final static int SPECIAL_DROP_ARMOUR    = 6;
    private final static int SPECIAL_DROP_UTILITY   = 7;    
    
    public Enemy(int rarity, int playerHealth, int playerDefense)
    {
        this.rarity = rarity;
        this.playerHealth = playerHealth;
        this.playerDefense = playerDefense;
        getEnemyTable();
    }
    
    // Loads the enemy table to the enemyTableList ArrayList
    // Contains arrays like this:
    //
    // [Weight, Sprite, Life, Damage, Armour_Penetration, 
    //  Special_Drop_Weapon, Special_Drop_Armour, Special_Drop_Utility]
    private void getEnemyTable()
    {
        BufferedReader objReader = null;
            
        try {
            String command;
            String category = ">EMPTY";
            Object[] enemy = new Object[8];
            ArrayList<String> sprite = new ArrayList<String>();

            objReader = new BufferedReader(new FileReader("src/EnemyTable.txt"));
            String line;

            while((line = objReader.readLine()) != null)
            {
                
                // Get the command (if any)
                char commandToken = line.charAt(0);
                
                switch(commandToken)
                {
                    case '/': command = "Empty Token"; break;
                    case '+': command = "Rarity Token"; break;
                    case '>': command = "Category Token"; break;
                    case '=': command = "Creation Token"; break;
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
                            doneScanning = true;;   // Leave the lootTable if tableRarity 
                                            // exceeds the chest's rarity
                        }
                        
                        System.out.println("Rarity Token Evaluated: " + tableRarity);
                    break;
                    case "Category Token": // Get the category of the following
                                           // Item tokens.  All item tokens
                                           // Evaluated after a category token
                                           // Is declared will go into that category
                        category = line;
                    break;
                    case "Creation Token":
                        switch(line)
                        {
                            case "=NEW_ENEMY": // Create a clean enemy array to fill with data
                                enemy = new Object[8];
                                sprite = new ArrayList<String>();
                                System.out.println("New enemy created");
                                break;
                            case "=PUSH_ENEMY": // Save the enemy to the enemyList
                                
                                System.out.println("Pushing enemy");
                                
                                // Add the sprite to the enemy
                                enemy[SPRITE] = sprite;
                                
                                // Add the enemy to the enemyList equal to its weight
                                for (int i = 0; i < (int)enemy[WEIGHT]; i++) {
                                    enemyTableList.add(enemy); // Add the enemy to the enemyList
                                }
                                System.out.println("Pushing enemy successful");
                                break;
                        }
                        break;
                    case "Item token":
                        // Weight - number
                        // Sprite - ArrayList of ASCII art lines
                        // Life - number
                        // Damage - number
                        // Armour_Penetration - number
                        // Special_Drop_Weapon - weapon object
                        // Special_Drop_Armour - armour object
                        // Special_Drop_Utility - utility object
                            
                        switch(category)
                        {
                            case ">WEIGHT":
                                enemy[WEIGHT] = Integer.parseInt(line);
                                //System.out.println("Adding weight");
                                break;
                            case ">SPRITE":
                                sprite.add(line);
                                //System.out.println("Adding sprite line");
                                break;
                            case ">LIFE":
                                enemy[LIFE] = Integer.parseInt(line);
                                //System.out.println("Adding life");
                                break;
                            case ">DAMAGE":
                                enemy[DAMAGE] = Integer.parseInt(line);
                                //System.out.println("Adding Damage");
                                break;
                            case ">ARMOUR_PENETRATION":
                                enemy[ARMOUR_PENETRATION] = Integer.parseInt(line);
                                //System.out.println("Adding Armour Penetration");
                                break;
                            case ">SPECIAL_DROP_WEAPON": 
                                enemy[SPECIAL_DROP_WEAPON] = line;
                                //System.out.println("Adding special Item");
                                break;
                            case ">SPECIAL_DROP_ARMOUR": 
                                enemy[SPECIAL_DROP_ARMOUR] = line;
                                //System.out.println("Adding special Item");
                                break;
                            case ">SPECIAL_DROP_UTILITY":
                                enemy[SPECIAL_DROP_UTILITY] = line;
                                //System.out.println("Adding special Item");
                                break;
                        }
                    break;
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
            }catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public int getPlayerHealth()
    {
        return playerHealth;
    }
    
    public ArrayList<Item> fightEnemy(Weapon weapon)
    {
        // Create the variable that stores and returns enemy drop loot
        ArrayList<Item> enemyLoot = new ArrayList<Item>();
        
        Object[] enemy = getRandomEnemy();
        
        // Loop until enemy is dead (or otherwise broken)
        while((int)enemy[LIFE] > 0)
        {
            
            // Get the player's attack speed
            int playerAttackSpeed = weapon.getAttackSpeed();
            
            // Draw the enemy
            ArrayList<String> sprite = (ArrayList<String>)enemy[SPRITE];
            for (String s : sprite)
            {
                System.out.println(s);
            }
            
            // Draw stats
            System.out.println("Life: " + (int)enemy[LIFE]);
            System.out.println("Damage: " + (int)enemy[DAMAGE]);
            System.out.println("Penetration: " + (int)enemy[ARMOUR_PENETRATION]);
            
            System.out.println("\nYour life is: " + playerHealth);
            String command = readLine("What would you like to do?\n"
                            + "r - run || f - fight || u - utility");
            
            if (command.equals("r"))
            {
                break;
            }
                
                switch(command)
                {
                    case "f": 
                        // Loop for each turn the player has
                        for (int i = 0; i < playerAttackSpeed; i++)
                        {
                            
                            readLine("Press enter to attack! (Swings left: " + (playerAttackSpeed - i) + ")");
                            System.out.println("You swing for " + weapon.getDamage() + " damage");
                            
                            // Enemy takes damage
                            enemy[LIFE] = (int)enemy[LIFE] - weapon.getDamage();
                            
                            System.out.println("The enemy's life is now: " + enemy[LIFE]);
                            readLine("Press enter to continue...");
                            
                            if ((int)enemy[LIFE] <= 0)
                            {
                                i = playerAttackSpeed;
                            }
                        }
                        
                        // Only let the enemy strike back if it
                        // is still alive
                        if ((int)enemy[LIFE] > 0)
                        {
                            // Calculate damage to player
                            double damage = (int)enemy[DAMAGE];
                            int penetration = (int)enemy[ARMOUR_PENETRATION];
                            damage *= (int)((double)penetration/(playerDefense+1));
                            
                            System.out.println("Your defense is " + playerDefense
                                + " and the enemy penetration is " + penetration + "!");
                            System.out.println("The enemy swings back at you for "
                                + damage + " damage!");
                            
                            // Player takes damage
                            playerHealth -= damage;
                            System.out.println("Your life is now " + playerHealth);
                            
                            readLine("Press enter to continue...");
                        }
                        
                        break;
                    case "u": System.out.println("Opened utility");
                        break;
                }
            
        }
        
        System.out.println("Fight ended");
        
        // Check to see if you actually killed the enemy
        if ((int)enemy[LIFE] <= 0)
        {
            System.out.println("Enemy died");
            
            Random rand = new Random();
            
            // Check if the enemy had a special weapon drop
            if (enemy[SPECIAL_DROP_WEAPON] != null)
            {
                // Organized by [Percent Drop, Name, Value, Damage, Attack Speed]
                String[] wStats = String.valueOf(enemy[SPECIAL_DROP_WEAPON]).split(" ");
                
                // Check if the enemy drops the item
                int dropChance = Integer.parseInt(wStats[0]);
                
                if ((rand.nextInt(99)+1) <= dropChance)
                {
                    Item special_drop_weapon = new Weapon(
                        wStats[1],                      // Name
                        Integer.parseInt(wStats[2]),    // Value
                        Integer.parseInt(wStats[3]),    // Damage
                        Integer.parseInt(wStats[4]));   // Attack Speed
                        
                    enemyLoot.add(special_drop_weapon);
                }
            }
            
            // Check if the enemy had a special armour drop
            if (enemy[SPECIAL_DROP_ARMOUR] != null)
            {
                // Organized by [Percent Drop, Name, Value, Defense]
                String[] aStats = String.valueOf(enemy[SPECIAL_DROP_ARMOUR]).split(" ");
                
                // Check if the enemy drops the item
                int dropChance = Integer.parseInt(aStats[0]);
                if ((rand.nextInt(99)+1) <= dropChance)
                {
                    Item special_drop_armour = new Armour(
                        aStats[1],                      // Name
                        Integer.parseInt(aStats[2]),    // Value
                        Integer.parseInt(aStats[3]),    // Defense
                        aStats[4]);                     // ArmourType
                        
                    enemyLoot.add(special_drop_armour);
                }
            }
            
            // Check if the enemy had a special utility drop
            if (enemy[SPECIAL_DROP_UTILITY] != null)
            {
                // Organized by [Percent Drop, Name, Value]
                String[] uStats = String.valueOf(enemy[SPECIAL_DROP_UTILITY]).split(" ");
                
                // Check if the enemy drops the item
                int dropChance = Integer.parseInt(uStats[0]);
                if ((rand.nextInt(99)+1) <= dropChance)
                {
                    Item special_drop_utility = new Utility(
                        uStats[1],                      // Name
                        Integer.parseInt(uStats[2]));   // Value
                        
                    enemyLoot.add(special_drop_utility);
                }
            }
        }
        
        return enemyLoot;
    }
    
    private Object[] getRandomEnemy()
    {
        // Get a random enemy
        Collections.shuffle(enemyTableList);
        return enemyTableList.get(0);
    }
}