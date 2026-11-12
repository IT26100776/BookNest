
// ==========================================
// BOOKNEST ADMIN DASHBOARD
// Load real book data from Spring Boot API
// ==========================================

const API_URL = "/api/books";


async function bookWriteHeaders() {
    const csrfCookie = document.cookie
        .split("; ")
        .find(row => row.startsWith("XSRF-TOKEN="));

    if (!csrfCookie) {
        throw new Error("CSRF token not found. Please refresh the page and try again.");
    }

    const csrfToken = decodeURIComponent(
        csrfCookie.substring("XSRF-TOKEN=".length)
    );

    return {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": csrfToken
    };
}


// Get HTML elements
const totalBooksElement = document.getElementById("totalBooks");
const totalStockElement = document.getElementById("totalStock");
const outOfStockElement = document.getElementById("outOfStock");
const inventoryValueElement = document.getElementById("inventoryValue");

const bookTableBody = document.getElementById("bookTableBody");


// Format prices in Sri Lankan Rupees
function formatPrice(price) {
    return "Rs. " + Number(price || 0).toLocaleString("en-LK", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}


// Create a table cell safely
function createCell(value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "";
    return cell;
}


// Load books from Spring Boot
async function loadBooks() {

    try {

        bookTableBody.innerHTML = "";

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Unable to retrieve books.");
        }

        const books = await response.json();

        // Update dashboard statistics
        updateDashboard(books);

        // Display books
        displayBooks(books);

    } catch (error) {

        console.error("Book loading error:", error);

        bookTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">
                    Failed to load books. Please check your backend.
                </td>
            </tr>
        `;
    }
}


// Update dashboard statistics
function updateDashboard(books) {

    const totalBooks = books.length;

    const totalStock = books.reduce(
        (sum, book) => sum + Number(book.stockQuantity || 0), 0
    );

    const outOfStock = books.filter(
        book => Number(book.stockQuantity || 0) === 0
    ).length;

    const inventoryValue = books.reduce(
        (sum, book) =>
            sum + (Number(book.price || 0) *
                   Number(book.stockQuantity || 0)), 0
    );

    totalBooksElement.textContent = totalBooks;

    totalStockElement.textContent = totalStock;

    outOfStockElement.textContent = outOfStock;

    inventoryValueElement.textContent = formatPrice(inventoryValue);
}



// Display books in the table
function displayBooks(books) {

    bookTableBody.innerHTML = "";

    if (books.length === 0) {

        const row = document.createElement("tr");
        const cell = createCell("No books have been added yet.");

        cell.colSpan = 6;
        cell.className = "text-center";

        row.appendChild(cell);
        bookTableBody.appendChild(row);

        return;
    }

    books.forEach(book => {

        const row = document.createElement("tr");

        row.appendChild(createCell("BK" + String(book.id).padStart(3, "0")));

        row.appendChild(createCell(book.title));

        row.appendChild(createCell(book.author));

        row.appendChild(createCell(formatPrice(book.price)));

        const stock = Number(book.stockQuantity || 0);

        const stockCell = createCell(stock);

        if (stock === 0) {
            stockCell.style.color = "#dc3545";
            stockCell.style.fontWeight = "bold";
        }

        row.appendChild(stockCell);

        // Create Edit button
        const actionCell = document.createElement("td");

        const editButton = document.createElement("button");
        editButton.textContent = "Edit";
        editButton.className = "edit-button";
        editButton.dataset.id = book.id;

        actionCell.appendChild(editButton);

// Create Delete button
const deleteButton = document.createElement("button");

deleteButton.textContent = "Delete";
deleteButton.className = "delete-button";
deleteButton.dataset.id = book.id;

actionCell.appendChild(deleteButton);

row.appendChild(actionCell);

        bookTableBody.appendChild(row);

    });

}



// Load books when the page opens
document.addEventListener("DOMContentLoaded", loadBooks);
	
	

// ==========================================
// ADD NEW BOOK
// ==========================================

const addBookForm = document.getElementById("addBookForm");

if (addBookForm) {

    addBookForm.addEventListener("submit", async function(event) {

        event.preventDefault();

        const message = document.getElementById("formMessage");

        const book = {

            title: document.getElementById("title").value.trim(),

            author: document.getElementById("author").value.trim(),

            isbn: document.getElementById("isbn").value.trim(),

            publisher: document.getElementById("publisher").value.trim(),

            genre: document.getElementById("genre").value.trim(),

            language: document.getElementById("language").value.trim(),

            price: Number(document.getElementById("price").value),

            stockQuantity: Number(document.getElementById("stockQuantity").value),

            pages: document.getElementById("pages").value === ""
                ? null
                : Number(document.getElementById("pages").value)

        };

        // Basic validation
        if (
            !book.title ||
            !book.author ||
            !book.isbn ||
            !Number.isFinite(book.price) ||
            book.price <= 0 ||
            !Number.isInteger(book.stockQuantity) ||
            book.stockQuantity < 0 ||
            (book.pages !== null &&
                (!Number.isInteger(book.pages) || book.pages < 1))
        ) {
            message.textContent = "Please enter valid book information.";
            message.style.color = "#dc3545";
            return;
        }

        try {

            message.textContent = "Saving book...";
            message.style.color = "#81766f";

            const response = await fetch(API_URL, {

                method: "POST",

                headers: await bookWriteHeaders(),

                body: JSON.stringify(book)

            });

            if (!response.ok) {

                const errorText = await response.text();

                throw new Error(errorText || "Unable to add book.");

            }

            const savedBook = await response.json();

            message.textContent =
                "Book successfully added! Generated ID: " + savedBook.id;

            message.style.color = "#198754";

            addBookForm.reset();

            // Refresh dashboard and book table
            await loadBooks();

        } catch (error) {

            console.error("Add book error:", error);

            message.textContent =
                "Failed to add book: " + error.message;

            message.style.color = "#dc3545";

        }

    });

}


// ==========================================
// UPDATE BOOK - SAVE EDITED INFORMATION
// ==========================================

const editBookForm = document.getElementById("editBookForm");

editBookForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const bookId = document.getElementById("editId").value;
    const editMessage = document.getElementById("editMessage");

    const updatedBook = {

        title: document.getElementById("editTitle").value.trim(),

        author: document.getElementById("editAuthor").value.trim(),

        isbn: document.getElementById("editIsbn").value.trim(),

        publisher: document.getElementById("editPublisher").value.trim(),

        genre: document.getElementById("editGenre").value.trim(),

        language: document.getElementById("editLanguage").value.trim(),

        price: Number(document.getElementById("editPrice").value),

        stockQuantity: Number(document.getElementById("editStock").value),

        pages: document.getElementById("editPages").value === ""
            ? null
            : Number(document.getElementById("editPages").value)

    };

    // Validate information
    if (
        !updatedBook.title ||
        !updatedBook.author ||
        !updatedBook.isbn ||
        !Number.isFinite(updatedBook.price) ||
        updatedBook.price <= 0 ||
        !Number.isInteger(updatedBook.stockQuantity) ||
        updatedBook.stockQuantity < 0 ||
        (updatedBook.pages !== null &&
            (!Number.isInteger(updatedBook.pages) || updatedBook.pages < 1))
    ) {

        editMessage.textContent = "Please enter valid book information.";
        editMessage.style.color = "#dc3545";

        return;
    }

    try {

        editMessage.textContent = "Updating book...";
        editMessage.style.color = "#81766f";

        const response = await fetch(API_URL + "/" + bookId, {

            method: "PUT",

            headers: await bookWriteHeaders(),

            body: JSON.stringify(updatedBook)

        });

        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(errorText || "Unable to update book.");

        }

        editMessage.textContent = "Book updated successfully!";
        editMessage.style.color = "#198754";

        // Refresh dashboard and book table
        await loadBooks();

        // Hide form after successful update
        setTimeout(() => {

            document.getElementById("edit-book").style.display = "none";

            editBookForm.reset();

        }, 1000);

    } catch (error) {

        console.error("Book update error:", error);

        editMessage.textContent = "Update failed: " + error.message;
        editMessage.style.color = "#dc3545";

    }

});


// ==========================================
// EDIT BOOK - LOAD EXISTING BOOK DETAILS
// ==========================================

// Listen for clicks on Edit buttons
bookTableBody.addEventListener("click", async function(event) {

    if (!event.target.classList.contains("edit-button")) {
        return;
    }

    const bookId = event.target.dataset.id;
    const editMessage = document.getElementById("editMessage");
    const editSection = document.getElementById("edit-book");

    try {

        editMessage.textContent = "Loading book details...";
        editMessage.style.color = "#81766f";

        // Retrieve the selected book
        const response = await fetch(API_URL + "/" + bookId);

        if (!response.ok) {
            throw new Error("Unable to retrieve this book.");
        }

        const book = await response.json();

        // Fill the editing form
        document.getElementById("editId").value = book.id;
        document.getElementById("editTitle").value = book.title || "";
        document.getElementById("editAuthor").value = book.author || "";
        document.getElementById("editIsbn").value = book.isbn || "";
        document.getElementById("editPublisher").value = book.publisher || "";
        document.getElementById("editGenre").value = book.genre || "";
        document.getElementById("editLanguage").value = book.language || "";
        document.getElementById("editPrice").value = book.price;
        document.getElementById("editStock").value = book.stockQuantity;
        document.getElementById("editPages").value = book.pages ?? "";

        // Show the editing section
        editSection.style.display = "block";

        editMessage.textContent = "";

        editSection.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });

    } catch (error) {

        console.error("Edit loading error:", error);

        editMessage.textContent = error.message;
        editMessage.style.color = "#dc3545";

    }

});


// Cancel editing
document.getElementById("cancelEdit").addEventListener("click", function() {

    document.getElementById("edit-book").style.display = "none";

    document.getElementById("editBookForm").reset();

});


// ==========================================
// DELETE BOOK
// ==========================================

bookTableBody.addEventListener("click", async function(event) {

    if (!event.target.classList.contains("delete-button")) {
        return;
    }

    const bookId = event.target.dataset.id;

    // Ask for confirmation
    const confirmed = confirm(
        "Are you sure you want to delete this book?\n\nBook ID: " + bookId +
        "\n\nThis action cannot be undone."
    );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(API_URL + "/" + bookId, {

            method: "DELETE",
            headers: await bookWriteHeaders()

        });

        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(errorText || "Unable to delete book.");

        }

        alert("Book successfully deleted!");

        // Refresh dashboard and inventory table
        await loadBooks();

    } catch (error) {

        console.error("Book deletion error:", error);

        alert("Failed to delete book: " + error.message);

    }

});



