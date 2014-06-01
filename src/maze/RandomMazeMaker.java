package maze;

import java.util.Random;

/**
 * Needed to create a random maze.
 */
public abstract class RandomMazeMaker {
	static final Random rand = new Random();

	/**
	 * Recursive backtracker algorithm.
	 * <p> For more information see
	 * <a href="http://www.astrolog.org/labyrnth/algrithm.htm">Maze algorithms</a>
	 *
	 * @param field the labyrinth
	 * @param pos   the new position to be carved
	 * @throws InterruptedException
	 */
	public static void carvePassage( Field field, Position pos ) throws InterruptedException {

		field.cells[ pos.row ][ pos.col ].state = State.MARKED;

		field.repaint();
		field.sleep();

		while( hasNextBlockCell( field.cells, pos ) ) {
			int nextRow = pos.row;
			int nextCol = pos.col;

			// if (random) AND there are at least one suitable vertical neighbor
			// or if there are no horizontal suitable neighbors
			if( rand.nextBoolean() &&
					( isBlock( field.cells, pos.row - 1, pos.col ) || isBlock( field.cells, pos.row + 1, pos.col ) ) ||
					!( isBlock( field.cells, pos.row, pos.col - 1 ) ||
							isBlock( field.cells, pos.row, pos.col + 1 ) ) ) {

				// if (random) AND if the state of the bottom cell is 'BLOCK'
				// or if the top cell has already been carved
				if( rand.nextBoolean() && isBlock( field.cells, pos.row + 1, pos.col ) ||
						!isBlock( field.cells, pos.row - 1, pos.col ) ) {
					nextRow++;
					field.cells[ pos.row ][ pos.col ].bottomWall = false;
					field.cells[ nextRow ][ pos.col ].topWall = false;
				} else {
					nextRow--;
					field.cells[ pos.row ][ pos.col ].topWall = false;
					field.cells[ nextRow ][ pos.col ].bottomWall = false;
				}
			} else {

				// if (random) AND if the state of the right cell is 'BLOCK'
				// or if the left cell has already been carved
				if( rand.nextBoolean() && isBlock( field.cells, pos.row, pos.col + 1 ) ||
						!isBlock( field.cells, pos.row, pos.col - 1 ) ) {
					nextCol++;
					field.cells[ pos.row ][ pos.col ].rightWall = false;
					field.cells[ pos.row ][ nextCol ].leftWall = false;
				} else {
					nextCol--;
					field.cells[ pos.row ][ pos.col ].leftWall = false;
					field.cells[ pos.row ][ nextCol ].rightWall = false;
				}
			}

			carvePassage( field, new Position( nextRow, nextCol ) );
		}

		field.cells[ pos.row ][ pos.col ].state = State.PASSAGE;

		field.repaint();
		field.sleep();
	}


	/**
	 * Checks if there is at least one suitable (BLOCK) cell next to the specified one.
	 *
	 * @param cells cell array
	 * @param pos   the position of the cell to be checked
	 * @return
	 */
	private static boolean hasNextBlockCell( Cell[][] cells, Position pos ) {
		return ( pos.col - 1 >= 0 && cells[ pos.row ][ pos.col - 1 ].state == State.BLOCK ) ||
				( pos.col + 1 < Setup.MAZE_COL && cells[ pos.row ][ pos.col + 1 ].state == State.BLOCK ) ||
				( pos.row - 1 >= 0 && cells[ pos.row - 1 ][ pos.col ].state == State.BLOCK ) ||
				( pos.row + 1 < Setup.MAZE_ROW && cells[ pos.row + 1 ][ pos.col ].state == State.BLOCK );
	}


	/**
	 * Checks if the cell specified by the column and the row can be carved.
	 *
	 * @param cells cell array
	 * @param row   the row to be checked
	 * @param col   the column to be checked
	 */
	private static boolean isBlock( Cell[][] cells, int row, int col ) {
		return row >= 0 && row < Setup.MAZE_ROW &&
				col >= 0 && col < Setup.MAZE_COL &&
				cells[ row ][ col ].state == State.BLOCK;
	}


	/**
	 * Makes some extra gates.
	 * <p> Does it only if there is at least one border-line next to the each 'end'
	 * of making gate, otherwise skips the iteration.
	 * Tries to make one horizontal and one vertical gate in each iteration.
	 *
	 * @param field the labyrinth
	 * @throws InterruptedException
	 */
	public static void addExtraGates( Field field ) throws InterruptedException {
		Cell[][] cells = field.cells;
		for( int i = 0; i < field.mainWindow.getNumberOfGates(); i++ ) {
			Position p = new Position( true );
			if( cells[ p.row ][ p.col ].rightWall &&
					( cells[ p.row ][ p.col ].topWall ||
							cells[ p.row - 1 ][ p.col ].rightWall ||
							cells[ p.row ][ p.col + 1 ].topWall ) &&
					( cells[ p.row ][ p.col ].bottomWall ||
							cells[ p.row + 1 ][ p.col ].rightWall ||
							cells[ p.row ][ p.col + 1 ].bottomWall ) ) {
				cells[ p.row ][ p.col ].rightWall = false;
				cells[ p.row ][ p.col + 1 ].leftWall = false;
				field.repaint();
				field.sleep();
			}
			p = new Position( true );
			if( cells[ p.row ][ p.col ].bottomWall &&
					( cells[ p.row ][ p.col ].leftWall ||
							cells[ p.row ][ p.col - 1 ].bottomWall ||
							cells[ p.row + 1 ][ p.col ].leftWall ) &&
					( cells[ p.row ][ p.col ].rightWall ||
							cells[ p.row ][ p.col + 1 ].bottomWall ||
							cells[ p.row + 1 ][ p.col ].rightWall ) ) {
				cells[ p.row ][ p.col ].bottomWall = false;
				cells[ p.row + 1 ][ p.col ].topWall = false;
				field.repaint();
				field.sleep();
			}
		}
	}

	/**
	 * Sets the start and the goal cell in the maze.
	 *
	 * @param field the labyrinth
	 */
	public static void setStartAndGoal( Field field ) {
		field.start = new Position( false );
		field.goal = new Position( false );
		while( field.start.equals( field.goal ) ) {
			field.goal = new Position( false );
		}

		field.cells[ field.start.row ][ field.start.col ].state = State.START;
		field.cells[ field.goal.row ][ field.goal.col ].state = State.GOAL;
		field.repaint();
	}
}
