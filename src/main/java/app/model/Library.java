package app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Library implements Serializable {
    private static final long serialVersionUID = 1L;


    private final List<Item> items = new ArrayList<>();
    private final Map<UUID, Item> byId = new HashMap<>();


    public synchronized void addItem(Item item) {
        items.add(item);
        byId.put(item.getId(), item);
    }


    public synchronized void removeItem(Item item) {
        items.remove(item);
        byId.remove(item.getId());
    }


    public synchronized Item getById(UUID id) { return byId.get(id); }
    public synchronized List<Item> getItems() { return new ArrayList<>(items); }
    public synchronized int size() { return items.size(); }
}
