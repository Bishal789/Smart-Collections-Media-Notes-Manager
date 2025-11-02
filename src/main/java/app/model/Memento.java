package app.model;

import java.io.Serializable;
import java.util.UUID;

public class Memento implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID itemId;
    private final Object state;
    private final String action;

    public Memento(UUID itemId, Object state, String action) {
        this.itemId = itemId;
        this.state = state;
        this.action = action;
    }

    public UUID getItemId() { return itemId; }
    public Object getState() { return state; }
    public String getAction() { return action; }
}
