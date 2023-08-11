package AlchemyElementAdder;

import Alchemy.Element;
import Alchemy.ElementHandler;
import Alchemy.Reaction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ElementPopUp {

    @FXML private TextField name;
    @FXML private CheckBox isFinal;
    @FXML private TextField parent1;
    @FXML private TextField parent2;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;
    @FXML private Label idLabel;
    @FXML private Button addReactionButton;
    @FXML private Button removeReactionButton;
    @FXML private ChoiceBox parentDropDown;
    private TreeItem<String> treeItem;
    private TreeView<String> treeView;
    private static ElementHandler elementHandler;
    private ArrayList<Element[]> parentList = new ArrayList<Element[]>();
    private Element element;

    public ElementPopUp(){
    }


    @FXML
    public void initialize(){
        parentDropDown.getItems().add("");
        parentDropDown.setValue("");
        parentDropDown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (number2.intValue() <= 0) {
                    return;
                } else {
                    Element[] elements = parentList.get(number2.intValue() - 1);
                    parent1.setText(elements[0].getName());
                    parent2.setText(elements[1].getName());
                }
            }});
        cancelButton.setCancelButton(true);
        saveButton.setDefaultButton(true);
        saveButton.setOnAction((event) -> {
            saveStage();
        });
        cancelButton.setOnAction((event) -> {
            closeStage();
        });
        addReactionButton.setOnAction((event) -> {
            addReaction();
        });
        removeReactionButton.setOnAction((event) -> {
            removeReaction();
        });
    }

    public void setElementHandler(ElementHandler eh){
        elementHandler = eh;
        setIdText(elementHandler.getElementSize() + "");
    }
    public void setTreeItems(TreeItem<String> treeItem, TreeView<String> treeView){
        this.treeItem = treeItem;
        this.treeView = treeView;
    }
    public void setElement(Element element){
        this.element = element;
        name.setText(element.getName());
        setIdText(element.getId() + "");
        for (Reaction r : element.getParents()){
            parentList.add(r.getReactants().toArray(new Element[2]));
        }
        if (parentList.size() > 0){
            parentDropDown.getItems().addAll(Arrays.stream(parentList.toArray(new Element[0][0])).map(e1 -> e1[0] + " + " + e1[1]).collect(Collectors.toList()));
            parentDropDown.setValue(String.join(" + ", Arrays.stream(parentList.get(0)).map(Element::getName).collect(Collectors.toList())));
        }
    }

    private void saveStage(){
        if (name.getText().isEmpty()){
            setErrorText("Please enter a name!");
        } else if (!name.getText().chars().mapToObj(i -> Character.valueOf((char) i)).allMatch(character -> Character.isAlphabetic(character) || Character.isSpaceChar(character))) {
            setErrorText("Element names can only have letters!");
        } else if (element != null) {
            element.setName(name.getText());
            element.setFinal(isFinal.isSelected());
            element.clearParents();
            System.out.println(Arrays.deepToString(parentList.toArray()));
            for (Element[] reactants: parentList) {
                try{
                    Reaction r = elementHandler.getReaction(new ArrayList<>(Arrays.asList(reactants)));
                    if (r.getProducts().contains(element)){
                        element.addParent(r);
                    } else {
                        r.addProduct(element);
                        element.addParent(r);
                        System.out.println("hi");
                    }
                } catch (NullPointerException npe) {
                    Reaction r = new Reaction(new ArrayList(Arrays.asList(reactants)), new ArrayList<>(Arrays.asList(element)));
                    System.out.println(r);
                    element.addParent(r);
                }
            }
            closeStage();
        } else if (elementHandler.getElement(name.getText()) != null){
            setErrorText("This Element Name has already been taken!");
        } else if (parentList.size() <= 0){
            setErrorText("You must have at least 1 parent reaction!");
        } else {
            Element e = new Element(name.getText(), isFinal.isSelected(), elementHandler.getElementSize() - 1);
            for (Element[] reactants: parentList) {
                try{
                    Reaction r = elementHandler.getReaction(getParentElements());
                    System.out.println(r);
                    r.addProduct(e);
                } catch (NullPointerException npe) {
                    Reaction r = new Reaction(new ArrayList(Arrays.asList(reactants)), new ArrayList<>(Arrays.asList(e)));
                    e.addParent(r);
                }
            }
            elementHandler.getElementClass(treeItem.getValue()).addElement(e);
            e.setElementClass(elementHandler.getElementClass(treeItem.getValue()));
            elementHandler.addElement(e);
            closeStage();
        }
    }

    private void addReaction() {
        if (parent1.getText().isEmpty() || parent2.getText().isEmpty()) {
            setErrorText("2 Parents required!");
        } else if (elementHandler.getElement(parent1.getText()) == null) {
            setErrorText("Not able to add Reaction: Invalid Parent 1!");
        } else if (elementHandler.getElement(parent2.getText()) == null) {
            setErrorText("Not able to add Reaction: Invalid Parent 2!");
        } else if (getParentElements().contains(element)) {
            setErrorText("Not able to add Reaction: Parent cannot be child!");
        } else if (parentList.stream().anyMatch(r -> Arrays.asList(r).stream().sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList()).equals(getParentElements().stream().sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList())))){
            setErrorText("This reaction already exists!");
        } else {
            int selected = parentDropDown.getSelectionModel().getSelectedIndex();
            if(selected > 0){
                System.out.println(selected);
                parentList.set(selected  - 1, getParentElements().toArray(new Element[2]));
                parentDropDown.getItems().set(selected, parent1.getText() + " + " + parent2.getText());
            } else {
                parentList.add(getParentElements().toArray(new Element[2]));
                System.out.println(Arrays.deepToString(parentList.toArray()));
                parentDropDown.getItems().add(parent1.getText() + " + " + parent2.getText());
            }
            parentDropDown.setValue("");
            parent1.clear();
            parent2.clear();
        }
    }
    private void removeReaction(){
        int selected = parentDropDown.getSelectionModel().getSelectedIndex();
        if(selected > 0){
            System.out.println(selected);
            parentList.remove(selected - 1);
            parentDropDown.getItems().remove(selected);
        }
    }


    private void closeStage() {
        ElementAdder.refreshDisplay(treeView);
        elementHandler.refresh();
        elementHandler.saveMaster();
        cancelButton.getScene().getWindow().hide();
    }

    public void setIdText(String str){
        idLabel.setText(str);
    }

    public void setErrorText(String str){
        errorLabel.setText(str);
    }

    private ArrayList<Element> getParentElements(){
        return new ArrayList<>(Arrays.asList(elementHandler.getElement(parent1.getText()), elementHandler.getElement(parent2.getText())));
    }

}
