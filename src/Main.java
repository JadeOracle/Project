import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;


public class Main {
	static List<Game> games = new ArrayList<Game>();
	static int[] scores = new int[100000000];
	

	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner(System.in);
		
		   
		
		int numberOfRounds = 1;
		int trainingGame = 0;
		
		
		
		System.out.println("Enter 0 for a game against the computer, 1 to train the network");
		trainingGame = Integer.parseInt(scanner.nextLine());
		
		Game game = new Game(trainingGame == 1?true:false, Network.loadNN(), null);
		
		if(trainingGame == 1)
		{
			System.out.println("Enter number of loops to play:");
			numberOfRounds = Integer.parseInt(scanner.nextLine());
		}
		
		int lowestShots = 9999;
		
		for(int i = 0; i < numberOfRounds; i++)
		{
			game.game();
			if(game.getShots() < lowestShots)
			{
				lowestShots = game.getShots();
			}
		}
		
		if(trainingGame == 1)
		{
			System.out.println("Quickest win: " + lowestShots);
		}
		
		scanner.close();
	}
	
	
	
}
