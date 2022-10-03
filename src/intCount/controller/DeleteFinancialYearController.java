/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;
import intCount.database.Database;
import intCount.database.InvoicePersistence;
import intCount.model.FinancialYear;
import intCount.utility.TabContent;
import intCount.utility.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class DeleteFinancialYearController implements TabContent {

	private Stage mainWindow;
	private TabPane tabPane;

	@FXML
	private ListView<FinancialYear> lvwFinancialYears;
	@FXML
	private Button btnDelete;
	@FXML
	private Button btnClose;

	public void initialize() {
		btnDelete.disableProperty().bind(lvwFinancialYears.getSelectionModel().selectedItemProperty().isNull());

		lvwFinancialYears.setCellFactory((ListView<FinancialYear> param) -> {
			final ListCell<FinancialYear> cell = new ListCell<FinancialYear>() {

				@Override
				protected void updateItem(final FinancialYear item, boolean empty) {
					super.updateItem(item, empty);

					if (empty) {
						setText(null);
						setOnMouseClicked(null);
					} else {
						setText(item.toString());
						setOnMouseClicked((MouseEvent event) -> {
							if (event.getClickCount() == 2) {
								try {
									deleteFinancialYear(item);
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
					deleteFinancialYear(lvwFinancialYears.getSelectionModel().getSelectedItem());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

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
			String message = "An error occurred while trying to read the"
					+ "existing fiscal years from disk.";
			Utility.beep();
			Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.setTitle("Erreur occured!");
			alert.setHeaderText("Error reading exercises from disc");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		final ObservableList<FinancialYear> observableList = FXCollections.<FinancialYear>observableList(years);
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

	@FXML
	private void onCloseTabAction(ActionEvent event) {
		Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(currentTab);
	}

	@FXML
	private void onDeleteFinancialYearAction(ActionEvent event) throws SQLException {

		final FinancialYear year = lvwFinancialYears.getSelectionModel().getSelectedItem();
		Connection connection = Database.getConnection(year, false);
		if (InvoicePersistence.getRecordCount(connection) == 0) {
			deleteFinancialYear(year);
		} else {
			final String message = "Fiscal year contains invoice, Cannot be deleted";
			final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.initOwner(mainWindow);
			alert.setTitle("Error deleting the year ");
			alert.setHeaderText("Error deleting selected year!");
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			loadData();
		}

	}

	private boolean deleteYear(final FinancialYear year) {
		if (!Global.deleteFinancialYearFolder(year)) {
			final String message = "An error occurred while deleting the folder (directory) "
					+ "of the selected year." + "\n\nIt is suggested to try deleting the following year. "
					+ "restart the app."
					+ "\n\nIf the problem persists, it is advisable to try this operation. "
					+ "about running the application with administrative privileges."
					+ "\n(Right-click on the executable file of the application and select the option. "
					+ "'Run as Administrator' from the context menu.)";
			final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.initOwner(mainWindow);
			alert.setTitle("Error deleting folder");
			alert.setHeaderText("Error deleting selected year folder!");
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		return true;
	}

	private boolean confirmDeletion(final FinancialYear year) {
		final String message = "Are you sure you want to delete the exercise? " + year.toString() + "  ?";
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		alert.initOwner(mainWindow);
		alert.setTitle("Confirm deletion");
		alert.setHeaderText("Confirm to delete the year  " + year.toString());
		Global.styleAlertDialog(alert);
		Optional<ButtonType> response = alert.showAndWait();
		if (!response.isPresent()) {
			return false;
		}

		return (response.get() == ButtonType.YES ? true : false);
	}

	private void deleteFinancialYear(final FinancialYear year) throws SQLException {

		if (!confirmDeletion(year)) {
			return;
		}

		final FinancialYear activeYear = Global.getActiveFinancialYear();

		// if selected year is currently opened
		if (activeYear != null && activeYear.equals(year)) {
			// close all tabs except this tab prior to deleting the year.
			tabPane.getTabs().retainAll(tabPane.getSelectionModel().getSelectedItem());

			// shut down the database of the selected year
			if (!Database.shutDownActiveYearDatabase()) {
				Utility.beep();
				final String message = "An error occurred while stopping the database "
						+ " of the selected year."
						+ "\n\nYou can try removing the year after restarting the app.";
				final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
				alert.initOwner(mainWindow);
				alert.setTitle("The database could not be closed");
				alert.setHeaderText("The selected year database could not be closed!");
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;
			}
		}

		if (!deleteYear(year)) {
			return;
		}

		final String message = "The financial year " + year.toString() + "was successfully deleted!";
		final Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
		alert.initOwner(mainWindow);
		alert.setTitle("Successful deletion");
		alert.setHeaderText("Selected year deleted successfully!!");
		Global.styleAlertDialog(alert);
		alert.showAndWait();
		loadData();

	}

}
