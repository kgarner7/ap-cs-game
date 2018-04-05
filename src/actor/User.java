package actor;
import pathFinder.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * YOU!
 * @author Kendall Garner
 */
public class User extends Player{
	ArrayList<Item> inventory = new ArrayList<Item>();
	int health, attack, defense;
	Item[] equipped = new Item[3];

	/**
	 * sets attack and defense to 5, and health to 100
	 */
	public User()
	{
		this.attack = 5;
		this.defense = 5;
		this.health = 100;
	}
	
	/**
	 * adds item i to the equipped inventory
	 * @param i the item to be equipped
	 */
	public void equip(Item i)
	{
		if(i.getType() == 2)
		{
			this.health += i.stat();
			for(int j = 0; j < inventory.size(); j++)
			{
				if(i.getType() == inventory.get(j).getType() && i.stat() == inventory.get(j).stat())
				{
					inventory.remove(j);
					break;
				}
			}
			return;
		}

		for(int j = 0; j < inventory.size(); j++)
		{
			if(i.getType() == inventory.get(j).getType() && i.stat() == inventory.get(j).stat())
			{
				inventory.remove(j);
				break;
			}
		}

		int index = 0;
		for(int j = 0; j < 3; j++)
		{
			if(equipped[j] == null)
			{
				index = j;
				break;
			}
			else if(j == 2 && equipped[j] != null)
			{
				index = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter index of item to be replaced")) - 1;
				equipped[index].remove();
				if(equipped[index].getType() == 0)
					this.attack -= equipped[index].stat();
				else
					this.defense -= equipped[index].stat();
				inventory.add(equipped[index]);
			}
		}

		i.equip();
		this.equipped[index] = i;

		if(i.getType() == 0)
			this.attack += i.stat();
		else
			this.defense += i.stat();
	}

	/**
	 * gets the attack stat
	 * @return attack the User's attack
	 */
	public int getAttack()
	{
		return attack;
	}

	/**
	 * gets the defense stat
	 * @return defense the User's defense
	 */
	public int getDefense()
	{
		return defense;
	}

	/**
	 * checks if the Location in direction is available
	 * @param direction the direction to check
	 * @return true if it is valid and is either empty, a Player, or a Treasure; false otherwise
	 */
	public boolean canMove(int direction)
	{
		return (getGrid().isValid(getLocation().getAdjacentLocation(direction)) && !(getGrid().get(getLocation().getAdjacentLocation(direction)) instanceof Wall));			
	}


	/**
	 * adds t's item to inventory and removes t from the grid
	 * @param t the Treasure to be opened
	 */
	public void open(Treasure t)
	{
		Item i = t.getItem();
		i.remove();
		inventory.add(i);
		t.removeSelfFromGrid();
		JOptionPane.showMessageDialog(null, "You have gained a: " + i.toString());
	}

	/**
	 * attacks another player and deals damage equal to the attack minus the other player's defense, or misses if the difference is high enough
	 * @param p the Player to be attacked
	 */
	public void attack(Player p)
	{
		int damage = this.attack - p.getDefense();
		if(damage <= 0)
		{
			damage = 1;
		}
		p.reduce(damage);
   
        int health = p.getHealth();
        if(health < 0)
        	health = 0;
		JOptionPane.showMessageDialog(null, "User attacked. " + "Enemy Player's health = " + health + "\n" + "Damage dealt = " + damage); 
	}

	/**
	 * reduces health by damage
	 * @param damage the damage dealt
	 */
	public void reduce(int damage)
	{
        this.health -= damage;
		if(this.health <= 0)
		{
			this.removeSelfFromGrid();
			JOptionPane.showMessageDialog(null, "You have lost");
		}
	}

	/**
	 * checks if it can move to direction, and acts accordingly
	 * @param direction the direction to move
	 */
	public void move(int direction)
	{
		if(this.canMove(direction))
		{
			if(getGrid().get(getLocation().getAdjacentLocation(direction)) == null)
			{
				this.moveTo(getLocation().getAdjacentLocation(direction));
				setDirection(direction);
			}
			else if(getGrid().get(getLocation().getAdjacentLocation(direction)) instanceof Player)
			{				
				setDirection(direction);
				this.attack((Player) getGrid().get(getLocation().getAdjacentLocation(direction)));
			}
			else if(getGrid().get(getLocation().getAdjacentLocation(direction)) instanceof Treasure)
			{
				this.open((Treasure) getGrid().get(getLocation().getAdjacentLocation(direction)));
				setDirection(direction);
			}
		}
	}

	/**
	 * gets the Items not equipped by the User
	 * @return inventory the Items not equipped by the User
	 */
	public ArrayList<Item> getInventory()
	{
		return this.inventory;
	}
	
	/**
	 * gets the equipped item slots of the User
	 * @return equipped the equipment slot
	 */
	public Item[] getEquipped()
	{
		return equipped;
	}

	public int getHealth()
	{
		return this.health;
	}

	/**
	 * returns a string that tells the Users stats
	 * @return a string with the User's health, attack, and defense
	 */
	public String toString()
	{
		return "You with: " + health + " health, " + attack + " attack, " + defense + " defense"; 
	}
	
	/**
	 * finds if there is a possible path to the stairs
	 * @return true if a path can be made, false otherwise
	 */
	public boolean hasPath()
	{
		PathFinder as = new PathFinder(getGrid(), 625);
    	Path p = (as.findPath(this, getGrid().getStairs().getLocation().getRow(), getGrid().getStairs().getLocation().getCol(),false)); //!= null);
    	return (p.getLength() != 0);
	}
}
