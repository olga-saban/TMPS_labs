import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//only keeps what the customer selected (single responsibility)
public class Cart2 {
    private final Map<Item, Integer> lines = new LinkedHashMap<>();

    public void add(Item item) { lines.merge(item, 1, Integer::sum); }

    //removes one piece; the line disappears when quantity reaches 0
    public void decrease(Item item) { lines.computeIfPresent(item, (k, qty) -> qty > 1 ? qty - 1 : null); }

    public void remove(Item item) { lines.remove(item); }
    public void clear() { lines.clear(); }
    public boolean isEmpty() { return lines.isEmpty(); }

    public Map<Item, Integer> getLines() { return Collections.unmodifiableMap(lines); }

    public int getCount() {
        int n = 0;
        for (int qty : lines.values()) n += qty;
        return n;
    }

    public double getTotal() {
        double sum = 0;
        for (Map.Entry<Item, Integer> e : lines.entrySet()) {
            sum += e.getKey().getPrice() * e.getValue();
        }
        return sum;
    }

    //flat list of items (repeated by quantity) used to build an Order
    public List<Item> toItems() {
        List<Item> result = new ArrayList<>();
        for (Map.Entry<Item, Integer> e : lines.entrySet()) {
            for (int i = 0; i < e.getValue(); i++) result.add(e.getKey().copy());
        }
        return result;
    }
}
