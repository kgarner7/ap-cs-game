package pathFinder;

import grid.Location;

import java.util.ArrayList;

public class Path 
{
	private ArrayList<Location> steps = new ArrayList<Location>();


	/**
	 * get the amount of Locations in the Path
	 * @return the size of steps
	 */
	public int getLength() 
	{
		return steps.size();
	}
	
	/**
	 * returns the Location at the specified index
	 * @param index the index to check
	 * @return the Location at the specified index
	 */
	public Location getStep(int index)
	{
		return steps.get(index);
	}
	
	/**
	 * adds a new Location (row,col) at index
	 * @param index the index to be added
	 * @param row the row of the Location
	 * @param col the col of the Location
	 */
	public void addStep(int index, int row, int col) 
	{
		steps.add(index, new Location(row,col));
	}
	
	/**
	 * returns whether steps contains the Location (row,col)
	 * @param row the row of the Location to check
	 * @param col the col of the Location to check
	 * @return true if the Location exists in the list, false otherwise
	 */
	public boolean contains(int row, int col) 
	{
		return steps.contains(new Location(row,col));
	}
	
	public String toString()
	{
		return steps.toString();
	}
}