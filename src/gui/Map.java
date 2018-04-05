package gui;

import grid.*;
import actor.*;

/**
 * An adventurer's best friend! The Map keeps track of your location and the occupants of any location you have checked.
 * @author kendallgarner
 *
 */
public class Map {
	private Location[][] locsToCheck;
	private String[][] stringDescription;
	@SuppressWarnings("rawtypes")
	private Grid grid;
	private int[] smallest, largest;

	/**
	 * creates a map which contains the grid g
	 * @param g the grid the map checks
	 */
	@SuppressWarnings("rawtypes")
	public Map(Grid g)
	{
		this.locsToCheck = new Location[g.getNumRows()][g.getNumRows()];
		this.stringDescription = new String[g.getNumRows()][g.getNumRows()];
		this.update(g);
		smallest = this.findLargest();
		largest = this.findLargest();
	}

	/**
	 * updates the map's grid to the new grid. Used after step has been called
	 * @param g the current grid
	 */
	@SuppressWarnings("rawtypes")
	public void update(Grid g)
	{
		this.grid = g;
	}

	/**
	 * adds all locations within a range of two from a User to the map that have not been checked
	 * @param g the grid to check
	 */
	@SuppressWarnings("rawtypes")
	public void add(Grid g)
	{
		for(int i = -2; i < 3; i++)
		{
			for(int j = -2; j < 3; j++)
			{
				if(grid.isValid(new Location(grid.getUser().getLocation().getRow() + i, grid.getUser().getLocation().getCol() + j)) && !this.contains(new Location(grid.getUser().getLocation().getRow() + i, grid.getUser().getLocation().getCol() + j)))
					locsToCheck[grid.getUser().getLocation().getRow() + i][grid.getUser().getLocation().getCol() + j] = (new Location(grid.getUser().getLocation().getRow() + i, grid.getUser().getLocation().getCol() + j));
			}
		}
	}

	/**
	 * returns whether the Location is already in the map
	 * @param l the Location to be checked
	 * @return true if the Location has been checked, false otherwise
	 */
	public boolean contains(Location l)
	{
		for(int i = 0; i < grid.getNumRows(); i++)
		{
			for(int j = 0; j < grid.getNumCols(); j++)
			{
				if(locsToCheck[i][j] != null && locsToCheck[i][j].equals(l))
					return true;
			}
		}
		return false;
	}

	/**
	 * finds the top left corner that has been checked
	 * @return the Location of the top left corner, expressed as an int[]
	 */
	public int[] findSmallest()
	{
		int minRow = grid.getNumRows() - 1;
		int minColumn = grid.getNumCols() - 1;
		for(int i = 0; i < grid.getNumRows(); i++)
		{
			for(int j = 0; j < grid.getNumCols(); j++)
			{
				if(locsToCheck[i][j] != null)
				{
					minRow = Math.min(minRow, i);
					minColumn = Math.min(minColumn, j);
					break;
				}
			}
		}
		return new int[]{minRow,minColumn};
	}

	/**
	 * finds the bottom right corner of the map that has been checked
	 * @return the location of the bottom right corner (row,col)
	 */
	public int[] findLargest()
	{
		int maxRow = 0;
		int maxColumn = 0;
		for(int i = 0; i < grid.getNumRows(); i++)
		{
			for(int j = 0; j < grid.getNumCols(); j++)
			{
				if(locsToCheck[i][j] != null)
				{
					maxRow = Math.max(maxRow, i);
					maxColumn = Math.max(maxColumn, j);
				}
			}
		}
		return new int[]{maxRow,maxColumn};
	}

	/**
	 * sets the range of the visible map for the Map and adds string representations of all Actors that are in the checked range
	 * @param g the grid to be checked
	 */
	@SuppressWarnings("rawtypes")
	public void setBounds(Grid g)
	{
		update(g);
		if(new Location(smallest[0],smallest[1]).compareTo(new Location(findSmallest()[0], findSmallest()[1])) > 1)
		{
			smallest[0] = findSmallest()[0];
			smallest[1] = findSmallest()[1];
		}

		if(new Location(largest[0],largest[1]).compareTo(new Location(findLargest()[0], findLargest()[1])) < 1)
		{
			largest[0] = findLargest()[0];
			largest[1] = findLargest()[1];
		}
		for(int i = 0; i < g.getNumRows(); i++)
		{
			for(int j = 0; j < g.getNumCols(); j++)
			{
				if(i < smallest[0] || i > largest[0] || j < smallest[1] || j > largest[1])
					stringDescription[i][j] = "";
				else
				{
					if(locsToCheck[i][j] == null)
						stringDescription[i][j] = " ";
					else if(g.get(new Location(i,j)) == null)
						stringDescription[i][j] = "_";
					else if(g.get(new Location(i,j)) instanceof Wall && !(g.get(new Location(i,j)) instanceof Stairs))
						stringDescription[i][j] = "W";
					else if(g.get(new Location(i,j)) instanceof User)
						stringDescription[i][j] = "O";
					else if(g.get(new Location(i,j)) instanceof Enemy)
						stringDescription[i][j] = "X";
					else if(g.get(new Location(i,j)) instanceof Treasure)
						stringDescription[i][j] = "*";
					else if(g.get(new Location(i,j)) instanceof Stairs)
						stringDescription[i][j] = "√";
				}
			}
		}
	}

	/**
	 * returns a string representation of the map
	 */
	public String toString()
	{
		String s = "_: empty location   W: wall   O: you   X: enemy   *: treasure   √:stairs" + "\n";
		for(int i = smallest[0]; i <= largest[0]; i++)
		{
			for(int j = smallest[1]; j < largest[1]; j++)
			{
				if(i >= smallest[0] && i <= largest[0] && j >= smallest[1] && j <= largest[1])
				{
					if(!stringDescription[i][j].equals(""))
						s += stringDescription[i][j];

				}
			}
			if(i >= smallest[0] && i <= largest[0])
				s += "\n";
		}
		return s;
	}
}
