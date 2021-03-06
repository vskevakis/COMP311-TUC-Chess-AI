import java.util.*;

public class World
{
	private String[][] board = null;
	public int rows = 7;
	public int columns = 5;
	public int myColor = 0;
	public ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int maxdepth = 5;	//9 is doable but takes too much time 7 is ok
	private int noPrize = 9;
	private String chosenMove;
	private boolean staticminmax=false; //if min max or montecarlo is called || true =minmax false =montecarlo
	//TODO add comments mostly
	private  int[][] pawn = {{50, 50, 50, 50, 50},
			{30, 30, 30, 30, 30},
			{20, 10, 30, 10, 20},
			{20, 20, 20, 20, 20},
			{20, 10, 0, 10, 20},
			{5, 10, -20, 10, 5},
			{-10, 0, 0, 0, -10}
	};
	private  int[][] rook = {{0, 0, 0, 0, 0},
			{10, 5, 10, 5, 10},
			{-5, 0, 0, 0, -5},
			{-5, 0, 0, 0, -5},
			{-5, 0, 0, 0, -5},
			{-5, 0, 0, 0, -5},
			{0, 0, 5, 0, 0}
	};
	private  int[][] king_midgame = {{-30, -40, -50, -40, -30},
			{-30, -40, -50, -40, -30},
			{-30, -40, -50, -40, -30},
			{-20, -30, -40, -30, -20},
			{-10, -10, -20, -20, -10},
			{20, 20, 0, 20, 20},
			{20, 30, 0, 30, 20}

	};
	private  int[][] king_endgame = {{-30,-40, -50, -40, -30},
			{-10, -30, 0, -30, -10},
			{-10, -30, 30, -30, -10},
			{-10, -30, 40, -30, -10},
			{-10, -30, 30, -30, -10},
			{-30, -30, 0, -30, -30},
			{-30, -50, -30, -50, -30}

	};

	public World()
	{

		board = new String[rows][columns];
		
		/* represent the board
		
		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		*/
		
		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";
		
		// setting the black player's chess parts
		
		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";
		
		board[0][0] = "BP";
		board[0][columns-1] = "BP";
		
		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";
		
		// black king
		board[0][columns/2] = "BK";
		
		// setting the white player's chess parts
		
		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";
		
		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";
		
		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";
		
		// white king
		board[rows-1][columns/2] = "WK";
		
		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";
		
		availableMoves = new ArrayList<String>();
	}
	
	public void setMyColor(int myColor)
	{
		if (myColor==0 && this.maxdepth%2==0){
			this.maxdepth = this.maxdepth +1;
		}
		if (myColor==1 && this.maxdepth%2==1){
			this.maxdepth = this.maxdepth +1;
		}
		this.myColor = myColor;
	}
	
