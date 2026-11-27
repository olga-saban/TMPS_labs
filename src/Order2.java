import java.util.List;

public class Order2 {
    private static int counter = 0;

    private final String id;
    private final List<Item> items;

    public Order2(List<Item> items) {
        this.id = String.format("A-%04d", ++counter);   // A-0001, A-0002...
        this.items = List.copyOf(items);
    }

    public String getId() { return id; }
    public List<Item> getItems() { return items; }

    public double getTotal() {
        double sum = 0;
        for (Item item : items) {
            sum += item.getPrice();
        }
        return sum;
    }
}
