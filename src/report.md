# Topic: *Creational Design Patterns*
Author: Saban Olga
------
## Objectives:
__1. Get familiar with the creational design patterns.__

__2. Choose a specific domain.__

__3. Implement at least 3 creational design patterns for the chosen domain.__

## Domain: Online Shop
The chosen domain is an online shop with a desktop (Swing) window. The customer picks products from a catalog, adds them to a cart, chooses a payment method (Card, PayPal or Crypto), pays, receives a receipt and can ask for a refund when the payment method supports it.

Main classes:

| Class | Responsibility |
|---|---|
| `Main` | entry point, opens the shop window |
| `ShopWindow2` | the main window (Swing GUI) |
| `ItemList2` | the catalog of products, loaded from `items2.csv` |
| `Item` | name and price of a product; it can create a copy of itself |
| `Cart2` | what the customer selected (items and quantities); gives copies of the items for an order |
| `Order2` | an order: generated id, items, total |
| `Payable`, `Refundable` | interfaces of the payment methods |
| `CardPayment`, `PayPalPayment`, `CryptoPayment` | concrete payment methods |
| `PaymentFactory`, `CardPaymentFactory`, `PayPalPaymentFactory`, `CryptoPaymentFactory` | creators of the payment methods |
| `PaymentFactoryProvider` | chooses the right factory by name |
| `CheckoutService` | pays an order with the chosen payment method |
| `ReceiptPrinter2` | builds the text of the receipt |
| `PaymentResult` | result of a payment (success flag, transaction id) |

## Used Creational Patterns:
* **Singleton**: `ItemList2`, `ReceiptPrinter2` and `ShopWindow2` each have exactly one instance in the application.
* **Factory Method**: `PaymentFactory` and its three implementations decide which payment method object is created.
* **Prototype**: `Item` creates a copy of itself with `copy()`. When an order is made, the cart asks every item for a copy, so the order does not share objects with the catalog.

## Some Theory:
Creational design patterns deal with **how objects are created**. Instead of calling `new` everywhere, the creation is hidden behind a well-defined place, so the rest of the code does not depend on the details of construction. There are six patterns in the list of this work:

| Pattern | Idea | Example in this shop |
|---|---|---|
| **Singleton** | a class has exactly one instance and a global access point to it | the product catalog, the receipt printer, the main window |
| **Factory Method** | an interface for creating one object, subclasses decide which class to instantiate | `PaymentFactory` creates a `Payable` (Card, PayPal or Crypto) |
| **Abstract Factory** | an interface for creating a **family** of related objects without naming their concrete classes | a "region" factory that creates a matching payment method **and** a matching receipt format and tax rule |
| **Builder** | builds a complex object step by step; the same steps can give different results | an `Order2` built with `addItem(...)`, `coupon(...)`, `shipping(...)`, then `build()` |
| **Prototype** | creates a new object by copying (cloning) an existing one | `Item.copy()` gives an order its own copy of every product |
| **Object Pool** | keeps a set of ready objects that are borrowed and returned, instead of creating and destroying them | a pool of expensive payment gateway connections |

The three patterns used in the project (Singleton, Factory Method and Prototype) are explained in the next sections. The other three (Abstract Factory, Builder and Object Pool) are not implemented in the code and are described only as theory.

**Factory Method vs Abstract Factory.** Both hide the concrete class. A Factory Method creates **one** kind of product (here: a payment method). An Abstract Factory creates **several related** products that must be used together (for example payment + receipt + tax of the same country). The factories in this project create only a `Payable`, so the pattern is **Factory Method**.

## Project Structure:
```text
src/
├── Main.java                      (entry point)
├── ShopWindow2.java               (main window)          <- Singleton
├── ItemList2.java                 (catalog)               <- Singleton
├── ReceiptPrinter2.java           (receipts)              <- Singleton
├── items2.csv                     (product data)
├── Item.java                      (product + copy())      <- Prototype
├── Cart2.java                     (toItems() copies)      <- Prototype (client)
├── Order2.java
├── CheckoutService.java
├── PaymentResult.java
├── Payable.java                   (product interface)     <- Factory Method
├── Refundable.java
├── CardPayment.java               (concrete products)
├── PayPalPayment.java
├── CryptoPayment.java
├── PaymentFactory.java            (creator interface)     <- Factory Method
├── CardPaymentFactory.java        (concrete creators)
├── PayPalPaymentFactory.java
├── CryptoPaymentFactory.java
└── PaymentFactoryProvider.java    (chooses a factory by name)
```

