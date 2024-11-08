const apiUrl = '/api';
let cart = [];

document.addEventListener('DOMContentLoaded', () => {
    loadBooks();

    document.getElementById('searchType').addEventListener('change', updateSearchInputs);
    document.getElementById('searchBtn').addEventListener('click', searchBooks);
    document.getElementById('viewCartBtn').addEventListener('click', viewCart);
});

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

        bookDiv.innerHTML = `
            <h2>${book.title}</h2>
            <p><strong>Author:</strong> ${author}</p>
            <p>${book.description}</p>
            <p><strong>Price:</strong> ${price}</p>
            <button class="addToCartBtn" data-id="${book.id}">Add to Cart</button>
        `;

        content.appendChild(bookDiv);
    });

    document.querySelectorAll('.addToCartBtn').forEach(button => {
        button.addEventListener('click', addToCart);
    });
}

function addToCart(event) {
    const bookId = event.target.getAttribute('data-id');
    const existingItem = cart.find(item => item.bookId === bookId);
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({ bookId, quantity: 1 });
    }
    updateCartCount();
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
    const userId = 1; // Placeholder for user ID
    fetch(`${apiUrl}/purchase/checkout?userId=${userId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(cart)
    })
        .then(response => {
            if (response.ok) {
                alert('Checkout successful!');
                cart = [];
                updateCartCount();
                loadBooks();
            } else {
                alert('Checkout failed.');
            }
        })
        .catch(error => {
            console.error('Error during checkout:', error);
            alert('An error occurred during checkout.');
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
            alert('Login successful!');
            loginModal.style.display = 'none';
            // Save token or update UI with login state
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

