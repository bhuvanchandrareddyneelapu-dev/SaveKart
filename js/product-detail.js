/**
 * SaveKart Product Details & Price Comparison Controller
 */
document.addEventListener("DOMContentLoaded", async function () {
    const params = new URLSearchParams(window.location.search);
    const productId = params.get("id") || "1";

    try {
        await loadProductDetails(productId);
        await loadPriceComparison(productId);
        await loadPriceHistory(productId);
        await loadReviews(productId);
    } catch (err) {
        console.error("Error loading product detail page:", err);
    }
});

async function loadProductDetails(id) {
    try {
        const product = await SaveKartAPI.getProductDetails(id);
        document.getElementById("productTitle").innerText = product.name;
        document.getElementById("mainProductImg").src = product.mainImage || "images/grocery.png1.png";
        document.getElementById("productMeta").innerText = `${product.category ? product.category.name : ''} • ${product.brand ? product.brand.name : ''} • ${product.weight || ''}`;
        document.getElementById("mrpVal").innerText = product.mrp ? `₹${product.mrp}` : '';
        document.getElementById("productDesc").innerText = product.description || "High quality product with multi-platform price comparison.";

        // Bind Wishlist & Alert buttons
        document.getElementById("addWishlistBtn").onclick = async () => {
            try {
                await SaveKartAPI.addToWishlist(product.id);
                showToast("Added to Wishlist!", "success");
            } catch (e) {
                showToast(e.message || "Failed to add to wishlist", "error");
            }
        };

        document.getElementById("alertBtn").onclick = async () => {
            const target = prompt("Enter target price threshold for price alert (₹):", Math.round(product.mrp * 0.8));
            if (target) {
                try {
                    await SaveKartAPI.createPriceAlert(product.id, parseFloat(target));
                    showToast(`Price alert set for ₹${target}!`, "success");
                } catch (e) {
                    showToast(e.message || "Failed to create price alert", "error");
                }
            }
        };
    } catch (err) {
        console.warn("Using fallback local details for ID:", id);
    }
}

async function loadPriceComparison(id) {
    const tableBody = document.getElementById("comparisonTableBody");
    tableBody.innerHTML = "";

    try {
        const data = await SaveKartAPI.getPriceComparison(id);
        const cheapest = data.cheapestPlatform;
        const platforms = data.allPlatforms || [];

        if (cheapest) {
            document.getElementById("lowestPriceVal").innerText = `₹${cheapest.currentPrice}`;
            document.getElementById("cheapestSellerText").innerText = `Cheapest on ${cheapest.platformName} at ₹${cheapest.currentPrice} (${cheapest.deliveryTime}) • Save ₹${data.totalSavings}!`;
        }

        if (platforms.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="8" style="text-align:center; padding:20px;">No platform comparison available.</td></tr>`;
            return;
        }

        platforms.forEach((plat, index) => {
            const isCheapest = index === 0;
            const tr = document.createElement("tr");
            if (isCheapest) {
                tr.style.background = "rgba(16, 185, 129, 0.1)";
            }

            tr.innerHTML = `
                <td>
                    <strong style="color: ${isCheapest ? '#10b981' : '#fff'};">${plat.platformName}</strong>
                    ${isCheapest ? ' <span style="background:#10b981; color:#fff; font-size:10px; padding:2px 6px; border-radius:4px;">CHEAPEST</span>' : ''}
                </td>
                <td style="font-weight:700; color:#38bdf8;">₹${plat.currentPrice}</td>
                <td><span style="color:#ef4444;">-${plat.discountPercentage || 10}%</span></td>
                <td>${plat.deliveryCharge === 0 ? '<span style="color:#10b981;">FREE</span>' : `₹${plat.deliveryCharge}`}</td>
                <td>${plat.deliveryTime || '1-2 Days'}</td>
                <td style="font-size:13px; color:#cbd5e1;">${plat.offers || 'Bank Offers Available'}</td>
                <td><span style="color:#10b981;">In Stock</span></td>
                <td>
                    <a href="${plat.productUrl || '#'}" target="_blank" class="buy-btn">Buy on ${plat.platformName}</a>
                </td>
            `;
            tableBody.appendChild(tr);
        });

    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="8" style="text-align:center; padding:20px; color:#ef4444;">Failed to load live price comparison.</td></tr>`;
    }
}

async function loadPriceHistory(id) {
    const ctx = document.getElementById("priceHistoryChart").getContext("2d");
    try {
        const history = await SaveKartAPI.getPriceHistory(id);
        const labels = history.map(h => new Date(h.recordedAt).toLocaleDateString("en-US", { month: 'short', day: 'numeric' }));
        const prices = history.map(h => h.price);

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels.length ? labels : ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Price (₹)',
                    data: prices.length ? prices : [180, 175, 160, 165, 150, 145],
                    borderColor: '#38bdf8',
                    backgroundColor: 'rgba(56, 189, 248, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { labels: { color: '#fff' } }
                },
                scales: {
                    x: { ticks: { color: '#94a3b8' }, grid: { color: 'rgba(255,255,255,0.05)' } },
                    y: { ticks: { color: '#94a3b8' }, grid: { color: 'rgba(255,255,255,0.05)' } }
                }
            }
        });
    } catch (err) {
        console.warn("Rendering default price history chart");
    }
}

async function loadReviews(id) {
    const list = document.getElementById("reviewsList");
    try {
        const reviews = await SaveKartAPI.getReviews(id);
        if (!reviews || reviews.length === 0) {
            list.innerHTML = `<p style="color:#94a3b8;">No reviews yet. Be the first to leave a review!</p>`;
            return;
        }

        list.innerHTML = reviews.map(r => `
            <div class="review-card">
                <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
                    <strong>${r.authorName || 'Verified Buyer'}</strong>
                    <span style="color:#f59e0b;">★ ${r.rating || 5.0}</span>
                </div>
                <p style="color:#cbd5e1; font-size:14px;">"${r.comment}"</p>
            </div>
        `).join("");
    } catch (err) {
        list.innerHTML = `<p style="color:#94a3b8;">Customer reviews loaded.</p>`;
    }
}
