package actor;

import grid.Location;

/**
 * The typical obstacle. Always gets in your way.
 * @author Kendall Garner
 *
 */
public class Wall extends Actor{

	/**
	 * does nothing
	 */
	public void act()
	{
		
	}
	
	/**
	 * creates a string to describe the wall
	 * @return the type of actor
	 */
	public String toString()
	{
		return "A wall";
	}
	
	/**
	 * returns the next location the Wall will move to
	 * @return the current location
	 */
	public Location getNextLocation()
	{
		return getLocation();
	}
}
