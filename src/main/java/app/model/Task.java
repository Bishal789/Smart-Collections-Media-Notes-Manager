package app.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class Task implements Serializable, Comparable<Task> {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final UUID itemId;
    private String description;
    private Instant deadline;
    private int priority; // Lower value = higher priority
    private Item item;

    public Task(UUID itemId, String description, Instant deadline, int priority) {
        this.id = UUID.randomUUID();
        this.itemId = itemId;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
    }

    public Task(Item item, Instant deadline) {
        this.item = item;
        this.deadline = deadline;
        this.id = UUID.randomUUID();
        this.itemId = item.getId();
    }

    public UUID getId() { return id; }
    public UUID getItemId() { return itemId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getDeadline() { return deadline; }
    public void setDeadline(Instant deadline) { this.deadline = deadline; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    @Override
    public int compareTo(Task other) {
        // Higher priority first, then earlier deadline
        int cmp = Integer.compare(this.priority, other.priority);
        if (cmp == 0 && this.deadline != null && other.deadline != null) {
            cmp = this.deadline.compareTo(other.deadline);
        }
        return cmp;
    }

    @Override
    public String toString() {
        return String.format("Task: %s (Priority: %d, Deadline: %s)", 
                             description != null ? description : "No Description", 
                             priority, 
                             deadline != null ? deadline : "No Deadline");
    }
}
