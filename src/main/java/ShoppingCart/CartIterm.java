package ShoppingCart;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;
import java.util.UUID;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "itemType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PhysicalBookCartItem.class, name = "PHYSICAL_BOOK"),
        @JsonSubTypes.Type(value = DigitalBookCartItem.class, name = "DIGITAL_BOOK")
})
    class CartItem {

    // Encapsulated private attributes
    private String itemId;
    private String bookId;
    private String bookTitle;
    private double unitPrice;
    private int quantity;
    private ItemType itemType;


    public CartItem() {
        this.itemId = UUID.randomUUID().toString();
    }


    public CartItem(String bookId, String bookTitle, double unitPrice, int quantity, ItemType itemType) {
        this.itemId = UUID.randomUUID().toString();
        this.bookId = Objects.requireNonNull(bookId, "Book ID cannot be null");
        this.bookTitle = Objects.requireNonNull(bookTitle, "Book title cannot be null");
        setUnitPrice(unitPrice);
        setQuantity(quantity);
        this.itemType = Objects.requireNonNull(itemType, "Item type cannot be null");
    }


    public double calculateSubtotal() {
        return 0;
    }


    public double calculateTax() {
        return 0;
    }


    public double calculateShippingCost() {
        return 0;
    }

    public String getItemDetails() {
        return null;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = Objects.requireNonNull(bookId, "Book ID cannot be null");
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = Objects.requireNonNull(bookTitle, "Book title cannot be null");
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative: " + unitPrice);
        }
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Item quantity must be at least 1. Given: " + quantity);
        }
        this.quantity = quantity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(itemId, cartItem.itemId) ||
                (Objects.equals(bookId, cartItem.bookId) && itemType == cartItem.itemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, itemType);
    }
}

