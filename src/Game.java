import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;


public class Game implements Runnable {
	private GUI gui;
	private int[][] gridP = new int[12][12];
	private int[][] gridFoundP = new int[12][12];
	
	private int[][] gridE = new int[12][12];
	private int[][] gridFoundE = new int[12][12];
	
	private Network network;
	static private Scanner in = new Scanner(System.in);
	private Boolean trainingGame;
	private int shots;
	private int[] ships = {2, 2, 3, 3, 3, 4, 5};
	
	public Game(Boolean trainingGame, Network net, GUI gui)
	{
		this.network = net;
		this.gui = gui;
		this.trainingGame = trainingGame;
	}
	
	public void setNetwork(Network network)
	{
		this.network = network;
	}
	
	public void game()
	{		
		shots = 0;
		
		setupGrid(gridP, gridFoundP, true);
		setupGrid(gridE, gridFoundE, false);
		
		if(gui == null)
		{
			System.out.println("Players Ship configuration:");
			printGrid(gridP);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	gui.updateGrid(gridFoundE);
	            }
	        });
		}
		
		while (!hasFinished())
		{
			if(!trainingGame)
			{
				playerTurn();
			}
			enemyTurn();
		}
		System.out.println("Layout: ");
		printGrid(gridP);
		
		saveNN();
		in.close();
	}
	
	private void enemyTurn()
	{
		int[] coords = network.getCoord(gridFoundP);
		
		boolean lastHit = shoot(coords[0], coords[1], gridP, gridFoundP);
		
		if(lastHit == false)
		{
			network.mutate(1);
		}
		
		System.out.println("Enemies Turn: \n");
		printGrid(gridFoundP);
		shots++;
		
	}
	
	private void playerTurn()
	{
		System.out.println("Players Turn: \n");
		printGrid(gridFoundE);
		System.out.println("Select a location (in the form \"11 10\")"); 

		String answer = in.nextLine();
		String[] coords = answer.split(" ");
		int[] fireLoc = {Integer.parseInt(coords[0]) -1 , Integer.parseInt(coords[1]) -1};
		
		while(fireLoc[0] < 1 || fireLoc[1] < 1 || fireLoc[0] > 12 || fireLoc[1] > 12)
		{
			System.out.println("Location out of bounds, the grid goes from 1 - 12: ");
			answer = in.nextLine();
			coords = answer.split(" ");
			fireLoc[0] = Integer.parseInt(coords[0]) - 1;
			fireLoc[1] = Integer.parseInt(coords[1]) - 1;
		}
		
		
		while(gridFoundE[fireLoc[0]][fireLoc[1]] != 0)
		{
			System.out.println("You've hit that location already, please choose another:"); 

			answer = in.nextLine();
			coords = answer.split(" ");
			fireLoc[0] = Integer.parseInt(coords[0]) -1;
			fireLoc[1] = Integer.parseInt(coords[1]) -1;
		}
		
		shoot(fireLoc[0], fireLoc[1], gridE, gridFoundE);
		
	}
	
	public Network getNetwork()
	{
		return network;
	}
	
	private void setupGrid(int[][] grid, int[][] gridFound, boolean playerPlacing)
	{
		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
			{
				grid[i][j] = 0;
			}
		}
		
		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
			{
				gridFound[i][j] = 0;
			}
		}
		
		if(playerPlacing)
		{
			playerPlacing();
		}
		else
		{
			for(int n : ships)
			{
				placeShip(n, grid);
			}
		}

	}
	
	private void playerPlacing()
	{
		for(int n : ships)
		{
			printGrid(gridP);
			
			System.out.println("Please enter the coordinates to place the ship: ");
			String answer = in.nextLine();
			String[] coords = answer.split(" ");
			int[] placeLoc = {Integer.parseInt(coords[0]) +1 , Integer.parseInt(coords[1]) +1};
			while(placeLoc[0] < 1 || placeLoc[1] < 1 || placeLoc[0] > 12 || placeLoc[1] > 12)
			{
				System.out.println("Location out of bounds, the grid goes from 1 - 12: ");
				answer = in.nextLine();
				coords = answer.split(" ");
				placeLoc[0] = Integer.parseInt(coords[0]) - 1;
				placeLoc[1] = Integer.parseInt(coords[1]) - 1;
			}
			
			
			Boolean north = canFitN(n, placeLoc[0], placeLoc[1], gridP);
			Boolean east = canFitE(n, placeLoc[0], placeLoc[1], gridP);
			Boolean south = canFitS(n, placeLoc[0], placeLoc[1], gridP);
			Boolean west = canFitW(n, placeLoc[0], placeLoc[1], gridP);
			
			while(north == false && east == false && south == false && west == false)
			{
				System.out.println("Please enter the coordinates to place the ship: ");
				answer = in.nextLine();
				coords = answer.split(" ");
				placeLoc[0] = Integer.parseInt(coords[0]) - 1;
				placeLoc[1] = Integer.parseInt(coords[1]) - 1;
				
				north = canFitN(n, placeLoc[0], placeLoc[1], gridP);
				east = canFitE(n, placeLoc[0], placeLoc[1], gridP);
				south = canFitS(n, placeLoc[0], placeLoc[1], gridP);
				west = canFitW(n, placeLoc[0], placeLoc[1], gridP);
			}
			
			System.out.println("Which direction would you like to place the ship? (" + (north?" n":"") + (east?" e":"") + (south?" s":"") + (west?" w":"") + ")");
			
			String ans = in.nextLine();

			while(((!ans.equals("n")) && (!ans.equals("e")) && (!ans.equals("s")) && (!ans.equals("w"))) || 
					((ans.equals("n") && !north) && (ans.equals("e") && !east) && (ans.equals("s") && !south) && (ans.equals("w") && !west)))
			{
				System.out.println("Which direction would you like to place the ship? (" + (north?" n":"") + (east?" e":"") + (south?" s":"") + (west?" w":"") + ")");

				ans = in.nextLine();
			}
			
			switch(ans)
			{
			case "n":
				placeN(n, placeLoc[0], placeLoc[1], gridP);
				break;
			case "e":
				placeE(n, placeLoc[0], placeLoc[1], gridP);
				break;
			case "s":
				placeS(n, placeLoc[0], placeLoc[1], gridP);
				break;
			case "w":
				placeW(n, placeLoc[0], placeLoc[1], gridP);
				break;
			}
		}
	}
	
	private void placeShip(int size, int[][] grid)
	{
		boolean placed = false;
		Random rand = new Random();
		
		do {
			int x = rand.nextInt(12);
			int y = rand.nextInt(12);
			
			if(grid[x][y] == 0)
			{
				switch(rand.nextInt(4))
				{
				case 0:
					if(canFitN(size, x, y, grid))
					{
						placeN(size, x, y, grid);
					}
					break;
				case 1:
					if(canFitE(size, x, y, grid))
					{
						placeE(size, x, y, grid);
					}
					break;
				case 2:
					if(canFitS(size, x, y, grid))
					{
						placeS(size, x, y, grid);
					}
					break;
				case 3:
					if(canFitW(size, x, y, grid))
					{
						placeW(size, x, y, grid);
					}
					break;
				}
			}
		} while (placed == false);
	}
	
	private boolean canFitE(int size, int x, int y, int[][] grid)
	{
		boolean fits = true;
		for (int i=0; i < size; i++)
		{
			if(y+i > 11 || grid[x][y + i] == 1)
			{
				fits = false;
			}
		}
		
		return fits;
	}
	
	private boolean canFitS(int size, int x, int y, int[][] grid)
	{
		boolean fits = true;
		for (int i=0; i < size; i++)
		{
			if(x+i > 11 || grid[x + i][y] == 1)
			{
				fits = false;
			}
		}
		
		return fits;
	}
	
	private boolean canFitW(int size, int x, int y, int[][] grid)
	{
		boolean fits = true;
		for (int i=0; i < size; i++)
		{
			if(y-i < 0 || grid[x][y - i] == 1)
			{
				fits = false;
			}
		}
		
		return fits;
	}
	
	private boolean canFitN(int size, int x, int y, int[][] grid)
	{
		boolean fits = true;
		for (int i=0; i < size; i++)
		{
			if(x-i < 0 || grid[x - i][y] == 1)
			{
				fits = false;
			}
		}
		
		return fits;
	}
	
	private void placeE(int size, int x, int y, int[][] grid)
	{
		for (int i=0; i < size; i++)
		{
			grid[x][y+i] = 1;
		}
	}
	
	private void placeS(int size, int x, int y, int[][] grid)
	{
		for (int i=0; i < size; i++)
		{
			grid[x+i][y] = 1;
		}
	}
	
	private void placeW(int size, int x, int y, int[][] grid)
	{
		for (int i=0; i < size; i++)
		{
			grid[x][y-i] = 1;
		}
	}
	
	private void placeN(int size, int x, int y, int[][] grid)
	{
		for (int i=0; i < size; i++)
		{
			grid[x-i][y] = 1;
		}
	}
	
	
	public boolean shoot(int x, int y, int[][] grid, int[][] gridFound)
	{
		boolean hit = false;
		if(grid[x][y] == 1 && gridFound[x][y] == 0)
		{
			hit = true;
			gridFound[x][y] = 1;
		}
		else
		{
			gridFound[x][y] = 2;
		}
		
		if(hit)
		{
			System.out.println("Hit!");
		}
		else
		{
			System.out.println("Miss");
		}
		
		return hit;
	}
	
	public void printGrid(int[][] prntGrid)
	{
		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
			{
				if(prntGrid[i][j] == 0)
					System.out.print('#');
				if(prntGrid[i][j] == 1)
					System.out.print('X');
				if(prntGrid[i][j] == 2)
					System.out.print('O');
			}
			System.out.print('\n');
		}
		System.out.print("\n\n\n");
	}
	
	private boolean hasFinished()
	{
		int shipHits = 0;
		
		for(int locs : ships)
		{
			shipHits = shipHits + locs;
		}
		int pHits = 0;
		int eHits = 0;
		
		for(int i=0; i<12; i++)
		{
			for(int j=0; j<12; j++)
			{
				if(gridFoundE[i][j] == 1)
				{
					pHits++;
				}
			}
		}
		
		for(int i=0; i<12; i++)
		{
			for(int j=0; j<12; j++)
			{
				if(gridFoundP[i][j] == 1)
				{
					eHits++;
				}
			}
		}
		
		if(pHits == shipHits || eHits == shipHits)
		{
			return true;
		}
		
		return false;
	}
	
	private void saveNN()
	{
		File file = new File("C:/Users/Administrator/workspace/Test/temp/network.ser");
		try
	      {
			if(!file.exists())
			{
				file.createNewFile();
			}
			
	         FileOutputStream fileOut =
	         new FileOutputStream(file);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(network);
	         out.close();
	         fileOut.close();
	         System.out.println("Serialized data is saved in /tmp/network.ser");
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}


	public int getShots() {
		return shots;
	}


	@Override
	public void run() {
		this.game();
		
	}
}
