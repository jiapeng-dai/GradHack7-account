package com.example.accountview;

public class Clients {
    // fields
    private int clientID;
    private String clientName;
    private float clientBalance;
    // constructors
    public Clients() {}
    public Clients(int id, String clientname, float balance) {
        this.clientID = id;
        this.clientName = clientname;
        this.clientBalance = balance;
    }
    // properties
    public void setID(int id) {
        this.clientID = id;
    }
    public int getID() {
        return this.clientID;
    }
    public void setClientName(String clientname) {
        this.clientName = clientname;
    }
    public String getClientName() {
        return this.clientName;
    }
    public void setBalance(float balance) {
        this.clientBalance = balance;
    }
    public float getBalance() {
        return this.clientBalance;
    }

}
