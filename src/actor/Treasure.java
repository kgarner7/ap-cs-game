package actor;

import pathFinder.*;

/**
 * A treasure chest. Stores a item.
 * @author Kendall Garner
 */
public class Treasure extends Actor
{
	Item i;
	//" |-|"
	//"/   \"
	//"\___/"

	/**
	 * creates a random item with a random attack or defense stat or healing stat
	 */
	public Treasure()
	{
		int choice = (int)(Math.random() * 3);
		if(choice == 0)
		{
			int stat = (int)(Math.random() * 3) + 2;
			if(stat == 2)
				i = new AItem(stat, "+---->" + "\n" + "+---->","A stick. Breaks easily");
			else if(stat == 3)
				i = new AItem(stat, "                 +=+" + "\n" + "=======+=+=>" + "\n" + "                 +=+","A axe. Very blunt");
			else
				i = new AItem(stat, "   |" + "\n" + "=+-----=>" + "\n" + "   |","A sword. Very sharp");
				
		}
		else if(choice == 1)
		{
			int stat = (int)(Math.random() * 3) + 2;
			if(stat == 2)
				i = new DItem(stat, "|----|" + "\n" + "|  --  |" + "\n" + "|----|","A small shield. Rather usesless");
			else if(stat == 3)
				i = new DItem(stat, "<^^^^>" + "\n" + "<  --  >" + "\n" + "<vvvv>","A spiked shield. To repel attackers");
			else
				i = new DItem(stat, "    |--|" + "\n" + "    |    |" + "\n" + "________","A top hat. Be your inner gentleman");
		}
		else
		{
			i = new HItem((int)(Math.random() * 20) + 10, "  |-|" + "\n" + "/     \\" + "\n" + "\\___/","A potion");
		}
	}
	
	/**
	 * does nothing
	 */
	public void act()
	{
		
	}
	
	/**
	 * accesses the Item stored
	 * @return i the stored Item
	 */
	public Item getItem()
	{
		return i;
	}
	
	/**
	 * returns a string description of the Actor
	 * @return a description of the Treasure
	 */
	public String toString()
	{
		return "Treasure with some item";
	}
	
	/**
	 * finds if there is a path from the Treasure to the User
	 * @return true if a path can be formed, false otherwise
	 */
	public boolean hasPath()
	{
		PathFinder as = new PathFinder(getGrid(), 1500);
    	Path p = (as.findPath(this, getGrid().getUser().getLocation().getRow(), getGrid().getUser().getLocation().getCol(),false));
    	return (p != null);
	}
}
