package org.egovframe.cloud.reserverequestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import reactor.blockhound.BlockHound;

import java.security.Security;

@ComponentScan({"org.egovframe.cloud.common", "org.egovframe.cloud.reactive", "org.egovframe.cloud.reserverequestservice"}) // org.egovframe.cloud.common package 포함하기 위해
@EnableDiscoveryClient
@SpringBootApplication
public class ReserveRequestServiceApplication {

    public static void main(String[] args) {
        // TLSv1/v1.1 No longer works after upgrade, "No appropriate protocol" error
        String property = Security.getProperty("jdk.tls.disabledAlgorithms").replace(", TLSv1", "").replace(", TLSv1.1", "");
        Security.setProperty("jdk.tls.disabledAlgorithms", property);

        //blocking 코드 감지
        BlockHound.builder()
                //mysql r2dbc 에서 호출되는 FileInputStream.readBytes() 가 블로킹코드인데 이를 허용해주도록 한다.
                //해당 코드가 어디서 호출되는지 알지 못하는 상태에서 FileInputStream.readBytes() 자체를 허용해주는 것은 좋지 않다.
                // 누군가 무분별하게 사용하게 되면 검출해 낼 수ㅂ    없어 시스템의 위험요소로 남게 된다.
                // r2dbc를 사용하기 위해 해당 호출부분만 허용하고 나머지는 여전히 검출대상으로 남기도록 한다.
                .allowBlockingCallsInside("dev.miku.r2dbc.mysql.client.ReactorNettyClient", "init")
                .install();

        SpringApplication.run(ReserveRequestServiceApplication.class, args);
    }

}
