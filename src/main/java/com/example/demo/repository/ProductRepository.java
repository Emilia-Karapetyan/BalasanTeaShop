package com.example.demo.repository;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Product findProductByUser(User user);
    List<Product> findAllByCategoryId(int id);


    List<Product> findProductByUserId(int id);

    List<Product> findAllByUser(User user);

    List<Product> findAllByCategoryIdAndUser(int id,User user);

    Product findOneById(int id);

    Product findOneByUserId(int id);
    @Transactional
    @Modifying
    @Query("delete from Product u where u.id = ?1")
    void deleteProductById(int id);

    List<Product> findAllByCategoryName(String cat);

    @Procedure(procedureName = "addLike")
    void addLike(int usId,int prId);

    List<Product> findAllByUserId(int id);

    @Transactional
    @Modifying
    @Query("update Product p set p.count=?1 where p.id=?2")
    void updateProduct(int c,int id);


    @Transactional
    @Modifying
    @Query("update Product p set p.active=?1 where p.id=?2")
    void updateActiveProduct(int a,int id);
}
