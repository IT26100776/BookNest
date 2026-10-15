package ShoppingCart;

public class PhysicalBookCartItem extends CartItem {

    private double weightKg;
    private double shippingHandlingRate; // Shipping rate per kg
    private String coverType;            // e.g., "Hardcover", "Paperback"

    public static final double DEFAULT_SHIPPING_RATE_PER_KG = 2.50;
    public static final double PHYSICAL_BOOK_TAX_RATE = 0.05; // 5% Goods Tax

    /**
     * Default constructor for deserialization.
     */
    public PhysicalBookCartItem() {
        super();
        setItemType(ItemType.PHYSICAL_BOOK);
        this.shippingHandlingRate = DEFAULT_SHIPPING_RATE_PER_KG;
        this.coverType = "Paperback";
    }

    /**
     * Full constructor initializing a physical book item.
     */
    public PhysicalBookCartItem(String bookId, String bookTitle, double unitPrice, int quantity,
                                double weightKg, String coverType) {
        super(bookId, bookTitle, unitPrice, quantity, ItemType.PHYSICAL_BOOK);
        setWeightKg(weightKg);
        this.shippingHandlingRate = DEFAULT_SHIPPING_RATE_PER_KG;
        this.coverType = (coverType != null && !coverType.isBlank()) ? coverType : "Paperback";
    }

    // =========================================================================
    // OOP Concept: POLYMORPHISM (Method Overriding)
    // =========================================================================

    /**
     * Calculates the subtotal for physical books: (unit price * quantity) + shipping cost.
     */
    @Override
    public double calculateSubtotal() {
        return (getUnitPrice() * getQuantity()) + calculateShippingCost();
    }

    /**
     * Calculates shipping cost based on weight, quantity, and per-kg rate.
     */
    @Override
    public double calculateShippingCost() {
        return this.weightKg * this.shippingHandlingRate * getQuantity();
    }

    /**
     * Calculates 5% tax on the base product price.
     */
    @Override
    public double calculateTax() {
        return (getUnitPrice() * getQuantity()) * PHYSICAL_BOOK_TAX_RATE;
    }

    /**
     * Returns detailed physical specification string.
     */
    @Override
    public String getItemDetails() {
        return String.format("Physical Book [Cover: %s, Weight: %.2f kg, Shipping Rate: $%.2f/kg]",
                coverType, weightKg, shippingHandlingRate);
    }

    // =========================================================================
    // Getters and Setters with Encapsulation
    // =========================================================================

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (weightKg < 0) {
            throw new IllegalArgumentException("Weight cannot be negative: " + weightKg);
        }
        this.weightKg = weightKg;
    }

    public double getShippingHandlingRate() {
        return shippingHandlingRate;
    }

    public void setShippingHandlingRate(double shippingHandlingRate) {
        if (shippingHandlingRate < 0) {
            throw new IllegalArgumentException("Shipping rate cannot be negative: " + shippingHandlingRate);
        }
        this.shippingHandlingRate = shippingHandlingRate;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }
}

