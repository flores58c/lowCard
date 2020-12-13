import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class Phase3
{

	public static void main(String[] args)
	{

		gameModel model1 = new gameModel();
		gameView view1 = new gameView();
		gameController control = new gameController(view1, model1);

	}

}

class gameModel
{
	static final int NUM_PLAYERS = 2;
	static final int NUM_CARDS_PER_HAND = 7;
	CardGameFramework game;
	static Card[] playerWinnings = new Card[336];
	// Holds points player has won
	static int playerPoints = 0;
	// Holds cards cpu has won
	static Card[] cpuWinnings = new Card[336];
	// Holds points cpu has won
	static int cpuPoints = 0;
	int numPacksPerDeck = 1;
	int numJokersPerPack = 2;
	int numUnusedCardsPerPack = 0;
	Card[] unusedCardsPerPack = null;
	Hand playerHand = new Hand();
	Hand cpuHand = new Hand();

	static CardGameFramework model1;

	public gameModel()
	{
		this.model1 = new CardGameFramework(numPacksPerDeck, numJokersPerPack, numUnusedCardsPerPack, unusedCardsPerPack,
				NUM_PLAYERS, NUM_CARDS_PER_HAND);

	}

}

class gameView extends JFrame implements ActionListener
{

	static JLabel[] computerLabels = new JLabel[gameModel.NUM_CARDS_PER_HAND];
	static JLabel[] playedCardLabels = new JLabel[gameModel.NUM_PLAYERS];
	static JLabel[] playLabelText = new JLabel[gameModel.NUM_PLAYERS];

	static JLabel playerCard = new JLabel("", JLabel.CENTER);
	static JLabel cpuCard = new JLabel("", JLabel.CENTER);
	// 0 index is cpu, 1 index is player
	static Card[] playedCards = new Card[gameModel.NUM_PLAYERS];

	// Holds player's card buttons
	static ArrayList<JButton> playerButtons = new ArrayList<>();
	  static JLabel time = new JLabel("0:00", JLabel.CENTER);
	   static JButton start = new JButton("Start");
	   static JButton stop = new JButton("Pause");
	   static JPanel timerGui = new JPanel(new GridLayout(2, 1));

	   Timer t = new Timer(this);



	CardTable myCardTable = new CardTable("CardTable", gameModel.NUM_CARDS_PER_HAND, gameModel.NUM_PLAYERS);

	public gameView()
	{

		myCardTable.setSize(800, 600);
		myCardTable.setLocationRelativeTo(null);
		myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// TODO Auto-generated constructor stub
		// CREATE LABELS ----------------------------------------------------
		playLabelText[0] = new JLabel("Computer", JLabel.CENTER);
		playLabelText[1] = new JLabel("Player 1", JLabel.CENTER);

		myCardTable.pnlPlayArea.add(playLabelText[0]);
		myCardTable.pnlPlayArea.add(playLabelText[1]);

		myCardTable.pnlPlayArea.add(cpuCard);
		myCardTable.pnlPlayArea.add(playerCard);
		
		timerGui.setBorder(new TitledBorder("Timer"));
	      timerGui.add(time);
	     timerGui.add(start);
	     timerGui.setPreferredSize(new Dimension(150, 50));
	     
	     myCardTable.add(timerGui,BorderLayout.EAST);



	      start.addActionListener(this);

	}
	
	public synchronized void actionPerformed(ActionEvent e) {


      // called each time button is clicked but function only allows one Timer to be created
         t.getInstance(this);

         //if thread has not yet started change boolean flag for run in loop
         if(!t.isAlive()) {
            t.start();
            t.paused = !t.paused;
         }
         // this
         else if(t.isAlive())
         {
            t.paused = !t.paused;
         }



   }

}

class gameController
{
	public gameController(gameView view, gameModel model)

