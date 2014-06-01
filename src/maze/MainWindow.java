package maze;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.Hashtable;

public final class MainWindow {
	private final static String pathLengthText = "Path length: ";

	private final Field field;
	private final JSlider mazeAnimation;
	private final JSlider extraGates;
	private final JSlider findPathAnimation;
	private final JLabel pathLength;

	MainWindow() {
		field = new Field( this );

		Hashtable labelTable = new Hashtable();
		labelTable.put( Setup.ANIMATION_SLOW, new JLabel( "Slow" ) );
		labelTable.put( Setup.ANIMATION_INSTANT, new JLabel( "Instant" ) );
		
		/* === MAZE WINDOW ================================================== */

		// Button
		JButton mazeGenerateBtn = new JButton( "Generate" );
		mazeGenerateBtn.setPreferredSize( new Dimension( 100, 27 ) );
		mazeGenerateBtn.addActionListener( ( ActionEvent ) -> {
			setPathLength( "" );
			field.generateMaze();
		} );

		// Slider
		mazeAnimation = new JSlider( JSlider.HORIZONTAL, Setup.ANIMATION_SLOW, Setup.ANIMATION_INSTANT,
				Setup.ANIMATION_DEFAULT );
		mazeAnimation.setPreferredSize( new Dimension( 130, 50 ) );
		mazeAnimation.setMajorTickSpacing( Setup.ANIMATION_INSTANT / Setup.ANIMATION_TICK );
		mazeAnimation.setMinorTickSpacing( Setup.ANIMATION_INSTANT / Setup.ANIMATION_TICK / 2 );
		mazeAnimation.setPaintTicks( true );
		mazeAnimation.setLabelTable( labelTable );
		mazeAnimation.setPaintLabels( true );

		// Gates
		Hashtable labelTableGates = new Hashtable();
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
		findPathAnimation = new JSlider( JSlider.HORIZONTAL, Setup.ANIMATION_SLOW, Setup.ANIMATION_INSTANT,
				Setup.ANIMATION_DEFAULT );
		findPathAnimation.setPreferredSize( new Dimension( 130, 50 ) );
		findPathAnimation.setMajorTickSpacing( Setup.ANIMATION_INSTANT / Setup.ANIMATION_TICK );
		findPathAnimation.setMinorTickSpacing( Setup.ANIMATION_INSTANT / Setup.ANIMATION_TICK / 2 );
		findPathAnimation.setPaintTicks( true );
		findPathAnimation.setLabelTable( labelTable );
		findPathAnimation.setPaintLabels( true );

		// Button
		JButton findPathBtn = new JButton( "Find Path" );
		findPathBtn.setPreferredSize( new Dimension( 100, 27 ) );
		findPathBtn.addActionListener( ( ActionEvent ) -> {
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

		pathLength = new JLabel( pathLengthText );
		pathLength.setPreferredSize( new Dimension( 100, 30 ) );

		JPanel menu = new JPanel( new FlowLayout() );
		menu.setPreferredSize( new Dimension( 190, 658 ) );
		menu.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
		menu.add( mazeGeneratorWnd );
		menu.add( findPathWnd );
		menu.add( pathLength );

		JFrame mainWindow = new JFrame( "Maze" );
		mainWindow.setSize( 827, 658 );
		mainWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		mainWindow.setLocationRelativeTo( null );
		mainWindow.setResizable( false );
		mainWindow.add( menu, BorderLayout.EAST );
		mainWindow.add( field );
		mainWindow.setVisible( true );
	}

	private JButton newTestMapBtn( String text, int i ) {
		JButton btn = new JButton( text );
		btn.setPreferredSize( new Dimension( 100, 27 ) );
		btn.addActionListener( ( ActionEvent ) -> {
			setPathLength( "" );
			field.showTestMap( i );
		} );
		return btn;
	}


	int getNumberOfGates() {
		return extraGates.getValue();
	}


	void setPathLength( String length ) {
		pathLength.setText( pathLengthText + length );
	}


	int getAnimationSpeed( GlobalState gState ) {
		return ( gState == GlobalState.PATHFINDING ) ? findPathAnimation.getValue() : mazeAnimation.getValue();
	}


	public static void main( String[] args ) {
		new MainWindow();
	}
}
