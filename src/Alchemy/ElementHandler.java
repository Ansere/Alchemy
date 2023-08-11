package Alchemy;
import java.io.FileWriter;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.Comparator;
import java.util.StringJoiner;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;

public class ElementHandler{

    private ArrayList<Element> elements;
    private ArrayList<Element> ownedElements;
    private ArrayList<ElementClass> elementClasses;
    private ArrayList<ElementClass> ownedElementClasses;
    private ArrayList<Reaction> reactions;
    private ArrayList<Reaction> ownedReactions;

    public ElementHandler(){
        elements = new ArrayList<>();
        ownedElements = new ArrayList<>();
        elementClasses = new ArrayList<>();
        ownedElementClasses = new ArrayList<>();
        reactions = new ArrayList<>();
        ownedReactions = new ArrayList<>();
    }

    public void addOwnedElements(ArrayList<Element> elems){
        ownedElements.addAll(elems);
        refresh();
        save();
    }
    public void addOwnedReaction(Reaction reaction){
        ownedReactions.add(reaction);
        refresh();
        save();
    }
    public void addElement(Element element){
        elements.add(element);
        elements.sort(Comparator.comparing(Element::getId));
    }
    public void addElementClass(ElementClass elementClass) {
        elementClasses.add(elementClass);
        elementClasses.sort(Comparator.comparing(ElementClass::getId));
    }

    public Element getElement(String name){
        for (Element e : elements) {
            if (name.equals(e.getName())) {
                return e;
            }
        }
        return null;
    }
    public ElementClass getElementClass(String name){
        for (ElementClass ec : elementClasses){
            if (name.equals(ec.getName())){
                return ec;
            }
        }
        return null;
    }
    public ElementClass getElementClass(int id){
        for (ElementClass ec : elementClasses){
            if (ec.getId() == id){
                return ec;
            }
        }
        return null;
    }
    public Reaction getReaction(String id){
        for (Reaction r : reactions){
            if (r.getId().equals(id)){
                return r;
            }
        }
        return null;
    }

