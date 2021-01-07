import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * @author Noah Gilgo (njg2438)
 * @since 4/26/2020
 */


public class NumbersGame {
	private String playerName = "";
	private int difficulty = 0;
	private int[] correctNumbers;
	private JTextArea gameArea;
	private char[] validNumbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private int correctNums;
	private int correctPositions;
	private JCheckBox timeCheckBox;
	private JCheckBox numberOfPlaysBox;
	private JCheckBox topPlayerBox;
	private JCheckBox mostDifficultBox;
	
	private int time;
	
	private boolean timeChecked;
	private boolean numberOfPlaysChecked;
	private boolean topPlayerChecked;
	private boolean mostDifficultGamesChecked;
	
	private ArrayList<Game> gameArray = new ArrayList<Game>();
	
	private int maxValue;
	private int correctNum;
	
	private boolean gameOver = true;
	private int singleGuess;
	
	private Scanner inputFile;
	
	//Constructor for NumbersGame class
	public NumbersGame() throws IOException {
		//Instantiates name frame
		while(playerName.equals("")) {
			try {
				playerName = JOptionPane.showInputDialog("Enter your name:");
			} catch(Exception e) {
				playerName = "a";
				System.exit(0);
			}
		}
		
		//Instantiates difficulty frame
		while(difficulty < 1) {
			try {
				difficulty = Integer.parseInt(JOptionPane.showInputDialog("Enter difficulty (must be integer greater than 0):"));
				if(difficulty == 1) {
					maxValue = Integer.parseInt(JOptionPane.showInputDialog("Enter max value (must be integer greater than 0):"));
				}
			} catch(Exception e) {
				difficulty = 1;
				System.exit(0);
			}
		}
		
		//Instantiates file if not present/reads from file and copies data to arraylist if it is
		File f = new File("numbers game data.txt");
		if(f.exists()) {
			inputFile = new Scanner(f);
		} else {
			f.createNewFile();
			inputFile = new Scanner(f);
		}
		if (inputFile != null && f.length() > 0) {
			while(inputFile.hasNextLine()) {
				String[] gameData = inputFile.nextLine().split(",");
				gameArray.add(new Game(gameData[0], Integer.parseInt(gameData[1]), Integer.parseInt(gameData[2])));
			}
		}
		inputFile.close();
		
		//Instantiates the frame
		JFrame frame = new JFrame("Numbers Game");
		frame.addWindowListener(new FrameListener());
		frame.setResizable(false);
		
		//Instantiates panels
		JPanel topPanel = new JPanel();
	    JPanel centerPanel = new JPanel();
	    JPanel bottomPanel = new JPanel();
	    
	    //Instantiates objects in frame
	    //Top panel:
	    JButton startButton = new JButton("Start Game");
	    startButton.addActionListener(new ButtonListener());
	    JTextField nextGuessField = new JTextField("Enter guess here:", 20);
	    nextGuessField.addActionListener(new TextFieldListener());
	    //Center panel:
	    gameArea = new JTextArea(24, 50);
	    //Bottom panel:
	    JButton clearDisplayButton = new JButton("Clear Display");
	    clearDisplayButton.addActionListener(new ButtonListener());
	    JButton displayStatsButton = new JButton("Display Stats");
	    displayStatsButton.addActionListener(new ButtonListener());
	    timeCheckBox = new JCheckBox("Time");
	    timeCheckBox.addItemListener(new CheckBoxListener());
	    numberOfPlaysBox = new JCheckBox("# of plays");
	    numberOfPlaysBox.addItemListener(new CheckBoxListener());
	    topPlayerBox = new JCheckBox("Top Players");
	    topPlayerBox.addItemListener(new CheckBoxListener());
	    mostDifficultBox = new JCheckBox("Most difficult games");
	    mostDifficultBox.addItemListener(new CheckBoxListener());
	    
	    //Sets dimensions of frame
	  	frame.setSize(700,500);
	    frame.setLocation(400,150);
	  	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	  	//Add components to panels
	  	//Top panel:
	  	topPanel.add(startButton);
	  	topPanel.add(nextGuessField);
	  	//Center panel:
	  	JScrollPane scrollBar = new JScrollPane(gameArea);
	  	centerPanel.add(scrollBar);
	  	//Bottom panel:
	  	bottomPanel.add(clearDisplayButton);
	  	bottomPanel.add(displayStatsButton);
	  	bottomPanel.add(timeCheckBox);
	  	bottomPanel.add(numberOfPlaysBox);
	  	bottomPanel.add(topPlayerBox);
	  	bottomPanel.add(mostDifficultBox);
	  	
	  	//Add panels to frame
	  	frame.add(topPanel, BorderLayout.PAGE_START);
	  	frame.add(centerPanel, BorderLayout.CENTER);
	  	frame.add(bottomPanel, BorderLayout.SOUTH);
	  	
	    //Sets everything in the frame to visible
	    frame.setVisible(true);
	}
	
