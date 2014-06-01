package maze;

import javax.swing.*;
import java.awt.*;


/**
 * Determines the global state of the program which is used during its running.
 * <p> May be either <code>GENERATION</code> of a new random maze or
 * <code>PATHFINDING</code>
 */
enum GlobalState {
	GENERATION, PATHFINDING
}


/**
 * The labyrinth itself.
 * <p> Contains cell array and initializes their rendering.
 * <p> Responds to the main window calls.
 */
public class Field extends JPanel implements Runnable {
	final MainWindow mainWindow;
	final Cell cells[][] = new Cell[ Setup.MAZE_ROW ][ Setup.MAZE_COL ];

	Position start = null;
	Position goal = null;

	private GlobalState gState = GlobalState.GENERATION;
	private Thread thread = new Thread( this );


	/**
	 * Creates a new Field with cell array initialised by the default cell's state.
	 *
	 * @param parentMainWindow the link to the parent to save
	 */
	Field( MainWindow parentMainWindow ) {
		mainWindow = parentMainWindow;
		for( int row = 0; row < Setup.MAZE_ROW; row++ ) {
			for( int col = 0; col < Setup.MAZE_COL; col++ ) {
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
		for( int row = 0; row < Setup.MAZE_ROW; row++ ) {
			for( int col = 0; col < Setup.MAZE_COL; col++ ) {
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
			if( gState == GlobalState.GENERATION ) {
				if( Setup.MAZE_ROW < 3 || Setup.MAZE_ROW < 3 ) {
					return;
				}
				RandomMazeMaker.carvePassage( this, new Position( false ) );
				RandomMazeMaker.addExtraGates( this );
				RandomMazeMaker.setStartAndGoal( this );
			} else {
				if( AStar.findPath( this ) ) {
					mainWindow.setPathLength( "" + cells[ goal.row ][ goal.col ].g );
				}
			}
		} catch( InterruptedException e ) {}
	}


	/**
	 * Displays one of the predefined test maps.
	 * <p> This method is called only by the main window of the program.
	 *
	 * @param mapNumber the number of the map to be shown
	 */
	void showTestMap( int mapNumber ) {
		if( Setup.MAZE_ROW < 25 || Setup.MAZE_ROW < 25 ) {
			return;
		}
		if( thread.isAlive() ) {
			thread.interrupt();
		}
		for( int row = 0; row < Setup.MAZE_ROW; row++ ) {
			for( int col = 0; col < Setup.MAZE_COL; col++ ) {
				cells[ row ][ col ] = new Cell( row, col, State.PASSAGE );
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
		cells[ start.row ][ start.col ].state = State.START;

		goal = new Position( 15, 23 );
		cells[ goal.row ][ goal.col ].state = State.GOAL;

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
		for( int row = 0; row < Setup.MAZE_ROW; row++ ) {
			for( int col = 0; col < Setup.MAZE_COL; col++ ) {
				cells[ row ][ col ] = new Cell( row, col );
			}
		}
		gState = GlobalState.GENERATION;
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
			if( gState == GlobalState.GENERATION ) {
				return;
			} else {
				thread.interrupt();
			}
		}
		for( int row = 0; row < Setup.MAZE_ROW; row++ ) {
			for( int col = 0; col < Setup.MAZE_COL; col++ ) {
				cells[ row ][ col ].state = State.PASSAGE;
				cells[ row ][ col ].dirStart = null;
				cells[ row ][ col ].dirGoal = null;
			}
		}
		cells[ start.row ][ start.col ].state = State.START;
		cells[ goal.row ][ goal.col ].state = State.GOAL;

		gState = GlobalState.PATHFINDING;
		thread = new Thread( this );
		thread.start();
	}


	/**
	 * Used when running visualisation to make a pause for a custom time.
	 *
	 * @throws InterruptedException
	 */
	void sleep() throws InterruptedException {
		int animationTime = mainWindow.getAnimationSpeed( gState );
		long delay = ( long ) ( Math.pow( 2, Setup.ANIMATION_INSTANT - animationTime ) - 1 );
		if( delay > 0 ) Thread.sleep( delay );
	}
}
