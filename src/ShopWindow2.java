import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShopWindow2 extends JFrame {
    private static ShopWindow2 instance;

    // the place where payment methods are listed
    private List<Payable> methods = PaymentFactoryProvider.createAll();
    private List<Item> catalog = ItemList2.getInstance().getItems();
    private Cart2 cart2 = new Cart2();
    private ReceiptPrinter2 printer = ReceiptPrinter2.getInstance();

    public static synchronized ShopWindow2 getInstance() {
        if (instance == null) {
            instance = new ShopWindow2();
        }
        return instance;
    }

    private DefaultListModel<String> productModel = new DefaultListModel<>();
    private JList<String> productList = new JList<>(productModel);

    private DefaultListModel<String> cartModel = new DefaultListModel<>();
    private JList<String> cartList = new JList<>(cartModel);
    private List<Item> cartRows = new ArrayList<>();   // same order as cartList

    private JLabel totalLabel = new JLabel("Total: 0.00 EUR");
    private JComboBox<String> paymentBox = new JComboBox<>();
    private JTextArea receiptArea = new JTextArea(10, 40);
    private JButton refundButton = new JButton("Refund");

    private Order2 currentOrder;
    private Payable currentPayment;
    private PaymentResult currentResult;

    private ShopWindow2() {
        setTitle("Online Shop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // products
        for (Item item : catalog) {
            productModel.addElement(item.getName() + " - " + money(item.getPrice()));
        }
        JButton addButton = new JButton("Add to cart");
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.add(new JLabel("Products"), BorderLayout.NORTH);
        left.add(new JScrollPane(productList), BorderLayout.CENTER);
        left.add(addButton, BorderLayout.SOUTH);

        // cart
        JButton removeButton = new JButton("Remove one");
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.add(new JLabel("Your cart"), BorderLayout.NORTH);
        right.add(new JScrollPane(cartList), BorderLayout.CENTER);
        right.add(removeButton, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(left);
        center.add(right);
        add(center, BorderLayout.CENTER);

        // payment + receipt
        for (Payable m : methods) paymentBox.addItem(m.getName());
        JButton payButton = new JButton("Pay");
        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        payPanel.add(totalLabel);
        payPanel.add(new JLabel("   Payment method:"));
        payPanel.add(paymentBox);
        payPanel.add(payButton);
        payPanel.add(refundButton);

        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        refundButton.setEnabled(false);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(payPanel, BorderLayout.NORTH);
        bottom.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // button actions
        addButton.addActionListener(e -> addSelected());
        removeButton.addActionListener(e -> removeSelected());
        payButton.addActionListener(e -> pay());
        refundButton.addActionListener(e -> refund());

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setSize(750, 600);
        setLocationRelativeTo(null);
    }

    private void addSelected() {
        int index = productList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }
        cart2.add(catalog.get(index));
        refreshCart();
    }

    private void removeSelected() {
        int index = cartList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Select an item from the cart first.");
            return;
        }
        cart2.decrease(cartRows.get(index));
        refreshCart();
    }

    private void refreshCart() {
        cartModel.clear();
        cartRows.clear();
        for (Map.Entry<Item, Integer> e : cart2.getLines().entrySet()) {
            cartRows.add(e.getKey());
            cartModel.addElement(e.getKey().getName() + "  x" + e.getValue());
        }
        totalLabel.setText("Total: " + money(cart2.getTotal()));
    }

    private void pay() {
        if (cart2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The cart is empty.");
            return;
        }
        Payable payment = methods.get(paymentBox.getSelectedIndex());
        Order2 order = new Order2(cart2.toItems());

        //the service works with the abstraction (Dependency Inversion)
        CheckoutService service = new CheckoutService(payment);
        PaymentResult result = service.checkout(order);

        currentOrder = order;
        currentPayment = payment;
        currentResult = result;
        receiptArea.setText(printer.format(order, payment, result));
        refundButton.setEnabled(result.isSuccess() && payment instanceof Refundable);

        cart2.clear();
        refreshCart();
    }

    private void refund() {
        if (currentPayment instanceof Refundable) {
            ((Refundable) currentPayment).refund(currentOrder.getTotal());
            receiptArea.setText(printer.format(currentOrder, currentPayment, currentResult, true));
            refundButton.setEnabled(false);
        }
    }

    private static String money(double value) {
        return String.format(Locale.US, "%.2f EUR", value);
    }
}
