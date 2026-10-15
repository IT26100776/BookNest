package ShoppingCart;


public class DigitalBookCartItem extends CartItem {

    private double fileSizeMb;
    private String downloadFormat; // e.g., "PDF", "EPUB", "MOBI"
    private boolean drmProtected;

    public static final double DIGITAL_BOOK_TAX_RATE = 0.02; // 2% Digital Services Tax



    public DigitalBookCartItem() {
        super();
        setItemType(ItemType.DIGITAL_BOOK);
        this.downloadFormat = "PDF";
        this.drmProtected = true;
    }



    public DigitalBookCartItem(String bookId, String bookTitle, double unitPrice, int quantity,
                               double fileSizeMb, String downloadFormat, boolean drmProtected) {
        super(bookId, bookTitle, unitPrice, quantity, ItemType.DIGITAL_BOOK);
        setFileSizeMb(fileSizeMb);
        this.downloadFormat = (downloadFormat != null && !downloadFormat.isBlank()) ? downloadFormat : "PDF";
        this.drmProtected = drmProtected;
    }


    @Override
    public double calculateSubtotal() {
        return getUnitPrice() * getQuantity();
    }



    @Override
    public double calculateShippingCost() {
        return 0.0;
    }



    @Override
    public double calculateTax() {
        return (getUnitPrice() * getQuantity()) * DIGITAL_BOOK_TAX_RATE;
    }



    @Override
    public String getItemDetails() {
        return String.format("Digital E-Book [Format: %s, Size: %.2f MB, DRM Protected: %b, Instant Delivery: $0.00 Shipping]",
                downloadFormat, fileSizeMb, drmProtected);
    }



    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        if (fileSizeMb < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + fileSizeMb);
        }
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

