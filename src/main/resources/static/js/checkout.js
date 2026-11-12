function getCsrfToken() {

    const cookie = document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='));

    return cookie
        ? decodeURIComponent(cookie.substring('XSRF-TOKEN='.length))
        : null;
}

document.addEventListener('DOMContentLoaded', async () => {
const form = document.getElementById('checkoutForm');
const itemsContainer = document.getElementById('checkoutItems');
const totalElement = document.getElementById('checkoutTotal');
const button = document.getElementById('placeOrderButton');
const message = document.getElementById('checkoutMessage');

// Wait until the BookNest catalogue and cart are ready
try {
    await window.BookNestReady;
} catch (error) {
    console.error('BookNest initialization failed:', error);
}

if (!window.BookNestCart) {
    message.textContent = 'Unable to load your shopping cart. Please refresh the page.';
    button.disabled = true;
    return;
}

// Display the current cart
function renderCheckout() {

    const items = window.BookNestCart.getItems();
    const subtotal = window.BookNestCart.getSubtotal();

    itemsContainer.replaceChildren();

    if (items.length === 0) {
        const emptyMessage = document.createElement('p');
        emptyMessage.textContent = 'Your shopping cart is empty.';
        itemsContainer.appendChild(emptyMessage);

        totalElement.textContent = 'Rs. 0.00';
        button.disabled = true;
        return;
    }

    items.forEach(item => {

        const itemElement = document.createElement('div');
        itemElement.className = 'checkout-item';

        const name = document.createElement('h5');
        name.textContent = item.title;

        const details = document.createElement('p');
        details.textContent =
            `${item.quantity} × Rs. ${item.unitPrice.toFixed(2)}`;

        const itemTotal = document.createElement('strong');
        itemTotal.textContent = `Rs. ${item.subtotal.toFixed(2)}`;

        itemElement.append(name, details, itemTotal);
        itemsContainer.appendChild(itemElement);
    });

    totalElement.textContent = `Rs. ${subtotal.toFixed(2)}`;
    button.disabled = false;
}

renderCheckout();

// Submit the order
form.addEventListener('submit', async (event) => {

    event.preventDefault();

    const items = window.BookNestCart.getItems();

    if (items.length === 0) {
        message.textContent = 'Your cart is empty.';
        return;
    }

    const payload = {
        customerId: '',
        customerName: document.getElementById('customerName').value.trim(),
        email: document.getElementById('customerEmail').value.trim(),
        phone: document.getElementById('customerPhone').value.trim(),
        deliveryAddress: document.getElementById('deliveryAddress').value.trim(),

        items: items.map(item => ({
            bookId: item.bookId,
            quantity: item.quantity
        }))
    };

    button.disabled = true;
    button.textContent = 'Placing Order...';
    message.textContent = '';

    try {

        const csrfToken = getCsrfToken();

if (!csrfToken) {
    throw new Error('Security token not found. Please refresh the page and try again.');
}

const response = await fetch('/api/orders', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': csrfToken
    },
    body: JSON.stringify(payload)
});

        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(
                result.error ||
                result.message ||
                `Order placement failed. HTTP ${response.status}`
            );
        }

        // Clear the cart only after successful order creation
        window.BookNestCart.clear();

        form.hidden = true;
        itemsContainer.replaceChildren();
        totalElement.textContent = 'Rs. 0.00';

        const orderNumber = result.orderId ?? result.id ?? 'Created';

        message.textContent =
            `Your order has been placed successfully! Order ID: ${orderNumber}. Payment status: Pending.`;

        message.style.color = 'green';

    } catch (error) {

        console.error('Order placement error:', error);

        message.textContent = error.message;
        message.style.color = 'red';

        button.disabled = false;
        button.textContent = 'Place Order';
    }
});
});