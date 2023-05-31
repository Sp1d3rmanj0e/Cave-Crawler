public class Main extends ConsoleProgram{
    public Main() {
        
        // Italics code
        //System.out.println("\033[3mText goes here\033[0m");
        
        Inventory inventory = new Inventory();
        Map m = new Map(20, 20, inventory);
        
        while(true)
        {
            char movement = 'x';
            
            try
            {
                m.drawMap();
                movement = readLine("Direction: ").charAt(0);
            }
            catch(Exception e)
            {
                
            }
            
            switch(movement)
            {
                case 'w': m.movePlayer(0, -1);
                    break;
                case 'a': m.movePlayer(-1, 0);
                    break;
                case 's': m.movePlayer(0, 1);
                    break;
                case 'd': m.movePlayer(1, 0);
                    break;
                case 'i': System.out.println(inventory);
                    break;
                case 'e': inventory.equipWeapon();
                    break;
                case 'r': inventory.equipArmour();
                    break;
                case 'c': Loot l = new Loot(100);
                          inventory.addItems(l.loot());
                    break;
                case 'u': inventory.seeUtility();
                    break;
                //case 'm': 
                //    m.generateMap();
                //    break;
            }
        }
    }
}
