package ShoppingCart;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {

    @NotBlank(message = "Book ID is required")
    private String bookId;

    @NotBlank(message = "Book title is required")
    private String bookTitle;

    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    private double unitPrice;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    @NotNull(message = "Item type is required (PHYSICAL_BOOK or DIGITAL_BOOK)")
    private ItemType itemType = ItemType.PHYSICAL_BOOK;

    // Physical Book specific attributes (optional for digital items)
    private double weightKg = 0.5; // default 0.5 kg
    private String coverType = "Paperback";

    // Digital Book specific attributes (optional for physical items)
    private double fileSizeMb = 12.0; // default 12 MB
    private String downloadFormat = "PDF";
    private boolean drmProtected = true;

    public AddToCartRequest() {
    }

    /**
     * Factory method: Instantiates the appropriate polymorphic subclass of CartItem.
     * Demonstrates Polymorphism and Object-Oriented Factory pattern.
     *
     * @return Concrete CartItem (PhysicalBookCartItem or DigitalBookCartItem)
     */
    public CartItem toDomainCartItem() {
        if (itemType == ItemType.DIGITAL_BOOK) {
            return new DigitalBookCartItem(
                    this.bookId,
                    this.bookTitle,
                    this.unitPrice,
                    this.quantity,
                    this.fileSizeMb,
                    this.downloadFormat,
                    this.drmProtected
            );
        } else {
            return new PhysicalBookCartItem(
                    this.bookId,
                    this.bookTitle,
                    this.unitPrice,
                    this.quantity,
                    this.weightKg,
                    this.coverType
            );
        }
    }

    // Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        this.fileSizeMb = fileSizeMb;
    }

    public String getDownloadFormat() {
        return downloadFormat;
    }

    public void setDownloadFormat(String downloadFormat) {
        this.downloadFormat = downloadFormat;
    }

    public boolean isDrmProtected() {
        return drmProtected;
    }

    public void setDrmProtected(boolean drmProtected) {
        this.drmProtected = drmProtected;
    }
}

