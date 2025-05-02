package pyo.quizgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("main");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/sign-up").setViewName("signup");
        registry.addViewController("/add-quiz").setViewName("add-quiz");
        registry.addViewController("/quiz").setViewName("quiz");
        registry.addViewController("/user-messages").setViewName("user-messages");
        registry.addViewController("/user-boards").setViewName("user-boards");
        registry.addViewController("/posts").setViewName("posts");
        registry.addViewController("/add-post").setViewName("add-post");
        registry.addViewController("/error-page").setViewName("error-page");
        registry.addViewController("/user-page").setViewName("user-page");
        registry.addViewController("/user-post-info").setViewName("user-post-info");
        registry.addViewController("/user-comment-info").setViewName("user-comment-info");
        registry.addViewController("/user-block-info").setViewName("user-block-info");
        registry.addViewController("/edit-post").setViewName("edit-post");
        registry.addViewController("/user-reports").setViewName("user-reports");
    }

    //delete 사용하기위해 추가
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

}
