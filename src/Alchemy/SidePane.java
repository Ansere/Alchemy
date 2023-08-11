package Alchemy;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class SidePane {

    private Pane pane;
    private int viewedClass; //-1 signifies home view
    private int selectedElement;
    private int selectedClass;
    private ElementHandler elementHandler;
    private VBox vb;

    public SidePane(){
        viewedClass = -1;
        selectedElement = 0;
        selectedClass = 0;
        pane = new Pane();
        pane.setPrefHeight(400.0);
        pane.setPrefWidth(150.0);
        initialize();
    }

    public void setElementHandler(ElementHandler elementHandler) {
        this.elementHandler = elementHandler;
    }

    public void initialize(){
        vb = new VBox(5);
        vb.prefWidthProperty().bind(pane.widthProperty());
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY()/40;
                int size;
                if (viewedClass < 0){
                    size = elementHandler.getOwnedElementClasses().size();
                } else {
                    size = elementHandler.getOwnedElementsByClass(elementHandler.getElementClass(viewedClass)).size();
                }
                if (selectedElement - deltaY < 0){
                    selectedElement = 0;
                } else if (selectedElement - deltaY >= size){
                    selectedElement = size - 1;
                } else {
                    selectedElement -= deltaY;
                }
                refreshDisplay();
            }
        });
        pane.setStyle("-fx-background-color: gray;");
        pane.getChildren().add(vb);
    }

    public Label getTitleLabel(){
        if (viewedClass < 0){
            Label label = new Label("Classes");
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
            label.setMinHeight(50);
            label.setMinWidth(150);
            label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
            return label;
        } else {
            ElementClass ec = elementHandler.getElementClass(viewedClass);
            Label label = new Label(ec.getName());
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
            label.setMinHeight(50);
            label.setMinWidth(150);
            label.setStyle("-fx-background-color: rgb(" + (255 - ec.getColor().getRed()) + "," + (255 - ec.getColor().getGreen()) + "," + (255 - ec.getColor().getBlue()) + "); -fx-border-color: black; -fx-border-width: 0 0 1 0;");
            label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    viewedClass = -1;
                    selectedElement = selectedClass;
                    refreshDisplay();
                }
            });
            return label;
        }
    }

    public ArrayList<Label> getDisplay(){
        ArrayList<Label> labelList = new ArrayList<>();
        labelList.add(getTitleLabel());
        for (int i = selectedElement; i < 2; i++){
            Label emptyLabel = new Label();
            emptyLabel.setMinHeight(50);
            emptyLabel.setMinWidth(100);
            emptyLabel.setStyle("-fx-background-color: gray;");
            labelList.add(emptyLabel);
            vb.setMargin(emptyLabel, new Insets(5));
        }
        if (viewedClass < 0){
            for (int i = Math.max(0, selectedElement - 2); i <= Math.min(selectedElement + 2, elementHandler.getOwnedElementClasses().size() - 1); i++){
                ElementClass ec = elementHandler.getOwnedElementClasses().get(i);
                Label label = new Label(ec.getName());
                label.setStyle("-fx-background-color: rgb(" + (255 - ec.getColor().getRed()) + "," + (255 - ec.getColor().getGreen()) + "," + (255 - ec.getColor().getBlue()) + ");");
                label.setMinHeight(50);
                label.setMinWidth(100);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setAlignment(Pos.CENTER);
                int finalI = ec.getId();
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        viewedClass = finalI;
                        selectedClass = finalI;
                        selectedElement = 0;
                        refreshDisplay();
                    }
                });
                labelList.add(label);
                vb.setMargin(label, new Insets(5));
            }
        }else {
            ElementClass ec = elementHandler.getElementClass(viewedClass);
            ArrayList<Element> ownedElements = elementHandler.getOwnedElementsByClass(ec);
            for (int i = Math.max(0, selectedElement - 2); i <= Math.min(selectedElement + 2, ownedElements.size() - 1); i++){
                Element e = ownedElements.get(i);
                Label label = new Label(e.getName());
                label.setStyle("-fx-background-color: white;");
                label.setMinHeight(50);
                label.setMinWidth(100);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setAlignment(Pos.CENTER);
                labelList.add(label);
                vb.setMargin(label, new Insets(5));
            }
            labelList.get(3).setStyle(labelList.get(3).getStyle() + "-fx-border-color: black; -fx-border-width: 5;");
        }
        while (labelList.size() < 6){
            Label emptyLabel = new Label();
            emptyLabel.setMinHeight(50);
            emptyLabel.setMinWidth(100);
            labelList.add(emptyLabel);
        };
        return labelList;
    }

    public void refreshDisplay(){
        ArrayList<Label> labelList = getDisplay();
        pane.getChildren().clear();
        vb.getChildren().clear();
        vb.getChildren().addAll(labelList);
        pane.getChildren().add(vb);
    }

    public Pane getPane() {
        return pane;
    }

    public Element getSelectedElement(){
        if (viewedClass >= 0) {
            return elementHandler.getOwnedElementsByClass(elementHandler.getElementClass(viewedClass)).get(selectedElement);
        } else {
            return null;
        }
    }
}
