import java.util.*;

public class Map extends ConsoleProgram
{
    private MAP[][] mapTiles;
    private boolean[][] mapVisibility; // False = hidden ; True = shown
    
    // Enums
    private enum MAP {
        FLOOR,
        WALL,
        CHEST,
        ENEMY,
        DOOR,
        SHOP
    }
    
    // Player stats
    private int playerX;
    private int playerY;
    private int playerHealth = 100;
    private int playerViewDist = 5;
    
    // Properties
    private int width;
    private int height;
    private int baseRarity = 0;
    
    // Inventory to store loot
    private Inventory inventory;
    
    public Map(int width, int height, Inventory inventory)
    {
        this.width = width;
        this.height = height;
        
        this.inventory = inventory;
        
        generateMap(width*height);
    }
    
    public void movePlayer(int xChange, int yChange)
    {
        // Move the player if not colliding with a wall
        if (mapTiles[playerX + xChange][playerY+yChange] != MAP.WALL)
        {
            playerX += xChange;
            playerY += yChange;
        }
        
        // Get more map visibility
        mapVisibility[playerX][playerY] = true;
        mapVisibility[playerX+1][playerY] = true;
        mapVisibility[playerX-1][playerY] = true;
        mapVisibility[playerX][playerY+1] = true;
        mapVisibility[playerX][playerY-1] = true;
        
        drawMap();
        
        activateTile();
    }
    
    private void activateTile()
    {
        MAP tile = mapTiles[playerX][playerY];
        int rarity = baseRarity + (int)distToSpawn(playerX, playerY);
        
        switch(tile)
        {
            case CHEST: // Loot a chest
                
                // Create the chest to loot
                Loot l = new Loot(rarity); // Change 1 to a real number later
                
                // Get the items looted from the chest
                ArrayList<Item> lootedItems = l.loot();
                inventory.addItems(lootedItems); // Add items to inventory
                
                // Get the coins looted from the chest
                int lootedCoins = l.lootCoins();
                inventory.addCoins(lootedCoins); // Add items to the inventory
                
                // Turn tile into a normal floor after interacting with it
                mapTiles[playerX][playerY] = MAP.FLOOR;
                break;
            case ENEMY: // Fight an enemy
                
                // Create an enemy to fight
                Enemy en = new Enemy(rarity, playerHealth, inventory.getPlayerDefense()); // Chance 1 to a real number later
                
                // Fight the enemy and store loot spoils to ArrayList
                ArrayList<Item> enemyLoot = en.fightEnemy(inventory.getEquippedWeapon());
                
                // Adjust player health according to the fight
                playerHealth = en.getPlayerHealth();
                
                // Push loot spoils to inventory
                inventory.addItems(enemyLoot);
                
                readLine("Press enter to continue...");
                drawMap();
                
                // Turn tile into a normal floor after interacting with it
                mapTiles[playerX][playerY] = MAP.FLOOR;
                break;
            
            case DOOR: // Give the player an option to go to a new map
                    
                if (askYN("Would you like to go to the next room? (y/n)"))
                {
                    baseRarity += 5;
                    generateMap();
                }
                break;
            case SHOP: // Give the player an option to enter the shop
                
                if (askYN("You have found a shop!  Would you like to enter? (y/n)"))
                {
                    Shop shop = new Shop(inventory, rarity);
                    shop.open();
                }
                break;
        }
    }
    
    // Ask a question
    // Return true if Y and false if N
    private boolean askYN(String question)
    {
        while(true)
        {
            String answer = readLine(question);
            
            if (answer.length() != 0)
            {
                char answerCh = answer.charAt(0);
                if (answerCh == 'y')
                {
                    return true;
                }
                else if (answerCh == 'n')
                {
                    return false;
                }
            }
        }  
    }
    
    // Returns false if a rule is broken
    private boolean mapGenRules(int x, int y)
    {
        Random rand = new Random();
        
        // Prevent floor builder from breaking border walls
        if ((x == 0) || (x == width-1) || (y == 0) || (y == height-1))
        {
            return false;
        }
        
        // If a neighboring space is a floor, make it less likely to break
        // this wall
        int unblockedSpaces = 0;
        
        if (mapTiles[x+1][y] == MAP.FLOOR)
        {
            unblockedSpaces++;
        }
        if (mapTiles[x-1][y] == MAP.FLOOR)
        {
            unblockedSpaces++;
        }
        if (mapTiles[x][y+1] == MAP.FLOOR)
        {
            unblockedSpaces++;
        }
        if (mapTiles[x][y-1] == MAP.FLOOR)
        {
            unblockedSpaces++;
        }
        
        if (unblockedSpaces >= rand.nextInt(3)+2)
        {
            return false;
        }
        
        return true;
    }
    
