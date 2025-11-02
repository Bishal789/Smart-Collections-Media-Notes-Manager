package app.service;


import app.model.Item;
import app.model.Library;


import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

public class IndexService {
    private final Map<String, Set<UUID>> keywordIndex = new HashMap<>();
    private final Map<String, Integer> tagFrequency = new HashMap<>();
    private final Library library;


    public IndexService(Library library) {
        this.library = library;
        library.getItems().forEach(this::indexItem);
    }


    public void indexItem(Item item) {
        tokenize(item.getTitle()).forEach(tok ->
                keywordIndex.computeIfAbsent(tok, k -> new HashSet<>()).add(item.getId())
        );
        if (item.getTags() != null) {
            item.getTags().forEach(t -> tagFrequency.merge(t.toLowerCase(), 1, Integer::sum));
        }
}
    public List<Item> search(String query, int limit) {
        Map<UUID, Integer> score = new HashMap<>();
        for (String tok : tokenize(query)) {
            Set<UUID> ids = keywordIndex.getOrDefault(tok, Collections.emptySet());
            for (UUID id : ids) score.merge(id, 1, Integer::sum);
        }
        PriorityQueue<Map.Entry<UUID,Integer>> pq = new PriorityQueue<>(
                Comparator.<Map.Entry<UUID,Integer>>comparingInt(Map.Entry::getValue).reversed());
        pq.addAll(score.entrySet());
        List<Item> results = new ArrayList<>();
        while (!pq.isEmpty() && results.size() < limit) {
            results.add(library.getById(pq.poll().getKey()));
        }
        return results.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    private static Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();
        return Arrays.stream(text.toLowerCase().split("/W+"))
                .filter(s -> s.length() > 1)
                .collect(Collectors.toSet());
    }


    public Map<String,Integer> getTagFrequency() { return Collections.unmodifiableMap(tagFrequency); }
}
