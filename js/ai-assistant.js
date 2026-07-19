/**
 * SaveKart AI Shopping Assistant Floating Widget
 */
document.addEventListener("DOMContentLoaded", function () {
    injectAiAssistantWidget();
});

function injectAiAssistantWidget() {
    // Check if widget already injected
    if (document.getElementById("aiAssistantFAB")) return;

    // Floating Action Button
    const fab = document.createElement("button");
    fab.id = "aiAssistantFAB";
    fab.innerHTML = `<i class="fa fa-robot" style="font-size:22px;"></i> <span>AI Assistant</span>`;
    fab.style.position = "fixed";
    fab.style.bottom = "25px";
    fab.style.left = "25px";
    fab.style.background = "linear-gradient(135deg, #6366f1, #a855f7)";
    fab.style.color = "#fff";
    fab.style.border = "none";
    fab.style.padding = "14px 22px";
    fab.style.borderRadius = "30px";
    fab.style.fontWeight = "600";
    fab.style.cursor = "pointer";
    fab.style.boxShadow = "0 10px 25px rgba(99, 102, 241, 0.4)";
    fab.style.zIndex = "999";
    fab.style.display = "flex";
    fab.style.alignItems = "center";
    fab.style.gap = "10px";
    fab.style.transition = "all 0.3s ease";

    // Chat Window Overlay
    const modal = document.createElement("div");
    modal.id = "aiAssistantModal";
    modal.style.position = "fixed";
    modal.style.bottom = "85px";
    modal.style.left = "25px";
    modal.style.width = "380px";
    modal.style.height = "520px";
    modal.style.background = "#16233e";
    modal.style.borderRadius = "20px";
    modal.style.boxShadow = "0 20px 40px rgba(0,0,0,0.6)";
    modal.style.border = "1px solid rgba(255,255,255,0.1)";
    modal.style.zIndex = "1000";
    modal.style.display = "none";
    modal.style.flexDirection = "column";
    modal.style.overflow = "hidden";

    modal.innerHTML = `
        <div style="background:#0f172a; padding:16px 20px; display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid rgba(255,255,255,0.08);">
            <div style="display:flex; align-items:center; gap:10px;">
                <i class="fa fa-sparkles" style="color:#a855f7;"></i>
                <strong style="color:#fff; font-size:16px;">SaveKart AI Assistant</strong>
            </div>
            <button id="closeAiModalBtn" style="background:none; border:none; color:#94a3b8; font-size:18px; cursor:pointer;">&times;</button>
        </div>

        <div id="aiChatBody" style="flex:1; padding:16px; overflow-y:auto; display:flex; flex-direction:column; gap:12px;">
            <div style="background:rgba(255,255,255,0.05); padding:12px 16px; border-radius:12px; color:#cbd5e1; font-size:14px;">
                Hello! I am your AI Shopping Assistant. How can I help you save money today?
            </div>

            <div style="display:flex; flex-wrap:wrap; gap:8px;">
                <button class="ai-chip" data-query="Build my monthly grocery list under ₹4000">🛒 Grocery under ₹4000</button>
                <button class="ai-chip" data-query="Find the cheapest iPhone today">📱 Cheapest iPhone</button>
                <button class="ai-chip" data-query="Best milk products near me">🥛 Best Milk Products</button>
                <button class="ai-chip" data-query="Compare all 5-star refrigerators">🧊 5-Star Refrigerators</button>
            </div>
        </div>

        <div style="padding:14px; background:#0f172a; border-top:1px solid rgba(255,255,255,0.08); display:flex; gap:8px;">
            <input type="text" id="aiInput" placeholder="Ask AI Assistant..." style="flex:1; background:#16233e; border:none; padding:10px 14px; border-radius:10px; color:#fff; font-size:14px; outline:none;">
            <button id="aiSendBtn" style="background:#5865ff; color:#fff; border:none; padding:10px 16px; border-radius:10px; cursor:pointer;"><i class="fa fa-paper-plane"></i></button>
        </div>
    `;

    document.body.appendChild(fab);
    document.body.appendChild(modal);

    // Style chips
    const style = document.createElement("style");
    style.innerHTML = `
        .ai-chip {
            background: #22314d;
            color: #38bdf8;
            border: 1px solid rgba(56, 189, 248, 0.3);
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            cursor: pointer;
            transition: 0.2s;
        }
        .ai-chip:hover {
            background: #5865ff;
            color: #fff;
        }
    `;
    document.head.appendChild(style);

    // Event Handlers
    fab.onclick = () => {
        modal.style.display = modal.style.display === "none" ? "flex" : "none";
    };

    document.getElementById("closeAiModalBtn").onclick = () => {
        modal.style.display = "none";
    };

    const aiInput = document.getElementById("aiInput");
    const aiSendBtn = document.getElementById("aiSendBtn");
    const chatBody = document.getElementById("aiChatBody");

    async function sendAiMessage(text) {
        if (!text) return;

        // User message
        const userMsg = document.createElement("div");
        userMsg.style.alignSelf = "flex-end";
        userMsg.style.background = "#5865ff";
        userMsg.style.color = "#fff";
        userMsg.style.padding = "10px 14px";
        userMsg.style.borderRadius = "12px 12px 0 12px";
        userMsg.style.fontSize = "14px";
        userMsg.innerText = text;
        chatBody.appendChild(userMsg);
        chatBody.scrollTop = chatBody.scrollHeight;

        aiInput.value = "";

        // Bot loading placeholder
        const botMsg = document.createElement("div");
        botMsg.style.alignSelf = "flex-start";
        botMsg.style.background = "rgba(255,255,255,0.05)";
        botMsg.style.color = "#cbd5e1";
        botMsg.style.padding = "10px 14px";
        botMsg.style.borderRadius = "12px 12px 12px 0";
        botMsg.style.fontSize = "14px";
        botMsg.innerText = "Analyzing prices across 15+ platforms...";
        chatBody.appendChild(botMsg);
        chatBody.scrollTop = chatBody.scrollHeight;

        try {
            const data = await SaveKartAPI.askAiAssistant(text);
            botMsg.innerHTML = `<div>${data.answer || 'Found best deals for you:'}</div>`;

            if (data.products && data.products.length > 0) {
                const prodGrid = document.createElement("div");
                prodGrid.style.display = "flex";
                prodGrid.style.flexDirection = "column";
                prodGrid.style.gap = "8px";
                prodGrid.style.marginTop = "8px";

                data.products.slice(0, 4).forEach(p => {
                    const pCard = document.createElement("div");
                    pCard.style.display = "flex";
                    pCard.style.alignItems = "center";
                    pCard.style.gap = "10px";
                    pCard.style.background = "#0f172a";
                    pCard.style.padding = "8px";
                    pCard.style.borderRadius = "8px";
                    pCard.style.cursor = "pointer";

                    pCard.innerHTML = `
                        <img src="${p.image}" style="width:36px; height:36px; object-fit:contain;">
                        <div style="flex:1;">
                            <div style="font-size:12px; color:#fff; font-weight:600;">${p.name}</div>
                            <div style="font-size:11px; color:#38bdf8;">₹${p.price}</div>
                        </div>
                    `;
                    pCard.onclick = () => window.location.href = `product-detail.html?id=${p.id}`;
                    prodGrid.appendChild(pCard);
                });

                botMsg.appendChild(prodGrid);
            }
        } catch (e) {
            botMsg.innerText = "Here are top deals matching your query. Explore SaveKart products catalog!";
        }

        chatBody.scrollTop = chatBody.scrollHeight;
    }

    aiSendBtn.onclick = () => sendAiMessage(aiInput.value.trim());
    aiInput.onkeypress = (e) => {
        if (e.key === 'Enter') sendAiMessage(aiInput.value.trim());
    };

    modal.querySelectorAll(".ai-chip").forEach(chip => {
        chip.onclick = function () {
            sendAiMessage(this.getAttribute("data-query"));
        };
    });
}
