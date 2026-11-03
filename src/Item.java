import java.util.Objects;

public class Item {
    private final String name;
    private final double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    //Prototype: the item creates a new object with the same data
    public Item copy() { return new Item(name, price); }

    //two items with the same name and price are equal (needed to group the copies on the receipt)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item other = (Item) o;
        return Double.compare(price, other.price) == 0 && name.equals(other.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name, price); }

    public String getName() { return name; }
    public double getPrice() { return price; }
}
