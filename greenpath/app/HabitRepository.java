package org.greenpath.app;

import java.util.ArrayList;
import java.util.List;

public final class HabitRepository {
    private HabitRepository() {}

    public static List<Habit> habits() {
        List<Habit> x = new ArrayList<>();
        x.add(new Habit("bottle", "Carry a reusable water bottle", "Waste", "Skip a single-use drink bottle today.", 10));
        x.add(new Habit("lunch", "Pack a low-waste lunch", "Waste", "Use a reusable container, utensil, or napkin where possible.", 10));
        x.add(new Habit("food", "Avoid food waste for one meal", "Food", "Take only what you can finish and save leftovers when possible.", 10));
        x.add(new Habit("plantmeal", "Choose one plant-forward meal", "Food", "Try one meal built mostly around plants, grains, beans, fruits, or vegetables.", 10));
        x.add(new Habit("shower", "Take a shorter shower", "Water", "Try cutting a few minutes from one shower today.", 10));
        x.add(new Habit("tap", "Turn off the tap while brushing", "Water", "Do not let water run while brushing your teeth.", 10));
        x.add(new Habit("full-load", "Run only a full load", "Water", "Wait for a full dishwasher or laundry load when practical.", 10));
        x.add(new Habit("lights", "Switch off unused lights", "Energy", "Do a quick check before leaving each room.", 10));
        x.add(new Habit("devices", "Power down an unused device", "Energy", "Turn off or unplug one device that does not need to stay on.", 10));
        x.add(new Habit("walk", "Walk or bike one short trip", "Transportation", "Replace one short car ride when it is safe and practical.", 10));
        x.add(new Habit("share-ride", "Share a ride or use transit", "Transportation", "Choose carpooling or public transit for one trip when available.", 10));
        x.add(new Habit("sort", "Sort waste correctly", "Waste", "Check one item before throwing it away: recycle, compost, reuse, or trash.", 10));
        return x;
    }

    public static Habit byId(String id) {
        for (Habit h : habits()) if (h.id.equals(id)) return h;
        return null;
    }
}
