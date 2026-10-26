package org.greenpath.app;

public class Habit {
    public final String id;
    public final String title;
    public final String category;
    public final String description;
    public final int points;

    public Habit(String id, String title, String category, String description, int points) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.points = points;
    }
}
