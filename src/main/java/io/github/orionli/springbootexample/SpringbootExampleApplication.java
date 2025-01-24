package io.github.orionli.springbootexample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot示例应用程序
 *
 * @author OrionLi
 * @date 2024/04/21
 */
@MapperScan("io.github.orionli.springbootexample.dao")
@SpringBootApplication
public class SpringbootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootExampleApplication.class, args);
    }

}