	public String selectAction()
	{
		availableMoves = new ArrayList<String>();
				
		if(myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();
		
		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();

		double a=-1000,b=1000;
		if (staticminmax){
			//calling min max
			double ev_move = this.minmax(board,maxdepth, true,a,b);
		}else{
			// Trying Monte Carlo instead of minmax
			MonteCarloTreeSearch MTS = new MonteCarloTreeSearch();
			chosenMove = MTS.findNextMove(board, myColor,this);
		}
		System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------");
		if (chosenMove == null) {
			System.out.println("Random Move");
			return selectRandomAction();
		}
		else {
			System.out.println("Chosenmove : "+chosenMove);
			return chosenMove;
		}
	}

	private void whiteMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));

				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{

					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i-1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i-1) + Integer.toString(j);

						availableMoves.add(move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j-1);

							availableMoves.add(move);
						}
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {

							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j+1);
							availableMoves.add(move);
						}
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i-(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i+(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j-(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j+(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j);

							availableMoves.add(move);
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j);

							availableMoves.add(move);
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j-1);

							availableMoves.add(move);
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j+1);

							availableMoves.add(move);
						}
					}
				}
			}
		}
	}

	private void blackMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));

				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{

					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i+1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i+1) + Integer.toString(j);

						availableMoves.add(move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j-1);

							availableMoves.add(move);
						}
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j+1);

							availableMoves.add(move);
						}



					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i-(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i+(k+1)) + Integer.toString(j);

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j-(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j+(k+1));

						availableMoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j);

							availableMoves.add(move);
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j);

							availableMoves.add(move);
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j-1);

							availableMoves.add(move);
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j+1);

							availableMoves.add(move);
						}
					}
				}
			}
		}
	}


	public ArrayList<String> whiteMoves2(String[][] tmpboard)
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
		ArrayList<String> tempmoves = new ArrayList<String>();

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(tmpboard[i][j].charAt(0));

				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(tmpboard[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{

					// check if it can move one vertical position ahead
					firstLetter = Character.toString(tmpboard[i-1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i-1) + Integer.toString(j);

						tempmoves.add(move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(tmpboard[i-1][j-1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j-1);

							tempmoves.add(move);
						}
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(tmpboard[i-1][j+1].charAt(0));
						if(!(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))) {

							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j+1);
							tempmoves.add(move);
						}
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(tmpboard[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i-(k+1)) + Integer.toString(j);

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(tmpboard[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i+(k+1)) + Integer.toString(j);

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(tmpboard[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j-(k+1));

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(tmpboard[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("W"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j+(k+1));

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(tmpboard[i-1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j);

							tempmoves.add(move);
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(tmpboard[i+1][j].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j);

							tempmoves.add(move);
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(tmpboard[i][j-1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j-1);

							tempmoves.add(move);
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(tmpboard[i][j+1].charAt(0));

						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j+1);

							tempmoves.add(move);
						}
					}
				}
			}
		}
		return tempmoves;
	}

	public ArrayList<String> blackMoves2(String[][] tmpboard)
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
		ArrayList<String> tempmoves = new ArrayList<String>();

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(tmpboard[i][j].charAt(0));

				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;

				// check the kind of the white chess part
				secondLetter = Character.toString(tmpboard[i][j].charAt(1));

				if(secondLetter.equals("P"))	// it is a pawn
				{

					// check if it can move one vertical position ahead
					firstLetter = Character.toString(tmpboard[i+1][j].charAt(0));

					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i+1) + Integer.toString(j);

						tempmoves.add(move);
					}

					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(tmpboard[i+1][j-1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j-1);

							tempmoves.add(move);
						}
					}

					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(tmpboard[i+1][j+1].charAt(0));

						if(!(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))) {
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j+1);

							tempmoves.add(move);
						}



					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;

						firstLetter = Character.toString(tmpboard[i-(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i-(k+1)) + Integer.toString(j);

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;

						firstLetter = Character.toString(tmpboard[i+(k+1)][j].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i+(k+1)) + Integer.toString(j);

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;

						firstLetter = Character.toString(tmpboard[i][j-(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j-(k+1));

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}

					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;

						firstLetter = Character.toString(tmpboard[i][j+(k+1)].charAt(0));

						if(firstLetter.equals("B"))
							break;

						move = Integer.toString(i) + Integer.toString(j) +
								Integer.toString(i) + Integer.toString(j+(k+1));

						tempmoves.add(move);

						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(tmpboard[i-1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i-1) + Integer.toString(j);

							tempmoves.add(move);
						}
					}

					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(tmpboard[i+1][j].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i+1) + Integer.toString(j);

							tempmoves.add(move);
						}
					}

					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(tmpboard[i][j-1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j-1);

							tempmoves.add(move);
						}
					}

					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(tmpboard[i][j+1].charAt(0));

						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) +
									Integer.toString(i) + Integer.toString(j+1);

							tempmoves.add(move);
						}
					}
				}
			}
		}
		return tempmoves;
	}

	
	private String selectRandomAction()
	{		
		Random ran = new Random();
		int x = ran.nextInt(availableMoves.size());
		
		return availableMoves.get(x);
	}
	
	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}
	
	public void makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY)
	{
		String chesspart = Character.toString(board[x1][y1].charAt(1));
		
		boolean pawnLastRow = false;
		
		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}
		
		// otherwise
		if(!pawnLastRow)
		{
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}
		
		// check if a prize has been added in the game
		if(prizeX != noPrize)
			board[prizeX][prizeY] = "P";
	}

	/* Our minmax algorithm */
	public double minmax( String tmpboard[][],int depth, boolean maxPlayer,double a,double b) {
		ArrayList<String> availableMove = null;
		String move;
		String[][] board2 = null;
		board2 = new String[rows][columns];
		int[] moveInt = new int[4];
		for (int i = 0; i < rows; i++) {
			board2[i] = Arrays.copyOf(tmpboard[i], columns);
		}
		if (depth == 0 || terminalState(tmpboard)) {
			return evaluate(tmpboard);
		}
		if (depth == 0){
			if (maxPlayer) {
				if (myColor == 0){
					return Quiesce(board2, myColor, a, b, 3);
				}else {
					return Quiesce(board2, myColor, a, b, 4);
				}
			} else { // min player here
				if (myColor == 0)
					return Quiesce(board2, 1, a, b, 3);
				else
					return Quiesce(board2, 0, a, b, 4);
			}
		}
		if (myColor==0){
			if(maxPlayer) {        // I am the white player
				availableMove = this.whiteMoves2(board2);
			}else {                    // he is the black player
				availableMove = this.blackMoves2(board2);
			}
		}else{
			if(maxPlayer) {        // I am the black player
				availableMove = this.blackMoves2(board2);
			}else {                    // he is the white player
				availableMove = this.whiteMoves2(board2);
			}
		}
		if (maxPlayer) {
			double value = -100000;
			for (int i = 0; i < availableMove.size(); i++) {
				move=availableMove.get(i);
				for (int j = 0; j < move.length(); j++) {
					moveInt[j] = Integer.parseInt(Character.toString(move.charAt(j)));
				}
				if ((tmpboard[moveInt[0]][moveInt[1]]=="WP" && moveInt[2]==0) || (tmpboard[moveInt[0]][moveInt[1]]=="BP" && moveInt[2]==6) ){
					board2[moveInt[0]][moveInt[1]]=" ";
					board2[moveInt[2]][moveInt[3]]=" ";
				}else{
					board2[moveInt[2]][moveInt[3]]=tmpboard[moveInt[0]][moveInt[1]];
					board2[moveInt[0]][moveInt[1]]=" ";
				}
				double tempvalue = minmax(board2,depth -1, false,a ,b);
				if (tempvalue > value) {
					value = tempvalue;
					if (depth==maxdepth){
						chosenMove= move;
					}
				}
				a= Math.max(a, value);
				if (b<=a){
					break;
				}
			}
			return value;
		}
		else {
			double value = 100000;
			for (int i = 0; i < availableMove.size(); i++) {
				move=availableMove.get(i);
				for (int j = 0; j < move.length(); j++) {
					moveInt[j] = Integer.parseInt(Character.toString(move.charAt(j)));
				}
				if ((tmpboard[moveInt[0]][moveInt[1]]=="WP" && moveInt[2]==0)||(tmpboard[moveInt[0]][moveInt[1]]=="BP" && moveInt[2]==6) ){
					board2[moveInt[0]][moveInt[1]]=" ";
					board2[moveInt[2]][moveInt[3]]=" ";
				}else{
					board2[moveInt[2]][moveInt[3]]=tmpboard[moveInt[0]][moveInt[1]];
					board2[moveInt[0]][moveInt[1]]=" ";
				}
				double tempvalue = minmax(board2,depth -1, true,a,b);
				if (tempvalue < value) {
					value = tempvalue;
				}
				b= Math.min(b, value);
				if (b<=a){
					break;
				}
			}
			return value;
		}
	}

	/* Checking if we are on terminal state */
	public boolean terminalState(String tmpboard[][]) {

		int kings = 0;
		int other = 0;
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 5; col++) {
				if (tmpboard[row][col].equals("BK") || tmpboard[row][col].equals("WK")) {
					kings++;
				}else if (!tmpboard[row][col].equals(" ") && !tmpboard[row][col].equals("P")) {
					other++;
				}
			}
		}
		if ((kings == 2 && other == 0) || kings <= 1) {
			return true;
		}
		else {
			return false;
		}
	}

	public double Quiesce (String[][] tmpboard, int side, double alpha, double beta, int depth) {
		int [] moveInt = new int [4];
		String move;
		String[][] board2 = null;
		board2 = new String[rows][columns];
		for (int i = 0; i < rows; i++){
			board2[i] = Arrays.copyOf(tmpboard[i], columns);
		}

		// evaluate the current state
		double stand_pat = evaluate(tmpboard);

		if (depth == 0) return stand_pat;
		if (stand_pat >= beta) {
			return beta;
		}
		if (alpha < stand_pat) {
			alpha = stand_pat;
		}
		ArrayList<String> tmpavailableMove = null;
		// generate available moves
		if (side == 0) {
			tmpavailableMove=whiteMoves2(tmpboard);
		}else {
			tmpavailableMove=blackMoves2(tmpboard);
		}

		double score;
		for (int i = 0; i < tmpavailableMove.size(); i++) {
			// analyzing the move
			int action_int = Integer.parseInt(tmpavailableMove.get(i));
			int y2 = action_int % 10;
			int x2 = (action_int % 100 - y2) / 10;

			// if it is a non capture move check the next one
			if (tmpboard[x2][y2].charAt(0)!='W' && tmpboard[x2][y2].charAt(0)!='B') {
				continue;
			}
			move=tmpavailableMove.get(i);
			for (int j = 0; j < move.length(); j++) {
				moveInt[j] = Integer.parseInt(Character.toString(move.charAt(j)));
			}
			if ((tmpboard[moveInt[0]][moveInt[1]]=="WP" && moveInt[2]==0) || (tmpboard[moveInt[0]][moveInt[1]]=="BP" && moveInt[2]==6) ){
				board2[moveInt[0]][moveInt[1]]=" ";
				board2[moveInt[2]][moveInt[3]]=" ";
			}else{
				board2[moveInt[2]][moveInt[3]]=tmpboard[moveInt[0]][moveInt[1]];
				board2[moveInt[0]][moveInt[1]]=" ";
			}
			// calling recursively
			if (side == 0) {
				score = -Quiesce(board2, 1, -beta, -alpha, depth-1);
			}else {
				score = -Quiesce (board2, 0, -beta, -alpha, depth-1);
			}
			// we hit a hard bound ?
			if (score >= beta) {
				return beta;
			}
			// updating alpha
			if (score > alpha) {
				alpha = score;
			}
		}
		return alpha;
	}
	/* Basic move evaluation based on points
	 * String move = "3758"
	 * row/col --> row/col
	 * */