	//body of ButtonListener class
	class ButtonListener implements ActionListener{

		@Override
		//Defines actions taken whenever a button is pressed.
		public void actionPerformed(ActionEvent ae) {
			JButton b = (JButton) ae.getSource();
			//Defines code ran whenever the 'Start Game' button is pressed.
			if(b.getText().contentEquals("Start Game")) {
				gameOver = false;
				time = 0;
				correctNumbers = new int[difficulty];
				int randInt;
				for(int i=0; i<correctNumbers.length; i++) {
					randInt = (int) Math.floor(Math.random() * 10);
					correctNumbers[i] = randInt;
				}
				correctNum = (int) Math.floor(Math.random() * (maxValue + 1));
			//Defines code ran whenever the 'Clear Display' button is pressed.
			} else if(b.getText().contentEquals("Clear Display")) {
				gameArea.setText(null);
			//Defines code ran whenever the 'Display Stats' button is pressed.
			} else if(b.getText().contentEquals("Display Stats")) {
				//'Display Stats' runs this code if the 'Time' checkbox is checked.
				if(timeChecked) {
					gameArray.sort(new SortByTime());
					gameArea.append("GAME TIME LEADERBOARD\n(ranked from shortest to longest)\n(format is player name-difficulty-time)\n--------------------------------------------\n");
					for(Game g : gameArray) {
						gameArea.append(g.getPlayerName() + "  " + Integer.toString(g.getDifficulty()) + "  " + Integer.toString(g.numOfGuesses) + "\n");
					}
					gameArea.append("\n");
				}
				//'Display Stats' runs this code if the '# of plays' checkbox is checked.
				if(numberOfPlaysChecked) {
					ArrayList<Player> playerList = new ArrayList<Player>();
					for(Game g : gameArray) {
						Player tempPlayer = new Player(g.getPlayerName(), 1);
						boolean playerFound = false;
						int playerFoundIndex = 0;
						int i = 0;
						while(playerFound == false && i < playerList.size()) {
							if(tempPlayer.getPlayerName().equalsIgnoreCase((playerList.get(i).getPlayerName()))) {
								playerFoundIndex += i;
								playerFound = true;
							}
							i++;
						}
						if(playerFound) {
							Player foundPlayer = playerList.get(playerFoundIndex);
							Player incrementedPlayer = new Player(foundPlayer.getPlayerName(), foundPlayer.getNumOfPlays());
							incrementedPlayer.incrementPlays();
							playerList.set(playerFoundIndex, incrementedPlayer);
						} else {
							playerList.add(tempPlayer);
						}
					}
					playerList.sort(new SortByPlays());
					gameArea.append("# OF PLAYS LEADERBOARD\n(ranked from most games played to least)\n(format is player name-# of plays)\n--------------------------------------------\n");
					for(Player p : playerList) {
						gameArea.append(p.getPlayerName() + "  " + p.getNumOfPlays() + "\n");
					}
					gameArea.append("\n");
				}
				//'Display Stats' runs this code if the 'Top Players' checkbox is checked.
				if(topPlayerChecked) {
					gameArray.sort(new SortByDifficulty());
					ArrayList<DifficultyArray> difficultyArrayArray = new ArrayList<DifficultyArray>();
					for(Game g : gameArray) {
						boolean difficultyArrayFound = false;
						int index = 0;
						while(!difficultyArrayFound && index <= difficultyArrayArray.size()) {
							if(difficultyArrayArray.isEmpty()) {
								difficultyArrayArray.add(new DifficultyArray(g.getDifficulty()));
								break;
							}
							
							if(index == difficultyArrayArray.size()) {
								difficultyArrayArray.add(new DifficultyArray(g.getDifficulty()));
								break;
							}
							if(difficultyArrayArray.get(index).getDifficulty() == g.getDifficulty()) {
								difficultyArrayFound = true;
							}
							index++;
						}
					}
					
					for(Game g : gameArray) {
						boolean difficultyArrayFound = false;
						int index = 0;
						while(!difficultyArrayFound && index < difficultyArrayArray.size()) {
							if(difficultyArrayArray.get(index).getDifficulty() == g.getDifficulty()) {
								Player tempPlayer = new Player(g.getPlayerName(), 1, g.getNumOfGuesses());
								boolean playerFound = false;
								int playerFoundIndex = 0;
								int i = 0;
								while(playerFound == false && i < difficultyArrayArray.get(index).size()) {
									if(tempPlayer.getPlayerName().equalsIgnoreCase((difficultyArrayArray.get(index).get(i).getPlayerName()))) {
										playerFoundIndex += i;
										playerFound = true;
									}
									i++;
								}
								if(playerFound) {
									Player foundPlayer = difficultyArrayArray.get(index).get(playerFoundIndex);
									Player incrementedPlayer = new Player(foundPlayer.getPlayerName(), foundPlayer.getNumOfPlays() + 1, foundPlayer.getTimePlayed() + g.getNumOfGuesses());
									difficultyArrayArray.get(index).set(playerFoundIndex, incrementedPlayer);
								} else {
									difficultyArrayArray.get(index).add(tempPlayer);
								}
								difficultyArrayFound = true;
							}
							index++;
						}
					}
					gameArea.append("TOP PLAYERS LEADERBOARD\n(ranked from best average time to worst)\n(format is player name-average time)\n--------------------------------------------\n");
					for(DifficultyArray da : difficultyArrayArray) {
						gameArea.append(String.format("Level %d Results - Average # of Guesses\n", da.getDifficulty()));
						int amountShown = 3;
						int index = 0;
						while(index < amountShown && index < da.size()) {
							Player tempPlayer = da.get(index);
							gameArea.append(String.format("%s  %f\n", tempPlayer.getPlayerName(), tempPlayer.getAverageTime()));
							index++;
						}
						gameArea.append("\n");
					}
				}
				////'Display Stats' runs this code if the 'Most difficult games' checkbox is checked.
				if(mostDifficultGamesChecked) {
					gameArray.sort(new SortByDifficulty());
					gameArea.append("DIFFICULTY LEADERBOARD\n(ranked from hardest to easiest)\n(format is player name-difficulty-time)\n(top three players from each difficulty are shown)\n--------------------------------------------\n");
					for(Game g : gameArray) {
						gameArea.append(g.getPlayerName() + "  " + Integer.toString(g.getDifficulty()) + "  " + Integer.toString(g.numOfGuesses) + "\n");
					}
					gameArea.append("\n");
				}
			}
		}
	}
	
