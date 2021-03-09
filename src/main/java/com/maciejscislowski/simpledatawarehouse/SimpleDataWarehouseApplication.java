package com.maciejscislowski.simpledatawarehouse;

import com.adelean.inject.resources.spring.EnableResourceInjection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableResourceInjection
@EnableFeignClients
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class SimpleDataWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleDataWarehouseApplication.class, args);
    }

}
