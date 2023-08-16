package com.example.model;

public class ChangerMotDePasseRequest {
    private String ancienMotDePasse;
    private String nouveauMotDePasse;

    public ChangerMotDePasseRequest(String ancienMotDePasse, String nouveauMotDePasse) {
        this.ancienMotDePasse = ancienMotDePasse;
        this.nouveauMotDePasse = nouveauMotDePasse;
    }

    public String getAncienMotDePasse() {
        return ancienMotDePasse;
    }

    public void setAncienMotDePasse(String ancienMotDePasse) {
        this.ancienMotDePasse = ancienMotDePasse;
    }

    public String getNouveauMotDePasse() {
        return nouveauMotDePasse;
    }

    public void setNouveauMotDePasse(String nouveauMotDePasse) {
        this.nouveauMotDePasse = nouveauMotDePasse;
    }
}