	//Body for TextFieldListener class.
	class TextFieldListener implements ActionListener{

		@Override
		//Handles the guess-entering text-box.
		public void actionPerformed(ActionEvent ae) throws ArrayIndexOutOfBoundsException {
			if(gameOver) {
				gameArea.append("Press the 'Start Game' button to begin a new game.\n");
				return;
			}
			time++;
			JTextField t = (JTextField) ae.getSource();
			String guess = t.getText();
			char[] chars = guess.toCharArray();
			int[] guessArray = new int[difficulty];
			for (int i=0; i<guessArray.length; i++) {
				guessArray[i] = -1;
			}
			if(difficulty > 1) {
				try {
					int guessed = 0;
					for(char c : chars) {
						boolean validNum = false;
						int i = 0;
						while(!validNum && i <= 9) {
							if(c == validNumbers[i]) {
								guessArray[guessed] = Character.getNumericValue(c);
								validNum = true;
								guessed++;
							}
							i++;
						}
					}
				} catch(ArrayIndexOutOfBoundsException e) {
					gameArea.append("Error: guess was not input in valid format.\n");
					return;
				}
			} else if(difficulty == 1) {
				try {
					singleGuess = Integer.parseInt(guess);
				} catch(Exception e) {
					gameArea.append("Error: guess was not input in valid format.\n");
					return;
				}
			}
			
			if(guessArray[difficulty-1] == -1 && difficulty > 1) {
				gameArea.append("Error: guess was not input in valid format.\n");
				return;
			}
			
			correctNums = 0;
			correctPositions = 0;
			
			int index;
			int[] correctNumbersCopy = Arrays.copyOfRange(correctNumbers, 0, correctNumbers.length);
			if(difficulty > 1) {
				for(int i=0; i<guessArray.length; i++) {
					index = 0;
					boolean matchFound = false;
					while(!matchFound && index < correctNumbersCopy.length) {
						if(guessArray[i] == correctNumbersCopy[index]) {
							correctNums++;
							correctNumbersCopy[index] = -1;
							matchFound = true;
						}
						index++;
					}
				}
				int ind = 0;
				for(int i=0; i<guessArray.length; i++) {
					if(guessArray[i] == correctNumbers[ind]) {
						correctPositions++;
					}
					ind++;
				}
				
				String result = String.format("(%d, %d)", correctNums, correctPositions);
				gameArea.append(guess + " -> " + result + "\n");
				
				//Victory condition met
				if(correctNums == difficulty && correctPositions == difficulty) {
					gameOver = true;
					JOptionPane.showMessageDialog(null, "You did it! \n(go you)", "Congratulations, " + playerName + "!", JOptionPane.PLAIN_MESSAGE);
					gameArray.add(new Game(playerName, difficulty, time));
					time = 0;
					gameArea.append("\n");
				}
				
			} else if(difficulty == 1) {
				if(singleGuess > correctNum) {
					gameArea.append("Less than\n");
				} else if(singleGuess < correctNum) {
					gameArea.append("Greater than\n");
				} else {
					//Victory condition met for difficulty = 1
					gameOver = true;
					JOptionPane.showMessageDialog(null, "You did it! \n(go you)", "Congratulations " + playerName + "!", JOptionPane.PLAIN_MESSAGE);
					gameArray.add(new Game(playerName, difficulty, time));
					time = 0;
				}
			}
			
		}
	}
	//Body of CheckBoxListener class
	class CheckBoxListener implements ItemListener{

