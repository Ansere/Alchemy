package Alchemy;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class GridPane {

    @FXML
    private Label topLabel;
    private SidePane leftPane;
    private SidePane rightPane;
    private ElementHandler elementHandler;

    public GridPane() {
    }

    public void setInfo(SidePane lp, SidePane rp, ElementHandler eh){
        leftPane = lp;
        rightPane = rp;
        elementHandler = eh;
        topLabel.setTextAlignment(TextAlignment.CENTER);
    }

    public void setTopLabel(String str){
        topLabel.setText(str);
    }

    public void mix(){
        try {
            Reaction reaction = elementHandler.mix(leftPane.getSelectedElement().toString(), rightPane.getSelectedElement().toString());
            if (elementHandler.ownsReaction(reaction)){
                setTopLabel("You already have this reaction!\n" + reaction.toString());
            } else {
                elementHandler.addOwnedElements(reaction.getProducts());
                elementHandler.addOwnedReaction(reaction);
                setTopLabel("You made " + reaction.getProducts().toString());
                leftPane.refreshDisplay();
                rightPane.refreshDisplay();
            }
        } catch (NullPointerException npe){
            setTopLabel("Invalid Reaction!");
        }
    }

    public void reload(){
        elementHandler.save();
        elementHandler.loadElements();
        elementHandler.loadReactions();
        elementHandler.loadClasses();
    }

}
