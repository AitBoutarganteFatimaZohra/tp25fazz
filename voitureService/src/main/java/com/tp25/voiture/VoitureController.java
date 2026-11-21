package com.tp25.voiture;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voitures")
public class VoitureController {
    private final VoitureRepository voitures;

    public VoitureController(VoitureRepository voitures) {
        this.voitures = voitures;
    }

    @GetMapping
    public List<Voiture> list() {
        return voitures.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voiture> get(@PathVariable Long id) {
        return voitures.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
