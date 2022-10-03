/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;






import intCount.database.AccountLookup;
import intCount.database.CustomersPersistence;
import intCount.database.InvoiceSearch;
import intCount.database.PaymentSearch;
import intCount.model.AccountEntry;
import intCount.model.AccountLookupCriteria;
import intCount.model.Customer;
import intCount.model.EntityEditedEvent;
import intCount.model.EntityEditedListener;
import intCount.model.Invoice;
import intCount.model.InvoiceItem;
import intCount.model.Payment;
import intCount.utility.IndianCurrencyFormatting;
import intCount.utility.TabContent;
import intCount.utility.Utility;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import java.util.logging.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class CustomerAccountController implements TabContent, EntityEditedListener {

	private static final Logger logger = Logger.getLogger(CustomerAccountController.class.getName());

	// @FXML
	// private ComboBox<Customer> cbCustomers;
	@FXML
	private TextField tfCustomer;
	@FXML
	private Label lblCustomersError;
	@FXML
	private Label lblEntriesSinceError;
	@FXML
	private CheckBox chkEntriesSince;
	@FXML
	private DatePicker dpEntriesSince;
	@FXML
	private Button btnShowEntries;
	@FXML
	private TitledPane entriesPane;
	@FXML
	private TitledPane titledPane;
	@FXML
	private TableView<AccountEntry> tableView;
	@FXML
	private TableColumn<AccountEntry, LocalDate> dateColumn;
	@FXML
	private TableColumn<AccountEntry, String> descriptionColumn;
	@FXML
	private TableColumn<AccountEntry, BigDecimal> debitAmountColumn;
	@FXML
	private TableColumn<AccountEntry, BigDecimal> creditAmountColumn;
	@FXML
	private TextField tfDebitTotal;
	@FXML
	private TextField tfCreditTotal;
	@FXML
	private TextField tfAccountBalance;
	@FXML
	private Button btnEditEntry;
	@FXML
	private Button btnDeleteEntry;
	@FXML
	private Button btnPrintAccount;
	@FXML
	private ScrollPane scrollPane;

	private Stage mainWindow;
	private TabPane tabPane;
	private Label[] errorLabels = null;
	private final IntegerProperty entriesCount = new SimpleIntegerProperty(0);
	private final LocalDate maxDate = LocalDate.now();
	private final LocalDate minDate = Global.getActiveFinancialYear().getStartDate();
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private List<Customer> customers = null;

	
	public void initialize() {
		errorLabels = new Label[] { lblCustomersError, lblEntriesSinceError };
		for (Label label : errorLabels) {
			label.managedProperty().bind(label.visibleProperty());
			label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
		}

		dpEntriesSince.disableProperty().bind(chkEntriesSince.selectedProperty().not());
		btnShowEntries.disableProperty().bind(tfCustomer.textProperty().length().isEqualTo(0));

		scrollPane.managedProperty().bind(scrollPane.visibleProperty());
		scrollPane.visibleProperty().bind(entriesCount.greaterThan(0));

		dpEntriesSince.setValue(LocalDate.now());
		dpEntriesSince.setConverter(Utility.getDateStringConverter());
		dpEntriesSince.setDayCellFactory(this::getDateCell);

		setTableCellFactories();
		btnDeleteEntry.disableProperty().bind(btnEditEntry.disableProperty());

		tableView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends AccountEntry> observable, AccountEntry oldValue, AccountEntry newValue) -> {
					boolean disable = false;
					String suffix = "";

					if (newValue == null) {
						disable = true;
					} else if (newValue.getInvoiceId() != 0) {
						suffix = " Invoice";
					} else if (newValue.getPaymentId() != 0) {
						suffix = " Payment";
					} else {
						disable = true;
					}

					btnEditEntry.setDisable(disable);
					btnEditEntry.setText("Edit" + suffix);
					btnDeleteEntry.setText("Delete" + suffix);
				});

	}

	@Override
	public boolean shouldClose() {
		return true;
	}

	@Override
	public void putFocusOnNode() {
		tfCustomer.requestFocus();
		// cbCustomers.requestFocus();
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
	private void onEntriesLookupAction(ActionEvent event) {
		if (!validateInput()) {
			Utility.beep();
			return;
		}

		AccountLookupCriteria criteria = getLookupCriteria();
		List<AccountEntry> entries = null;

		try {
			entries = AccountLookup.getAccountEntries(criteria);
		} catch (Exception e) {
			final String message = "An error occurred while trying to retrieve clients "
					+ "database account details!";
			Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.setTitle("Error Occurred");
			alert.setHeaderText("Error while retrieving data");
			alert.initOwner(mainWindow);
			Utility.beep();
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		final ObservableList<AccountEntry> observableList = FXCollections.<AccountEntry>observableList(entries,
				(AccountEntry param) -> {
					Observable[] array = new Observable[] { param.entryDateProperty(), param.entryDescriptionProperty(),
							param.debitAmountProperty(), param.creditAmountProperty() };
					return array;
				});

		tfDebitTotal.textProperty().bind(getDebitBinding(observableList));
		tfCreditTotal.textProperty().bind(getCreditBinding(observableList));
		tfAccountBalance.textProperty().bind(getAccountBalanceBinding(observableList));

		tableView.setItems(observableList);
		entriesCount.set(entries.size());
		titledPane.setExpanded(false);
		entriesPane.setExpanded(true);
		tableView.getSelectionModel().selectFirst();
		tableView.scrollTo(0);
		tableView.requestFocus();
	}

	private boolean validateInput() {
		clearErrorLabels();

		boolean valid = true;

		final String text = tfCustomer.getText();
		if (text == null || text.trim().isEmpty()) {
			lblCustomersError.setText("Customer not specified !");
			valid = false;
		} else { // check whether it is a valid customer or not
			Optional<Customer> findFirst = customers.stream().filter((Customer t) -> t.getName().equalsIgnoreCase(text))
					.findFirst();
			if (!findFirst.isPresent()) {
				lblCustomersError.setText("No customer matches this name !");
				valid = false;
			} else {
				tfCustomer.setUserData(findFirst.get());
			}
		}

		if (chkEntriesSince.isSelected()) {
			LocalDate entrySince = dpEntriesSince.getValue();
			if (entrySince == null) {
				lblEntriesSinceError.setText("Date not specified!");
				valid = false;
			} else if (entrySince.isBefore(minDate)) {
				lblEntriesSinceError
						.setText("The selected date cannot be earlier than" + minDate.format(dateTimeFormatter));
				valid = false;
			} else if (entrySince.isAfter(maxDate)) {
				lblEntriesSinceError.setText(
						"The selected date cannot be later than " + maxDate.format(dateTimeFormatter));
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
		return Utility.getDateCell(datePicker, minDate, maxDate);
	}

	private AccountLookupCriteria getLookupCriteria() {
		AccountLookupCriteria criteria = new AccountLookupCriteria();
		criteria.setCustomer((Customer) tfCustomer.getUserData());
		//criteria.setInvoiceType("'Devis'");

		if (chkEntriesSince.isSelected()) {
			criteria.setEntriesSince(dpEntriesSince.getValue());
		}

		return criteria;
	}

	private void setTableCellFactories() {
		dateColumn.setCellFactory(
				new Callback<TableColumn<AccountEntry, LocalDate>, TableCell<AccountEntry, LocalDate>>() {

					@Override
					public TableCell<AccountEntry, LocalDate> call(TableColumn<AccountEntry, LocalDate> param) {
						TableCell<AccountEntry, LocalDate> cell = new TableCell<AccountEntry, LocalDate>() {

							@Override
							protected void updateItem(LocalDate item, boolean empty) {
								super.updateItem(item, empty);
								this.setText(null);
								if (item != null) {
									this.setText(item.format(dateTimeFormatter));
									this.setAlignment(Pos.CENTER);
								}
							}

						};
						return cell;
					}
				});

		descriptionColumn.setCellFactory((TableColumn<AccountEntry, String> param) -> {
			TableCell<AccountEntry, String> cell = new TableCell<AccountEntry, String>() {

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);// for empty rows
					if (!empty) {
						this.setText(item);
						this.setAlignment(Pos.CENTER_LEFT);
					}
				}

			};
			cell.getStyleClass().add("descriptionCell");
			return cell;
		});

		debitAmountColumn.setCellFactory((TableColumn<AccountEntry, BigDecimal> param) -> {
			TableCell<AccountEntry, BigDecimal> cell = new TableCell<AccountEntry, BigDecimal>() {

				@Override
				protected void updateItem(BigDecimal item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					if (item != null) {
						this.setText(IndianCurrencyFormatting.applyFormatting(item));
						this.setAlignment(Pos.CENTER_RIGHT);
					}
				}

			};
			cell.getStyleClass().add("amountCell");
			return cell;
		});

		creditAmountColumn.setCellFactory((TableColumn<AccountEntry, BigDecimal> param) -> {
			TableCell<AccountEntry, BigDecimal> cell = new TableCell<AccountEntry, BigDecimal>() {

				@Override
				protected void updateItem(BigDecimal item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					if (item != null) {
						this.setText(IndianCurrencyFormatting.applyFormatting(item));
						this.setAlignment(Pos.CENTER_RIGHT);
					}
				}

			};
			cell.getStyleClass().add("amountCell");
			return cell;
		});
	}

	private boolean loadCustomers() {
		// List<Customer> customers = null;

		try {
			customers = CustomersPersistence.getCustomers();
		} catch (Exception e) {
			String message = Utility.getDataFetchErrorText();
			Alert alert = Utility.getErrorAlert("Error Occurred",
					"Error extracting customer data !", message, mainWindow);
			Utility.beep();
			alert.showAndWait();
			return false;
		}

		// final ObservableList<Customer> customerList
		// = FXCollections.<Customer>observableList(customers);
		// cbCustomers.setItems(customerList);
		TextFields.<Customer>bindAutoCompletion(tfCustomer, customers);
		return true;
	}

	private StringBinding getDebitBinding(final ObservableList<AccountEntry> list) {
		final StringBinding debitBinding = new StringBinding() {

			{
				super.bind(list);
			}

			@Override
			protected String computeValue() {
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal amount = null;
				for (AccountEntry entry : list) {
					amount = entry.getDebitAmount();
					if (amount != null) {
						total = total.add(amount);
					}
				}
				return IndianCurrencyFormatting.applyFormatting(total);
			}
		};
		return debitBinding;
	}

	private StringBinding getCreditBinding(final ObservableList<AccountEntry> list) {
		final StringBinding creditBinding = new StringBinding() {

			{
				super.bind(list);
			}

			@Override
			protected String computeValue() {
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal amount = null;
				for (AccountEntry entry : list) {
					amount = entry.getCreditAmount();
					if (amount != null) {
						total = total.add(amount);
					}
				}
				return IndianCurrencyFormatting.applyFormatting(total);
			}
		};
		return creditBinding;
	}

	private StringBinding getAccountBalanceBinding(final ObservableList<AccountEntry> list) {
		final StringBinding binding = new StringBinding() {

			{
				super.bind(list);
			}

			@Override
			protected String computeValue() {
				BigDecimal debitTotal = BigDecimal.ZERO;
				BigDecimal creditTotal = BigDecimal.ZERO;
				BigDecimal amount = null;

				for (AccountEntry entry : list) {
					amount = entry.getDebitAmount();
					if (amount != null) {
						debitTotal = debitTotal.add(amount);
					}
					amount = entry.getCreditAmount();
					if (amount != null) {
						creditTotal = creditTotal.add(amount);
					}
				}

				BigDecimal balance = creditTotal.subtract(debitTotal);
				String suffix = "";
				if (balance.signum() == 1) {
					suffix = " CR.";
				} else if (balance.signum() == -1) {
					suffix = " DR.";
				}

				return (IndianCurrencyFormatting.applyFormatting(balance.abs()) + suffix);
			}
		};

		return binding;
	}

	@Override
	public void onEntityEdited(EntityEditedEvent event) {
		Object entityEdited = event.getEntityEdited();
		AccountEntry entry = tableView.getSelectionModel().getSelectedItem();

		if (entityEdited instanceof Invoice) {
			Invoice invoice = (Invoice) entityEdited;
			entry.setEntryDate(invoice.getInvoiceDate());
			entry.setDebitAmount(getInvoiceTotal(invoice));
		} else if (entityEdited instanceof Payment) {
			Payment payment = (Payment) entityEdited;
			entry.setEntryDate(payment.getPaymentDate());
			entry.setEntryDescription(payment.getPaymentMode().toString() + " Payment");
			entry.setCreditAmount(payment.getAmount());
		}
	}

	private BigDecimal getInvoiceTotal(Invoice invoice) {
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal amount = null;
		List<InvoiceItem> invoiceItems = invoice.getInvoiceItems();
		for (InvoiceItem item : invoiceItems) {
			amount = item.getQuantity().multiply(item.getRate()).setScale(2, RoundingMode.HALF_UP);
			total = total.add(amount);
		}

		if (invoice.getAdditionalCharge() != null) {
			total = total.add(invoice.getAdditionalCharge());
		}

		if (invoice.getDiscount() != null) {
			total = total.subtract(invoice.getDiscount());
		}
		return total;
	}

	@FXML
	private void onEditEntryAction(ActionEvent event) {
		AccountEntry entry = tableView.getSelectionModel().getSelectedItem();
		int paymentId = entry.getPaymentId();
		int invoiceId = entry.getInvoiceId();

		if (paymentId == 0 && invoiceId == 0) {
			return;
		}

		String path = "/intCount/view/";
		if (paymentId != 0) {
			path += "Payment.fxml";
		} else {
			path += "Invoice.fxml";
		}

		FXMLLoader loader = new FXMLLoader();
		URL resource = this.getClass().getResource(path);
		loader.setLocation(resource);
		Parent rootPane = null;

		try {
			rootPane = loader.load();
		} catch (Exception e) {
			final String text = (paymentId != 0 ? "payment" : "invoice");
			e.printStackTrace(System.err);
			Alert alert = Utility.getErrorAlert("Error Occurred",
					"An error occurred while opening the " + text,
					"Unable to open the " + text + " due to an error.", mainWindow);
			Utility.beep();
			alert.showAndWait();
			return;
		}

		TabContent controller = loader.getController();
		controller.setMainWindow(mainWindow);
		if (!controller.loadData()) {
			return;
		}

		if (paymentId != 0) {
			PaymentController paymentController = (PaymentController) controller;
			if (!paymentController.loadPayment(paymentId)) {
				return;
			}
			paymentController.addEntityEditedEventListener(this);
		} else {
			InvoiceController invoiceController = (InvoiceController) controller;
			if (!invoiceController.loadInvoice(String.valueOf(invoiceId))) {
				return;
			}
		}

		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(mainWindow);
		stage.setOnCloseRequest((WindowEvent event1) -> {
			if (!controller.shouldClose()) {
				event1.consume();
			}
		});

		if (paymentId != 0) {
			stage.setTitle("Editing Payment #" + paymentId);
		} else {
			stage.setTitle("Editing Invoice #" + invoiceId);
		}

		stage.getIcons().add(new Image("/resources/images/billing_32.png"));
		controller.setMainWindow(stage);

		Global.setStageDefaultDimensions(stage);

		Scene scene = new Scene(rootPane);
		stage.setScene(scene);
		stage.showAndWait();

	}

	@FXML
	private void onDeleteEntryAction(ActionEvent event) {
		AccountEntry entry = tableView.getSelectionModel().getSelectedItem();
		int paymentId = entry.getPaymentId();
		int invoiceId = entry.getInvoiceId();

		if (paymentId == 0 && invoiceId == 0) {
			return;
		}

		String message = null;
		String header = null;
		if (invoiceId != 0) {
			message = "Are you sure you want to delete this numbered invoice? " + invoiceId + " et daté "
					+ entry.getEntryDate().format(dateTimeFormatter) + " ?";
			header = "Confirm invoice deletion";
		} else {
			message = "Are you sure you want to delete this payment dated "
					+ entry.getEntryDate().format(dateTimeFormatter) + " and rising to DH"
					+ IndianCurrencyFormatting.applyFormatting(entry.getCreditAmount()) + " ?";
			header = "Confirm deletion of payment";
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		alert.setTitle("Confirm deletion!");
		alert.setHeaderText(header);
		alert.initOwner(mainWindow);
		Global.styleAlertDialog(alert);
		Optional<ButtonType> response = alert.showAndWait();
		if (!response.isPresent()) { // user cancelled the dialog box
			return;
		}

		try {
			if (invoiceId != 0) {
				InvoiceSearch.deleteInvoice(invoiceId);
			} else {
				PaymentSearch.deletePayment(paymentId);
			}
		} catch (Exception e) {
			String content = "An error occurred while deleting the "
					+ (invoiceId != 0 ? "invoice" : "payment") + ".";
			Alert dialog = Utility.getErrorAlert("Erreur occured", "Delete erreur", content, mainWindow);
			Utility.beep();
			dialog.showAndWait();
			return;
		}

		tableView.getItems().remove(entry);
	}

	@FXML
	private void onPrintAccountAction(ActionEvent event) {
	
		Map<String, Object> map = new HashMap<String, Object>(1);
		Customer customer = (Customer) tfCustomer.getUserData();

		String customerName = customer.getName();
		if (customerName != null) {
			map.put("customerName", customerName);
		}
		String city = customer.getCity();
		if (city != null) {
			map.put("customerAdresse", city);
		}
		
		String taxeProfessionnel = customer.getTaxeProfessionnel();
		if (taxeProfessionnel != null) {
			map.put("customerTaxeProfessionnel", taxeProfessionnel);
		}
		
		List<AccountEntry> entries = tableView.getItems();

		final String resourcePath = "/resources/reports/intCount_customer_account.jasper";
		JasperPrint jasperPrint = null;

		try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePath)) {
			jasperPrint = JasperFillManager.fillReport(reportStream, map,
					new JRBeanCollectionDataSource(entries, false));
			File client = new File(
					System.getProperty("user.dir").concat("/intCount").concat("/Client/").concat(tfCustomer.getText()));
			if (!client.exists()) {
				client.mkdirs();
			}else

			JasperExportManager.exportReportToPdfFile(jasperPrint, System.getProperty("user.dir").concat("/intCount")
					.concat("/client/").concat(tfCustomer.getText()).concat("/") + "ReleveDeCompte" + ".pdf");
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CustomerAccountController.class.getName(), "onPrintAccountAction",
					"Error in call to fillReport function", e);
			Utility.beep();
			final String message = "An error occurred while preparing to print the customer's account.!";
			final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			alert.setTitle("Error Occurred");
			alert.setHeaderText("Account statement printing error");
			alert.initOwner(mainWindow);
			Global.styleAlertDialog(alert);
			alert.showAndWait();
			return;
		}

		JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
		jasperViewer.setTitle("Invoice");
		jasperViewer.setVisible(true);
		
	}

	@FXML
	private void onCloseButtonAction(ActionEvent event) {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(tab); // close the current tab
	}

}
