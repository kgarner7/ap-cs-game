/* 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2002-2006 College Entrance Examination Board 
 * (http://www.collegeboard.com).
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @author Julie Zelenski
 * @author Cay Horstmann
 */

package gui;

import actor.*;
import grid.Grid;
import grid.Location;
import world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The GUIController controls the behavior in a WorldFrame. <br />
 * This code is not tested on the AP CS A and AB exams. It contains GUI
 * implementation details that are not intended to be understood by AP CS
 * students.
 */

public class GUIController<T>
{
	//public static final int INDEFINITE = 0, FIXED_STEPS = 1, PROMPT_STEPS = 2;

	private static final int MIN_DELAY_MSECS = 10, MAX_DELAY_MSECS = 1000;
	@SuppressWarnings("unused")
	private static final int INITIAL_DELAY = MIN_DELAY_MSECS
	+ (MAX_DELAY_MSECS - MIN_DELAY_MSECS) / 2;

	private JButton invetoryButton, mapButton;
	private JComponent controlPanel;
	private GridPanel display;
	private WorldFrame<T> parentFrame;
	private boolean upPushed = false, downPushed = false, leftPushed = false, rightPushed = false, spacePressed = false, gameOver = false, isAdmin = false, beginInput = false;
	@SuppressWarnings("unused")
	private int count = 0, steps = 0, numStepsSoFar = 0, inputCount = 0;
	private ResourceBundle resources;
	private DisplayMap displayMap;
	@SuppressWarnings("rawtypes")
	private Set<Class> occupantClasses;
	private Map m;
	private double multFactor = 1, factor = 0;

