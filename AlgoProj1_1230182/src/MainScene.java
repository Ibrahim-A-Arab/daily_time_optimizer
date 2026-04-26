import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.ButtonBar;


public class MainScene{
	
	private Stage stage;
	private Scene scene;
    private MyArrayList<Task> tasks;
    
	public MainScene(Stage stage, MyArrayList<Task> tasks) {
		this.stage = stage;
        this.tasks = tasks;
        stage.setFullScreen(true);
        buildUI();
	}
	
	public Scene getScene() {
        return scene;
    }
	private void buildUI() {
		BorderPane root = new BorderPane();
	    scene = new Scene(root, 1000,600);
	    
	    //start of topBar
	    Label titleLabel = new Label("OPTIMIZEEE®");
	    Label titleSmallLabel = new Label("my daily tasks optimizer");
	    VBox verticalTitleSmallCenter = new VBox(titleSmallLabel);
	    verticalTitleSmallCenter.setAlignment(Pos.CENTER);
	    HBox titleHbox = new HBox(titleLabel,verticalTitleSmallCenter); 
	    Button dpRelationButton = new Button("DP Relation");
	    dpRelationButton.setPrefSize(200,100);
	    BorderPane topBar = new BorderPane();
	    topBar.setLeft(titleHbox);
	    topBar.setRight(dpRelationButton);
	    topBar.setPadding(new Insets(10,0,130,0));
	    root.setTop(topBar);
	    //end
	    //start of tableview Left center 
	    TableView<Task> tableView = new TableView<>();
	    tableView.setPrefHeight(550);
	    tableView.setMinHeight(550);
	    tableView.setMaxHeight(550);
	    tableView.setPrefWidth(1000);

	    TableColumn<Task, String> nameCol = new TableColumn<>("Task Name");
	    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
	    TableColumn<Task, Double> timeCol = new TableColumn<>("Time (hours)");
	    timeCol.setCellValueFactory(new PropertyValueFactory<>("timeHours"));
	    TableColumn<Task, Integer> profitCol = new TableColumn<>("Productivity");
	    profitCol.setCellValueFactory( new PropertyValueFactory<>("productivity"));
	    
	    nameCol.setSortable(false);
	    timeCol.setSortable(false);
	    profitCol.setSortable(false);

	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    nameCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.34));
	    timeCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.33));
	    profitCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.33));

	    
	    tableView.getColumns().addAll(nameCol,timeCol, profitCol);
	    
	    ObservableList<Task> tableTasks = FXCollections.observableArrayList();
	    tableView.setItems(tableTasks);
	    refreshTable(tableTasks);
	    
	    HBox tableCenterHBox = new HBox(tableView);
	    tableCenterHBox.setAlignment(Pos.CENTER);
	    //end of table
	    //start of buttons Right Center 
	    Button loadFileBtn = new Button("Load New File");
	    Button addBtn = new Button("Add Task");
	    Button editBtn = new Button("Edit Task");
	    Button deleteBtn = new Button("Delete Task");
	    Label fileLoaded = new Label("");
	    VBox controlsBox = new VBox(10,
	    		fileLoaded,
	            loadFileBtn,
	            addBtn,
	            editBtn,
	            deleteBtn
	    );
	    controlsBox.setAlignment(Pos.CENTER);
	    loadFileBtn.setPrefSize(300,80);
	    addBtn.setPrefSize(300, 150);
	    editBtn.setPrefSize(300,150);
	    deleteBtn.setPrefSize(300,150);
	    controlsBox.setPadding(new Insets(10));
//	    controlsBox.setPrefWidth(150);
	    //end of buttons
	    //start of center hbox
	    HBox centerBox = new HBox(10, tableCenterHBox, controlsBox);
	    centerBox.setPadding(new Insets(10));
	    centerBox.setAlignment(Pos.CENTER);
	    root.setCenter(centerBox);
	    //end of center hbox
	    //start of bottom section
	    Button runButton = new Button("Run");
	    runButton.setPrefHeight(100);
	    runButton.setPrefWidth(1000);
	    Spinner<Double> freeTimeSpinner =
	            new Spinner<>(0.5, 24.0, 8.0, 0.5);

	    freeTimeSpinner.setPrefSize(300, 100);
	    freeTimeSpinner.setEditable(false);


	    HBox bottomBar = new HBox(10,runButton,freeTimeSpinner);
	    bottomBar.setPadding(new Insets(0,0,200,0));
	    bottomBar.setAlignment(Pos.CENTER);

	    root.setBottom(bottomBar);
	    //end of bottom section
	    
	    scene.getStylesheets().add(
	    	    getClass().getResource("style.css").toExternalForm());
	    titleLabel.getStyleClass().add("title-label");
	    titleSmallLabel.getStyleClass().add("title-small-label");


