package maze;

import java.util.Random;

/**
 * The cell position.
 */
class Position {
	private static final Random rand = new Random();
	int row;
	int col;


	/**
	 * Creates a new random position with or without indent.
	 *
	 * @param indent 1-cell indent
	 */
	Position( boolean indent ) {
		row = ( indent ) ? 1 + rand.nextInt( Field.ROW - 2 ) : rand.nextInt( Field.ROW );
		col = ( indent ) ? 1 + rand.nextInt( Field.COL - 2 ) : rand.nextInt( Field.COL );
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
