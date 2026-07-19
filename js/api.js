/**
 * SaveKart Unified API Client
 * Manages REST API requests, JWT token header injection, and fallback handling.
 */
const API_BASE_URL = (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' || window.location.protocol === 'file:'))
    ? 'http://localhost:8080/api/v1'
    : `${window.location.origin}/api/v1`;

class SaveKartAPI {
    static getAuthToken() {
        return localStorage.getItem('savekart_jwt_token');
    }

    static setAuthToken(token) {
        if (token) {
            localStorage.setItem('savekart_jwt_token', token);
        } else {
            localStorage.removeItem('savekart_jwt_token');
        }
    }

    static getUser() {
        const user = localStorage.getItem('savekart_user');
        return user ? JSON.parse(user) : null;
    }

    static setUser(user) {
        if (user) {
            localStorage.setItem('savekart_user', JSON.stringify(user));
        } else {
            localStorage.removeItem('savekart_user');
        }
    }

    static async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        const token = this.getAuthToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.error || data.message || 'API request failed');
            }
            return data;
        } catch (error) {
            console.warn(`[SaveKart API Warning] ${endpoint}: ${error.message}`);
            throw error;
        }
    }

    // Auth APIs
    static login(email, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
    }

    static signup(fullName, email, password) {
        return this.request('/auth/signup', {
            method: 'POST',
            body: JSON.stringify({ fullName, email, password })
        });
    }

    static getMe() {
        return this.request('/auth/me', { method: 'GET' });
    }

    static forgotPassword(email) {
        return this.request('/auth/forgot-password', {
            method: 'POST',
            body: JSON.stringify({ email })
        });
    }

    static resetPassword(token, newPassword) {
        return this.request('/auth/reset-password', {
            method: 'POST',
            body: JSON.stringify({ token, newPassword })
        });
    }

    // Product APIs
    static getProducts(search = '', category = '') {
        let query = [];
        if (search) query.push(`search=${encodeURIComponent(search)}`);
        if (category) query.push(`category=${encodeURIComponent(category)}`);
        const queryString = query.length ? `?${query.join('&')}` : '';
        return this.request(`/products${queryString}`, { method: 'GET' });
    }

    static getProductDetails(id) {
        return this.request(`/products/${id}`, { method: 'GET' });
    }

    static getPriceComparison(id) {
        return this.request(`/products/${id}/price-comparison`, { method: 'GET' });
    }

    static getPriceHistory(id) {
        return this.request(`/products/${id}/price-history`, { method: 'GET' });
    }

    static getReviews(id) {
        return this.request(`/products/${id}/reviews`, { method: 'GET' });
    }

    // Search & AI APIs
    static autocomplete(query) {
        return this.request(`/search/autocomplete?q=${encodeURIComponent(query)}`, { method: 'GET' });
    }

    static askAiAssistant(query) {
        return this.request('/ai/assistant', {
            method: 'POST',
            body: JSON.stringify({ query })
        });
    }

    // User Feature APIs
    static getWishlist() {
        return this.request('/user/wishlist', { method: 'GET' });
    }

    static addToWishlist(productId) {
        return this.request(`/user/wishlist/add/${productId}`, { method: 'POST' });
    }

    static getFavorites() {
        return this.request('/user/favorites', { method: 'GET' });
    }

    static addToFavorites(productId) {
        return this.request(`/user/favorites/add/${productId}`, { method: 'POST' });
    }

    static getCart() {
        return this.request('/user/cart', { method: 'GET' });
    }

    static addToCart(productId, platform, quantity = 1) {
        return this.request('/user/cart/add', {
            method: 'POST',
            body: JSON.stringify({ productId, platform, quantity })
        });
    }

    static createPriceAlert(productId, targetPrice) {
        return this.request('/user/alerts/create', {
            method: 'POST',
            body: JSON.stringify({ productId, targetPrice })
        });
    }

    // Admin & Analytics APIs
    static getAdminDashboard() {
        return this.request('/admin/dashboard', { method: 'GET' });
    }

    static getTrendingAnalytics() {
        return this.request('/admin/analytics/trending', { method: 'GET' });
    }
}

window.SaveKartAPI = SaveKartAPI;
