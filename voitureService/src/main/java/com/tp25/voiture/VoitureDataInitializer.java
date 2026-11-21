package com.tp25.voiture;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class VoitureDataInitializer {
    @Bean
    CommandLineRunner seedVoitures(VoitureRepository voitures) {
        return args -> {
            if (voitures.count() == 0) {
                voitures.save(new Voiture("Dacia", "Duster", 130, 1L));
                voitures.save(new Voiture("Renault", "Clio", 90, 2L));
            }
        };
    }
}
