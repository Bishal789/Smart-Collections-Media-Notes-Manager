package app.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String title;
    private Set<String> tags = new LinkedHashSet<>();
    private String pathOrUrl;
    private ItemType type;
    private final Instant createdAt;
    private int rating;

    public Item(String title, ItemType type, String pathOrUrl) {
        this.id = UUID.randomUUID();
        this.title = title == null ? "" : title;
        this.type = type;
        this.pathOrUrl = pathOrUrl;
        this.createdAt = Instant.now();
    }

    public Item(String title, ItemType type, String pathOrUrl, Set<String> tags, int rating, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.title = title == null ? "" : title;
        this.type = type;
        this.pathOrUrl = pathOrUrl;
        this.tags = tags == null ? new LinkedHashSet<>() : tags;
        this.rating = rating;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public Item(Item other) {
        this.id = other.id;
        this.title = other.title;
        this.tags = new LinkedHashSet<>(other.tags);
        this.pathOrUrl = other.pathOrUrl;
        this.type = other.type;
        this.createdAt = other.createdAt;
        this.rating = other.rating;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public String getPathOrUrl() { return pathOrUrl; }
    public void setPathOrUrl(String pathOrUrl) { this.pathOrUrl = pathOrUrl; }
    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }
    public int getRating() { return rating; }
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return title + " (" + type + ")";
    }
}
