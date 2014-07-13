package maze;

import javax.swing.JPanel;
import java.awt.Graphics;


/**
 * The labyrinth itself.
 * <p> Contains cell array and initializes their rendering.
 * <p> Responds to the main window calls.
 */
class Field extends JPanel implements Runnable {
	final MainWindow.Callback mainWndCallback;
	final Cell cells[][];
	static int ROW;
	static int COL;

	Position start = null;
	Position goal = null;

	private FState fState = FState.GENERATION;
	private Thread thread = new Thread( this );


	/**
	 * Creates a new Field with cell array initialised by the default cell's state.
	 *
	 * @param totalRow the total number of rows in the maze
	 * @param totalCol the total number of columns in the maze
	 * @param callback object for communication with the main window
	 */
	Field( int totalRow, int totalCol, MainWindow.Callback callback ) {
		mainWndCallback = callback;
		ROW = totalRow;
		COL = totalCol;
		cells = new Cell[ ROW ][ COL ];
		for( int row = 0; row < ROW; row++ ) {
			for( int col = 0; col < COL; col++ ) {
				cells[ row ][ col ] = new Cell( row, col );
			}
		}
	}


	/**
	 * Paints all the cells with their current state.
	 *
	 * @param g the Graphics context will be transferred into a cell's method.
	 */
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		for( int row = 0; row < ROW; row++ ) {
			for( int col = 0; col < COL; col++ ) {
				cells[ row ][ col ].paint( g );
			}
		}
	}


	/**
	 * Represents visualization of a random maze generation or pathfinding.
	 */
	@Override
	public void run() {
		try {
			if( fState == FState.GENERATION ) {
				RandomMazeMaker.carvePassage( this, new Position( false ) );
				RandomMazeMaker.addExtraGates( this );
				RandomMazeMaker.setStartAndGoal( this );
			} else {
				if( AStar.findPath( this ) ) {
					mainWndCallback.setPathLength( "" + cells[ goal.row ][ goal.col ].g );
				}
			}
		} catch( InterruptedException ignored ) {}
	}


	/**
	 * Displays one of the predefined test maps.
	 * <p> This method is called only by the main window of the program.
	 *
	 * @param mapNumber the number of the map to be shown
	 */
	void showTestMap( int mapNumber ) {
		if( ROW < 25 || ROW < 25 ) {
			return;
		}
		if( thread.isAlive() ) {
			thread.interrupt();
		}
		for( int row = 0; row < ROW; row++ ) {
			for( int col = 0; col < COL; col++ ) {
				cells[ row ][ col ] = new Cell( row, col, Cell.State.PASSAGE );
			}
		}
		switch( mapNumber ) {
			case 1: // Only start and goal present on this map
				break;
			case 2: // ~shape: '|'
				for( int row = 10; row < 20; row++ ) {
					cells[ row ][ 14 ].rightWall = true;
					cells[ row ][ 15 ].leftWall = true;
				}
				break;
			case 3: // ~shape: '_____|'
				for( int row = 13; row < 16; row++ ) {
					cells[ row ][ 20 ].rightWall = true;
					cells[ row ][ 21 ].leftWall = true;
				}
				for( int col = 7; col < 21; col++ ) {
					cells[ 15 ][ col ].bottomWall = true;
					cells[ 16 ][ col ].topWall = true;
				}
				break;
		}

		start = new Position( 15, 7 );
		cells[ start.row ][ start.col ].state = Cell.State.START;

		goal = new Position( 15, 23 );
		cells[ goal.row ][ goal.col ].state = Cell.State.GOAL;

		repaint();
	}


	/**
	 * Resets cells in cell array to their default state (BLOCK)
	 * and starts generation algorithm via running a new thread.
	 * <p> This method is called only by the main window of the program.
	 */
	void generateMaze() {
		if( thread.isAlive() ) {
			thread.interrupt();
		}
		for( int row = 0; row < ROW; row++ ) {
			for( int col = 0; col < COL; col++ ) {
				cells[ row ][ col ] = new Cell( row, col );
			}
		}
		fState = FState.GENERATION;
		thread = new Thread( this );
		thread.start();
	}


	/**
	 * Resets cells in cell array to the last generated state (PASSAGE)
	 * and starts pathfinding algorithm via running a new thread.
	 * <p> This method is called only by the main window of the program.
	 */
	void findPath() {
		if( start == null || goal == null ) return;
		if( thread.isAlive() ) {
			if( fState == FState.GENERATION ) {
				return;
			} else {
				thread.interrupt();
			}
		}
		for( int row = 0; row < ROW; row++ ) {
			for( int col = 0; col < COL; col++ ) {
				cells[ row ][ col ].state = Cell.State.PASSAGE;
				cells[ row ][ col ].dirStart = null;
				cells[ row ][ col ].dirGoal = null;
			}
		}
		cells[ start.row ][ start.col ].state = Cell.State.START;
		cells[ goal.row ][ goal.col ].state = Cell.State.GOAL;

		fState = FState.PATHFINDING;
		thread = new Thread( this );
		thread.start();
	}


	/**
	 * Used when running visualisation to make a pause for a custom time.
	 *
	 * @throws InterruptedException
	 */
	void sleep() throws InterruptedException {
		int animationTime = mainWndCallback.getAnimationSpeed( fState );
		long delay = ( long ) ( Math.pow( 2, 10 - animationTime ) - 1 );
		if( delay > 0 ) Thread.sleep( delay );
	}

	/**
	 * Determines the global state of the program which is used during its running.
	 * <p> May be either <code>GENERATION</code> of a new random maze or
	 * <code>PATHFINDING</code>
	 */
	static enum FState {
		GENERATION, PATHFINDING
	}
}
