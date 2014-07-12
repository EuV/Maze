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
	private final JSlider mazeAnimation;
	private final JSlider extraGates;
	private final JSlider findPathAnimation;
	private final JLabel pathLengthVal;

	MainWindow( int totalRow, int totalCol ) {
		field = new Field( totalRow, totalCol, new Callback() {
			public void setPathLength( String length ) {
				MainWindow.this.setPathLength( length );
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

		/* === MAZE WINDOW ================================================== */

		// Button
		JButton mazeGenerateBtn = new JButton( "Generate" );
		mazeGenerateBtn.setPreferredSize( new Dimension( 100, 27 ) );
		mazeGenerateBtn.addActionListener( ActionEvent -> {
			setPathLength( "" );
			field.generateMaze();
		} );

		// Slider
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put( 0, new JLabel( "Slow" ) );
		labelTable.put( 10, new JLabel( "Instant" ) );
		mazeAnimation = new JSlider( JSlider.HORIZONTAL, 0, 10, 7 );
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
		extraGates = new JSlider( JSlider.HORIZONTAL, 0, 100, 10 );
		extraGates.setPreferredSize( new Dimension( 130, 50 ) );
		extraGates.setMajorTickSpacing( 20 );
		extraGates.setMinorTickSpacing( 10 );
		extraGates.setPaintTicks( true );
		extraGates.setLabelTable( labelTableGates );
		extraGates.setPaintLabels( true );

		// Maze Box
		JPanel mazeGeneratorWnd = new JPanel( new FlowLayout() );
		mazeGeneratorWnd.setBorder( BorderFactory.createTitledBorder( "Maze" ) );
		mazeGeneratorWnd.setPreferredSize( new Dimension( 150, 305 ) );
		mazeGeneratorWnd.add( newTestMapBtn( "Map 1", 1 ) );
		mazeGeneratorWnd.add( newTestMapBtn( "Map 2", 2 ) );
		mazeGeneratorWnd.add( newTestMapBtn( "Map 3", 3 ) );
		mazeGeneratorWnd.add( mazeGenerateBtn );
		mazeGeneratorWnd.add( new JLabel( "Animation speed:" ) );
		mazeGeneratorWnd.add( mazeAnimation );
		mazeGeneratorWnd.add( new JLabel( "Extra gates:" ) );
		mazeGeneratorWnd.add( extraGates );


		/* === PATHFINDING WINDOW =========================================== */

		// Slider
		findPathAnimation = new JSlider( JSlider.HORIZONTAL, 0, 10, 7 );
		findPathAnimation.setPreferredSize( new Dimension( 130, 50 ) );
		findPathAnimation.setMajorTickSpacing( 2 );
		findPathAnimation.setMinorTickSpacing( 1 );
		findPathAnimation.setPaintTicks( true );
		findPathAnimation.setLabelTable( labelTable );
		findPathAnimation.setPaintLabels( true );

		// Button
		JButton findPathBtn = new JButton( "Find Path" );
		findPathBtn.setPreferredSize( new Dimension( 100, 27 ) );
		findPathBtn.addActionListener( ActionEvent -> {
			setPathLength( "" );
			field.findPath();
		} );

		// Box
		JPanel findPathWnd = new JPanel( new FlowLayout() );
		findPathWnd.setBorder( BorderFactory.createTitledBorder( "Pathfinding" ) );
		findPathWnd.setPreferredSize( new Dimension( 150, 133 ) );
		findPathWnd.add( findPathBtn );
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

		JFrame mainWindow = new JFrame( "Maze" );
		mainWindow.setSize( 827, 658 );
		mainWindow.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		mainWindow.setLocationRelativeTo( null );
		mainWindow.setResizable( false );
		mainWindow.add( menu, BorderLayout.EAST );
		mainWindow.add( field );
		mainWindow.setVisible( true );
	}


	// TODO: button-factory
	private JButton newTestMapBtn( String text, int i ) {
		JButton btn = new JButton( text );
		btn.setPreferredSize( new Dimension( 100, 27 ) );
		btn.addActionListener( ActionEvent -> {
			setPathLength( "" );
			field.showTestMap( i );
		} );
		return btn;
	}


	private void setPathLength( String length ) {
		pathLengthVal.setText( length );
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
