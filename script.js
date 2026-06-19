// Search Button
function searchProduct() {
    let product = document.getElementById("searchInput").value.trim();

    if (product === "") {
        alert("Please enter a product name");
        return;
    }

    window.location.href = `products.html?search=${product}`;
}

// Category Click
function openCategory(category) {
    window.location.href = `products.html?category=${category}`;
}

// Run only after page loads
document.addEventListener("DOMContentLoaded", function () {

    // Trending Chips Click
    document.querySelectorAll(".chips span").forEach(chip => {
        chip.addEventListener("click", function () {
            let value = this.innerText;
            window.location.href = `products.html?search=${value}`;
        });
    });

    // Learn More Button
    let learnBtn = document.querySelector(".learn-btn");

    if (learnBtn) {
        learnBtn.addEventListener("click", function () {
            alert("SaveKart helps you compare prices across multiple apps and buy smart.");
        });
    }

    // Navbar Active Link
    document.querySelectorAll("nav a").forEach(link => {
        link.addEventListener("click", function () {
            document.querySelectorAll("nav a").forEach(a => a.classList.remove("active"));
            this.classList.add("active");
        });
    });

});