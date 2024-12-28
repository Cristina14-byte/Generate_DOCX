package org.cristina.generaredocsrl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ClientMenuController {

    @FXML
    private Button accountDataButton;
    @FXML
    private Button createDocButton;
    @FXML
    private Button backButton;

    public void createDocButtonOnAction(ActionEvent event) {
        Utility.navigateToView("chooseFolder-view.fxml", createDocButton);
    }

    public void accountDataButtonOnAction(ActionEvent event) {
        Utility.navigateToView("accountData-view.fxml", accountDataButton);
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("login-view.fxml", backButton);
    }


}