	{
		model.model1.deal();
		model.playerHand = model.model1.getHand(0);
		model.cpuHand = model.model1.getHand(1);
		model.cpuHand.sort();
		load(model.playerHand, model.cpuHand, view.myCardTable, view, model);

		show(model.playerHand, view.myCardTable, view);
		cpuPlayLowCard(model.cpuHand, view.myCardTable, view);
		loadCpuLabels(model.cpuHand, view.myCardTable, view);

		
		view.myCardTable.setVisible(true);

	}

	public static void loadCpuLabels(Hand hand, CardTable table, gameView view)
	{
		// Ensure array is empty
		clearLabels(view.computerLabels);
		// Clear computer hand panel
		table.pnlComputerHand.removeAll();

		// iterate over all grid columns in JPanel
		for (int i = 0; i <= hand.getNumCards(); i++)
		{
			JLabel label = new JLabel(GUICard.getBackCardIcon());
			view.computerLabels[i] = label;
			table.pnlComputerHand.add(view.computerLabels[i]);
		}
	}

	// Clear all labels in array
	public static void clearLabels(JLabel[] labelArray)
	{
		for (int i = 0, length = labelArray.length; i < length; i++)
		{
			labelArray[i] = null;
		}
	}

	public static void load(Hand playerHand, Hand cpuHand, CardTable table, gameView view, gameModel m)
	{
		// Ensure there are cards to play
		if (cpuHand.getNumCards() > 0 && playerHand.getNumCards() > 0)
		{
			// iterate over all JLabels currently in humanLabels
			for (int i = 0, length = playerHand.getNumCards(); i < length; i++)
			{
				// Create JButton from JLabel
				JButton playerButton = (new JButton(GUICard.getIcon(playerHand.inspectCard(i + 1))));

				// Remove button border and background
				playerButton.setContentAreaFilled(false);
				playerButton.setBorderPainted(false);

				// Add button to list
				view.playerButtons.add(playerButton);

				// add button to JPanel
				table.pnlHumanHand.add(playerButton);

				// Add action listeners
				playerButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// get index of player button from list
						int buttonIndex = view.playerButtons.indexOf(playerButton);

						// Add played card label to player's index in played card labels array
						view.playedCardLabels[1] = new JLabel(GUICard.getIcon(playerHand.inspectCard(buttonIndex + 1)));

						// take card from hand and place it into played cards array
						view.playedCards[1] = playerHand.playCard(buttonIndex);

						// Set playerCard to clicked card
						view.playerCard.setIcon(view.playedCardLabels[1].getIcon());

						// Hide played card from view
						playerButton.setVisible(false);

						// Remove clicked button from buttons list
						view.playerButtons.remove(playerButton);

						// Calculate who won
						int winner = calculateRound(view.playedCards[0], view.playedCards[1], m);

						// Display round winner in an alert
						displayRoundWinner(winner, table);

						view.playerCard.setIcon(null);

						// Cpu is the only one "playing" cards
						// When cpu hand hits 0, round is over
						if (cpuHand.getNumCards() > 0)
						{
							cpuPlayLowCard(cpuHand, table, view);
							loadCpuLabels(cpuHand, table, view);
						} else
						{
							displayGameWinner(table, m);
							System.exit(0);
						}
					}
				});
			}
		}
	}

	public static int calculateRound(Card cpuCard, Card playerCard, gameModel m)
	{
		// Cpu won
		if (cpuCard.getValue() < playerCard.getValue())
		{
			m.cpuWinnings[m.cpuPoints] = cpuCard;
			m.cpuPoints++;
			m.cpuWinnings[m.cpuPoints] = playerCard;
			m.cpuPoints++;
			return 0;
		}
		// Player won
		else if (cpuCard.getValue() > playerCard.getValue())
		{
			m.playerWinnings[m.playerPoints] = cpuCard;
			m.playerPoints++;
			m.playerWinnings[m.playerPoints] = playerCard;
			m.playerPoints++;
			return 1;
		}
		// Draw
		// Each player wins their played card
		else
		{
			m.cpuWinnings[m.cpuPoints] = cpuCard;
			m.cpuPoints++;
			m.playerWinnings[m.playerPoints] = playerCard;
			m.playerPoints++;
			return -1;
		}
	}

	public static void displayRoundWinner(int winner, CardTable table)
	{
		// Computer won
		if (winner == 0)
		{
			showMessageDialog(table, "Computer wins");
		}
		// Player won
		else if (winner == 1)
		{
			showMessageDialog(table, "You win");
		}
		// Draw
		else
		{
			showMessageDialog(table, "Draw");
		}
	}

	public static void show(Hand hand, CardTable table, gameView view)
	{
		// loop to add them to screen
		for (int i = 0, length = hand.getNumCards(); i < length; i++)
		{
			table.pnlHumanHand.add(view.playerButtons.get(i));
		}
	}

	// Cpu will play the lowest card it has
	public static void cpuPlayLowCard(Hand hand, CardTable table, gameView view)
	{
		if (hand.getNumCards() > 0)
		{
			// Computer card label stored at 0
			view.playedCardLabels[0] = new JLabel(GUICard.getIcon(hand.inspectCard(1)));

			// Card to be played
			view.playedCards[0] = hand.playCard(0);

			// Set the icon of the cpu card
			view.cpuCard.setIcon(view.playedCardLabels[0].getIcon());
		}
	}

	public static void displayGameWinner(CardTable table, gameModel m)
	{
		if (m.playerPoints > m.cpuPoints)
		{
			showMessageDialog(table, "You won the game");
		} else if (m.playerPoints < m.cpuPoints)
		{
			showMessageDialog(table, "Computer won the game");
		} else
		{
			showMessageDialog(table, "The game ended in a draw.");
		}
	}

}



