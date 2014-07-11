package maze;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The A star abstract class.
 * <p> For more information see
 * <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">Wikipedia</a> and
 * <a href="http://www.policyalmanac.org/games/aStarTutorial.htm">A* Pathfinding for Beginners</a>
 */
abstract class AStar {

	/**
	 * The A star algorithm.
	 *
	 * @param field the labyrinth to be solved.
	 * @return true if the path joining the start and the goal has been found,
	 * false otherwise.
	 * @throws InterruptedException
	 */
	static boolean findPath( Field field ) throws InterruptedException {

		boolean pathWasFound = false;

		Queue<Cell> openSet = new PriorityQueue<>();
		openSet.add( field.cells[ field.start.row ][ field.start.col ] );

		while( !openSet.isEmpty() ) {
			Cell cell = openSet.poll();

			List<Position> neighbors = new ArrayList<>();

			if( !cell.topWall ) neighbors.add( new Position( cell.pos.row - 1, cell.pos.col ) );
			if( !cell.leftWall ) neighbors.add( new Position( cell.pos.row, cell.pos.col - 1 ) );
			if( !cell.rightWall ) neighbors.add( new Position( cell.pos.row, cell.pos.col + 1 ) );
			if( !cell.bottomWall ) neighbors.add( new Position( cell.pos.row + 1, cell.pos.col ) );

			for( Position newPosition : neighbors ) {
				tryToAddToOpenSet( openSet, cell, newPosition, field );
			}

			if( cell.state != Cell.State.START ) {
				cell.state = Cell.State.A_CLOSED;
			}

			field.repaint();
			field.sleep();

			if( field.cells[ field.goal.row ][ field.goal.col ].state == Cell.State.ACHIEVED_GOAL ) {
				pathWasFound = true;
				break;
			}
		}

		if( !pathWasFound ) return false;

		reconstructPath( field );

		return true;
	}


	/**
	 * Tries to add the cell specified by the <code>p</code> to the A* open set.
	 *
	 * @param openSet the open set (A*)
	 * @param parent  the parent cell to be saved in a child and to be used in g()
	 * @param p       the new position to be added to the open set
	 * @param field   the labyrinth
	 */
	private static void tryToAddToOpenSet( Queue<Cell> openSet, Cell parent, Position p, Field field ) {

		// Out of the maze bounds
		if( p.row < 0 || p.row >= Field.ROW || p.col < 0 || p.col >= Field.COL ) {
			return;
		}

		// Couldn't be added because of its state
		if( field.cells[ p.row ][ p.col ].state == Cell.State.START ||
				field.cells[ p.row ][ p.col ].state == Cell.State.BLOCK ||
				field.cells[ p.row ][ p.col ].state == Cell.State.A_CLOSED ) {
			return;
		}

		Cell openCell = field.cells[ p.row ][ p.col ];

		// Update the cell if it has already been added to the open set
		if( openSet.remove( openCell ) ) {

			// AND if the new g() value is better than the older one
			if( parent.g + 1 < openCell.g ) {
				openCell.g = parent.g + 1;
				openCell.parent = parent;
			}
		} else {
			openCell.state = ( openCell.state == Cell.State.GOAL ) ?
					Cell.State.ACHIEVED_GOAL :
					Cell.State.A_OPEN;
			openCell.parent = parent;
			openCell.g = parent.g + 1;

			// Manhattan distance
			openCell.h = Math.abs( field.goal.row - p.row ) + Math.abs( field.goal.col - p.col );
		}
		openCell.f = openCell.g + openCell.h;
		openSet.add( openCell );
	}


	/**
	 * Reconstructs the path joining the start and the goal.
	 *
	 * @param field the labyrinth
	 * @throws InterruptedException
	 */
	private static void reconstructPath( Field field ) throws InterruptedException {
		Cell stage = field.cells[ field.goal.row ][ field.goal.col ];
		while( stage.parent != null ) {
			if( stage.parent.pos.col < stage.pos.col ) {
				stage.parent.dirGoal = Cell.Direction.RIGHT;
				stage.dirStart = Cell.Direction.LEFT;
			} else if( stage.parent.pos.col > stage.pos.col ) {
				stage.parent.dirGoal = Cell.Direction.LEFT;
				stage.dirStart = Cell.Direction.RIGHT;
			} else if( stage.parent.pos.row < stage.pos.row ) {
				stage.parent.dirGoal = Cell.Direction.DOWN;
				stage.dirStart = Cell.Direction.UP;
			} else {
				stage.parent.dirGoal = Cell.Direction.UP;
				stage.dirStart = Cell.Direction.DOWN;
			}
			stage = stage.parent;
			field.repaint();
			field.sleep();
		}
	}
}
