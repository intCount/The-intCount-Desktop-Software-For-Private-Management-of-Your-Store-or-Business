/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import intCount.model.*;
import intCount.utility.Utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 *
 * @author Hicham ALAOUI RIZQ
 */
public class CustomerEditController {

    public ObservableList<CustomerWithState> customers = null;
    
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfCity;
    @FXML
    private TextField tfPhoneNumbers;
    @FXML
    private TextField tfIce;
    @FXML
    private TextField tfTaxePro;
    @FXML
    private TextField tfOpeningBalance;
    @FXML
    private RadioButton rdCredit;
    @FXML
    private RadioButton rdDebit;
    @FXML
    private Label customerNameError;
    @FXML
    private Label cityError;
    @FXML
    private Label phoneNumbersError;
    @FXML
    private Label taxeProError;
    @FXML
    private Label iceError;
    @FXML
    private Label openingBalanceError;
    @FXML
    private Label balanceTypeError;

    private final ReadOnlyBooleanWrapper isOkay
            = new ReadOnlyBooleanWrapper(false);
    private CustomerWithState customer = null;

    public void initialize() {
        customerNameError.managedProperty().bind(customerNameError.visibleProperty());
        cityError.managedProperty().bind(cityError.visibleProperty());
        phoneNumbersError.managedProperty().bind(phoneNumbersError.visibleProperty());
        taxeProError.managedProperty().bind(taxeProError.visibleProperty());
        iceError.visibleProperty().bind(iceError.textProperty().length().greaterThanOrEqualTo(1));
        openingBalanceError.managedProperty().bind(openingBalanceError.visibleProperty());
        balanceTypeError.managedProperty().bind(balanceTypeError.visibleProperty());

        customerNameError.visibleProperty().bind(customerNameError.textProperty()
                .length().greaterThanOrEqualTo(1));
        cityError.visibleProperty().bind(cityError.textProperty()
                .length().greaterThanOrEqualTo(1));
        phoneNumbersError.visibleProperty().bind(phoneNumbersError.textProperty()
                .length().greaterThanOrEqualTo(1));
        taxeProError.visibleProperty().bind(taxeProError.textProperty()
                .length().greaterThanOrEqualTo(1));
        openingBalanceError.visibleProperty().bind(openingBalanceError.textProperty()
                .length().greaterThanOrEqualTo(1));
        balanceTypeError.visibleProperty().bind(balanceTypeError.textProperty()
                .length().greaterThanOrEqualTo(1));

    }

    public void putFocusOnFirstNode() {
        tfName.requestFocus();
    }

    public void populateFields(CustomerWithState customer) {
        this.customer = customer;

        tfName.setText(customer.getName());
        String city = customer.getCity();
        if (city != null) {
            tfCity.setText(city);
        }
        String phoneNumbers = customer.getPhoneNumbers();
        if (phoneNumbers != null) {
            tfPhoneNumbers.setText(phoneNumbers);
        }
        String taxeProfessionnel = customer.getTaxeProfessionnel();
        if (taxeProfessionnel != null) {
            tfTaxePro.setText(taxeProfessionnel);
        }
        
        if (customer.getIce() != null) {
            tfIce.setText(customer.getIce());
        }
        BigDecimal openingBalance = customer.getOpeningBalance();
        if (openingBalance != null) {
            tfOpeningBalance.setText(openingBalance.toPlainString());
        }

        BalanceType balanceType = customer.getBalanceType();
        if (balanceType != null) {
            switch (balanceType) {
                case CREDIT:
                    rdCredit.setSelected(true);
                    break;
                case DEBIT:
                    rdDebit.setSelected(true);
                    break;
            }
        }
    }

    public ReadOnlyBooleanProperty isOkayProperty() {
        return isOkay.getReadOnlyProperty();
    }

