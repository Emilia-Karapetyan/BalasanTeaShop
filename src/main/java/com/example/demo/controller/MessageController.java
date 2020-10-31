package com.example.demo.controller;

import com.example.demo.model.Message;

import com.example.demo.model.OutputMessage;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.FeedBackRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class MessageController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FeedBackRepository feedBackRepository;
    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public Message register(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message chatMessage) {
        User user = userRepository.findOneById(chatMessage.getUserId());
        String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        Product product = productRepository.findOneById(chatMessage.getPrId());
        OutputMessage message = OutputMessage.builder()
                .user(user)
                .product(product)
                .comment(chatMessage.getContent())
                .date(time)
                .build();
        System.out.println(chatMessage);
        System.err.println(message);
        feedBackRepository.save(message);
        return chatMessage;
    }

}
