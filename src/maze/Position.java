package maze;

import java.util.Random;

/**
 * The cell position.
 */
public class Position {
	static final Random rand = new Random();
	int row;
	int col;


	/**
	 * Creates a new random position with or without 1-cell indent.
	 *
	 * @param indent
	 */
	Position( boolean indent ) {
		row = ( indent ) ? 1 + rand.nextInt( Setup.MAZE_ROW - 2 ) : rand.nextInt( Setup.MAZE_ROW );
		col = ( indent ) ? 1 + rand.nextInt( Setup.MAZE_COL - 2 ) : rand.nextInt( Setup.MAZE_COL );
	}


	/**
	 * Creates a new specified position.
	 *
	 * @param row the row number
	 * @param col the column number
	 */
	Position( int row, int col ) {
		this.row = row;
		this.col = col;
	}


	@Override
	public boolean equals( Object obj ) {
		if( obj == null || getClass() != obj.getClass() ) {
			return false;
		}
		final Position other = ( Position ) obj;
		return ( this.row == other.row ) && ( this.col == other.col );
	}
}
