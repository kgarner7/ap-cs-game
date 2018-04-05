package actor;

import grid.*;
import pathFinder.PathFinder;

import java.util.ArrayList;

/**
 * A subset of the Enemy class. Attacks from a distance.
 * @author Kendall Garner
 *
 */
public class RangedEnemy extends Enemy{
	private int health, attack, defense;

    /**
	 * sets the health, attack, and defense stats to a random number in range, multiplied by the factor
	 * @param multFactor the factor to increase the stats by
	 */
	public RangedEnemy(double multFactor) 
	{
		super(multFactor);
		health = (int) (((int) (Math.random() * 5) + 5) * multFactor);
		attack = (int) (((int) (Math.random() * 3) + 2) * multFactor);
		defense = (int) (((int) (Math.random() * 3) + 1) * multFactor);
	}

	public int getAttack()
	{
		return attack;
	}

	public int getDefense()
	{
		return defense;
	}

	public int getHealth()
	{
		return health;
	}

	public void reduce(int damage)
	{
		health -= damage;
		if(health <= 0)
			this.removeSelfFromGrid();
	}

	/**
	 * finds if the User is in a range of five steps, and if a path can be formed and finds the next Location of the Path
	 * @return the next step in the Path, a random empty adjacent if no path can be formed, or a Location directly opposite the User if the user is a neighbor
	 */
	public Location inRange()
	{
		if(getGrid() == null)
			return null;
		boolean equals = false;
		for(int i = -5; i < 6; i++)
		{
			for(int j = -5; j < 6; j++)
			{				
				if(getGrid().isValid(new Location(getLocation().getRow() + i, getLocation().getCol() + j)))
				{
					if(getGrid().get(new Location(getLocation().getRow() + i, getLocation().getCol() + j)) instanceof User)
					{
						equals = true;
					}
				}

			}
		}
		if(!equals)
		{
			ArrayList<Location> locs = getGrid().getEmptyAdjacentLocations(getLocation());
			if(locs.size() != 0)
				return locs.get( (int) (Math.random() * locs.size()));
			else
				return getLocation();
		}
		
		PathFinder a = new PathFinder(getGrid(),20);
		boolean hasLocation = a.findPath(this, this.getGrid().getUser().getLocation().getRow(), this.getGrid().getUser().getLocation().getCol(),true) != null && a.findPath(this, this.getGrid().getUser().getLocation().getRow(), this.getGrid().getUser().getLocation().getCol(),true).getLength() >= 3;
		Location moveLocation = null;

		if(hasLocation)
			moveLocation = a.findPath(this, this.getGrid().getUser().getLocation().getRow(), this.getGrid().getUser().getLocation().getCol(),true).getStep(0);


		for(Actor actor: getGrid().getNeighbors(getLocation()))
		{
			if(actor instanceof User)
			{
				moveLocation = this.getLocationsInDirections();
				break;
			}
		}

		if(moveLocation == null)
		{
			ArrayList<Location> locs = getGrid().getEmptyAdjacentLocations(getLocation());
			if(locs.size() != 0)
				moveLocation = locs.get( (int) (Math.random() * locs.size()));
			else
				moveLocation = null;
		}

		return moveLocation;
	}

	/**
	 * moves to the Location, and attacks the User if in range of two blocks
	 * @param moveLocation the location to move to
	 */
	public void move(Location moveLocation)
	{
		if(getGrid() == null)
			return;
		if(moveLocation != null)
		{
			if(moveLocation.equals(getLocation()))
			{
				if(getGrid().getNeighbors(getLocation()).contains(getGrid().getUser()))
					this.attack(getGrid().getUser());
				return;
			}
			else if(getGrid().get(moveLocation) != null)
			{
				if(getGrid().getNeighbors(getLocation()).contains(getGrid().getUser()))
					this.attack(getGrid().getUser());
				return;
			}
			
			this.moveTo(moveLocation);

			boolean userInRange = false;
			for(int row = -2; row < 3; row ++)
			{
				for(int col = -2; col < 3; col++)
				{
					if(getGrid().isValid(new Location(getLocation().getRow() + row, getLocation().getCol() + col)))
					{
						if(getGrid().get(new Location(getLocation().getRow() + row,getLocation().getCol() + col)) instanceof User)
						{
							if(getGrid().get(getLocation().getAdjacentLocation(getLocation().getDirectionToward(getGrid().getUser().getLocation()))) == null || getGrid().get(getLocation().getAdjacentLocation(getLocation().getDirectionToward(getGrid().getUser().getLocation()))) instanceof Enemy || getGrid().get(getLocation().getAdjacentLocation(getLocation().getDirectionToward(getGrid().getUser().getLocation()))) instanceof User)
								userInRange = true;
						}
					}
				}
			}
			if(userInRange)
			{
				attack(getGrid().getUser());
			}
		}
		else
		{
			if(getGrid().getNeighbors(getLocation()).contains(getGrid().getUser()))
				attack(getGrid().getUser());
		}
	}

	/**
	 * returns the next Location the RangedEnemy would move, or the current Location if there is no Location available
	 */
	public Location getNextLocation()
	{
		if(this.inRange() != null)
			return this.inRange();
		else
			return getLocation();
	}
	
	/**
	 * returns the optimal Location opposite the User
	 * @return the Location that best is away from the User
	 */
	public Location getLocationsInDirections()
	{
		Location moveLocation = null;
		int count = 0;
		while(moveLocation == null)
		{
			int direction = 180 + getDirection() + 45 * count;
			if(direction > 360)
				direction -= 360;
			
			if(getGrid().get(getLocation().getAdjacentLocation(direction)) == null)
			{
				moveLocation = getLocation().getAdjacentLocation(direction);
				break;
			}
			
			if(count == 0)
				count = 1;
			else if(count > 0)
				count *= -1;
			else if(count != -4)
			{
				count -= 1;
				count *= -1;
			}
			
			if(count == -4)
				break;
		}
		
		return moveLocation;
		
	}
	
	public String toString()
	{
		return "Ranged Enemy: health= " + this.health + " attack= " + this.attack + " defense= " + this.defense;
	}
}
