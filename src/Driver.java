import actor.*;
import grid.BoundedGrid;
import grid.Location;
import world.World;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class Driver {

	public static void main(String[] args)
	{
		makeWorld();
	}

	/**
	 * Creates a ActorWorld with factor input by the user and constantly updates the time, health, and turns and checks if the player has won or lost
	 */
	public static void makeWorld() {
		String option;
		double factor = 0;
		do
		{
			option = JOptionPane.showInputDialog(null,"Enter Mode: Easy, Medium, Hard");
			if(option != null)
			{
                if(option.equalsIgnoreCase("Rena"))
                    factor = 2.25;
				else if(option.equalsIgnoreCase("easy"))
					factor = .5;
				else if(option.equalsIgnoreCase("medium"))
					factor = 1.25;
				else if(option.equalsIgnoreCase("hard"))
					factor = 1.5;
				else if(option.equalsIgnoreCase("pi"))
					factor = 3.14;
				else if (option.equalsIgnoreCase("die"))
					factor = 5;
				else if(option.equalsIgnoreCase("exit"))
					System.exit(0);
				else if(option.equals("blank"))
				{
					factor = -1;
				}
				else
					option = null;
			}
		}while(option == null);
		
		if(factor == -1) {
			ActorWorld world = new ActorWorld(new BoundedGrid<>(25,25),1);
			world.add(new User());
			world.add(new Stairs());
			world.add(new Enemy(0));
			world.show();
			return;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ActorWorld world = new ActorWorld(new BoundedGrid(25,25), factor);
		makeEdge(world);
		placeActors(world, 5, factor);
		placeWalls(world);
		world.show();

        ETimer timer = new ETimer(new MyActionListener(world));
        timer.start();
	}


	/**
	 * adds an edge to world 2 blocks thick
	 * @param world the world to be accessed
	 */
	private static void makeEdge(ActorWorld world)
	{
		for(int i = 0; i < world.getGrid().getNumRows(); i++)
		{
			for(int j = 0; j < world.getGrid().getNumRows(); j++)
			{
				if(i < 2 || i > world.getGrid().getNumRows() - 3)
					world.add(new Location(i,j), new Wall());
				else if(j < 2 || j > world.getGrid().getNumRows() - 3)
					world.add(new Location(i,j), new Wall());
			}
		}
	}

	/**
	 * places a set number of Enemies and Treasures and then the Stairs and User
	 * @param world the world to contain the Actors
	 * @param setNumber the number of Enemies and Tresures to be placed
	 */
	private static void placeActors(ActorWorld world, int setNumber, double factor)
	{
		for(int i = 0; i < setNumber; i++)
		{
			if(Math.random() < Enemy.SPAWNRATE)
				world.add(new Enemy(factor));
			else
				world.add(new RangedEnemy(factor));
		}
		
		for(int i = 0; i < setNumber * factor; i++)
			world.add(new Treasure());

		world.add(new Stairs());
		world.add(new User());
	}

	/**
	 * returns true if every actor (that matters) has a path
	 * @param world the world to be checked
	 * @return true if the User, Enemies, and Treasures have paths, false otherwise
	 */
	private static boolean hasPath(ActorWorld world)
	{
		ArrayList<Actor> actors = new ArrayList<>();
		for(Location l: world.getGrid().getOccupiedLocations())
		{
			if (world.getGrid().get(l) instanceof User || world.getGrid().get(l) instanceof Enemy || world.getGrid().get(l) instanceof Treasure)
			{
				actors.add(world.getGrid().get(l));
			}
		}
		boolean hasPath = true;
		for(Actor a: actors)
		{
			if((a instanceof User && !((User)a).hasPath()) ||(a instanceof Enemy && !((Enemy)a).hasPath()) || (a instanceof Treasure && !((Treasure)a).hasPath()))
			{
				hasPath = false;
				break;
			}

		}
		return hasPath;
	}

	/**
	 * places walls in the world while the world has a path and the set number of walls placed has not exceeded the limit
	 * @param world the world to place the walls
	 */
	private static void placeWalls(ActorWorld world)
	{
		boolean canContinue = true;
		int failCount = 0;
		int wallCount = 0;
		while(canContinue)
		{
			Wall w = new Wall();
			world.add(w);
			if(!hasPath(world))
			{
				w.removeSelfFromGrid();
				failCount++;
			}		
			else
			{
				failCount = 0;
				wallCount++;
				
			}
            if(failCount >= 5 || wallCount >= 400)
				canContinue = false;
		}
	}

    public static class ETimer extends Timer
    {

        public ETimer(MyActionListener m) {
            super(1000, m);

        }

        @Override
        protected void fireActionPerformed(ActionEvent e) {
            super.fireActionPerformed(e);
            if(((MyActionListener)getActionListeners()[0]).done())
                stop();
        }
    }

    public static class MyActionListener implements ActionListener
    {
        private World world;


        public MyActionListener(World world)
        {
            super();
            this.world = world;
        }

        public void actionPerformed(ActionEvent e)
        {
            world.incrementTime();


            if(world.getFrame().getGUI().gameOver()) {
                //System.out.println("OK");
                world.getFrame().dispose();
                world = null;
                makeWorld();
            }
        }

        public boolean done()
        {
            return world == null;
        }
    }
}