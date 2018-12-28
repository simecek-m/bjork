package com.example.app.bjork.model;

import java.util.Date;

public class Feedback {

    private String author;
    private String text;
    private Date date;

    public Feedback(String author, String text) {
        this.author = author;
        this.text = text;
        this.date = new Date();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
