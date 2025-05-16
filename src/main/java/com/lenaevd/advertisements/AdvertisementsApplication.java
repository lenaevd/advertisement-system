package com.lenaevd.advertisements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class AdvertisementsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvertisementsApplication.class, args);
    }
}
