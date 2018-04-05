package actor;

/**
 * The Item interface is used to branch out for the other Items which are used
 * @author Kendall Garner
 *
 */
public interface Item 
{

	/**
	 * accesses the stat (attack, defense, or healing)
	 * @return defense the value of defense
	 */
	public int stat();
	
	/**
	 * returns the name of the Item
	 */
	public String getName();
	
	/**
	 * sets Item to be equipped
	 */
	public void equip();
	
	/**
	 * sets Item to not be equipped
	 */
	public void remove();

	
	/**
	 * checks if the two Items have the same name, description, and stats
	 * @param i the Item to check
	 * @return true if they are equal, false otherwise
	 */
	public boolean equals(Object i);
	
	/**
	 * returns the Item name, followed by its attack or defense stat, followed by its word-based image
	 * @return description the description of the Item
	 */
	public String toString();

	/**
	 * returns the type of Item
	 * @return 0 for attack stat, 1 for defense, and 2 for healing
	 */
	public int getType();
}