    public Reaction getReaction(Element parent1, Element parent2){
        return getReaction(new ArrayList<>(Arrays.asList(parent1, parent2)));
    }
    public Reaction getReaction(ArrayList<Element> reactants){
        try {
            return reactions.stream().filter(r -> r.getReactants().stream().sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList()).equals(reactants.stream().sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList()))).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ioobe){
            return null;
        }

    }

    public int getElementSize(){
        return elements.size();
    }
    public int getOwnedElementSize(){
        return ownedElements.size();
    }
    public int getReactionSize(){
        return reactions.size();
    }
    public ArrayList<ElementClass> getElementClasses(){
        return elementClasses;
    }
    public ArrayList<ElementClass> getOwnedElementClasses(){
        return ownedElementClasses;
    }
    public ArrayList<Element> getOwnedElementsByClass(ElementClass elementClass){
        ArrayList<Element> result = new ArrayList<>();
        for (Element e : elementClass.getElements()){
            if (ownsElement(e)){
                result.add(e);
            }
        }
        return result;
    }
    public ArrayList<Reaction> getOwnedReactions(){
        return ownedReactions;
    }

    public boolean ownsElement(Element element){
        for (Element e : ownedElements) {
            if (e.equals(element)){
                return true;
            }
        }
        return false;
    }
    public boolean ownsReaction(Reaction reaction){
        for (Reaction r : ownedReactions){
            if (reaction.getId().equals(r.getId())){
                return true;
            }
        }
        return false;
    }

    public Element removeElement(Element e){
        if (elements.contains(e)){
            for (int i = 0; i < e.getParents().size(); i++){
                Reaction r = e.getParents().get(i);
                if (r.getProducts().size() > 1){
                    r.getProducts().remove(e);
                } else {
                    removeReaction(r);
                    i--;
                }
            }
            for (int i = 0; e.getChildren().size() > 0; removeReaction(e.getChildren().get(i))) {
            }
            e.getElementClass().getElements().remove(e);
            elements.remove(e);
            ownedElements.remove(e);
            refresh();
            return e;
        } else {
            return null;
        }
    }

    public ElementClass removeElementClass(ElementClass ec){
        if (elementClasses.contains(ec)) {
            for (int i = 0; i < ec.getElements().size(); i ++){
                removeElement(ec.getElements().get(0));
            }
            elementClasses.remove(ec);
            ownedElementClasses.remove(ec);
            refresh();
            return ec;
        } else {
            return null;
        }
    }

    public Reaction removeReaction(Reaction r){
        if (reactions.contains(r)){
            for (Element e : r.getReactants()){
                e.getChildren().remove(r);
            }
            for (Element e : r.getProducts()){
                e.getParents().remove(r);
            }
            reactions.remove(r);
            ownedReactions.remove(r);
            return r;
        } else {
            return null;
        }
    }

    public void loadElements() {
        elements.clear();
        ownedElements.clear();
        JSONParser parser = new JSONParser();
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("src\\data\\elements.json"));
            for (Object o : a) {
                JSONObject element = (JSONObject) o;
                elements.add(new Element((String) element.get("name"), Boolean.parseBoolean((String) element.get("isFinal")), Integer.parseInt((String) element.get("id"))));
            }
            System.out.println(elements);
            System.out.println("Loaded " +  elements.size() + " elements");
        } catch (ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException fnfe){

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject save = (JSONObject) parser.parse(new FileReader("src\\data\\save.json"));
            for (Object element : (JSONArray) save.get("elements"))
                ownedElements.add(getElement((String) element));
            System.out.println("Added " + ownedElements.size() + " owned elements");
        } catch (ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException fnfe){
            try {
                FileWriter file = new FileWriter("src\\data\\save.json");
                System.out.println("No save file found. Making new one...");
                file.write("{\n\t\"elements\": [\n\t\t\"water\",\n\t\t\"fire\",\n\t\t\"earth\",\n\t\t\"air\"\n\t],\n\t\"reactions\": [\n\n\t]\n}");
                file.close();
                loadElements();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadReactions(){
        reactions.clear();
        ownedReactions.clear();
        JSONParser parser = new JSONParser();
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("src\\data\\reactions.json"));
            for (Object o : a) {
                JSONObject reaction = (JSONObject) o;
                ArrayList<Element> reactants = new ArrayList<>();
                ArrayList<Element> products = new ArrayList<>();
                for (Object elem : (JSONArray) reaction.get("reactants"))
                    reactants.add(getElement((String) elem));
                for (Object elem : (JSONArray) reaction.get("products"))
                    products.add(getElement((String) elem));
                Reaction rc = new Reaction(reactants, products);
                for (Element e : reactants){
                    e.addChild(rc);
                }
                for (Element e : products) {
                    e.addParent(rc);
                }
            }
            reactions = new ArrayList<>(elements.stream().flatMap(element -> element.getParents().stream()).distinct().collect(Collectors.toList()));
            System.out.println("Loaded " + reactions.size() + " reactions");

        } catch (ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException fnfe){

        } catch (IOException ie){

        }
        try {
            JSONObject save = (JSONObject) parser.parse(new FileReader("src\\data\\save.json"));
            for (Object reaction : (JSONArray) save.get("reactions"))
                ownedReactions.add(getReaction((String) reaction));
            System.out.println("Added " + ownedReactions.size() + " owned reactions");
        } catch (ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException fnfe) {
            try {
                FileWriter file = new FileWriter("src\\data\\save.json");
                System.out.println("No save file found. Making new one...");
                file.write("{\n\t\"elements\": [\n\t\t\"water\",\n\t\t\"fire\",\n\t\t\"earth\",\n\t\t\"air\"\n\t],\n\t\"reactions\": [\n\n\t]\n}");
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ie){

        }
    }

    public void loadClasses(){
        elementClasses.clear();
        ownedElementClasses.clear();
        JSONParser parser = new JSONParser();
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("src\\data\\classes.json"));
            for (Object o : a) {
                JSONObject element = (JSONObject) o;
                Color color;
                try {
                    color = Color.web((String) element.get("hex"));
                } catch (Exception je) {
                    color = Color.WHITE;
                }
                ElementClass ec = new ElementClass((String) element.get("name"), Integer.parseInt((String) element.get("id")), color);
                for (Object elem: (JSONArray) element.get("elements")) {
                    Element e = getElement((String) elem);
                    ec.addElement(e);
                    e.setElementClass(ec);
                }
                elementClasses.add(ec);
            }
            System.out.println("Loaded " +  elementClasses.size() + " element classes");
            for (Element e : ownedElements){
                if (!(ownedElementClasses.contains(e.getElementClass()))) {
                    ownedElementClasses.add(e.getElementClass());
                }
            }
            elementClasses.sort(Comparator.comparing(e -> Integer.valueOf(e.getId())));
            ownedElementClasses.sort(Comparator.comparing(e -> Integer.valueOf(e.getId())));
            System.out.println("Added " + ownedElementClasses.size() + " owned element classes");
        } catch (ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException fnfe){

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refresh(){ //adds classes of newly discovered elements and resorts classes by id order
        fixID();
        for (Element e : ownedElements){
            if (!(ownedElementClasses.contains(e.getElementClass()))) {
                ownedElementClasses.add(e.getElementClass());
            }
        }
        ownedElementClasses.sort(Comparator.comparing(e -> Integer.valueOf(e.getId())));
        ownedElements.sort(Comparator.comparing(e -> Integer.valueOf(e.getId())));
        reactions = new ArrayList<>(elements.stream().flatMap(element -> element.getParents().stream()).distinct().collect(Collectors.toList()));
    }

    public Reaction mix(String element1, String element2){
        try {
            Reaction reaction = getElement(element1).searchChildren(getElement(element2));
            return reaction;
        }catch (NullPointerException npe) {
            return null;
        }
    }

    public void saveMaster(){
        refresh();
        try {
            FileWriter file = new FileWriter("src\\data\\classes.json", false);
            StringJoiner ecSJ = new StringJoiner(",\n\t", "[\n\t", "\n]");
            for (ElementClass ec : elementClasses){
                StringJoiner eSJ = new StringJoiner(",\n\t\t\t", "\t\"elements\":[\n\t\t\t", "\n\t\t]");
                ecSJ.add("{\n\t\t\"name\": \"" + ec.getName() + "\"");
                for (Element e : ec.getElements()){
                    eSJ.add("\"" + e.toString() + "\"");
                }
                ecSJ.add(eSJ.toString());
                ecSJ.add("\t\"id\": \"" + ec.getId() + "\"\n\t}");
            }
            file.write(ecSJ.toString());
            file.close();
        } catch (FileNotFoundException fnfe){
            saveMaster();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter file = new FileWriter("src\\data\\elements.json", false);
            StringJoiner eSJ = new StringJoiner(",\n\t", "[\n\t", "\n]");
            for (Element e : elements){
                eSJ.add("{\n\t\t\"name\": \"" + e.getName() + "\"");
                eSJ.add("\t\"isFinal\": \"" + e.getFinal() + "\"");
                eSJ.add("\t\"id\": \"" + e.getId() + "\"\n\t}");
            }
            file.write(eSJ.toString());
            file.close();
        } catch (FileNotFoundException fnfe){
            saveMaster();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter file = new FileWriter("src\\data\\reactions.json", false);
            StringJoiner rSJ = new StringJoiner(",\n\t", "[\n\t", "\n]");
            for (Reaction r: reactions){
                StringJoiner eSJ = new StringJoiner(",\n\t\t\t", "{\n\t\t\"reactants\": [\n\t\t\t", "\n\t\t]");
                for (Element e : r.getReactants()){
                    eSJ.add("\"" + e.getName() + "\"");
                }
                rSJ.add(eSJ.toString());
                eSJ = new StringJoiner(",\n\t\t\t", "\t\"products\": [\n\t\t\t", "\n\t\t]\n\t}");
                for (Element e : r.getProducts()){
                    eSJ.add("\"" + e.getName() + "\"");
                }
                rSJ.add(eSJ.toString());
            }
            file.write(rSJ.toString());
            file.close();
        } catch (FileNotFoundException fnfe){
            saveMaster();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            FileWriter file = new FileWriter("src\\data\\save.json", false);
            StringJoiner sj = new StringJoiner(",\n\t\t", "{\n\t\"elements\": [\n\t\t", "\n\t],");
            for (Element e : ownedElements){
                sj.add("\"" + e.toString() + "\"");
            }
            file.write(sj.toString());
            if (ownedReactions.size() < 1){
                file.write("\n\"reactions\": [\n\n\t]\n}");
            } else {
                sj = new StringJoiner(",\n\t\t","\n\t\"reactions\": [\n\t\t", "\n\t]\n}");
                for (Reaction r : ownedReactions){
                    sj.add("\"" + r.getId() + "\"");
                }
                file.write(sj.toString());
            }
            file.close();
        }catch(FileNotFoundException fnfe){ // no save file found
            try {
                FileWriter file = new FileWriter("src\\data\\save.json");
                System.out.println("No save file found. Making new one...");
                file.write("{\n\t\"elements\": [\n\t\t\"water\",\n\t\t\"fire\",\n\t\t\"earth\",\n\t\t\"air\"\n\t],\n\t\"reactions\": [\n\n\t]\n}");
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean fixID(){
        elements.sort(Comparator.comparing(e -> e.getId()));
        if (elements.stream().filter(e -> e.getId() != elements.indexOf(e)).collect(Collectors.toList()).size() > 0){
            for (int i = 0; i < elements.size(); i++){
                elements.get(i).setId(i);
            }
            return true;
        } else {
            return false;
        }

    }
}

