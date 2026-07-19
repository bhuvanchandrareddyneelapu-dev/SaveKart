/**
 * SaveKart Authentication & User Manager
 */
document.addEventListener("DOMContentLoaded", function () {
    initAuthUI();
    bindAuthForms();
});

function initAuthUI() {
    const user = SaveKartAPI.getUser();
    const nav = document.querySelector("header nav");

    if (!nav) return;

    const loginBtn = nav.querySelector(".Login-btn");
    const signUpBtn = nav.querySelector(".btn");

    if (user) {
        // Replace Login/Signup with User Profile Dropdown or Links
        if (loginBtn) loginBtn.style.display = "none";
        if (signUpBtn) signUpBtn.style.display = "none";

        // Remove old user elements if existing
        const existingUserNav = nav.querySelector(".user-nav-item");
        if (existingUserNav) existingUserNav.remove();

        const userContainer = document.createElement("div");
        userContainer.className = "user-nav-item";
        userContainer.style.display = "flex";
        userContainer.style.alignItems = "center";
        userContainer.style.gap = "12px";

        userContainer.innerHTML = `
            <span style="color: #38bdf8; font-weight: 600;"><i class="fa fa-user-circle"></i> ${user.fullName || 'User'}</span>
            <a href="profile.html" style="font-size:14px; color:#c4b5fd;">Profile</a>
            <a href="wishlist.html" style="font-size:14px; color:#c4b5fd;">Wishlist</a>
            <a href="cart.html" style="font-size:14px; color:#c4b5fd;">Cart</a>
            ${user.role === 'ROLE_ADMIN' ? '<a href="admin.html" style="font-size:14px; color:#ec4899;">Admin</a>' : ''}
            <button onclick="handleLogout()" style="background:none; border:1px solid #ef4444; color:#ef4444; padding:6px 12px; border-radius:8px; cursor:pointer;">Logout</button>
        `;

        nav.appendChild(userContainer);
    }
}

function bindAuthForms() {
    // LoginForm Handler
    const loginForm = document.querySelector(".login-box form");
    if (loginForm) {
        loginForm.addEventListener("submit", async function (e) {
            e.preventDefault();
            const inputs = loginForm.querySelectorAll("input");
            const email = inputs[0].value.trim();
            const password = inputs[1].value.trim();

            if (!email || !password) {
                showToast("Please enter email and password", "error");
                return;
            }

            try {
                const response = await SaveKartAPI.login(email, password);
                SaveKartAPI.setAuthToken(response.token);
                SaveKartAPI.setUser({
                    id: response.id,
                    fullName: response.fullName,
                    email: response.email,
                    role: response.role
                });

                showToast("Login successful! Redirecting...", "success");
                setTimeout(() => {
                    window.location.href = "index.html";
                }, 1000);
            } catch (err) {
                showToast(err.message || "Invalid credentials", "error");
            }
        });
    }

    // SignupForm Handler
    const signupForm = document.querySelector(".signup-box form");
    if (signupForm) {
        signupForm.addEventListener("submit", async function (e) {
            e.preventDefault();
            const inputs = signupForm.querySelectorAll("input");
            const fullName = inputs[0].value.trim();
            const email = inputs[1].value.trim();
            const password = inputs[2].value.trim();
            const confirmPassword = inputs[3].value.trim();

            if (!fullName || !email || !password) {
                showToast("Please fill in all fields", "error");
                return;
            }

            if (password !== confirmPassword) {
                showToast("Passwords do not match", "error");
                return;
            }

            try {
                const response = await SaveKartAPI.signup(fullName, email, password);
                SaveKartAPI.setAuthToken(response.token);
                SaveKartAPI.setUser({
                    id: response.id,
                    fullName: response.fullName,
                    email: response.email,
                    role: response.role
                });

                showToast("Account created successfully! Redirecting...", "success");
                setTimeout(() => {
                    window.location.href = "index.html";
                }, 1000);
            } catch (err) {
                showToast(err.message || "Signup failed", "error");
            }
        });
    }
}

function handleLogout() {
    SaveKartAPI.setAuthToken(null);
    SaveKartAPI.setUser(null);
    showToast("Logged out successfully", "info");
    setTimeout(() => {
        window.location.href = "index.html";
    }, 800);
}

function showToast(message, type = "info") {
    let toast = document.getElementById("savekartToast");
    if (!toast) {
        toast = document.createElement("div");
        toast.id = "savekartToast";
        toast.style.position = "fixed";
        toast.style.bottom = "24px";
        toast.style.right = "24px";
        toast.style.padding = "14px 24px";
        toast.style.borderRadius = "12px";
        toast.style.color = "#fff";
        toast.style.zIndex = "9999";
        toast.style.boxShadow = "0 10px 25px rgba(0,0,0,0.3)";
        toast.style.fontFamily = "Poppins, sans-serif";
        toast.style.transition = "all 0.3s ease";
        document.body.appendChild(toast);
    }

    if (type === "success") toast.style.background = "#10b981";
    else if (type === "error") toast.style.background = "#ef4444";
    else toast.style.background = "#5865ff";

    toast.innerText = message;
    toast.style.opacity = "1";
    toast.style.transform = "translateY(0)";

    setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transform = "translateY(20px)";
    }, 3000);
}

window.handleLogout = handleLogout;
window.showToast = showToast;
