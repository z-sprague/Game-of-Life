import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/* 
 * Author: Zack Sprague 
 * 
 * Date: 1-5-20
 * 
 * Description: This program creates a similar application called Game of Life created by John Conway.
 * This differs from the original because the if a figure goes all the way to the right border,
 * it will loop back around to the left border. Similarly if it hits the bottom it will continue 
 * transforming at the top. The original had the cells die once they reached a border.
 * 
 * */

public class GameOfLife extends Application{

	// Create and initialize cell
	private final int DIM = 32; // Table dimensions
	private Cell[][] cell =  new Cell[DIM][DIM]; // 2D Array to hold status of each Cell
	private boolean[][] next = new boolean[DIM][DIM]; // 2D Array to determine status of cell in next generation
	boolean play = true;
	private Timeline animationLife;
	private Timeline animationHighLife;

	// Start method in the Application class
	public void start(Stage primaryStage) {
		
		// Setup for the screen menu bar
		MenuBar menuBar = new MenuBar();    
		Menu menuFile = new Menu("File");
		Menu menuAnimation = new Menu("Cool Animations");
		menuBar.getMenus().addAll(menuFile, menuAnimation);
		
		// Items for File menu
		MenuItem menuItemNewGame = new MenuItem("New Game");
		MenuItem menuItemSaveAs = new MenuItem("Save As...");
		MenuItem menuItemLoadGame = new MenuItem("Load Game");
		MenuItem menuItemExit = new MenuItem("Exit");
		menuFile.getItems().addAll(menuItemNewGame, new SeparatorMenuItem(), 
				menuItemSaveAs, menuItemLoadGame, new SeparatorMenuItem(),
				menuItemExit);

		menuItemNewGame.setOnAction(e -> newGame());
		menuItemSaveAs.setOnAction(e -> saveAs(primaryStage));
		menuItemLoadGame.setOnAction(e -> loadGame(primaryStage));
		menuItemExit.setOnAction(e -> System.exit(0));

		menuItemNewGame.setAccelerator(
				KeyCombination.keyCombination("Ctrl+N"));
		menuItemSaveAs.setAccelerator(
				KeyCombination.keyCombination("Ctrl+S"));
		menuItemLoadGame.setAccelerator(
				KeyCombination.keyCombination("Ctrl+L"));
		menuItemExit.setAccelerator(
				KeyCombination.keyCombination("Ctrl+X"));
		
		// Items for Cool Animations menu
		Menu menuStill = new Menu("Still");
		Menu menuOscillators = new Menu("Oscillators");
		Menu menuSpaceships = new Menu("Spaceships");
		
		// Items for Still
		MenuItem menuItemBlock = new MenuItem("Block");
		MenuItem menuItemBeehive = new MenuItem("Bee-hive");
		MenuItem menuItemLoaf = new MenuItem("Loaf");
		MenuItem menuItemBoat = new MenuItem("Boat");
		MenuItem menuItemTub = new MenuItem("Tub");
		
		// Items for Oscillators
		MenuItem menuItemBlinker = new MenuItem("Blinker");
		MenuItem menuItemToad = new MenuItem("Toad");
		MenuItem menuItemBeacon = new MenuItem("Beacon");
		MenuItem menuItemPulsar = new MenuItem("Pulsar");
		MenuItem menuItemPenta = new MenuItem("Penta-decathlon");
		
		// Items for Spaceships
		MenuItem menuItemGlider = new MenuItem("Glider");
		MenuItem menuItemLwss = new MenuItem("Light Weight");
		MenuItem menuItemMwss = new MenuItem("Middle Weight");
		MenuItem menuItemHwss = new MenuItem("Heavy Weight");
		
		// Add items to menu	
		menuStill.getItems().addAll(menuItemBlock, menuItemBeehive, menuItemLoaf, menuItemBoat, menuItemTub);
		menuOscillators.getItems().addAll(menuItemBlinker, menuItemToad, menuItemBeacon, menuItemPulsar, menuItemPenta);
		menuSpaceships.getItems().addAll(menuItemGlider, menuItemLwss, menuItemMwss, menuItemHwss);
		
		menuAnimation.getItems().addAll(menuStill, new SeparatorMenuItem(),
				menuOscillators, new SeparatorMenuItem(), menuSpaceships);
		
		// Load the animation file
		menuItemBlock.setOnAction(e -> loadAnimation(new File("Animations/Still/block.lif"), primaryStage));
		menuItemBeehive.setOnAction(e -> loadAnimation(new File("Animations/Still/beehive.lif"), primaryStage));
		menuItemLoaf.setOnAction(e -> loadAnimation(new File("Animations/Still/loaf.lif"), primaryStage));
		menuItemBoat.setOnAction(e -> loadAnimation(new File("Animations/Still/boat.lif"), primaryStage));
		menuItemTub.setOnAction(e -> loadAnimation(new File("Animations/Still/tub.lif"), primaryStage));
		
		menuItemBlinker.setOnAction(e -> loadAnimation(new File("Animations/Oscillators/blinker.lif"), primaryStage));
		menuItemToad.setOnAction(e -> loadAnimation(new File("Animations/Oscillators/toad.lif"), primaryStage));
		menuItemBeacon.setOnAction(e -> loadAnimation(new File("Animations/Oscillators/beacon.lif"), primaryStage));
		menuItemPulsar.setOnAction(e -> loadAnimation(new File("Animations/Oscillators/pulsar.lif"), primaryStage));
		menuItemPenta.setOnAction(e -> loadAnimation(new File("Animations/Oscillators/penta.lif"), primaryStage));
		
		menuItemGlider.setOnAction(e -> loadAnimation(new File("Animations/Spaceships/glider.lif"), primaryStage));
		menuItemLwss.setOnAction(e -> loadAnimation(new File("Animations/Spaceships/lwss.lif"), primaryStage));
		menuItemMwss.setOnAction(e -> loadAnimation(new File("Animations/Spaceships/mwss.lif"), primaryStage));
		menuItemHwss.setOnAction(e -> loadAnimation(new File("Animations/Spaceships/hwss.lif"), primaryStage));
		

		// Form controls and default settings
		HBox hbox = new HBox(20);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(5,0,5,0));
		Button btnStep = new Button("Step");
		Button btnPlay = new Button("Play");
		Button btnRandom = new Button("Random");
		Label lblRate = new Label("Rate:");
		Slider slSpeed = new Slider();
		slSpeed.setMax(5);
		slSpeed.setValue(2);
		Button btnClear = new Button("Clear");
		VBox radioButtons = new VBox(2);
		RadioButton rbLife = new RadioButton("Life");
		RadioButton rbHighLife = new RadioButton("High Life");
		radioButtons.getChildren().addAll(rbLife, rbHighLife);
		rbLife.fire();
		ToggleGroup group = new ToggleGroup();
		rbLife.setToggleGroup(group);
		rbHighLife.setToggleGroup(group);
		rbLife.isSelected();
		
