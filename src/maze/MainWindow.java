package maze;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Hashtable;

// TODO: scalable window
public final class MainWindow {
	private final Field field;
	private final JLabel pathLengthVal;

	MainWindow( int totalRow, int totalCol ) {

		/* === GENERATION BOX =============================================== */

		// Slider
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put( 0, new JLabel( "Slow" ) );
		labelTable.put( 10, new JLabel( "Instant" ) );
		JSlider mazeAnimation = new JSlider( JSlider.HORIZONTAL, 0, 10, 7 );
		mazeAnimation.setPreferredSize( new Dimension( 130, 50 ) );
		mazeAnimation.setMajorTickSpacing( 2 );
		mazeAnimation.setMinorTickSpacing( 1 );
		mazeAnimation.setPaintTicks( true );
		mazeAnimation.setLabelTable( labelTable );
		mazeAnimation.setPaintLabels( true );

		// Gates
		Hashtable<Integer, JLabel> labelTableGates = new Hashtable<>();
		labelTableGates.put( 0, new JLabel( "No" ) );
		labelTableGates.put( 100, new JLabel( "Plenty" ) );
		JSlider extraGates = new JSlider( JSlider.HORIZONTAL, 0, 100, 10 );
		extraGates.setPreferredSize( new Dimension( 130, 50 ) );
		extraGates.setMajorTickSpacing( 20 );
		extraGates.setMinorTickSpacing( 10 );
		extraGates.setPaintTicks( true );
		extraGates.setLabelTable( labelTableGates );
		extraGates.setPaintLabels( true );

		// Box
		JPanel mazeGeneratorWnd = new JPanel( new FlowLayout() );
		mazeGeneratorWnd.setBorder( BorderFactory.createTitledBorder( "Maze" ) );
		mazeGeneratorWnd.setPreferredSize( new Dimension( 150, 305 ) );
		mazeGeneratorWnd.add( new Button( BtnType.TEST, 1 ) );
		mazeGeneratorWnd.add( new Button( BtnType.TEST, 2 ) );
		mazeGeneratorWnd.add( new Button( BtnType.TEST, 3 ) );
		mazeGeneratorWnd.add( new Button( BtnType.GENERATE ) );
		mazeGeneratorWnd.add( new JLabel( "Animation speed:" ) );
		mazeGeneratorWnd.add( mazeAnimation );
		mazeGeneratorWnd.add( new JLabel( "Extra gates:" ) );
		mazeGeneratorWnd.add( extraGates );


		/* === PATHFINDING BOX ============================================== */

		// Slider
		JSlider findPathAnimation = new JSlider( JSlider.HORIZONTAL, 0, 10, 7 );
		findPathAnimation.setPreferredSize( new Dimension( 130, 50 ) );
		findPathAnimation.setMajorTickSpacing( 2 );
		findPathAnimation.setMinorTickSpacing( 1 );
		findPathAnimation.setPaintTicks( true );
		findPathAnimation.setLabelTable( labelTable );
		findPathAnimation.setPaintLabels( true );

		// Box
		JPanel findPathWnd = new JPanel( new FlowLayout() );
		findPathWnd.setBorder( BorderFactory.createTitledBorder( "Pathfinding" ) );
		findPathWnd.setPreferredSize( new Dimension( 150, 133 ) );
		findPathWnd.add( new Button( BtnType.FIND_PATH ) );
		findPathWnd.add( new JLabel( "Animation speed:" ) );
		findPathWnd.add( findPathAnimation );


		/* === MAIN MENU ==================================================== */

		JLabel pathLength = new JLabel( "Path length:" );
		pathLength.setPreferredSize( new Dimension( 70, 30 ) );

		pathLengthVal = new JLabel();
		pathLengthVal.setPreferredSize( new Dimension( 40, 30 ) );

		JPanel menu = new JPanel( new FlowLayout() );
		menu.setPreferredSize( new Dimension( 190, 658 ) );
		menu.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
		menu.add( mazeGeneratorWnd );
		menu.add( findPathWnd );
		menu.add( pathLength );
		menu.add( pathLengthVal );

		/* === FIELD ======================================================== */

		field = new Field( totalRow, totalCol, new Callback() {
			public void setPathLength( String length ) {
				pathLengthVal.setText( length );
			}

			public int getNumberOfGates() {
				return extraGates.getValue();
			}

			public int getAnimationSpeed( Field.FState fState ) {
				return ( fState == Field.FState.PATHFINDING ) ?
						findPathAnimation.getValue() :
						mazeAnimation.getValue();
			}
		} );


		/* === MAIN WINDOW ================================================== */

		JFrame mainWindow = new JFrame( "Maze" );
		mainWindow.setSize( 827, 658 );
		mainWindow.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		mainWindow.setLocationRelativeTo( null );
		mainWindow.setResizable( false );
		mainWindow.add( menu, BorderLayout.EAST );
		mainWindow.add( field );
		mainWindow.setVisible( true );
	}


	private static enum BtnType {
		TEST,
		GENERATE,
		FIND_PATH
	}


	private class Button extends JButton {
		Button( BtnType type ) {
			setPreferredSize( new Dimension( 100, 27 ) );
			addActionListener( ActionEvent -> pathLengthVal.setText( "" ) );
			switch( type ) {
				case GENERATE:
					setText( "Generate" );
					addActionListener( ActionEvent -> field.generateMaze() );
					break;
				case FIND_PATH:
					setText( "Find Path" );
					addActionListener( ActionEvent -> field.findPath() );
					break;
			}
		}

		Button( BtnType type, int number ) {
			this( type );
			setText( "Map " + number );
			addActionListener( ActionEvent -> field.showTestMap( number ) );
		}
	}


	static interface Callback {
		void setPathLength( String length );
		int getNumberOfGates();
		int getAnimationSpeed( Field.FState fState );
	}


	public static void main( String[] args ) {
		// TODO: commandline arguments (row, col)
		// TODO: check for max/min size (throw exception)
		new MainWindow( 30, 30 );
	}
}