		@Override
		//Handles checkboxes being checked/unchecked.
		public void itemStateChanged(ItemEvent ie) {
			JCheckBox c = (JCheckBox) ie.getSource();
			//Handles the 'Time' checkbox.
			if(c.getText().equals("Time")) {
				if(c.isSelected()) {
					timeChecked = true;
				} else {
					timeChecked = false;
				}
			//Handles the '# of plays' checkbox.
			} else if(c.getText().equals("# of plays")) {
				if(c.isSelected()) {
					numberOfPlaysChecked = true;
				} else {
					numberOfPlaysChecked = false;
				}
			//Handles the 'Top Players' checkbox.
			} else if(c.getText().equals("Top Players")) {
				if(c.isSelected()) {
					topPlayerChecked = true;
				} else {
					topPlayerChecked = false;
				}
			//Handles the 'Most difficult games' checkbox.
			} else if(c.getText().equals("Most difficult games")) {
				if(c.isSelected()) {
					mostDifficultGamesChecked = true;
				} else {
					mostDifficultGamesChecked = false;
				}
			}
			
		}
		
	}
	
	//Body of FrameListener class.
	class FrameListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {}

		@Override
		public void windowClosed(WindowEvent arg0) {}

		@Override
		//Runs code whenever the main frame is closed. Saves game data from session.
		public void windowClosing(WindowEvent we) {
			FileWriter f = null;
			if(new File("numbers game data.txt").exists()) {
				try {
					f = new FileWriter("numbers game data.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				File a = new File("numbers game data.txt");
				try {
					a.createNewFile();
					f = new FileWriter("numbers game data.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			for(Game g : gameArray) {
				try {
					f.write(g.getPlayerName() + "," + g.getDifficulty() + "," + g.getNumOfGuesses() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {}

		@Override
		public void windowDeiconified(WindowEvent arg0) {}

		@Override
		public void windowIconified(WindowEvent arg0) {}

		@Override
		public void windowOpened(WindowEvent arg0) {}
		
	}
	
	//Body for Game class. Acts as a container for individual game data.
	private class Game {
		private String playerName;
		private int difficulty;
		private int numOfGuesses;
		
		//Constructor for the Game class.
		public Game(String playerName, int difficulty, int numOfGuesses) {
			this.playerName = playerName;
			this.difficulty = difficulty;
			this.numOfGuesses = numOfGuesses;
		}
		
		public String getPlayerName() {
			return playerName;
		}

		public int getDifficulty() {
			return difficulty;
		}

		public int getNumOfGuesses() {
			return numOfGuesses;
		}

	}
	
	//Body for Player class. Contains data about specific players.
	private class Player {
		public int getTimePlayed() {
			return timePlayed;
		}

		public double getAverageTime() {
			return ((double) this.timePlayed) / ((double) this.numOfPlays);
		}

		private String playerName;
		private int numOfPlays;
		private int timePlayed;
		
		//Constructor for Player class without time played.
		public Player(String playerName, int numOfPlays) {
			this.playerName = playerName;
			this.numOfPlays = numOfPlays;
		}
		
		//Constructor for Player class with time played.
		public Player(String playerName, int numOfPlays, int timePlayed) {
			this.playerName = playerName;
			this.numOfPlays = numOfPlays;
			this.timePlayed = timePlayed;
		}
		
		public String getPlayerName() {
			return playerName;
		}

		public int getNumOfPlays() {
			return numOfPlays;
		}

		public void incrementPlays() {
			this.numOfPlays++;
		}
	}
	
	//Sort by time comparator for Game class
	private class SortByTime implements Comparator<Game> {

		@Override
		//Compares times between Game objects.
		public int compare(Game g1, Game g2) {
			int result = g1.getNumOfGuesses() - g2.getNumOfGuesses();
			return result;
		}
		
	}
	
	//Sort by difficulty for Game class
	private class SortByDifficulty implements Comparator<Game> {

		@Override
		//Compares difficulty between game objects.
		public int compare(Game g1, Game g2) {
			int result = g2.getDifficulty() - g1.getDifficulty();
			return result;
		}
		
	}
	
	//Sort by number of plays for Player class
	private class SortByPlays implements Comparator<Player> {

		@Override
		//Compares number of games played between Player classes.
		public int compare(Player p1, Player p2) {
			int result = p2.getNumOfPlays() - p1.getNumOfPlays();
			return result;
		}
	}
	
	//Body of DifficultyArray class. Acts as a container for games with the same difficulty.
	private class DifficultyArray extends ArrayList<Player> {
		private static final long serialVersionUID = 1L;
		private int difficulty;
		
		//Constructor for DifficultyArray class.
		public DifficultyArray(int difficulty) {
			this.difficulty = difficulty;
		}
		
		public int getDifficulty() {
			return difficulty;
		}
	}
}
