package org.greenpath.app;

public class Opportunity {
    public final String id;
    public final String title;
    public final String org;
    public final String city;
    public final String category;
    public final String actionType;
    public final String date;
    public final String distance;
    public final String age;
    public final String effort;
    public final String description;
    public final String why;
    public final String badge;
    public final int match;

    public Opportunity(String id, String title, String org, String city, String category,
                       String actionType, String date, String distance, String age, String effort,
                       String description, String why, String badge, int match) {
        this.id = id;
        this.title = title;
        this.org = org;
        this.city = city;
        this.category = category;
        this.actionType = actionType;
        this.date = date;
        this.distance = distance;
        this.age = age;
        this.effort = effort;
        this.description = description;
        this.why = why;
        this.badge = badge;
        this.match = match;
    }
}
