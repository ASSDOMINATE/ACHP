package org.dominate.achp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author dominate
 * @since 2023-04-03
 */
@MapperScan("org.dominate.achp.mapper")
@EnableScheduling
@SpringBootApplication
public class AchpApplication {
    public static void main(String[] args) {
        SpringApplication.run(AchpApplication.class, args);
    }
}
