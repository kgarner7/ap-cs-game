package actor;

/**
 * The AItem class provides an attack boost the User
 * @author Kendall Garner
 *
 */
public class AItem implements Item
{
	private int attack;
	private String image, name;
	public boolean equipped;

	/**
	 * creates an AItem with attack of stat
	 * @param stat the attack stat of the AItem
	 * @param image the string representation of the AItem
	 * @param name the name of the AItem
	 */
	public AItem(int stat, String image, String name)
	{
		setItem(stat,image,name);
	}
	
	/**
	 * initializes the stats
	 */
	public void setItem(int stat, String image, String name) 
	{
		this.attack = stat;
		this.image = image;
		this.name = name;
	}

	/**
	 * returns the attack stat of the AItem
	 * @return the attack bonus
	 */
	public int stat() 
	{
		return attack;
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
		return ((other instanceof AItem) && ((AItem) other).stat() == attack);
	}
	
	public String toString()
	{
		return name + ". " + attack + " attack " + "\n" + image; 
	}

	/**
	 * returns the type of Item
	 * @return 0 to represent AItem
	 */
	public int getType() 
	{
		return 0;
	}
}