class Timer extends Thread implements Runnable
{

   int timeCount = 0 ;
   boolean paused = true ;


   //this is made to prevent the use of more timers
   private static Timer one = null;


   //Counter count = new Counter();
   gameView v = null;
   Timer(gameView view)
   {
   	v = view;
   }
   @Override
   public void run() {
     //paused = !paused;

      // timer is always running
      while (this.isAlive()) {
         if (paused == false) {
            increment();
            if(timeCount < 10 ) {
              v.time.setText(Integer.toString(timeCount / 60) + ":0" + Integer.toString(timeCount % 60));
            }
            else
            {
               v.time.setText(Integer.toString(timeCount / 60) + ":" + Integer.toString(timeCount % 60));
            }

            v.start.setText("Pause");

            doNothing(1000);


         }
         else{
            v.start.setText("Start");
         }
      }





      }



   //used to create an Instance of one Timer so clicking on button again won't create more timers
   public static Timer getInstance(gameView v)
   {
      if(one == null)
      {
         one = new Timer(v);

      }
      return one;
   }

   // this is used to create effect of seconds passing by
   public void doNothing(int millsec)
   {
      try
      {
         Thread.sleep(millsec);
      }catch (InterruptedException e)
      {
         System.out.println("interrupted");
      }

   }

   // increment timerCount
   public synchronized void increment()
   {

      int count = timeCount;
      count++;
      timeCount = count;


   }



   }


//class CardGameFramework  ----------------------------------------------------
class CardGameFramework
{
	private static final int MAX_PLAYERS = 50;

	private int numPlayers;
	private int numPacks; // # standard 52-card packs per deck
									// ignoring jokers or unused cards
	private int numJokersPerPack; // if 2 per pack & 3 packs per deck, get 6
	private int numUnusedCardsPerPack; // # cards removed from each pack
	private int numCardsPerHand; // # cards to deal each player
	private Deck deck; // holds the initial full deck and gets
								// smaller (usually) during play
	private Hand[] hand; // one Hand for each player
	private Card[] unusedCardsPerPack; // an array holding the cards not used
													// in the game. e.g. pinochle does not
													// use cards 2-8 of any suit

