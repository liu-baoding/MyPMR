package com.example.todolist.model;

import java.io.Serializable;

public class ItemToDo implements Serializable {
    private static final long serialVersionUID=3L;
    private String description;
    private boolean fait;

    public ItemToDo() {
    }

    public ItemToDo(String description, boolean fait) {
        this.description = description;
        this.fait = fait;
    }

    public ItemToDo(String description) {
        this(description, false);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFait() {
        return fait;
    }

    public void setFait(boolean fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return "\"" + description + "\":" + fait;
    }
}
