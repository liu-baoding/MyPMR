package com.example.todolist.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ListeToDo implements Serializable {
    private static final long serialVersionUID=2L;
    private String titreListeToDo;
    private ArrayList<ItemToDo> lesItems;

    public ListeToDo() {
    }

    public ListeToDo(String titreListeToDo, ArrayList<ItemToDo> lesItems) {
        this.titreListeToDo = titreListeToDo;
        this.lesItems = lesItems;
    }

    public ListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
        this.lesItems = new ArrayList<ItemToDo>();
    }

    public String getTitreListeToDo() {
        return titreListeToDo;
    }

    public void setTitreListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
    }

    public ArrayList<ItemToDo> getLesItems() {
        return lesItems;
    }

    public void setLesItems(ArrayList<ItemToDo> lesItems) {
        this.lesItems = lesItems;
    }

    public int rechercherItem(String descriptionItem) {
        for (int i = 0; i < lesItems.size(); i++) {
            if (descriptionItem == lesItems.get(i).getDescription()) {
                return i;
            }
        }
        return -1;
    }

    public void ajouteItem(ItemToDo unItem){
        this.lesItems.add(unItem);
    }


    @Override
    public String toString() {
        return "ListeToDo{" +
                "titreListeToDo='" + titreListeToDo.toString() + '\'' +
                ", lesItems=" + lesItems.toString() +
                '}';
    }
}
