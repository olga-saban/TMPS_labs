import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//the catalog of products the customer can choose from
//loaded from items2.csv
//Singleton: one shared catalog instance for the whole application
public class ItemList2 {
    private static final class Holder {
        private static final ItemList2 INSTANCE = new ItemList2();
    }

    private final List<Item> items;

    private ItemList2() {
        this(Path.of("items2.csv"));
    }

    private ItemList2(Path file) {
        this.items = load(file);
    }

    public static ItemList2 getInstance() {
        return Holder.INSTANCE;
    }

    public List<Item> getItems() {
        return items;
    }

    private static List<Item> load(Path file) {
        List<Item> result = new ArrayList<>();
        if (Files.exists(file)) {
            try {
                for (String line : Files.readAllLines(file)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int cut = line.lastIndexOf(',');
                    if (cut < 1) continue;
                    try {
                        String name = line.substring(0, cut).trim();
                        double price = Double.parseDouble(line.substring(cut + 1).trim());
                        result.add(new Item(name, price));
                    } catch (NumberFormatException ignored) {
                        // skip lines without a valid price (e.g. a header)
                    }
                }
            } catch (IOException ignored) {
                // fall back to defaults below
            }
        }
        return result.isEmpty() ? defaults() : List.copyOf(result);
    }

    private static List<Item> defaults() {
        return List.of(
                new Item("Laptop", 899.99),
                new Item("Desktop PC", 1199.99),
                new Item("Monitor", 249.99),
                new Item("Keyboard", 59.99),
                new Item("Mouse", 29.99),
                new Item("Headphones", 89.99),
                new Item("Smartphone", 799.99),
                new Item("Tablet", 449.99),
                new Item("Smartwatch", 199.99),
                new Item("Webcam", 69.99),
                new Item("Microphone", 99.99),
                new Item("External SSD 1TB", 109.99),
                new Item("USB Flash Drive 128GB", 19.99),
                new Item("Router", 79.99),
                new Item("Speakers", 59.99),
                new Item("Printer", 149.99),
                new Item("Gaming Console", 499.99),
                new Item("Power Bank", 39.99),
                new Item("USB-C Hub", 34.99),
                new Item("Laptop Stand", 44.99)
        );
    }
}
