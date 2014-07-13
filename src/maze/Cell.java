package maze;

import java.awt.*;


/**
 * The Cell class.
 * <p> Contains all the data needed to calculate and render a cell.
 */
class Cell implements Comparable<Cell> {

	// Public changeable data
	Position pos;
	State state = State.BLOCK;
	boolean topWall = true;
	boolean rightWall = true;
	boolean bottomWall = true;
	boolean leftWall = true;

	// A*
	int g = 0; // Movement cost
	int h = 0; // Heuristic cost estimate
	int f = g + h;
	Cell parent = null;

	// Path reconstruction
	Direction dirStart = null;
	Direction dirGoal = null;

	// Used when drawing
	static final int WIDTH = 20;
	static final int HEIGHT = 20;
	private boolean sharpCorners = true;
	private boolean outline = false;
	private final int x; // Just for short:
	private final int y; // not to calculate them each time


	/**
	 * Creates a new cell in the specified position.
	 *
	 * @param row the row number (between 0 (inclusive) and ROW (exclusive))
	 * @param col the column number (between 0 (inclusive) and COL (exclusive))
	 */
	Cell( int row, int col ) {
		pos = new Position( row, col );
		x = pos.col * ( WIDTH + 1 );
		y = pos.row * ( HEIGHT + 1 );
	}


	/**
	 * Creates a new cell with the specified state and position.
	 *
	 * @param row   the row number (between 0 (inclusive) and ROW (exclusive))
	 * @param col   the column number (between 0 (inclusive) and COL (exclusive))
	 * @param state cell state
	 */
	Cell( int row, int col, State state ) {
		this( row, col );
		this.state = state;

		// If we are here - it isn't a random maze
		// so let's improve visual perception
		sharpCorners = false;
		outline = true;

		if( state == State.PASSAGE ) {
			topWall = false;
			rightWall = false;
			bottomWall = false;
			leftWall = false;
		}
	}


	/**
	 * Paints a cell using the internal data.
	 *
	 * @param g parent's Graphics context
	 */
	void paint( Graphics g ) {
		Graphics2D g2 = ( Graphics2D ) g;
		g2.setStroke( new BasicStroke( 1 ) );

		switch( state ) {
			case BLOCK:
				g.setColor( Color.LIGHT_GRAY );
				break;
			case MARKED:
				g.setColor( Color.PINK );
				break;
			case PASSAGE:
				g.setColor( Color.WHITE );
				break;
			case START:
				g.setColor( new Color( 0, 200, 0 ) );
				break;
			case GOAL:
			case ACHIEVED_GOAL:
				g.setColor( new Color( 255, 100, 0 ) );
				break;
			case A_OPEN:
				g.setColor( new Color( 152, 255, 152 ) );
				break;
			case A_CLOSED:
				g.setColor( new Color( 175, 238, 238 ) );
				break;
			default:
				g.setColor( Color.CYAN );
				break;
		}
		g.fillRect( x, y, WIDTH + 1, HEIGHT + 1 );

		if( outline ) {
			g.setColor( new Color( 200, 200, 200 ) );
			g.drawRect( x, y, WIDTH, HEIGHT );
		}

		g.setColor( Color.BLACK );
		if( topWall ) g.drawLine( x, y, x + WIDTH, y );
		if( rightWall ) g.drawLine( x + WIDTH, y, x + WIDTH, y + HEIGHT );
		if( bottomWall ) g.drawLine( x + WIDTH, y + HEIGHT, x, y + HEIGHT );
		if( leftWall ) g.drawLine( x, y + HEIGHT, x, y );
		if( sharpCorners ) {
			g.fillRect( x, y, 1, 1 );
			g.fillRect( x + WIDTH, y, 1, 1 );
			g.fillRect( x, y + HEIGHT, 1, 1 );
			g.fillRect( x + WIDTH, y + HEIGHT, 1, 1 );
		}

		if( dirGoal == null && dirStart == null ) return;

		g.setColor( new Color( 255, 100, 0 ) );
		g2.setStroke( new BasicStroke( 3 ) );
		if( dirGoal == Direction.LEFT || dirStart == Direction.LEFT )
			g.drawLine( x, y + HEIGHT / 2, x + WIDTH / 2, y + HEIGHT / 2 );
		if( dirGoal == Direction.RIGHT || dirStart == Direction.RIGHT )
			g.drawLine( x + WIDTH / 2, y + HEIGHT / 2, x + WIDTH, y + HEIGHT / 2 );
		if( dirGoal == Direction.UP || dirStart == Direction.UP )
			g.drawLine( x + WIDTH / 2, y, x + WIDTH / 2, y + HEIGHT / 2 );
		if( dirGoal == Direction.DOWN || dirStart == Direction.DOWN )
			g.drawLine( x + WIDTH / 2, y + HEIGHT / 2, x + WIDTH / 2, y + HEIGHT );
	}


	/**
	 * Used when sorting in PriorityQueue (open set in A*).
	 */
	@Override
	public int compareTo( Cell other ) {
		if( this.f > other.f ) return 1;
		if( this.f < other.f ) return -1;
		return 0;
	}

	/**
	 * Determines the state of a cell which is used during the visualisation.
	 * <p> <code>BLOCK</code> by default - a blocked cell with all the walls.
	 * <p> <code>MARKED</code> - a marked cell (during the maze generation).
	 * <p> <code>PASSAGE</code> - an open for the passage cell. In this state
	 * it isn't necessary but it's assumed that there isn't at least one cell wall.
	 * <p> <code>START</code> - the source of the maze.
	 * <p> <code>GOAL</code> - the destination of the maze.
	 * <p> <code>ACHIEVED_GOAL</code> - the reached destination of the maze.
	 * <p> <code>A_OPEN</code> - a cell in the open set (A*).
	 * <p> <code>A_CLOSED</code> - a cell in the closed set (A*).
	 */
	static enum State {
		BLOCK, MARKED, PASSAGE, START, GOAL, ACHIEVED_GOAL, A_OPEN, A_CLOSED
	}


	/**
	 * Describes the direction between start and goal cells which is marked on cells.
	 */
	static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
}
