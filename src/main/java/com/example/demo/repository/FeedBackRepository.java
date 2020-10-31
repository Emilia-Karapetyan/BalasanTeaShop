package com.example.demo.repository;

import com.example.demo.model.OutputMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedBackRepository extends JpaRepository<OutputMessage,Integer> {
List<OutputMessage> findAllByProductId(int id);
}
