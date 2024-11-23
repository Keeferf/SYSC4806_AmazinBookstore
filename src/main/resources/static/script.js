const apiUrl = '/api';
let cart = [];

let currentUserId = 1; // Default to customer user ID
let currentUserRole = 'customer'; // Default role

const customerUserId = 1; // ID of the customer user
const adminUserId = 2;    // ID of the admin user

document.addEventListener('DOMContentLoaded', () => {
    // Existing event listeners
    document.getElementById('searchType').addEventListener('change', updateSearchInputs);
    document.getElementById('searchBtn').addEventListener('click', searchBooks);
    document.getElementById('viewCartBtn').addEventListener('click', viewCart);
    document.getElementById('roleSwitchBtn').addEventListener('click', switchRole);
    document.getElementById('homeBtn').addEventListener('click', showHome);
    document.getElementById('viewHistoryBtn').addEventListener('click', viewPurchaseHistory);

    // Load initial content based on role
    updateLoginStatus();
    updateRoleView();
});

function switchRole() {
    if (currentUserRole === 'customer') {
        currentUserId = adminUserId;
        currentUserRole = 'admin';
        document.getElementById('roleSwitchBtn').textContent = 'Switch to Customer View';
    } else {
        currentUserId = customerUserId;
        currentUserRole = 'customer';
        document.getElementById('roleSwitchBtn').textContent = 'Switch to Admin View';
    }
    updateRoleView();
}

function updateRoleView() {
    const customerActions = document.getElementById('customerActions');

    if (hasRole('admin')) {
        // Hide customer-specific elements
        customerActions.style.display = 'none';
        loadAdminView();
    } else {
        // Show customer-specific elements
        customerActions.style.display = 'flex';
        loadBooks();
    }
}

function viewPurchaseHistory() {
    const content = document.getElementById('content');
    content.innerHTML = '<h2>Purchase History</h2>';
    const token = getAuthToken();
    if(!token) {
        alert("You must log in to view your purchase history");
        return;
    }

    fetch(`${apiUrl}/purchase/history`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })

    .then(response => {
        if (!response.ok) {
            throw new Error(`Failed to fetch purchase history. Status: ${response.status}`);
        }
        return response.json();
    })
    .then(purchases => {
        if (!purchases || purchases.length === 0) {
            content.innerHTML += '<p>No purchase history found.</p>';
            return;
        }
        // Sort purchases by date in descending order (most recent first)
        const sortedPurchases = purchases.sort((a, b) =>
            new Date(b.purchaseDate) - new Date(a.purchaseDate)
        );

        const purchasePromises = sortedPurchases.map(purchase => {
            const purchaseDate = new Date(purchase.purchaseDate).toLocaleString();

            const itemPromises = purchase.items.map(item =>
                fetch(`${apiUrl}/books/${item.bookId}`)
                    .then(response => response.json())
                    .then(book => ({
                        ...book,
                        quantity: item.quantity
                    }))
            );

            return Promise.all(itemPromises).then(items => ({
                purchaseDate,
                items
            }));
        });

        return Promise.all(purchasePromises);
    })
        .then(purchasesWithDetails => {
            purchasesWithDetails.forEach(purchase => {
                const purchaseDiv = document.createElement('div');
                purchaseDiv.className = 'purchase-record';

                let totalItems = 0;
                let totalCost = 0;

                // Build items list and calculate totals
                const itemsList = purchase.items.map(item => {
                    totalItems += item.quantity;
                    const itemTotal = item.price * item.quantity;
                    totalCost += itemTotal;

                    return `
                    <div class="purchase-item">
                        <p><strong>${item.title}</strong> by ${item.author || 'Unknown Author'}</p>
                        <p>Quantity: ${item.quantity}</p>
                        <p>Price: $${item.price.toFixed(2)}</p>
                        <p>Total: $${itemTotal.toFixed(2)}</p>
                    </div>
                `;
                }).join('');

                purchaseDiv.innerHTML = `
                <div class="purchase-header">
                    <h3>Purchase Date: ${purchase.purchaseDate}</h3>
                    <p><strong>Total Items:</strong> ${totalItems}</p>
                    <p><strong>Total Cost:</strong> $${totalCost.toFixed(2)}</p>
                </div>
                <div class="purchase-items">
                    ${itemsList}
                </div>
                <hr>
            `;

                content.appendChild(purchaseDiv);
            });
        })
        .catch(error => {
            console.error('Error fetching purchase history:', error);
            content.innerHTML += '<p>Error loading purchase history. Please try again later.</p>';
        });
}

