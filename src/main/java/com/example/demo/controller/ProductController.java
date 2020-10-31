package com.example.demo.controller;

import com.example.demo.mail.EmailServiceImpl;
import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.StripeService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@MultipartConfig(maxFileSize = 1024*1024*1024, maxRequestSize = 1024*1024*1024)
public class ProductController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private CartRepository cartRepository;
    @Value("${image.upload.url}")
    private String imageUploadUrl;
    @Value("${image.upload.productPicUrl}")
    private String imageProductUploadUrl;
    @Value("${stripe.public.key}")
    private String tempEmail;
    private String tempPhone;
    private int tempPr;
    private int id;

    @GetMapping("/addProduct")
    public String addProductPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        CurrentUser currentUser = (CurrentUser) userDetails;
        map.addAttribute("prUser", currentUser.getUser());
        map.addAttribute("newPr", new Product());
        map.addAttribute("cat", categoryRepository.findAll());
        return "addProduct";
    }

    @GetMapping("/getProductAllImg/{picUrl}")
    public void getAllImg(@PathVariable(name = "picUrl") String picUrl, HttpServletResponse response) throws
            IOException {
        //List<ProductImg> imgs = productImgRepository.findAllByProductId(id);
        InputStream in = new FileInputStream(imageProductUploadUrl + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/getAllProductByCatId/{str}")
    public String getAllProductByCatId(@PathVariable("str") String str, RedirectAttributes attributes) {
        attributes.addFlashAttribute("allProd", str);
        return "redirect:/shop";
    }

    @GetMapping("/getProductByCat")
    public @ResponseBody
    void getProductByCategory(@RequestParam("catId") int id, HttpServletResponse
            response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        System.out.println(id);
        User user = ((CurrentUser) userDetails).getUser();
        List<Product> productList = productRepository.findAllByCategoryIdAndUser(id, user);
        response.setContentType("text/html;charset=UTF-8");
        for (Product product : productList) {
            if(product.getActive()==0) {
                response.getWriter().write(product.getTitle() + "&copy;" + product.getPrice() + "&copy;" + product.getId() + "&copy;" + "orange" + "\n");
            }else if(product.getActive()==-1){
                response.getWriter().write(product.getTitle()  + "&copy;" + product.getPrice() + "&copy;" + product.getId() + "&copy;" + "red" + "\n");
            }else if(product.getActive()==1){
                response.getWriter().write(product.getTitle()  + "&copy;" + product.getPrice() + "&copy;" + product.getId() + "&copy;" + "green" + "\n");
            }
        }
    }

    @GetMapping("/getProductImg/{id}")
    public void getProductImage(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        List<ProductImage> productImages = productImageRepository.findAllByProductId(id);
        System.out.println(productImages.get(0).getPicUrl());
        InputStream in = new FileInputStream(imageProductUploadUrl + productImages.get(0).getPicUrl());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/wishList")
    public String wishListPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        List<Product> list = new ArrayList<>();
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            map.addAttribute("us", user);
            for (Product product : productRepository.findAll()) {
                if (wishlistRepository.countByUserIdAndProductId(user.getId(), product.getId()) == 1) {
                    list.add(product);
                }
            }
        }
        map.addAttribute("wishProduct", list);
        return "wishlist";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") int id, RedirectAttributes attributes) {
        Product product = productRepository.findOneById(id);
        attributes.addFlashAttribute("viewProduct", product);
        return "redirect:/shopDetail";
    }


    @GetMapping("/editProduct")
    public String editProductPage(@ModelAttribute("edPr") Product product, ModelMap
            map, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(product);
        map.addAttribute("edPr", product);
        map.addAttribute("us", ((CurrentUser) userDetails).getUser());
        map.addAttribute("cats", categoryRepository.findAll());
        return "editProduct";
    }

    @GetMapping("/editPr/{id}")
    public String editPr(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findOneById(id);
        tempPr = id;
        redirectAttributes.addFlashAttribute("edPr", product);
        return "redirect:/editProduct";
    }

    @GetMapping("/searchProduct")
    public @ResponseBody
    void searchProduct(@RequestParam("str") String str, HttpServletResponse resp) throws
            IOException {
        List<Product> list = productRepository.findAll();
        boolean f = false;
        for (Product product : list) {
            if (product.getTitle().toLowerCase().contains(str.toLowerCase())) {
                String s = product.getTitle() + " " + "&" + product.getId() + "*";
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write(s);
            }
        }
    }

    @GetMapping("/addWishList")
    public @ResponseBody
    void addWishList(@RequestParam("id") int id,
                     @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        if (userDetails != null) {
            User user = ((CurrentUser) userDetails).getUser();
            if (cartRepository.findOneByUserIdAndProductId(user.getId(), id) == null) {
                productRepository.addLike(user.getId(), id);
            } else {
                response.getWriter().write("Wish");
            }
        } else {
            response.getWriter().write("NotFound");
        }
    }

    @GetMapping("/deletePrWishlist")
    public @ResponseBody
    void deleteInWishlist(@RequestParam("prId") int id,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = ((CurrentUser) userDetails).getUser();
        wishlistRepository.deleteWishlistById(wishlistRepository.findOneByUserIdAndProductId(user.getId(), id).getId());
    }

    @PostMapping("/addNewProduct")
    public String addProduct(@ModelAttribute(name = "newPr") Product product, @AuthenticationPrincipal UserDetails
            userDetails, @RequestParam("photo") List<MultipartFile> multipartFile, @RequestParam("category") int id) throws
            IOException {
        File file = new File(imageProductUploadUrl);
        file.mkdir();
        for (MultipartFile multipartFile1 : multipartFile) {
            String fileName = System.currentTimeMillis() + "_" + multipartFile1.getOriginalFilename();
            multipartFile1.transferTo(new File(imageProductUploadUrl + fileName));
            product.getProductImageList().add(ProductImage.builder().product(product).picUrl(fileName).build());
        }
        CurrentUser currentUser = (CurrentUser) userDetails;
        product.setUser(currentUser.getUser());
        product.setCategory(categoryRepository.findOneById(id));
        String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        product.setDate(time);
        product.setActive(0);
        productRepository.save(product);
        return "redirect:/addProduct";
    }

    @PostMapping("/allProduct")
    public @ResponseBody
    void allProduct(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws
            IOException {
        CurrentUser currentUser = (CurrentUser) userDetails;
        List<Product> productList = productRepository.findAllByUser(currentUser.getUser());
        response.setContentType("text/html;charset=UTF-8");
        for (Product product : productList) {
            if(product.getActive()==0) {
                response.getWriter().write(product.getTitle() + "&copy;" + product.getPrice() + "&copy;" + product.getId() + "&copy;" + "orange" + "\n");
            }else if(product.getActive()==-1){
                response.getWriter().write(product.getTitle() + "&copy;" + product.getPrice() + "&copy;" + product.getId() + "&copy;" + "red" + "\n");
            }else if(product.getActive()==1){
                response.getWriter().write(product.getTitle()  + "&copy;" + product.getPrice() + "&copy;" + product.getId() + "&copy;" + "green" + "\n");
            }
        }
    }

    @PostMapping("/deleteProduct")
    public @ResponseBody
    void deleteProduct(@RequestParam("productId") int id, HttpServletResponse response) throws IOException {
        Product product = productRepository.findOneById(id);
        List<ProductImage> images = product.getProductImageList();
        String str = "";
        for (int i = 0; i < images.size(); i++) {
            str = images.get(0).getPicUrl();
            System.out.println(new File(imageProductUploadUrl + images.get(i).getPicUrl()).delete());
        }
        if (!str.equals("")) {
            System.out.println(new File(imageProductUploadUrl + str).delete());
        }
        productRepository.deleteProductById(id);
        response.setContentType("text/plain");
        response.getWriter().write(String.valueOf(id));
    }


    @PostMapping("/editNewProduct")
    public String editProduct(@ModelAttribute("edPr") Product product, @AuthenticationPrincipal UserDetails
            userDetails) {
        User user = ((CurrentUser) userDetails).getUser();
        Product product1 = productRepository.findOneById(tempPr);
        product1.setUser(user);
        product1.setTitle(product.getTitle());
        product1.setDescription(product.getDescription());
        product1.setCount(product.getCount());
        String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        product1.setDate(time);
        product1.setPrice(product.getPrice());
        product1.setCategory(product.getCategory());
        product1.setActive(0);
        productRepository.save(product1);
        return "redirect:/user";
    }


}