//	public double evaluate(String tmpboard[][]) {
//		for ()tmpboard[][]
//	}
	// estimates the current state's value
	/* based on the linear function
	 * 1000(K-K')+500(R-R')+100(P-P')+10(M-M')+50(B-B'+I-I'+D-D')
	 */
    /*
        Checking our alive kings,rooks,pawns, checking enemy's alive r_kings,r_rooks,r_pawns
        Checking backward_pawns, which are pawns that are left far behind other allied forces and can't help in battle
        Checking doubled_pawns, which are pawns that are in pairs (Can protect each other)
        Checking isolated_pawns, which are pawns that are not protected from other allied forces
        Checking mobility. Encourage our agent to choose a move that ensures that he has more options for the next move
         */
	public double evaluate (String tmpboard[][]) {
		// TODO Need to fix /update the evaluation function and maybe quisce
		double result = 0;
		int king = 0;
		int rooks = 0;
		int pawns = 0;
		int r_king = 0;
		int r_rooks = 0;
		int r_pawns = 0;
		int backward_pawns = 0;
		int doubled_pawns = 0;
		int isolated_pawns = 0;
		int r_backward_pawns = 0;
		int r_doubled_pawns = 0;
		int r_isolated_pawns = 0;

		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 5; col++) {
				if (tmpboard[row][col].equals("WK")) {
					king++;
				}else if (tmpboard[row][col].equals("BK")) {
					r_king++;
				}else if (tmpboard[row][col].equals("WR")) {
					rooks ++;
				}else if (tmpboard[row][col].equals("BR")) {
					r_rooks++;
				}else if (tmpboard[row][col].equals("WP")) {
					pawns++;
				}else if (tmpboard[row][col].equals("BP")) {
					r_pawns++;
				}

				// count backward pawns
				if (tmpboard[row][col].equals("WP")) {
					backward_pawns++;
					isolated_pawns++;
					boolean flag = false;
					for (int r = row; r < 7; r++) {
						for (int c = (col == 0)?0:col - 1; c < ((col == 4)?4:col + 2); c++) {
							if (tmpboard[r][c].equals("WP") && col != c) {
								backward_pawns--;
								flag = true;
								break;
							}
						}
						if (flag) break;
					}
					flag = false;
					for (int r = 0; r < 7; r++) {
						for (int c = (col == 0)?0:col - 1; c < ((col == 4)?4:col + 2); c++) {
							if (tmpboard[r][c].equals("WP")&& col != c) {
								isolated_pawns--;
								flag = true;
								break;
							}
						}
						if (flag) break;
					}
				}else if (tmpboard[row][col].equals("BP")) {
					r_backward_pawns++;
					r_isolated_pawns++;
					boolean flag = false;
					for (int r = 0; r <= row; r++) {
						for (int c = (col == 0)?0:col - 1; c < ((col == 4)?4:col + 2); c++) {
							if (tmpboard[r][c].equals("BP") && col != c) {
								r_backward_pawns--;
								flag = true;
								break;
							}
						}
						if (flag) break;
					}
					flag = false;
					for (int r = 0; r < 7; r++) {
						for (int c = (col == 0)?0:col - 1; c < ((col == 4)?4:col + 2); c++) {
							if (tmpboard[r][c].equals("BP")&& col != c) {
								r_isolated_pawns--;
								flag = true;
								break;
							}
						}
						if (flag) break;
					}

				}

			}
		}
		// count double pawns
		for (int col = 0; col < 5; col++) {
			int tempb = 0;
			int tempw = 0;
			for (int row = 0; row < 7; row++) {
				if (tmpboard[row][col].equals("WP")){
					tempw++;
				}else if (tmpboard[row][col].equals("BP")){
					tempb++;
				}
			}
			if (tempw >= 2) doubled_pawns++;
			if (tempb >= 2) r_doubled_pawns++;

		}
		// compute mobility of each player (mobility = number of legal moves)
		int mobility = 0, r_mobility = 0;
		ArrayList<String> availableMove = null;

		availableMove=whiteMoves2(tmpboard);
		mobility= availableMove.size();
		availableMove.clear();
		availableMove=blackMoves2(tmpboard);
		r_mobility = availableMove.size();

		result += 20000 * (king - r_king);
		result += 1000 * (rooks - r_rooks);
		result += 100 * (pawns - r_pawns);
		result += 10 * (mobility - r_mobility);
		if (myColor == 1) { // in case we have black
			result = -result;
		}
		if (myColor == 0) {
			result += 50 * (doubled_pawns - r_doubled_pawns + isolated_pawns - r_isolated_pawns + backward_pawns - r_backward_pawns);
		}else {
			result += 50 * (-doubled_pawns + r_doubled_pawns - isolated_pawns + r_isolated_pawns - backward_pawns + r_backward_pawns);
		}

		// finally, we encourage pieces standing well and discourage pieces standing badly
		// e.g. at the start-middle of the game we encourage the king to stay behind the pawns
		// the regarding tables are pawn, rook, king_midgame and king_endgame as seen above
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 5; col++) {
				if (myColor == 0) {
					if (tmpboard[row][col].equals("WP")) {
						result += pawn[row][col];
					}else if (tmpboard[row][col].equals("WR")) {
						result += rook[row][col];
					}else if (tmpboard[row][col].equals("WK")) {
						if (rooks < 2 && pawns <= 4) {
							result += king_endgame[row][col];
						}else {
							result += king_midgame[row][col];
						}
					}
				}else {
					if (tmpboard[row][col].equals("BP")) {
						result += pawn[6-row][4-col];
					}else if (tmpboard[row][col].equals("BR")) {
						result += rook[6-row][4-col];
					}else if (tmpboard[row][col].equals("BK")) {
						if (rooks < 2 && pawns <= 4) {
							result += king_endgame[6-row][4-col];
						}else {
							result += king_midgame[6-row][4-col];
						}
					}
				}
			}
		}
		return result;
	}

}

