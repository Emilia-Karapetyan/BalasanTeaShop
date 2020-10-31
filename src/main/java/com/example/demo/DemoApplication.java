package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.model.UserGender;
import com.example.demo.model.UserType;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsUserByEmail("der@gmail.com")) {
            User user = User.builder()
                    .name("Deren")
                    .surname("Saakyan")
                    .age(21)
                    .email("der@gmail.com")
                    .password(new BCryptPasswordEncoder().encode("12345678"))
                    .phone("+37455555555")
                    .gender(UserGender.MALE)
                    .verify(true)
                    .token(null)
                    .type(UserType.ADMIN)
                    .picUrl("default.png")
                    .code(0)
                    .build();
            userRepository.save(user);
        }

    }
}
