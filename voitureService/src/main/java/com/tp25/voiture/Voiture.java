package com.tp25.voiture;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marque;
    private String modele;
    private int puissance;
    private Long clientId;

    protected Voiture() {
    }

    public Voiture(String marque, String modele, int puissance, Long clientId) {
        this.marque = marque;
        this.modele = modele;
        this.puissance = puissance;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public int getPuissance() {
        return puissance;
    }

    public Long getClientId() {
        return clientId;
    }
}
