package com.example.practice16;

public class User {
    private String name;
    private String state;
    private int age;

    public User(String name, String state, int age) {
        this.name = name; this.state = state; this.age = age;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
