package com.example.todolist.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.todolist.entity.User;

@Configuration
@EnableWebSecurity
public class UserConfig implements WebMvcConfigurer {

	// 設定を補完する情報のことをリゾルバ(resolver)と呼ぶ
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

		// Pageableに対して設定を行うためのクラスであり、リゾルバ
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();

		// ページ単位に表示する件数を追加(第一引数：ページ番号、第二引数：1ページあたりの表示件数)
		resolver.setFallbackPageable(PageRequest.of(0, 10));

		// 具体的な設定をリゾルバに追加後、リストに追加
		argumentResolvers.add(resolver);
	}

}