    public void setRarity(int rarity)
    {
        this.baseRarity = rarity;
    }
    
    public void generateMap()
    {
        generateMap(height*width);
    }
    
    public void generateMap(int area)
    {
        // Stores whether an exit has been added
        boolean exitAdded = false;
        
        // Create the map
        mapTiles = new MAP[width][height];
        mapVisibility = new boolean[width][height];
        
        // Put the player on the bottom middle of the map
        playerX = width/2;
        playerY = height-2;
        
        mapTiles[width/2][height-2] = MAP.FLOOR; // The tile the player is standing
                                             // on must be a floor
        
        // Set each tile in the map to HIDDEN / false
        // and set each tile in the map to a wall
        for (int w = 0; w < width; w ++) {
        for (int h = 0; h < height; h++) {
            
            mapVisibility[w][h] = false; //false;
            mapTiles[w][h] = MAP.WALL;
        }}
        
        // Create random object
        Random rand = new Random();
        
        // Start the map at the player's location
        int x = playerX;
        int y = playerY;
        
        // Build the paths
        for (int i = 0; i < area*2; i++)
        {
            // Get the direction to move
            int move = rand.nextInt(3)-1; // -1 left or up, 1 right or down, 0 no move
            
            // Move in a direction
            if (rand.nextInt(2) == 0) // Left-right
            {
                if (mapGenRules(x+move, y))
                {
                    x += move;
                }
            }
            else // Up-down
            {
                if (mapGenRules(x, y+move))
                {
                    y += move;
                }
            }
            
            mapTiles[x][y] = MAP.FLOOR;
            

        }
        
        // Loop through all non-border spaces
        // Add chests and enemies
        for (int w = 1; w < width-1; w++) {
        for (int h = 1; h < height-1; h++) {
        
            // Confirm it is an empty space
            if (mapTiles[w][h] == MAP.FLOOR)
            {
                // Add the chests
                if (rand.nextInt(40) == 0)
                {
                    mapTiles[w][h] = MAP.CHEST;
                }
                // Add the enemies
                else if (rand.nextInt(12) == 0)
                {
                    mapTiles[w][h] = MAP.ENEMY;
                }
                else if (rand.nextInt(180) == 0)
                {
                    mapTiles[w][h] = MAP.SHOP;
                }
            }
        }}
        
        // Add an exit door on a blue wall next to a path
        // If the door wasn't placed in the first path,
        // keep looping until a door is placed
        while(!exitAdded)
        {
            for (int w = 1; w < width-1; w++) {
            for (int h = 1; h < height-1; h++) {
                
                // Confirm the selected tile is a wall
                if (mapTiles[w][h] == MAP.WALL)
                {
                    // Confirm only one side from the wall
                    // is not a wall
                    int numFloors = 0;
                    if (mapTiles[w+1][h] != MAP.WALL) {numFloors++;}
                    if (mapTiles[w-1][h] != MAP.WALL) {numFloors++;}
                    if (mapTiles[w][h+1] != MAP.WALL) {numFloors++;}
                    if (mapTiles[w][h-1] != MAP.WALL) {numFloors++;}
                    
                    // If only one side is a wall, randomize
                    // and potentially add a door there
                    if (numFloors == 1)
                    {
                        if (rand.nextInt(area/4) == 0)
                        {
                            mapTiles[w][h] = MAP.DOOR;
                            exitAdded = true;
                        }
                    }
                }
            }}
        }
        
        // Player can't spawn on an enemy
        mapTiles[playerX][playerY] = MAP.FLOOR;
        
        movePlayer(0, 0); // Get initial visiblity
    }
    
    private double distToSpawn(int x, int y)
    {
        double distX = (double)width/2 - x;
        double distY = (double)height-1 - y;
        
        return Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
    }

