package com.lk.entity;

/**
 * @ClassName Human
 * @Description TODO
 * @Author lk
 * @Date 2022/07/13 13:29
 * @Version 1.0
 */
public class Human {
    private int id;
    private String name;

    public Human() {
    }

    public Human(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Human{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
