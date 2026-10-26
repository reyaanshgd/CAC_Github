package org.greenpath.app;

import java.util.ArrayList;
import java.util.List;

/** Curated, trusted climate-learning links. The content opens at the original publisher. */
public final class LearningRepository {
    private LearningRepository() {}

    public static List<LearningResource> resources() {
        List<LearningResource> x = new ArrayList<>();
        x.add(new LearningResource("nasa-evidence", "How do we know climate change is happening?", "NASA Science", "Learn", "Climate science",
                "A clear explanation of the evidence scientists use to understand a warming planet.",
                "https://science.nasa.gov/climate-change/evidence/"));
        x.add(new LearningResource("nasa-kids", "NASA Climate Kids", "NASA", "Learn", "Student learning",
                "Games, activities, videos, and articles about Earth and climate for students.",
                "https://www.nasa.gov/stem-content/nasa-climate-kids/"));
        x.add(new LearningResource("mit-explainers", "Climate Explainers", "MIT Climate Portal", "Learn", "Solutions + science",
                "Short expert explainers covering energy, food, cities, adaptation, policy, and climate science.",
                "https://climate.mit.edu/explainers"));
        x.add(new LearningResource("drawdown", "Drawdown Explorer", "Project Drawdown", "Case study", "Climate solutions",
                "Explore science-based climate solutions and how different actions can reduce emissions.",
                "https://drawdown.org/explorer"));
        x.add(new LearningResource("c40-cases", "Climate action in cities", "C40 Cities", "Case study", "Cities",
                "Real examples of cities around the world reducing climate risks and improving communities.",
                "https://www.c40.org/case-studies/"));
        x.add(new LearningResource("c40-sf", "San Francisco climate case studies", "C40 Cities", "Case study", "Bay Area",
                "Examples involving transportation, buildings, water, public health, and zero waste in San Francisco.",
                "https://www.c40.org/cities/san-francisco/"));
        x.add(new LearningResource("calepa-dashboard", "California Climate Dashboard", "CalEPA", "Learn", "California",
                "See California climate impacts, state actions, and examples involving water, energy, transportation, and wildfire.",
                "https://calepa.ca.gov/climate-dashboard/"));
        x.add(new LearningResource("noaa-news", "Latest climate news and stories", "NOAA", "News", "Current events",
                "Current NOAA climate stories, research updates, drought, ocean, heat, and weather information.",
                "https://www.noaa.gov/climate/related-news-and-stories"));
        x.add(new LearningResource("nasa-news", "NASA climate news and stories", "NASA Science", "News", "Current research",
                "New NASA articles and research about ice, oceans, atmosphere, wildfires, and a changing Earth.",
                "https://science.nasa.gov/category/earth/climate-change/"));
        x.add(new LearningResource("calepa-news", "California environmental news", "CalEPA", "News", "California",
                "California environmental and climate announcements, programs, grants, and policy updates.",
                "https://calepa.ca.gov/newsroom/2026newsreleases/"));
        x.add(new LearningResource("nasa-video", "Climate Change Multimedia", "NASA Science", "Video", "Climate science",
                "Visualizations and videos explaining climate indicators, greenhouse gases, ice, oceans, and extreme weather.",
                "https://science.nasa.gov/climate-change/multimedia/"));
        x.add(new LearningResource("ted-countdown", "TED Countdown: Watch & Listen", "TED", "Video", "Climate solutions",
                "Talks and interviews about climate ideas, technology, cities, energy, nature, and solutions.",
                "https://countdown.ted.com/watch-listen"));
        x.add(new LearningResource("climate-one", "Climate One", "The Commonwealth Club", "Podcast", "Climate conversations",
                "A weekly podcast and public-radio program exploring climate solutions and different viewpoints.",
                "https://www.climateone.org/"));
        x.add(new LearningResource("drawdown-roadmap", "The Drawdown Roadmap", "Project Drawdown", "Video", "Solutions",
                "A science-based guide and video series about how climate solutions can be put into action.",
                "https://drawdown.org/drawdown-roadmap"));
        return x;
    }
}
