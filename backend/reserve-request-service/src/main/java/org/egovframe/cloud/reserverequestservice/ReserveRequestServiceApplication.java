package org.egovframe.cloud.reserverequestservice;

import java.security.Security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"org.egovframe.cloud.common", "org.egovframe.cloud.reserverequestservice"})
@EnableDiscoveryClient
@SpringBootApplication
public class ReserveRequestServiceApplication {

    public static void main(String[] args) {
        // TLSv1/v1.1 No longer works after upgrade, "No appropriate protocol" error
        String property = Security.getProperty("jdk.tls.disabledAlgorithms").replace(", TLSv1", "").replace(", TLSv1.1", "");
        Security.setProperty("jdk.tls.disabledAlgorithms", property);

        SpringApplication.run(ReserveRequestServiceApplication.class, args);
    }

}
