package actor;

import grid.Location;
import pathFinder.*;

import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Oh, the classic enemy. Represented as a slime.
 * @author Kendall Garner
 *
 */
public class Enemy extends Player{
	private int health, attack, defense;
	/**the chance of spawning an Enemy*/
	public static final double SPAWNRATE = .55;
	
	/**
	 * creates an Enemy and initializes the stats randomly, multiplied by some factor
	 * @param multFactor the factor by which the stats are increased
	 */
	public Enemy(double multFactor){
		this.health = (int) (((int) (Math.random() * 8) + 10) * multFactor);
		this.attack = (int) (((int) (Math.random() * 3) + 3) * multFactor);
		this.defense = (int)(((int) (Math.random() * 3) + 2) * multFactor);
	}
	
	/**
	 * this finds the User object within a range of five blocks, or a random adjacent empty location if none is found
	 * @return moveLocation the Location the Enemy will move to
	 */
	public Location inRange()
	{
		boolean equals = false;
		if(getGrid() == null)
			return null;
		for(int i = -5; i < 6; i++)
		{
			for(int j = -5; j < 6; j++)
			{				
				if(getGrid().isValid(new Location(getLocation().getRow() + i, getLocation().getCol() + j)))
				{
					if(getGrid().get(new Location(getLocation().getRow() + i, getLocation().getCol() + j)) instanceof User)
					{
						equals = true;
						break;
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
			
		Location moveLocation;
		
		
		if(getGrid().getNeighbors(getLocation()).contains(getGrid().getUser()))
		{
			moveLocation = getGrid().getUser().getLocation();
		}
		else
        {
            PathFinder a = new PathFinder(getGrid(),20);
            Path path = a.findPath(this, this.getGrid().getUser().getLocation().getRow(), this.getGrid().getUser().getLocation().getCol(),true);
            if(path.getLength() != 0)
                moveLocation = a.findPath(this, this.getGrid().getUser().getLocation().getRow(), this.getGrid().getUser().getLocation().getCol(),true).getStep(0);
            else
                moveLocation = null;

        }

		if(moveLocation == null)
		{
			ArrayList<Location> locs = getGrid().getEmptyAdjacentLocations(getLocation());
			if(locs.size() != 0)
				moveLocation = locs.get( (int) (Math.random() * locs.size()));
			else
				moveLocation = getLocation();
		}
		
		return moveLocation;
	}
	
	public void reduce(int damage)
	{
		health -= damage;
		if(health <= 0)
			this.removeSelfFromGrid();
	}
	
	public int getHealth()
	{
		return health;
	}

	public void move(Location l)
	{
		if(l == null || l.equals(getLocation()))
		{
			return;
		}
		if(getGrid().get(l) != null && !(getGrid().get(l) instanceof User))
		{
			Location newLoc = getLocation().getAdjacentLocation(getLocation().getDirectionToward(l) + 45);
			if(getGrid().get(newLoc) != null && !(getGrid().get(newLoc) instanceof User))
			{
				newLoc = this.getLocation().getAdjacentLocation(getLocation().getDirectionToward(l) - 45);
				
				if(getGrid().get(newLoc) != null && !(getGrid().get(newLoc) instanceof User))
				{
					ArrayList<Location> locs = getGrid().getEmptyAdjacentLocations(getLocation());
					if(locs.size() != 0)
					{
						Location finalMove = (locs.get( (int) (Math.random() * locs.size())));
						this.setDirection(getLocation().getDirectionToward(finalMove));
						this.moveTo(finalMove);
					}
				}
				else if(getGrid().get(newLoc) instanceof User)
				{
					setDirection(this.getLocation().getDirectionToward(l));
					this.attack( (User) (getGrid().get(l)));
				}
				else
				{
					setDirection(getLocation().getDirectionToward(newLoc));
					this.moveTo(newLoc);
					ArrayList<Location> locs = getGrid().getOccupiedAdjacentLocations(getLocation());
					for(Location loc: locs)
					{
						if(getGrid().get(loc) instanceof User)
						{
							setDirection(this.getLocation().getDirectionToward(loc));
							this.attack((User) getGrid().get(loc));
							break;
						}
					}
				}
			}
			else if(getGrid().get(newLoc) instanceof User)
			{
				setDirection(this.getLocation().getDirectionToward(l));
				this.attack( (User) (getGrid().get(l)));
			}
			else
			{
				setDirection(getLocation().getDirectionToward(newLoc));
				this.moveTo(newLoc);
			}
			
		}
		else if(getGrid().get(l) instanceof User)
		{
			setDirection(this.getLocation().getDirectionToward(l));
			this.attack( (User) (getGrid().get(l)));
		}
		else 
		{
			setDirection(this.getLocation().getDirectionToward(l));
			this.moveTo(l);
			ArrayList<Location> locs = getGrid().getOccupiedAdjacentLocations(getLocation());
			for(Location loc: locs)
			{
				if(getGrid().get(loc) instanceof User)
				{
					setDirection(this.getLocation().getDirectionToward(loc));

					this.attack((User) getGrid().get(loc));
					break;
				}
			}
		}		
	}

	/**
	 * attacks the User and deals damage equal to its attack minus the User's defense, and does at least one damage or misses if the User's defense is high enougn
	 * @param p the User to attack
	 */
	public void attack(User p)
	{
		int damage = getAttack() - p.getDefense();
		int evade = 0;
		if(damage <= 0)
		{
			evade = p.getDefense() - getAttack();
			damage = 1;
		}
		
		if(Math.random() * 100 < evade * 5)
		{
			JOptionPane.showMessageDialog(null, "Miss");
			return;
		}
		
		p.reduce(damage);
        JOptionPane.showMessageDialog(null,"Enemy attacked you! User's health = " + p.getHealth() + "\n" + "Damage dealt = " + damage);  
	}

	public int getAttack()
	{
		return this.attack;
	}
	
	public int getDefense()
	{
		return this.defense;
	}
	
	public void act()
	{
		Location moveLocation = this.inRange();
		move(moveLocation);
	}
	
	/**
	 * returns if a path can be formed from the Enemy to the User
	 * @return true if a path exists, false otherwise
	 */
	public boolean hasPath()
	{
		PathFinder as = new PathFinder(getGrid(), 625);
    	return (as.findPath(this, getGrid().getUser().getLocation().getRow(), getGrid().getUser().getLocation().getCol(),false).getLength() != 0);

	}
	
	public String toString()
	{
		return "Enemy: health= " + this.health + " attack= " + this.attack + " defense= " + this.defense;
	}
	
	public Location getNextLocation()
	{
		if(this.inRange() != null && !(getGrid().get(this.inRange()) instanceof User))
			return this.inRange();
		else
			return this.getLocation();
	}
}
