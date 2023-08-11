package Alchemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Element {

    private String name;
    private ArrayList<Reaction> parents;
    private ArrayList<Reaction> children;
    private boolean isFinal;
    private int id;
    private ElementClass elementClass;

    public Element(String n, boolean isF, int i) {
        name = n;
        isFinal = isF;
        parents = new ArrayList<>();
        children = new ArrayList<>();
        id = i;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParents(ArrayList<Reaction> parents) {
        this.parents = parents;
    }
    public void clearParents(){
        parents = new ArrayList<>();
    }

    public void setChildren(ArrayList<Reaction> childrens) {
        this.children = children;
    }

    public void addParent(Reaction parent) {
        parents.add(parent);
    }

    public void addChild(Reaction child) {
        children.add(child);
    }

    public ArrayList<Reaction> getParents() {
        return parents;
    }

    public ArrayList<Reaction> getChildren() {
        return children;
    }

    public ArrayList<Reaction> searchParents(Element element) {
        if (element.getFinal()) {
            return null;
        }
        ArrayList<Reaction> result = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i).getReactants().contains(element))
                result.add(parents.get(i));
        }
        return result;
    }

    public ArrayList<Reaction> searchParents(Element reactant1, Element reactant2) {
        if (reactant1.getFinal() || reactant2.getFinal())
            return null;
        ArrayList<Reaction> result = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i).getReactants().contains(reactant1) && parents.get(i).getReactants().contains(reactant2))
                result.add(parents.get(i));
        }
        return result;
    }

    public Reaction searchChildren(Element element) {
        Reaction result = null;
        if (element.getFinal()) {
            return result;
        }
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getReactants().stream().sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList()).equals(Arrays.asList(element, this).stream().sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList())))
                result = children.get(i);
        }
        return result;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean getFinal() {
        return isFinal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setElementClass(ElementClass ec) {
        elementClass = ec;
    }

    public ElementClass getElementClass() {
        return elementClass;
    }

    public boolean equals(Element element) { //just equates name and final
        boolean isName = name.equals(element.getName());
        boolean isF = isFinal == element.getFinal();
        return isName && isF;
    }

    public String toString() {
        return name;
    }
}
