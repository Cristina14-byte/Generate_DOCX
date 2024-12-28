package org.cristina.generaredocsrl;
import org.cristina.generaredocsrl.Utility;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminMenuController {

    @FXML
    private Button backButton;
    @FXML
    private Button seeClientDataButton;
    @FXML
    private Button srlDBButton;

    public void srlDBButtonOnAction() {
        Utility.navigateToView("srlDB-view.fxml", srlDBButton);
    }

    public void seeClientDataButtonOnAction(){
        Utility.navigateToView("clientsDB-view.fxml", seeClientDataButton);
    }

    public void backButtonOnAction(ActionEvent e) {
        Utility.navigateToView("login-view.fxml", backButton);
    }
}