function loadBooks() {
    fetch(`${apiUrl}/books`)
        .then(response => response.json())
        .then(books => {
            displayBooks(books);
        })
        .catch(error => {
            console.error('Error fetching books:', error);
        });
}

function showHome() {
    if (currentUserRole === 'admin') {
        loadAdminView();
    } else {
        loadBooks();
    }
    updateRoleView();
}

function updateSearchInputs() {
    const searchType = document.getElementById('searchType').value;
    const searchInput = document.getElementById('searchInput');
    const minValue = document.getElementById('minValue');
    const maxValue = document.getElementById('maxValue');

    // Reset input fields
    searchInput.style.display = 'none';
    minValue.style.display = 'none';
    maxValue.style.display = 'none';

    if (searchType === 'title' || searchType === 'author' || searchType === 'publisher') {
        searchInput.placeholder = `Enter ${searchType} keyword...`;
        searchInput.style.display = 'inline-block';
    } else if (searchType === 'price' || searchType === 'inventory') {
        minValue.style.display = 'inline-block';
        if (searchType === 'price') {
            maxValue.style.display = 'inline-block';
            minValue.placeholder = 'Min price';
            maxValue.placeholder = 'Max price';
        } else {
            minValue.placeholder = 'Min inventory';
        }
    }
}

function searchBooks() {
    const searchType = document.getElementById('searchType').value;
    const searchInput = document.getElementById('searchInput').value.trim();
    const minValue = document.getElementById('minValue').value;
    const maxValue = document.getElementById('maxValue').value;
    let endpoint;

    if (searchType === 'title') {
        endpoint = `${apiUrl}/books/search?keyword=${encodeURIComponent(searchInput)}`;
    } else if (searchType === 'author') {
        endpoint = `${apiUrl}/books/search/author?author=${encodeURIComponent(searchInput)}`;
    } else if (searchType === 'publisher') {
        endpoint = `${apiUrl}/books/search/publisher?publisher=${encodeURIComponent(searchInput)}`;
    } else if (searchType === 'price') {
        endpoint = `${apiUrl}/books/filter/price?minPrice=${encodeURIComponent(minValue)}&maxPrice=${encodeURIComponent(maxValue)}`;
    } else if (searchType === 'inventory') {
        endpoint = `${apiUrl}/books/filter/inventory?minInventory=${encodeURIComponent(minValue)}`;
    } else {
        alert('Invalid search type selected.');
        return;
    }

    fetch(endpoint)
        .then(response => {
            if (!response.ok) {
                throw new Error('Search failed');
            }
            return response.json();
        })
        .then(books => {
            displayBooks(books);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while searching for books.');
        });
}

function displayBooks(books) {
    const content = document.getElementById('content');
    content.innerHTML = '';

    if (books.length === 0) {
        content.innerHTML = '<p>No books found.</p>';
        return;
    }

    books.forEach(book => {
        const bookDiv = document.createElement('div');
        bookDiv.className = 'book';

        const author = book.author || 'Unknown Author';
        const price = book.price !== undefined ? `$${book.price.toFixed(2)}` : 'Price not available';
        const inventoryText = book.inventory > 0 ? `In Stock: ${book.inventory}` : 'Out of Stock';

        // Only show "Add to Cart" button for customers
        const addToCartButton = currentUserRole === 'customer' && book.inventory > 0
            ? `<button class="addToCartBtn" data-id="${book.id}">Add to Cart</button>`
            : '';

        // Only show edit and delete buttons for admins
        const adminButtons = currentUserRole === 'admin'
            ? `
                <button class="editBookBtn" data-id="${book.id}">Edit</button>
                <button class="deleteBookBtn" data-id="${book.id}">Delete</button>
              `
            : '';

        bookDiv.innerHTML = `
            <h2>${book.title}</h2>
            <p><strong>Author:</strong> ${author}</p>
            <p>${book.description}</p>
            <p><strong>Price:</strong> ${price}</p>
            <p><strong>Inventory:</strong> ${inventoryText}</p>
            ${addToCartButton}
            ${adminButtons}
        `;

        content.appendChild(bookDiv);
    });

    // Attach event listeners based on role
    if (currentUserRole === 'customer') {
        document.querySelectorAll('.addToCartBtn').forEach(button => {
            button.addEventListener('click', addToCart);
        });
    }

    if (currentUserRole === 'admin') {
        document.querySelectorAll('.editBookBtn').forEach(button => {
            button.addEventListener('click', editBook);
        });
        document.querySelectorAll('.deleteBookBtn').forEach(button => {
            button.addEventListener('click', deleteBook);
        });
    }
}

