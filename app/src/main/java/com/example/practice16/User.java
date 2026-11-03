package com.example.practice16;

public class User {
    private String name;
    private String state;
    private int age;
    private int stateSignal;

    public User(String name, String state, int age, int stateSignal) {
        this.name = name; this.state = state; this.age = age; this.stateSignal = stateSignal;
    }
    public int getStateSignal() { return stateSignal; }
    public void setStateSignal(int stateSignal) { this.stateSignal = stateSignal; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public int getAge() { return age; }
    public void setAge(int age, int stateSignal) { this.age = age; }
}