		// Animation speed
		animationLife = new Timeline(
				new KeyFrame(Duration.millis(100), e -> nextGenerationLife()));
		animationLife.setCycleCount(Timeline.INDEFINITE);
		animationLife.rateProperty().bind(slSpeed.valueProperty());
		animationHighLife = new Timeline(
				new KeyFrame(Duration.millis(100), e -> nextGenerationHighLife()));
		animationHighLife.setCycleCount(Timeline.INDEFINITE);
		animationHighLife.rateProperty().bind(slSpeed.valueProperty());

		// Add all components to the screen
		hbox.getChildren().addAll(btnStep, btnPlay, btnRandom, lblRate, slSpeed, btnClear, radioButtons);

		// Pane to hold cells
		GridPane pane = new GridPane(); 
		for (int i = 0; i < DIM; i++)
			for (int j = 0; j < DIM; j++)
				pane.add(cell[i][j] = new Cell(), j, i);

		// Action event to step through the animations
		btnStep.setOnAction(event -> nextGenerationLife());
		
		// Action event to play normal Life
		btnPlay.setOnAction(event -> {
			if (play) {
				play = false;
				animationLife.play();
				btnStep.setDisable(true);
				btnPlay.setText("Stop");
			}
			else {
				play = true;
				animationLife.stop();
				btnStep.setDisable(false);
				btnPlay.setText("Play");
			}
		});


		// Action event to play High Life
		rbHighLife.setOnAction(e -> {
			btnStep.setOnAction(event -> nextGenerationHighLife());
			btnPlay.setOnAction(event -> {
				if (play) {
					play = false;
					animationHighLife.play();
					btnStep.setDisable(true);
					btnPlay.setText("Stop");
				}
				else {
					play = true;
					animationHighLife.stop();
					btnStep.setDisable(false);
					btnPlay.setText("Play");
				}
			});
		});

		// Action event to display transformations of the figures
		btnPlay.setOnAction(event -> {
			if (play) {
				play = false;
				animationLife.play();
				btnStep.setDisable(true);
				btnPlay.setText("Stop");
			}
			else {
				play = true;
				animationLife.stop();
				btnStep.setDisable(false);
				btnPlay.setText("Play");
			}
		});

		// Action event to randomize the board with dead or alive cells
		Random rand = new Random();		
		btnRandom.setOnAction(e -> {
			for (int i = 0; i < DIM; i++)
				for (int j = 0; j < DIM; j++) {
					int num = rand.nextInt(2);
					if (num == 1) {
						cell[i][j].setLife(true);
					}
					else {
						cell[i][j].setLife(false);
					}
				}
		});

		// Action event to clear the board - making all cells dead
		btnClear.setOnAction(e -> {
			for (int i = 0; i < DIM; i++)
				for (int j = 0; j < DIM; j++) {
					cell[i][j].isAlive = false;
					cell[i][j].setLife(false);
				}
			play = true;
			animationLife.stop();
			animationHighLife.stop();
			btnStep.setDisable(false);
			btnPlay.setText("Play");
		});
		
		BorderPane borderPane = new BorderPane();

