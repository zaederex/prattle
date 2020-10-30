package com.neu.prattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * This class acts as the entry point for running the API as a Spring Boot application.
 *
 * @author zoheb.nawaz
 */
@SpringBootApplication
public class PrattleApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(PrattleApplication.class);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(PrattleApplication.class);
  }
}
