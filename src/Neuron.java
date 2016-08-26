import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Neuron implements java.io.Serializable {
	private float weight = 0.5f;
	private List<Neuron> inputs = new ArrayList<Neuron>();
	private boolean input = false;
	private Random rand = new Random();
	
	public Neuron()
	{
		this.weight = rand.nextFloat();
	}
	

	public void makeInput()
	{
		input = true;
	}
	
	public void addInput(Neuron e)
	{
		inputs.add(e);
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public float fire()
	{
		if(input)
		{
			return weight;
		}else
		{
			float sum = 0f;
			for(Neuron n : inputs)
			{
				sum = sum + (n.fire() * weight);
			}
			
			return sum % 12;
		}
	}
	
	public void mutate(int direction)
	{
		if(rand.nextFloat() > 0.5f) 
		{
			switch (direction)
			{
			case 0:
				weight = weight + (rand.nextFloat() * -0.025f);
				break;
			case 1:
				weight = weight + ((rand.nextFloat() * 0.01f) - 0.005f);
				break;
			case 2:
				weight = weight + (rand.nextFloat() * 0.025f);
				break;
			}
		}
	}
}
