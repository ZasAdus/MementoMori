package com.example.mementomori;

public class User {
    private final String login;
    private final String imie;
    private final String nazwisko;
    private final String email;
    private final String nrTelefonu;

    public User(String login, String imie, String nazwisko, String email, String nrTelefonu) {
        this.login = login;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
        this.nrTelefonu = nrTelefonu;
    }

    public String getLogin() {
        return login;
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public String getNrTelefonu() {
        return nrTelefonu;
    }
}
