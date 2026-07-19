package com.savekart.controller;

import com.savekart.model.*;
import com.savekart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserFeatureController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private PriceAlertRepository priceAlertRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Default demo user fallback if unauthenticated client calls
            return userRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("User not found"));
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseGet(() -> userRepository.findAll().stream().findFirst().get());
    }

    // --- Wishlist ---
    @GetMapping("/wishlist")
    public ResponseEntity<List<Wishlist>> getWishlist(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return ResponseEntity.ok(wishlistRepository.findByUserId(user.getId()));
    }

    @PostMapping("/wishlist/add/{productId}")
    public ResponseEntity<?> addToWishlist(@PathVariable Long productId, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existing.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "Product already in wishlist"));
        }

        Wishlist wishlist = Wishlist.builder().user(user).product(product).build();
        wishlistRepository.save(wishlist);
        return ResponseEntity.ok(Map.of("message", "Added to wishlist successfully"));
    }

    @DeleteMapping("/wishlist/remove/{productId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist successfully"));
    }

    // --- Favorites ---
    @GetMapping("/favorites")
    public ResponseEntity<List<Favorite>> getFavorites(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return ResponseEntity.ok(favoriteRepository.findByUserId(user.getId()));
    }

    @PostMapping("/favorites/add/{productId}")
    public ResponseEntity<?> addToFavorites(@PathVariable Long productId, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existing.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "Product already in favorites"));
        }

        Favorite favorite = Favorite.builder().user(user).product(product).build();
        favoriteRepository.save(favorite);
        return ResponseEntity.ok(Map.of("message", "Added to favorites successfully"));
    }

    @DeleteMapping("/favorites/remove/{productId}")
    @Transactional
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long productId, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        favoriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return ResponseEntity.ok(Map.of("message", "Removed from favorites successfully"));
    }

    // --- Price Drop Alerts ---
    @GetMapping("/alerts")
    public ResponseEntity<List<PriceAlert>> getAlerts(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return ResponseEntity.ok(priceAlertRepository.findByUserId(user.getId()));
    }

    @PostMapping("/alerts/create")
    public ResponseEntity<?> createAlert(@RequestBody Map<String, Object> body, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        Long productId = Long.parseLong(body.get("productId").toString());
        Double targetPrice = Double.parseDouble(body.get("targetPrice").toString());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        PriceAlert alert = PriceAlert.builder()
                .user(user)
                .product(product)
                .targetPrice(targetPrice)
                .active(true)
                .build();

        priceAlertRepository.save(alert);
        return ResponseEntity.ok(Map.of("message", "Price alert created! We'll notify you when price drops below ₹" + targetPrice));
    }

    // --- Shopping Cart ---
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCart(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return ResponseEntity.ok(cartItemRepository.findByUserId(user.getId()));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        Long productId = Long.parseLong(body.get("productId").toString());
        String platform = body.getOrDefault("platform", "Amazon").toString();
        int quantity = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : 1;

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductIdAndSelectedPlatform(user.getId(), productId, platform);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .selectedPlatform(platform)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(cartItem);
        }

        return ResponseEntity.ok(Map.of("message", "Added to cart successfully"));
    }
}
