package com.tp25.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClientDataInitializer {
    @Bean
    CommandLineRunner seedClients(ClientRepository clients) {
        return args -> {
            if (clients.count() == 0) {
                clients.save(new Client("Amina El Idrissi", "amina@example.com"));
                clients.save(new Client("Youssef Benali", "youssef@example.com"));
            }
        };
    }
}
