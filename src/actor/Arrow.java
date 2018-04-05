package actor;

import grid.Grid;
import grid.Location;

import javax.swing.JOptionPane;

public class Arrow extends Player{
	private int attack;

	public Arrow(int direction, int attack)
	{
		this.setDirection(direction);
		this.attack = attack;
	}

	public void act()
	{
		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException ignored)
		{
			
		}
		if(getGrid().get(getLocation().getAdjacentLocation(getDirection())) == null)
		{
			this.moveTo(getLocation().getAdjacentLocation(getDirection()));
			JOptionPane.showMessageDialog(null, "moved to " + getLocation());
		}
		else if(getGrid().get(getLocation().getAdjacentLocation(getDirection())) instanceof Enemy)
			this.attack((Enemy)getGrid().get(getLocation().getAdjacentLocation(getDirection())));
		else
		{
			this.removeSelfFromGrid();
			JOptionPane.showMessageDialog(null, "Ran into object");
		}
	}

	public void putSelfInGrid(Grid<Actor> grid, Location l)
	{
		if(getGrid() != null)
			throw new IllegalStateException("The actor is already contained in the grid");
		
		if(grid.get(l) != null)
		{
			if(grid.get(l) instanceof Enemy)
				this.attack((Enemy)grid.get(l));
		}
		else
		{
			super.putSelfInGrid(grid, l);
		}
	}
	
	public boolean objectInRange()
	{
		if(getGrid() == null)
			return false;
		else if(getGrid().get(getLocation().getAdjacentLocation(getDirection())) == null || getGrid().get(getLocation().getAdjacentLocation(getDirection())) instanceof Enemy)
			return true;
		else
			return false;
	}
	
	public void attack(Enemy p)
	{
		int damage = attack - p.getDefense();
		int evade = 0;
		if(damage <= 0)
		{
			damage = 1;
			evade = p.getDefense() - attack;
		}

		if(((int)Math.random() * evade * 5) != 0)
			JOptionPane.showMessageDialog(null, "Miss");
		else
		{
			p.reduce(damage);
			JOptionPane.showMessageDialog(null, "Arrow hit enemy and did " + damage + " damage. Enemy's health: " + p.getHealth());
		}
		if(getGrid() != null)
			this.removeSelfFromGrid();
	}
}
