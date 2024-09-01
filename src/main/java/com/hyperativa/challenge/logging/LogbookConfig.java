package com.hyperativa.challenge.logging;

import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.Conditions;

@Configuration
public class LogbookConfig {

	 @Bean  @ConditionalOnBean(Logbook.class)
	    public Logbook logbook() {	 
		 
	        Logbook logbook = Logbook.builder()
	            .condition(Conditions.exclude(
							Conditions.requestTo("/health"),
							Conditions.requestTo("/admin/**"),
							Conditions.contentType("application/octet-stream"),
							Conditions.header("X-Secret", Set.of("1", "true")::contains)))
					.build();
	        return logbook;
	    }
//	@Bean
//	public BodyFilter bodyFilter() {
//	    return HeaderFilter.merge(
//	    		BodyFilters.defaultValue(), 
//	    		HeaderFilter.repl(Collections.singleton("secret"), "XXX"));
//	}
//	
//    @Bean
//    public HeaderFilter authorizationHeaderFilter() {
//        return HeaderFilter.replaceHeaders("Authorization", "<filtered>");
//    }
//
//    @Bean
//    public QueryFilter queryFilter() {
//        return QueryFilter.replaceQuery("password", "<filtered>");
//    }
}
