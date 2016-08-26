import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI {
	private Game game;
	private JFrame frame;
	private JButton[][] jButtons = new JButton[12][12];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		game = new Game(false, Network.loadNN(), this);
		
		frame = new JFrame();
		frame.setBounds(100, 100, 753, 590);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 510, 530);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		
		
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				JButton b = new JButton("");
				panel.add(b);
				b.setBounds((i * 40) + 10, (j * 40) + 10, 40, 40);
				b.setBackground(new Color(0, 0, 0));
				jButtons[i][j] = b;
				
				b.repaint();
			}
		}
		
		
		
		
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(530, 77, 196, 464);
		frame.getContentPane().add(panel_1);
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				game.run();
			}
		});
		panel_1.add(btnNewGame);
	}
	
	public void updateGrid(int[][] grid)
	{
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				if(grid[i][j] == 1) 
				{
					jButtons[i][j].setBackground(new Color(0, 255, 0));
				}
				else if(grid[i][j] == 2)
				{
					jButtons[i][j].setBackground(new Color(255, 0, 0));
				}
				else
				{
					jButtons[i][j].setBackground(new Color(0, 0, 255));
				}
			}
		}
	}

	public void showGui() {
		new GUI().frame.setVisible(true);
	}
}
