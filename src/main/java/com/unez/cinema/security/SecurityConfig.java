package com.unez.cinema.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private DataSource dataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder passwordEncoder = passwordEncoder();
		System.out.println("****************************");
		System.out.println(passwordEncoder.encode("admin"));
		System.out.println("****************************");
		System.out.println("****************************");
		System.out.println(passwordEncoder.encode("user"));
		System.out.println("****************************");
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery("SELECT username as principal, password as credentials, active from users where username=?")
			.authoritiesByUsernameQuery("SELECT username as principal, role from users_roles where username=?")
			.passwordEncoder(passwordEncoder)
			.rolePrefix("ROLE_");
		
		/*
		auth.inMemoryAuthentication().withUser("younes").password(passwordEncoder.encode("younes")).roles("USER");
		auth.inMemoryAuthentication().withUser("zouid").password(passwordEncoder.encode("zouid")).roles("USER");
		auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN");*/
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login");
		http.authorizeRequests().antMatchers("/save**/**","/delete**/**","/form**/**").hasRole("ADMIN");
		http.authorizeRequests().antMatchers("/**gest/**").hasRole("USER");
		http.authorizeRequests().antMatchers("/login/**","/webjars/**").permitAll();
		//http.authorizeRequests().anyRequest().authenticated();
		http.exceptionHandling().accessDeniedPage("/notAuthorized");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
