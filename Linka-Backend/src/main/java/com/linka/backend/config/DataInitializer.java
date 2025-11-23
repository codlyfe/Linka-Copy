package com.linka.backend.config;

import com.linka.backend.entity.Category;
import com.linka.backend.entity.User;
import com.linka.backend.repository.CategoryRepository;
import com.linka.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create test users if they don't exist
        if (userRepository.count() == 0) {
            
            // Create admin user
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@linka.com");
            admin.setPhoneNumber("+256700000001");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setLocation("Kampala");
            admin.setCity("Kampala");
            admin.setDistrict("Kampala");
            admin.setCountry("Uganda");
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setUserType(User.UserType.ADMIN);
            admin.setEmailVerified(true);
            admin.setPhoneVerified(true);
            userRepository.save(admin);

            // Create buyer user
            User buyer = new User();
            buyer.setFirstName("John");
            buyer.setLastName("Doe");
            buyer.setEmail("john@example.com");
            buyer.setPhoneNumber("+256700000002");
            buyer.setPassword(passwordEncoder.encode("password123"));
            buyer.setLocation("Kampala");
            buyer.setCity("Kampala");
            buyer.setDistrict("Kampala");
            buyer.setCountry("Uganda");
            buyer.setStatus(User.UserStatus.ACTIVE);
            buyer.setUserType(User.UserType.BUYER);
            buyer.setEmailVerified(true);
            buyer.setPhoneVerified(true);
            userRepository.save(buyer);

            // Create seller user
            User seller = new User();
            seller.setFirstName("Jane");
            seller.setLastName("Smith");
            seller.setEmail("jane@example.com");
            seller.setPhoneNumber("+256700000003");
            seller.setPassword(passwordEncoder.encode("password123"));
            seller.setLocation("Kampala");
            seller.setCity("Kampala");
            seller.setDistrict("Kampala");
            seller.setCountry("Uganda");
            seller.setStatus(User.UserStatus.ACTIVE);
            seller.setUserType(User.UserType.SELLER);
            seller.setEmailVerified(true);
            seller.setPhoneVerified(true);
            userRepository.save(seller);

            System.out.println("Test users created successfully:");
            System.out.println("Admin: admin@linka.com / admin123");
            System.out.println("Buyer: john@example.com / password123");
            System.out.println("Seller: jane@example.com / password123");
        }
        
        // Create test categories if they don't exist
        if (categoryRepository.count() == 0) {
            
            // Electronics category
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Electronic devices and gadgets");
            electronics.setSlug("electronics");
            electronics.setActive(true);
            electronics.setFeatured(false);
            electronics.setItemCount(0);
            electronics.setSortOrder(1);
            categoryRepository.save(electronics);
            
            // Fashion category
            Category fashion = new Category();
            fashion.setName("Fashion");
            fashion.setDescription("Clothing, shoes, and accessories");
            fashion.setSlug("fashion");
            fashion.setActive(true);
            fashion.setFeatured(false);
            fashion.setItemCount(0);
            fashion.setSortOrder(2);
            categoryRepository.save(fashion);
            
            // Home & Garden category
            Category homeGarden = new Category();
            homeGarden.setName("Home & Garden");
            homeGarden.setDescription("Home improvement, furniture, and garden supplies");
            homeGarden.setSlug("home-garden");
            homeGarden.setActive(true);
            homeGarden.setFeatured(false);
            homeGarden.setItemCount(0);
            homeGarden.setSortOrder(3);
            categoryRepository.save(homeGarden);
            
            System.out.println("Test categories created successfully:");
            System.out.println("Electronics (ID: 1)");
            System.out.println("Fashion (ID: 2)");
            System.out.println("Home & Garden (ID: 3)");
        }
    }
}