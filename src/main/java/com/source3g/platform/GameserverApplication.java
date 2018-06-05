package com.source3g.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class GameserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameserverApplication.class, args);
	}

	@Bean
	@ConfigurationProperties(prefix = "custom.rest.connection")
	public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {
		return new HttpComponentsClientHttpRequestFactory();
	}

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate(customHttpRequestFactory());
	}
}
