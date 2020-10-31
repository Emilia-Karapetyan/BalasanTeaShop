package com.example.demo.controller;

import com.example.demo.mail.EmailServiceImpl;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private StripeService paymentsService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Value("${image.upload.url}")
    private String imageUploadUrl;
    @Value("${image.upload.productPicUrl}")
    private String imageProductUploadUrl;
    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping("/admin")
    public String adminPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            CurrentUser currentUser = (CurrentUser) userDetails;
            map.addAttribute("us", currentUser.getUser());
        }
        return "admin";
    }

    @GetMapping("/allUsers")
    public String allUsers(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        CurrentUser currentUser = (CurrentUser) userDetails;
        map.addAttribute("us", currentUser.getUser());
        map.addAttribute("allUser", userRepository.findAll());
        return "allUsers";
    }

    @GetMapping("/allProducts")
    public String allProducts(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        CurrentUser currentUser = (CurrentUser) userDetails;
        map.addAttribute("us", currentUser.getUser());
        map.addAttribute("allProduct", productRepository.findAll());
        return "allProducts";
    }

    @GetMapping("/deleteUser")
    public @ResponseBody
    void deleteUser(@RequestParam("userId") int id) {
        System.out.println(id);
        userRepository.deleteUserById(id);
    }

    @GetMapping("/blockUser")
    public
    @ResponseBody
    void blockUser(@RequestParam("id") int id, @RequestParam("sec") int sec) {
        userRepository.updateUserByActive(sec, id);
    }

    @GetMapping("/stopTimer")
    public
    @ResponseBody
    void stopTimer(@RequestParam("id") int id, @RequestParam("sec") int sec, HttpServletResponse response) throws IOException {
        userRepository.updateUserByActive(sec, id);
        response.setContentType("text/plain");
        if (userRepository.findAllByActive().isEmpty()) {
            response.getWriter().write("end");
        }
    }

    @GetMapping("/blockUserSec")
    public
    @ResponseBody
    void blockUserSec(HttpServletResponse response) throws IOException {
        List<User> list = userRepository.findAllByActive();
        response.setContentType("text/plain");
        // Map<Integer, Integer> m = new LinkedHashMap<>();
        for (User user : list) {
            int c = user.getActive();
            c--;
            userRepository.updateUserByActive(c, user.getId());
            //m.put(user.getId(), c);
            response.getWriter().write(user.getId()+"="+user.getActive()+",");
        }
    }

    @GetMapping("/unBlockUser")
    public
    @ResponseBody
    void unBlockUser(@RequestParam("id") int id) {
        userRepository.updateUserByActive(0, id);
    }


    @GetMapping("/acceptProduct")
    public
    @ResponseBody
    void acceptProduct(@RequestParam("id") int id){
        productRepository.updateActiveProduct(1,id);
    }
    @GetMapping("/ignoreProduct")
    public
    @ResponseBody
    void ignoreProduct(@RequestParam("id") int id){
        productRepository.updateActiveProduct(-1,id);
    }

}
