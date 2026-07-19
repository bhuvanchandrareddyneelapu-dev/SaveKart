/**
 * SaveKart Smart Search Engine
 * Features: Autocomplete Dropdown, Speech Recognition (Voice Search), Barcode Scanner
 */
document.addEventListener("DOMContentLoaded", function () {
    setupSmartSearch();
});

function setupSmartSearch() {
    const searchBox = document.querySelector(".search-box");
    if (!searchBox) return;

    const input = searchBox.querySelector("input");
    if (!input) return;

    // Create Autocomplete Suggestions Container
    const dropdown = document.createElement("div");
    dropdown.id = "autocompleteDropdown";
    dropdown.style.position = "absolute";
    dropdown.style.top = "100%";
    dropdown.style.left = "0";
    dropdown.style.right = "0";
    dropdown.style.background = "#16233e";
    dropdown.style.borderRadius = "12px";
    dropdown.style.marginTop = "8px";
    dropdown.style.boxShadow = "0 15px 30px rgba(0,0,0,0.5)";
    dropdown.style.zIndex = "1000";
    dropdown.style.display = "none";
    dropdown.style.overflow = "hidden";
    dropdown.style.border = "1px solid rgba(255,255,255,0.1)";

    searchBox.style.position = "relative";
    searchBox.appendChild(dropdown);

    // Create Voice & Barcode Search Buttons inside Search Box
    const iconGroup = document.createElement("div");
    iconGroup.style.display = "flex";
    iconGroup.style.alignItems = "center";
    iconGroup.style.gap = "10px";
    iconGroup.style.marginRight = "10px";

    const voiceBtn = document.createElement("i");
    voiceBtn.className = "fa fa-microphone";
    voiceBtn.title = "Voice Search";
    voiceBtn.style.fontSize = "20px";
    voiceBtn.style.color = "#38bdf8";
    voiceBtn.style.cursor = "pointer";

    const barcodeBtn = document.createElement("i");
    barcodeBtn.className = "fa fa-barcode";
    barcodeBtn.title = "Scan Barcode / Image";
    barcodeBtn.style.fontSize = "20px";
    barcodeBtn.style.color = "#ec4899";
    barcodeBtn.style.cursor = "pointer";

    iconGroup.appendChild(voiceBtn);
    iconGroup.appendChild(barcodeBtn);

    const searchBtn = searchBox.querySelector("button");
    if (searchBtn) {
        searchBox.insertBefore(iconGroup, searchBtn);
    }

    // Input Autocomplete Listener
    let debounceTimer;
    input.addEventListener("input", function () {
        clearTimeout(debounceTimer);
        const query = this.value.trim();

        if (query.length < 2) {
            dropdown.style.display = "none";
            return;
        }

        debounceTimer = setTimeout(async () => {
            try {
                const results = await SaveKartAPI.autocomplete(query);
                renderAutocompleteResults(results, dropdown, input);
            } catch (e) {
                dropdown.style.display = "none";
            }
        }, 250);
    });

    // Voice Search Listener (Speech Recognition API)
    voiceBtn.addEventListener("click", function () {
        if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
            showToast("Speech Recognition not supported in this browser.", "error");
            return;
        }
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        const recognition = new SpeechRecognition();
        recognition.lang = 'en-US';

        showToast("Listening... Speak product name now", "info");
        voiceBtn.style.color = "#ef4444";

        recognition.onresult = function (event) {
            const transcript = event.results[0][0].transcript;
            input.value = transcript;
            voiceBtn.style.color = "#38bdf8";
            showToast(`Voice Search: "${transcript}"`, "success");
            window.location.href = `products.html?search=${encodeURIComponent(transcript)}`;
        };

        recognition.onerror = function () {
            voiceBtn.style.color = "#38bdf8";
            showToast("Voice recognition error. Try typing.", "error");
        };

        recognition.start();
    });

    // Barcode Search Listener
    barcodeBtn.addEventListener("click", function () {
        const mockBarcode = prompt("Enter product Barcode SKU (e.g., SKU-FREEDOMOIL1LTR):", "SKU-FREEDOMOIL1LTR");
        if (mockBarcode) {
            window.location.href = `products.html?search=${encodeURIComponent(mockBarcode)}`;
        }
    });

    // Close dropdown on outside click
    document.addEventListener("click", function (e) {
        if (!searchBox.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });
}

function renderAutocompleteResults(results, dropdown, input) {
    if (!results || results.length === 0) {
        dropdown.style.display = "none";
        return;
    }

    dropdown.innerHTML = results.map(item => `
        <div class="autocomplete-item" data-id="${item.id}" data-name="${item.name}" style="display:flex; align-items:center; gap:12px; padding:12px 18px; cursor:pointer; border-bottom:1px solid rgba(255,255,255,0.05); transition:0.2s;">
            <img src="${item.image}" alt="${item.name}" style="width:40px; height:40px; object-fit:contain; border-radius:6px; background:#07152d; padding:4px;">
            <div style="flex:1;">
                <div style="color:#fff; font-weight:600; font-size:15px;">${item.name}</div>
                <div style="color:#94a3b8; font-size:12px;">Category: ${item.category || 'General'}</div>
            </div>
            <div style="color:#38bdf8; font-weight:700; font-size:14px;">₹${item.mrp || 0}</div>
        </div>
    `).join("");

    dropdown.style.display = "block";

    dropdown.querySelectorAll(".autocomplete-item").forEach(el => {
        el.addEventListener("hover", function() {
            this.style.background = "#22314d";
        });
        el.addEventListener("click", function () {
            const id = this.getAttribute("data-id");
            window.location.href = `product-detail.html?id=${id}`;
        });
    });
}
