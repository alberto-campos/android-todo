package com.codepath.todolist;

/**
 * Created by acampos on 8/2/15.
 */
public class Item {

    private int id;
    private String name;
    private int status;
    private int due;
    private int category;

    private int DEFAULT_TIMESTAMP  = 1; // 1420070400;
    private int DEFAULT_STATUS = 1;
    private int DEFAULT_CATEGORY = 1;

    public Item () {}

    public Item (String name) {
        super();
        this.name = name;
        this.status = DEFAULT_STATUS;
        this.due = DEFAULT_TIMESTAMP;
        this.category = DEFAULT_CATEGORY;
    }

    public Item (String name, int status, int due, int cat) {
        super();
       // this.id = id;
        this.name = name;
        this.status = status;
        this.due = due;
        this.category = cat;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDue() {
        return this.due;
    }

    public void setDue(int due) {
        this.due = due;
    }

    public void setCategory(int cat) {this.category = cat; }

    public int getCategory() {return this.category; }

}
