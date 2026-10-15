package ShoppingCart;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }


    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @PathVariable("userId") String userId,
            @Valid @RequestBody AddToCartRequest request) {

        CartResponse response = shoppingCartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Item successfully added to cart", response));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> viewCart(
            @PathVariable("userId") String userId) {

        CartResponse response = shoppingCartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok("Shopping cart retrieved successfully", response));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> viewDefaultCart() {
        CartResponse response = shoppingCartService.getCartByUserId("default_user");
        return ResponseEntity.ok(ApiResponse.ok("Shopping cart retrieved successfully", response));
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @PathVariable("userId") String userId,
            @PathVariable("itemId") String itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {

        CartResponse response = shoppingCartService.updateItemQuantity(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.ok("Item quantity updated successfully", response));
    }


    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable("userId") String userId,
            @PathVariable("itemId") String itemId) {

        CartResponse response = shoppingCartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart successfully", response));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @PathVariable("userId") String userId) {

        CartResponse response = shoppingCartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Shopping cart cleared successfully", response));
    }


    @PostMapping("/{userId}/discount")
    public ResponseEntity<ApiResponse<CartResponse>> applyDiscount(
            @PathVariable("userId") String userId,
            @Valid @RequestBody ApplyDiscountRequest request) {

        CartResponse response = shoppingCartService.applyDiscount(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Discount strategy applied successfully", response));
    }


    @PostMapping("/{userId}/checkout")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> checkout(
            @PathVariable("userId") String userId) {

        CartResponse currentCart = shoppingCartService.getCartByUserId(userId);
        CartSummaryResponse summary = currentCart.getSummary();

        // Clear cart upon successful checkout
        shoppingCartService.clearCart(userId);

        return ResponseEntity.ok(ApiResponse.ok("Checkout completed successfully! Order total: $" + summary.getGrandTotal(), summary));
    }
}

