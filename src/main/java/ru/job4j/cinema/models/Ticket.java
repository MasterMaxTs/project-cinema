package ru.job4j.cinema.models;

public class Ticket {

    private int id;
    private int sessionId;
    private int row;
    private int cell;
    private int userId;


    public Ticket() {

    }

    public Ticket(int id, int sessionId, int row, int cell, int userId) {
        this.id = id;
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