	/**
	 * Creates a new controller tied to the specified display and gui
	 * frame.
	 * @param parent the frame for the world window
	 * @param disp the panel that displays the grid
	 * @param displayMap the map for occupant displays
	 * @param res the resource bundle for message display
	 * @param factor the difficulty factor for the current run
	 */
	@SuppressWarnings("rawtypes")
	public GUIController(WorldFrame<T> parent, GridPanel disp,
			DisplayMap displayMap, ResourceBundle res, double factor)
	{
		this.factor = factor;
		resources = res;
		display = disp;
		parentFrame = parent;
		this.displayMap = displayMap;
		makeControls();

		occupantClasses = new TreeSet<Class>(new Comparator<Class>()
				{
			public int compare(Class a, Class b)
			{
				return a.getName().compareTo(b.getName());
			}
				});

		World<T> world = parentFrame.getWorld();
		Grid<T> gr = world.getGrid();
		for (Location loc : gr.getOccupiedLocations())
			addOccupant(gr.get(loc));
		for (String name : world.getOccupantClasses())
			try
		{
				occupantClasses.add(Class.forName(name));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		display.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				Grid<T> gr = parentFrame.getWorld().getGrid();
				Location loc = display.locationForPoint(evt.getPoint());
				if (loc != null && gr.isValid(loc))
				{
					display.setCurrentLocation(loc);
					locationClicked();
				}
			}
		});



		m = new Map(parentFrame.getWorld().getGrid());

	}

	/**
	 * Advances the world one step, updates the map, and adds actors or changes the dificulty level as needed
	 */
	@SuppressWarnings("unchecked")
	public void step()
	{
		numStepsSoFar++;

		ArrayList<Actor> actors = new ArrayList<Actor>();
		for(Location locs: parentFrame.getWorld().getGrid().getOccupiedLocations())
			actors.add((Actor)parentFrame.getWorld().getGrid().get(locs));

		for(Actor actor: actors)
		{
			if(parentFrame.getWorld().getGrid().getUser() == null)
				return;
			
			display.setCurrentLocation(actor.getNextLocation());
			if(!(actor instanceof User))
				actor.act();

			parentFrame.getWorld().setMessage("Health: " + parentFrame.getWorld().getGrid().getUser().getHealth() + " Turns: " + this.numStepsSoFar);

		}

		int occupied = 0;
		for(Location l: parentFrame.getWorld().getGrid().getOccupiedLocations())
		{
			if(parentFrame.getWorld().getGrid().get(l) instanceof Enemy)	
				occupied++;
		}

		if((numStepsSoFar % 50 == 0 && numStepsSoFar != 0))
			multFactor += .25 * factor;

		if(numStepsSoFar %((int)(20/factor)) == 0 && occupied < 25)
		{
			if(Math.random() < Enemy.SPAWNRATE)
			{
				Enemy e = new Enemy(multFactor);
				Location l = parentFrame.getWorld().getRandomEmptyLocation();
				parentFrame.getWorld().add(l,(T) e);
				int failCount = 0;
				while(!e.hasPath() || failCount < 10)
				{
					e.removeSelfFromGrid();
					parentFrame.getWorld().add(parentFrame.getWorld().getRandomEmptyLocation(), (T) e);
					failCount++;
				}
			}
			else
			{
				RangedEnemy e = new RangedEnemy(multFactor);
				Location l =parentFrame.getWorld().getRandomEmptyLocation();
				parentFrame.getWorld().add(l,(T) e);
				int failCount = 0;
				while(!e.hasPath() || failCount < 10)
				{
					e.removeSelfFromGrid();
					parentFrame.getWorld().add(parentFrame.getWorld().getRandomEmptyLocation(), (T) e);
					failCount++;
				}
			}

		}

		Grid<T> gr = parentFrame.getWorld().getGrid();

		for (Location loc : gr.getOccupiedLocations())
			addOccupant(gr.get(loc));

		if(parentFrame.getWorld().getGrid().getUser() != null)
		{
			m.update(parentFrame.getWorld().getGrid());
			m.add(parentFrame.getWorld().getGrid());
			m.setBounds(parentFrame.getWorld().getGrid());
		}
	}


	@SuppressWarnings("rawtypes")
	private void addOccupant(T occupant)
	{
		Class cl = occupant.getClass();
		do
		{
			if ((cl.getModifiers() & Modifier.ABSTRACT) == 0)
				occupantClasses.add(cl);
			cl = cl.getSuperclass();
		}
		while (cl != Object.class);
	}


	/**
	 * Builds the panel with the various controls (buttons and
	 * keylistener).
	 */
	private void makeControls()
	{
		controlPanel = new JPanel();
		invetoryButton = new JButton(resources.getString("button.gui.inventory"));
		mapButton = new JButton(resources.getString("button.gui.map"));

		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.setBorder(BorderFactory.createEtchedBorder());

		Dimension spacer = new Dimension(5, invetoryButton.getPreferredSize().height + 10);

		controlPanel.add(invetoryButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(mapButton);

		invetoryButton.setEnabled(true);
		mapButton.setEnabled(true);

		invetoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User u = parentFrame.getWorld().getGrid().getUser();
				int dex = 1;
				int nDex = 1;
				String s = "Equipped" + "\n";
				for (int i = 0; i < parentFrame.getWorld().getGrid().getUser().getEquipped().length; i++) {
					if (parentFrame.getWorld().getGrid().getUser().getEquipped()[i] != null) {
						s += dex + ":" + parentFrame.getWorld().getGrid().getUser().getEquipped()[i].toString() + "\n";
						dex++;
					}
				}
				s += "Inventory" + "\n";
				for (Item i : u.getInventory()) {
					s += nDex + ":" + i.toString() + "\n";
					nDex++;
				}
				boolean correct = false;
				while (!correct) {
					JTextArea textArea = new JTextArea(s + "\n" + "Enter index of item to be added, or 0 to exit");
					JScrollPane scroll = new JScrollPane(textArea);
					textArea.setLineWrap(true);
					textArea.setWrapStyleWord(true);
					scroll.setPreferredSize(new Dimension(300, 300));
					String index = JOptionPane.showInputDialog(null, scroll, "Inventory", JOptionPane.YES_NO_OPTION);
					if (index == null || index.equals("0"))

						break;
					if (Integer.parseInt(index) - 1 < u.getInventory().size() && Integer.parseInt(index) > 0) {
						u.equip(u.getInventory().get(Integer.parseInt(index) - 1));
						correct = true;
					}
				}
				invetoryButton.setEnabled(false);
				invetoryButton.setEnabled(true);
			}
		});
		
		mapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JTextArea textArea = new JTextArea(m.toString());
				JScrollPane scroll = new JScrollPane(textArea);
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				scroll.setPreferredSize(new Dimension(500,500));
				JOptionPane.showMessageDialog(null,scroll,"Map",JOptionPane.YES_NO_OPTION);
				mapButton.setEnabled(false);
				mapButton.setEnabled(true);
			}
		});

		/**
		 * used to move the User and advance the grid
		 * @author Kendall Garner
		 *
		 */
		class MyKeyListener extends KeyAdapter
		{
			/**
			 * sets up the direction
			 * @param e the key pressed
			 */
			public void keyPressed(KeyEvent e)
			{
				int code = e.getKeyCode();
				if (beginInput){
					boolean fail = false;
					switch (code){
						case KeyEvent.VK_UP:
							if (inputCount > 1)
								fail = true;
							break;
						case KeyEvent.VK_DOWN:
							if (inputCount < 2 || inputCount > 3)
								fail = true;
							break;
						case KeyEvent.VK_LEFT:
							if (!(inputCount == 4 || inputCount == 6))
								fail = true;
							break;
						case KeyEvent.VK_RIGHT:
							if (!(inputCount == 5 || inputCount == 7))
								fail = true;
							break;
						case KeyEvent.VK_B:
							if (inputCount != 8)
								fail = true;
							break;
						case KeyEvent.VK_A:
							if (inputCount != 9)
								fail = true;
							else {
								isAdmin = true;
								beginInput = false;
								inputCount = 0;
								System.out.println("admin");
								return;
							}
							break;
					}

					if (fail)
					{
						isAdmin = beginInput = false;
						inputCount = 0;
					}
					else
						inputCount++;
					return;
				}
				if(code == KeyEvent.VK_LEFT)
				{
					if(count < 2)
					{
						leftPushed = true;
						count++;
					}
				}
				if(code == KeyEvent.VK_RIGHT)
				{
					if(count < 2)
					{
						rightPushed = true;
						count++;

					}
				}
				if(code == KeyEvent.VK_UP)
				{
					if(count < 2)
					{
						upPushed = true;
						count++;

					}
				}
				if(code == KeyEvent.VK_DOWN)
				{
					if(count < 2)
					{
						downPushed = true;
						count++;

					}
				}
				if(code == KeyEvent.VK_SPACE)
					spacePressed = true;
				if (code == KeyEvent.VK_COMMA)
					beginInput = true;
			}

			/**
			 * checks if the input is valid, and moves accordingly
			 */
			@SuppressWarnings("unchecked")
			public void keyReleased(KeyEvent e)
			{
				if(count >= 1 && count <= 2)
				{
					count = 0;
					int direction;
					if(upPushed)
					{
						if(leftPushed)
							direction = 315;
						else if(rightPushed)
							direction = 45;
						else if(downPushed)
						{
							direction = -1;
							JOptionPane.showMessageDialog(null, "Wrong input");
						}
						else
							direction = 0;
					}
					else if(downPushed)
					{
						if(leftPushed)
							direction = 225;
						else if(rightPushed)
							direction = 135;
						else
							direction = 180;
					}
					else if(leftPushed)
					{
						if(rightPushed)
						{
							direction = -1;
							JOptionPane.showMessageDialog(null, "Wrong input");
						}
						else
							direction = 270;
					}
					else
					{
						direction = 90;
					}


					User u = parentFrame.getWorld().getGrid().getUser();					
					if(spacePressed)
					{
						Location target = u.getLocation().getAdjacentLocation(direction);
						Arrow a = new Arrow(direction,u.getAttack());
						parentFrame.getWorld().add(target,(T) a);
						u.setDirection(direction);
						while(a.objectInRange())
						{
							a.act();
							for (Location loc : u.getGrid().getOccupiedLocations())
								addOccupant((T) u.getGrid().get(loc));
							parentFrame.repaint();
							
						}
						count = 0;
						upPushed = false; 
						downPushed = false;
						leftPushed = false;
						rightPushed = false;
						spacePressed = false;
						step();
						return;
					}
					if(direction != -1 && u.canMove(direction))
					{
						u.move(direction);
						display.recenter(u.getLocation());
						Stairs s = parentFrame.getWorld().getGrid().getStairs();
						if(s.hasWon())
						{  
							JOptionPane.showMessageDialog(null, "You have won!");
							try
							{
								Thread.sleep(2000);
							}
							catch(InterruptedException ignored)
							{

							}
                            gameOver = true;


                        }
						else
						{
							step();
							if(parentFrame.getWorld().getGrid().getUser() != null)
							{
								m.update(parentFrame.getWorld().getGrid());
								m.add(parentFrame.getWorld().getGrid());
								m.setBounds(parentFrame.getWorld().getGrid());
							}
							else
							{
								JOptionPane.showMessageDialog(null, "YOU FAILED :(");
								try
								{
									Thread.sleep(2000);
								}
								catch(InterruptedException ignored)
								{

								}
								gameOver = true;
                            }
						}
					}
					else if (direction != -1)
					{

						JOptionPane.showMessageDialog(null, "Cannot move in that direction");	
					}
					count = 0;
					upPushed = false; 
					downPushed = false;
					leftPushed = false;
					rightPushed = false;
				}
			}
		}		
		controlPanel.addKeyListener(new MyKeyListener());
		controlPanel.setFocusable(true);
		controlPanel.setFocusTraversalKeysEnabled(false);

	}


	/**
	 * Returns the panel containing the controls.
	 * @return the control panel
	 */
	public JComponent controlPanel()
	{
		return controlPanel;
	}



	/**
	 * Edits the contents of the current location if admin controls are enabled, by displaying the constructor
	 * or method menu.
	 */

	public void editLocation()
	{
		World<T> world = parentFrame.getWorld();

		Location loc = display.getCurrentLocation();
		if (loc != null)
		{
			T occupant = world.getGrid().get(loc);
			if (occupant == null)
			{
				MenuMaker<T> maker = new MenuMaker<T>(parentFrame, resources,
						displayMap);
				JPopupMenu popup = maker.makeConstructorMenu(occupantClasses,
						loc);
				Point p = display.pointForLocation(loc);
				popup.show(display, p.x, p.y);
			}
			else
			{
				MenuMaker<T> maker = new MenuMaker<T>(parentFrame, resources,
						displayMap);
				JPopupMenu popup = maker.makeMethodMenu(occupant, loc);
				Point p = display.pointForLocation(loc);
				popup.show(display, p.x, p.y);
			}
		}
		parentFrame.repaint();
	}

	/**
	 * Edits the contents of the current location, by displaying the constructor
	 * or method menu.
	 */

	public void deleteLocation()
	{
		World<T> world = parentFrame.getWorld();
		Location loc = display.getCurrentLocation();
		if (loc != null)
		{
			world.remove(loc);
			parentFrame.repaint();
		}
	}

	private void locationClicked()
	{
		World<T> world = parentFrame.getWorld();
		Location loc = display.getCurrentLocation();
		if (loc != null && !world.locationClicked(loc) && isAdmin) {
			editLocation();
			isAdmin = false;
		}
		parentFrame.repaint();
	}


    public boolean gameOver()
    {
        return gameOver;
    }
}
