package intCount.controller;

import javafx.fxml.FXML;


import javafx.scene.control.*;
import intCount.database.RecoveryPersistance;
import intCount.model.Recovery;
import intCount.model.Stock;
import intCount.utility.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hicham ALAOUI RIZQ
 */
public final class RecoveryController implements TabContent {

	private Stage MainWindow;
	@SuppressWarnings("unused")
	private List<Stock> deletedItems = null;
	private TabPane tabPane = null;
	@SuppressWarnings("unused")
	private FilteredList<Recovery> filteredList = null;

	@FXML
	private TableView<Recovery> tableView;
	@FXML
	private TableColumn<Recovery, String> CustomerColumn;
	@FXML
	private TableColumn<Recovery, LocalDate> DatePaimentColumn;
	@FXML
	private TableColumn<Recovery, BigDecimal> AmountColumn;

	@FXML
	private ImageView checkImage;

	private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

	public void initialize() {

		setListViewCellFactory();

		checkImage.managedProperty().bind(checkImage.visibleProperty());
		checkImage.visibleProperty().bind(checkImage.opacityProperty().greaterThan(0.0));

		// loadData();
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

		}

		return true;
	}

	@Override
	public boolean loadData() {

		List<Recovery> list = null;
		try {
			list = RecoveryPersistance.getRecovery();
			// System.out.println(list.get(2));
		} catch (Exception e) {
			String message = Utility.getDataFetchErrorText();
			Alert alert = Utility.getErrorAlert("Erreur occured",
					"Error in loading item list", message, MainWindow);
			Utility.beep();
			alert.showAndWait();
			return false;
		}

		List<Recovery> items = Recovery.fromItems(list);
		// System.out.println(items);
		ObservableList<Recovery> itemstList = FXCollections.<Recovery>observableList(items, (Recovery unit) -> {
			Observable[] array = new Observable[] { unit.customerProperty(), unit.amountProperty(),
					unit.paymentDateProperty()};

			// System.out.println(array);

			return array;
		});

		filteredList = new FilteredList<Recovery>(itemstList, null);
		tableView.setItems(itemstList);
		itemstList.addListener((ListChangeListener.Change<? extends Recovery> c) -> {

		});

		return true;
	}

	@Override
	public void setMainWindow(Stage stage) {
		MainWindow = stage;
	}

	private void closeTab() {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		tabPane.getTabs().remove(tab); // close the current tab
	}

	@Override
	public void setTabPane(TabPane pane) {
		this.tabPane = pane;
	}

	private void setListViewCellFactory() {

		CustomerColumn.setCellValueFactory((TableColumn.CellDataFeatures<Recovery, String> param) -> {
			Recovery invoiceItem = param.getValue();
			ObjectBinding<String> binding = new ObjectBinding<String>() {
				{
					super.bind(invoiceItem.customerProperty());
				}

				@Override
				protected String computeValue() {
					return invoiceItem.getCustomer();
				}
			};
			return binding;
		});

		AmountColumn.setCellValueFactory((TableColumn.CellDataFeatures<Recovery, BigDecimal> param) -> {
			Recovery invoiceItem = param.getValue();
			ObjectBinding<BigDecimal> binding = new ObjectBinding<BigDecimal>() {
				{
					super.bind(invoiceItem.amountProperty());
				}

				@Override
				protected BigDecimal computeValue() {
					return invoiceItem.getAmount();
				}
			};
			return binding;
		});

		

		DatePaimentColumn.setCellValueFactory((TableColumn.CellDataFeatures<Recovery, LocalDate> param) -> {
			Recovery invoiceItem = param.getValue();
			ObjectBinding<LocalDate> binding = new ObjectBinding<LocalDate>() {
				{
					super.bind(invoiceItem.paymentDateProperty());
				}

				@Override
				protected LocalDate computeValue() {
					return invoiceItem.getPaymentDate();
				}
			};
			return binding;
		});
		
	}

	@Override
	public void putFocusOnNode() {
		// TODO Auto-generated method stub

	}

}
