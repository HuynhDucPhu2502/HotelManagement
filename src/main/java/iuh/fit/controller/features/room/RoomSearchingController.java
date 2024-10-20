package iuh.fit.controller.features.room;

import iuh.fit.dao.RoomCategoryDAO;
import iuh.fit.dao.RoomDAO;
import iuh.fit.models.Room;
import iuh.fit.models.enums.RoomStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoomSearchingController {

    // Search Fields
    @FXML
    private TextField roomIDSearchField;
    @FXML
    private ComboBox<String> roomStatusSearchField;
    @FXML
    private DatePicker dateOfCreationLowerBoundSearchField;
    @FXML
    private DatePicker dateOfCreationUpperBoundSearchField;
    @FXML
    private ComboBox<String> roomCategorySearchField;

    // Table
    @FXML
    private TableView<Room> roomTableView;
    @FXML
    private TableColumn<Room, String> roomIDColumn;
    @FXML
    private TableColumn<Room, String> roomStatusColumn;
    @FXML
    private TableColumn<Room, LocalDateTime> dateOfCreationColumn;
    @FXML
    private TableColumn<Room, String> roomCategoryColumn;

    // Buttons
    @FXML
    private Button searchBtn;
    @FXML
    private Button resetBtn;

    private ObservableList<Room> items;

    public void initialize() {
        loadData();
        setupTable();

        searchBtn.setOnAction(e -> handleSearchAction());
        resetBtn.setOnAction(e -> handleResetAction());
    }

    private void loadData() {
        List<Room> roomList = RoomDAO.getRoom();
        items = FXCollections.observableArrayList(roomList);
        roomTableView.setItems(items);
        roomTableView.refresh();

        List<String> comboBoxItems = RoomCategoryDAO.getRoomCategory()
                .stream()
                .map(roomCategory -> roomCategory.getRoomCategoryID() +
                        " " + roomCategory.getRoomCategoryName()
                )
                .collect(Collectors.toList());
        comboBoxItems.addFirst("KHÔNG CÓ");
        comboBoxItems.addFirst("TẤT CẢ");

        ObservableList<String> observableComboBoxItems = FXCollections.observableArrayList(comboBoxItems);
        roomCategorySearchField.getItems().setAll(observableComboBoxItems);

        roomStatusSearchField.getItems().setAll(
                Stream.of(RoomStatus.values()).map(Enum::name).toList()
        );
        roomStatusSearchField.getSelectionModel().selectFirst();

        if (!roomCategorySearchField.getItems().isEmpty())
            roomCategorySearchField.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        roomIDColumn.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("roomStatus"));
        dateOfCreationColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfCreation"));
        roomCategoryColumn.setCellValueFactory(data -> {
            String categoryName = data.getValue().getRoomCategory().getRoomCategoryName();
            return new SimpleStringProperty(categoryName != null ? categoryName : "KHÔNG CÓ");
        });

        roomTableView.setItems(items);
    }

    private void handleResetAction() {
        roomIDSearchField.setText("");
        roomStatusSearchField.getSelectionModel().clearSelection();
        dateOfCreationLowerBoundSearchField.setValue(null);
        dateOfCreationUpperBoundSearchField.setValue(null);
        roomCategorySearchField.getSelectionModel().clearSelection();

        loadData();
    }

    private void handleSearchAction() {
        String roomID = roomIDSearchField.getText().isBlank() ? null : roomIDSearchField.getText().trim();
        String roomStatus = roomStatusSearchField.getSelectionModel().getSelectedItem();
        LocalDateTime lowerDate = handleDateInput(dateOfCreationLowerBoundSearchField.getValue());
        LocalDateTime upperDate = handleDateInput(dateOfCreationUpperBoundSearchField.getValue());
        String selectedCategory = roomCategorySearchField.getSelectionModel().getSelectedItem();
        String categoryID = handleCategoryIDInput(selectedCategory);

        List<Room> searchResults = RoomDAO.searchRooms(roomID, roomStatus, lowerDate, upperDate, categoryID);
        items.setAll(searchResults);
        roomTableView.setItems(items);
    }

    private LocalDateTime handleDateInput(LocalDate date) {
        return (date != null) ? date.atStartOfDay() : null;
    }

    private String handleCategoryIDInput(String selectedCategory) {
        if (selectedCategory == null) return null;
        if (selectedCategory.equalsIgnoreCase("TẤT CẢ")) return "ALL";
        if (selectedCategory.equalsIgnoreCase("KHÔNG CÓ")) return "NULL";
        return selectedCategory.split(" ")[0];
    }
}
