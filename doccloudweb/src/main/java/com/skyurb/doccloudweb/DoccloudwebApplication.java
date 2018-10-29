package com.skyurb.doccloudweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class DoccloudwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoccloudwebApplication.class, args);
    }
}