    // Contains the 16X7 (square technically) ascii design for each block in the map.
    // Returns the layer asked for
    private String getStructureLayer(int layer, MAP structure, boolean isPlayer, boolean isVisible)
    {
        String[] layers = new String[7];

        switch(structure)
        {
            case FLOOR:
            layers[0] = "                ";
            layers[1] = "                ";
            layers[2] = "                ";
            layers[3] = "                ";
            layers[4] = "                ";
            layers[5] = "                ";
            layers[6] = "                ";
            break;
            case CHEST:

            layers[0] = "  ████████████  ";
            layers[1] = " ██__________██ ";
            layers[2] = " | |        | | ";
            layers[3] = " | |________| | ";
            layers[4] = " ████  ██  ████ ";
            layers[5] = " █ |_█____█_| █ ";
            layers[6] = " ██████████████ ";
            break;
            case WALL:
            layers[0] = "████████████████";
            layers[1] = "██            ██";
            layers[2] = "██            ██";
            layers[3] = "██            ██";
            layers[4] = "██            ██";
            layers[5] = "██            ██";
            layers[6] = "████████████████";
            break;
            case DOOR:
            layers[0] = "████████████████";
            layers[1] = " ██          ██ ";
            layers[2] = " ██   [===]  ██ ";
            layers[3] = " ██       __ ██ ";
            layers[4] = " ██          ██ ";
            layers[5] = " ██          ██ ";
            layers[6] = "███__________███";
            break;
            case ENEMY:
            layers[0] = " ██          ██ ";
            layers[1] = "  ██        ██  ";
            layers[2] = "   ██      ██   ";
            layers[3] = " █            █ ";
            layers[4] = " █    _____   █ ";
            layers[5] = "     ███████    ";
            layers[6] = "    ███   ███   ";
            break;
            case SHOP:
            layers[0] = "      █  █      ";
            layers[1] = "    ████████    ";
            layers[2] = "   █  █  █      ";
            layers[3] = "    ███████     ";
            layers[4] = "      █  █ █    ";
            layers[5] = "   ████████     ";
            layers[6] = "      █  █      ";
            break;
        }

        if (!isVisible)
        {
            layers[0] = "░░░░░░░░░░░░░░░░";
            layers[1] = "░░░░░░░░░░░░░░░░";
            layers[2] = "░░░░░░░░░░░░░░░░";
            layers[3] = "░░░░░░░░░░░░░░░░";
            layers[4] = "░░░░░░░░░░░░░░░░";
            layers[5] = "░░░░░░░░░░░░░░░░";
            layers[6] = "░░░░░░░░░░░░░░░░";
        }

        else if (isPlayer)
        {
            layers[0] = "     ,          ";
            layers[1] = "    (_0_        ";
            layers[2] = "      W )       ";
            layers[3] = "     /\"\\__      ";
            layers[4] = "    |      .    ";
            layers[5] = "   _|           ";
            layers[6] = "                ";
        }

        return layers[layer-1];
    }

    public void drawMap()
    {
        for (int i = 0; i < 30; i++)
        {
            System.out.println();
        }
        
        // This calculates the area that the player can see
        // Given their current location and their view distance
        int lowestViewY = Math.max(0, playerY - playerViewDist);
        int highestViewY = Math.min(height, playerY + playerViewDist);
        int lowestViewX = Math.max(0, playerX - playerViewDist);
        int highestViewX = Math.min(width, playerX + playerViewDist);

        // This will use another function called getStructureLayer(int layer)
        // Structures will be 16 X 7 (technically a square), but we can only draw one line at a time,
        // so it will return only the line we are currently at
        for (int h = lowestViewY; h < highestViewY; h++) 
        {
            // Loop through each height 7 times to get each layer drawn on the terminal
            for (int layer = 1; layer <= 7; layer++) 
            {
                for (int w = lowestViewX; w < highestViewX; w++) {

                    System.out.print(getStructureLayer(layer, mapTiles[w][h], ((h == playerY)&& (w == playerX)), mapVisibility[w][h]));
                }
                System.out.println();
            }

            System.out.println();
        }

        /*
        for (int h = lowestViewY; h < highestViewY; h++) 
        {
            
            for (int w = lowestViewX; w < highestViewX; w++) 
            {
                if ((h == playerY) && (w == playerX))
                {
                    System.out.print("R ");
                }
                else if (mapVisibility[w][h])
                {
                    switch (mapTiles[w][h])
                    {
                        case FLOOR: System.out.print("  ");
                            break;
                        case WALL: System.out.print("██");
                            break;
                        case CHEST: System.out.print("Y ");
                            break;
                        case ENEMY: System.out.print("G ");
                            break;
                        case DOOR: System.out.print("P ");
                            break;
                        case SHOP: System.out.print("B ");
                            break;
                        default: System.out.print(String.valueOf(mapTiles[w][h]));
                    }
                }
                else
                {
                    System.out.print("░░");
                }
            }
            System.out.println();
        }
        */
    }
    
}