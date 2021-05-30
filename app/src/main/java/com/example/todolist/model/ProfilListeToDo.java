package com.example.todolist.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ProfilListeToDo implements Serializable {
    private static final long serialVersionUID=1L;
    private String login;
    protected ArrayList<ListeToDo> mesListeToDo;

    public ProfilListeToDo() {
    }

    public ProfilListeToDo(String login, ArrayList<ListeToDo> mesListeToDo) {
        this.login = login;
        this.mesListeToDo = mesListeToDo;
    }

    public ProfilListeToDo(ArrayList<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    public ProfilListeToDo(String login) {
        this.login = login;
        this.mesListeToDo = new ArrayList<ListeToDo>();
    }

    public ArrayList<ListeToDo> getMesListeToDo() {
        return mesListeToDo;
    }

    public void setMesListeToDo( ArrayList<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void ajouteListe(ListeToDo uneListe) {
        this.mesListeToDo.add(uneListe);
    }

    @Override
    public String toString() {
        return "ProfilListeToDo{" +
                "login='" + login.toString() + '\'' +
                ", mesListeToDo=" + mesListeToDo.toString() +
                '}';
    }
}