function loadAdminView() {
    fetch(`${apiUrl}/books`)
        .then(response => response.json())
        .then(books => {
            displayBooks(books);
            // Add a button to upload a new book
            const content = document.getElementById('content');
            const uploadButton = document.createElement('button');
            uploadButton.textContent = 'Add New Book';
            uploadButton.addEventListener('click', showAddBookForm);
            content.prepend(uploadButton);
        })
        .catch(error => {
            console.error('Error fetching books:', error);
        });
}

function showAddBookForm() {
    const content = document.getElementById('content');
    content.innerHTML = `
        <h2>Add New Book</h2>
        <form id="addBookForm">
            <label>ISBN: <input type="text" name="isbn" required></label><br>
            <label>Title: <input type="text" name="title" required></label><br>
            <label>Author: <input type="text" name="author" required></label><br>
            <label>Description: <textarea name="description"></textarea></label><br>
            <label>Publisher: <input type="text" name="publisher"></label><br>
            <label>Picture URL: <input type="text" name="pictureURL"></label><br>
            <label>Price: <input type="number" step="0.01" name="price" required></label><br>
            <label>Inventory: <input type="number" name="inventory" required></label><br>
            <button type="submit">Add Book</button>
        </form>
    `;

    document.getElementById('addBookForm').addEventListener('submit', submitNewBook);
}

function submitNewBook(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const bookData = {};
    formData.forEach((value, key) => {
        bookData[key] = key === 'price' || key === 'inventory' ? Number(value) : value;
    });

    fetch(`${apiUrl}/books?userId=${currentUserId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bookData)
    })
        .then(response => {
            if (response.ok) {
                alert('Book added successfully!');
                loadAdminView();
            } else {
                return response.text().then(text => { throw new Error(text); });
            }
        })
        .catch(error => {
            console.error('Error adding book:', error);
            alert('An error occurred while adding the book: ' + error.message);
        });
}

function editBook(event) {
    const bookId = event.target.getAttribute('data-id');
    fetch(`${apiUrl}/books/${bookId}`)
        .then(response => response.json())
        .then(book => {
            const content = document.getElementById('content');
            content.innerHTML = `
                <h2>Edit Book</h2>
                <form id="editBookForm">
                    <input type="hidden" name="id" value="${book.id}">
                    <label>ISBN: <input type="text" name="isbn" value="${book.isbn}" required></label><br>
                    <label>Title: <input type="text" name="title" value="${book.title}" required></label><br>
                    <label>Author: <input type="text" name="author" value="${book.author}" required></label><br>
                    <label>Description: <textarea name="description">${book.description}</textarea></label><br>
                    <label>Publisher: <input type="text" name="publisher" value="${book.publisher}"></label><br>
                    <label>Picture URL: <input type="text" name="pictureURL" value="${book.pictureURL}"></label><br>
                    <label>Price: <input type="number" step="0.01" name="price" value="${book.price}" required></label><br>
                    <label>Inventory: <input type="number" name="inventory" value="${book.inventory}" required></label><br>
                    <button type="submit">Update Book</button>
                </form>
            `;

            document.getElementById('editBookForm').addEventListener('submit', submitEditBook);
        })
        .catch(error => {
            console.error('Error fetching book data:', error);
            alert('An error occurred while fetching the book details.');
        });
}

function submitEditBook(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const bookData = {};
    formData.forEach((value, key) => {
        bookData[key] = key === 'price' || key === 'inventory' ? Number(value) : value;
    });
    const bookId = bookData.id;

    fetch(`${apiUrl}/books/${bookId}?userId=${currentUserId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bookData)
    })
        .then(response => {
            if (response.ok) {
                alert('Book updated successfully!');
                loadAdminView();
            } else {
                return response.text().then(text => { throw new Error(text); });
            }
        })
        .catch(error => {
            console.error('Error updating book:', error);
            alert('An error occurred while updating the book: ' + error.message);
        });
}

