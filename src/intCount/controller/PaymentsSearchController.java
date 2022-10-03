/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;

import intCount.database.CustomersPersistence;
import intCount.database.PaymentSearch;
import intCount.model.Customer;
import intCount.model.EntityEditedEvent;
import intCount.model.EntityEditedListener;
import intCount.model.Payment;
import intCount.model.PaymentMode;
import intCount.model.PaymentSearchCriteria;
import intCount.model.PaymentSearchResult;
import intCount.utility.IndianCurrencyFormatting;
import intCount.utility.TabContent;
import intCount.utility.Utility;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.controlsfx.control.CheckListView;
import java.util.logging.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class PaymentsSearchController implements TabContent, EntityEditedListener {

	private final static Logger logger = Logger.getLogger(PaymentsSearchController.class.getName());

	private Stage mainWindow;
	private TabPane tabPane;
	@SuppressWarnings("unused")
	private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
	private final IntegerProperty matchingPaymentsCount = new SimpleIntegerProperty(0);
	private Label[] errorLabels = null;
	private final LocalDate minPaymentDate = Global.getActiveFinancialYear().getStartDate();
	private final LocalDate maxPaymentDate = Utility.minDate(Global.getActiveFinancialYear().getEndDate(),
			LocalDate.now());
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private ObservableList<Customer> customersList = null;
	private FilteredList<Customer> filteredCustomerList = null;

	@FXML
	private TitledPane searchCriteriaPane;
	@FXML
	private CheckBox chkBetweenDates;
	@FXML
	private HBox paymentDatePane;
	@FXML
	private DatePicker dpStartDate;
	@FXML
	private DatePicker dpEndDate;
	@FXML
	private Label lblPaymentDateError;

	@FXML
	private CheckBox chkPaymentMode;
	@FXML
	private VBox paymentModeContainer;
	@FXML
	private CheckBox chkCash;
	@FXML
	private CheckBox chkCheque;
	@FXML
	private CheckBox chkDD;
	@FXML
	private CheckBox chkBankTransfer;
	@FXML
	private CheckBox chkRealisedPaymentsOnly;
	@FXML
	private Label lblPaymentModeError;

	@FXML
	private CheckBox chkCustomers;
	@FXML
	private VBox customersPane;
	@FXML
	private CheckListView<Customer> lvwCustomers;
	@FXML
	private TextField tfSearchCustomer;
	@FXML
	private Label lblCustomersError;
	@FXML
	private CheckBox selectAllCustomersButton;

	@FXML
	private Button btnSearchPayments;

	@FXML
	private TitledPane searchResultPane;

	@FXML
	private TableView<PaymentSearchResult> tableView;
	@FXML
	private TableColumn<PaymentSearchResult, LocalDate> paymentDateColumn;
	@FXML
	private TableColumn<PaymentSearchResult, Customer> customerColumn;
	@FXML
	private TableColumn<PaymentSearchResult, BigDecimal> amountColumn;
	@FXML
	private TableColumn<PaymentSearchResult, PaymentMode> paymentModeColumn;
	@FXML
	private TableColumn<PaymentSearchResult, String> instrumentRealisedColumn;

	@FXML
	private Label lblPaymentsSummary;
	@FXML
	Button btnEditPayment;
	@FXML
	Button btnDeletePayment;

	@FXML
	private Button btnClose;

	public void initialize() {
		paymentDatePane.visibleProperty().bind(chkBetweenDates.selectedProperty());
		paymentDatePane.managedProperty().bind(paymentDatePane.visibleProperty());

		paymentModeContainer.visibleProperty().bind(chkPaymentMode.selectedProperty());
		paymentModeContainer.managedProperty().bind(paymentModeContainer.visibleProperty());

		chkRealisedPaymentsOnly.managedProperty().bind(chkRealisedPaymentsOnly.visibleProperty());
		chkRealisedPaymentsOnly.visibleProperty().bind(chkCheque.selectedProperty().or(chkDD.selectedProperty()));

		customersPane.visibleProperty().bind(chkCustomers.selectedProperty());
		customersPane.managedProperty().bind(customersPane.visibleProperty());

		errorLabels = new Label[] { lblPaymentDateError, lblCustomersError, lblPaymentModeError };

		for (Label label : errorLabels) {
			label.managedProperty().bind(label.visibleProperty());
			label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
		}

		dpStartDate.setValue(LocalDate.now());
		dpStartDate.setConverter(Utility.getDateStringConverter());
		dpStartDate.setDayCellFactory(this::getDateCell);

		dpEndDate.setValue(LocalDate.now());
		dpEndDate.setConverter(Utility.getDateStringConverter());
		dpEndDate.setDayCellFactory(this::getDateCell);

		btnSearchPayments.disableProperty().bind(chkBetweenDates.selectedProperty()
				.or(chkPaymentMode.selectedProperty()).or(chkCustomers.selectedProperty()).not());

		setTableCellFactories();

		lblPaymentsSummary.managedProperty().bind(lblPaymentsSummary.visibleProperty());
		lblPaymentsSummary.visibleProperty().bind(lblPaymentsSummary.textProperty().isNotEmpty());

		btnEditPayment.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		btnDeletePayment.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

		searchResultPane.managedProperty().bind(searchResultPane.visibleProperty());
		searchResultPane.visibleProperty().bind(matchingPaymentsCount.greaterThan(0));

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
	}

	@Override
	public boolean shouldClose() {
		return true;
	}

	@Override
	public void putFocusOnNode() {
		chkBetweenDates.requestFocus();
	}

	@Override
	public boolean loadData() {
		return loadCustomers();
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
	private void onPaymentsSearchAction(ActionEvent event) {
		if (!validateCriteria()) {
			Utility.beep();
			return;
		}

		PaymentSearchCriteria criteria = getPaymentCriteria();
		List<PaymentSearchResult> results = null;

		try {
			results = PaymentSearch.searchPayments(criteria);
		} catch (Exception e) {
			Utility.beep();
			Alert alert = new Alert(Alert.AlertType.ERROR,
					"An error occurred " + "while looking for the corresponding payments", ButtonType.OK);
			alert.setHeaderText("Erreur occured");
			alert.setTitle("Error in search of payments");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		lblPaymentsSummary.textProperty().unbind();
		final ObservableList<PaymentSearchResult> listItems = FXCollections.<PaymentSearchResult>observableList(results,
				(PaymentSearchResult param) -> {
					Observable[] array = new Observable[] { param.amountProperty(), param.customerProperty(),
							param.paymentDateProperty(), param.paymentModeProperty(),
							param.realisationStatusProperty() };
					return array;
				});
		tableView.setItems(listItems);

		final StringBinding summaryBinding = new StringBinding() {
			{
				super.bind(listItems);
			}

			@Override
			protected String computeValue() {
				BigDecimal sum = BigDecimal.ZERO;
				for (PaymentSearchResult result : listItems) {
					sum = sum.add(result.getAmount());
				}
				String output = String.format("The total of %d payments is \\u20B9 %s", listItems.size(),
						IndianCurrencyFormatting.applyFormatting(sum));
				return output;
			}
		};

		lblPaymentsSummary.textProperty().bind(summaryBinding);
		int resultSize = results.size();
		matchingPaymentsCount.set(resultSize);

		if (resultSize > 0) {
			searchCriteriaPane.setExpanded(false);
			searchResultPane.setExpanded(true);
			tableView.getSelectionModel().selectFirst();
			tableView.scrollTo(0);
			tableView.requestFocus();
		} else {
			Utility.beep();
			Alert alert = new Alert(Alert.AlertType.INFORMATION,
					"No payments match your search criteria!", ButtonType.OK);
			alert.setTitle("No corresponding payment");
			alert.setHeaderText("No matching payment found");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
		}

	}

	@FXML
	private void onCloseTabAction(ActionEvent event) {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(tab); // close the current tab
	}

	private boolean validateCriteria() {
		boolean valid = true;

		clearErrorLabels();

		if (chkBetweenDates.isSelected()) {
			if (dpStartDate.getValue() == null || dpEndDate.getValue() == null) {
				lblPaymentDateError.setText("Start date or end date not specified!");
				valid = false;
			} else if (dpStartDate.getValue().isBefore(minPaymentDate)) {
				lblPaymentDateError
						.setText("Start date cannot be earlier than " + minPaymentDate.format(dateFormatter));
				valid = false;
			} else if (dpEndDate.getValue().isAfter(maxPaymentDate)) {
				lblPaymentDateError
						.setText("The end date cannot be later than " + maxPaymentDate.format(dateFormatter));
				valid = false;
			} else if (dpStartDate.getValue().isAfter(dpEndDate.getValue())) {
				lblPaymentDateError.setText("The start date cannot be later than the end date!");
				valid = false;
			}
		}

		if (chkCustomers.isSelected()) {
			if (lvwCustomers.getCheckModel().isEmpty()) {
				lblCustomersError.setText("No customer selected!");
				valid = false;
			}
		}

		if (chkPaymentMode.isSelected()) {
			if (!(chkCash.isSelected() || chkCheque.isSelected() || chkDD.isSelected()
					|| chkBankTransfer.isSelected())) {
				lblPaymentModeError.setText("Payment method not selected!");
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

	private DateCell getDateCell(DatePicker datePicker) {
		return Utility.getDateCell(datePicker, minPaymentDate, maxPaymentDate);
	}

	private boolean loadCustomers() {
		List<Customer> customers = null;

		try {
			customers = CustomersPersistence.getCustomers();
		} catch (Exception e) {
			String message = Utility.getDataFetchErrorText();
			Alert alert = Utility.getErrorAlert("Erreur occured", "Error retrieving clients",
					message, mainWindow);
			Utility.beep();
			alert.showAndWait();
			return false;
		}

		customersList = FXCollections.<Customer>observableList(customers);
		filteredCustomerList = new FilteredList<>(customersList, null);
		lvwCustomers.setItems(filteredCustomerList);

		if (customers.isEmpty()) {
			selectAllCustomersButton.setDisable(true);
		}
		return true;
	}

	private PaymentSearchCriteria getPaymentCriteria() {

		PaymentSearchCriteria criteria = new PaymentSearchCriteria();

		if (chkBetweenDates.isSelected()) {
			criteria.setStartDate(dpStartDate.getValue());
			criteria.setEndDate(dpEndDate.getValue());
		}

		if (chkCustomers.isSelected()) {
			List<Customer> customers = lvwCustomers.getCheckModel().getCheckedItems();
			criteria.setCustomers(customers);
		}

		if (chkPaymentMode.isSelected()) {
			List<PaymentMode> modes = new ArrayList<>(4);
			if (chkCash.isSelected()) {
				modes.add(PaymentMode.CASH);
			}
			if (chkCheque.isSelected()) {
				modes.add(PaymentMode.CHEQUE);
			}
			if (chkDD.isSelected()) {
				modes.add(PaymentMode.DD);
			}
			if (chkBankTransfer.isSelected()) {
				modes.add(PaymentMode.BANKTRANSFER);
			}

			criteria.setPaymentModes(modes);

			if (chkCheque.isSelected() || chkDD.isSelected() || chkCash.isSelected() || chkBankTransfer.isSelected()) {
				if (chkRealisedPaymentsOnly.isSelected()) {
					criteria.setRealisedPaymentsOnly(Boolean.TRUE);
				} else {
					criteria.setRealisedPaymentsOnly(Boolean.FALSE);
				}
			}

		}

		return criteria;
	}

	private void setTableCellFactories() {

		paymentDateColumn.setCellFactory((TableColumn<PaymentSearchResult, LocalDate> param) -> {
			TableCell<PaymentSearchResult, LocalDate> tableCell = new TableCell<PaymentSearchResult, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null); // cleanup job as the cells may be
										// reused
					if (!empty) {
						this.setText(dateFormatter.format(item));
						this.setTextAlignment(TextAlignment.CENTER);
						this.setAlignment(Pos.CENTER);
					}
				}

			};
			return tableCell;
		});

		amountColumn.setCellFactory((TableColumn<PaymentSearchResult, BigDecimal> param) -> {
			TableCell<PaymentSearchResult, BigDecimal> tableCell = new TableCell<PaymentSearchResult, BigDecimal>() {

				@Override
				protected void updateItem(BigDecimal item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					if (!empty) {
						this.setText(IndianCurrencyFormatting.applyFormatting(item));
						this.setTextAlignment(TextAlignment.RIGHT);
						this.setAlignment(Pos.TOP_RIGHT);
						this.setPadding(new Insets(0, 7.0, 0, 0));
					}
				}

			};
			return tableCell;
		});

		paymentModeColumn.setCellFactory(
				new Callback<TableColumn<PaymentSearchResult, PaymentMode>, TableCell<PaymentSearchResult, PaymentMode>>() {

					@Override
					public TableCell<PaymentSearchResult, PaymentMode> call(
							TableColumn<PaymentSearchResult, PaymentMode> param) {
						TableCell<PaymentSearchResult, PaymentMode> cell = new TableCell<PaymentSearchResult, PaymentMode>() {

							@Override
							protected void updateItem(PaymentMode item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null);
								if (!empty) {
									this.setText(item.toString());
									this.setTextAlignment(TextAlignment.CENTER);
									this.setAlignment(Pos.CENTER);
								}
							}

						};
						return cell;
					}
				});

		instrumentRealisedColumn.setCellFactory((TableColumn<PaymentSearchResult, String> param) -> {
			TableCell<PaymentSearchResult, String> cell = new TableCell<PaymentSearchResult, String>() {

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					if (!empty) {
						this.setText(item);
						this.setTextAlignment(TextAlignment.CENTER);
						this.setAlignment(Pos.CENTER);
					}
				}

			};
			return cell;
		});

		customerColumn.setCellFactory((TableColumn<PaymentSearchResult, Customer> param) -> {
			TableCell<PaymentSearchResult, Customer> cell = new TableCell<PaymentSearchResult, Customer>() {

				@Override
				protected void updateItem(Customer item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					if (!empty) {
						this.setText(item.getName());
						this.setTextAlignment(TextAlignment.LEFT);
						this.setPadding(new Insets(0, 0, 0, 7.0));
					}
				}

			};
			return cell;
		});

	} // end of function definition

	@FXML
	private void onDeletePaymentAction(ActionEvent event) {
		PaymentSearchResult item = tableView.getSelectionModel().getSelectedItem();
		if (!shouldDeletePayment(item)) {
			return;
		}

		int paymentId = item.getPaymentId();
		try {
			PaymentSearch.deletePayment(paymentId);
		} catch (Exception e) {
			String message = "An error occurred while deleting the payment from the database..";
			Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.setTitle("Erreur occured");
			alert.setHeaderText("Error deleting payment");
			alert.initOwner(mainWindow);
			Utility.beep();
			alert.showAndWait();
			return;
		}

		@SuppressWarnings("unused")
		boolean removed = tableView.getItems().remove(item);
		tableView.requestFocus();

	}

	private boolean shouldDeletePayment(PaymentSearchResult item) {
		LocalDate date = item.getPaymentDate();
		BigDecimal amount = item.getAmount();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

		String message = "Are you sure you want to delete the payment of the " + date.format(formatter)
				+ " and amount of" +"  "+ "Dh" + IndianCurrencyFormatting.applyFormatting(amount) + " ?";
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		alert.setTitle("Sure to delete?");
		alert.setHeaderText("Make sure to delete this payment?");
		alert.initOwner(mainWindow);
		Optional<ButtonType> response = alert.showAndWait();

		if (!response.isPresent()) {
			return false;
		}

		ButtonType buttonType = response.get();
		return (buttonType == ButtonType.YES ? true : false);
	}

	@FXML
	private void onEditPaymentAction(ActionEvent event) {
		// get the selected payment
		PaymentSearchResult searchResult = tableView.getSelectionModel().getSelectedItem();
		int paymentId = searchResult.getPaymentId();

		String path = "/intCount/view/Payment.fxml";
		FXMLLoader loader = new FXMLLoader();
		URL resource = this.getClass().getResource(path);
		loader.setLocation(resource);
		Parent rootPane = null;

		try {
			rootPane = loader.load();
		} catch (Exception e) {
			logger.logp(Level.SEVERE, this.getClass().getName(), "onEditPaymentAction",
					"Error loading payment fxml file", e);
			Alert alert = Utility.getErrorAlert("Erreur occured",
					"An error occurred while opening the payment",
					"Unable to open payment due to an error.", mainWindow);
			Utility.beep();
			alert.showAndWait();
			return;
		}

		final PaymentController controller = (PaymentController) loader.getController();
		controller.setMainWindow(mainWindow);
		if (!controller.loadData()) {
			return;
		}

		if (!controller.loadPayment(paymentId)) {
			return;
		}

		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(mainWindow);
		stage.setOnCloseRequest((WindowEvent event1) -> {
			if (!controller.shouldClose()) {
				event1.consume();
			}
		});

		stage.setTitle("Changing payment #" + paymentId);
		stage.getIcons().add(new Image("/resources/images/billing_32.png"));
		controller.setMainWindow(stage);
		controller.addEntityEditedEventListener(this);

		Global.setStageDefaultDimensions(stage);

		Scene scene = new Scene(rootPane);
		stage.setScene(scene);
		stage.showAndWait();
	}

	@Override
	public void onEntityEdited(EntityEditedEvent event) {
		Object entityEdited = event.getEntityEdited();
		Payment payment = (Payment) entityEdited;

		PaymentSearchResult item = tableView.getSelectionModel().getSelectedItem();

		item.setCustomer(payment.getCustomer());
		item.setAmount(payment.getAmount());
		item.setPaymentDate(payment.getPaymentDate());
		PaymentMode mode = payment.getPaymentMode();
		item.setPaymentMode(mode);

		if (mode == PaymentMode.CHEQUE || mode == PaymentMode.DD || mode == PaymentMode.CASH || mode== PaymentMode.BANKTRANSFER) {
			item.setPaymentRealised(payment.getInstrumentRealised());
		} else {
			item.setPaymentRealised(null);
		}
	}

	@FXML
	private void onSelectAllCustomersAction(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof CheckBox) {
			CheckBox checkBox = (CheckBox) source;
			if (checkBox.isSelected()) {
				lvwCustomers.getCheckModel().checkAll();
				checkBox.getTooltip().setText("Unchecking this box will deselect all clients");
			} else {
				lvwCustomers.getCheckModel().clearChecks();
				checkBox.getTooltip().setText("Check this box to select all customers.");
			}
		}
	}

} // end of class definition
