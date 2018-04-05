package pathFinder;
import java.util.ArrayList;
import java.util.Collections;

import actor.*;
import grid.*;

public class PathFinder 
{
	private ArrayList<Node> open = new ArrayList<Node>();
	private ArrayList<Node> closed = new ArrayList<Node>();
	private Grid<?> grid;
	private int maxSteps;
	private Node[][] nodes;
	
	/**
	 * creates a pathfinder object
	 * @param grid the grid the pathfinder is checking
	 * @param maxDistance the max amount of steps the pathfinder will check
	 */
	public PathFinder(Grid<?> grid, int maxDistance)
	{
		this.grid = grid;
		this.maxSteps = maxDistance;
		nodes = new Node[grid.getNumCols()][grid.getNumCols()];
		for(int row = 0; row < grid.getNumRows(); row++)
		{
			for(int col = 0; col < grid.getNumCols(); col++)
			{
				nodes[row][col] = new Node(row,col);
				nodes[row][col].steps = Integer.MAX_VALUE;
			}
		}
	}

	/**
	 * finds the best path from player's location to the goal location
	 * @param player the player to check
	 * @param goalRow the target row
	 * @param goalCol the target col
	 * @param allowDiagonal true if diagonal moves are allowed
	 * @return the best path to the target consisting of up to maxDistance steps, or null if no path is found
	 */
	public Path findPath(Actor player, int goalRow, int goalCol, boolean allowDiagonal)
	{
		int startRow = player.getLocation().getRow();
		int startCol = player.getLocation().getCol();
		nodes[startRow][startCol].steps = 0;
		nodes[startRow][startCol].cost = 0;
		open.clear();
		closed.clear();
		addToOpen(new Node(startRow,startCol));

		nodes[goalRow][goalCol].previousLocation = null;
		
		int maxStepsTaken = 0;
		while(maxStepsTaken < maxSteps && (open.size() != 0))
		{
			Node current = open.get(0);
			open.remove(current);
			closed.add(current);
			
			for(int dRow = -1; dRow < 2; dRow++)
			{
				for(int dCol = -1; dCol < 2; dCol++)
				{
					if((dRow == 0) && (dCol == 0))
					{
						continue;
					}
					
					if(!allowDiagonal && ((dRow != 0) && (dCol != 0)))
						continue;
					
					int nRow = current.location.getRow() + dRow;
					int nCol = current.location.getCol() + dCol;
					
					if(isValid(player,startRow,startCol,nRow,nCol))
					{
						double nextStepCost = current.cost + 1;
						Node neighbor = nodes[nRow][nCol];
						
						if(nextStepCost < neighbor.cost)
						{
							if(open.contains(neighbor))
								open.remove(neighbor);
							if(closed.contains(neighbor))
								closed.remove(neighbor);
						}
						
						if(!open.contains(neighbor) && !closed.contains(neighbor))
						{
							neighbor.cost = nextStepCost;
							neighbor.relativeCost = this.getHeuristic(nRow,nCol,goalRow);
							maxStepsTaken = Math.max(maxStepsTaken, neighbor.setPrevious(current));
							addToOpen(neighbor);
						}
					}
				}
			}
		}
		
		if(nodes[goalRow][goalCol].previousLocation == null)
		{
			return new Path();
		}
		
		Path p = new Path();
		Node target = nodes[goalRow][goalCol];
		while(target.previousLocation != null)
		{
			p.addStep(0, target.location.getRow(), target.location.getCol());
			target = target.previousLocation;
		}
		return p;
	}
	
	/**
	 * returns whether the specified location is valid for the specified actor
	 * @param player the actor that is trying to move
	 * @param currentRow the actor's row
	 * @param currentCol the actor's col
	 * @param targetRow the row to check
	 * @param targetCol the col to check
	 * @return true if that location is valid and doesn't consist of a certain object
	 */
	public boolean isValid(Actor player, int currentRow, int currentCol, int targetRow, int targetCol)
	{
		boolean invalid = targetRow < 0 || targetRow >= grid.getNumRows() || targetCol < 0 || targetCol >= grid.getNumCols();
		if(!invalid && (currentRow != targetRow || currentCol != targetCol))
		{
			invalid = ((player instanceof User || player instanceof Treasure) && grid.get(new Location(targetRow, targetCol)) instanceof Wall && !(grid.get(new Location(targetRow, targetCol)) instanceof Stairs)) || (player instanceof Enemy && grid.get(new Location(targetRow, targetCol)) instanceof Wall);
		}
		return !invalid;
	}
	
	/**
	 * adds a node to the open list and shuffles it
	 * @param n the node to be added
	 */
	public void addToOpen(Node n)
	{
		open.add(n);
		Collections.sort(open);
	}
	
	/**
	 * gets the heuristic cost of the move
	 * @param currentRow the start row
	 * @param currentCol the start col
	 * @param goalRow the target row
	 * @return cost determined by distance formula
	 */
	public double getHeuristic(int currentRow, int currentCol, int goalRow)
	{
		return Math.sqrt(Math.pow(currentRow - goalRow,2) + Math.pow(currentCol - goalRow, 2));
	}
	
	private class Node implements Comparable<Object>
	{
		private Location location;
		private double cost;
		private double relativeCost;
		private Node previousLocation;
		private int steps;
		
		/**
		 * creates a Node at row,col
		 * @param row the row the Node represents
		 * @param col the col the Node represents
		 */
		public Node(int row, int col)
		{
			location = new Location(row,col);
		}
		
		/**
		 * sets the previous Node to previousLocation
		 * @param previousLocation the previous Node that was used to reach the current Node
		 * @return the new amount of steps
		 */
		public int setPrevious(Node previousLocation)
		{
			this.steps = previousLocation.steps + 1;
			this.previousLocation = previousLocation;
			return steps;
		}
		
		/**
		 * compares two nodes' heuristic costs 
		 */
		public int compareTo(Object o)
		{
			Node other = (Node) o;			
			double f = relativeCost + cost;
			double of = other.relativeCost + other.cost;

			if (f < of) 
			{
				return -1;
			} 
			else if (f > of) 
			{
				return 1;
			} 
			else 
			{
				return 0;
			}
		}
		
	}
}