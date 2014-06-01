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
public abstract class AStar {

	/**
	 * The A star algorithm.
	 *
	 * @param field the labyrinth to be solved.
	 * @return true if the path joining the start and the goal has been found,
	 * false otherwise.
	 * @throws InterruptedException
	 */
	public static boolean findPath( Field field ) throws InterruptedException {

		boolean pathFound = false;

		Queue<Cell> openSet = new PriorityQueue<>();
		openSet.add( field.cells[ field.start.row ][ field.start.col ] );

		while( !openSet.isEmpty() ) {
			Cell currentCell = openSet.poll();

			List<Position> neighbors = new ArrayList<>();

			if( !currentCell.topWall ) neighbors.add( new Position( currentCell.pos.row - 1, currentCell.pos.col ) );
			if( !currentCell.leftWall ) neighbors.add( new Position( currentCell.pos.row, currentCell.pos.col - 1 ) );
			if( !currentCell.rightWall ) neighbors.add( new Position( currentCell.pos.row, currentCell.pos.col + 1 ) );
			if( !currentCell.bottomWall ) neighbors.add( new Position( currentCell.pos.row + 1,
					currentCell.pos.col ) );

			for( Position newPosition : neighbors ) {
				tryToAddToOpenSet( openSet, currentCell, newPosition, field );
			}

			if( currentCell.state != State.START ) {
				currentCell.state = State.A_CLOSED;
			}

			field.repaint();
			field.sleep();

			if( field.cells[ field.goal.row ][ field.goal.col ].state == State.ACHIEVED_GOAL ) {
				pathFound = true;
				break;
			}
		}

		if( !pathFound ) return false;

		reconstructPath( field );

		return true;
	}


	/**
	 * Tries to add the cell specified by the <code>pos</code> to the A* open set.
	 *
	 * @param openSet
	 * @param parent  the parent cell to be saved in a child and to be used in g()
	 * @param pos     the new position to be added to the open set
	 * @param field   the labyrinth
	 */
	private static void tryToAddToOpenSet( Queue<Cell> openSet, Cell parent, Position pos, Field field ) {

		// Out of the maze bounds
		if( pos.row < 0 || pos.row >= Setup.MAZE_ROW ||
				pos.col < 0 || pos.col >= Setup.MAZE_COL ) {
			return;
		}

		// Couldn't be added because of its state
		if( field.cells[ pos.row ][ pos.col ].state == State.START ||
				field.cells[ pos.row ][ pos.col ].state == State.BLOCK ||
				field.cells[ pos.row ][ pos.col ].state == State.A_CLOSED ) {
			return;
		}

		Cell openCell = field.cells[ pos.row ][ pos.col ];

		// Update the cell if it has already been added to the open set
		if( openSet.remove( openCell ) ) {

			// AND if the new g() value is better than the older one
			if( parent.g + 1 < openCell.g ) {
				openCell.g = parent.g + 1;
				openCell.parent = parent;
			}
		} else {
			openCell.state = ( openCell.state == State.GOAL ) ? State.ACHIEVED_GOAL : State.A_OPEN;
			openCell.parent = parent;
			openCell.g = parent.g + 1;

			// Manhattan distance
			openCell.h = Math.abs( field.goal.row - pos.row ) + Math.abs( field.goal.col - pos.col );
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
				stage.parent.dirGoal = Direction.RIGHT;
				stage.dirStart = Direction.LEFT;
			} else if( stage.parent.pos.col > stage.pos.col ) {
				stage.parent.dirGoal = Direction.LEFT;
				stage.dirStart = Direction.RIGHT;
			} else if( stage.parent.pos.row < stage.pos.row ) {
				stage.parent.dirGoal = Direction.DOWN;
				stage.dirStart = Direction.UP;
			} else {
				stage.parent.dirGoal = Direction.UP;
				stage.dirStart = Direction.DOWN;
			}
			stage = stage.parent;
			field.repaint();
			field.sleep();
		}
	}
}
