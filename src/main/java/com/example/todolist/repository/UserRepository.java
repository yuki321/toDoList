package com.example.todolist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todolist.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

/**
 * JpaRepositoryで定義されているため、findAll(), findByIdの再定義は不要
 * 
 */
//	List<User> findAll();
//	Optional<User> findById(Long id);
	
	//　バリデーション（存在チェック）
	boolean existsByUserName(final String userName);
    boolean existsByEmail(final String email);
    
//    Optional<User> findByLoginId(String userName);
    Optional<User> findByUserName(final String userName);

    List<User> findByEmail(final String email);
    
}