function deleteBook(event) {
    const bookId = event.target.getAttribute('data-id');
    if (confirm('Are you sure you want to delete this book?')) {
        fetch(`${apiUrl}/books/${bookId}?userId=${currentUserId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert('Book deleted successfully!');
                    loadAdminView();
                } else {
                    return response.text().then(text => { throw new Error(text); });
                }
            })
            .catch(error => {
                console.error('Error deleting book:', error);
                alert('An error occurred while deleting the book: ' + error.message);
            });
    }
}

function addToCart(event) {
    const bookId = event.target.getAttribute('data-id');

    fetch(`${apiUrl}/books/${bookId}`)
        .then(response => response.json())
        .then(book => {
            const existingItem = cart.find(item => item.bookId === bookId);
            const currentQuantityInCart = existingItem ? existingItem.quantity : 0;

            if (currentQuantityInCart < book.inventory) {
                if (existingItem) {
                    existingItem.quantity += 1;
                } else {
                    cart.push({ bookId, quantity: 1 });
                }
                updateCartCount();
            } else {
                alert('Cannot add more items than available in inventory.');
            }
        })
        .catch(error => {
            console.error('Error fetching book data:', error);
            alert('An error occurred while adding the item to the cart.');
        });
}

function updateCartCount() {
    document.getElementById('cartCount').textContent = cart.reduce((total, item) => total + item.quantity, 0);
}

function viewCart() {
    const content = document.getElementById('content');
    content.innerHTML = '<h2>Your Cart</h2>';

    if (cart.length === 0) {
        content.innerHTML += '<p>Your cart is empty.</p>';
        return;
    }

    const cartItemsContainer = document.createElement('div');
    content.appendChild(cartItemsContainer);

    const fetchPromises = cart.map(item => {
        return fetch(`${apiUrl}/books/${item.bookId}`)
            .then(response => response.json())
            .then(book => {
                const itemDiv = document.createElement('div');
                itemDiv.className = 'book';

                const price = book.price || 0;

                itemDiv.innerHTML = `
                    <h2>${book.title}</h2>
                    <p><strong>Quantity:</strong> ${item.quantity}</p>
                    <p><strong>Total Price:</strong> $${(price * item.quantity).toFixed(2)}</p>
                `;

                cartItemsContainer.appendChild(itemDiv);
            });
    });

    Promise.all(fetchPromises).then(() => {
        const checkoutBtn = document.createElement('button');
        checkoutBtn.textContent = 'Checkout';
        checkoutBtn.addEventListener('click', checkout);
        content.appendChild(checkoutBtn);
    });
}

function checkout() {
    // Validate cart quantities against inventory
    const validationPromises = cart.map(cartItem => {
        return fetch(`${apiUrl}/books/${cartItem.bookId}`)
            .then(response => response.json())
            .then(book => {
                if (cartItem.quantity > book.inventory) {
                    throw new Error(`Not enough inventory for ${book.title}. Available: ${book.inventory}, In Cart: ${cartItem.quantity}`);
                }
            });
    });
    const token = getAuthToken();
    if (!token) {
        alert('You must log in to proceed with the checkout.');
        return;
    }

    Promise.all(validationPromises)
        .then(() => {
            return fetch(`${apiUrl}/purchase/checkout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(cart)
            });
        })
        .then(response => {
            if (response.ok) {
                alert('Checkout successful!');
                cart = [];
                updateCartCount();
                loadBooks();
            } else {
                return response.text().then(text => { throw new Error(text); });
            }
        })
        .catch(error => {
            console.error('Error during checkout:', error);
            alert(error.message);
        });
}

// Selectors for login and signup buttons
const loginButton = document.getElementById('loginBtn');
const signupButton = document.getElementById('signupBtn');

// Selectors for modals
const loginModal = document.getElementById('loginModal');
const signupModal = document.getElementById('signupModal');

// Selectors for modal close buttons
const closeLogin = document.getElementById('closeLogin');
const closeSignup = document.getElementById('closeSignup');

const jwt_token = 'jwt_token'

// Event listeners for login and signup buttons to open modals
loginButton.addEventListener('click', () => {
    loginModal.style.display = 'block';  // Show login modal
});

