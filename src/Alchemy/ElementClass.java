package Alchemy;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;

public class ElementClass {

    private String name;
    private ArrayList<Element> elements;
    private int id;
    private Color color;

    public ElementClass(String n, int i, Color c) {
        name = n;
        elements = new ArrayList<>();
        id = i;
        color = c;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addElement(Element e) {
        int q = 0;
        elements.add(e);
        elements.sort(Comparator.comparing(Element::getName));
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
