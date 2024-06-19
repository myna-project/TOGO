/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CsrfFilter;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;

import it.mynaproject.togo.api.filter.CorsFilter;
import it.mynaproject.togo.api.jwt.JwtAuthEntryPoint;
import it.mynaproject.togo.api.jwt.JwtAuthTokenFilter;
import it.mynaproject.togo.api.login.LoginFailureHandler;
import it.mynaproject.togo.api.login.LoginSuccessHandler;
import it.mynaproject.togo.api.login.LogoutSuccessHandler;
import it.mynaproject.togo.api.login.RestAuthenticationEntryPoint;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScans(value = { @ComponentScan("it.mynaproject.togo.api.mqtt"), @ComponentScan("it.mynaproject.togo.api.jwt") })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	/*
	 * Basic Authentication configuration
	 */
	@Configuration
	@Order(2)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.addFilterAfter(new CorsFilter(), SecurityContextPersistenceFilter.class);

			CsrfTokenResponseHeaderBindingFilter csrfTokenFilter = new CsrfTokenResponseHeaderBindingFilter();
			http.addFilterAfter(csrfTokenFilter, CsrfFilter.class);

			http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint()).and()

				.authorizeRequests()

				.antMatchers(HttpMethod.GET, "/token").permitAll()
				.antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/swagger/**").permitAll()

				.antMatchers("/wemo").hasRole("WEMO")
				.antMatchers(HttpMethod.POST, "/organization/formula").hasAnyRole("USER_RO", "USER")
				.antMatchers(HttpMethod.POST, "/organization/dashboard_widgets").hasAnyRole("USER_RO", "USER")
				.antMatchers(HttpMethod.PUT, "/organization/dashboard_widgets/**").hasAnyRole("USER_RO", "USER")
				.antMatchers(HttpMethod.DELETE, "/organization/dashboard_widgets/**").hasAnyRole("USER_RO", "USER")
				.antMatchers(HttpMethod.GET, "/organization/**").hasAnyRole("USER_RO", "USER")
				.antMatchers("/organization/**").hasRole("USER")
				.antMatchers("/**").hasRole("ADMIN")

				.anyRequest().authenticated().and().formLogin().loginProcessingUrl("/authenticate")
				.successHandler(new LoginSuccessHandler()).failureHandler(new LoginFailureHandler())

				.and().httpBasic()

				.and().logout().logoutSuccessHandler(new LogoutSuccessHandler());
		}
	}

	/*
	 * JWT Auth configuration
	 */
	@Configuration
	@Order(1)
	public static class JWTSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Bean
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		@Bean
		public JwtAuthTokenFilter authenticationJwtTokenFilter() {
			return new JwtAuthTokenFilter();
		}

		@Autowired
		private JwtAuthEntryPoint unauthorizedHandler;

		@Override
		protected void configure(HttpSecurity httpSecurity) throws Exception {

			httpSecurity
				.antMatcher("/jwt/**")
				.cors().and().csrf().disable()
				.authorizeRequests()
				.antMatchers("/**/auth/**").permitAll()
				.antMatchers("/jwt/sintra/**").hasRole("USER")
				.antMatchers("/**/test/admin").hasRole("ADMIN")
				.antMatchers("/**/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

			// Custom JWT based security filter
			httpSecurity.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new Pbkdf2CustomEncoder();
	}
}
