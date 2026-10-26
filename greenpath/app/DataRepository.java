package org.greenpath.app;

import java.util.ArrayList;
import java.util.List;

public final class DataRepository {
    private DataRepository() {}

    public static List<Opportunity> opportunities() {
        List<Opportunity> x = new ArrayList<>();
        x.add(new Opportunity("creek", "Weekend Creek Cleanup", "Local Watershed Partners", "Santa Clara",
                "Water", "Volunteer", "Sat · 9:00 AM", "2.4 mi", "12+", "2 hours",
                "Join a small volunteer crew to remove litter and protect a neighborhood creek corridor.",
                "Cleaner waterways help wildlife, reduce plastic pollution, and build community stewardship.", "HIGH MATCH", 96));
        x.add(new Opportunity("grant", "Youth Climate Microgrant", "City Youth Climate Program", "San José",
                "Projects", "Funding", "Deadline · Sep 30", "8.8 mi", "15–24", "Project",
                "Turn a student-led climate idea into a funded local project with a short application.",
                "Funding can move a good idea from a school discussion into measurable local action.", "DEADLINE SOON", 93));
        x.add(new Opportunity("bike", "Safe Routes Feedback Walk", "Active Transportation Team", "Sunnyvale",
                "Transportation", "Civic", "Wed · 5:30 PM", "4.1 mi", "All ages", "75 min",
                "Walk a proposed school route and submit safety, shade, crossing, and bike-access observations.",
                "Safer walking and biking routes can reduce short car trips while improving student mobility.", "NEAR YOU", 91));
        x.add(new Opportunity("trees", "Neighborhood Tree Planting", "Urban Forest Volunteers", "Cupertino",
                "Nature", "Volunteer", "Oct 3 · 8:30 AM", "6.7 mi", "10+ w/adult", "3 hours",
                "Plant young shade trees, mulch them, and learn how urban forests cool neighborhoods.",
                "Shade trees reduce heat exposure, store carbon, and improve neighborhood comfort.", "FAMILY FRIENDLY", 88));
        x.add(new Opportunity("energy", "Home Energy Detective", "GreenPath Challenge", "CA-17",
                "Energy", "Learn", "Anytime", "At home", "All ages", "30 min",
                "Audit five energy uses at home and identify one realistic efficiency improvement.",
                "Small efficiency changes are a practical entry point for understanding household energy demand.", "DO TODAY", 85));
        x.add(new Opportunity("repair", "Repair Café Volunteer", "Community Repair Network", "Fremont",
                "Waste", "Volunteer", "Oct 10 · 10:00 AM", "14 mi", "14+", "2–4 hours",
                "Help check in visitors, sort items, document repairs, or learn alongside volunteer fixers.",
                "Extending product life avoids waste and helps people see reuse as a climate solution.", "LEARN A SKILL", 82));
        x.add(new Opportunity("meeting", "Climate Plan Youth Comment", "GreenPath Civic Guide", "Milpitas",
                "Civic", "Civic", "This month", "11 mi", "13+", "45 min",
                "Read a one-page climate-plan summary, choose one issue, and prepare a concise public comment.",
                "Public participation gives residents a direct way to shape local priorities and implementation.", "YOUR VOICE", 80));
        x.add(new Opportunity("food", "Low-Waste Lunch Challenge", "GreenPath Challenge", "CA-17",
                "Waste", "Challenge", "Anytime", "At school", "All ages", "1 week",
                "Track lunch packaging for five school days and test one lower-waste replacement.",
                "The challenge turns observation into a small experiment with a measurable result.", "7-DAY CHALLENGE", 78));
        return x;
    }
}
