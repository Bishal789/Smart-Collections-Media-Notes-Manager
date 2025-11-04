package app;

import app.model.Item;
import app.model.ItemType;
import app.model.Task;
import app.model.Memento;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class Main extends Application {

    private final ObservableList<Item> library = FXCollections.observableArrayList();
    private final Stack<Item> recentlyViewed = new Stack<>();
    private final Stack<Memento> undoStack = new Stack<>();
    private final Set<String> importedPaths = new HashSet<>();

    private final Map<String, Set<UUID>> keywordIndex = new HashMap<>();
    private final Map<String, Integer> tagFrequency = new HashMap<>();

    private final PriorityQueue<Task> taskQueue = new PriorityQueue<>();

    private ListView<Item> listView;
    private TextField titleField;
    private ComboBox<ItemType> typeBox;
    private TextField pathField;
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private TextField taskDescField;
    private DatePicker taskDeadlinePicker;
    private Spinner<Integer> taskPrioritySpinner;
    private ListView<Task> taskListView;

    private TextField tagsField;
    private Slider ratingSlider;
    private Label createdAtLabel;

    private GridPane detailGrid;

    private final String DATA_FILE = System.getProperty("user.dir") + File.separator + "library.dat";

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        listView = new ListView<>(library);
        listView.setPrefWidth(200);

        titleField = new TextField();
        typeBox = new ComboBox<>(FXCollections.observableArrayList(ItemType.values()));
        pathField = new TextField();
        mediaView = new MediaView();
        mediaView.setFitWidth(400);
        mediaView.setFitHeight(300);
        mediaView.setPreserveRatio(true);

        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button undoBtn = new Button("Undo Delete");
        Button importBtn = new Button("Import Folder");

        taskDescField = new TextField();
        taskDescField.setPromptText("Task Description");
        taskDeadlinePicker = new DatePicker();
        taskPrioritySpinner = new Spinner<>(1, 5, 3);
        Button addTaskBtn = new Button("Add Study Task");

        taskListView = new ListView<>();
        taskListView.setPrefHeight(100);
        updateTaskListView();

        loadLibrary();

        detailGrid = new GridPane();
        detailGrid.setHgap(15);
        detailGrid.setVgap(12);
        detailGrid.setPadding(new Insets(20, 20, 20, 20));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100); col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(150); col2.setPrefWidth(180);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(100); col3.setPrefWidth(120);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setMinWidth(50);  col4.setPrefWidth(100);
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setMinWidth(150); col5.setPrefWidth(200);
        ColumnConstraints col6 = new ColumnConstraints();
        col6.setMinWidth(100); col6.setPrefWidth(120);
        detailGrid.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6);

        Label sectionItem = new Label("Item Details");
        sectionItem.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true;");
        detailGrid.add(sectionItem, 0, 0, 3, 1);
        detailGrid.add(new Label("Title:"), 0, 1);
        detailGrid.add(titleField, 1, 1, 2, 1);
        detailGrid.add(new Label("Type:"), 0, 2);
        detailGrid.add(typeBox, 1, 2, 2, 1);
        detailGrid.add(new Label("Path/URL:"), 0, 3);
        detailGrid.add(pathField, 1, 3, 2, 1);

        tagsField = new TextField();
        tagsField.setPromptText("Tags (comma-separated)");

        ratingSlider = new Slider(1, 5, 3);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setSnapToTicks(true);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);

        createdAtLabel = new Label("N/A");

        detailGrid.add(new Label("Tags:"), 0, 4);
        detailGrid.add(tagsField, 1, 4, 2, 1);
        detailGrid.add(new Label("Rating:"), 0, 5);
        detailGrid.add(ratingSlider, 1, 5, 2, 1);
        detailGrid.add(new Label("Created At:"), 0, 6);
        detailGrid.add(createdAtLabel, 1, 6, 2, 1);

        Label sectionActions = new Label("Actions");
        sectionActions.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        HBox buttonBox = new HBox(12, addBtn, editBtn, deleteBtn, undoBtn, importBtn);
        buttonBox.setPadding(new Insets(5, 0, 5, 0));
        Label sectionTasks = new Label("Study Tasks");
        sectionTasks.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        Button completeTaskBtn = new Button("Complete Task");
        completeTaskBtn.setOnAction(_ -> completeNextTask());

        buttonBox.getChildren().add(completeTaskBtn);

        detailGrid.add(sectionActions, 0, 7, 6, 1);
        detailGrid.add(buttonBox, 0, 8, 6, 1);
        detailGrid.add(sectionTasks, 0, 9, 6, 1);
        detailGrid.add(new Label("Description:"), 0, 10);
        detailGrid.add(taskDescField, 1, 10, 2, 1);
        detailGrid.add(new Label("Deadline:"), 3, 10);
        detailGrid.add(taskDeadlinePicker, 4, 10);
        detailGrid.add(new Label("Priority (1=High):"), 0, 11);
        detailGrid.add(taskPrioritySpinner, 1, 11);
        detailGrid.add(addTaskBtn, 2, 11);
        detailGrid.add(new Label("Task List:"), 0, 12);
        detailGrid.add(taskListView, 1, 12, 5, 1);
        GridPane.setMargin(taskListView, new Insets(5, 0, 0, 0));

        titleField.setPrefWidth(180);
        typeBox.setPrefWidth(120);
        pathField.setPrefWidth(320);
        tagsField.setPrefWidth(180);
        taskDescField.setPrefWidth(180);
        taskDeadlinePicker.setPrefWidth(120);
        taskPrioritySpinner.setPrefWidth(80);
        addTaskBtn.setPrefWidth(120);
        taskListView.setPrefHeight(120);
        mediaView.setFitWidth(400);
        mediaView.setFitHeight(250);

        listView.getSelectionModel().selectedItemProperty().addListener((_, _, newItem) -> {
            if (newItem != null) {
                recentlyViewed.push(newItem);
                titleField.setText(newItem.getTitle());
                typeBox.setValue(newItem.getType());
                pathField.setText(newItem.getPathOrUrl());
                tagsField.setText(String.join(", ", newItem.getTags()));
                ratingSlider.setValue(newItem.getRating());
                createdAtLabel.setText(newItem.getCreatedAt().toString());
                playMedia(newItem.getPathOrUrl(), detailGrid);
                FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(350), detailGrid);
                fade.setFromValue(0.3);
                fade.setToValue(1.0);
                fade.play();
            }
        });

        Label sectionMedia = new Label("Media Preview");
        sectionMedia.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-underline: true;");
        detailGrid.add(sectionMedia, 3, 0, 3, 1);
        detailGrid.add(mediaView, 3, 1, 3, 3);

        addMediaControls(detailGrid);

        addBtn.setOnAction(_ -> addItem());
        editBtn.setOnAction(_ -> editItem());
        deleteBtn.setOnAction(_ -> deleteItem());
        undoBtn.setOnAction(_ -> undoDelete());
        importBtn.setOnAction(_ -> importFolder(stage));
        addTaskBtn.setOnAction(_ -> addStudyTask());

        HBox root = new HBox(20, listView, detailGrid);
        root.setPadding(new Insets(15));
        root.prefHeightProperty().bind(stage.heightProperty());
        listView.prefHeightProperty().bind(stage.heightProperty().subtract(30));
        detailGrid.prefHeightProperty().bind(stage.heightProperty().subtract(30));

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Smart Collections – Media & Notes Manager");
        stage.setMinHeight(600);
        stage.setMaximized(true);
        stage.show();

        stage.setOnCloseRequest(_ -> backupOnExit());
    }

    private void addItem() {
        String title = titleField.getText().trim();
        ItemType type = typeBox.getValue();
        String path = pathField.getText().trim();
        Set<String> tags = new LinkedHashSet<>(Arrays.asList(tagsField.getText().trim().split(",\\s*")));
        int rating = (int) ratingSlider.getValue();
        Instant createdAt = Instant.now();

        if (title.isEmpty() || type == null) {
            showAlert("Error", "Title and Type are required");
            return;
        }

        boolean isDuplicate = library.stream().anyMatch(item ->
            item.getTitle().equalsIgnoreCase(title) &&
            item.getType() == type &&
            Objects.equals(item.getPathOrUrl(), path)
        );

        if (isDuplicate) {
            showAlert("Duplicate Item", "An item with the same title, type, and path already exists.");
            return;
        }

        Item item = new Item(title, type, path.isEmpty() ? null : path, tags, rating, createdAt);
        library.add(item);
        indexItem(item);
        listView.getSelectionModel().select(item);
    }

    private void editItem() {
        Item selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        undoStack.push(new Memento(selected.getId(), new Item(selected), "edit"));

        selected.setTitle(titleField.getText().trim());
        selected.setType(typeBox.getValue());
        selected.setPathOrUrl(pathField.getText().trim());
        selected.setTags(new LinkedHashSet<>(Arrays.asList(tagsField.getText().trim().split(",\\s*"))));
        selected.setRating((int) ratingSlider.getValue());
        listView.refresh();
        playMedia(selected.getPathOrUrl(), detailGrid);
        indexItem(selected);
    }

    private void deleteItem() {
        Item selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        undoStack.push(new Memento(selected.getId(), new Item(selected), "delete"));
        library.remove(selected);
        listView.getSelectionModel().clearSelection();
        mediaView.setMediaPlayer(null);
    }

    private void undoDelete() {
        if (!undoStack.isEmpty()) {
            Memento memento = undoStack.pop();
            if (memento.getAction().equals("delete") || memento.getAction().equals("edit")) {
                Item restored = (Item) memento.getState();
                library.add(restored);
                indexItem(restored);
                showAlert("Undo", "Restored item: " + restored.getTitle());
            }
        } else {
            showAlert("Undo", "No actions to undo.");
        }
    }

    private void playMedia(String pathOrUrl, GridPane detailGrid) {
        if (pathOrUrl == null || pathOrUrl.isEmpty()) {
            mediaView.setMediaPlayer(null);
            return;
        }

        String lowerPath = pathOrUrl.toLowerCase();
        if (!(lowerPath.endsWith(".mp3") || lowerPath.endsWith(".mp4"))) {
            mediaView.setMediaPlayer(null);
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }

            String encodedPath = pathOrUrl.startsWith("http")
                    ? pathOrUrl
                    : "file:///" + java.net.URLEncoder.encode(pathOrUrl, "UTF-8").replace("%2F", "/").replace("+", "%20");

            Media media = new Media(encodedPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setOnError(() -> mediaView.setMediaPlayer(null));

            addMediaControls(detailGrid);
        } catch (Exception e) {
            mediaView.setMediaPlayer(null);
        }
    }

    private void addMediaControls(GridPane detailGrid) {
        Button playButton = new Button("▶");
        Button pauseButton = new Button("⏸");
        Button stopButton = new Button("⏹");

        Slider mediaSlider = new Slider();
        mediaSlider.setMin(0);
        mediaSlider.setValue(0);
        mediaSlider.setDisable(true);

        HBox mediaControls = new HBox(10, playButton, pauseButton, stopButton, mediaSlider);
        mediaControls.setPadding(new Insets(10, 0, 0, 0));

        playButton.setOnAction(_ -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
                mediaPlayer.currentTimeProperty().addListener((_, _, newTime) -> {
                    if (!mediaSlider.isValueChanging()) {
                        mediaSlider.setValue(newTime.toSeconds());
                    }
                });

                mediaPlayer.setOnReady(() -> {
                    mediaSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                    mediaSlider.setDisable(false);
                });
            } else {
                showAlert("Media Error", "No media loaded.");
            }
        });

        pauseButton.setOnAction(_ -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            } else {
                showAlert("Media Error", "No media loaded.");
            }
        });

        stopButton.setOnAction(_ -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaSlider.setValue(0);
            } else {
                showAlert("Media Error", "No media loaded.");
            }
        });

        mediaSlider.valueChangingProperty().addListener((_, _, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(javafx.util.Duration.seconds(mediaSlider.getValue()));
            }
        });

        detailGrid.add(mediaControls, 3, 4, 3, 1);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void importFolder(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        File folder = chooser.showDialog(stage);
        if (folder != null && folder.isDirectory()) {
            scanFolder(folder);
        }
    }

    private void scanFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("Could not access folder: " + folder.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file);
            } else {
                String path = file.getAbsolutePath();
                if (importedPaths.contains(path)) {
                    System.out.println("Skipped duplicate file: " + path);
                    continue;

                }

                String lower = path.toLowerCase();
                ItemType type = null;
                if (lower.endsWith(".txt") || lower.endsWith(".md")) type = ItemType.NOTE;
                else if (lower.endsWith(".pdf")) type = ItemType.PDF;
                else if (lower.endsWith(".mp3")) type = ItemType.AUDIO;
                else if (lower.endsWith(".mp4")) type = ItemType.VIDEO;

                if (type != null) {
                    Item item = new Item(file.getName(), type, path);
                    library.add(item);
                    importedPaths.add(path);
                    indexItem(item);
                } else {
                    System.out.println("Skipped unsupported file: " + path);
                }
            }
        }
    }

    private void indexItem(Item item) {
        String[] words = item.getTitle().split("\\s+");
        for (String word : words) {
            word = word.toLowerCase();
            keywordIndex.computeIfAbsent(word, _ -> new HashSet<>()).add(item.getId());
        }

        String typeTag = item.getType().name();
        tagFrequency.put(typeTag, tagFrequency.getOrDefault(typeTag, 0) + 1);

        for (String tag : item.getTags()) {
            tag = tag.toLowerCase();
            tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
        }
    }

    private void addStudyTask() {
        Item selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select an item first");
            return;
        }
        String desc = taskDescField.getText().trim();
        java.time.LocalDate deadlineDate = taskDeadlinePicker.getValue();
        int priority = taskPrioritySpinner.getValue();
        if (desc.isEmpty() || deadlineDate == null) {
            showAlert("Error", "Task description and deadline required");
            return;
        }
        Instant deadline = deadlineDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        Task task = new Task(selected.getId(), desc, deadline, priority);
        taskQueue.add(task);
        updateTaskListView();
        showAlert("Task Added", "Next task: " + taskQueue.peek());
        taskDescField.clear();
        taskDeadlinePicker.setValue(null);
        taskPrioritySpinner.getValueFactory().setValue(3);
    }

    private void updateTaskListView() {
        List<Task> tasks = new ArrayList<>(taskQueue);
        tasks.sort(Task::compareTo);
        taskListView.getItems().setAll(tasks);

        taskListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Priority: %d, Deadline: %s)",
                            task.getDescription(),
                            task.getPriority(),
                            task.getDeadline().atZone(java.time.ZoneId.systemDefault()).toLocalDate()));
                }
            }
        });

        FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(350), taskListView);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.play();
    }

    private void completeNextTask() {
        if (taskQueue.isEmpty()) {
            showAlert("No Tasks", "There are no tasks to complete.");
            return;
        }

        Task completedTask = taskQueue.poll();
        updateTaskListView();
        showAlert("Task Completed", "Completed task: " + completedTask.getDescription());
    }

    private void saveLibrary() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeUTF("LIBRARY_V1");
            oos.writeObject(new ArrayList<>(library));
            oos.writeObject(new HashMap<>(keywordIndex));
            oos.writeObject(new HashMap<>(tagFrequency));
            oos.writeObject(new PriorityQueue<>(taskQueue));
            System.out.println("Library saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            showAlert("Save Error", "Could not save library: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadLibrary() {
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            System.out.println("Library file not found. Starting with an empty library.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            String header = ois.readUTF();
            if (!"LIBRARY_V1".equals(header)) {
                System.err.println("Unsupported library version: " + header);
                return;
            }
            library.setAll((List<Item>) ois.readObject());
            keywordIndex.putAll((Map<String, Set<UUID>>) ois.readObject());
            tagFrequency.putAll((Map<String, Integer>) ois.readObject());
            taskQueue.addAll((PriorityQueue<Task>) ois.readObject());
            System.out.println("Library loaded successfully from " + DATA_FILE);
        } catch (OptionalDataException e) {
            System.err.println("Corrupted library file. Starting with an empty library.");
            library.clear();
            keywordIndex.clear();
            tagFrequency.clear();
            taskQueue.clear();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading library: " + e.getMessage());
            e.printStackTrace();
        }

        listView.setItems(library);
        listView.refresh();
    }

    private void backupOnExit() {
        saveLibrary();
        showAlert("Backup", "Library saved to " + DATA_FILE);
    }
}
