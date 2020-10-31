package com.example.demo.controller;

import com.example.demo.mail.EmailServiceImpl;
import com.example.demo.model.User;
import com.example.demo.model.UserGender;
import com.example.demo.model.UserType;
import com.example.demo.repository.*;
import com.example.demo.security.CurrentUser;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceImpl emailService;
    @Value("${image.upload.url}")
    private String imageUploadUrl;
    @Value("${image.upload.productPicUrl}")
    private String imageProductUploadUrl;
    private String tempEmail;
    private String tempPhone;
    private int tempPr;
    private int id;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    public static  int Id=0;


    @GetMapping("/user")
    public String userPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        CurrentUser currentUser = (CurrentUser) userDetails;
        map.addAttribute("tempUser",userRepository.findOneById(currentUser.getUser().getId()));
        map.addAttribute("cats",categoryRepository.findAll());
        map.addAttribute("products",productRepository.findAllByUser(currentUser.getUser()));
        return "user";
    }

    @GetMapping("/signIn")
    public String signInPage(ModelMap map) {
        map.addAttribute("us", new User());
        return "signIn";
    }

    @GetMapping("/signUp")
    public String signUpPage(ModelMap map, @ModelAttribute("errorPhone") String phone, @ModelAttribute("errorPass") String pass1, @ModelAttribute("errorConfPass") String pass, @ModelAttribute("errorMail") String mail) {
        if (pass.equals("")) {
            pass = "Confirm Password";
        }
        if (pass1.equals("")) {
            pass1 = "Enter Password";
        }
        if (mail.equals("")) {
            mail = "Enter Email";
        }
        if (phone.equals("")) {
            phone = "Enter phone";
        }
        map.addAttribute("errorConfPass", pass);
        map.addAttribute("errorPass", pass1);
        map.addAttribute("errorMail", mail);
        map.addAttribute("errorPhone", phone);
        map.addAttribute("newUser", new User());
        return "signUp";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("email") String email, @RequestParam("token") String token)
    {
        User user = userRepository.findOneByEmail(email);
        if (user != null) {
            if (user.getToken() != null && user.getToken().equals(token)) {
                user.setVerify(true);
                user.setToken(null);
                userRepository.save(user);
            }
        }
        return "redirect:/signIn";
    }



    @GetMapping("/login")
    public String login(@AuthenticationPrincipal UserDetails userDetails) {
        CurrentUser currentUser = (CurrentUser) userDetails;
        if (currentUser != null) {
//            Id = currentUser.getUser().getId();
            if (currentUser.getUser().getType().equals(UserType.ADMIN)) {
                return "redirect:/admin";
            } else {
                return "redirect:/user";
            }
        }
        return "redirect:/gallery";
    }

    @GetMapping("/verifyError")
    public String verifyError() {
        return "verifyError";
    }

    @GetMapping("/editUser")
    public String editUserPage(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        CurrentUser currentUser = (CurrentUser) userDetails;
        map.addAttribute("editUser",currentUser.getUser());
        if (currentUser.getUser().getType().equals(UserType.USER)){
            map.addAttribute("typeUs","user");
        }else{
            map.addAttribute("typeUs","admin");
        }
        return "editUser";
    }

    @GetMapping("/changePassword")
    public String changePassword(ModelMap map, @ModelAttribute("errorConfPass") String pass1,@ModelAttribute("errorCode") String code) {
        if (pass1.equals("")) {
            pass1 = "Confirm Password";
        }
        if(code.isEmpty()){
            code="Enter activate code";
        }
        map.addAttribute("errorCode",code);
        map.addAttribute("errorConfPass", pass1);
        return "forgotPass";
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage(ModelMap map, @ModelAttribute("errorEmail") String errorMail) {
        if (errorMail.equals("")) {
            errorMail = "Enter Email";
        }
        map.addAttribute("errorEmail", errorMail);

        return "forgotPassword";
    }

    @GetMapping("/image/{picUrl}")
    public void getImage(@PathVariable(name = "picUrl") String str, HttpServletResponse response) throws IOException {
        InputStream in = new FileInputStream(imageUploadUrl+str);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in,response.getOutputStream());
    }

    @PostMapping("/editNewUser")
    public String editUser(@ModelAttribute(name = "editUser") User user,@RequestParam("newPassword") String password,@AuthenticationPrincipal UserDetails userDetails,@RequestParam("photo") MultipartFile multipartFile) throws IOException {
        User us = ((CurrentUser) userDetails).getUser();
        if (!password.equals(""))
            us.setPassword(passwordEncoder.encode(password));
        if (!multipartFile.getOriginalFilename().isEmpty()) {
            File file = new File(imageUploadUrl);
            file.mkdir();
            String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            multipartFile.transferTo(new File(imageUploadUrl + fileName));
            us.setPicUrl(fileName);
        }
        us.setName(user.getName());
        us.setSurname(user.getSurname());
        us.setAge(user.getAge());
        us.setGender(user.getGender());
        userRepository.save(us);
        return "redirect:/user";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute("newUser") User user, @RequestParam("confPassword") String pass, @RequestParam("gender") UserGender userGender, @RequestParam("picture") MultipartFile multipartFile
            , RedirectAttributes attributes) throws IOException {
        if (user.getPassword().equals(pass) && !userRepository.existsUserByEmail(user.getEmail()) && validatePhoneNumber(user.getPhone()) && user.getPassword().length() >= 8 && !userRepository.existsUserByPhone(user.getPhone())) {
            if (!multipartFile.getOriginalFilename().isEmpty()) {
                File file = new File(imageUploadUrl);
                file.mkdir();
                String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
                multipartFile.transferTo(new File(imageUploadUrl + fileName));
                user.setPicUrl(fileName);
            } else
                user.setPicUrl("default.png");
            user.setType(UserType.USER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setGender(userGender);
            user.setToken(UUID.randomUUID().toString());
            user.setVerify(false);
            user.setActive(0);
            userRepository.save(user);
            String messUrl = String.format("http://localhost:8081/verify?email=%s&token=%s", user.getEmail(), user.getToken());
            String message = String.format(" Welcome dear %s \n Please follow link to activate your profile in  TeaShop \n %s", user.getName(), messUrl);
            emailService.sendSimpleMessage(user.getEmail(), "Welcome TeaShop", message);
            return "redirect:/";
        }
        if (!user.getPassword().equals(pass))
            attributes.addFlashAttribute("errorConfPass", "Password is not correct");
        if (!validatePhoneNumber(user.getPhone()))
            attributes.addFlashAttribute("errorPhone", "Phone is not correct");
        else if (userRepository.existsUserByPhone(user.getPhone()))
            attributes.addFlashAttribute("errorPhone", "Phone is Busy");
        if (userRepository.existsUserByEmail(user.getEmail()))
            attributes.addFlashAttribute("errorMail", "Email is busy");
        if (user.getPassword().length() < 8)
            attributes.addFlashAttribute("errorPass", "Min size 8 symbol");
        return "redirect:/signUp";
    }


    @PostMapping("/forgot")
    public String forgot(@RequestParam("forgotEmail") String email, RedirectAttributes attributes) {
        User user = userRepository.findOneByEmail(email);
        if (user!=null){
            if (!user.isVerify()){
                return "redirect:/verifyError";
            }
            tempEmail= email;
            int k = (int) (Math.floor(Math.random() * 8999) + 1000);
            user.setCode(k);
            System.out.println(1);
            userRepository.save(user);
            String message = String.format("Dear %s \n Use %s to verify your TeaShop account", user.getName(), k);
            emailService.sendSimpleMessage(user.getEmail(), "Forgot Password", message);
            return "redirect:/changePassword";
        }
        attributes.addFlashAttribute("errorEmail","Email not found");

        return "redirect:/forgotPassword";
    }


    @PostMapping("/changeNewPassword")
    public String changeNewPassword(@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, RedirectAttributes attributes,@RequestParam("code") String code) {
        User user = userRepository.findOneByEmail(tempEmail);
        try {
            if (newPassword.equals(confirmPassword) && user.getCode() == Integer.parseInt(code)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setCode(0);
                userRepository.save(user);
                return "redirect:/signIn";
            }
            if (!newPassword.equals(confirmPassword))
                attributes.addFlashAttribute("errorConfPass", "Password is not correct");
            attributes.addFlashAttribute("errorCode", "Code is not correct");
            return "redirect:/changePassword";
        }catch (NumberFormatException e){
            attributes.addFlashAttribute("errorCode", "Code is not correct");
            return "redirect:/changePassword";
        }
    }


    public static boolean validatePhoneNumber(String str) {
        Pattern pattern = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }




}
