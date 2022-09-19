package com.kovarpavel.ownyourfeed;

import com.kovarpavel.ownyourfeed.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class OwnyourfeedApplication {
	public static void main(String[] args) {
		SpringApplication.run(OwnyourfeedApplication.class, args);
	}
}
