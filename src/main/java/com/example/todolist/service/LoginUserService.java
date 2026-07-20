package com.example.todolist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.todolist.entity.User;
import com.example.todolist.repository.UserRepository;

@Service
public class LoginUserService implements UserDetailsService  {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーは存在しません"));
		
		return new org.springframework.security.core.userdetails.User(
                // userName
                user.getUserName(),
                // password
                user.getPassword(),
                // enabled
                true,
                // accountNonExpired
                true,
                // credentialsNonExpired
                true,
                // accountNonLocked
                true,
                // authorities
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
		
		
	}
	
	
	
}