		borderPane.setTop(menuBar);
		borderPane.setCenter(pane);
		borderPane.setBottom(hbox);

		// Create a scene and place it in the stage
		Scene scene = new Scene(borderPane, 1000, 1000);
		primaryStage.setTitle("Game Of Life"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage 
	} 

	/* Implements new game and makes all cells dead */
	public void newGame() {
		for (int i = 0; i < DIM; i++)
			for (int j = 0; j < DIM; j++) {
				cell[i][j].isAlive = false;
				cell[i][j].setStyle("-fx-border-color: #6DD47E; -fx-background-color: #293150;");
			}
	}
	
	/* While playing the game you can save and load it back later - if you want to save cool animations */
	private void saveAs(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.setTitle("Enter file name");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Life files", "*.lif"));
		File selectedFile = fileChooser.showSaveDialog(primaryStage);
		if (selectedFile != null) {
			try ( 
				// Create an output stream for file object.dat 
				ObjectOutputStream output =
					new ObjectOutputStream(new FileOutputStream(selectedFile));) {
				boolean[][] cellStates = new boolean[DIM][DIM];
				for (int i = 0; i < DIM; i++)
					for (int j = 0; j < DIM; j++)
						cellStates[i][j] = cell[i][j].getLife();
				output.writeObject(cellStates);
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/* The load option allows you to retrieve a saved game from your files */
	private void loadGame(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.setTitle("Enter file name");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Life files", "*.lif"));
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null)
			try {
				// Create an input stream for the file we want to load
				try (
					ObjectInputStream input =
						new ObjectInputStream(new FileInputStream(selectedFile));) {
					boolean[][] cellStates = (boolean[][])(input.readObject());
					for (int i = 0; i < DIM; i++)
						for (int j = 0; j < DIM; j++)
							cell[i][j].setLife(cellStates[i][j]);
				}
			}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/* Load preset animations */
	private void loadAnimation(File file, Stage primaryStage) {	
		if (file != null)
			try {
				// Create an input stream for the file we want to load
				try (
					ObjectInputStream input =
						new ObjectInputStream(new FileInputStream(file));) {
					boolean[][] cellStates = (boolean[][])(input.readObject());
					for (int i = 0; i < DIM; i++)
						for (int j = 0; j < DIM; j++)
							cell[i][j].setLife(cellStates[i][j]);
				}
			}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}

	/* Creating new generation for life */
	public void nextGenerationLife() {
		for (int r = 0; r < DIM; r++)
			for (int c = 0; c < DIM; c++) {
				int neighborCount = neighbors(r,c);				
				if (neighborCount == 3 || (neighborCount == 2 && cell[r][c].isAlive)) {
					next[r][c] = true;
				}
				else {
					next[r][c] = false;
				}
			}
		for (int r = 0; r < DIM; r++)
			for (int c = 0; c < DIM; c++) {
				cell[r][c].setLife(next[r][c]);			
			}
	}
	
	/* Creating new generation for high life */
	public void nextGenerationHighLife() {
		for (int r = 0; r < DIM; r++)
			for (int c = 0; c < DIM; c++) {
				int neighborCount = neighbors(r,c);
				if (neighborCount == 3 || (neighborCount == 6 && !cell[r][c].isAlive) || (neighborCount == 2 && cell[r][c].isAlive)) {
					next[r][c] = true;
				}
				else {
					next[r][c] = false;
				}
			}
		for (int r = 0; r < DIM; r++)
			for (int c = 0; c < DIM; c++) {
				cell[r][c].setLife(next[r][c]);
			}
	}

	/* Counting number of neighbors */
	public int neighbors(int row, int col) {
		int liveCount = 0;
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++)
				if (cell[(row + i + DIM) % DIM][(col + j + DIM) % DIM].isAlive)
					liveCount++;		
		if (cell[row][col].isAlive)
			liveCount--;
		return liveCount;
	}

	/* An inner class for a cell */
	public class Cell extends Pane {
		boolean isAlive = false;
		
		/* Cell initialization */
		public Cell() {		  
			this.setStyle("-fx-border-color: #6DD47E; -fx-background-color: #293150;"); 
			this.setPrefSize(800, 800);
			this.setOnMouseClicked(e -> {
				handleMouseClick();
			});
		}
		
		/* When the cell is clicked - check cells live status - change color to accordingly */
		private void handleMouseClick() {
			if(!isAlive) {
				isAlive = true;
				setStyle("-fx-border-color: #6DD47E; -fx-background-color: #6DD47E;");			  	  
			}
			else {
				isAlive = false;
				setStyle("-fx-border-color: #6DD47E; -fx-background-color: #293150;");			  	  
			}			  
		}

		/* Returns the life status of a cell */
		public boolean getLife() {
			return isAlive;
		}

		/* Set the life status of a cell */
		public void setLife(boolean b) {
			isAlive = b;
			getChildren().clear();
			if(isAlive) {
				setStyle("-fx-border-color: #6DD47E; -fx-background-color: #6DD47E;");			  	  
			}
			else {
				setStyle("-fx-border-color: #6DD47E; -fx-background-color: #293150;");
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}