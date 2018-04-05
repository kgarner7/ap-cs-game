package actor;

/**
 * The DItem provides a defense boost to the User
 * @author Kendall Garner
 */
public class DItem implements Item
{
	private int defense;
	private String image, name;
	public boolean equipped;

	/**
	 * creates a DItem with defense stat of stat
	 * @param stat the defense bonus of the DItem
	 * @param image the String representation of the DItem
	 * @param name the name of the DItem
	 */
	public DItem(int stat, String image, String name)
	{
		setItem(stat,image,name);
	}
	
	/**
	 * Initializes the variables
	 */
	public void setItem(int stat, String image, String name) 
	{
		this.defense = stat;
		this.image = image;
		this.name = name;
	}

	/**
	 * returns the defense stat
	 * @return the defense of the DItem
	 */
	public int stat() 
	{
		return defense;
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
		return ((other instanceof DItem) && ((DItem) other).stat() == defense);
	}
	
	public String toString()
	{
		return name + ". " + defense + " defense "+ "\n" + image; 
	}
	
	/**
	 * returns the type of Item
	 * @return 1 representing a DItem
	 */
	public int getType()
	{
		return 1;
	}
}
