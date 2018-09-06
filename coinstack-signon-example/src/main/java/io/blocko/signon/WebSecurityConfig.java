package io.blocko.signon;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Override 
  protected void configure(HttpSecurity http) throws Exception {
	  
    http.httpBasic().disable().logout().disable() 
    .addFilterAfter(new SSOFilter(), AbstractPreAuthenticatedProcessingFilter.class);
    
  }
}