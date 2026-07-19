package com.savekart.util;

import com.savekart.model.*;
import com.savekart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class SeedDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PlatformPriceRepository platformPriceRepository;
    @Autowired
    private PriceHistoryRepository priceHistoryRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            return; // Data already seeded
        }

        // 1. Seed Users
        User admin = User.builder()
                .fullName("SaveKart Admin")
                .email("admin@savekart.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .verified(true)
                .build();
        userRepository.save(admin);

        User demoUser = User.builder()
                .fullName("Bhuvan Chandra")
                .email("user@savekart.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.ROLE_USER)
                .verified(true)
                .build();
        userRepository.save(demoUser);

        // 2. Seed Categories
        Map<String, Category> categories = new HashMap<>();
        String[][] catData = {
                {"Grocery", "grocery", "images/grocery.png1.png"},
                {"Milk Products", "milk", "images/milk-products.png1.png"},
                {"Electronics", "electronics", "images/electronics.png1.png"},
                {"Mobiles", "mobiles", "images/mobiles.png1.png"},
                {"Appliances", "appliances", "images/appliances.png1.png"},
                {"Biryani", "biryani", "images/Briyani.png1.png"},
                {"Fashion", "fashion", "images/fashion.png1.png"},
                {"Icecream", "Icecream", "images/Icecreme.png1.png"}
        };

        for (String[] c : catData) {
            Category category = Category.builder()
                    .name(c[0])
                    .slug(c[1])
                    .imageUrl(c[2])
                    .build();
            categoryRepository.save(category);
            categories.put(c[1], category);
        }

        // 3. Seed Brands
        Map<String, Brand> brands = new HashMap<>();
        String[] brandNames = {"Apple", "Samsung", "Freedom", "Visakha", "Amul", "Heritage", "Jersey", "OnePlus", "Google", "Sony", "LG", "Xiaomi", "Generic"};
        for (String bName : brandNames) {
            Brand brand = Brand.builder().name(bName).build();
            brandRepository.save(brand);
            brands.put(bName, brand);
        }

        // 4. Seed Products & Multi-Platform Prices
        List<Object[]> rawProducts = Arrays.asList(
                // Grocery
                new Object[]{"Freedom oil 1ltr", "grocery", "images/freedom oil 1tr.png", 145.0, "1 Ltr", "Freedom"},
                new Object[]{"Freedom oil Tin", "grocery", "images/freedom oil tin.png", 2150.0, "15 Ltr", "Freedom"},

                // Electronics
                new Object[]{"Laptop", "electronics", "images/Laptop.png", 54990.0, "1.8 kg", "Apple"},
                new Object[]{"Smart TV", "electronics", "images/Television.png", 32990.0, "55 inch", "Samsung"},
                new Object[]{"Earbuds", "electronics", "images/EarPods.png", 2990.0, "50g", "Apple"},
                new Object[]{"Smart Watch", "electronics", "images/Smart Watch.png", 4990.0, "40g", "Samsung"},
                new Object[]{"Hard Disk", "electronics", "images/Hard Disc..png", 4290.0, "2TB", "Generic"},
                new Object[]{"Head Phones", "electronics", "images/Head Phones.png", 3490.0, "200g", "Sony"},
                new Object[]{"Bluetooth Speaker", "electronics", "images/Bluetooth.png", 2490.0, "500g", "Generic"},
                new Object[]{"Gaming Console", "electronics", "images/Gaming Console.png", 49990.0, "4.5 kg", "Sony"},
                new Object[]{"Digital Camera", "electronics", "images/Camera.png", 64990.0, "600g", "Sony"},
                new Object[]{"Printer", "electronics", "images/Printer.png", 12990.0, "5 kg", "Generic"},

                // Mobiles
                new Object[]{"iPhone 15 Pro Max", "mobiles", "images/iPhone 15 Pro Max.png", 149900.0, "221g", "Apple"},
                new Object[]{"Samsung Galaxy S24 Ultra", "mobiles", "images/Samsung Galaxy S24 Ultra.png", 129999.0, "232g", "Samsung"},
                new Object[]{"OnePlus 12 Pro", "mobiles", "images/One Plus 12.png", 64999.0, "205g", "OnePlus"},
                new Object[]{"Google Pixel 8 Pro", "mobiles", "images/Google Pixel 8 Pro.png", 93999.0, "213g", "Google"},
                new Object[]{"Xiaomi 14 Pro", "mobiles", "images/Xiaomi 14 Pro.png", 69999.0, "220g", "Xiaomi"},

                // Appliances
                new Object[]{"Air Conditioner", "appliances", "images/AC.png", 36990.0, "1.5 Ton 5 Star", "LG"},
                new Object[]{"Washing Machine", "appliances", "images/washing machine.png", 28990.0, "8 kg Fully Automatic", "Samsung"},
                new Object[]{"Refrigerator", "appliances", "images/Fridge.png", 45990.0, "340L Double Door", "LG"},
                new Object[]{"Oven", "appliances", "images/oven.png", 11490.0, "28L Convection", "Samsung"},

                // Biryani
                new Object[]{"Chicken Biryani", "biryani", "images/Chicken Briyani.png", 280.0, "Single Servo", "Generic"},
                new Object[]{"Mutton Biryani", "biryani", "images/Mutton Briyani.png", 380.0, "Single Servo", "Generic"},
                new Object[]{"Chicken Sharwama", "biryani", "images/Chicken Sharwama.png", 160.0, "1 Pc", "Generic"},

                // Icecream
                new Object[]{"VANILLA", "Icecream", "images/VANILLA.png", 150.0, "500 ml", "Amul"},
                new Object[]{"CHOCOLATE", "Icecream", "images/CHOCOLATE.png", 180.0, "500 ml", "Amul"},
                new Object[]{"BELGIAN CHOCOLATE", "Icecream", "images/BELGIAN CHOCOLATE.png", 260.0, "500 ml", "Amul"},

                // Milk Products
                new Object[]{"Visakha dairy cow milk", "milk", "images/visakha cow milk.png", 32.0, "500 ml", "Visakha"},
                new Object[]{"Visakha dairy ghee", "milk", "images/visakha ghee.png", 580.0, "1 Ltr", "Visakha"},
                new Object[]{"Amul Butter", "milk", "images/amul butter.png", 275.0, "500g", "Amul"},
                new Object[]{"Heritage Paneer", "milk", "images/heritage paneer.png", 110.0, "200g", "Heritage"}
        );

        String[] allPlatforms = {
                "Amazon", "Flipkart", "Zepto", "Blinkit", "Swiggy Instamart",
                "BigBasket", "Dmart Ready", "JioMart", "Reliance Fresh", "Croma",
                "Vijay Sales", "Tata Neu", "Myntra", "Ajio", "Meesho"
        };

        Random random = new Random(42);

        for (Object[] item : rawProducts) {
            String name = (String) item[0];
            String catSlug = (String) item[1];
            String image = (String) item[2];
            Double basePrice = (Double) item[3];
            String weight = (String) item[4];
            String brandName = (String) item[5];

            Category cat = categories.get(catSlug);
            Brand brand = brands.getOrDefault(brandName, brands.get("Generic"));

            Product product = Product.builder()
                    .name(name)
                    .sku("SKU-" + name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase())
                    .description("Premium quality " + name + " available across India's top online shopping apps with live price comparison and express delivery.")
                    .mrp(Math.round(basePrice * 1.25 * 100.0) / 100.0)
                    .weight(weight)
                    .mainImage(image)
                    .variants("Standard, Premium Pack")
                    .category(cat)
                    .brand(brand)
                    .rating(4.2 + (random.nextDouble() * 0.7))
                    .reviewCount(50 + random.nextInt(450))
                    .build();

            productRepository.save(product);

            // Select 4 to 8 platforms for this product
            int numPlatforms = 4 + random.nextInt(6);
            List<String> shuffledPlatforms = new ArrayList<>(Arrays.asList(allPlatforms));
            Collections.shuffle(shuffledPlatforms, random);

            for (int i = 0; i < numPlatforms; i++) {
                String platName = shuffledPlatforms.get(i);
                double priceVariation = 0.88 + (random.nextDouble() * 0.24);
                double platPrice = Math.round(basePrice * priceVariation);
                double mrp = product.getMrp();
                int discount = (int) Math.round(((mrp - platPrice) / mrp) * 100);
                double deliveryFee = (random.nextBoolean() && platPrice > 299) ? 0.0 : 29.0;
                String delTime = platName.matches("Zepto|Blinkit|Swiggy Instamart") ? "10 mins" : (platName.matches("Amazon|Flipkart|Croma") ? "Same Day" : "1 - 2 Days");

                PlatformPrice platformPrice = PlatformPrice.builder()
                        .product(product)
                        .platformName(platName)
                        .currentPrice(platPrice)
                        .mrp(mrp)
                        .discountPercentage(Math.max(discount, 5))
                        .deliveryCharge(deliveryFee)
                        .deliveryTime(delTime)
                        .rating(4.0 + (random.nextDouble() * 0.9))
                        .offers(platName + " Special: Extra 10% off with Bank Cards")
                        .coupons(discount > 15 ? "SAVEKART10" : null)
                        .cashback(platPrice > 1000 ? 50.0 : 10.0)
                        .stockStatus("IN_STOCK")
                        .productUrl("https://www.google.com/search?q=" + name + "+" + platName)
                        .build();

                platformPriceRepository.save(platformPrice);
            }

            // Seed price history records over past 6 months
            for (int month = 6; month >= 0; month--) {
                PriceHistory history = PriceHistory.builder()
                        .product(product)
                        .platformName(shuffledPlatforms.get(0))
                        .price((double) Math.round(basePrice * (0.90 + (random.nextDouble() * 0.20))))
                        .recordedAt(LocalDateTime.now().minusMonths(month))
                        .build();
                priceHistoryRepository.save(history);
            }

            // Seed sample reviews
            Review review1 = Review.builder()
                    .product(product)
                    .authorName("Rahul Sharma")
                    .rating(5.0)
                    .comment("Super fast price comparison! Found the cheapest price on " + shuffledPlatforms.get(0) + " and saved ₹" + Math.round(basePrice * 0.15))
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build();

            Review review2 = Review.builder()
                    .product(product)
                    .authorName("Priya Verma")
                    .rating(4.5)
                    .comment("Excellent quality and fast delivery time! Highly recommended.")
                    .createdAt(LocalDateTime.now().minusDays(10))
                    .build();

            reviewRepository.save(review1);
            reviewRepository.save(review2);
        }
    }
}
