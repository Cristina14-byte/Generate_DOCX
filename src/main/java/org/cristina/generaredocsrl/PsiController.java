package org.cristina.generaredocsrl;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.cristina.generaredocsrl.model.ClientData;



public class PsiController {

    @FXML
    private TextField psiRespTextField;
    @FXML
    private Button nextPageButton;
    @FXML
    private Button backButton;

    public static ClientData clientData = ChooseFolderController.clientData;

    public void nextPageButtonOnAction(){
        String psiResp = psiRespTextField.getText();
        if(psiResp.isEmpty()){
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Trebuie să completaţi responsabilul PSI!");
            return;
        }

        clientData.setPsiResponsible(psiResp);
        Utility.navigateToView("saveFolder-view.fxml", nextPageButton);
    }

    public void backButtonOnAction() {
        if (clientData.isSsm()) {
            Utility.navigateToView("ssm-view.fxml", backButton);
        }
        else{
            Utility.navigateToView("chooseFolder-view.fxml", backButton);
        }
    }
}
