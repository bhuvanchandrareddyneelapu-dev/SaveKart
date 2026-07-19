/**
 * SaveKart Progressive Web App Service Worker
 */
const CACHE_NAME = 'savekart-cache-v1';
const ASSETS_TO_CACHE = [
    '/',
    '/index.html',
    '/products.html',
    '/product-detail.html',
    '/login.html',
    '/signup.html',
    '/wishlist.html',
    '/cart.html',
    '/admin.html',
    '/style.css',
    '/js/api.js',
    '/js/auth.js',
    '/js/smart-search.js',
    '/js/ai-assistant.js',
    '/js/product-detail.js'
];

self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            return cache.addAll(ASSETS_TO_CACHE);
        })
    );
});

self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request).then((cachedResponse) => {
            return cachedResponse || fetch(event.request);
        })
    );
});
