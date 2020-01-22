package geocoder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {

    @FXML
    public Label howToLabel;
    @FXML
    private TextField inputField;
    @FXML
    private TextField outputField;
    @FXML
    private ListView locationListView;
    @FXML
    private Button codingBtn;

    private ObservableList<Locations> locationsObservableList;

    private File inputFile, outputFile;

    private Stage mainStage;
    private boolean isCtrlPressed, isShiftPressed;

    public void initialize() throws IOException {
        locationsObservableList = FXCollections.observableArrayList();
        isCtrlPressed = false;
        isShiftPressed = false;

        initHowTo();

        inputField.textProperty().addListener((observableValue, s, t1) -> {
            if(inputField.getTooltip() != null){
                inputField.getStyleClass().remove("error");
                inputField.setTooltip(null);
            }
        });

        locationListView.setCellFactory(param -> new ListCell<Locations>(){
            @Override
            protected void updateItem(Locations item, boolean empty){
                super.updateItem(item, empty);

                if(empty || item == null || item.getAddress() == null){
                    setText(null);
                }else {
                    setText("Adres: " + item.getAddress() + " Szerokość: " + item.getLatitude() + " Długość: " + item.getLongitude());
                }
            }
        });

        locationListView.setItems(locationsObservableList);

        locationListView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(!isCtrlPressed && !isShiftPressed)
                    locationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                if(isShiftPressed && isCtrlPressed){

                }
            }
        });

        inputFile = new File("input.txt");
        if(inputFile.exists()) {
            inputField.setText(inputFile.getAbsolutePath());
            codingBtn.setDisable(false);
        }
        outputFile = new File("output.txt");
        outputField.setText(outputFile.getAbsolutePath());
    }

    private void initHowTo() throws IOException {
        InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/resources/HowTo.txt"));
        BufferedReader reader = new BufferedReader(in);
        String howTo = "";
        String temp;
        while((temp = reader.readLine()) != null){
            if(!temp.startsWith("4."))
                howTo += temp +"\n";
            else
                howTo += temp;
        }
        reader.close();
        howToLabel.setText(howTo);
    }

    public void inputBtnReleased(MouseEvent mouseEvent) {
        File oldFile = new File(inputField.getText());
        inputFile = openFileChooser(0, mainStage);

        if(inputFile == null) {
            if(!oldFile.exists()){
                inputField.getStyleClass().add("error");
                inputField.setTooltip(new Tooltip("Nie wskazałeś pliku."));
                codingBtn.setDisable(true);
            }else{
                inputFile = oldFile;
            }
        }else{
            inputField.setText(inputFile.getAbsolutePath());
            codingBtn.setDisable(false);
        }
    }

    public void outputBtnReleased(MouseEvent mouseEvent) {
        outputFile = openFileChooser(1, mainStage);
        if(outputFile == null){
            outputFile = new File(outputField.getText());
        }
        outputField.setText(outputFile.getAbsolutePath());
    }

    public void removeBtnReleased(MouseEvent mouseEvent) {
        List <Integer> indices = locationListView.getSelectionModel().getSelectedIndices();
        for (int i = indices.size()-1; i>=0; i--){
            locationsObservableList.remove(indices.get(i).intValue());
        }
        locationListView.getSelectionModel().clearSelection();
        locationListView.requestFocus();
    }

    public void clearBtnReleased(MouseEvent mouseEvent) {
        locationsObservableList.clear();
    }

    public void saveBtnReleased(MouseEvent mouseEvent) throws IOException {

        if(outputFile.exists()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Plik już istnieje!");
            alert.setHeaderText("W wybranej lokalizacji istnieje już plik o podanej nazwie");
            alert.setContentText("Wybierz co chcesz zrobić.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/icon.png")));

            List<ButtonType> buttonList = new ArrayList<>();
            buttonList.add(new ButtonType("Nadpisz", ButtonBar.ButtonData.OK_DONE));
            buttonList.add(new ButtonType("Zmień nazwę/lokalizację"));
            buttonList.add(new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE));

            alert.getButtonTypes().setAll(buttonList);

            Optional<ButtonType> result = alert.showAndWait();

            switch (buttonList.indexOf(result.get())){
                case 0:
                    break;
                case 1:
                    openFileChooser(1,mainStage);
                    break;
                case 2:
                    return;
                default:
                    return;
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
            for (Locations k : locationsObservableList) {
                bufferedWriter.write(k.getAddress() + " " + k.getLatitude() + " " + k.getLongitude());
                if (locationsObservableList.size() != locationsObservableList.indexOf(k) + 1) {
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File openFileChooser(int option, Stage mainStage){

        FileChooser fileChooser = new FileChooser();

        switch (option){
            case 0:
                fileChooser.setTitle("Podaj lokalizację pliku wejściowego");
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("plik tekstowy", "*.txt"));
                return fileChooser.showOpenDialog(mainStage);
            case 1:
                fileChooser.setTitle("Podaj lokalizację dla pliku wynikowego");
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("plik tekstowy", "*.txt"));
                return fileChooser.showSaveDialog(mainStage);
        }
        return new File("");
    }

    public void setMainStage(Stage primaryStage) {
        this.mainStage = primaryStage;
    }

    public void codingBtnReleased(MouseEvent mouseEvent) throws IOException {
        try( BufferedReader read = new BufferedReader(new FileReader(inputFile))) {
            String address;
            while((address = read.readLine()) != null){
                locationsObservableList.add(new Locations(address));
            }
            locationListView.getSelectionModel().clearSelection();
            locationListView.requestFocus();
        } catch (FileNotFoundException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.CONTROL)){
            locationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            isCtrlPressed = true;
        }
        if(keyEvent.getCode().equals(KeyCode.SHIFT)){
            locationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            isShiftPressed = true;
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.CONTROL)){
            isCtrlPressed = false;
        }
        if(keyEvent.getCode().equals(KeyCode.SHIFT)){
            isShiftPressed = false;
        }
    }
}