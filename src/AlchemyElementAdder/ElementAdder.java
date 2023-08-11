package AlchemyElementAdder;

import Alchemy.Element;
import Alchemy.ElementClass;
import Alchemy.Reaction;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import Alchemy.ElementHandler;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;

public class ElementAdder extends Application {

    private static Stage primaryStage;
    private static StackPane root;
    private static ElementHandler eh;
    private static Image errorIcon;

    public static void main(String[] args) {
        eh = new ElementHandler();
        eh.loadElements();
        eh.loadClasses();
        eh.loadReactions();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        errorIcon = SwingFXUtils.toFXImage(ImageIO.read(new File("error.png")), null);
        this.primaryStage = primaryStage;
        TreeItem<String> rootItem = new TreeItem<>("Classes");
        for (ElementClass ec : eh.getElementClasses()){
            TreeItem<String> elementClass = new TreeItem<>(ec.getName());
            for (Element e : ec.getElements()){
                TreeItem<String> element = new TreeItem<>(e.getName());
                element.getChildren().add(new TreeItem<>("isFinal: " + e.getFinal()));
                TreeItem<String> parents = new TreeItem<>("parents");
                for (Reaction reaction : e.getParents()){
                    parents.getChildren().add(new TreeItem<>(reaction.toString()));
                }
                if (parents.getChildren().size() == 0 && e.getId() >= 4){
                    element.setGraphic(new ImageView(errorIcon));
                }
                element.getChildren().addAll(parents);
                elementClass.getChildren().add(element);
            }
            if (elementClass.getChildren().stream().anyMatch(el -> el.getGraphic() != null)){
                elementClass.setGraphic(new ImageView(errorIcon));
            }
            rootItem.getChildren().add(elementClass);
        }
        if (rootItem.getChildren().stream().anyMatch(ec -> ec.getGraphic() != null)){
            rootItem.setGraphic(new ImageView(errorIcon));
        }
        TreeView<String> tree = new TreeView<>(rootItem);
        tree.setStyle("-fx-font-size: 20;");
        tree.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return new ClassCell(eh);
            }
        });
        root = new StackPane();
        root.getChildren().add(tree);
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void refreshDisplay(TreeView<String> originalTreeView){
        TreeItem<String> rootItem = new TreeItem<>("Classes");
        rootItem.setExpanded(true);
        for (int i = 0; i < eh.getElementClasses().size(); i++){
            ElementClass ec = eh.getElementClasses().get(i);
            TreeItem<String> elementClass = new TreeItem<>(ec.getName());
            if (originalTreeView.getTreeItem(0).getChildren().stream().anyMatch(treeItem -> treeItem.getValue().equals(ec.getName()) && treeItem.isExpanded())) {
                elementClass.setExpanded(true);
            }
            for (int j = 0; j < ec.getElements().size(); j++){
                Element e = ec.getElements().get(j);
                TreeItem element = new TreeItem<>(e.getName());
                if (originalTreeView.getTreeItem(0).getChildren().get(i).getChildren().stream().anyMatch(treeItem -> treeItem.getValue().equals(e.getName()) && treeItem.isExpanded())) {
                    element.setExpanded(true);
                }
                element.getChildren().add(new TreeItem<>("isFinal: " + e.getFinal()));
                TreeItem parents = new TreeItem<>("parents");
                if (originalTreeView.getTreeItem(0).getChildren().get(i).getChildren().stream().anyMatch(el -> el.getValue().equals(e.getName()) && el.getChildren().get(1).isExpanded())) {
                    parents.setExpanded(true);
                }
                for (Reaction reaction : e.getParents()){
                    parents.getChildren().add(new TreeItem<>(reaction.toString()));
                }
                if (parents.getChildren().size() == 0 && e.getId() >= 4){
                    element.setGraphic(new ImageView(errorIcon));
                }
                element.getChildren().add(parents);
                elementClass.getChildren().add(element);
            }
            if (elementClass.getChildren().stream().anyMatch(el -> el.getGraphic() != null)){
                elementClass.setGraphic(new ImageView(errorIcon));
            }
            rootItem.getChildren().add(elementClass);
        }
        if (rootItem.getChildren().stream().anyMatch(ec -> ec.getGraphic() != null)){
            rootItem.setGraphic(new ImageView(errorIcon));
        }
        TreeView<String> tree = new TreeView<>(rootItem);
        tree.setStyle("-fx-font-size: 20;");
        tree.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return new ClassCell(eh);
            }
        });
        root.getChildren().clear();
        root.getChildren().add(tree);
    }

}

 class ClassCell extends TreeCell<String>{
    private ContextMenu menu = new ContextMenu();
    private ElementHandler elementHandler;

    public ClassCell(ElementHandler eh){
        elementHandler = eh;
    }

     @Override
     public void updateItem(String item, boolean empty) {
         super.updateItem(item, empty);
         if (empty) {
             setText(null);
             setGraphic(null);
             setContextMenu(null);
         } else {
             setText(getString());
             setGraphic(getTreeItem().getGraphic());
             if (getTreeView().getTreeItemLevel(getTreeItem()) == 0){
                 menu.getItems().clear();
                 MenuItem addMenu = new MenuItem("Add Class");
                 addMenu.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClassPopUp.fxml"));
                         Parent popUp = new Parent() {
                         };
                         try {
                             popUp = (Parent) fxmlLoader.load();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         Scene scene = new Scene(popUp);
                         Stage stage = new Stage();
                         ClassPopUp cpu = fxmlLoader.getController();
                         cpu.setElementHandler(elementHandler);
                         cpu.setTreeItems(getTreeItem(), getTreeView());
                         stage.setTitle("Add Class");
                         stage.setScene(scene);
                         stage.show();
                     }
                 });
                 menu.getItems().add(addMenu);
                 setContextMenu(menu);
             } else if (getTreeView().getTreeItemLevel(getTreeItem()) == 1){
                 menu.getItems().clear();
                 MenuItem addMenu = new MenuItem("Add Element");
                 MenuItem setMenu = new MenuItem("Set Class");
                 MenuItem removeMenu = new MenuItem("Remove Class");
                 addMenu.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ElementPopUp.fxml"));
                         Parent popUp = new Parent() {
                         };
                         try {
                             popUp = (Parent) fxmlLoader.load();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         Scene scene = new Scene(popUp);
                         Stage stage = new Stage();
                         ElementPopUp epu = fxmlLoader.getController();
                         epu.setElementHandler(elementHandler);
                         epu.setTreeItems(getTreeItem(), getTreeView());
                         stage.setTitle("Add Element");
                         stage.setScene(scene);
                         stage.show();
                     }
                 });
                 setMenu.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClassPopUp.fxml"));
                         Parent popUp = new Parent() {
                         };
                         try {
                             popUp = (Parent) fxmlLoader.load();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         Scene scene = new Scene(popUp);
                         Stage stage = new Stage();
                         ClassPopUp cpu = fxmlLoader.getController();
                         cpu.setElementHandler(elementHandler);
                         cpu.setTreeItems(getTreeItem(), getTreeView());
                         cpu.setElementClass(elementHandler.getElementClass(getTreeItem().getValue()));
                         stage.setTitle("Set Class");
                         stage.setScene(scene);
                         stage.show();
                     }
                 });
                 removeMenu.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         elementHandler.removeElementClass(elementHandler.getElementClass(getTreeItem().getValue()));
                         getTreeItem().getParent().getChildren().remove(getTreeItem());
                         setContextMenu(null);
                         elementHandler.saveMaster();
                         ElementAdder.refreshDisplay(getTreeView());
                     }
                 });
                 menu.getItems().addAll(setMenu, removeMenu, addMenu);
                 setContextMenu(menu);
             } else if (getTreeView().getTreeItemLevel(getTreeItem()) == 2 && !getTreeItem().isLeaf() && elementHandler.getElement(getTreeItem().getValue()) != null) {
                 menu.getItems().clear();
                 MenuItem setMenu = new MenuItem("Set Element");
                 MenuItem removeMenu = new MenuItem("Remove Element");
                 setMenu.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ElementPopUp.fxml"));
                         Parent popUp = new Parent() {
                         };
                         try {
                             popUp = (Parent) fxmlLoader.load();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         Scene scene = new Scene(popUp);
                         Stage stage = new Stage();
                         ElementPopUp epu = fxmlLoader.getController();
                         epu.setElementHandler(elementHandler);
                         epu.setTreeItems(getTreeItem(), getTreeView());
                         epu.setElement(elementHandler.getElement(getTreeItem().getValue()));
                         stage.setScene(scene);
                         stage.setTitle("Set Element");
                         stage.show();
                     }
                 });
                 removeMenu.setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent actionEvent) {
                         elementHandler.removeElement(elementHandler.getElement(getTreeItem().getValue()));
                         getTreeItem().getParent().getChildren().remove(getTreeItem());
                         setContextMenu(null);
                         elementHandler.saveMaster();
                         ElementAdder.refreshDisplay(getTreeView());
                     }
                 });
                 menu.getItems().addAll(setMenu, removeMenu);
                 setContextMenu(menu);
             }
         }
     }

     private String getString() {
         return getItem().toString();
     }
}
