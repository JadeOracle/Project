import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class Network implements java.io.Serializable {
	List<Neuron> mid1List = new ArrayList<Neuron>();
	List<Neuron> mid2List = new ArrayList<Neuron>();
	List<Neuron> inputs = new ArrayList<Neuron>();
	List<Neuron> outputs = new ArrayList<Neuron>();
	
	/*
	 * Need to implement back propogation at a later point to get this working correctly..
	 */
	
	
	private Network()
	{
		
		
		Neuron outputX = new Neuron();
		Neuron outputY = new Neuron();
		
		outputs.add(outputX);
		outputs.add(outputY);
		
		for(int i = 0; i < 7; i++)
		{
			Neuron n = new Neuron();
			mid1List.add(n);
		}
		
		for(int i = 0; i < 7; i++)
		{
			Neuron n = new Neuron();
			mid2List.add(n);
		}
		

		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
			{
				Neuron n = new Neuron();
				n.makeInput();
				inputs.add(n);
			}
		}
	
		for(Neuron m: mid1List)
		{
			for(Neuron n: mid2List)
			{
				n.addInput(m);
			}
		}
		
		for(Neuron n: mid2List)
		{
			outputY.addInput(n);
			outputX.addInput(n);
		}
		
		
		for(Neuron m: mid1List)
		{
			for(Neuron n: inputs)
			{
				m.addInput(n);
			}
		}
		
		
		
	}
	
	public int[] getCoord(int[][] grid)
	{
		
		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
			{
				inputs.get(i*12 + j).setWeight((float) (grid[i][j] + 1));
			}
		}
		
		int[] coords = new int[2];
		coords[0] = (int) outputs.get(0).fire();
		coords[1] = (int) outputs.get(1).fire();

		while(coords[0] > 11 || coords[1] > 11 || coords[0] < 0 || coords[1] < 0  || grid[coords[0]][coords[1]] != 0)
		{
			boolean mutated = false;
			if(coords[0] > 11 || coords[1] > 11)
			{
				mutate(0);
				mutated = true;
			}
			
			if (coords[0] <= 0 || coords[1] <= 0 )
			{
				mutate(2);
				mutated = true;
			} 
			
			if (!mutated)
			{
				mutate(1);
			}
			
			coords = new int[2];
			coords[0] = (int) outputs.get(0).fire();
			coords[1] = (int) outputs.get(1).fire();
			System.out.println("Shot thought: " + coords[0] + " " + coords[1]);
		}
		
		
		System.out.println("Shot made: " + coords[0] + " " + coords[1]);
		return coords;		
	}
	
	public void mutate(int dir)
	{
		for(Neuron n:mid1List)
		{
			n.mutate(dir);
		}
		
		for(Neuron n:mid2List)
		{
			n.mutate(dir);
		}
		
		for(Neuron n:outputs)
		{
			n.mutate(dir);
		}
	}
	
	public Network copyNetwork()
	{
		Network clone = null;
		try {
			clone = (Network) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clone;
	}
	
	public static Network loadNN()
	{
		File file = new File("C:/Users/Administrator/workspace/Test/temp/network.ser");
		Network net = null;
		if(file.exists())
		{
			
			try
		      {
		         FileInputStream fileIn = new FileInputStream(file);
		         ObjectInputStream in = new ObjectInputStream(fileIn);
		         net = (Network) in.readObject();
		         in.close();
		         fileIn.close();
		         System.out.println("Loaded NN");
		      }catch(IOException i)
		      {
		         i.printStackTrace();
		      }catch(ClassNotFoundException c)
		      {
		         c.printStackTrace();
		      }
		}
		else
		{
			net = new Network();
		}
		return net;
	}

}
