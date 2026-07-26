package com.example.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.todolist.entity.User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	public SecurityFilterChain fiterChain(HttpSecurity http) throws Exception {
		
		http
		.formLogin(formLogin -> formLogin
				.loginPage("/login")
				.permitAll()
		)
		.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/login").permitAll()  // ログインページは認証不要で使えるようにする
				.requestMatchers("/css/**").permitAll() // CSSファイルは認証不要で使えるようにする
				.anyRequest().authenticated()
		)
		// ログアウト設定を追加
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login") 
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
            
        );
		
		
		return http.build();
	}
	
	
	@Bean
	public WebSecurityCustomizer configure() throws Exception {
        return web -> web.ignoring().requestMatchers(
    		"/image/**",
            "/style/**" ,
            "/js/**"
		);
    }
	
	
	
}