## Implementation:

### 1. Singleton
**Problem.** The product catalog must be read from the file only once, and the whole application must see the same list. The receipt printer has no state and does not need to be created again and again. The shop has one main window.

**Solution.** The class has a private constructor, so nobody can write `new ItemList2()`, and a static `getInstance()` that always returns the same object. The project uses two variants of the pattern.

**Variant A: initialization-on-demand holder** (`ItemList2`, `ReceiptPrinter2`). The instance lives in a private nested class `Holder`. The JVM loads `Holder` only when `getInstance()` is called for the first time, and class loading is thread-safe, so the instance is **lazy** and **thread-safe** without any `synchronized`.

```java
public class ItemList2 {
    private static final class Holder {
        private static final ItemList2 INSTANCE = new ItemList2();
    }

    private final List<Item> items;

    private ItemList2() {                      // nobody outside can call new
        this(Path.of("items2.csv"));
    }

    private ItemList2(Path file) {
        this.items = load(file);               // the file is read once
    }

    public static ItemList2 getInstance() {
        return Holder.INSTANCE;
    }

    public List<Item> getItems() {
        return items;
    }
    // load(...) reads items2.csv, defaults() is the fallback list
}
```

```java
public class ReceiptPrinter2 {
    private static final class Holder {
        private static final ReceiptPrinter2 INSTANCE = new ReceiptPrinter2();
    }

    private ReceiptPrinter2() { }

    public static ReceiptPrinter2 getInstance() {
        return Holder.INSTANCE;
    }
    // format(...) and print(...)
}
```

`ItemList2` returns an immutable list (`List.copyOf` / `List.of`), so the shared catalog cannot be changed by the code that uses it.

**Variant B: lazy initialization with `synchronized`** (`ShopWindow2`). The instance is created on the first call. `synchronized` makes sure that two threads cannot create two windows at the same time.

```java
public class ShopWindow2 extends JFrame {
    private static ShopWindow2 instance;

    public static synchronized ShopWindow2 getInstance() {
        if (instance == null) {
            instance = new ShopWindow2();
        }
        return instance;
    }

    private ShopWindow2() { /* builds the GUI */ }
}
```

**Usage.** The singletons are taken through `getInstance()` and never created with `new`:

```java
// Main.java
SwingUtilities.invokeLater(() -> ShopWindow2.getInstance().setVisible(true));

// ShopWindow2.java
private List<Item> catalog = ItemList2.getInstance().getItems();
private ReceiptPrinter2 printer = ReceiptPrinter2.getInstance();
```

**Check.** Two calls and 16 threads started at the same time all received the same object:

```text
ItemList2 same instance: true, items=20
ReceiptPrinter2 same instance: true
16 threads got the same ItemList2: true
```

**Comparison of the two variants.**

| | Holder (A) | `synchronized` (B) |
|---|---|---|
| Lazy | yes | yes |
| Thread-safe | yes, guaranteed by class loading | yes, by the lock |
| Cost of each call | none | takes the lock every time |
| Code | one nested class | a static field and an `if` |

Variant A is better for `ItemList2` and `ReceiptPrinter2` because they are called often. Variant B is simple and is enough for `ShopWindow2`, which is requested only once at start-up.

**Limitations.** A Singleton is global state, which makes unit tests harder (a test cannot easily replace `ItemList2` with a fake catalog). The implementation is also not protected against reflection or serialization, which is acceptable for this application.

### 2. Factory Method
**Problem.** The shop has several payment methods and the window must create them without knowing the concrete classes. If the window contained `new CardPayment()`, `new PayPalPayment()` and so on, it would be tied to every payment class.

**Solution.** `PaymentFactory` is the creator interface. Its method `createPayment()` is the factory method: each concrete factory decides which `Payable` it returns.

```java
// the creator (Factory Method)
public interface PaymentFactory {
    Payable createPayment();
    String getName();
}
```

```java
// concrete creators
final class CardPaymentFactory implements PaymentFactory {
    @Override public Payable createPayment() { return new CardPayment(); }
    @Override public String getName() { return "Card"; }
}

final class PayPalPaymentFactory implements PaymentFactory {
    @Override public Payable createPayment() { return new PayPalPayment(); }
    @Override public String getName() { return "PayPal"; }
}

final class CryptoPaymentFactory implements PaymentFactory {
    @Override public Payable createPayment() { return new CryptoPayment(); }
    @Override public String getName() { return "Crypto"; }
}
```

