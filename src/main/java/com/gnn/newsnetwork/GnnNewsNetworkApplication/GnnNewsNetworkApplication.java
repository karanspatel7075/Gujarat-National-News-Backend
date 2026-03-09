package com.gnn.newsnetwork.GnnNewsNetworkApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
//@EnableCaching
public class GnnNewsNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(GnnNewsNetworkApplication.class, args);
	}

}
