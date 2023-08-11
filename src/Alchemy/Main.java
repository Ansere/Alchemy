package Alchemy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private SidePane lp;
    private SidePane rp;
    private GridPane gp;
    private ElementHandler elementHandler;


    @Override
    public void start(Stage primaryStage) throws Exception{
        ElementHandler elementHandler = new ElementHandler();
        elementHandler.loadElements();
        elementHandler.loadReactions();
        elementHandler.loadClasses();
        Scanner sc = new Scanner(System.in);

        /*
        boolean game = true;
        while (game){
            System.out.println("Type the name of element 1");
            String input1 = sc.nextLine();
            System.out.println("Type the name of element 2");
            String input2 = sc.nextLine();
            if (input1.toLowerCase().equals("exit") || input2.toLowerCase().equals("exit"))
                break;
            if (input1.toLowerCase().equals("stats") || input2.toLowerCase().equals("stats"))
                System.out.println("You have " + eh.getOwnedElementSize() + " elements out of " + eh.getElementSize() + " total elements.");
            else
                eh.mix(input1.toLowerCase(), input2.toLowerCase());
        }
         */
        rootLayout = new BorderPane();
        rootLayout.setPrefHeight(400);
        rootLayout.setPrefWidth(600);
        FXMLLoader gridPaneLoader = new FXMLLoader(getClass().getResource("gridPane.fxml"));
        lp = new SidePane();
        rp = new SidePane();
        rootLayout.setLeft(lp.getPane());
        rootLayout.setRight(rp.getPane());
        rootLayout.setCenter(gridPaneLoader.load());
        gp = (GridPane) gridPaneLoader.getController();
        lp.setElementHandler(elementHandler);
        lp.refreshDisplay();
        rp.setElementHandler(elementHandler);
        rp.refreshDisplay();
        gp.setInfo(lp, rp, elementHandler);
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(rootLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}

class GameScene {

    public GameScene(){

    }

}
