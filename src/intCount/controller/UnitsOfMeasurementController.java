/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import intCount.Global;
import intCount.database.MeasurementUnitPersistence;
import intCount.model.MeasurementUnit;
import intCount.model.UoMWithState;
import intCount.model.UpdateState;
import intCount.utility.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public final class UnitsOfMeasurementController implements TabContent {

    private Stage MainWindow;
     private List<UoMWithState> deletedUnits = null;
     private TabPane tabPane = null;
    
    @FXML
    private RadioButton rdoNew;
    @FXML
    private RadioButton rdoEdit;
    @FXML
    private TextField tfMeasurementUnit;
    @FXML
    private TextField tfAbbreviation;
    @FXML
    private Label lblUnitError;
    @FXML
    private Label lblAbbreviationError;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<UoMWithState> tableView;
    @FXML
    private ToggleGroup newOrEditToggle;
    @FXML
    private TableColumn<UoMWithState, String> measurementColumn;
    @FXML
    private TableColumn<UoMWithState, String> abbreviationColumn;
    @FXML private ImageView checkImage;
    
    private BooleanProperty isDirty = new SimpleBooleanProperty(false);
    
    public void initialize() {
        newOrEditToggle.selectedToggleProperty().addListener(this::onToggleChanged);
        
        tfMeasurementUnit.textProperty().addListener(e -> btnAdd.setDisable(false));
        tfAbbreviation.textProperty().addListener(e -> btnAdd.setDisable(false));
        
        rdoEdit.disableProperty().bind(tableView.getSelectionModel()
                .selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(tableView.getSelectionModel()
                .selectedItemProperty().isNull());
        
        btnAdd.setOnAction(this::onAddButtonAction);
        
        lblUnitError.managedProperty().bind(lblUnitError.visibleProperty());
        lblUnitError.visibleProperty().bind(lblUnitError.textProperty()
                .length().greaterThanOrEqualTo(1));
        
        lblAbbreviationError.managedProperty().bind(lblAbbreviationError.visibleProperty());
        lblAbbreviationError.visibleProperty().bind(lblAbbreviationError.textProperty()
                .length().greaterThanOrEqualTo(1));
        
        btnSave.disableProperty().bind(isDirty.not());
        
        tableView.getSelectionModel().selectedItemProperty()
                .addListener(this::onSelectedRowChanged);
        
        setTableCellFactories();
        
         checkImage.managedProperty().bind(checkImage.visibleProperty());
        checkImage.visibleProperty().bind(checkImage.opacityProperty()
                .greaterThan(0.0));
    }
    
    @FXML
    private void onSaveAction() {
        if(! saveData(true)) {
            return;     
        }
        
        final FadeTransition transition = new FadeTransition(Duration.seconds(3.0),
                checkImage);
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        transition.setOnFinished((ActionEvent event1) -> {
            tfMeasurementUnit.requestFocus();
        });
        
        try {
            transition.play();
        } catch (Exception e) {
           // do nothing
        }
        
    }
    
    @FXML
    private void onCloseAction() {
         if (shouldClose()) {
             closeTab();
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
                return saveData(false);
            }

        }

        return true;
    }

    @Override
    public void putFocusOnNode() {
       tfMeasurementUnit.requestFocus();
    }

    @Override
    public boolean loadData() {
        
        List<MeasurementUnit> list = null;
        try {
            list = MeasurementUnitPersistence.getMeasurementUnits();
        } catch (Exception e) {
            String message = Utility.getDataFetchErrorText();
            Alert alert = Utility.getErrorAlert("Erreur occured",
                    "Error while retrieving data", message, MainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }

        List<UoMWithState> units = UoMWithState.fromUnits(list);
        ObservableList<UoMWithState> observableList
                = FXCollections.<UoMWithState>observableList(units,
                        (UoMWithState unit) -> {
                            Observable[] array = new Observable[]{
                                unit.unitNameProperty(), unit.abbreviationProperty()
                            };

                            return array;
                        });

        tableView.setItems(observableList);
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
        MainWindow = stage;
    }
    
    private void onToggleChanged(ObservableValue<? extends Toggle> observable, 
            Toggle oldValue, Toggle newValue) {
        
        tfMeasurementUnit.clear();
        tfAbbreviation.clear();
        
        String text = (String) newValue.getUserData();
        if (text.equalsIgnoreCase("new")) {
            btnAdd.setText("_Add");
        } else{
            btnAdd.setText("_Update");
           UoMWithState unit =  tableView.getSelectionModel().getSelectedItem();
            populateFields(unit);
        }
        
        btnAdd.setDisable(true);
        Platform.runLater(() -> tfMeasurementUnit.requestFocus());
    }
    
    private void onAddButtonAction(ActionEvent event) {
        if (!validateInput()) {
            Utility.beep();
            return;
        }
        
        String name = tfMeasurementUnit.getText().trim();
        String abbreviation = tfAbbreviation.getText().trim();
        
        if (rdoNew.isSelected()) {
            UoMWithState unit = new UoMWithState();
            unit.setUnitName(name);
            unit.setAbbreviation(abbreviation);
            unit.setUpdateState(UpdateState.NEW);
            tableView.getItems().add(unit);
            tableView.getSelectionModel().select(unit);
            tableView.scrollTo(unit);
            tfMeasurementUnit.clear();
            tfAbbreviation.clear();
            tfMeasurementUnit.requestFocus();
        } else {
            UoMWithState unit = tableView.getSelectionModel().getSelectedItem();
            unit.setUnitName(name);
            unit.setAbbreviation(abbreviation);
            UpdateState updateState = unit.getUpdateState();
            if (updateState != UpdateState.NEW) {
                unit.setUpdateState(UpdateState.UPDATED);
            }
            tableView.requestFocus();
        }
        
        btnAdd.setDisable(true);
        isDirty.set(true);
    }
    
    @FXML
    private void onDeleteUnitAction(ActionEvent event) {
       UoMWithState unit = tableView.getSelectionModel()
                .getSelectedItem();
        String name = unit.getUnitName();

        String message = "Are you sure you want to delete the unit? '"
                + name + "'?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm deletion");
        alert.setHeaderText("Sure to delete?");
        alert.initOwner(MainWindow);
         Global.styleAlertDialog(alert);
         
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            ButtonType response = result.get();
            if (response == ButtonType.YES) {
                if (deletedUnits == null) {
                    deletedUnits = new ArrayList<>();
                }
                deletedUnits.add(unit);
                tableView.getItems().remove(unit);
                isDirty.set(true);
                tableView.requestFocus();
            }
        }
    }
    
    private boolean validateInput() {
       boolean validName = isValidUnitName();
       boolean validAbbreviation = isValidAbbreviation();
       
       return (validName && validAbbreviation);
       
    }
    
    private boolean isValidUnitName() {
         boolean valid = true;
        String text = tfMeasurementUnit.getText().trim();
        
        if (text.isEmpty()) {
            lblUnitError.setText("Unit of measurement not entered.");
            valid = false;
        } else if (text.length() > 40) {
            lblUnitError.setText("The length of the unit of measure cannot exceed 40 characters.");
            valid = false;
        } else if (isDuplicateUnitName(text)){
            lblUnitError.setText(String.format("Unit name '%s' already exists.", text));
            valid = false;
        } else {
            lblUnitError.setText("");
            valid = true;
        }
        return valid;
    }
    
    private boolean isValidAbbreviation() {
         boolean valid = true;
        String text = tfAbbreviation.getText().trim();
        
        if (text.isEmpty()) {
           lblAbbreviationError.setText("");
           valid = true;
        } else if (text.length() > 15) {
            lblAbbreviationError.setText("The length of the abbreviation cannot exceed 15 characters.");
            valid = false;
        } else if (isDuplicateAbbreviation(text)){
            lblAbbreviationError.setText(String.format("The abbreviation '% s' already exists.", text));
            valid = false;
        } else {
            lblAbbreviationError.setText("");
            valid = true;
        }
        return valid;
    }
    
    private boolean isDuplicateUnitName(String unitName) {
        UoMWithState selected = null;
        if (rdoEdit.isSelected()) {
            selected = tableView.getSelectionModel().getSelectedItem();
        }
        boolean duplicate = false;
        for(UoMWithState unit: tableView.getItems() ) {
            if (unitName.equalsIgnoreCase(unit.getUnitName())) {
                if (selected != null && !selected.equals(unit)) {
                    duplicate = true;
                    break;
                } else if (selected == null) {
                    duplicate = true;
                    break;
                }
            }
        }
        return duplicate;
    }
    
     private boolean isDuplicateAbbreviation(String abbreviation) {
        UoMWithState selected = null;
        if (rdoEdit.isSelected()) {
            selected = tableView.getSelectionModel().getSelectedItem();
        }
        boolean duplicate = false;
        for(UoMWithState unit: tableView.getItems() ) {
            if (abbreviation.equalsIgnoreCase(unit.getAbbreviation())) {
                if (selected != null && !selected.equals(unit)) {
                    duplicate = true;
                    break;
                } else if (selected == null) {
                    duplicate = true;
                    break;
                }
            }
        }
        return duplicate;
    }
     
     @SuppressWarnings("incomplete-switch")
	private boolean saveData(boolean toHouseKeep) {

        List<UoMWithState> addedUnits = new ArrayList<>();
        List<UoMWithState> updatedUnits = new ArrayList<>();

        UpdateState state = null;
        for (UoMWithState c : tableView.getItems()) {
            state = c.getUpdateState();
            if (state != null) {
                switch (c.getUpdateState()) {
                    case NEW:
                        addedUnits.add(c);
                        break;
                    case UPDATED:
                        updatedUnits.add(c);
                        break;
                }
            }

        }

        List<? extends MeasurementUnit> deleted = (deletedUnits != null) ? deletedUnits
                : Collections.<UoMWithState>emptyList();
        int[] autoIDs = null;

        try {
            autoIDs = MeasurementUnitPersistence.saveUnits(addedUnits, updatedUnits, deleted);
        } catch (Exception e) {
            String message = Utility.getDataSaveErrorText();
            Utility.beep();
            Alert alert = Utility.getErrorAlert("Erreur occured", "Error saving data",
                    message, MainWindow);
            alert.showAndWait();
            return false;
        }

        if (toHouseKeep) {
            doHouseKeeping(autoIDs, addedUnits, updatedUnits);
        }

        isDirty.set(false);
        return true;
    }
     
     private void doHouseKeeping(int[] autoIDs, List<UoMWithState> newUnits,
            List<UoMWithState> updatedUnits) {

        if (autoIDs != null) {
            int i = 0;
            for (UoMWithState c : newUnits) {
                c.setUnitId(autoIDs[i++]);
                c.setUpdateState(UpdateState.NONE);
            }
        }

        if (updatedUnits != null) {
            for (UoMWithState c : updatedUnits) {
                c.setUpdateState(UpdateState.NONE);
            }
        }

        if (deletedUnits != null) {
            deletedUnits.clear();
        }
    }
     
     private void closeTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(tab); //close the current tab
    }
     
     public void onSelectedRowChanged(ObservableValue<? extends UoMWithState> observable,
                    UoMWithState oldValue, UoMWithState newValue) {
             if (rdoEdit.isSelected()) {
                 populateFields(newValue);
             }
    }
     
     private void populateFields(UoMWithState unit) {
         tfMeasurementUnit.clear();
         tfAbbreviation.clear();
         
          if (unit != null) {
               tfMeasurementUnit.setText(unit.getUnitName());
               tfAbbreviation.setText(unit.getAbbreviation());
           }
     }
     
    @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }
    
    private void setTableCellFactories() {
        measurementColumn.setCellFactory((TableColumn<UoMWithState, String> param) -> {
           final TableCell<UoMWithState, String> cell = new TableCell<UoMWithState, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty); 
                    
                    setText(null);
                    setGraphic(null);
                    
                    if (item != null && !empty) {
                        setText(item);
                    }
                }
                
            };
           cell.getStyleClass().add("uom-cell");
            return cell;        
        });
        
        abbreviationColumn.setCellFactory((TableColumn<UoMWithState, String> param) -> {
            final TableCell<UoMWithState, String> cell = 
                    new TableCell<UoMWithState, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty); 
                    
                    setText(null);
                    setGraphic(null);
                    
                    if (item != null && !empty) {
                        setText(item);
                    }
                }
                
            };
            cell.getStyleClass().add("abbreviation-cell");
            return cell;
        });
    }
    
     private ButtonType shouldSaveUnsavedData() {
    	 final String promptMessage = "There are unsaved changes in the unit list.\n"
                 + "Save changes before closing the tab?";
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, promptMessage,
             ButtonType.YES, ButtonType.NO, ButtonType.CANCEL );
            alert.setHeaderText("List of unsaved units. Back up now?");
            alert.setTitle("Unsaved Units");
            alert.initOwner(MainWindow);
             Global.styleAlertDialog(alert);
             
            Optional<ButtonType> result = alert.showAndWait();
            if (! result.isPresent()) {
                return ButtonType.CANCEL;
            }
            
            return result.get();
    }
}
