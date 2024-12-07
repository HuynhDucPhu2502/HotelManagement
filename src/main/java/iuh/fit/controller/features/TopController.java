package iuh.fit.controller.features;

import iuh.fit.controller.MainController;
import iuh.fit.models.Account;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TopController {
    @FXML
    private Label clockLabel;
    @FXML
    private Button logoutBtn;
    @FXML
    private AnchorPane buttonPanel;
    @FXML
    private ImageView img;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Account currentAccount;
    private MainController mainController;

    private NotificationButtonController topBarController;

    @FXML
    public NotificationButtonController initialize(Account account, MainController mainController) {
        setupContext(account, mainController);
        // clock
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
        timeline.setCycleCount(Timeline.INDEFINITE); //
        timeline.play();

        initializeNotificationButton();
        handleTooltips();
        handleButtons();

        // logout
        return topBarController;
    }

    public void setupContext(Account account, MainController mainController) {
        this.currentAccount = account;
        this.mainController = mainController;
    }

    private void updateClock() {
        String currentTime = LocalTime.now().format(timeFormatter);
        clockLabel.setText(currentTime);
    }

    public void logout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/iuh/fit/view/ui/LoginUI.fxml"));
            AnchorPane loginPane = fxmlLoader.load();

            Stage currentStage = (Stage) (logoutBtn.getScene().getWindow());

            Scene loginScene = new Scene(loginPane);

            currentStage.setScene(loginScene);

            currentStage.setResizable(false);
            currentStage.setWidth(610);
            currentStage.setHeight(400);
            currentStage.setMaximized(false);
            currentStage.centerOnScreen();

            currentStage.show();
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTooltips() {
        Tooltip tooltip = new Tooltip("Đăng xuất");
        Tooltip.install(logoutBtn, tooltip);

        logoutBtn.setTooltip(tooltip);
        tooltip.setShowDelay(javafx.util.Duration.millis(400));
    }

    private void handleButtons() {
        // Tạo hiệu ứng khi hover
        ColorAdjust hoverEffect = new ColorAdjust();
        hoverEffect.setBrightness(-0.2); // Làm màu đậm hơn

        ColorAdjust hoverEffect2 = new ColorAdjust();
        hoverEffect2.setBrightness(-0.5); // Làm màu đậm hơn

        // Khi hover vào button
        logoutBtn.setOnMouseEntered(event -> img.setEffect(hoverEffect));

        // Khi rời chuột khỏi button
        logoutBtn.setOnMouseExited(event -> img.setEffect(null));

        logoutBtn.setOnMousePressed(event -> img.setEffect(hoverEffect2));

        logoutBtn.setOnMouseReleased(event -> img.setEffect(null));

        // logout
        logoutBtn.setOnAction(e -> logout());
    }

    public void initializeNotificationButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iuh/fit/view/features/NotificationButton.fxml"));
            AnchorPane buttonLayout = loader.load();

            NotificationButtonController notificationButtonController = loader.getController();
            topBarController = notificationButtonController.initialize(currentAccount);
            buttonPanel.getChildren().clear();
            buttonPanel.getChildren().addAll(buttonLayout.getChildren());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