	public CardGameFramework(int numPacks, int numJokersPerPack, int numUnusedCardsPerPack, Card[] unusedCardsPerPack,
			int numPlayers, int numCardsPerHand)
	{
		int k;

		// filter bad values
		if (numPacks < 1 || numPacks > 6)
			numPacks = 1;
		if (numJokersPerPack < 0 || numJokersPerPack > 4)
			numJokersPerPack = 0;
		if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) // > 1 card
			numUnusedCardsPerPack = 0;
		if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
			numPlayers = 4;
		// one of many ways to assure at least one full deal to all players
		if (numCardsPerHand < 1 || numCardsPerHand > numPacks * (52 - numUnusedCardsPerPack) / numPlayers)
			numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

		// allocate
		this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
		this.hand = new Hand[numPlayers];
		for (k = 0; k < numPlayers; k++)
			this.hand[k] = new Hand();
		deck = new Deck(numPacks);

		// assign to members
		this.numPacks = numPacks;
		this.numJokersPerPack = numJokersPerPack;
		this.numUnusedCardsPerPack = numUnusedCardsPerPack;
		this.numPlayers = numPlayers;
		this.numCardsPerHand = numCardsPerHand;
		for (k = 0; k < numUnusedCardsPerPack; k++)
			this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

		// prepare deck and shuffle
		newGame();
	}

	// constructor overload/default for game like bridge
	public CardGameFramework()
	{
		this(1, 0, 0, null, 4, 13);
	}

	public Hand getHand(int k)
	{
		// hands start from 0 like arrays

		// on error return automatic empty hand
		if (k < 0 || k >= numPlayers)
			return new Hand();

		return hand[k];
	}

	public Card getCardFromDeck()
	{
		return deck.dealCard();
	}

	public int getNumCardsRemainingInDeck()
	{
		return deck.getNumCards();
	}

	public void newGame()
	{
		int k, j;

		// clear the hands
		for (k = 0; k < numPlayers; k++)
			hand[k].resetHand();

		// restock the deck
		deck.init(numPacks);

		// remove unused cards
		for (k = 0; k < numUnusedCardsPerPack; k++)
			deck.removeCard(unusedCardsPerPack[k]);

		// add jokers
		for (k = 0; k < numPacks; k++)
			for (j = 0; j < numJokersPerPack; j++)
				deck.addCard(new Card('X', Card.Suit.values()[j]));

		// shuffle the cards
		deck.shuffle();
	}

	public boolean deal()
	{
		// returns false if not enough cards, but deals what it can
		int k, j;
		boolean enoughCards;

		// clear all hands
		for (j = 0; j < numPlayers; j++)
			hand[j].resetHand();

		enoughCards = true;
		for (k = 0; k < numCardsPerHand && enoughCards; k++)
		{
			for (j = 0; j < numPlayers; j++)
				if (deck.getNumCards() > 0)
					hand[j].takeCard(deck.dealCard());
				else
				{
					enoughCards = false;
					break;
				}
		}

		return enoughCards;
	}

	void sortHands()
	{
		int k;

		for (k = 0; k < numPlayers; k++)
			hand[k].sort();
	}

	Card playCard(int playerIndex, int cardIndex)
	{
		// returns bad card if either argument is bad
		if (playerIndex < 0 || playerIndex > numPlayers - 1 || cardIndex < 0 || cardIndex > numCardsPerHand - 1)
		{
			// Creates a card that does not work
			return new Card('M', Card.Suit.SPADES);
		}

		// return the card played
		return hand[playerIndex].playCard(cardIndex);

	}

	boolean takeCard(int playerIndex)
	{
		// returns false if either argument is bad
		if (playerIndex < 0 || playerIndex > numPlayers - 1)
			return false;

		// Are there enough Cards?
		if (deck.getNumCards() <= 0)
			return false;

		return hand[playerIndex].takeCard(deck.dealCard());
	}
}
