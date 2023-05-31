import java.util.ArrayList;

public class Shop extends ConsoleProgram
{
    private Inventory inventory;
    private int playerMoney;
    private int rarity;
    
    
    
    public Shop(Inventory inventory, int rarity)
    {
        this.rarity = rarity;
        this.inventory = inventory;
        this.playerMoney = inventory.getCoins();
    }
    
    public void open()
    {
        System.out.println("Welcome to the shop!");
        
        readLine(" ");
        
        // Get four pieces of loot to put on display
        ArrayList<Item> itemsForSale = new ArrayList<Item>(); // Stores the 4 items
        Loot l = new Loot(rarity);
        
        readLine(" ");
        
        for (int i = 0; i < 4; i++)
        {
            itemsForSale.add(l.lootRandom());
        }
    }
}