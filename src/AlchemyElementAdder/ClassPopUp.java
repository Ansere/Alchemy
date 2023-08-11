package AlchemyElementAdder;

import Alchemy.Element;
import Alchemy.ElementClass;
import Alchemy.ElementHandler;
import Alchemy.Reaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassPopUp {

    @FXML private TextField name;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;
    @FXML private Label idLabel;
    private static ElementHandler elementHandler;
    private ElementClass elementClass;
    private TreeItem<String> treeItem;
    private TreeView<String> treeView;

    public ClassPopUp(){

    }

    @FXML
    public void initialize(){
        cancelButton.setCancelButton(true);
        saveButton.setDefaultButton(true);
        saveButton.setOnAction((event) -> {
            saveStage();
        });
        cancelButton.setOnAction((event) -> {
            closeStage();
        });
    }

    public void setElementHandler(ElementHandler eh){
        elementHandler = eh;
        setIdText(elementHandler.getElementClasses().size() + "");
    }
    public void setTreeItems(TreeItem<String> treeItem, TreeView<String> treeView){
        this.treeItem = treeItem;
        this.treeView = treeView;
    }
    public void setElementClass(ElementClass elementClass){
        this.elementClass = elementClass;
        name.setText(elementClass.getName());
        setIdText(elementClass.getId() + "");
    }

    public void setIdText(String s){
        idLabel.setText(s);
    }
    public void setErrorText(String s){
        errorLabel.setText(s);
    }

    public void saveStage(){
        if (name.getText().isEmpty()){
            setErrorText("Please enter a name!");
        } else if (!name.getText().chars().mapToObj(i -> Character.valueOf((char) i)).allMatch(character -> Character.isAlphabetic(character) || Character.isSpaceChar(character))) {
            setErrorText("Class names can only have letters!");
        } else if (elementClass != null) {
            elementClass.setName(name.getText());
            closeStage();
        } else if (elementHandler.getElementClass(name.getText()) != null){
            setErrorText("This Class Name has already been taken!");
        } else {
            System.out.println("hi");
            ElementClass ec = new ElementClass(name.getText(), Integer.parseInt(idLabel.getText()), Color.RED);
            elementHandler.addElementClass(ec);
            closeStage();
        }
    }
    public void closeStage(){
        ElementAdder.refreshDisplay(treeView);
        elementHandler.refresh();
        elementHandler.saveMaster();
        cancelButton.getScene().getWindow().hide();
    }


}
