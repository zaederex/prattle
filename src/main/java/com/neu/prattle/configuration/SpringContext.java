package com.neu.prattle.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Configuration for the spring application context.
 */
@Component
public class SpringContext implements ApplicationContextAware {

  private static ApplicationContext context;

  /**
   * Sets the static application context.
   *
   * @param ctx context to be set
   */
  private static void setContext(ApplicationContext ctx) {
    context = ctx;
  }

  @Override
  public void setApplicationContext(ApplicationContext ctx) {
    setContext(ctx);
  }

  /**
   * Returns the Bean from the application context.
   *
   * @param beanClass the Bean class.
   * @param <T>       the type of Bean class.
   * @return the bean
   */
  public static <T> T getBean(Class<T> beanClass) {
    return context.getBean(beanClass);
  }
}
