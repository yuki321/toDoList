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
        .exceptionHandling(exception -> exception
            // 権限がないユーザーがアクセスした際のエラーハンドリング
            .accessDeniedPage("/")
        )
		.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/users/create", 
						"/login", 
						"/reset-password/**",
						"/validate_token", // パスワードリセット用のトークンを検証
						"/reset-password/**", // パスワード再設定画面
						"/error"
						).permitAll()  // ユーザー登録画面・ログイン画面は認証不要で使えるようにする
				.requestMatchers("/css/**").permitAll() // CSSファイルは認証不要で使えるようにする
				.requestMatchers("/api/users/**").hasAnyRole("ADMIN") // ユーザー登録は管理者のみアクセス可能
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
