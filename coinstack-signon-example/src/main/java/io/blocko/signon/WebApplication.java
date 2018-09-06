package io.blocko.signon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class WebApplication {
  @RequestMapping("/main_page")
  public String mainPage(){ 
    return "main_page"; 
  }

  @RequestMapping("/admin_page")
  public String adminPage() {
    return "admin_page"; 
  }

  @RequestMapping("/user_page")
  public String userPage() {
    return "user_page"; 
  }

  @Bean
  public ErrorPageRegistrar errorPageRegistrar() {
    return new ErrorPageRegistrar() {
    @Override
      public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/403_page.jsp"));
      } 
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args); 
  }
}