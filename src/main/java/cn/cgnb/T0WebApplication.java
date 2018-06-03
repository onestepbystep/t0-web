package cn.cgnb;

import cn.cgnb.conf.CrossDomainInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class T0WebApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(T0WebApplication.class);
	}

	@Bean
	public FilterRegistrationBean crossDomainFilterRegistration()
	{
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new CrossDomainInterceptor());
		registration.addUrlPatterns(new String[] { "/*" });
		registration.setName("crossDomainFilter");
		registration.setOrder(1);
		return registration;
	}

	public static void main(String[] args) {
		SpringApplication.run(T0WebApplication.class, args);
	}
}
