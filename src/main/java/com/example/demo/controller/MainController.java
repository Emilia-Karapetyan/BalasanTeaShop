package com.example.demo.controller;

import com.example.demo.mail.EmailServiceImpl;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {
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
    private FeedBackRepository feedBackRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Value("${image.upload.url}")
    private String imageUploadUrl;
    @Value("${image.upload.productPicUrl}")
    private String imageProductUploadUrl;
    @Value("${stripe.public.key}")
    private String stripePublicKey;
    private String tempEmail;
    private String tempPhone;
    private int tempPr;
    private int id;
    private String productName;
    private int sum;
    private List<Product> products = new ArrayList<>();


    @GetMapping("/")
    public String mainPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        List<Product> list = new ArrayList<>();
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            map.addAttribute("us", user);
            if (user.getType().equals(UserType.USER)){
                map.addAttribute("typeUs","user");
            }else{
                map.addAttribute("typeUs","admin");
            }
            for (Product product : productRepository.findAll()) {
                if (wishlistRepository.countByUserIdAndProductId(user.getId(), product.getId()) == 1) {
                    list.add(product);
                }
                if (cartRepository.findOneByUserIdAndProductId(user.getId(), product.getId()) != null) {
                    products.add(product);
                }
            }
        }
        map.addAttribute("carts", products);
        map.addAttribute("wishProduct", list);
        map.addAttribute("allProduct", productRepository.findAll());
        return "index";
    }

    @GetMapping("/about")
    public String aboutPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            CurrentUser currentUser = (CurrentUser) userDetails;
            map.addAttribute("us", currentUser.getUser());
            if (currentUser.getUser().getType().equals(UserType.USER)){
                map.addAttribute("typeUs","user");
            }else{
                map.addAttribute("typeUs","admin");
            }
        }
        return "about";
    }

    @GetMapping("/order")
    public String orderPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            CurrentUser currentUser = (CurrentUser) userDetails;
            map.addAttribute("us", currentUser.getUser());
            map.addAttribute("allOrder",orderRepository.findAllByUserId(currentUser.getUser().getId()));
        }
        return "order";
    }

    @GetMapping("/cart")
    public String cartPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        sum=0;
        if (userDetails != null) {
            User currentUser = ((CurrentUser) userDetails).getUser();
            map.addAttribute("us", currentUser);
            List<Product> productList = productRepository.findAll();
            List<Cart> carts = cartRepository.findAllByUserId(currentUser.getId());
            List<Product> userCarts = new ArrayList<>();
            for (Cart cart : carts) {
                for (Product product : productList) {
                    if (cart.getProductId() == product.getId() && cart.getUserId() == currentUser.getId()) {
                        sum += product.getPrice();
                        userCarts.add(product);
                        break;
                    }
                }
            }
            map.addAttribute("userCards", userCarts);
            map.addAttribute("carts", carts);
            map.addAttribute("amount", sum * 100); // in cents
            map.addAttribute("stripePublicKey", stripePublicKey);
            map.addAttribute("currency", ChargeRequest.Currency.USD);
        }
        return "cart";
    }

    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    @GetMapping("/buyProduct")
    public @ResponseBody
    void buyProduct(@AuthenticationPrincipal UserDetails userDetails) {
        User user = ((CurrentUser) userDetails).getUser();
        List<Cart> carts = cartRepository.findAllByUserId(user.getId());
        String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        for (Cart cart : carts) {
            Product p = productRepository.findOneById(cart.getProductId());
            List<ProductImage> productImages = productImageRepository.findAllByProductId(p.getId());
            double d = cart.getQuantity() * p.getPrice();
            Order order = Order.builder()
                    .productId(cart.getProductId())
                    .userId(cart.getUserId())
                    .date(time)
                    .total(d)
                    .quantity(cart.getQuantity())
                    .picUrl(productImages.get(0).getPicUrl())
                    .productTitle(p.getTitle())
                    .build();
            System.out.println(order.getQuantity());
            if (p.getCount()==1 || p.getCount()==cart.getQuantity()){
                productRepository.deleteProductById(p.getId());
            }else{
                int c = p.getCount();
                p.setCount(--c);
                productRepository.updateProduct(p.getCount(),p.getId());
            }
            orderRepository.save(order);
        }
        cartRepository.deleteAll();
    }

    @GetMapping("/getOrdersImg/{str}")
    public void getOrdersImg(@PathVariable(name = "str") String str, HttpServletResponse response) throws IOException {
        InputStream in = new FileInputStream(imageProductUploadUrl + str);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/getQuantity")
    public @ResponseBody
    void getQuantity(@RequestParam("id") int prId, @RequestParam("val") String val, HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        double price = productRepository.findOneById(prId).getPrice();
        double total = price * Integer.parseInt(val);
        User user = ((CurrentUser) userDetails).getUser();
        Cart cart = cartRepository.findOneByUserIdAndProductId(user.getId(), prId);
        cart.setQuantity(Integer.parseInt(val));
        cartRepository.updateCart(Integer.parseInt(val), cart.getId());
        response.setContentType("text/plain");
        response.getWriter().write(String.valueOf(total));
    }


    @GetMapping("/addToCart")
    public @ResponseBody
    void addToCart(HttpServletResponse response, @RequestParam("id") int id,
                   @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        if (userDetails == null) {
            response.getWriter().write("NotFound");
            response.setContentType("text/plain");
        } else {
            User user = ((CurrentUser) userDetails).getUser();
            Cart cart1 = Cart.builder().userId(user.getId()).productId(id).quantity(1).build();
            cartRepository.save(cart1);
            if(wishlistRepository.findOneByUserIdAndProductId(user.getId(),id)!=null){
                WishList wishList=wishlistRepository.findOneByUserIdAndProductId(user.getId(),id);
                wishlistRepository.deleteWishlistById(wishList.getId());
            }
        }

    }


    @GetMapping("/gallery")
    public String galleryPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        List<Product> list = new ArrayList<>();
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            map.addAttribute("us", user);
            if (user.getType().equals(UserType.USER)){
                map.addAttribute("typeUs","user");
            }else{
                map.addAttribute("typeUs","admin");
            }
            for (Product product : productRepository.findAll()) {
                if (wishlistRepository.countByUserIdAndProductId(user.getId(), product.getId()) == 1) {
                    list.add(product);
                }
                if (cartRepository.findOneByUserIdAndProductId(user.getId(), product.getId()) != null) {
                    products.add(product);
                }
            }
        }
        map.addAttribute("carts",products);
        map.addAttribute("wishProduct", list);
        map.addAttribute("allPr", productRepository.findAll());
        return "gallery";
    }


    @GetMapping("/shop")
    public String shopPage(ModelMap map, @AuthenticationPrincipal UserDetails
            userDetails, @ModelAttribute("allProd") String cat) {
        List<Product> list = new ArrayList<>();
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            if (user.getType().equals(UserType.USER)){
                map.addAttribute("typeUs","user");
            }else{
                map.addAttribute("typeUs","admin");
            }
            map.addAttribute("us", user);
            for (Product product : productRepository.findAll()) {
                if (wishlistRepository.countByUserIdAndProductId(user.getId(), product.getId()) == 1) {
                    list.add(product);
                }
                if (cartRepository.findOneByUserIdAndProductId(user.getId(), product.getId()) != null) {
                    products.add(product);
                }
            }
        }
        if (cat.equals("")) {
            cat = productName;
        }
        productName = cat;
        map.addAttribute("carts",products);
        map.addAttribute("cat",cat);
        map.addAttribute("wishProduct", list);
        map.addAttribute("prByCat", productRepository.findAllByCategoryName(cat));
        return "shop";
    }

    @GetMapping("/deleteCart")
    public @ResponseBody
    void deleteInCart(@RequestParam("id") int id,
                      @AuthenticationPrincipal UserDetails userDetails) {
        User user = ((CurrentUser) userDetails).getUser();
        cartRepository.deleteCartById(cartRepository.findOneByUserIdAndProductId(user.getId(), id).getId());
    }

    @GetMapping("/shopDetail")
    public String shopDetailPage(ModelMap map, @AuthenticationPrincipal UserDetails
            userDetails, @ModelAttribute("viewProduct") Product product) {
        boolean containsCart=false;
        int count = 0;
        if (product.getId() == 0) {
            product = productRepository.findOneById(id);
        }
        if (product==null){
            return "redirect:/";
        }
        id = product.getId();
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            if (user.getType().equals(UserType.USER)){
                map.addAttribute("typeUs","user");
            }else{
                map.addAttribute("typeUs","admin");
            }
            map.addAttribute("us", user);
            count = wishlistRepository.countByUserIdAndProductId(user.getId(), product.getId());
            if (cartRepository.findOneByUserIdAndProductId(user.getId(), product.getId()) != null) {
                containsCart=true;
            }
        }
        List<ProductImage> list = product.getProductImageList();
        ProductImage productImg = product.getProductImageList().get(0);
        list.remove(0);
        List<OutputMessage> outputMessages = feedBackRepository.findAllByProductId(product.getId());
        map.addAttribute("feedback",outputMessages);
        map.addAttribute("containsCart",containsCart);
        map.addAttribute("viewPr", product);
        map.addAttribute("prImg", list);
        map.addAttribute("activeImg", productImg);
        map.addAttribute("wishPr", count);
        return "shop-detail";
    }
    @GetMapping("/getImgById/{id}")
    public void getImgById(@PathVariable(name = "id") int id, HttpServletResponse response) throws IOException {
        User user=userRepository.findOneById(id);
        InputStream in = new FileInputStream(imageUploadUrl + user.getPicUrl());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }


    @GetMapping("/addToCartWishList")
    public @ResponseBody
    void addToCartWishList(@RequestParam("id") int id,@AuthenticationPrincipal UserDetails userDetails){
        User user = ((CurrentUser) userDetails).getUser();
        WishList wishList = wishlistRepository.findOneByUserIdAndProductId(user.getId(),id);
        Cart cart1 = Cart.builder().userId(user.getId()).productId(id).quantity(1).build();
        cartRepository.save(cart1);
        wishlistRepository.deleteWishlistById(wishList.getId());
    }

    @PostMapping("/charge")
    public String charge(ChargeRequest chargeRequest, Model model,@AuthenticationPrincipal UserDetails userDetails
    )
            throws StripeException {
        User user = ((CurrentUser)userDetails).getUser();
        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(ChargeRequest.Currency.USD);
        Charge charge = paymentsService.charge(chargeRequest);
        model.addAttribute("us",user);
        model.addAttribute("id", charge.getId());
        model.addAttribute("status", charge.getStatus());
        model.addAttribute("chargeId", charge.getId());
        model.addAttribute("balance_transaction", charge.getBalanceTransaction());
        return "result";
    }

    @GetMapping("/lock")
    public String lockPage(){
        return "lock";
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        System.err.println("errors");
        System.out.println(ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "result";
    }

    @GetMapping("/viewCart")
    public @ResponseBody
    void viewCart(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException {
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            response.setContentType("text/html;charset=UTF-8");
            List<Product> list = productRepository.findAll();
            int j = 0;
            for (int i = 0; i < list.size(); i++) {
                if (cartRepository.findOneByUserIdAndProductId(user.getId(), list.get(i).getId()) != null && j<=3) {
                    j++;
                    response.getWriter().write(list.get(i).getId() + " " + list.get(i).getTitle() + " " + list.get(i).getPrice() + "&");
                }

            }

        }
    }





}
