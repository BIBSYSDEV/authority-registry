package no.bibsys;


import no.bibsys.controllers.DatabaseController;
import no.bibsys.controllers.DatabaseControllerExcepctionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;




@SpringBootApplication
@Import({DatabaseController.class, DefaultConfiguration.class,
    DatabaseControllerExcepctionHandler.class})
public class Application extends SpringBootServletInitializer {

  // silence console logging
  @Value("${logging.level.root:OFF}")
  String message = "";

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /*
   * Create required HandlerMapping, to avoid several default HandlerMapping instances being created
   */
  @Bean
  public HandlerMapping handlerMapping() {
    return new RequestMappingHandlerMapping();
  }

  /**
   * Create required HandlerAdapter, to avoid several default HandlerAdapter instances being created
   */
  @Bean
  public HandlerAdapter handlerAdapter() {
    return new RequestMappingHandlerAdapter();
  }

  //This is actually a bug because it shows everything as Error 500 (Internal server error)
//  /**
//   * optimization - avoids creating default exception resolvers; not required as the serverless container handles
//   * all exceptions
//   *
//   * By default, an ExceptionHandlerExceptionResolver is created which creates many dependent object, including
//   * an expensive ObjectMapper instance.
//   */

//  @Bean
//  public HandlerExceptionResolver handlerExceptionResolver() {
//    return new HandlerExceptionResolver() {
//
//      @Override
//      public ModelAndView resolveException(HttpServletRequest request,
//          HttpServletResponse response,
//          Object handler, Exception ex) {
//        return null;
//      }
//    };
//  }
}