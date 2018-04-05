package actor;


/**
 * The ultimate goal.
 * @author Kendall Garner
 *
 */
public class Stairs extends Wall{

	/**
	 * returns whether the User is a neighbor of the Stairs
	 * @return true if the User is a neighbor, false otherwise
	 */
	public boolean hasWon()
	{
		return getGrid().getNeighbors(this.getLocation()).contains(getGrid().getUser());
	}

	/**
	 * returns a string representation of the Stairs
	 */
	public String toString()
	{
		return "Stairs. Nothing much";
	}
}