```java
// the products
public interface Payable {
    String getName();
    PaymentResult pay(double amount);
}

public class CardPayment implements Payable, Refundable { /* ... */ }
public class PayPalPayment implements Payable, Refundable { /* ... */ }
public class CryptoPayment implements Payable { /* no refund */ }
```

**Choosing a factory.** `PaymentFactoryProvider` is a small helper with a private constructor. It turns a text name into the right factory and can create all the payment methods at once:

```java
final class PaymentFactoryProvider {
    private PaymentFactoryProvider() { }

    public static PaymentFactory getFactory(String type) {
        String normalized = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "card":   return new CardPaymentFactory();
            case "paypal": return new PayPalPaymentFactory();
            case "crypto": return new CryptoPaymentFactory();
            default:
                throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }

    public static List<Payable> createAll() {
        return List.of(
                getFactory("card").createPayment(),
                getFactory("paypal").createPayment(),
                getFactory("crypto").createPayment());
    }
}
```

**Usage.** The window gets all the methods from the provider and fills the payment combo box with them. It only knows the `Payable` interface:

```java
// ShopWindow2.java
private List<Payable> methods = PaymentFactoryProvider.createAll();
...
for (Payable m : methods) paymentBox.addItem(m.getName());
...
Payable payment = methods.get(paymentBox.getSelectedIndex());
CheckoutService service = new CheckoutService(payment);   // works with the interface
```

**Check.**

```text
factory=PayPalPaymentFactory (PayPal) -> product=PayPalPayment
getFactory("card")    -> CardPayment
getFactory("PayPal ") -> PayPalPayment        (spaces and capital letters are normalized)
getFactory("CRYPTO")  -> CryptoPayment
getFactory("cash")    -> Unsupported payment type: cash
createAll(): Card PayPal Crypto
```

**Why it is Factory Method and not Abstract Factory.** Every factory creates a single product type, `Payable`. There is no second product (for example a receipt or a fee calculator) that must match it. That is the Factory Method structure: one creator interface, several concrete creators, one product hierarchy.

**Limitations.** `PaymentFactoryProvider.getFactory` contains a `switch`, so adding a fourth payment method needs a new product class, a new factory class **and** one new `case` in the provider. The creation of the payment method itself is closed for modification (the window and `CheckoutService` do not change), but the provider is not.

### 3. Prototype
**Problem.** The catalog is a Singleton, so its `Item` objects are shared by the whole application. When the customer pays, an `Order2` is created from the cart. If the order kept references to the catalog objects, the order and its receipt would depend on shared objects: a later change of a catalog item could silently change an old order. The order must keep the products exactly as they were at the moment of the purchase.

**Solution.** `Item` knows how to create a new object with the same data, so the code that needs a duplicate does not have to read every field and call the constructor itself. This is the idea of the Prototype pattern: a new object is made by copying an existing one.

```java
public class Item {
    private final String name;
    private final double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    //Prototype: the item creates a new object with the same data
    public Item copy() { return new Item(name, price); }
    // getters, equals, hashCode
}
```

`Cart2` stores the items that the customer selected (the catalog objects, with quantities). When an order is created, `toItems()` makes one **copy** for every piece:

```java
//flat list of items (repeated by quantity) used to build an Order
public List<Item> toItems() {
    List<Item> result = new ArrayList<>();
    for (Map.Entry<Item, Integer> e : lines.entrySet()) {
        for (int i = 0; i < e.getValue(); i++) result.add(e.getKey().copy());
    }
    return result;
}
```

**Usage.** The window passes the copies to the order:

```java
// ShopWindow2.java, pay()
Order2 order = new Order2(cart2.toItems());
```

**Check.** A cart with 1 x Keyboard and 2 x Mouse:

```text
copy != original: true, same name: true, same price: true, same class: true
cart lines (the cart keeps catalog objects): 2, count=3
toItems(): 3 items, none of them is a catalog object: true
the two mouse entries are different objects: true
```

**Consequence: `equals` and `hashCode`.** After copying, two mice in an order are two different objects. `ReceiptPrinter2` groups the lines of a receipt with a `Map<Item, Integer>`, and a map without `equals`/`hashCode` treats different objects as different keys. The receipt then shows `Mouse 1 x 29.99` twice instead of `Mouse 2 x 29.99`. For this reason `Item` compares its objects by **name and price**:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Item)) return false;
    Item other = (Item) o;
    return Double.compare(price, other.price) == 0 && name.equals(other.name);
}

