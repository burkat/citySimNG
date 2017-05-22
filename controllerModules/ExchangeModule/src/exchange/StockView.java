package exchange;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;

public class StockView extends Application {

    private static Stage stage;
    private static Stock stock;
    private static TableView<Resource> table;
    private static LineChart<String, Number> lineChart;
    private static ObservableList<PieChart.Data> pieChartData;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void start(Stage primaryStage) {

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        // stage settings
        Platform.setImplicitExit(false);
        stage = primaryStage;
        stage.setAlwaysOnTop(true);
        stage.setFullScreen(true);
        stage.setResizable(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        // resources table label
        final Label tableLabel = new Label("Resource table detailed info:");
        tableLabel.setFont(new Font("Arial", 20));
        tableLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.18, primaryScreenBounds.getHeight() * 0.04);
        tableLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.05);
        tableLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.60);

        // resources table settings
        table = new TableView<>();
        table.setEditable(false);
        table.setFixedCellSize(primaryScreenBounds.getHeight() * 0.04);
        table.setMaxHeight((primaryScreenBounds.getHeight() * 0.04) * 6.8);
        table.setLayoutX(primaryScreenBounds.getWidth() * 0.05);
        table.setLayoutY(primaryScreenBounds.getHeight() * 0.65);

        // settings columns
        TableColumn resourceNames = new TableColumn("Name");
        resourceNames.setMinWidth(primaryScreenBounds.getWidth() * 0.060);
        resourceNames.setCellValueFactory(new PropertyValueFactory<Resource, String>("name"));

        TableColumn resourcePrice = new TableColumn("Price");
        resourcePrice.setMinWidth(primaryScreenBounds.getWidth() * 0.060);
        resourcePrice.setCellValueFactory(new PropertyValueFactory<Resource, String>("priceString"));

        TableColumn resoureQuantity = new TableColumn("Quantity");
        resoureQuantity.setMinWidth(primaryScreenBounds.getWidth() * 0.060);
        resoureQuantity.setCellValueFactory(new PropertyValueFactory<Resource, Integer>("quantity"));

        ObservableList<Resource> lineChartData = FXCollections.observableArrayList(stock.getResources());
        table.setItems(lineChartData);
        table.getColumns().addAll(resourceNames, resourcePrice, resoureQuantity);

        // axis settings
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Turn");

        // linechart settings
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Stock Monitoring");
        lineChart.setLayoutX(primaryScreenBounds.getWidth() * 0.30);
        lineChart.setLayoutY(primaryScreenBounds.getHeight() * 0.05);
        lineChart.setPrefSize(primaryScreenBounds.getWidth() * 0.6, primaryScreenBounds.getHeight() * 0.7);
        lineChart.setStyle("-fx-background-color: rgba(0, 168, 255, 0.05);");

        // piechart settings
        pieChartData = FXCollections.observableArrayList();
        for (Resource resource : stock.getResources()) {
            pieChartData.add(new PieChart.Data(resource.getName(), resource.getQuantity()));
        }
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Stock resources chart");
        pieChart.setLayoutX(primaryScreenBounds.getWidth() * 0.02);
        pieChart.setLayoutY(primaryScreenBounds.getHeight() * 0.05);
        pieChart.setPrefSize(primaryScreenBounds.getWidth() * 0.25, primaryScreenBounds.getHeight() * 0.40);

        // combobox label
        final Label resourceComboBoxLabel = new Label("Choose resource type:");
        resourceComboBoxLabel.setFont(new Font("Arial", 20));
        resourceComboBoxLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.14,
                primaryScreenBounds.getHeight() * 0.04);
        resourceComboBoxLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.30);
        resourceComboBoxLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.77);

        // combo box for resource choosing
        final ComboBox resourceComboBox = new ComboBox();
        resourceComboBox.getItems().addAll(stock.getResourcesNames());
        resourceComboBox.getSelectionModel().selectFirst();
        resourceComboBox.setPrefSize(primaryScreenBounds.getWidth() * 0.10, primaryScreenBounds.getHeight() * 0.04);
        resourceComboBox.setLayoutX(primaryScreenBounds.getWidth() * 0.45);
        resourceComboBox.setLayoutY(primaryScreenBounds.getHeight() * 0.77);

        // text field label
        final Label resourceAmountLabel = new Label("Enter resource quantity:");
        resourceAmountLabel.setFont(new Font("Arial", 20));
        resourceAmountLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.14, primaryScreenBounds.getHeight() * 0.04);
        resourceAmountLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.30);
        resourceAmountLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.83);

        // text field settings
        final TextField resourceAmount = new TextField();
        resourceAmount.setPromptText("resource quantity");
        resourceAmount.setPrefColumnCount(10);
        resourceAmount.setPrefSize(primaryScreenBounds.getWidth() * 0.10, primaryScreenBounds.getHeight() * 0.04);
        resourceAmount.setLayoutX(primaryScreenBounds.getWidth() * 0.45);
        resourceAmount.setLayoutY(primaryScreenBounds.getHeight() * 0.83);

        // buy button label
        final Label buyButtonLabel = new Label("Buy chosen resources: ");
        buyButtonLabel.setFont(new Font("Arial", 20));
        buyButtonLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.14, primaryScreenBounds.getHeight() * 0.04);
        buyButtonLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.30);
        buyButtonLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.88);

        // buy button settings
        Button buyButton = new Button("BUY");
        buyButton.setOnAction(event -> {
            String message = stock.buyOperation((String) resourceComboBox.getValue(), resourceAmount.getText());
            updatePieChart();
            table.getColumns().get(0).setVisible(false);
            table.getColumns().get(0).setVisible(true);
            showAlert(message);
        });
        buyButton.setPrefSize(primaryScreenBounds.getWidth() * 0.10, primaryScreenBounds.getHeight() * 0.04);
        buyButton.setLayoutX(primaryScreenBounds.getWidth() * 0.45);
        buyButton.setLayoutY(primaryScreenBounds.getHeight() * 0.88);

        // sell button label
        final Label sellButtonLabel = new Label("Sell chosen resources: ");
        sellButtonLabel.setFont(new Font("Arial", 20));
        sellButtonLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.14, primaryScreenBounds.getHeight() * 0.04);
        sellButtonLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.30);
        sellButtonLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.93);

        // sell button settings
        Button sellButton = new Button("SELL");
        sellButton.setOnAction(event -> {
            String message = stock.sellOperation((String) resourceComboBox.getValue(), resourceAmount.getText());
            updatePieChart();
            table.getColumns().get(0).setVisible(false);
            table.getColumns().get(0).setVisible(true);
            showAlert(message);
        });
        sellButton.setPrefSize(primaryScreenBounds.getWidth() * 0.10, primaryScreenBounds.getHeight() * 0.04);
        sellButton.setLayoutX(primaryScreenBounds.getWidth() * 0.45);
        sellButton.setLayoutY(primaryScreenBounds.getHeight() * 0.93);

        // dice label
        final Label diceLabel = new Label("Take part in lottery:");
        diceLabel.setFont(new Font("Arial", 20));
        diceLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.14, primaryScreenBounds.getHeight() * 0.04);
        diceLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.58);
        diceLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.77);

        // dice button settings
        Button diceButton = new Button("ROLL THE DICE");
        diceButton.setOnAction(event -> {
            String message = stock.diceOperation();
            showAlert(message);
        });
        diceButton.setPrefSize(primaryScreenBounds.getWidth() * 0.10, primaryScreenBounds.getHeight() * 0.04);
        diceButton.setLayoutX(primaryScreenBounds.getWidth() * 0.72);
        diceButton.setLayoutY(primaryScreenBounds.getHeight() * 0.77);

        // exit label
        final Label exitLabel = new Label("Leave stock:");
        exitLabel.setFont(new Font("Arial", 20));
        exitLabel.setPrefSize(primaryScreenBounds.getWidth() * 0.14, primaryScreenBounds.getHeight() * 0.04);
        exitLabel.setLayoutX(primaryScreenBounds.getWidth() * 0.58);
        exitLabel.setLayoutY(primaryScreenBounds.getHeight() * 0.82);

        // exit button settings
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            stage.hide();
            stock.setWorkingStatus(true);
        });
        exitButton.setPrefSize(primaryScreenBounds.getWidth() * 0.10, primaryScreenBounds.getHeight() * 0.04);
        exitButton.setLayoutX(primaryScreenBounds.getWidth() * 0.72);
        exitButton.setLayoutY(primaryScreenBounds.getHeight() * 0.82);

        // main scene settings
        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(table, lineChart, exitButton, resourceAmount, resourceComboBox,
                buyButton, sellButton, diceButton, pieChart, resourceComboBoxLabel, resourceAmountLabel, buyButtonLabel,
                sellButtonLabel, tableLabel, diceLabel, exitLabel);

        stage.setScene(scene);
        stage.hide();
    }

    public static void initStockView() {
        launch();
    }

    public static void setStock(Stock stock) {
        StockView.stock = stock;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void updateLineChart() {
        lineChart.getData().clear();
        for (Resource resource : stock.getResources()) {
            XYChart.Series series = new XYChart.Series();
            series.setName(resource.getName());
            Double[] prices = stock.getPriceHistory().get(resource.getName());
            for (int i = 0; i < Stock.priceHistoryRange; i++) {
                series.getData().add(new XYChart.Data(String.valueOf(Stock.priceHistoryRange - i - 1), prices[i]));
            }
            lineChart.getData().add(series);
        }
    }

    private static void updatePieChart() {
        for (Data data : pieChartData) {
            data.setPieValue(stock.getResource(data.getName()).getQuantity());
        }
    }

    private static void showAlert(String message) {
        Alert alert;
        if (message.startsWith("ERROR")) {
            alert = new Alert(AlertType.ERROR);
        } else if (message.startsWith("WARNING")) {
            alert = new Alert(AlertType.WARNING);
        } else {
            alert = new Alert(AlertType.INFORMATION);
        }
        // alert.initStyle(StageStyle.UNDECORATED);
        alert.initOwner(stage);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void show() {
        Platform.runLater(() -> {
            updateLineChart();
            table.getColumns().get(0).setVisible(false);
            table.getColumns().get(0).setVisible(true);
            stage.show();
        });
    }

}