package Alchemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Reaction {

    private ArrayList<Element> reactants;
    private ArrayList<Element> products;

    public Reaction(ArrayList<Element> r, ArrayList<Element> p) {
        reactants = new ArrayList<>(r.stream().sorted(Comparator.comparing(element -> element.getName())).collect(Collectors.toList()));
        products = new ArrayList<>(p.stream().sorted(Comparator.comparing(element -> element.getName())).collect(Collectors.toList()));
    }
    public Reaction(){

    }

    public void setProducts(ArrayList<Element> products) {
        this.products = products;
        products.sort(Comparator.comparing(Element::getName));
    }

    public void addProduct(Element product) {
        products.add(product);
        products.sort(Comparator.comparing(Element::getName));
    }

    public Element removeProduct(Element product) {
        boolean isIn = products.remove(product);
        if (isIn)
            return product;
        else
            return null;
    }

    public Element removeProduct(int index) {
        if (index < 0 || index > products.size() - 1)
            return null;
        else
            return products.remove(index);
    }

    public ArrayList<Element> getProducts() {
        return products;
    }

    public void setReactants(ArrayList<Element> reactants) {
        this.reactants = reactants;
        reactants.sort(Comparator.comparing(Element::getName));
    }

    public void setReactants(Element reactant, int index) {
        if (index > 1 || index < 0) {

        } else {
            this.reactants.set(index, reactant);
        }
    }

    public ArrayList<Element> getReactants() {
        return reactants;
    }

    public String getId() {
        return reactants.get(0) + " + " + reactants.get(1);
    }

    public String toString() {
        StringJoiner reactString = new StringJoiner(" + ", "", "");
        StringJoiner productString = new StringJoiner(", ", "", "");
        for (Element e : reactants)
            reactString.add(e.getName());
        for (Element e : products)
            productString.add(e.getName());
        return reactString.toString() + " = " + productString.toString();
    }

}
