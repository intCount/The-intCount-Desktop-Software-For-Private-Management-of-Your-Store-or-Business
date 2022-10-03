/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.controller;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import intCount.database.StockPersistance;
import intCount.model.Stock;
import intCount.utility.*;

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
public final class StockController implements TabContent {

	private Stage MainWindow;
	@SuppressWarnings("unused")
	private List<Stock> deletedItems = null;
	private TabPane tabPane = null;
	@SuppressWarnings("unused")
	private FilteredList<Stock> filteredList = null;

	@FXML
	private TableView<Stock> tableView;
	@FXML
	private TableColumn<Stock, String> ArticlesColumn;
	@FXML
	private TableColumn<Stock, LocalDate> DateEntreeColumn;
	@FXML
	private TableColumn<Stock, Integer> StockInitialColumn;
	@FXML
	private TableColumn<Stock, LocalDate> DateSortieColumn;
	@FXML
	private TableColumn<Stock, Integer> StockEnMvtColumn;
	@FXML
	private TableColumn<Stock, Integer> StockActuelColumn;
	@FXML
	private ToggleGroup newOrEditToggle;
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

		List<Stock> list = null;
		try {
			list = StockPersistance.getStock();
			// System.out.println(list.get(2));
		} catch (Exception e) {
			String message = Utility.getDataFetchErrorText();
			Alert alert = Utility.getErrorAlert("Erreur occured",
					"Error in loading item list", message, MainWindow);
			Utility.beep();
			alert.showAndWait();
			return false;
		}

		List<Stock> items = Stock.fromItems(list);
		// System.out.println(items);
		ObservableList<Stock> itemstList = FXCollections.<Stock>observableList(items, (Stock unit) -> {
			Observable[] array = new Observable[] { unit.itemNameProperty(), unit.dateEntreeProperty(),
					unit.stockInitProperty(), unit.dateSortieProperty(), unit.stockEnMvtProperty() };

			// System.out.println(array);

			return array;
		});

		filteredList = new FilteredList<Stock>(itemstList, null);
		tableView.setItems(itemstList);
		itemstList.addListener((ListChangeListener.Change<? extends Stock> c) -> {

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

		ArticlesColumn.setCellValueFactory((TableColumn.CellDataFeatures<Stock, String> param) -> {
			Stock invoiceItem = param.getValue();
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

		DateEntreeColumn.setCellValueFactory((TableColumn.CellDataFeatures<Stock, LocalDate> param) -> {
			Stock invoiceItem = param.getValue();
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

		StockInitialColumn.setCellValueFactory((TableColumn.CellDataFeatures<Stock, Integer> param) -> {
			Stock invoiceItem = param.getValue();
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

		DateSortieColumn.setCellValueFactory((TableColumn.CellDataFeatures<Stock, LocalDate> param) -> {
			Stock invoiceItem = param.getValue();
			ObjectBinding<LocalDate> binding = new ObjectBinding<LocalDate>() {
				{
					super.bind(invoiceItem.dateSortieProperty());
				}

				@Override
				protected LocalDate computeValue() {
					return invoiceItem.getDateSortie();
				}
			};
			return binding;
		});

		StockEnMvtColumn.setCellValueFactory((TableColumn.CellDataFeatures<Stock, Integer> param) -> {
			Stock invoiceItem = param.getValue();
			ObjectBinding<Integer> binding = new ObjectBinding<Integer>() {
				{
					super.bind(invoiceItem.stockEnMvtProperty());
				}

				@Override
				protected Integer computeValue() {
					return invoiceItem.getStockEnMvt();
				}
			};
			return binding;
		});

		StockActuelColumn.setCellValueFactory((TableColumn.CellDataFeatures<Stock, Integer> param) -> {
			Stock invoiceItem = param.getValue();
			ObjectBinding<Integer> binding = new ObjectBinding<Integer>() {
				{
					super.bind();
				}

				@Override
				protected Integer computeValue() {
					return invoiceItem.getStockInit() - invoiceItem.getStockEnMvt() ;
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