@Override
public int hashCode() { return Objects.hash(name, price); }
```

**Why a copy method and not `clone()`.** Java has the `Cloneable` interface and `Object.clone()`, but they are considered error-prone: the `final` fields of `Item` are awkward to copy with `clone()`, and `clone()` skips the constructor. A `copy()` method that calls the constructor is simple and explicit.

**Limitations.** This is the simplest form of the pattern. There is no common `Prototype` interface, and `Cart2` calls `copy()` on the concrete class `Item`. The copy is shallow, which is enough here because the fields are a `String` and a `double`, which cannot change. In fact `Item` is immutable, so sharing it would also be safe at the moment; the copy protects the order if `Item` ever becomes mutable (for example, a discount applied to the item of one order must not change the catalog).

### How the patterns work together
When the program starts, `Main` takes the window from the Singleton. The window takes the catalog and the receipt printer from the Singletons, and the payment methods from the Factory Method. When the customer pays, the cart gives the order copies of the items (Prototype). The shopping flow runs without any `new` of a payment class:

```text
Main -> ShopWindow2.getInstance()
          |-- ItemList2.getInstance()            (Singleton: catalog)
          |-- ReceiptPrinter2.getInstance()      (Singleton: receipts)
          |-- PaymentFactoryProvider.createAll() (Factory Method: Card, PayPal, Crypto)
          '-- pay(): new Order2(cart2.toItems()) (Prototype: Item.copy() for every piece)
```

## Running the Project:
The application is a Swing window, so it needs a desktop session. Run it **from the `src` folder**, because `ItemList2` reads `items2.csv` from the current directory:

```bash
cd src
javac *.java
java Main
```

If the program is started from another folder (for example the project root in an IDE), `items2.csv` is not found and the catalog falls back to the same 20 products that are built into `ItemList2.defaults()`.

How to use it:
1. Select a product in the **Products** list and press **Add to cart**. The same product added again increases its quantity.
2. **Remove one** decreases the quantity of the selected cart line.
3. Choose a payment method in the combo box (Card, PayPal, Crypto) and press **Pay**. The receipt appears in the text area at the bottom.
4. **Refund** is enabled only after a successful payment with Card or PayPal. Crypto does not implement `Refundable`, so the button stays disabled.

### Example: payment with a card
Cart: 1 x Keyboard and 2 x Mouse, payment method Card (output of the same classes used by the window):

```text
[Card] Charged 119.97 EUR
--- RECEIPT ---
Order:    A-0001
Method:   Card
Tx ID:    CARD-001
------------------------------------
Keyboard                1 x    59.99
Mouse                   2 x    29.99
------------------------------------
Amount:   119.97 EUR
Status:   PAID
------------------------------------
```

```text
Card refundable: true, Crypto refundable: false
```

<!-- Screenshots: add screenshots of the main window, a receipt and a refund here. -->

## Conclusions / Screenshots / Results
In this laboratory work I studied the creational design patterns and applied them in an online shop:

* **Singleton** was applied to `ItemList2`, `ReceiptPrinter2` and `ShopWindow2`. The catalog is read from the file once, and the whole application uses the same catalog, printer and window. Two variants were used: the initialization-on-demand holder (lazy and thread-safe without locking) and lazy initialization with `synchronized`. The test with 16 threads confirmed that only one instance exists.
* **Factory Method** was applied through `PaymentFactory` and its three implementations. The window and `CheckoutService` work only with the `Payable` interface and never create a payment class with `new`.
* **Prototype** was applied in `Item.copy()` and `Cart2.toItems()`. Every order receives its own copies of the products instead of the shared catalog objects. Because the copies are different objects, `Item` needed `equals` and `hashCode`, otherwise the receipt did not group the equal lines.

The other creational patterns (Abstract Factory, Builder and Object Pool) were studied in the theory section, with a possible use of each one in this shop. The main limitations of the current solution are the global state introduced by the Singletons, which makes testing harder, the `switch` in `PaymentFactoryProvider`, which has to be edited when a new payment method is added, and the simplified form of the Prototype (no common interface, and a copy of an immutable class). The payments are simulated and always succeed, because the goal of the work is the structure of object creation and not the shop features.