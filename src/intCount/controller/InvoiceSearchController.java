/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;


import intCount.database.*;
import intCount.model.*;
import intCount.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.collections.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.util.Callback;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import java.util.logging.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class InvoiceSearchController implements TabContent, EntityEditedListener {

	private final static Logger logger = Logger.getLogger(InvoiceSearchController.class.getName());

	@FXML
	private Label criteriaSelectionError;
	@FXML
	private RadioButton invoiceNumberButton;
	@FXML
	private HBox invoiceNumberPanel;
	@FXML
	private RadioButton otherCriteriaButton;
	@FXML
	private VBox otherCriteriaPanel;
	@FXML
	private CheckBox invoiceDateButton;
	@FXML
	private HBox invoiceDatePanel;
	@FXML
	private VBox customersPanel;
	@FXML
	private CheckBox invoiceAmountButton;
	@FXML
	private HBox invoiceAmountPanel;
	@FXML
	private ToggleGroup searchCriteriaGroup;
	@FXML
	private Button searchInvoiceButton;
	@FXML
	private Label invoiceNumberError;
	@FXML
	private Label startDateError;
	@FXML
	private Label endDateError;
	@FXML
	private Label customersError;
	@FXML
	private Label startAmountError;
	@FXML
	private Label endAmountError;
	@FXML
	private TextField invoiceNumberField;
	@FXML
	private CheckListView<Customer> customersListView;
	@FXML
	private DatePicker startDateField;
	@FXML
	private DatePicker endDateField;
	@FXML
	private TextField startAmountField;
	@FXML
	private TextField endAmountField;
	@FXML
	private Label startAmountLabel;
	@FXML
	private Label endAmountLabel;
	@FXML
	private TableView<InvoiceSearchResult> tableView;
	@FXML
	private TableColumn<InvoiceSearchResult, Customer> customerColumn;
	@FXML
	private TableColumn<InvoiceSearchResult, LocalDate> invoiceDateColumn;
	@FXML
	private TableColumn<InvoiceSearchResult, String> invoiceNumberColumn;
	@FXML
	private TableColumn<InvoiceSearchResult, BigDecimal> invoiceAmountColumn;
	@FXML
	private TitledPane invoiceSearchResultPanel;
	@FXML
	private TitledPane invoiceSearchCriteriaPanel;
	@FXML
	private CheckBox selectAllCustomersButton;
	@FXML
	private RadioButton creditInvoiceButton;
	@FXML
	private VBox invoiceTypePanel;
	@FXML
	private CheckBox invoiceTypeButton;
	@FXML
	private RadioButton cashInvoiceButton;
	@FXML
	private Label invoiceTypeError;
	@FXML
	private Label invoiceTotalLabel;
	//@FXML
	//private Button deleteInvoiceButton;
	//@FXML
	//private Button editInvoiceButton;
	@FXML
	private Button printInvoiceButton;
	@FXML
	private TextField tfSearchCustomer;

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private final IntegerProperty matchingInvoicesCount = new SimpleIntegerProperty(0);
	private Stage mainWindow = null;
	private Label[] errorLabels = null;
	private TabPane tabPane = null;
	private UserPreferences userPreferences = null;
	private final LocalDate minDate = Global.getActiveFinancialYear().getStartDate();
	private final LocalDate maxDate = Utility.minDate(Global.getActiveFinancialYear().getEndDate(), LocalDate.now());
	private ObservableList<Customer> customersList = null;
	private FilteredList<Customer> filteredCustomerList = null;

	@Override
	public boolean shouldClose() {
		return true;
	}

	@Override
	public void putFocusOnNode() {
		invoiceNumberButton.requestFocus();
	}

	@Override
	public boolean loadData() {
		return loadCustomers();
	}

	@Override
	public void setMainWindow(Stage stage) {
		mainWindow = stage;
	}

	
	public void initialize() {

		invoiceNumberPanel.managedProperty().bind(invoiceNumberPanel.visibleProperty());
		invoiceNumberPanel.visibleProperty().bind(invoiceNumberButton.selectedProperty());

		invoiceDatePanel.managedProperty().bind(invoiceDatePanel.visibleProperty());
		invoiceDatePanel.visibleProperty().bind(invoiceDateButton.selectedProperty());

		otherCriteriaPanel.managedProperty().bind(otherCriteriaPanel.visibleProperty());
		otherCriteriaPanel.visibleProperty().bind(otherCriteriaButton.selectedProperty());

		invoiceTypePanel.managedProperty().bind(invoiceTypePanel.visibleProperty());
		invoiceTypePanel.visibleProperty().bind(invoiceTypeButton.selectedProperty());

		customersPanel.managedProperty().bind(customersPanel.visibleProperty());
		customersPanel.visibleProperty().bind(creditInvoiceButton.selectedProperty());

		invoiceAmountPanel.managedProperty().bind(invoiceAmountPanel.visibleProperty());
		invoiceAmountPanel.visibleProperty().bind(invoiceAmountButton.selectedProperty());

		searchInvoiceButton.disableProperty().bind(searchCriteriaGroup.selectedToggleProperty().isNull());

		errorLabels = new Label[] { criteriaSelectionError, invoiceNumberError, startDateError, endDateError,
				startAmountError, endAmountError, customersError, invoiceTypeError };
		for (Label label : errorLabels) {
			label.managedProperty().bind(label.visibleProperty());
			label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
		}

		startDateField.setValue(LocalDate.now());
		startDateField.setConverter(Utility.getDateStringConverter());
		startDateField.setDayCellFactory(this::getDateCell);

		endDateField.setValue(LocalDate.now());
		endDateField.setConverter(Utility.getDateStringConverter());
		endDateField.setDayCellFactory(this::getDateCell);

	
		startAmountLabel.setText(startAmountLabel.getText() + "DH");
		endAmountLabel.setText(endAmountLabel.getText() + "DH");

		selectAllCustomersButton.setTooltip(new Tooltip("Check this box to select all customers.."));

		/*
		editInvoiceButton.setMaxWidth(Double.MAX_VALUE);
		editInvoiceButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		deleteInvoiceButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		*/
		printInvoiceButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

		invoiceSearchResultPanel.managedProperty().bind(invoiceSearchResultPanel.visibleProperty());
		invoiceSearchResultPanel.visibleProperty().bind(matchingInvoicesCount.greaterThan(0));

		tfSearchCustomer.textProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					if (newValue == null || newValue.isEmpty()) {
						filteredCustomerList.setPredicate(null); // show all
																	// customers
					} else {
						filteredCustomerList.setPredicate(
								(Customer t) -> t.getName().toLowerCase().contains(newValue.toLowerCase()));
					}
				});

		setTableCellFactories();
	}

	@FXML
	private void onSelectAllCustomersAction(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof CheckBox) {
			CheckBox checkBox = (CheckBox) source;
			if (checkBox.isSelected()) {
				customersListView.getCheckModel().checkAll();
				checkBox.getTooltip().setText("Unchecking the checkbox will deselect all clients");
			} else {
				customersListView.getCheckModel().clearChecks();
				checkBox.getTooltip().setText("Check this box to select all customers.");
			}
		}

	}

	@FXML
	private void onInvoiceSearchAction(ActionEvent event) {
		if (!validateInput()) {
			return;
		}

		matchingInvoicesCount.set(0);
		InvoiceSearchCriteria criteria = getCriteria();
		List<InvoiceSearchResult> results = null;

		try {
			results = InvoiceSearch.searchInvoice(criteria);
		} catch (Exception e) {
			Utility.beep();
			String message = "An error occurred while searching the database for matching invoices.!";
			Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.setTitle("Erreur occured");
			alert.setHeaderText("Error while searching for invoices");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		tableView.setItems(null);
		final ObservableList<InvoiceSearchResult> observableResults = FXCollections
				.<InvoiceSearchResult>observableList(results, (InvoiceSearchResult result) -> {
					Observable[] observables = new Observable[] { result.customerProperty(),
							result.invoiceDateProperty(), result.invoiceAmountProperty() };
					return observables;
				});

		tableView.setItems(observableResults);
		int matchCount = results.size();

		if (matchCount > 0) {
			invoiceTotalLabel.setText(String.format("The total of %d invoices is %s", matchCount,
					IndianCurrencyFormatting.applyFormatting(getTotalAmount(results))));
			invoiceSearchCriteriaPanel.setExpanded(false);
			matchingInvoicesCount.set(matchCount);
			invoiceSearchResultPanel.setExpanded(true);
			tableView.getSelectionModel().selectFirst();
			tableView.scrollTo(0);
			tableView.requestFocus();
		} else {
			Utility.beep();
			final Alert alert = new Alert(Alert.AlertType.INFORMATION,
					"No invoice matches your search criteria.", ButtonType.OK);
			alert.setTitle("No result found");
			alert.setHeaderText("No matching invoice found");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
		}

	}

	private InvoiceSearchCriteria getCriteria() {
		InvoiceSearchCriteria criteria = new InvoiceSearchCriteria();

		if (invoiceNumberButton.isSelected()) {
			criteria.setInvoiceNumber(invoiceNumberField.getText().trim());
		} else {
			if (invoiceDateButton.isSelected()) {
				criteria.setStartDate(startDateField.getValue());
				criteria.setEndDate(endDateField.getValue());
			}

			if (invoiceTypeButton.isSelected()) {
				if (cashInvoiceButton.isSelected()) {
					criteria.setTypeInvoice("('Facture'" + "," + "'Avoir')");

				} else {
					if (creditInvoiceButton.isSelected()) {
						List<Customer> customers = customersListView.getCheckModel().getCheckedItems();
						criteria.setTypeInvoice("'Devis'");
						criteria.setCustomers(customers);

					}
				}
			}

			if (invoiceAmountButton.isSelected()) {
				BigDecimal amount = new BigDecimal(startAmountField.getText().trim());
				amount = amount.setScale(2, RoundingMode.HALF_UP);
				criteria.setStartAmount(amount);

				amount = new BigDecimal(endAmountField.getText().trim());
				amount = amount.setScale(2, RoundingMode.HALF_UP);
				criteria.setEndAmount(amount);
			}
		}

		return criteria;
	}

	private boolean validateInput() {
		clearErrorLabels();

		if (invoiceNumberButton.isSelected()) {
			return validateInvoiceNumber();
		}

		if (!(invoiceDateButton.isSelected() || invoiceAmountButton.isSelected() || invoiceTypeButton.isSelected())) {
			criteriaSelectionError.setText("No criteria selected!");
			return false;
		}

		boolean valid = true;

		if (invoiceTypeButton.isSelected()) {
			if (!(creditInvoiceButton.isSelected() || cashInvoiceButton.isSelected())) {
				invoiceTypeError.setText("Cash or credit bill not selected!");
				valid = false;
			}

			if (creditInvoiceButton.isSelected() && customersListView.checkModelProperty().getValue().isEmpty()) {
				customersError.setText("No customer selected!");
				valid = false;
			}

		}

		if (invoiceDateButton.isSelected() && !validateInvoiceDate()) {
			valid = false;
		}

		if (invoiceAmountButton.isSelected() && !validateInvoiceAmount()) {
			valid = false;
		}

		return valid;
	}

	private boolean validateInvoiceDate() {
		boolean valid = true;

		LocalDate startDate = startDateField.getValue();
		if (startDate == null) {
			startDateError.setText("Start date not specified!");
			valid = false;
		} else if (startDate.isAfter(maxDate)) {
			startDateError.setText("Start date cannot be later than" + maxDate.format(dateFormatter));
			valid = false;
		} else if (startDate.isBefore(minDate)) {
			startDateError.setText("Start date cannot be before " + minDate.format(dateFormatter));
			valid = false;
		}

		LocalDate endDate = endDateField.getValue();
		if (endDate == null) {
			endDateError.setText("End date not specified!");
			valid = false;
		} else if (endDate.isAfter(maxDate)) {
			endDateError.setText("End date cannot be later than " + maxDate.format(dateFormatter));
			valid = false;
		} else if (endDate.isBefore(minDate)) {
			endDateError.setText("End date cannot be before " + minDate.format(dateFormatter));
			valid = false;
		}

		if (valid && startDate.isAfter(endDate)) {
			startDateError.setText("The start date cannot be later than the end date!");
			valid = false;
		}

		return valid;

	}

	private boolean validateInvoiceAmount() {
		boolean valid = true;

		BigDecimal startAmount = null;
		String startAmountString = startAmountField.getText().trim();

		if (startAmountString.isEmpty()) {
			startAmountError.setText("Starting amount not specified!");
			valid = false;
		} else {
			try {
				startAmount = new BigDecimal(startAmountString);
			} catch (NumberFormatException e) {
				startAmountError.setText("Not a valid amount!");
				valid = false;
			}

			if (startAmount != null && startAmount.signum() == -1) {
				startAmountError.setText("The starting amount must be a positive number!");
				valid = false;
			}
		}

		BigDecimal endAmount = null;
		String endAmountString = endAmountField.getText().trim();

		if (endAmountString.isEmpty()) {
			endAmountError.setText("Final amount not specified!");
			valid = false;
		} else {
			try {
				endAmount = new BigDecimal(endAmountString);
			} catch (NumberFormatException e) {
				endAmountError.setText("Not a valid amount!");
				valid = false;
			}

			if (endAmount != null && endAmount.signum() == -1) {
				endAmountError.setText("The final amount must be a positive number!");
				valid = false;
			}
		}

		if (valid && startAmount.compareTo(endAmount) == 1) {
			startAmountError.setText("The starting amount cannot be greater than the ending amount!");
			valid = false;
		}

		return valid;
	}

	private boolean validateInvoiceNumber() {
		boolean valid = true;

		String numberString = invoiceNumberField.getText().trim();
		if (numberString.isEmpty()) {
			invoiceNumberError.setText("Invoice number not specified!");
			valid = false;
		} else {
			int number = -1;
			try {
				number = Integer.parseUnsignedInt(numberString);
			} catch (NumberFormatException e) {
				invoiceNumberError.setText("Invalid invoice number!");
				valid = false;
			}

			if (number == 0) {
				invoiceNumberError.setText("Invoice number cannot be zero!");
				valid = false;
			}
		}

		return valid;
	}

	private void clearErrorLabels() {
		for (Label label : errorLabels) {
			label.setText("");
		}
	}

	private boolean loadCustomers() {
		List<Customer> customers = null;

		try {
			customers = CustomersPersistence.getCustomers();
		} catch (Exception e) {
			String message = Utility.getDataFetchErrorText();
			Alert alert = Utility.getErrorAlert("Erreur occured", "Error retrieving clients !",
					message, mainWindow);
			Utility.beep();
			alert.showAndWait();
			return false;
		}

		customersList = FXCollections.<Customer>observableList(customers);
		filteredCustomerList = new FilteredList<>(customersList, null);
		customersListView.setItems(filteredCustomerList);

		if (customers.isEmpty()) {
			selectAllCustomersButton.setDisable(true);
		}

		return true;
	}

	private DateCell getDateCell(DatePicker datePicker) {
		return Utility.getDateCell(datePicker, minDate, maxDate);
	}

	@FXML
	private void onInvoiceDeleteAction(ActionEvent event) {
		// get the selected invoice
		InvoiceSearchResult searchResult = tableView.getSelectionModel().getSelectedItem();
		String invoiceNumber = searchResult.getInvoiceNumber();

		String promptMessage = "Are you sure you want to delete the numbered invoice" + invoiceNumber + " ?";
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("Make sure to delete the selected invoice ?");
		alert.setTitle("Confirm invoice deletion");
		alert.setContentText(promptMessage);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		alert.initOwner(mainWindow);
		Global.styleAlertDialog(alert);

		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent()) { // user dismissed the dialog without
									// selecting any option
			return;
		}

		ButtonType response = result.get();
		if (response == ButtonType.NO) {
			return;
		}

		try {
			InvoiceSearch.deleteInvoice(Integer.valueOf(invoiceNumber));
		} catch (Exception e) {
			Utility.beep();
			String message = "Unable to delete invoice due to error!";
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur occured");
			alert.setHeaderText("Error deleting invoice");
			alert.setContentText(message);
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		tableView.getItems().remove(searchResult);

	}

	@FXML
	private void onInvoiceEditAction(ActionEvent event) {
		// get the selected invoice
		InvoiceSearchResult searchResult = tableView.getSelectionModel().getSelectedItem();
		String invoiceNumber = searchResult.getInvoiceNumber();
		String invoiceType = searchResult.getInvoiceType();

		String path = "/intCount/view/Invoice.fxml";
		FXMLLoader loader = new FXMLLoader();
		URL resource = this.getClass().getResource(path);
		loader.setLocation(resource);
		Parent rootPane = null;

		try {
			rootPane = loader.load();
		} catch (Exception e) {
			logger.logp(Level.SEVERE, InvoiceSearchController.class.getName(), "onInvoiceEditAction",
					"Error loading invoice fxml file", e);
			Alert alert = Utility.getErrorAlert("Erreur occured",
					"An error occurred while opening the invoice",
					"Unable to open invoice due to an error.", mainWindow);
			Utility.beep();
			alert.showAndWait();
			return;
		}

		InvoiceController controller = (InvoiceController) loader.getController();
		controller.setMainWindow(mainWindow);
		if (!controller.loadData()) {
			return;
		}

		if (!controller.loadInvoice(invoiceNumber)) {
			return;
		}
		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(mainWindow);
		if (invoiceType == "Facture") {
			stage.setTitle("Editing of invoice #" + invoiceNumber);

		} else if (invoiceType == "Avoir") {
			stage.setTitle("Editing of creditInvoice #" + invoiceNumber);

		} else if (invoiceType == "Devis") {
			stage.setTitle("Editing quotation #" + invoiceNumber);
		}
		stage.getIcons().add(new Image("/resources/images/billing_32.png"));
		controller.setMainWindow(stage);
		controller.addEntityEditedEventListener(this);

		Global.setStageDefaultDimensions(stage);

		Scene scene = new Scene(rootPane);
		stage.setScene(scene);
		stage.showAndWait();

	}

	private BigDecimal getInvoiceTotal(Invoice invoice) {
		List<InvoiceItem> invoiceItems = invoice.getInvoiceItems();
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal amount = null;

		for (InvoiceItem invoiceItem : invoiceItems) {
			amount = invoiceItem.getQuantity().multiply(invoiceItem.getRate()).setScale(2, RoundingMode.HALF_UP);
			total = total.add(amount);
		}

		amount = invoice.getAdditionalCharge();
		if (amount != null) {
			total = total.add(amount);
		}

		amount = invoice.getDiscount();
		if (amount != null) {
			total = total.subtract(amount);
		}

		return total;

	}

	@Override
	public void setTabPane(TabPane pane) {
		this.tabPane = pane;
	}

	@Override
	public void onEntityEdited(EntityEditedEvent event) {
		Object editedEntity = event.getEntityEdited();
		Invoice invoice = (Invoice) editedEntity;

		InvoiceSearchResult searchResult = tableView.getSelectionModel().getSelectedItem();
		searchResult.setInvoiceDate(invoice.getInvoiceDate());

		// if (invoice.getIsCashInvoice()) {
		if (searchResult.getCustomer().getId() != 0) {
			Customer customer = new Customer();
			customer.setName(invoice.getCustomer().getName());
			searchResult.setCustomer(customer);
		}
		// } else {
		// if (searchResult.getCustomer().getId() == 0) {
		// searchResult.setCustomer(invoice.getCustomer());
		// }
		// }

		searchResult.setInvoiceAmount(getInvoiceTotal(invoice));
	}

	@FXML
	private void onInvoicePrintAction(ActionEvent event) {

			final InvoiceSearchResult selectedItem = tableView.getSelectionModel().getSelectedItem();
			final Integer invoiceNumber = Integer.valueOf(selectedItem.getInvoiceNumber());

			Invoice invoice = null;
			try {
				invoice = InvoicePersistence.getInvoice(invoiceNumber);
			} catch (Exception e) {
				String message = "An error occurred while retrieving invoice details."
						+ " from the database.";
				Alert alert = Utility.getErrorAlert("Erreur occured", "Error while retrieving data",
						message, mainWindow);
				Utility.beep();
				alert.showAndWait();
				return;
			}

			if (userPreferences == null) {
				userPreferences = Global.getUserPreferences();
			}

			try {
				printInvoice(invoice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	private void printInvoice(Invoice invoice) throws Exception {


		Map<String, Object> map = null;
		Connection connection = Database.getActiveYearConnection();

		try {
			map = getReportParameters(invoice);
		} catch (Exception e) {
			Utility.beep();
			String message = "An error occurred while collecting data to print the invoice.!";
			String messageDevis = "An error occurred while collecting data to print the quotation.!";
			String messageAvoir = "An error occurred while collecting data to print the creditInvoice.!";
			if (invoice.getInvoiceType().equals("Facture")) {

				Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
				alert.setTitle("Erreur occured");
				alert.setHeaderText("Error extracting data for invoice printing");
				alert.initOwner(mainWindow);
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;
			} else if (invoice.getInvoiceType().equals("Devis")) {
				Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
				alert.setTitle("Erreur occured");
				alert.setHeaderText("Error when extracting data for printing the estimate");
				alert.initOwner(mainWindow);
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;
			} else if (invoice.getInvoiceType().equals("Avoir")) {
				Alert alert = new Alert(Alert.AlertType.ERROR, messageAvoir, ButtonType.OK);
				alert.setTitle("Erreur occured");
				alert.setHeaderText("Error when extracting data for creditInvoice printing");
				alert.initOwner(mainWindow);
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;
			}

		}

		final String resourcePath = "/resources/reports/intCount_invoice.jasper";
		final String resourcePathDevis = "/resources/reports/intCount_Offer.jasper";
		final String resourcePathAvoir = "/resources/reports/intCount_Avoir.jasper";
		final String resourcePathAe = "/resources/reports/intCount_invoice_Ae.jasper";
		final String resourcePathDevisAe = "/resources/reports/intCount_Offer_Ae.jasper";
		final String resourcePathAvoirAe = "/resources/reports/intCount_Avoir_Ae.jasper";
		
		JasperPrint jasperPrint = null;
		File facture = null;
		
		if(InvoicePersistence.getInvoiceItemsCount(invoice, connection)== true){
		if (invoice.getInvoiceType().equals("Facture")) {

			try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePath)) {

				jasperPrint = JasperFillManager.fillReport(reportStream, map, new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));

				facture = new File(System.getProperty("user.dir").concat("/intCount").concat("/Facture/")
						.concat(invoice.getCustomer().getName().toString()));

				if (!facture.exists()) {
					facture.mkdirs();
				}else
				JasperExportManager.exportReportToPdfFile(jasperPrint,
						System.getProperty("user.dir").concat("/intCount").concat("/Facture/")
								.concat(invoice.getCustomer().getName().toString()).concat("/") + "F"
								+ String.format("%04d", InvoicePersistence.getIdOfInvoice(connection)) + ".pdf");
				
			} catch (Exception e) {
				logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
						"Error in call to fillReport function", e);
				Utility.beep();
				String message = "An error occurred while preparing to print the invoice.!";

				Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
				alert.setTitle("Erreur occured");
				alert.setHeaderText("Invoice printing error");
				alert.initOwner(mainWindow);
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;
			}
			
		} else if (invoice.getInvoiceType().equals("Devis")) {

			try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePathDevis)) {

				jasperPrint = JasperFillManager.fillReport(reportStream, map,
						new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));
				facture = new File(System.getProperty("user.dir").concat("/intCount").concat("/Devis/")
						.concat(invoice.getCustomer().getName().toString()));

				if (!facture.exists()) {
					facture.mkdirs();
				}else 
				JasperExportManager.exportReportToPdfFile(jasperPrint,
						System.getProperty("user.dir").concat("/intCount").concat("/Devis/")
								.concat(invoice.getCustomer().getName().toString()).concat("/") + "F"
								+ String.format("%04d", InvoicePersistence.getIdOfInvoice(connection)) + ".pdf");
			} catch (Exception e) {
				logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
						"Error in call to fillReport function", e);
				Utility.beep();
				String messageDevis = "An error occurred while preparing to print the quotation.!";
				Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
				alert.setTitle("Erreur occured");
				alert.setHeaderText("Quote printing error");
				alert.initOwner(mainWindow);
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;

			}
		} else if (invoice.getInvoiceType().equals("Avoir")) {

			try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePathAvoir)) {

				jasperPrint = JasperFillManager.fillReport(reportStream, map,
						new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));
				facture = new File(System.getProperty("user.dir").concat("/intCount").concat("/Avoir/")
						.concat(invoice.getCustomer().getName().toString()));

				if (!facture.exists()) {
					facture.mkdirs();
				}else
				JasperExportManager.exportReportToPdfFile(jasperPrint,
						System.getProperty("user.dir").concat("/intCount").concat("/Avoir/")
								.concat(invoice.getCustomer().getName().toString()).concat("/") + "F"
								+ String.format("%04d", InvoicePersistence.getIdOfInvoice(connection)) + ".pdf");
			} catch (Exception e) {
				logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
						"Error in call to fillReport function", e);
				Utility.beep();
				String messageDevis = "An error occurred while preparing the creditInvoice printing.!";
				Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
				alert.setTitle("Erreur occured");
				alert.setHeaderText("Credit note printing error");
				alert.initOwner(mainWindow);
				Global.styleAlertDialog(alert);
				alert.showAndWait();
				return;

			}
		}
		if (userPreferences.getShowPrintPreview()) {
			JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
			if (invoice.getInvoiceType().equals("Facture")) {
				jasperViewer.setTitle("Invoice");
			} else if (invoice.getInvoiceType().equals("Devis")) {
				jasperViewer.setTitle("Devis");
			} else if (invoice.getInvoiceType().equals("Avoir")) {
				jasperViewer.setTitle("Avoir");
			}

			jasperViewer.setVisible(true);
		} else {
			// command for printing the report
			try {
				// false means don't show the print dialog. Send output instead
				// directly to the default printer
				boolean showPrintDialog = userPreferences.getShowPrintDialog();
				JasperPrintManager.printReport(jasperPrint, showPrintDialog);

			} catch (Exception ex) {
				logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
						"Error in printReport function call", ex);
				Utility.beep();
				String message = "An error occurred while printing the invoice!";
				String messageDevis = "An error occurred while printing the quotation!";
				String messageAvoir = "An error occurred while printing the creditInvoice!";
				if (invoice.getInvoiceType().equals("Facture")) {
					Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
					alert.setTitle("Invoice printing error");
					alert.setHeaderText("Invoice printing error");
					alert.initOwner(mainWindow);
					Global.styleAlertDialog(alert);
					alert.showAndWait();
				} else if (invoice.getInvoiceType().equals("Devis")) {
					Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
					alert.setTitle("Quotation printing error");
					alert.setHeaderText("Quotation printing error");
					alert.initOwner(mainWindow);
					Global.styleAlertDialog(alert);
					alert.showAndWait();
				} else if (invoice.getInvoiceType().equals("Avoir")) {
					Alert alert = new Alert(Alert.AlertType.ERROR, messageAvoir, ButtonType.OK);
					alert.setTitle("CreditInvoice printing error");
					alert.setHeaderText("CreditInvoice printing error");
					alert.initOwner(mainWindow);
					Global.styleAlertDialog(alert);
					alert.showAndWait();
				}

			}
		}
		} else if(InvoicePersistence.getInvoiceItemsCount(invoice, connection)==false) {
			System.out.print("TVA=0");
			if (invoice.getInvoiceType().equals("Facture")) {

				try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePathAe)) {

					jasperPrint = JasperFillManager.fillReport(reportStream, map, new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));

					facture = new File(System.getProperty("user.dir").concat("/intCount").concat("/FactureAe/")
							.concat(invoice.getCustomer().getName().toString()));

					if (!facture.exists()) {
						facture.mkdirs();
					}else
					JasperExportManager.exportReportToPdfFile(jasperPrint,
							System.getProperty("user.dir").concat("/intCount").concat("/FactureAe/")
									.concat(invoice.getCustomer().getName().toString()).concat("/") + "F"
									+ String.format("%04d", InvoicePersistence.getIdOfInvoice(connection)) + ".pdf");
				} catch (Exception e) {
					logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
							"Error in call to fillReport function", e);
					Utility.beep();
					String message = "An error occurred while preparing to print the invoice.!";

					Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
					alert.setTitle("Erreur occured");
					alert.setHeaderText("Invoice printing error");
					alert.initOwner(mainWindow);
					Global.styleAlertDialog(alert);
					alert.showAndWait();
					return;
				}
				
			} else if (invoice.getInvoiceType().equals("Devis")) {

				try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePathDevisAe)) {

					jasperPrint = JasperFillManager.fillReport(reportStream, map,
							new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));
					facture = new File(System.getProperty("user.dir").concat("/intCount").concat("/DevisAe/")
							.concat(invoice.getCustomer().getName().toString()));

					if (!facture.exists()) {
						facture.mkdirs();
					}else
					JasperExportManager.exportReportToPdfFile(jasperPrint,
							System.getProperty("user.dir").concat("/intCount").concat("/DevisAe/")
									.concat(invoice.getCustomer().getName().toString()).concat("/") + "F"
									+ String.format("%04d", InvoicePersistence.getIdOfInvoice(connection)) + ".pdf");
				} catch (Exception e) {
					logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
							"Error in call to fillReport function", e);
					Utility.beep();
					String messageDevis = "An error occurred while preparing to print the quote.!";
					Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
					alert.setTitle("Erreur occured");
					alert.setHeaderText("Quote printing error");
					alert.initOwner(mainWindow);
					Global.styleAlertDialog(alert);
					alert.showAndWait();
					return;

				}
			} else if (invoice.getInvoiceType().equals("Avoir")) {

				try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePathAvoirAe)) {

					jasperPrint = JasperFillManager.fillReport(reportStream, map,
							new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));
					facture = new File(System.getProperty("user.dir").concat("/intCount").concat("/AvoirAe/")
							.concat(invoice.getCustomer().getName().toString()));

					if (!facture.exists()) {
						facture.mkdirs();
					}else
					JasperExportManager.exportReportToPdfFile(jasperPrint,
							System.getProperty("user.dir").concat("/intCount").concat("/AvoirAe/")
									.concat(invoice.getCustomer().getName().toString()).concat("/") + "F"
									+ String.format("%04d", InvoicePersistence.getIdOfInvoice(connection)) + ".pdf");
				} catch (Exception e) {
					logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
							"Error in call to fillReport function", e);
					Utility.beep();
					String messageDevis = "An error occurred while preparing the creditInvoice printing.!";
					Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
					alert.setTitle("Erreur occured");
					alert.setHeaderText("CreditInvoice printing error");
					alert.initOwner(mainWindow);
					Global.styleAlertDialog(alert);
					alert.showAndWait();
					return;

				}
			}
			if (userPreferences.getShowPrintPreview()) {
				JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
				if (invoice.getInvoiceType().equals("Facture")) {
					jasperViewer.setTitle("InvoiceAe");
				} else if (invoice.getInvoiceType().equals("Devis")) {
					jasperViewer.setTitle("DevisAe");
				} else if (invoice.getInvoiceType().equals("Avoir")) {
					jasperViewer.setTitle("AvoirAe");
				}

				jasperViewer.setVisible(true);
			} else {
				// command for printing the report
				try {
					// false means don't show the print dialog. Send output instead
					// directly to the default printer
					boolean showPrintDialog = userPreferences.getShowPrintDialog();
					JasperPrintManager.printReport(jasperPrint, showPrintDialog);

				} catch (Exception ex) {
					logger.logp(Level.SEVERE, InvoiceController.class.getName(), "printInvoice",
							"Error in printReport function call", ex);
					Utility.beep();
					String message = "An error occurred while printing the invoice!";
					String messageDevis = "An error occurred while printing the quotation!";
					String messageAvoir = "An error occurred while printing the creditInvoice!";
					if (invoice.getInvoiceType().equals("Facture")) {
						Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
						alert.setTitle("Invoice printing error");
						alert.setHeaderText("Invoice printing error");
						alert.initOwner(mainWindow);
						Global.styleAlertDialog(alert);
						alert.showAndWait();
					} else if (invoice.getInvoiceType().equals("Devis")) {
						Alert alert = new Alert(Alert.AlertType.ERROR, messageDevis, ButtonType.OK);
						alert.setTitle("Quotation printing error");
						alert.setHeaderText("Quotation printing error");
						alert.initOwner(mainWindow);
						Global.styleAlertDialog(alert);
						alert.showAndWait();
					} else if (invoice.getInvoiceType().equals("Avoir")) {
						Alert alert = new Alert(Alert.AlertType.ERROR, messageAvoir, ButtonType.OK);
						alert.setTitle("CreditInvoice printing error");
						alert.setHeaderText("CreditInvoice printing error");
						alert.initOwner(mainWindow);
						Global.styleAlertDialog(alert);
						alert.showAndWait();
					}

				}
			}
		}
	}

	private Map<String, Object> getReportParameters(Invoice invoice) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>(12); // represents
																		// report
																		// parameters

		map.put("invoiceNumber", new Integer(invoice.getInvoiceId()));
		map.put("invoiceDate", invoice.getInvoiceDate());

		// if (!invoice.getIsCashInvoice()) {
		Customer customer = invoice.getCustomer();

		if (customer != null) {
			map.put("customerName", customer.getName());
		}

		String ice = invoice.getCustomer().getIce();
		if (ice != null) {
			map.put("customerIce", ice);
			// }
		}
		String taxeProfessionnel = invoice.getCustomer().getTaxeProfessionnel();
		if ((taxeProfessionnel != null)) {
			map.put("customerTaxeProfessionnel", taxeProfessionnel);

		}
		String customerAdresse = invoice.getCustomer().getCity();
		if ((customerAdresse != null)) {
			map.put("customerAdresse", customerAdresse);

		}

		BigDecimal amount = invoice.getAdditionalCharge();
		if (amount != null) {
			map.put("additionalCharge", amount);
		}

		BigDecimal amounts = invoice.getDiscount();
		if (amount != null) {
			map.put("discount", amounts);
		}

		FirmDetails firmDetails = FirmDetailsPersistence.getData();
		if (firmDetails != null) {
			String name = firmDetails.getFirmName();
			String address = firmDetails.getAddress();
			if (name != null) {
				map.put("firmName", name);
			}
			if (address != null) {
				map.put("firmAddress", address);
			}
			String str = firmDetails.getPhoneNumbers();
			if (str != null) {
				map.put("firmPhoneNumbers", str);
			}

			str = firmDetails.getEmailAddress();
			if (str != null) {
				map.put("firmEmailAddress", str);
			}

			byte[] logoBytes = firmDetails.getLogo();

			if (logoBytes != null) {
				ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(logoBytes);
				map.put("firmLogo", byteArrayStream);
			}

			String firmTaxeProfessionnel = firmDetails.getTaxeProfessionnel();
			if (firmTaxeProfessionnel != null) {
				map.put("firmTaxeProfessionnel", firmTaxeProfessionnel);
			}
			String firmBankAccount = firmDetails.getBankAccount();
			if (firmBankAccount != null) {
				map.put("firmBankAccount", firmBankAccount);
			}
			String firmIce = firmDetails.getIceField();

			if (firmIce != null) {
				map.put("firmIce", firmIce);
			}

			String firmIF = firmDetails.getIdentificationFiscal();
			if (firmIF != null) {
				map.put("firmIF", firmIF);
			}
			String firmCnss = firmDetails.getNumeroDeCnss();
			if(firmCnss != null) {
				map.put("CIN",firmCnss);
			}

		}

		String invoiceType = invoice.getInvoiceType();
		map.put("invoiceType", invoiceType);

		return map;
	}

	private void setTableCellFactories() {
		// define a custom cell factory so as to format the data in the cell
		invoiceDateColumn.setCellFactory(
				new Callback<TableColumn<InvoiceSearchResult, LocalDate>, TableCell<InvoiceSearchResult, LocalDate>>() {

					@Override
					public TableCell<InvoiceSearchResult, LocalDate> call(
							TableColumn<InvoiceSearchResult, LocalDate> param) {
						TableCell<InvoiceSearchResult, LocalDate> cell = new TableCell<InvoiceSearchResult, LocalDate>() {

							@Override
							protected void updateItem(LocalDate item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null); // cleanup job as the cells
													// may be reused
								if (!empty) {
									String formattedDate = dateFormatter.format(item);
									this.setText(formattedDate);
									this.setTextAlignment(TextAlignment.CENTER);
								}
							}

						};
						cell.setAlignment(Pos.CENTER);
						return cell;
					}

				});

		invoiceNumberColumn.setCellFactory(
				new Callback<TableColumn<InvoiceSearchResult, String>, TableCell<InvoiceSearchResult, String>>() {

					@Override
					public TableCell<InvoiceSearchResult, String> call(TableColumn<InvoiceSearchResult, String> param) {
						TableCell<InvoiceSearchResult, String> cell = new TableCell<InvoiceSearchResult, String>() {

							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null); // cleanup job as the cells
													// may be reused
								if (!empty) {
									this.setText(item);
									this.setTextAlignment(TextAlignment.CENTER);
								}
							}

						};
						cell.setAlignment(Pos.CENTER);
						return cell;
					}

				});

		invoiceAmountColumn.setCellFactory(
				new Callback<TableColumn<InvoiceSearchResult, BigDecimal>, TableCell<InvoiceSearchResult, BigDecimal>>() {

					@Override
					public TableCell<InvoiceSearchResult, BigDecimal> call(
							TableColumn<InvoiceSearchResult, BigDecimal> param) {
						TableCell<InvoiceSearchResult, BigDecimal> cell = new TableCell<InvoiceSearchResult, BigDecimal>() {

							@Override
							protected void updateItem(BigDecimal item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null); // cleanup job as the cells
													// may be reused
								if (!empty) {
									this.setText(IndianCurrencyFormatting.applyFormatting(item));
								}
							}

						};
						cell.getStyleClass().add("amount-cell");
						return cell;
					}

				});

		customerColumn.setCellFactory((TableColumn<InvoiceSearchResult, Customer> param) -> {
			final TableCell<InvoiceSearchResult, Customer> cell = new TableCell<InvoiceSearchResult, Customer>() {

				@Override
				protected void updateItem(Customer item, boolean empty) {
					super.updateItem(item, empty);

					setText(null);
					setGraphic(null);

					if (item != null && !empty) {
						setText(item.getName());
					}
				}

			};
			cell.getStyleClass().add("name-cell");
			return cell;
		});

	}

	private BigDecimal getTotalAmount(List<InvoiceSearchResult> list) {
		BigDecimal total = BigDecimal.ZERO;
		for (InvoiceSearchResult result : list) {
			total = total.add(result.getInvoiceAmount());
		}

		return total;
	}

	@FXML
	private void onCloseButtonAction(ActionEvent event) {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(tab); // close the current tab
	}

}
