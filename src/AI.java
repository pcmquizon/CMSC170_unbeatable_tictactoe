import java.util.LinkedList;

class AI{
	private Symbol[][] board;
	private Symbol symbol;
	private Symbol opponent;
	private LinkedList<Action> acts;

	AI(){}

	AI(Symbol[][] board){
		this.board = board;
		this.acts = new LinkedList<Action>();
	}

	public void resetAI(){
		this.board = new Symbol[TicTacToe.SIZE][TicTacToe.SIZE];
		this.symbol = Symbol.O;
		this.opponent = Symbol.X;
		this.acts.clear();
	}

	public void setSymbol(Symbol sym){
		this.symbol = sym;
		this.opponent = ( sym == Symbol.X )? Symbol.O : Symbol.X;
	}

	public Symbol getSymbol(){
		return this.symbol;
	}

	public Symbol getOpponent(){
		return this.opponent;
	}

	LinkedList<Action> possibleActions(){
		LinkedList<Action> choices = new LinkedList<Action>();

		if( TicTacToe.goalState(this.symbol) || 
			TicTacToe.goalState(this.opponent) || 
			TicTacToe.drawState() ){
			return choices;
		}

		for(int i=0; i<TicTacToe.SIZE; i++){
			for(int j=0; j<TicTacToe.SIZE; j++){
				if( TicTacToe.board[i][j]!=Symbol.FREE ) continue;
				choices.add(new Action(i,j));
			}
		}

		return choices;
	}

	private Action minimax(int depth, Symbol player, int alpha, int beta){
		LinkedList<Action> choices = possibleActions();
		Action result = new Action();

		int score;
		int row=-1;
		int col=-1;

		if( choices.isEmpty() || depth==0 ){
			score = score();
			result = new Action(row, col, score);
			return result;
		}
		else{
			for(Action a : choices){

				//try move
				this.board[a.getX()][a.getY()] = player;
				TicTacToe.board[a.getX()][a.getY()] = player;

				//maximizing player, ai
				if( player==this.symbol ){
					score = minimax(depth-1, this.opponent, alpha, beta).getScore();

					if( score>alpha ){
						alpha = score;
						row = a.getX();
						col = a.getY();
					}
				}

				//minimizing player, us
				else{
					score = minimax(depth-1, this.symbol, alpha, beta).getScore();

					if( score<beta ){
						beta = score;
						row = a.getX();
						col = a.getY();
					}
				}

				//undo tried move
				this.board[a.getX()][a.getY()] = Symbol.FREE;
				TicTacToe.board[a.getX()][a.getY()] = Symbol.FREE;

				//cutoff
				if(alpha>=beta){
					break;
				}
			}
		}

		if(player==this.symbol){
			return new Action(row, col, alpha);	
		}
		else{
			return new Action(row, col, beta);	
		}
	}

	void printBoard(){
		for (int i=0; i<3; i++) {
			for(int j=0; j<3; j++){
				System.out.print(board[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public Action move(){
		Action act = new Action();
		act = minimax(2, this.symbol, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return act;
	}

	private int eval(int r0, int c0, int r1, int c1, int r2, int c2){
		int score = 0;

		//one square
		if( this.board[r0][c0]==this.symbol ){
			score = 1;
		}
		else if( this.board[r0][c0]==this.opponent ){
			score = -1;
		}

		//two squares
		if( this.board[r1][c1]==this.symbol ){
			if(score==1){
				score = 10;
			}
			else if( score==-1 ){	//opponent's symbol and mine
				score = 0;
			}
			else{					//empty
				score = 1;
			}
		}
		else if( this.board[r1][c1]==this.opponent ){
			if(score==-1){
				score = -10;
			}
			else if( score==1 ){	//opponent's symbol and mine
				score = 0;
			}
			else{
				score = -1;			//empty
			}
		}

		//three squares
		if( this.board[r2][c2]==this.symbol ){
			if(score>0){
				score *= 10;
			}
			else if( score<0 ){		//at least one opponent's symbol and mine
				score = 0;
			}
			else{					//empty
				score = 1;
			}
		}
		else if( this.board[r2][c2]==this.opponent ){
			if(score<0){
				score *=10;
			}
			else if( score>1 ){	//at least one opponent's symbol and mine
				score = 0;
			}
			else{					//empty
				score = -1;
			}
		}

		return score;
	}

	private int score(){	
		int score = 0;

		//check rows and cols
		for(int i=0; i<3; i++){
			score+=eval(i,0,i,1,i,2);
			score+=eval(0,i,1,i,2,i);
		}

		//check diagonals
		score+=eval(0,0,1,1,2,2);
		score+=eval(0,2,1,1,2,0);

		return score;
	}

}
