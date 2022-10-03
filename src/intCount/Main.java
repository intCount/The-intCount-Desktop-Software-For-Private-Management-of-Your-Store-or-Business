package intCount;

import intCount.controller.HomeController;
import intCount.database.Database;
import intCount.model.FinancialYear;
import intCount.model.WindowState;
import intCount.utility.TabContent;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.logging.*;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.application.Preloader;
import javafx.beans.binding.StringBinding;

import javafx.scene.control.Button;

import javafx.scene.control.CheckBox;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private final static Logger logger = Logger.getLogger(Main.class.getName());
	
	/**
	 * @author intcount.com
	 */
	
	public static void main(String args[]) throws SQLException, ParserConfigurationException, SAXException, IOException {

		System.out.println("it works");
		
			launch(args);
	}

	@Override
	public void init() throws Exception {

		super.init();

		initLogger();

		String appDataPath = Global.getAppDataPath();
		try {
			System.setProperty("derby.system.home", appDataPath);
		} catch (Exception e) {
			logger.logp(Level.SEVERE, Main.class.getName(), "init", "Error in setting the derby.system.home property",
					e);
		}

		if (Global.getUserPreferences().getAutoOpenLastOpenedYear()) {
			final FinancialYear year = Global.getLastOpenedFinancialYear();
			if (year != null) {
				Database.openAsActiveYear(year);
			}
		}

	}

	@Override
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader();
		URL resource = this.getClass().getResource("/intCount/view/Home.fxml");
		loader.setLocation(resource);
		Parent root = null;

		try {
			root = loader.<BorderPane>load();
		} catch (IOException e) {
			logger.logp(Level.SEVERE, Main.class.getName(), "start", "Error in loading the Home page view file", e);
			throw e;
		}
		Text text = new Text("I will be aligned TOPLEFT");
		text.setTextAlignment(TextAlignment.CENTER);

		final HomeController homeController = loader.getController();
		homeController.MainWindow = stage;
		final Scene scene = new Scene(root, 850, 700);
		addKeyFilter(scene);
		stage.setScene(scene);
		stage.getIcons().add(new Image("/resources/images/billing_32.png"));
		stage.getIcons().add(new Image("/resources/images/billing_48.png"));
		stage.getIcons().add(new Image("/resources/images/billing_64.png"));

		stage.getProperties().put("hostServices", getHostServices());

		stage.setOnCloseRequest((WindowEvent event) -> {
			if (!homeController.closeAllTabs()) {
				event.consume();
				return;
			}

			if (!stage.isIconified()) {
				// save the window state and window size
				WindowState s = new WindowState();
				s.setMaximized(stage.isMaximized());

				if (!stage.isMaximized()) {
					s.setXPos(stage.getX());
					s.setYPos(stage.getY());
					s.setWidth(stage.getWidth());
					s.setHeight(stage.getHeight());
				}
				//Global.saveWindowLastState(s);
				Platform.exit();
			
			}

		});

		stage.titleProperty().bind(new StringBinding() {
			{
				this.bind(Global.activeYearProperty());
			}

			@Override
			protected String computeValue() {
				String title = Global.getAppTitle();
				FinancialYear activeYear = Global.getActiveFinancialYear();
				if (activeYear != null) {
					title += " (" + activeYear.toString() + ")";
				}
				return title;
			}
		});

		final WindowState s = Global.getWindowLastState();
		stage.setX(s.getXPos());
		stage.setY(s.getYPos());
		stage.setWidth(s.getWidth());
		stage.setHeight(s.getHeight());
		stage.setMaximized(s.isMaximized());

		notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
		stage.show();

	}

	@Override
	public void stop() throws Exception {

		final FinancialYear year = Global.getActiveFinancialYear();
		Global.setLastOpenedFinancialYear(year);

		// shutdown all databases and the Derby engine
		Database.shutDown(null);

		super.stop();
	}

	

	private static void initLogger() {
		Path path = createLogFolder();
		if (path == null) {
			return;
		}

		String fileName = path.toAbsolutePath() + File.separator + "log-%g.xml";
		FileHandler fileHandler = null;

		try {
			// specify file handler to create 5 rotating files, if required, of
			// max 1 MB each.
			fileHandler = new FileHandler(fileName, 1024 * 1024, 5, true);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't create the log file handler's instance", e);
			return;
		}

		Logger rootLogger = Logger.getLogger("");
		rootLogger.addHandler(fileHandler);

	}

	private static Path createLogFolder() {
		String userHomeDir = null;
		Logger logger = Logger.getGlobal();

		try {
			userHomeDir = System.getenv("LOCALAPPDATA");
			if (userHomeDir == null) {
				userHomeDir = System.getProperty("user.dir");
			}
			if (userHomeDir == null) {
				userHomeDir = System.getProperty("user.dir");
			}
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, "Couldn't get the user home diretory", e);
			return null;
		}

		if (userHomeDir == null) {
			userHomeDir = "/";
		}

		String pathString = userHomeDir + File.separator + "intCount" + File.separator + "log";
		Path path = Paths.get(pathString);

		try {
			if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(path);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't create the log directory", e);
			return null;
		}

		return path;
	}

	private void addKeyFilter(final Scene scene) {

		final KeyCombination f4Key = new KeyCodeCombination(KeyCode.F4, KeyCombination.SHORTCUT_DOWN);

		final KeyCombination leftKey = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN);
		final KeyCombination leftNumPadKey = new KeyCodeCombination(KeyCode.KP_LEFT, KeyCombination.SHORTCUT_DOWN);

		final KeyCombination rightKey = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN);
		final KeyCombination rightNumPadKey = new KeyCodeCombination(KeyCode.KP_RIGHT, KeyCombination.SHORTCUT_DOWN);

		scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {

			// if shortcut(a.k.a. ctrl) + F4 key combination was pressed
			if (f4Key.match(event)) {
				final TabPane tabPane = (TabPane) scene.lookup("#tabPane");

				if (!tabPane.getSelectionModel().isEmpty()) {
					final Tab tab = tabPane.getSelectionModel().getSelectedItem();
					TabContent controller = (TabContent) tab.getProperties().get("controller");
					if (controller.shouldClose()) {
						final String resourcePath = System.getProperty("user.dir").concat("/texts.sql");
						File file = new File(resourcePath);
						file.deleteOnExit();
						tabPane.getTabs().remove(tab);
					}
				}
				event.consume();
			} else if (leftKey.match(event) || leftNumPadKey.match(event)) {
				/*
				 * control+ left arrow key is pressed shift to the previous tab (The movement is
				 * circular)
				 */
				final TabPane tabPane = (TabPane) scene.lookup("#tabPane");
				if (tabPane.getTabs().size() > 1 && !tabPane.getSelectionModel().isEmpty()) {
					if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
						tabPane.getSelectionModel().selectLast();
					} else {
						tabPane.getSelectionModel().selectPrevious();
					}
				}
				event.consume();
			} else if (rightKey.match(event) || rightNumPadKey.match(event)) {
				/*
				 * control+ right arrow key is pressed shift to the next tab (The movement is
				 * circular)
				 */
				final TabPane tabPane = (TabPane) scene.lookup("#tabPane");
				if (tabPane.getTabs().size() > 1 && !tabPane.getSelectionModel().isEmpty()) {
					if (tabPane.getSelectionModel().getSelectedIndex() == tabPane.getTabs().size() - 1) { // last
																											// tab
						tabPane.getSelectionModel().selectFirst();
					} else {
						tabPane.getSelectionModel().selectNext();
					}
				}
				event.consume();
			}
		});

		scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if (event.getTarget() instanceof Button && event.getCode() == KeyCode.ENTER) {
				Button button = (Button) event.getTarget();
				if (!button.isDisabled()) {
					button.fire();
				}
				event.consume();
			}
		});

		scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if (event.getTarget() instanceof CheckBox && event.getCode() == KeyCode.ENTER) {
				CheckBox checkBox = (CheckBox) event.getTarget();
				if (!checkBox.isDisabled()) {
					checkBox.setSelected(!checkBox.isSelected());
				}
				event.consume();
			}
		});

	}
}
