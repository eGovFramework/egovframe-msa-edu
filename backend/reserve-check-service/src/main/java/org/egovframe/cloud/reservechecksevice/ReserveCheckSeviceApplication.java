package org.egovframe.cloud.reservechecksevice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import reactivefeign.spring.config.EnableReactiveFeignClients;
//import reactor.blockhound.BlockHound;

import java.security.Security;

@ComponentScan({"org.egovframe.cloud.common", "org.egovframe.cloud.reactive", "org.egovframe.cloud.reservechecksevice"}) // org.egovframe.cloud.common package 포함하기 위해
@EnableDiscoveryClient
@EnableReactiveFeignClients
@SpringBootApplication
public class ReserveCheckSeviceApplication {

    public static void main(String[] args) {
        // TLSv1/v1.1 No longer works after upgrade, "No appropriate protocol" error
        String property = Security.getProperty("jdk.tls.disabledAlgorithms").replace(", TLSv1", "").replace(", TLSv1.1", "");
        Security.setProperty("jdk.tls.disabledAlgorithms", property);

        SpringApplication.run(ReserveCheckSeviceApplication.class, args);
    }

}
