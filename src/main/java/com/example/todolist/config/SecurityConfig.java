package com.example.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
//				.defaultSuccessUrl("/", true)
				.loginPage("/login")
				.permitAll()
		)
		.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/users/create", "/login").permitAll()
				.requestMatchers("/css/**").permitAll() // CSSファイルは認証不要で使えるようにする
	            .requestMatchers("/").permitAll() //  トップページは認証不要
	            .requestMatchers("/create/**").hasRole("ADMIN")
	            .requestMatchers("/delete/**").hasRole("ADMIN")
				.anyRequest().authenticated()
		)
		// ログアウト設定を追加
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout") // ログアウト成功時は ?logout を付与してリダイレクト
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );
		
		
		return http.build();
	}
	
	
}
