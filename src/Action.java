class Action{
	private int x;
	private int y;
	private int score;

	Action(){}

	Action(int x, int y){
		this.x = x;
		this.y = y;
	}

	Action(int x, int y, int score){
		this.x = x;
		this.y = y;
		this.score = score;
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}

	public int getScore(){
		return this.score;
	}
}