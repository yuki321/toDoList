package com.example.todolist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todolist.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

/**
 * JpaRepositoryで定義されているため、findAll(), findByIdの再定義は不要
 * 
 */
//	List<User> findAll();
//	Optional<User> findById(Long id);
	
	//　バリデーション（存在チェック）
	boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    
//    Optional<User> findByLoginId(String userName);
    Optional<User> findByUserName(String userName);

    
    
}