    public CustomerWithState getCustomer() {
        CustomerWithState customer = null;

        if (this.customer != null) {
            customer = this.customer;
        } else {
            customer = new CustomerWithState();
        }
        customer.setName(tfName.getText().trim());

        String city = tfCity.getText();
        if (city != null) {
            city = city.trim();
        }
        customer.setCity(city);

        String phone = tfPhoneNumbers.getText();
        if (phone != null) {
            phone = phone.trim();
        }
        customer.setPhoneNumbers(phone);
        
        String taxePro = tfTaxePro.getText();
        if (taxePro != null) {
        	taxePro = taxePro.trim();
        }
        customer.setTaxeProfessionnel(taxePro);
        
        String ice = tfIce.getText();
        if (ice != null) {
        	ice = ice.trim();
        }
        customer.setIce(ice);

        String strAmount = tfOpeningBalance.getText();
        BigDecimal balance = null;
        if (strAmount != null && !strAmount.isEmpty()) {
            balance = new BigDecimal(strAmount.trim());
            balance = balance.abs().setScale(2, RoundingMode.HALF_UP);
        }
        customer.setOpeningBalance(balance);

        BalanceType balanceType = null;
        if (balance != null && balance.compareTo(BigDecimal.ZERO) == 1) {
            if (rdCredit.isSelected()) {
                balanceType = BalanceType.CREDIT;
            } else if (rdDebit.isSelected()) {
                balanceType = BalanceType.DEBIT;
            }
        }
        customer.setBalanceType(balanceType);

        return customer;
    }

    @FXML
    private void onOkAction() {
        //if input is valid, then close the window.
        if (!isValidInput()) {
            Utility.beep();
           tfName.getScene().getWindow().sizeToScene();
            return;
        }

        this.isOkay.set(true); // to indicate to the calling window that the input was valid
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancelAction() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    private boolean isValidInput() {
        boolean valid = true;

        String name= tfName.getText().trim();
        int nameLength = name.length();
        if (nameLength == 0) {
            customerNameError.setText("Name not provided. ");
            valid = false;
        } else if (nameLength < 3 || nameLength > 70) {
            customerNameError.setText("The name must be between 3 and 70 characters..");
            valid = false;
        } else if(isDuplicateCustomerName(name)) {
            customerNameError.setText(String.format("A client with the name '% s' already exists.", name));
            valid = false;
        } else {
            customerNameError.setText("");
        }

        int cityLength = tfCity.getText().trim().length();
        if (cityLength > 120) {
            cityError.setText("The length of the city name cannot exceed 40 characters.");
            valid = false;
        } else {
            cityError.setText("");
        }

        int phoneLength = tfPhoneNumbers.getText().trim().length();
        if (phoneLength > 120) {
            phoneNumbersError.setText("The length of phone numbers cannot exceed 120 characters.");
            valid = false;
        } else {
            phoneNumbersError.setText("");
        }
        
        int taxePro = tfTaxePro.getText().trim().length();
        if (taxePro > 60) {
        	taxeProError.setText("The length of the professional tax cannot exceed 60 characters.");
            valid = false;
        } else {
        	taxeProError.setText("");
        }
        
        int ice = tfIce.getText().trim().length();
        if (ice > 20) {
            iceError.setText("The length of the ice cannot exceed 20 characters.");
            valid = false;
        } else {
            iceError.setText("");
        }

        BigDecimal balance = null;
        String str = tfOpeningBalance.getText().trim();
        if (str.length() > 0) {
            try {

                balance = new BigDecimal(str);
                if (balance.signum() == -1) {
                    openingBalanceError.setText("Opening balance cannot be specified as a negative number.");
                    valid = false;
                } else {
                    openingBalanceError.setText("");
                }
            } catch (NumberFormatException e) {
                openingBalanceError.setText("The opening balance amount is not in a valid numeric format");
                valid = false;
            }
        } else {
            openingBalanceError.setText("");
        }

        if (balance != null && balance.compareTo(BigDecimal.ZERO) == 1) {
            if (!rdCredit.isSelected() && !rdDebit.isSelected()) {
                balanceTypeError.setText("Opening balance type not specified.");
                valid = false;
            } else {
                balanceTypeError.setText("");
            }
        }

        return valid;
    }
    
    private boolean isDuplicateCustomerName(String name) {
        boolean isDuplicate = false;
        for(CustomerWithState c : customers) {
            if (c.getName().equalsIgnoreCase(name)) {
                if (this.customer != null && this.customer == c) {
                    continue;
                }  else {
                    isDuplicate = true;
                }
            }
        }
        
        return isDuplicate;
    }

}
