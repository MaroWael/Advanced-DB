package org.example.ums.ui;

import org.example.ums.dao.JpaUtil;
import org.example.ums.service.DataBootstrapService;

import javafx.application.Application;
import javafx.stage.Stage;

public class UmsFxApplication extends Application {

    private final DataBootstrapService dataBootstrapService = new DataBootstrapService();

    @Override
    public void start(Stage stage) {
        dataBootstrapService.seedIfEmpty();
        SceneNavigator.init(stage);
        SceneNavigator.switchTo("/org/example/ums/ui/login-view.fxml", "University Management System");
        stage.setOnCloseRequest(event -> JpaUtil.shutdown());
    }
}

