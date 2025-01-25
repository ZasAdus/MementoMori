package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaWizyty;
import com.example.mementomori.bazyDanych.BazaHarmonogram;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Map;

public class WalidatorWizyt {
    private final BazaHarmonogram bazaLekarze;
    private final BazaWizyty bazaWizyty;
    private static final int CZAS_WIZYTY = 15; // czas wizyty w minutach

    public WalidatorWizyt() {
        this.bazaLekarze = new BazaHarmonogram();
        this.bazaWizyty = new BazaWizyty();
    }

    public boolean sprawdzMozliwoscWizyty(int idLekarza, LocalDateTime proponowanyTermin) {
        // 1. Sprawdź czy to nie jest przeszła data
        if (proponowanyTermin.isBefore(LocalDateTime.now())) {
            return false;
        }

        DayOfWeek dzienTygodnia = proponowanyTermin.getDayOfWeek();

        // 3. Sprawdź czy lekarz ma zdefiniowane godziny przyjęć w tym dniu
        LocalTime godzinaWizyty = proponowanyTermin.toLocalTime();
        Map<String, TimeRange> harmonogram = bazaLekarze.pobierzHarmonogram();
        TimeRange godzinyPracy = harmonogram.get(dzienTygodnia.toString());

        if (godzinyPracy == null) {
            return false;
        }

        // 4. Sprawdź czy wizyta mieści się w godzinach pracy
        if (godzinaWizyty.isBefore(godzinyPracy.getStartTime()) ||
                godzinaWizyty.plusMinutes(CZAS_WIZYTY).isAfter(godzinyPracy.getEndTime())) {
            return false;
        }

        // 5. Sprawdź czy termin nie koliduje z innymi wizytami
        return bazaWizyty.czyTerminDostepny(idLekarza, proponowanyTermin);
    }

    public String sprawdzPowodNiedostepnosci(int idLekarza, LocalDateTime proponowanyTermin) {
        if (proponowanyTermin.isBefore(LocalDateTime.now())) {
            return "Nie można umówić wizyty na datę z przeszłości";
        }

        DayOfWeek dzienTygodnia = proponowanyTermin.getDayOfWeek();
        if (dzienTygodnia == DayOfWeek.SATURDAY || dzienTygodnia == DayOfWeek.SUNDAY) {
            return "Lekarz nie przyjmuje w weekendy";
        }

        Map<String, TimeRange> harmonogram = bazaLekarze.pobierzHarmonogram();
        TimeRange godzinyPracy = harmonogram.get(dzienTygodnia.toString());

        if (godzinyPracy == null) {
            return "Lekarz nie przyjmuje w tym dniu tygodnia";
        }

        LocalTime godzinaWizyty = proponowanyTermin.toLocalTime();
        if (godzinaWizyty.isBefore(godzinyPracy.getStartTime()) ||
                godzinaWizyty.plusMinutes(CZAS_WIZYTY).isAfter(godzinyPracy.getEndTime())) {
            return "Wizyta poza godzinami przyjęć lekarza";
        }

        if (!bazaWizyty.czyTerminDostepny(idLekarza, proponowanyTermin)) {
            return "Termin jest już zajęty przez inną wizytę";
        }

        return "Termin dostępny";
    }
}