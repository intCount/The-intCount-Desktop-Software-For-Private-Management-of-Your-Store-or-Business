/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;
import intCount.database.Database;
import intCount.model.FinancialYear;
import intCount.utility.TabContent;
import intCount.utility.Utility;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class OpenFinancialYearController implements TabContent {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OpenFinancialYearController.class.getName());

	private Stage mainWindow;
	private TabPane tabPane;

	@FXML
	private ListView<FinancialYear> lvwFinancialYears;
	@FXML
	private Button btnOpen;
	@FXML
	private Button btnClose;

	@Override
	public boolean shouldClose() {
		return true;
	}

	@Override
	public void putFocusOnNode() {
		lvwFinancialYears.requestFocus();
	}

	@Override
	public boolean loadData() {

		List<FinancialYear> years = null;

		try {
			years = Global.getExistingFinancialYears(null);
		} catch (Exception e) {
			String message = "An error occurred while trying to read the "
					+ "existing fiscal years from disk.";
			Utility.beep();
			Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.setTitle("Erreur occured");
			alert.setHeaderText("Error reading exercises from disc");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		ObservableList<FinancialYear> observableList = FXCollections.<FinancialYear>observableList(years);
		lvwFinancialYears.setItems(observableList);
		if (!observableList.isEmpty()) {
			lvwFinancialYears.getSelectionModel().selectFirst();
		}
		return true;
	}

	@Override
	public void setMainWindow(Stage stage) {
		mainWindow = stage;
	}

	@Override
	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	/**
	 * This method is automatically called by the JavaFX Runtime when loading
	 * the controller's associated view
	 */
	public void initialize() {
		btnOpen.disableProperty().bind(lvwFinancialYears.getSelectionModel().selectedItemProperty().isNull());

		lvwFinancialYears.setCellFactory((ListView<FinancialYear> param) -> {
			final ListCell<FinancialYear> cell = new ListCell<FinancialYear>() {

				@Override
				protected void updateItem(final FinancialYear item, boolean empty) {
					super.updateItem(item, empty);

					if (empty) {
						setText(null);
						setOnKeyPressed(null);
						setOnMouseClicked(null);
					} else {
						setText(item.toString());
						setOnMouseClicked((MouseEvent event) -> {
							if (event.getClickCount() == 2) {
								try {
									openYear(item);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}

				}

			};
			cell.getStyleClass().add("list-cell");
			return cell;
		});

		lvwFinancialYears.setOnKeyPressed((KeyEvent event) -> {
			if (event.getCode() == KeyCode.ENTER && !lvwFinancialYears.getSelectionModel().isEmpty()) {
				try {
					openYear(lvwFinancialYears.getSelectionModel().getSelectedItem());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@FXML
	private void onOpenFinancialYearAction(ActionEvent event) throws SQLException {
		final FinancialYear year = lvwFinancialYears.getSelectionModel().getSelectedItem();
		openYear(year);
	}

	@FXML
	private void onCloseTabAction(ActionEvent event) {
		Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(currentTab);
	}

	private void openYear(final FinancialYear year) throws SQLException {

		final FinancialYear activeYear = Global.getActiveFinancialYear();
		if (activeYear != null && activeYear.equals(year)) {
			//Utility.count();
			Utility.beep();
			final String message = "The year" + year.toString() + "is already open!";
			final Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
			alert.setHeaderText("The selected year is already open");
			alert.setTitle("Selected year already open");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		if (activeYear != null && tabPane.getTabs().size() > 1) {
			//Utility.count();
			final String message = "All currently open tabs related to the year" + activeYear.toString()
					+ "will be closed before opening the \" + \"selected year. \\n\\nIs it correct?";
			final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
			alert.setTitle("Confirm to close open tabs");
			alert.setHeaderText("Please confirm to close open tabs");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			Optional<ButtonType> response = alert.showAndWait();
			if (!response.isPresent() || response.get() == ButtonType.CANCEL) {
				return;
			}

			// close all open tabs except this tab
			final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
			if (!Global.closeTabs(tabPane, currentTab)) {
				return; // one of the open tabs refused to close
			}
		}

		if (activeYear != null) {
			//Utility.count();
			Database.shutDownActiveYearDatabase();
		}

		if (!Database.openAsActiveYear(year)) {

			final String message = "An error occurred in opening the " + "fiscal year " + year.toString() + " !";
			final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.initOwner(mainWindow);
			alert.setTitle("Error in opening year");
			alert.setHeaderText("Error opening selected year !");
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		// close the tab
		btnClose.fire();
	}

}
