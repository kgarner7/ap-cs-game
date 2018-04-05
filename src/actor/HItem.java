package actor;

/**
 * The HItem class restores the User's health
 * @author Kendall Garner
 *
 */
public class HItem implements Item
{
	private int restore;
	private String image, name;
	public boolean equipped;

	/**
	 * creates a healing item that heals stat 
	 * @param stat the amount the HItem heals
	 * @param image the string description of the HItem
	 * @param name the name of the HItem
	 */
	public HItem(int stat, String image, String name)
	{
		setItem(stat,image,name);
	}
	
	/**
	 * initializes the instance fields
	 */
	public void setItem(int stat, String image, String name) 
	{
		this.restore = stat;
		this.image = image;
		this.name = name;
	}

	/**
	 * accesses the restorative stat
	 * @return restore the health the HItem restores
	 */
	public int stat() 
	{
		return restore;
	}

	public String getName() 
	{
		return name;
	}

	public String getImage() 
	{
		return image;
	}

	public void equip() 
	{
		equipped = true;
	}

	public void remove() 
	{
		equipped = false;
		
	}

	public boolean isEquipped() 
	{
		return equipped;
	}

	public boolean equals(Object other)
	{
		return ((other instanceof AItem) && ((AItem) other).stat() == restore);
	}
	
	public String toString()
	{
		return name + ". " + restore + " health " + "\n" + image; 
	}
	
	/**
	 * returns the type of Item, or 2 to represent the class
	 */
	public int getType()
	{
		return 2;
	}
}
