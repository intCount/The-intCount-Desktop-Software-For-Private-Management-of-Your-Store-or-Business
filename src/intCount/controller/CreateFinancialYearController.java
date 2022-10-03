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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.time.*;
import java.util.List;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class CreateFinancialYearController implements TabContent {

	private final LocalDate maxStartDate = LocalDate.now().plusMonths(1);
	private final LocalDate maxEndDate = maxStartDate.plusYears(1).minusDays(1);
	private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
	private final ObservableList<FinancialYear> existingFinancialYears = FXCollections
			.<FinancialYear>observableArrayList();

	@FXML
	private DatePicker dpStartDate;
	@FXML
	private DatePicker dpEndDate;
	@FXML
	private CheckBox chkTransferData;
	@FXML
	private ListView<FinancialYear> lvwExistingYears;
	@FXML
	private Button btnCreate;
	@FXML
	private Label lblStartDateError;
	@FXML
	private Label lblEndDateError;
	@FXML
	private Label lblExistingYearError;
	@FXML
	private VBox existingYearsPane;
	@FXML
	private Button btnClose;

	private Stage mainWindow = null;
	private TabPane tabPane = null;
	private Label[] errorLabels = null;

	@Override
	public boolean shouldClose() {
		return true;
	}

	@Override
	public void putFocusOnNode() {
		dpStartDate.requestFocus();
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
			alert.setTitle("Error Occurred");
			alert.setHeaderText("Error reading the fiscal year from disc");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		existingFinancialYears.addAll(years);
		return true;
	}

	@Override
	public void setMainWindow(Stage stage) {
		mainWindow = stage;
	}

	@Override
	public void setTabPane(TabPane pane) {
		tabPane = pane;
	}

	public void initialize() {
		existingYearsPane.managedProperty().bind(existingYearsPane.visibleProperty());
		existingYearsPane.visibleProperty().bind(chkTransferData.selectedProperty());

		errorLabels = new Label[] { lblStartDateError, lblEndDateError, lblExistingYearError };
		for (Label label : errorLabels) {
			label.managedProperty().bind(label.visibleProperty());
			label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
		}

		dpStartDate.setConverter(Utility.getDateStringConverter());
		dpStartDate.setDayCellFactory(this::getStartDateCell);
		dpStartDate.setValue(LocalDate.now());

		dpEndDate.setConverter(Utility.getDateStringConverter());
		dpEndDate.setDayCellFactory(this::getEndDateCell);
		dpEndDate.setValue(LocalDate.now().plusYears(1));

		//btnCreate.disableProperty().bind(isDirty.not());

		dpStartDate.valueProperty().addListener((Observable observable) -> {
			isDirty.set(true);
		});

		dpEndDate.valueProperty().addListener((Observable observable) -> {
			isDirty.set(true);
		});

		chkTransferData.selectedProperty().addListener((Observable observable) -> {
			isDirty.set(true);
		});

		lvwExistingYears.setItems(existingFinancialYears);
		BooleanBinding binding = Bindings.isEmpty(existingFinancialYears);
		chkTransferData.disableProperty().bind(binding);
	}

	private DateCell getStartDateCell(DatePicker datePicker) {
		return Utility.getDateCell(datePicker, null, maxStartDate);
	}

	private DateCell getEndDateCell(DatePicker datePicker) {
		return Utility.getDateCell(datePicker, null, maxEndDate);
	}

	private void clearErrorLabels() {
		for (Label label : errorLabels) {
			label.setText("");
		}
	}

	@FXML
	private void onCloseTabAction(ActionEvent event) {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(tab);
		final String resourcePath = System.getProperty("user.dir").concat("/done.sql");
		File file = new File(resourcePath);
		file.deleteOnExit();
	}

	private boolean validateInput() {
		clearErrorLabels();
		boolean valid = true;

		final LocalDate startDate = dpStartDate.getValue();
		final LocalDate endDate = dpEndDate.getValue();

		if (startDate == null) {
			lblStartDateError.setText("Start date not specified!");
			valid = false;
		}

		if (endDate == null) {
			lblEndDateError.setText("End date not specified!");
			valid = false;
		}

		if (startDate != null && endDate != null) {
			if (startDate.compareTo(endDate) >= 0) {
				lblEndDateError.setText("The end date must come after the start date!");
				valid = false;
			}
		}

		if (chkTransferData.isSelected()) {
			if (!lvwExistingYears.getItems().isEmpty() && lvwExistingYears.getSelectionModel().isEmpty()) {
				lblExistingYearError.setText("No existing fiscal year selected!");
				valid = false;
			}

			if (valid && !lvwExistingYears.getItems().isEmpty()) {
				FinancialYear year = new FinancialYear(startDate, endDate);
				FinancialYear overlappingYear = FinancialYear.getOverlappingYear(lvwExistingYears.getItems(), year);

				if (overlappingYear != null) {
					lblExistingYearError.setText("The new fiscal year overlaps "
							+ "with existing year '" + overlappingYear.toString() + "'.");
					valid = false;
				} else {
					FinancialYear existingYear = lvwExistingYears.getSelectionModel().getSelectedItem();
					if (year.isBefore(existingYear)) {
						lblExistingYearError.setText("The year of data transfer from "
								+ "must come before the proposed new year.");
						valid = false;
					}
				}
				/*
				 * // Code added on 10/02/2019, for not writing the same year if
				 * (valid && !existingFinancialYears.isEmpty()) { FinancialYear
				 * year1 = new FinancialYear(startDate, endDate);
				 * 
				 * FinancialYear overlappingYear1 =
				 * FinancialYear.getOverlappingYear(existingFinancialYears,
				 * year1);
				 * 
				 * if (overlappingYear1 != null) { lblExistingYearError.
				 * setText("Le nouvel exercice financier se chevauche " +
				 * "avec l'année existante '" + overlappingYear.toString() +
				 * "'."); valid = false; } }
				 */
			}

		}

		return valid;
	}

	// Added ligne

	@FXML
	private void onCreateFinancialYearAction(ActionEvent event) throws SQLException {

		if (!validateInput()) {
			Utility.beep();
			return;
		}

		final FinancialYear year = new FinancialYear(dpStartDate.getValue(), dpEndDate.getValue());

		if (!createNewYearFolder(year)) {
			return;
		}

		if (!createDatabase(year)) {
			Global.deleteFinancialYearFolder(year);
			return;
		}

		if (chkTransferData.isSelected()) {
			if (!transferDataFromExistingYear(year)) {
				Global.deleteFinancialYearFolder(year);
				return;
			}
		}

		isDirty.set(false);

		boolean shouldOpen = shouldOpenTheNewYear(year);
		if (!shouldOpen) {
			btnClose.fire();
			return;
		}

		if (openNewYear(year)) {

			btnClose.fire(); // close this tab
		}

	}

	private boolean openNewYear(final FinancialYear year) {

		final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
		// close all tabs except the current tab
		if (!Global.closeTabs(tabPane, currentTab)) {
			return false;
		}

		Database.shutDownActiveYearDatabase();

		if (!Database.openAsActiveYear(year)) {
			String message = "An error occurred while connecting to the database. "
					+ " of the newly created year  " + year.toString();
			final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.initOwner(mainWindow);
			alert.setTitle("Connexion error");
			alert.setHeaderText("Unable to connect to new year's database!");
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		return true;

	}

	public boolean shouldOpenTheNewYear(final FinancialYear year) throws SQLException {
		String message = "The financial year  " + year.toString() + " was created successfully!!\n\n"
				+ "Do you want to open it now? ";

		final FinancialYear activeYear = Global.getActiveFinancialYear();
		if (activeYear != null) {

			message += "\nAll tabs related to the currently open year " + activeYear.toString()
			+ " will be closed first.";
		}

		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		alert.initOwner(mainWindow);
		alert.setTitle("Success");
		alert.setHeaderText("New Year created successfully! Open it now?");
		Global.styleAlertDialog(alert);

		Optional<ButtonType> response = alert.showAndWait();
		if (!response.isPresent()) {
			return false;
		}

		return (response.get() == ButtonType.YES ? true : false);
	}

	public boolean createDatabase(final FinancialYear year) {
		FinancialYear overlappingYear = FinancialYear.getOverlappingYear(lvwExistingYears.getItems(), year);
		if (!Database.createDatabase(year) || overlappingYear != null) {
			String message = "An error occurred while creating a new year. The new fiscal year overlaps "
					+ "with existing year '" + overlappingYear.toString() + "'.";
			Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.initOwner(mainWindow);
			alert.setTitle("Error Occurred");
			alert.setHeaderText("Error creating database");
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		return true;
	}

	/**
	 * Creates a folder in the disk corresponding to the specified financial
	 * year.
	 * 
	 * @param fy
	 * @return The path corresponding to the newly created folder
	 */
	public boolean createNewYearFolder(final FinancialYear fy) {

		if (!Global.createFinancialYearFolder(fy)) {
			Utility.beep();
			final Alert alert = new Alert(Alert.AlertType.ERROR,
					"An error occurred while creating a directory for the new year..",
					ButtonType.OK);
			alert.setTitle("Error Occurred");
			alert.setHeaderText("Error when creating a folder for the new year");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		return true;

	}

	/**
	 * 
	 * @param newConnection
	 *            - Connection to the newly created database
	 * @return - True if the data transfer was successful
	 */
	private boolean transferDataFromExistingYear(final FinancialYear newYear) {

		// get the selected existing year
		final FinancialYear existingYear = lvwExistingYears.getSelectionModel().getSelectedItem();

		if (!Database.transferData(newYear, existingYear)) {
			final String message = "An error occurred while transferring data from "
					+ "the existing year " + existingYear.toString() + " to the newly created year!"
					+ "\nAll the work done to create the new year has been undone.";
			final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.initOwner(mainWindow);
			alert.setTitle("Error during data transfer");
			alert.setHeaderText("Error transferring data from existing year to new year!");
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return false;
		}

		return true;

	}

}