//	    /////////////////////////////////////////////////	    
//	    tasks.add(new Task("Study Algorithms", 4, 80));
//	    tasks.add(new Task("Gym", 2, 30));
//	    tasks.add(new Task("Project Work", 5, 100));
//	    tasks.add(new Task("Reading", 1, 20));
//	    refreshTable(tableTasks);
//	    //testDummyTasks//

	    refreshTable(tableTasks);
	    
	    loadFileBtn.setOnAction(e -> {

	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Load Tasks File");
	        fileChooser.getExtensionFilters().add(
	            new FileChooser.ExtensionFilter("Text Files", "*.txt")
	        );

	        File file = fileChooser.showOpenDialog(stage);
	        if (file == null) return;

	        tasks.clear();
	        tableTasks.clear();

	        StringBuilder report = new StringBuilder();
	        int emptyLines = 0;
	        int lineNumber = 0;
	        boolean hasValidTask = false;

	        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

	        	String firstLine = br.readLine();

	        	if (firstLine != null && firstLine.matches("\\d+(\\.0|\\.5)?")) {
	        	    double freeTime = Double.parseDouble(firstLine);

	        	    if (freeTime >= 0.5 && freeTime <= 24.0) {
	        	        freeTimeSpinner.getValueFactory().setValue(freeTime);
	        	    }
	        	}

	            
	            String line;
	            
	            
	            while ((line = br.readLine()) != null) {
	                lineNumber++;

	                if (line.trim().isEmpty()) {
	                    emptyLines++;
	                    continue;
	                }

	                String[] parts = line.split(",");

	                if (parts.length != 3) {
	                    report.append("Line ").append(lineNumber)
	                          .append(": invalid format\n");
	                    continue;
	                }
	                String name = parts[0].trim();
	                String timeStr = parts[1].trim();
	                String profitStr = parts[2].trim();

	                if (!timeStr.matches("\\d+(\\.0|\\.5)?")) {
	                    report.append("Line ").append(lineNumber)
	                          .append(": time must be in 0.5 hour steps\n");
	                    continue;
	                }

	                int profit;
	                try {
	                    profit = Integer.parseInt(profitStr);
	                } catch (NumberFormatException ex) {
	                    report.append("Line ").append(lineNumber)
	                          .append(": productivity not a number\n");
	                    continue;
	                }

	                if (profit < 0) {
	                    report.append("Line ").append(lineNumber)
	                          .append(": negative productivity\n");
	                    continue;
	                }

	                // convert AFTER validation
	                double hours = Double.parseDouble(timeStr);
	                int timeUnits = (int) (hours * 2);

	                if (timeUnits <= 0) {
	                    report.append("Line ").append(lineNumber)
	                          .append(": time not positive\n");
	                    continue;
	                }

	                if (timeUnits > 48) {
	                    report.append("Line ").append(lineNumber)
	                          .append(": time exceeds 24 hours\n");
	                    continue;
	                }

	                tasks.add(new Task(name, timeUnits, profit));
	                hasValidTask = true;
	            }

	        } catch (IOException ex) {
	            Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle("File Error");
	            alert.setHeaderText("Cannot read file");
	            alert.setContentText("An error occurred while reading the file.");
	            alert.showAndWait();
	            return;
	        }

	        // Empty file
	        if (!hasValidTask) {
	            Alert alert = new Alert(Alert.AlertType.INFORMATION);
	            alert.setTitle("Empty File");
	            alert.setHeaderText("You have nothing to do all day 🙂");
	            alert.setContentText("The selected file is empty.");
	            alert.showAndWait();
	            return;
	        }else {
	        	fileLoaded.setText("Loaded file: " + file.getName());

		        if (report.length() > 0 || emptyLines > 0) {
		            if (emptyLines > 0) {
		                report.append("\nAnd ")
		                      .append(emptyLines)
		                      .append(" empty lines were ignored.");
		            }

		            Alert alert = new Alert(Alert.AlertType.WARNING);
		            alert.setTitle("File Loaded with Issues");
		            alert.setHeaderText("Some lines were skipped");
		            alert.setContentText(report.toString());
		            alert.showAndWait();
		        }
		        refreshTable(tableTasks);
	        }
  
	    });
	    addBtn.setOnAction(e->{
	    	Stage popup = new Stage();
	        popup.initOwner(stage);
	        popup.initModality(Modality.APPLICATION_MODAL);
	        popup.initStyle(StageStyle.UNDECORATED);
	        popup.setTitle("Add Task");

	        Label nameLabel = new Label("Name");
	        Label timeLabel = new Label("Time (0.5 steps)");
	        Label prodLabel = new Label("Productivity");

	        TextField nameField = new TextField();
	        TextField timeField = new TextField();
	        TextField prodField = new TextField();

	        nameField.setPromptText("e.g., Study Algorithms");
	        timeField.setPromptText("e.g., 1.5");
	        prodField.setPromptText("e.g., 80");

	        // optional: only allow numbers/dot in time, only digits in productivity
	        timeField.setTextFormatter(new TextFormatter<>(c ->
	                c.getControlNewText().matches("\\d*(\\.\\d*)?") ? c : null));

	        prodField.setTextFormatter(new TextFormatter<>(c ->
	                c.getControlNewText().matches("\\d*") ? c : null));

	        Button add = new Button("Add");
	        Button cancel = new Button("Cancel");

	        add.getStyleClass().add("primary-btn");
	        cancel.getStyleClass().add("secondary-btn");

	        GridPane form = new GridPane();
	        form.getStyleClass().add("popup-card");
	        form.setHgap(16);
	        form.setVgap(18);
	        form.setAlignment(Pos.CENTER);
	        form.setPadding(new Insets(22));

	        nameLabel.getStyleClass().add("form-label");
	        timeLabel.getStyleClass().add("form-label");
	        prodLabel.getStyleClass().add("form-label");

	        nameField.getStyleClass().add("form-input");
	        timeField.getStyleClass().add("form-input");
	        prodField.getStyleClass().add("form-input");

	        // good proportions
	        nameField.setPrefWidth(320);
	        timeField.setPrefWidth(320);
	        prodField.setPrefWidth(320);

	        form.add(nameLabel, 0, 0);
	        form.add(nameField, 1, 0);

	        form.add(timeLabel, 0, 1);
	        form.add(timeField, 1, 1);

	        form.add(prodLabel, 0, 2);
	        form.add(prodField, 1, 2);

	        HBox buttons = new HBox(12, add, cancel);
	        buttons.setAlignment(Pos.CENTER_RIGHT);
	        form.add(buttons, 1, 3);

	        GridPane.setHalignment(nameLabel, HPos.RIGHT);
	        GridPane.setHalignment(timeLabel, HPos.RIGHT);
	        GridPane.setHalignment(prodLabel, HPos.RIGHT);

	        BorderPane root1 = new BorderPane(form);
	        root1.setPadding(new Insets(18));
	        root1.getStyleClass().add("popup-root");

	        Scene popupScene = new Scene(root1, 560, 330);
	        popupScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
	        popup.setScene(popupScene);
	        popup.centerOnScreen();

	        cancel.setOnAction(ev -> popup.close());

	        add.setOnAction(ev -> {
	            String name = nameField.getText().trim();
	            String timeStr = timeField.getText().trim();
	            String prodStr = prodField.getText().trim();

	            StringBuilder errors = new StringBuilder();

	            if (name.isEmpty()) errors.append("• Name is empty\n");
	            if (timeStr.isEmpty()) errors.append("• Time is empty\n");
	            if (prodStr.isEmpty()) errors.append("• Productivity is empty\n");

	            if (errors.length() == 0) {
	                // validate time: must be 0.5 steps, 0.5..24
	                if (!timeStr.matches("\\d+(\\.0|\\.5)?")) {
	                    errors.append("• Time must be in 0.5 steps (e.g., 0.5, 1, 1.5)\n");
	                } else {
	                    double hours = Double.parseDouble(timeStr);
	                    if (hours < 0.5 || hours > 24.0) {
	                        errors.append("• Time must be between 0.5 and 24\n");
	                    }
	                }

	                // validate productivity
	                try {
	                    int p = Integer.parseInt(prodStr);
	                    if (p < 0) errors.append("• Productivity must be >= 0\n");
	                } catch (NumberFormatException ex) {
	                    errors.append("• Productivity must be an integer\n");
	                }
	            }

	            if (errors.length() > 0) {
	                Alert a = new Alert(Alert.AlertType.WARNING);
	                a.setTitle("Invalid Input");
	                a.setHeaderText("Fix these issues");
	                a.setContentText(errors.toString());
	                a.initOwner(popup);
	                a.showAndWait();
	                return;
	            }

	            int timeUnits = (int) (Double.parseDouble(timeStr) * 2);
	            int productivity = Integer.parseInt(prodStr);

	            tasks.add(new Task(name, timeUnits, productivity));
	            refreshTable(tableTasks);
	            popup.close();
	        });

	        popup.showAndWait();
	    });
	    editBtn.setOnAction(e->{
	    	Task selected = tableView.getSelectionModel().getSelectedItem();

	        if (selected == null) {
	            Alert a = new Alert(Alert.AlertType.WARNING);
	            a.setTitle("No Selection");
	            a.setHeaderText("No task selected");
	            a.setContentText("Please select a task to edit.");
	            a.initOwner(stage);
	            a.showAndWait();
	            return;
	        }

	        Stage popup = new Stage();
	        popup.initOwner(stage);
	        popup.initModality(Modality.APPLICATION_MODAL);
	        popup.initStyle(StageStyle.UNDECORATED);
	        popup.setTitle("Edit Task");

	        Label nameLabel = new Label("Name");
	        Label timeLabel = new Label("Time (0.5 steps)");
	        Label prodLabel = new Label("Productivity");

	        TextField nameField = new TextField(selected.getName());
	        TextField timeField =
	                new TextField(String.format("%.1f", selected.getTimeHours()));
	        TextField prodField =
	                new TextField(String.valueOf(selected.getProductivity()));

	        Button save = new Button("Save");
	        Button cancel = new Button("Cancel");

	        save.getStyleClass().add("primary-btn");
	        cancel.getStyleClass().add("secondary-btn");

	        GridPane form = new GridPane();
	        form.getStyleClass().add("popup-card");
	        form.setHgap(16);
	        form.setVgap(18);
	        form.setAlignment(Pos.CENTER);
	        form.setPadding(new Insets(22));

	        nameLabel.getStyleClass().add("form-label");
	        timeLabel.getStyleClass().add("form-label");
	        prodLabel.getStyleClass().add("form-label");

	        nameField.getStyleClass().add("form-input");
	        timeField.getStyleClass().add("form-input");
	        prodField.getStyleClass().add("form-input");

	        nameField.setPrefWidth(320);
	        timeField.setPrefWidth(320);
	        prodField.setPrefWidth(320);

	        form.add(nameLabel, 0, 0);
	        form.add(nameField, 1, 0);

	        form.add(timeLabel, 0, 1);
	        form.add(timeField, 1, 1);

	        form.add(prodLabel, 0, 2);
	        form.add(prodField, 1, 2);

	        HBox buttons = new HBox(12, save, cancel);
	        buttons.setAlignment(Pos.CENTER_RIGHT);
	        form.add(buttons, 1, 3);

	        BorderPane root2 = new BorderPane(form);
	        root2.getStyleClass().add("popup-root");

	        Scene scene = new Scene(root2, 560, 330);
	        scene.getStylesheets().add(
	                getClass().getResource("style.css").toExternalForm());

	        popup.setScene(scene);
	        popup.centerOnScreen();

	        cancel.setOnAction(ev -> popup.close());

	        save.setOnAction(ev -> {

	            String name = nameField.getText().trim();
	            String timeStr = timeField.getText().trim();
	            String prodStr = prodField.getText().trim();

	            StringBuilder errors = new StringBuilder();

	            if (name.isEmpty()) errors.append("• Name is empty\n");

	            if (!timeStr.matches("\\d+(\\.0|\\.5)?")) {
	                errors.append("• Time must be in 0.5 steps\n");
	            }

	            int productivity = -1;
	            try {
	                productivity = Integer.parseInt(prodStr);
	                if (productivity < 0) errors.append("• Productivity must be ≥ 0\n");
	            } catch (NumberFormatException ex) {
	                errors.append("• Productivity must be a number\n");
	            }

	            double hours = 0;
	            if (errors.length() == 0) {
	                hours = Double.parseDouble(timeStr);
	                if (hours < 0.5 || hours > 24) {
	                    errors.append("• Time must be between 0.5 and 24\n");
	                }
	            }

	            if (errors.length() > 0) {
	                Alert a = new Alert(Alert.AlertType.WARNING);
	                a.setTitle("Invalid Input");
	                a.setHeaderText("Fix the following");
	                a.setContentText(errors.toString());
	                a.initOwner(popup);
	                a.showAndWait();
	                return;
	            }

	            int timeUnits = (int) (hours * 2);

	            selected.setName(name);
	            selected.setTime(timeUnits);
	            selected.setProductivity(productivity);

	            refreshTable(tableTasks);
	            popup.close();
	        });

	        popup.showAndWait();
	    });
	    
	    deleteBtn.setOnAction(e->{
	    	Task selected = tableView.getSelectionModel().getSelectedItem();

	        if (selected == null) {
	            Alert a = new Alert(Alert.AlertType.WARNING);
	            a.setTitle("No Selection");
	            a.setHeaderText("No task selected");
	            a.setContentText("Please select a task to delete.");
	            a.initOwner(stage);
	            a.showAndWait();
	            return;
	        }

	        ButtonType confirmDeleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
	        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

	        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	        confirm.initStyle(StageStyle.UNDECORATED);
	        confirm.setTitle("Delete Task");
	        confirm.setHeaderText("Are you sure?");
	        confirm.setContentText(
	                "This will permanently delete:\n\n task: " + selected.getName()+" ,"+selected.getTimeHours()+" hrs ,"
	                		+selected.getProductivity()+" productivity"
	        );

	        confirm.getButtonTypes().setAll(confirmDeleteBtn, cancelBtn);
	        confirm.initOwner(stage);
	        
	        confirm.showAndWait().ifPresent(type -> {
	            if (type == confirmDeleteBtn) {
	                tasks.remove(tasks.getIndex(selected));
	                refreshTable(tableTasks);
	            }
	        });
	    });
	    dpRelationButton.setOnAction(e->{
	    	Stage popup = new Stage();
	        popup.initOwner(stage);
	        popup.initModality(Modality.APPLICATION_MODAL);
	        popup.initStyle(StageStyle.UNDECORATED);

	        Label title = new Label("Dynamic Programming Relation");

	        Label formula = new Label(
	            "dp[t] = max(dp[t], dp[t - w] + p)"
	        );

	        Label explanation = new Label(
	            "For each task:\n" +
	            "• t goes from maxTime → w\n" +
	            "• w = task time\n" +
	            "• p = task productivity\n\n" +
	            "Backward iteration ensures each task \n" +
	            "is chosen at most once.\n(bounded Knapsack)"
	        );

	        Button closeBtn = new Button("Got it");

	        VBox box = new VBox(20, title, formula, explanation, closeBtn);
	        box.setAlignment(Pos.CENTER);
	        box.setPadding(new Insets(30));
	        box.getStyleClass().add("dp-box");

	        Scene scene = new Scene(box);
	        scene.setFill(Color.TRANSPARENT);
	        scene.getStylesheets().add(
	            getClass().getResource("style.css").toExternalForm()
	        );

	        popup.setScene(scene);

	        closeBtn.setOnAction(e1 -> popup.close());

	        popup.showAndWait();
	    });
	    runButton.setOnAction(e -> {
	        new RunScene(stage, tasks,(int) (freeTimeSpinner.getValue()*2));
	    });


	}
	private void refreshTable(ObservableList<Task> tableTasks) {
	    tableTasks.clear();
	    for (int i = 0; i < tasks.size(); i++) {
	        tableTasks.add(tasks.get(i));
	    }
	}



}
