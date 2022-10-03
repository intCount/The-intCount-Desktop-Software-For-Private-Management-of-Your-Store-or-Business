/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import javafx.fxml.FXML;


import javafx.scene.control.*;
import intCount.Global;
import intCount.database.CsvPersistence;
import intCount.database.Database;
import intCount.database.ItemsPersistence;
import intCount.model.Item;
import intCount.model.ItemWithState;
import intCount.model.UpdateState;
import intCount.utility.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public final class ItemsController implements TabContent {

	private Stage MainWindow;
	private List<ItemWithState> deletedItems = null;
	private TabPane tabPane = null;
	// private ObservableList<ItemWithState> itemstList = null;
	@SuppressWarnings("unused")
	private FilteredList<ItemWithState> filteredList = null;
	// private final DateTimeFormatter dateFormatter =
	// DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@FXML
	private RadioButton rdoNew;
	@FXML
	private RadioButton rdoEdit;
	@FXML
	private TextField tfName;
	@FXML
	private TextField tfHt;
	@FXML
	private ComboBox<BigDecimal> cbTva;
	@FXML
	private TextField tfStockActuel;
	@FXML
	private TextField tfDateEntree;
	@FXML
	private Label lblItemError;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnAdd;
	@FXML
	private Button btnDelete;
	@FXML
	private Button  btnImport;
	@FXML
	private TableView<ItemWithState> tableView;
	@FXML
	private TableColumn<ItemWithState, String> ArticlesColumn;
	@FXML
	private TableColumn<ItemWithState, BigDecimal> HTColumn;
	@FXML
	private TableColumn<ItemWithState, BigDecimal> TVAColumn;
	@FXML
	private TableColumn<ItemWithState, Integer> StockActuelColumn;
	@FXML
	private TableColumn<ItemWithState, LocalDate> DateEntreeColumn;
	@FXML
	private ToggleGroup newOrEditToggle;
	@FXML
	private ImageView checkImage;

	private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

	public void initialize() {
		
		System.out.println(cbTva.getSelectionModel().getSelectedItem());

		newOrEditToggle.selectedToggleProperty().addListener(this::onToggleChanged);

		tfName.textProperty().addListener(e -> btnAdd.setDisable(false));
		tfHt.textProperty().addListener(e -> btnAdd.setDisable(false));
		cbTva.getSelectionModel().selectedItemProperty().addListener(e -> btnAdd.setDisable(false));
		tfStockActuel.textProperty().addListener(e -> btnAdd.setDisable(false));
		tfDateEntree.textProperty().addListener(e -> btnAdd.setDisable(false));

		rdoEdit.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		btnDelete.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

		btnAdd.setOnAction(this::onAddButtonAction);

		lblItemError.managedProperty().bind(lblItemError.visibleProperty());
		lblItemError.visibleProperty().bind(lblItemError.textProperty().length().greaterThanOrEqualTo(1));

		btnSave.disableProperty().bind(isDirty.not());

		tableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedRowChanged);
		setListViewCellFactory();

		checkImage.managedProperty().bind(checkImage.visibleProperty());
		checkImage.visibleProperty().bind(checkImage.opacityProperty().greaterThan(0.0));
		
		cbTva.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					cbTva.show();
				}
			}
		});
		

		// loadData();
	}

	@FXML
	private void onSaveAction() {
		if (!saveData(true)) {
			return;
		}

		final FadeTransition transition = new FadeTransition(Duration.seconds(3.0), checkImage);
		transition.setFromValue(1.0);
		transition.setToValue(0.0);
		transition.setOnFinished((ActionEvent event1) -> {
			tfName.requestFocus();
		});

		try {
			transition.play();
		} catch (Exception e) {
			// do nothing
		}
	}
	
	@FXML
	private void onImportAction(){
		try {

			Connection connection = Database.getActiveYearConnection();
			CSVLoader loader = new CSVLoader(connection);
			
			loader.loadCSV(System.getProperty("user.dir").concat("/intCount/items.csv"), "CSV", true);
			CsvPersistence.joinCsvItem();
			//CsvPersistence.selectCsv();
			loadData();
			
		} catch (Exception e) {
			e.printStackTrace();
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
		ObservableList<ItemWithState> items = tableView.getItems();
		if (items != null && !items.isEmpty()) {
			tableView.getSelectionModel().selectFirst();
			tableView.requestFocus();
		} else {
			tfName.requestFocus();
		}

	}

	@Override
	public boolean loadData() {

		List<Item> list = null;
		try {
			list = ItemsPersistence.getItems();
			System.out.println(list);
		} catch (Exception e) {
			String message = Utility.getDataFetchErrorText();
			Alert alert = Utility.getErrorAlert("Erreur occured",
					"Error in loading item list", message, MainWindow);
			Utility.beep();
			alert.showAndWait();
			return false;
		}

		List<ItemWithState> items = ItemWithState.fromItems(list);
		ObservableList<ItemWithState> itemstList = FXCollections.<ItemWithState>observableList(items,
				(ItemWithState unit) -> {
					Observable[] array = new Observable[] { unit.itemNameProperty(), unit.hTProperty(),
							unit.tvaProperty(), unit.stockInitProperty(), unit.dateEntreeProperty() };

					return array;
				});

		filteredList = new FilteredList<ItemWithState>(itemstList, null);
		tableView.setItems(itemstList);
		itemstList.addListener((ListChangeListener.Change<? extends ItemWithState> c) -> {

		});
		return true;
	}

	@Override
	public void setMainWindow(Stage stage) {
		MainWindow = stage;
	}

	private void onToggleChanged(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

		tfName.clear();

		String text = (String) newValue.getUserData();
		if (text.equalsIgnoreCase("new")) {
			btnAdd.setText("_Add");
		} else {
			btnAdd.setText("_Update");
			ItemWithState item = tableView.getSelectionModel().getSelectedItem();
			populateFields(item);
			tfName.clear();
			lblItemError.setText("");
			if (item != null) {
				tfName.setText(item.getItemName());
			}
		}

		btnAdd.setDisable(true);
		Platform.runLater(() -> tfName.requestFocus());
	}

	private void onAddButtonAction(ActionEvent event) {
		if (!validateInput()) {
			Utility.beep();
			return;
		}

		String name = tfName.getText().trim();
		BigDecimal ht = new BigDecimal(tfHt.getText().trim(), MathContext.DECIMAL64);
		ht.setScale(3, RoundingMode.HALF_UP);
		BigDecimal tva = null;
		String tvaString = String.valueOf(cbTva.getSelectionModel().getSelectedItem());
		if (!tvaString.isEmpty()) {
			try {
				tva = new BigDecimal(tvaString);
				tva = tva.abs().setScale(2, RoundingMode.HALF_UP);
			} catch (Exception e) {
			}
		}
		Integer stockInit = Integer.valueOf(tfStockActuel.getText().trim());

		LocalDate dateEntree = null;

		dateEntree = Utility.getDateStringConverter().fromString(tfDateEntree.getText().trim());

		if (rdoNew.isSelected()) {
			ItemWithState item = new ItemWithState();
			item.setItemName(name);
			item.setHt(ht);
			item.setTva(tva);
			item.setDateEntree(dateEntree);
			item.setStockInit(stockInit);
			item.setUpdateState(UpdateState.NEW);
			tableView.getItems().add(item);
			tableView.getSelectionModel().select(item);
			tableView.scrollTo(item);
			tfName.clear();
			tfHt.clear();
			cbTva.getSelectionModel().clearSelection();
			tfStockActuel.clear();
			tfDateEntree.clear();
			tfName.requestFocus();
		} else {
			ItemWithState item = tableView.getSelectionModel().getSelectedItem();
			item.setItemName(name);
			item.setHt(ht);
			item.setTva(cbTva.getSelectionModel().getSelectedItem());
			item.setDateEntree(dateEntree);
			item.setStockInit(stockInit);
			if (item.getUpdateState() != UpdateState.NEW) {
				item.setUpdateState(UpdateState.UPDATED);
			}
			tableView.requestFocus();
		}

		btnAdd.setDisable(true);
		isDirty.set(true);
	}

	@FXML
	private void onDeleteItemAction(ActionEvent event) {
		ItemWithState item = tableView.getSelectionModel().getSelectedItem();
		String name = item.getItemName();
		// BigDecimal ht = item.getHt();
		String message = "Are you sure you want to delete the item? '" + name + "'?";
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		alert.setTitle("Confirm deletion");
		alert.setHeaderText("Make sure to delete the selected item ?");
		alert.initOwner(MainWindow);
		Global.styleAlertDialog(alert);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			ButtonType response = result.get();
			if (response == ButtonType.YES) {
				if (deletedItems == null) {
					deletedItems = new ArrayList<>();
				}
				deletedItems.add(item);
				tableView.getItems().remove(item);
				isDirty.set(true);
				tableView.requestFocus();
			}
		}
	}

	private boolean validateInput() {

		boolean valid = true;
		String text = tfName.getText().trim();

		if (text.isEmpty()) {
			lblItemError.setText("Item name not entered!");
			valid = false;
		} else if (text.length() > 150) {
			lblItemError.setText("The length of the item name cannot exceed 150 characters!");
			valid = false;
		} else if (isDuplicateItemName(text)) {
			lblItemError.setText(String.format("Item name '%s' already exists!", text));
			valid = false;
		} else {
			lblItemError.setText("");
			valid = true;
		}
		return valid;
	}

	private boolean isDuplicateItemName(String itemName) {
		ItemWithState selected = null;
		if (rdoEdit.isSelected()) {
			selected = tableView.getSelectionModel().getSelectedItem();
		}
		boolean duplicate = false;
		for (ItemWithState item : tableView.getItems()) {
			if (itemName.equalsIgnoreCase(item.getItemName())) {
				if (selected != null && !selected.equals(item)) {
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

	private boolean saveData(boolean toHouseKeep) {

		List<ItemWithState> addedItems = new ArrayList<>();
		List<ItemWithState> updatedItems = new ArrayList<>();

		UpdateState state = null;
		for (ItemWithState c : tableView.getItems()) {
			state = c.getUpdateState();
			if (state != null) {
				switch (c.getUpdateState()) {
				case NEW:
					addedItems.add(c);
					break;
				case UPDATED:
					updatedItems.add(c);
					break;
				default:
					break;
				}
			}

		}

		List<? extends Item> deleted = (deletedItems != null) ? deletedItems : Collections.<ItemWithState>emptyList();
		int[] autoIDs = null;

		try {
			autoIDs = ItemsPersistence.saveItems(addedItems, updatedItems, deleted);
		} catch (Exception e) {
			String message = Utility.getDataSaveErrorText();
			Utility.beep();
			Alert alert = Utility.getErrorAlert("Erreur occured", "Error saving data",
					message, MainWindow);
			alert.showAndWait();
			return false;
		}

		if (toHouseKeep) {
			doHouseKeeping(autoIDs, addedItems, updatedItems);
		}

		isDirty.set(false);
		return true;
	}

	private void doHouseKeeping(int[] autoIDs, List<ItemWithState> newItems, List<ItemWithState> updatedItems) {

		if (autoIDs != null) {
			int i = 0;
			for (ItemWithState c : newItems) {
				c.setItemId(autoIDs[i++]);
				c.setUpdateState(UpdateState.NONE);
			}
		}

		if (updatedItems != null) {
			for (ItemWithState c : updatedItems) {
				c.setUpdateState(UpdateState.NONE);
			}
		}

		if (deletedItems != null) {
			deletedItems.clear();
		}
	}

	private void closeTab() {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(tab); // close the current tab
	}

	public void onSelectedRowChanged(ObservableValue<? extends ItemWithState> observable, ItemWithState oldValue,
			ItemWithState newValue) {
		if (rdoEdit.isSelected()) {
			populateFields(newValue);
			if (newValue != null) {
				populateFields(newValue);
			}
		}
	}

	public void populateFields(ItemWithState item) {

		tfName.clear();
		tfHt.clear();
		cbTva.getSelectionModel().clearSelection();
		tfDateEntree.clear();
		tfStockActuel.clear();

		if (item != null) {

			tfName.setText(item.getItemName());
			BigDecimal ht = item.getHt();
			tfHt.setText(ht.toPlainString());
			BigDecimal tva = item.getTva();
			cbTva.getSelectionModel().select(tva);
			tfStockActuel.setText(String.valueOf(item.getStockInit()));
			tfDateEntree.setText(Utility.getDateStringConverter().toString(item.getDateEntree()));
		}
	}

	@Override
	public void setTabPane(TabPane pane) {
		this.tabPane = pane;
	}

	private void setListViewCellFactory() {

		ArticlesColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemWithState, String> param) -> {
			ItemWithState invoiceItem = param.getValue();
			ObjectBinding<String> binding = new ObjectBinding<String>() {
				{
					super.bind(invoiceItem.itemNameProperty());
				}

				@Override
				protected String computeValue() {
					return invoiceItem.getItemName();
				}
			};
			return binding;
		});

		HTColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemWithState, BigDecimal> param) -> {
			ItemWithState invoiceItem = param.getValue();
			ObjectBinding<BigDecimal> binding = new ObjectBinding<BigDecimal>() {
				{
					super.bind(invoiceItem.hTProperty());
				}

				@Override
				protected BigDecimal computeValue() {
					return invoiceItem.getHt();
				}
			};
			return binding;
		});

		TVAColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemWithState, BigDecimal> param) -> {
			ItemWithState invoiceItem = param.getValue();
			ObjectBinding<BigDecimal> binding = new ObjectBinding<BigDecimal>() {
				{
					super.bind(invoiceItem.tvaProperty());
				}

				@Override
				protected BigDecimal computeValue() {
					return invoiceItem.getTva();
				}
			};
			return binding;
		});

		StockActuelColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemWithState, Integer> param) -> {
			ItemWithState invoiceItem = param.getValue();
			ObjectBinding<Integer> binding = new ObjectBinding<Integer>() {
				{
					super.bind(invoiceItem.stockInitProperty());
				}

				@Override
				protected Integer computeValue() {
					return invoiceItem.getStockInit();
				}
			};
			return binding;
		});

		DateEntreeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemWithState, LocalDate> param) -> {
			ItemWithState invoiceItem = param.getValue();
			ObjectBinding<LocalDate> binding = new ObjectBinding<LocalDate>() {
				{
					super.bind(invoiceItem.dateEntreeProperty());
				}

				@Override
				protected LocalDate computeValue() {
					return invoiceItem.getDateEntree();
				}
			};
			return binding;
		});

	}

	private ButtonType shouldSaveUnsavedData() {
		final String promptMessage = "There are unsaved changes in the list of items.\n"
				+ "Save changes before closing the tab?";
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, promptMessage, ButtonType.YES, ButtonType.NO,
				ButtonType.CANCEL);
		alert.setHeaderText("List of unsaved items. Back up now?");
		alert.setTitle("Unsaved Items");
		alert.initOwner(MainWindow);
		Global.styleAlertDialog(alert);

		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent()) {
			return ButtonType.CANCEL;
		}

		return result.get();
	}

}
