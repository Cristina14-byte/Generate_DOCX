package org.cristina.generaredocsrl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.cristina.generaredocsrl.model.ClientData;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SsmController {

    @FXML
    private Button nextPageButton;
    @FXML
    private Button backButton;
    @FXML
    private ListView<String> jobListView;
    @FXML
    private TextField searchBar;

    private ObservableList<String> jobNames = FXCollections.observableArrayList();
    boolean psi = ChooseFolderController.clientData.isPsi();

    public static ClientData clientData = ChooseFolderController.clientData;

    private static final String SSM_PATH = "D:\\Generare_Documente\\Materiale\\FUNCTII SSM";

    public void initialize() {
        loadJobs();
        jobListView.setItems(jobNames);
        jobListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void loadJobs() {
        try {
            Stream<Path> paths = java.nio.file.Files.walk(Path.of(SSM_PATH), 1);
            List<String> folders = paths
                    .filter(java.nio.file.Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> !name.equals("FUNCTII SSM"))
                    .collect(Collectors.toList());
            jobNames.setAll(folders);
        } catch (Exception e) {
            e.printStackTrace();
            Utility.showAlert(Alert.AlertType.ERROR, "Eroare!", "Eroare încărcare funcţii!");
        }
    }

    public void saveSelectedFolders() {
        List<String> selectedFolders = jobListView.getSelectionModel().getSelectedItems();
        clientData.setSelectedJobs(selectedFolders);
        if (selectedFolders.isEmpty()) {
            Utility.showAlert(Alert.AlertType.WARNING, "Atenţie!", "Nu aţi selectat nicio funcţie!");
            return;
        }

        Utility.navigateToView(psi ? "psi-view.fxml" : "saveFolder-view.fxml", nextPageButton);
    }

    public void filterJobs() {
        String filterText = searchBar.getText().toLowerCase();
        if (filterText.isEmpty()) {
            jobListView.setItems(jobNames);
        } else {
            ObservableList<String> filteredJobs = jobNames.stream()
                    .filter(job -> job.toLowerCase().contains(filterText))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            jobListView.setItems(filteredJobs);
        }
    }

    public void backButtonOnAction(ActionEvent event) {
        Utility.navigateToView("chooseFolder-view.fxml", backButton);
    }

}