signupButton.addEventListener('click', () => {
    signupModal.style.display = 'block';  // Show signup modal
});

// Event listeners for closing modals (clicking on the "X" button)
closeLogin.addEventListener('click', () => {
    loginModal.style.display = 'none';  // Close login modal
});

closeSignup.addEventListener('click', () => {
    signupModal.style.display = 'none';  // Close signup modal
});

// Close modal when clicking outside of the modal content
window.addEventListener('click', (event) => {
    if (event.target === loginModal) {
        loginModal.style.display = 'none';  // Close login modal if outside of content
    } else if (event.target === signupModal) {
        signupModal.style.display = 'none';  // Close signup modal if outside of content
    }
});

// Handle login form submission
document.getElementById('loginForm').addEventListener('submit', (event) => {
    event.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    fetch(`${apiUrl}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
        .then(response => {
            if (!response.ok) throw new Error('Login failed');
            return response.json();
        })
        .then(data => {
            if(data.token) {
                localStorage.setItem(jwt_token, data.token)
                alert('Login successful!');
                loginModal.style.display = 'none';
                updateLoginStatus();
            }
        })
        .catch(error => {
            console.error('Error logging in:', error);
            alert('Login failed. Please check your credentials.');
        });
});

// Handle signup form submission
document.getElementById('signupForm').addEventListener('submit', (event) => {
    event.preventDefault();

    const username = document.getElementById('signupUsername').value.trim();
    const password = document.getElementById('signupPassword').value.trim();
    const email = document.getElementById('signupEmail').value.trim();
    const firstName = document.getElementById('signupFirstName').value.trim();
    const lastName = document.getElementById('signupLastName').value.trim();

    if (!username || !password || !email || !firstName || !lastName) {
        alert("All fields are required.");
        return;
    }

    fetch(`${apiUrl}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, email, firstName, lastName })
    })
        .then(response => {
            if (!response.ok) {
                if (response.headers.get('Content-Type')?.includes('application/json')) {
                    return response.json().then(error => {
                        throw new Error(error.message || 'Signup failed');
                    });
                } else {
                    throw new Error('Signup failed: Non-JSON response from server');
                }
            }
            return response.json();
        })
        .then(data => {
            alert('Signup successful! You can now log in.');
            signupModal.style.display = 'none';
        })
        .catch(error => {
            console.error('Error signing up:', error);
            alert(`Signup failed: ${error.message}`);
        });
});

function getAuthToken() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        console.log('No token found'); // Debug log
        return null;
    }
    return token;
}

function getCurrentUser() {
    const token = getAuthToken();
    if (!token) return null;

    try {
        // JWT tokens are split into three parts by dots
        const payload = token.split('.')[1];
        // Decode the base64 payload
        const decoded = JSON.parse(atob(payload));
        console.log("Decoded token payload:", decoded); // Debugging log
        return decoded;
    } catch (error) {
        console.error('Error decoding token:', error);
        return null;
    }
}
// Add function to check user role
function hasRole(role) {
    const user = getCurrentUser();
    return user && user.role === role;
}

// Selectors for status display
const statusMessage = document.getElementById('statusMessage');
const logoutBtn = document.getElementById('logoutBtn');

// Function to check login status
function updateLoginStatus() {
    const token = getAuthToken();

    if (token) {
        const user = getCurrentUser(); // Decode the token to get user details
        if (user && user.sub) {
            statusMessage.textContent = `Logged in as ${user.sub}`; // Display username
            logoutBtn.style.display = 'inline-block'; // Show logout button
            loginButton.style.display = 'none';
            signupButton.style.display = 'none';
        } else {
            statusMessage.textContent = 'You are not logged in.';
            logoutBtn.style.display = 'none';
            loginButton.style.display = 'inline-block';
            signupButton.style.display = 'inline-block';
        }
    } else {
        statusMessage.textContent = 'You are not logged in.';
        logoutBtn.style.display = 'none';
        loginButton.style.display = 'inline-block';
        signupButton.style.display = 'inline-block';
    }
}
// Function to handle logout
logoutBtn.addEventListener('click', () => {
    localStorage.removeItem(jwt_token); // Remove token from localStorage
    alert('You have been logged out.');
    updateLoginStatus(); // Update the UI
    updateRoleView();
});


