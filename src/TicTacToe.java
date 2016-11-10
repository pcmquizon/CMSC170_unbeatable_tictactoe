import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage; 
import java.awt.Image;
import java.awt.Window;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO; 
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TicTacToe  extends JFrame 
						implements  MouseListener, 
									ActionListener{

	public static final int SIZE = 3;

	public static Status status = Status.NEW;
	public static Symbol player = Symbol.X;
	public static Symbol[][] board = new Symbol[SIZE][SIZE];
	
	public AI bot;
	
	private boolean human=true;
	private int x_count = 0;
	private int o_count = 0;

	private JButton[][] btns = new JButton[SIZE][SIZE];
	private Color background = new Color(244, 241, 224);

	private JFrame frame;
	private JPanel deck;
	private JPanel game_bg;
	private Window mainFrame;

	private BufferedImage human_prompt;
	private File human_prompt_file = new File("assets/labels/h_turn.png");
	private BufferedImage human_avatar;
	private File human_avatar_file = new File("assets/player/human.png");

	private BufferedImage ai_prompt;
	private File ai_prompt_file = new File("assets/labels/r_turn.png");
	private BufferedImage ai_avatar;
	private File ai_avatar_file = new File("assets/player/robot.png");

	private BufferedImage circle;
	private ImageIcon circle_icon;
	private File circle_file = new File("assets/game/circle.png");

	private BufferedImage cross;
	private ImageIcon cross_icon;
	private File cross_file = new File("assets/game/cross.png");

	private BufferedImage game_board;
	private File game_board_file = new File("assets/game/board.png");

	private BufferedImage play_screen;
	private File play_file = new File("assets/screens/play.png");

	private BufferedImage splash_screen;
	private File splash_file = new File("assets/screens/splash.png");

	private BufferedImage about_screen;
	private File about_file = new File("assets/screens/about.png");

	private BufferedImage help_screen;
	private File help_file = new File("assets/screens/help.png");

	private AudioInputStream game_start_audio;
	private Clip game_start_clip;
	private File game_start_file = new File("assets/audio/game_start.wav");
	
	private AudioInputStream game_over_audio;
	private Clip game_over_clip;
	private File game_over_file = new File("assets/audio/game_over.wav");
	
	private AudioInputStream select_btn_audio;
	private Clip select_btn_clip;
	private File select_btn_file = new File("assets/audio/select.wav");
	
	private AudioInputStream back_btn_audio;
	private Clip back_btn_clip;
	private File back_btn_file = new File("assets/audio/back.wav");

	

	public TicTacToe(){
		super("Tic-Tac-Toe");
		this.initialize();
	}

	private void initializeBoard(){
		for(int i=0; i<SIZE;i++){
			for(int j=0; j<SIZE; j++){
				this.board[i][j] = Symbol.FREE;
				btns[i][j] = new JButton("");
				btns[i][j].addActionListener(this);
				btns[i][j].setBorderPainted(false);
				btns[i][j].setContentAreaFilled(false);
				game_bg.add(btns[i][j]);
			}
		}
		bot = new AI(this.board);

	}

	private void initialize(){
		this.initializeFiles();
		this.initializeComponents();
		this.setListeners();
		this.initializeFrame();
		this.initializeBoard();
	}

	private void initializeFiles(){
		try{		
			//picture reading
			splash_screen = ImageIO.read(splash_file);
			about_screen = ImageIO.read(about_file);
			help_screen = ImageIO.read(help_file);
			play_screen = ImageIO.read(play_file);

			human_prompt = ImageIO.read(human_prompt_file);
			human_avatar = ImageIO.read(human_avatar_file);
			
			ai_prompt = ImageIO.read(ai_prompt_file);
			ai_avatar = ImageIO.read(ai_avatar_file);
			
			cross = ImageIO.read(cross_file);
			circle = ImageIO.read(circle_file);
			game_board = ImageIO.read(game_board_file);

		}catch(Exception e){
			e.printStackTrace();
		}

		cross_icon = new ImageIcon( (Image)cross );
		circle_icon = new ImageIcon( (Image)circle );
	}

	private void initializeComponents(){
		deck = new JPanel(){
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(splash_screen, 0, 0, this);
			}
		};			
		
		frame = new JFrame("Playing Tic-Tac-Toe");
		frame.setLayout(new GridLayout(SIZE,SIZE));

		game_bg = new JPanel(){
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(game_board, 0, 0, this);
			}
		};		
		game_bg.setLayout(new GridLayout(SIZE,SIZE));
		game_bg.setBackground(background);
	}

	private void setListeners(){
		deck.addMouseListener(this);
		frame.addWindowListener(new WindowAdapter() {
		@Override
			public void windowClosing(WindowEvent windowEvent) {
				frame.dispose();
				resetToDefault();
			}
		});
	}

	private void initializeFrame(){
		this.setContentPane(deck);
		this.setPreferredSize(new Dimension(610,680));
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);

		frame.setContentPane(game_bg);
		frame.setPreferredSize(new Dimension(480,480));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		mainFrame = SwingUtilities.getWindowAncestor(deck);
	}

	public void prompt_user(){
		Object[] options = 
			{"Sure!",
			"No, thanks.",
			"I'll be back"};
		
		int n = JOptionPane.showOptionDialog(
					this,
					"Would you like to go first?",
					"A Silly Question",
					
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);

		if( n==JOptionPane.YES_OPTION ){
			playAppSFX("game_start");
			startGame(true);
		}
		else if( n==JOptionPane.NO_OPTION ){
			playAppSFX("game_start");
			startGame(false);
		}
		else if( n==JOptionPane.CANCEL_OPTION ){ 
			playAppSFX("back");
			frame.dispose();
			renderScreen(splash_screen);
		}
	}

	private void startGame(boolean human_first){
		mainFrame.setVisible(false);
		status = Status.INGAME;

		if(human_first){
			player = Symbol.X;
			bot.setSymbol(Symbol.O);
			human = true;
		}
		else{
			player = Symbol.X;
			bot.setSymbol(Symbol.X);
			human = false;
			botTime();
		}

	}

	private void renderScreen(BufferedImage screen){
		deck.getGraphics().drawImage((Image)screen, 0,0, null);
	}

	public static Symbol togglePlayer(Symbol sym){
		if(sym==Symbol.X) return Symbol.O;
		return Symbol.X;
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

	void printPossibleMoves(Symbol player){
		State s = new State(board);
		System.out.println(player+"'s possible moves");
		LinkedList<Action> acts = bot.possibleActions();
		for(Action a : acts){
			System.out.println("("+a.getX()+","+a.getY()+")");
		}
	}

	private void endGame(Status s, String msg){
		playAppSFX("game_over");
		status = s;
		JOptionPane.showMessageDialog(frame,msg);
		frame.dispose();

		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);

		resetToDefault();
	}

	private void resetToDefault(){
		status = Status.NEW;
		player = Symbol.X;
		human = true;

		o_count = 0;
		x_count = 0;

		reInitializeBoard();

		bot.resetAI();
		bot = new AI(this.board);
	}

	private void reInitializeBoard(){
		for(int i=0; i<SIZE; i++){
			for(int j=0; j<SIZE; j++){
				btns[i][j].setText("");
				btns[i][j].setIcon(null);
				board[i][j] = Symbol.FREE;
				btns[i][j].addActionListener(this);
			}
		}
	}

	private void printStats(){
		printPossibleMoves(player);
		printBoard();
	}

	private void checkGame(){

		if(status!=Status.INGAME) return;

		if( goalState( bot.getSymbol() ) ){
			endGame(Status.LOSE,"You lost!");
		}
		else if( goalState( bot.getOpponent() ) ){
			endGame(Status.WIN,"You won!");
		}
		else if( drawState() ){
			endGame(Status.DRAW,"Draw!");
		}
	}

	private void nextTurn(){
		player = togglePlayer(player);		//set turn
		human = !human;						//flip bot/human flag
	}

	public static boolean drawState(){
		for(int i=0; i<SIZE; i++){
			for(int j=0; j<SIZE; j++){
				if(board[i][j]==Symbol.FREE) return false;
			}
		}
		return true;
	}

	public boolean checkLine(Symbol top, Symbol mid, Symbol bot){
		if( top==mid && mid==bot && top==bot && 
			(top!=Symbol.FREE || mid!=Symbol.FREE || bot!=Symbol.FREE) ) 
			return true;
		return false;
	}

	public static boolean goalState(Symbol player){
		//check rows and cols
		for(int i=0; i<3; i++){
			if(player==board[i][0] && player==board[i][1] && player==board[i][2]){
				return true;
			}
			if(player==board[0][i] && player==board[1][i] && player==board[2][i]){
				return true;
			}
		}

		//check diagonals
		if( (player==board[0][0] && player==board[1][1] && player==board[2][2]) ||
			(player==board[0][2] && player==board[1][1] && player==board[2][0]) ){
			return true;
		}

		return false;
	}

	private void printTurnCount(){
		System.out.println("human="+o_count+"\nbot="+x_count);
	}

	private void printTurn(String plyr, int x, int y){
		System.out.println(plyr+"@"+x+","+y+"\n");
	}

	@Override
	public void actionPerformed(ActionEvent ae){
		int i=0;
		int j=0;

		//player
		if(human && status==status.INGAME){
			for (i=0; i<SIZE && human; i++){
				for (j=0; j<SIZE && human; j++){
					if(ae.getSource()==btns[i][j] && board[i][j]==Symbol.FREE ){

						if( btns[i][j].getActionListeners().length>0 ){
							o_count+=1;

							if (Math.abs(o_count-x_count)>1){
								return;
							}

							//update internal
							board[i][j]=player;

							//update view
							if(player==Symbol.X){
								btns[i][j].setIcon(cross_icon);
							}
							else{
								btns[i][j].setIcon(circle_icon);	
							}

							//disable
							btns[i][j].removeActionListener(this);

							checkGame();
							nextTurn();

							i=-1;
							j=-1;
							
						}
						
					}
				}
			}	
		}

		//bot
		if(!human && status==status.INGAME){
			botTime();
		}

	}

	private void botTime(){
		Action act = bot.move();
		
		x_count+=1;

		if ( Math.abs((o_count-x_count))>1 && 
			 btns[act.getX()][act.getY()].getActionListeners().length<=0 ){
			return;
		}

		//ai will updates internal
		board[act.getX()][act.getY()] = player;

		//ai updates view
		if(player==Symbol.X ){
			btns[act.getX()][act.getY()].setIcon(cross_icon);	
		}
		else{
			btns[act.getX()][act.getY()].setIcon(circle_icon);	
		}

		//ai disables button
		btns[act.getX()][act.getY()].removeActionListener(this);

		checkGame();
		nextTurn();
		
	}

	public void playAppSFX(String neededSFX){

		File file_to_open = null;
		switch(neededSFX){
			case "select":		
				try{
					select_btn_audio = AudioSystem.getAudioInputStream(select_btn_file);
					select_btn_clip = AudioSystem.getClip();
					select_btn_clip.open(select_btn_audio);
					select_btn_clip.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			
			case "back":
				try{
					back_btn_audio = AudioSystem.getAudioInputStream(back_btn_file);
					back_btn_clip = AudioSystem.getClip();
					back_btn_clip.open(back_btn_audio);
					back_btn_clip.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				break;

			case "game_start":
				try{
					game_start_audio = AudioSystem.getAudioInputStream(game_start_file);
					game_start_clip = AudioSystem.getClip();
					game_start_clip.open(game_start_audio);
					game_start_clip.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				break;

			case "game_over":
				try{
					game_over_audio = AudioSystem.getAudioInputStream(game_over_file);
					game_over_clip = AudioSystem.getClip();
					game_over_clip.open(game_over_audio);
					game_over_clip.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e){
		if( e.getY()>=525 && e.getY()<=625 ){

			if( e.getX()>=0 && e.getX()<=180 ){
					playAppSFX("select");
				if( status==Status.NEW ){
					frame.setVisible(true);
					prompt_user();
				}
			}
			else if( e.getX()>=180 && e.getX()<=415 ){
				playAppSFX("select");
				renderScreen(about_screen);
			}
			else if( e.getX()>=415 && e.getX()<=610 ){
				playAppSFX("select");		
				renderScreen(help_screen);
			}

		}
		if( e.getY()<=70 && e.getX()>=425 ){
			playAppSFX("back");
			renderScreen(splash_screen);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mousePressed(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e){}

	public static void main(String[] args){
		TicTacToe ttt = new TicTacToe();
	}
}
