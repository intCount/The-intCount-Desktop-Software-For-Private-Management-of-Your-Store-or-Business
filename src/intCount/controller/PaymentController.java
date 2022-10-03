/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import intCount.Global;



import intCount.database.CustomersPersistence;
import intCount.database.PaymentPersistence;
import intCount.model.Customer;
import intCount.model.EntityEditedEvent;
import intCount.model.EntityEditedListener;
import intCount.model.Payment;
import intCount.model.PaymentMode;
import intCount.utility.IndianCurrencyFormatting;
import intCount.utility.TabContent;
import intCount.utility.Utility;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.controlsfx.control.textfield.TextFields;

import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public class PaymentController implements TabContent{
    
    @FXML private TextField tfCustomer;
    @FXML private GridPane gridPane;
    @FXML private ToggleGroup paymentModeToggle;
    @FXML private GridPane instrumentDetailsPane;
    @FXML private RadioButton rdCheque;
    @FXML private RadioButton rdDemandDraft;
    @FXML private RadioButton rdCash;
    @FXML private RadioButton rdBankTransfer;
    @FXML private TilePane tilePane;
    @FXML private Label lblCustomersError;
    @FXML private Label lblPaymentDateError;
    @FXML private Label lblAmountError;
    @FXML private Label lblPaymentModeError;
    @FXML private Label lblInstrumentNumberError;
    @FXML private Label lblInstrumentDateError;
    @FXML private Label lblInstrumentRealisedError;
    @FXML private Label lblInstrumentDrawnAtError;
    @FXML private Button btnRegisterPayment;
    @FXML private DatePicker dpPaymentDate;
    @FXML private TextField tfAmount;
    @FXML private TextField tfInstrumentNumber;
    @FXML private DatePicker dpInstrumentDate;
    @FXML private ToggleGroup paymentReceivedToggle;
    @FXML private TextField tfInstrumentDrawnAt;
    @FXML private RadioButton rdRealised;
    @FXML private RadioButton rdNotRealised;
    @FXML private TextField tfCustomerBalance;
    @FXML private ImageView checkImage;
    
    private Stage mainWindow;
    private Label[] errorLabels = null;
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private final LocalDate maxPaymentDate = Utility.minDate(LocalDate.now(), 
            Global.getActiveFinancialYear().getEndDate());
    private final LocalDate minPaymentDate = Global.getActiveFinancialYear().getStartDate();
    
    private final LocalDate minInstrumentDate = LocalDate.MIN;
    private final LocalDate maxInstrurmentDate = maxPaymentDate.plusMonths(1);
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private TabPane tabPane = null;
    private int paymentId = 0;
    private ArrayList<EntityEditedListener> eventListeners = null;
    private List<Customer> customers = null;
    
    public void initialize() {
        
        gridPane.setPadding(new Insets(20.0, 0, 20.0, 0));
        paymentModeToggle.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, 
                Toggle oldValue, Toggle newValue) -> {
            boolean visible = false;
            if (newValue != null && (newValue.equals(rdCash) ||
                    newValue.equals(rdCheque) ||
                    newValue.equals(rdDemandDraft) ||
                    newValue.equals(rdBankTransfer) )) {
                visible = true;
            }
            
            instrumentDetailsPane.setManaged(visible);
            instrumentDetailsPane.setVisible(visible);
            
            isDirty.set(true);
        });
        
        tilePane.setPadding(new Insets(25.0, 0, 0, 0));
        
        rdCash.setUserData(PaymentMode.CASH);
        rdCheque.setUserData(PaymentMode.CHEQUE);
        rdDemandDraft.setUserData(PaymentMode.DD);
        rdBankTransfer.setUserData(PaymentMode.BANKTRANSFER);
        
        errorLabels = new Label[] {
            lblCustomersError, lblPaymentDateError, lblAmountError,
            lblPaymentModeError, lblInstrumentNumberError,
            lblInstrumentDateError, lblInstrumentRealisedError,
            lblInstrumentDrawnAtError
        };
        
        //ensure that error labels are only visible when error text is assigned to them.
        for(Label label : errorLabels) {
            label.managedProperty().bind(label.visibleProperty());
            label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
        }
        
        btnRegisterPayment.disableProperty().bind(isDirty.not());
        
       if (! Global.getActiveFinancialYear().getEndDate().isBefore(LocalDate.now())) {
           dpPaymentDate.setValue(LocalDate.now());
       }
        dpPaymentDate.setConverter(Utility.getDateStringConverter());
        dpPaymentDate.setDayCellFactory(this::getAmountDateCell);
        
        dpInstrumentDate.setConverter(Utility.getDateStringConverter());
        dpInstrumentDate.setDayCellFactory(this::getInstrumentDateCell);
        
        instrumentDetailsPane.setPadding(new Insets(0,0,0,20.0));
        
        tfCustomer.textProperty().addListener((Observable observable) -> {
            isDirty.set(true);
            tfCustomerBalance.clear();
        });
        
        tfCustomer.focusedProperty().addListener((ObservableValue<? extends Boolean> observable,
                Boolean oldValue, Boolean newValue) -> {
            if (!newValue) { //if lost focus
                setCustomerBalance(getCustomer());
            }
        });
        
        tfCustomer.focusTraversableProperty().bind(tfCustomer.editableProperty());
        
        dpPaymentDate.valueProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        tfAmount.textProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                isDirty.set(true);
            }
        });
        
        tfInstrumentNumber.textProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        dpInstrumentDate.valueProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        paymentReceivedToggle.selectedToggleProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        checkImage.managedProperty().bind(checkImage.visibleProperty());
        checkImage.visibleProperty().bind(checkImage.opacityProperty()
                .greaterThan(0.0));
        
    }
    
    private void clearErrorLabels() {
        for(Label label : errorLabels) {
            label.setText("");
        }
    }

    @Override
    public boolean shouldClose() {
        if (isDirty.get()) {
            ButtonType response = shouldSaveUnsavedData();
            if (response == ButtonType.CANCEL) {
                return false;
            }

            if (response == ButtonType.YES) {
                return savePayment();
            }

        }

        return true;
    }

    @Override
    public void putFocusOnNode() {
        if (tfCustomer.isEditable()) {
            tfCustomer.requestFocus();
        } else {
            tfAmount.requestFocus();
        }
    }

    @Override
    public boolean loadData() {
        return loadCustomers();
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }
    
     private DateCell getAmountDateCell(DatePicker datePicker) {
        return Utility.getDateCell(datePicker, minPaymentDate, maxPaymentDate);
    }
     
    private DateCell getInstrumentDateCell(DatePicker datePicker) {
        
        return Utility.getDateCell(datePicker, minInstrumentDate, maxInstrurmentDate);
    }
     
      private boolean loadCustomers() {

        try {
            customers = CustomersPersistence.getCustomers();
        } catch (Exception e) {
            String message = Utility.getDataFetchErrorText();
            Alert alert = Utility.getErrorAlert("Erreur occured",
                    "Error retrieving clients", message, mainWindow);
            Utility.beep();
            alert.showAndWait();
            return false;
        }

        TextFields.<Customer>bindAutoCompletion(tfCustomer, customers);
        return true;
    }
      
    private boolean validateInput() {
        clearErrorLabels();
        
        boolean valid = true;
        
        String text = tfCustomer.getText();
        if (text == null || text.trim().isEmpty()) {
            lblCustomersError.setText("Customer not specified !");
            valid = false;
        } else {
            Customer customer = getCustomer();
            if (customer == null) {
                lblCustomersError.setText("No customer matches this name !");
                valid = false;
            } else {
                tfCustomer.setUserData(customer);
            }
        }
        
        if (dpPaymentDate.getValue() == null) {
            lblPaymentDateError.setText("Payment date not specified!");
            valid = false;
        } else {
            final LocalDate date = dpPaymentDate.getValue();
            if (date.isBefore(minPaymentDate)) {
                valid = false;
                lblPaymentDateError.setText("The date cannot be before " + 
                        minPaymentDate.format(dateFormatter));
            } else if (date.isAfter(maxPaymentDate)) {
                valid = false;
                lblPaymentDateError.setText("The date cannot be later than  " + 
                       maxPaymentDate.format(dateFormatter) );
            }
        }
        
        if (paymentModeToggle.selectedToggleProperty().isNull().get()) {
            lblPaymentModeError.setText("Payment mode not selected!");
            valid = false;
        }
        
        String amountString = tfAmount.getText().trim();
        if (!amountString.isEmpty()) {
            BigDecimal amount = null;
            try {
                amount = new BigDecimal(amountString);
            } catch (Exception e) {
                valid = false;
                lblAmountError.setText("Not a number!");
            }

            if (amount != null) {
                if (amount.signum() == -1) {
                    valid = false;
                    lblAmountError.setText("Negative amount!");
                } else if (amount.signum() == 0) {
                    valid = false;
                    lblAmountError.setText("Must be greater than zero!");
                }
            }
        } else {
            lblAmountError.setText("Amount not specified!");
            valid = false;
        }
        
        if (rdCheque.isSelected() || rdDemandDraft.isSelected() || rdCash.isSelected() || rdBankTransfer.isSelected()) {
            boolean result = validateInstrumentDetails();
            if (valid) {valid = result;}
        }
        
        return valid;
    }
    
    private boolean validateInstrumentDetails() {
        boolean valid = true;
        
        String instrumentNumber = tfInstrumentNumber.getText().trim();
        if (instrumentNumber.isEmpty()) {
            lblInstrumentNumberError.setText("Instrument number not specified!");
            valid = false;
        } else if (instrumentNumber.length() > 15) {
            valid = false;
            lblInstrumentNumberError.setText("The length cannot exceed 15 characters!");
        }
        
        if (dpInstrumentDate.getValue() == null) {
            lblInstrumentDateError.setText("Instrument date not specified!");
            valid = false;
        } else {
            final LocalDate date = dpInstrumentDate.getValue();
            if (date.isBefore(minInstrumentDate)) {
                valid = false;
                lblInstrumentDateError.setText("The date cannot be before  " + 
                     minInstrumentDate.format(dateFormatter));
            } else if (date.isAfter(maxInstrurmentDate)) {
                valid = false;
                lblInstrumentDateError.setText("The date cannot be later than  " +
                      maxInstrurmentDate.format(dateFormatter));
            } else {
                final LocalDate paymentDate = dpPaymentDate.getValue();
                if (paymentDate != null) {
                    final LocalDate minValidDate = paymentDate.minusMonths(3).plusDays(1);
                    if (date.isBefore(minValidDate)) {
                        valid = false;
                        lblInstrumentDateError.setText("The date cannot be before " + 
                             minValidDate.format(dateFormatter));
                    }
                }
            }
        }
        
        String drawnAt = tfInstrumentDrawnAt.getText().trim();
        if (drawnAt.length() > 100) {
            valid = false;
            lblInstrumentDrawnAtError.setText("The length cannot exceed 100 characters!");
        }
        
        if(paymentReceivedToggle.getSelectedToggle() == null) {
            lblInstrumentRealisedError.setText("Yes' or 'no' not selected!");
            valid = false;
        }
        
        return valid;
        
        
    }
    
    @FXML
    private void onRegisterPaymentAction(ActionEvent event) {
       
        if (!savePayment()) {
            return;
        }
        
        if (this.paymentId != 0) { //updated the payment
            mainWindow.close();
            return;
        }
        
        clearControls();
        isDirty.set(false);
        
        final FadeTransition transition = new FadeTransition(Duration.seconds(3.0),
                checkImage);
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        transition.setOnFinished((ActionEvent event1) -> {
            tfCustomer.requestFocus();
        });
        
        try {
            transition.play();
        } catch (Exception e) {
           // do nothing
        }
        
       
    }
    
    private boolean savePayment() {
         if (!validateInput()) {
            Utility.beep();
            return false;
        }
        
        Payment payment = gatherUserInput();
        try {
            PaymentPersistence.savePayment(payment);
        } catch (Exception e) {
            String message = "An error occurred while trying to save the payment to the database.!";
            Alert alert = new Alert(Alert.AlertType.ERROR, message,
                    ButtonType.OK);
            alert.setTitle("Erreur occured");
            alert.setHeaderText("Error saving payment");
            alert.initOwner(mainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        if (this.paymentId != 0) {
            fireEntityEditedEvent(payment);
        }
        
        isDirty.set(false);
        return true;
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        if (shouldClose()) {
            if (paymentId != 0) { //updating the payment
                mainWindow.close();
            } else {
                closeTab();
            }
        }
        
    }
    
    private void closeTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(tab); //close the current tab
    }
    
   /**
    * Creates and returns a Payment object populated with the data supplied by the user.
    * @return 
    */
    private Payment gatherUserInput() {
        Payment payment = new Payment();
        if (paymentId != 0) {
            payment.setPaymentId(paymentId);
        }
        payment.setCustomer((Customer) tfCustomer.getUserData());
        payment.setPaymentDate(dpPaymentDate.getValue());
        payment.setAmount(new BigDecimal(tfAmount.getText().trim()));
        PaymentMode mode = (PaymentMode) paymentModeToggle.getSelectedToggle()
                .getUserData();
        payment.setPaymentMode(mode);
        
        if (rdCheque.isSelected() || rdDemandDraft.isSelected() || rdBankTransfer.isSelected() || rdCash.isSelected()) {
            payment.setInstrumentNumber(tfInstrumentNumber.getText().trim());
            payment.setInstrumentDate(dpInstrumentDate.getValue());
            if (rdRealised.isSelected()) {
                payment.setInstrumentRealised(true);
            } else {
                payment.setInstrumentRealised(false);
            }
            String drawnAt = tfInstrumentDrawnAt.getText().trim();
            if (!drawnAt.isEmpty()) {
                payment.setDrawnAtBank(drawnAt);
            }
        }
        
        return payment;
    }
    
    private void clearControls() {
        tfCustomer.clear();
        tfCustomer.setUserData(null);
        tfCustomerBalance.clear();
        tfAmount.clear();
        
        paymentModeToggle.getToggles().stream().forEach((toggle) -> {
            if (toggle.isSelected()) {
                toggle.setSelected(false);
            }
        });
        
        tfInstrumentNumber.clear();
        tfInstrumentDrawnAt.clear();
        dpInstrumentDate.setValue(null);
        
        for (Toggle toggle : paymentReceivedToggle.getToggles()) {
            if (toggle.isSelected()) {
                toggle.setSelected(false);
            }
        }
        
    }

    private void setCustomerBalance(Customer customer) {
        
        if (customer == null) {
            String text = tfCustomer.getText();
            if (text == null || text.trim().isEmpty()) {
                tfCustomerBalance.clear();
            } else {
                tfCustomerBalance.setText("< client invalide >");
            }
            return;
        }
        
        BigDecimal balance = null;
        boolean error = false;
        
        try {
            balance = CustomersPersistence.getCustomerBalance(customer.getId());
        } catch (Exception e) {
            final String message = "An error occurred while retrieving the client" +
                    "database balance.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Erreur occured");
            alert.setHeaderText("Error retrieving customer balance !");
            alert.initOwner(mainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            error = true;
        }
        
        if (error) {
            tfCustomerBalance.setText("Erreur!!");
        } else {
            if (this.paymentId != 0) {
                BigDecimal amount = new BigDecimal(tfAmount.getText());
                balance = balance.subtract(amount);
            }
            String suffix = "";
            if (balance.signum() == -1) { //debit balance
                suffix = " DR.";
            } else if (balance.signum() == 1) {
                suffix = " CR.";
            }
            
            balance = balance.abs();
            String balanceString = IndianCurrencyFormatting
                    .applyFormatting(balance) + suffix;
            tfCustomerBalance.setText(balanceString);
        }
        
    }
    
    private ButtonType shouldSaveUnsavedData() {
    	String message = "Payment is not saved.\n"
                + "Do you want to save it before closing this window?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Payment not saved");
        alert.setHeaderText("Save payment before closing the window?");
        alert.initOwner(mainWindow);
         Global.styleAlertDialog(alert);

        Optional<ButtonType> response = alert.showAndWait();
        if (!response.isPresent()) {
            return ButtonType.CANCEL;
        }
        
        return response.get();

    }
    
     @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }
    
    public boolean loadPayment(int paymentId) {
        Payment payment = null;
        
        try {
            payment = PaymentPersistence.getPayment(paymentId);
        } catch (Exception e) {
        	String message = "An error occurred while retrieving the payment" +
                    " database details!";
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setHeaderText("Error retrieving payment details!");
            alert.setTitle("Erreur occured");
            alert.initOwner(mainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        this.paymentId = paymentId;
        populateControls(payment);
        btnRegisterPayment.setText("Update Payment");
        isDirty.set(false);
        return true;
    }
    
    private void populateControls(Payment payment) {
       dpPaymentDate.setValue(payment.getPaymentDate());
       final Customer customer = payment.getCustomer();
       
       tfCustomer.setText(customer.getName());
       tfCustomer.setUserData(customer);
       tfCustomer.setEditable(false);
       
       tfAmount.setText(payment.getAmount().toPlainString());
       
       PaymentMode mode = payment.getPaymentMode();
       switch(mode) {
           case CASH:
               rdCash.setSelected(true);
               break;
           case CHEQUE:
               rdCheque.setSelected(true);
               break;
           case DD:
               rdDemandDraft.setSelected(true);
               break;
           case BANKTRANSFER:
               rdBankTransfer.setSelected(true);
               break;
       }
       
       if (mode == PaymentMode.CASH || mode == PaymentMode.CHEQUE || mode == PaymentMode.DD || mode == PaymentMode.BANKTRANSFER ) {
           tfInstrumentNumber.setText(payment.getInstrumentNumber());
           dpInstrumentDate.setValue(payment.getInstrumentDate());
           String drawnAt = payment.getDrawnAtBank();
           if (drawnAt != null && !drawnAt.isEmpty()) {
               tfInstrumentDrawnAt.setText(drawnAt);
           }
           if (payment.getInstrumentRealised()) {
               rdRealised.setSelected(true);
           } else {
               rdNotRealised.setSelected(true);
           }
       }
       
        setCustomerBalance(customer);
    }
    
    public void addEntityEditedEventListener(EntityEditedListener listener) {
        if (eventListeners == null) {
            eventListeners = new ArrayList<>(2);
        }
        
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
        
    }
    
    public void removeEntityEditedEventListener(EntityEditedListener listener) {
        if (eventListeners != null) {
            if (eventListeners.contains(listener)) {
                eventListeners.remove(listener);
            }
        }
    }
    
    private void fireEntityEditedEvent(Object entityEdited) {
        if (eventListeners == null || eventListeners.isEmpty()) {
            return;
        }
        
        final EntityEditedEvent event = new EntityEditedEvent(this, entityEdited);
        
        for (EntityEditedListener listener : eventListeners) {
            listener.onEntityEdited(event);
        }
    }
    
    /**
     * 
     * @return - A Customer instance corresponding to the customer name
     * typed by the user in the tfCustomer text field. It returns null if no
     * matching customer is found.
     */
    private Customer getCustomer() {
         final String text = tfCustomer.getText();
        if (text == null || text.trim().isEmpty()) {
            return null;
        } else { //check whether it is a valid customer or not
            Optional<Customer> findFirst = customers.stream().filter((Customer t) -> 
                    t.getName().equalsIgnoreCase(text)
            ).findFirst();
            if (!findFirst.isPresent()) {
                return null;
            } else {
               return findFirst.get();
            }
        }
    }

    
}
