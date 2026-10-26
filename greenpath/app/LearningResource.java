package org.greenpath.app;

public class LearningResource {
    public final String id;
    public final String title;
    public final String source;
    public final String type;
    public final String topic;
    public final String description;
    public final String url;

    public LearningResource(String id, String title, String source, String type, String topic,
                            String description, String url) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.type = type;
        this.topic = topic;
        this.description = description;
        this.url = url;
    }
}
