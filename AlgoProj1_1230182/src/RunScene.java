import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RunScene {

    private Stage stage;
    private Stage owner;
    private MyArrayList<Task> tasks;
    private int maxTime;

    private int[][] dp;
    private boolean[][] take;

    private ObservableList<Task> selectedTasks = FXCollections.observableArrayList();
    private Label resultLabel = new Label();

    private TableView<Integer> dpTable;

    private long dpTimeNs = 0;
    private long greedyTimeNs = 0;

    public RunScene(Stage owner, MyArrayList<Task> tasks, int maxTime) {
        this.owner = owner;
        this.tasks = tasks;
        this.maxTime = maxTime;
        buildUI();
        runDP();
        stage.showAndWait();
    }

    private void buildUI() {

        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        Label title = new Label("Dynamic Programming Result");
        Button compareBtn = new Button("Compare with Greedy");

        BorderPane topBar = new BorderPane();
        topBar.setLeft(title);
        topBar.setRight(compareBtn);
        topBar.setPadding(new Insets(15));

        dpTable = new TableView<>();
        dpTable.setPrefWidth(1000);
        dpTable.setPrefHeight(400);
        dpTable.setMinHeight(400);
        dpTable.setMaxHeight(400);

        TableColumn<Integer, Integer> capCol = new TableColumn<>("w");
        capCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue()).asObject()
        );
        dpTable.getColumns().add(capCol);

        for (int i = 1; i <= tasks.size(); i++) {
            final int item = i;
            TableColumn<Integer, Integer> col = new TableColumn<>("Item " + item);
            col.setCellValueFactory(d ->
                    new javafx.beans.property.SimpleIntegerProperty(
                            dp[item][d.getValue()]
                    ).asObject()
            );
            dpTable.getColumns().add(col);
        }

        TableView<Task> selectedTable = new TableView<>(selectedTasks);

        TableColumn<Task, String> nameCol = new TableColumn<>("Task");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        TableColumn<Task, Double> hoursCol = new TableColumn<>("Hours");
        hoursCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("timeHours"));

        TableColumn<Task, Integer> prodCol = new TableColumn<>("Productivity");
        prodCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("productivity"));

        selectedTable.getColumns().addAll(nameCol, hoursCol, prodCol);
        selectedTable.setPrefWidth(450);
        selectedTable.setPrefHeight(400);
        selectedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox selectedPanel = new VBox(10, new Label("Selected Tasks"), selectedTable);
        selectedPanel.setPrefWidth(450);

        HBox tablesRow = new HBox(15, dpTable, selectedPanel);
        tablesRow.setPadding(new Insets(10));

        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(1450);

        VBox center = new VBox(10, tablesRow, resultLabel);
        center.setPadding(new Insets(10));

        Button backBtn = new Button("Go Back");
        backBtn.setOnAction(e -> stage.close());

        HBox bottom = new HBox(backBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(15));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(center);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 1500, 900);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);

        compareBtn.setOnAction(e -> {
            int greedy = runGreedy();
            resultLabel.setText(
                    resultLabel.getText() +
                    "\nGreedy Productivity: " + greedy
            );
        });

        ObservableList<Integer> rows = FXCollections.observableArrayList();
        for (int w = 1; w <= maxTime; w++) rows.add(w);
        dpTable.setItems(rows);
    }

    private void runDP() {

        long startDp = System.nanoTime();

        selectedTasks.clear();

        int n = tasks.size();
        dp = new int[n + 1][maxTime + 1];
        take = new boolean[n + 1][maxTime + 1];

        for (int i = 1; i <= n; i++) {

            Task task = tasks.get(i - 1);
            int w = task.getTime();
            int p = task.getProductivity();

            for (int t = 0; t <= maxTime; t++) {

                dp[i][t] = dp[i - 1][t];
                take[i][t] = false;

                if (t >= w) {//available time?>task weight or time 
                    int alter = dp[i - 1][t - w] + p;
                    if (alter > dp[i][t]) {
                        dp[i][t] = alter;
                        take[i][t] = true;
                    }
                }
            }
        }

        long endDp = System.nanoTime();
        dpTimeNs = endDp - startDp;

        buildSelectedTasks();
        dpTable.refresh();
    }

    private void buildSelectedTasks() {

        selectedTasks.clear();
        int t = maxTime;

        for (int i = tasks.size(); i > 0; i--) {
            if (take[i][t]) {
                Task task = tasks.get(i - 1);
                selectedTasks.add(task);
                t -= task.getTime();
            }
        }

//        FXCollections.reverse(selectedTasks);

        resultLabel.setText(
                "Max Productivity: " + dp[tasks.size()][maxTime] +
                "\nUsed Time Units: " + (maxTime - t) +" &	Used Time (Hours) : "+(maxTime-t)/2+
                "\nDP Time: " + dpTimeNs + " ns" 
        );
    }

    private int runGreedy() {


        int time = maxTime;
        int sum = 0;
        
        long startG = System.nanoTime();

        for (int i = 0; i < tasks.size(); i++) {
        	if (time <= 0) break;
            Task task = tasks.get(i);
            if (task.getTime() <= time) {
                time -= task.getTime();
                sum += task.getProductivity();
            }
        }

        long endG = System.nanoTime();
        greedyTimeNs = endG - startG;

        resultLabel.setText(
                resultLabel.getText() +
                "\nGreedy Time: " + greedyTimeNs + " ns"
        );

        return sum;
    }
}
