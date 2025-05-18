package com.example.mukormos;

import java.util.Date;

public class myTime {
    private String idopont;
    private String email;
    private Date ido;
    private String id;
    Boolean foglalt;
    public myTime() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getFoglalt() {
        return foglalt;
    }

    public void setFoglalt(Boolean foglalt) {
        this.foglalt = foglalt;
    }

    public myTime(String idopont, String email, Date ido) {
        this.idopont = idopont;
        this.email = email;
        this.ido = ido;
        this.id = "";
        this.foglalt = false;
    }

    // 🔓 Public getters
    public String getIdopont() {
        return idopont;
    }

    public String getEmail() {
        return email;
    }

    public Date getIdo() {
        return ido;
    }

    // (Optional) Setters — useful if reading data from Firestore
    public void setIdopont(String idopont) {
        this.idopont = idopont;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIdo(Date ido) {
        this.ido = ido;
    }
}
