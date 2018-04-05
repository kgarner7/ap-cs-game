package actor;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import grid.Location;

/**
 * The Player class is used as a super class to the User and the Enemy classes
 * @author Kendall Garner
 *
 */
public class Player extends Actor{
	int health, attack, defense;
	
	/**
	 * sets health to be a random number between 10 and 18, exclusive
	 */
	public Player()
	{
		health = 10 + (int) (Math.random() * 8);
		attack = 2 + (int) (Math.random() * 5);
		defense = 2 + (int) (Math.random() * 5);
	}

	/**
	 * @return attack the Player's attack stat
	 */
	public int getAttack()
	{
		return attack;
	}
	
	/**
	 * @return defense the Player's defense stat
	 */
	public int getDefense()
	{
		return defense;
	}
	
	/**
	 * @return health the Player's health
	 */
	public int getHealth()
	{
		return health;
	}
	
	/**
	 * this finds another Player object within a range of five blocks, or a random adjacent empty location if none is found
	 * @return moveLocation the Location the Player will move to
	 */
	public Location inRange()
	{
		Location moveLocation = null;
		for(int i = -5; i < 6; i++)
		{
			for(int j = -5; j < 6; j++)
			{				
				if(getGrid().isValid(new Location(getLocation().getRow() + i, getLocation().getCol() + j)))
				{
					if(getGrid().get(new Location(getLocation().getRow() + i, getLocation().getCol() + j)) instanceof Player && (i != 0 && j != 0))
					{						
						int direction = getLocation().getDirectionToward(new Location(getLocation().getRow() + i, getLocation().getCol() + j));
						moveLocation = this.getLocation().getAdjacentLocation(direction);
					}
				}
				
			}
		}
		if(moveLocation == null)
		{
			ArrayList<Location> locs = getGrid().getEmptyAdjacentLocations(getLocation());
			moveLocation = locs.get( (int) (Math.random() * locs.size()));
		}
		
		for(Location l: this.getGrid().getOccupiedAdjacentLocations(getLocation()))
		{
			if(getGrid().get(l) instanceof Player)
			{
				moveLocation = l;
			}
		}
		return moveLocation;
	}
	
	/**
	 * moves the Player to location l, or if l is empty, turns 45 degrees, and if that is empty, it turns -45 degrees
	 * @param l the location to move to
	 */
	public void move(Location l)
	{
		if(getGrid().get(l) != null && !(getGrid().get(l) instanceof Player))
		{
			Location newLoc = getLocation().getAdjacentLocation(getLocation().getDirectionToward(l) + 45);
			if(getGrid().get(newLoc) != null && !(getGrid().get(newLoc) instanceof Player))
			{
				newLoc = this.getLocation().getAdjacentLocation(getLocation().getDirectionToward(l) - 45);
			}
			else if(getGrid().get(newLoc) instanceof Player)
			{
				setDirection(this.getLocation().getDirectionToward(l));
				this.attack( (Player) (getGrid().get(l)));
			}
			
			if(getGrid().get(newLoc) != null && !(getGrid().get(newLoc) instanceof Player))
			{
				ArrayList<Location> locs = getGrid().getEmptyAdjacentLocations(getLocation());
				if(locs.size() != 0)
				{
					this.move(locs.get( (int) (Math.random() * locs.size())));
				}
			}
			else if(getGrid().get(newLoc) instanceof Player)
			{
				setDirection(this.getLocation().getDirectionToward(l));
				this.attack( (Player) (getGrid().get(l)));
			}
			else
			{
				setDirection(getLocation().getDirectionToward(newLoc));
				this.moveTo(newLoc);
			}
		}
		else if(getGrid().get(l) instanceof Player)
		{
			setDirection(this.getLocation().getDirectionToward(l));
			this.attack( (Player) (getGrid().get(l)));
		}
		else 
		{
			setDirection(this.getLocation().getDirectionToward(l));
			this.moveTo(l);
		}
		
	}

	/**
	 * reduces the health of the Player by damage
	 * @param damage the damage the Player takes
	 */
	public void reduce(int damage)
	{
		health -= damage;
		if(health <= 0)
			this.removeSelfFromGrid();
	}
	
	/**
	 * deals damage to p equal to the value of this.attack minus p.getDefense
	 * @param p the Player to be attacked
	 */
	public void attack(Player p)
	{
		int damage = getAttack() - p.getDefense();
		if(damage <= 0)
			damage = 0;
		p.reduce(damage);
		JOptionPane.showMessageDialog(null, "Player's health = " + p.getHealth() + "\n" + "Damage dealt = " + damage); 
		
	}
	
	/**
	 * selects the next location and moves to the Location if available
	 */
	public void act()
	{
		Location moveLocation = this.inRange();
		move(moveLocation);
	}
}
