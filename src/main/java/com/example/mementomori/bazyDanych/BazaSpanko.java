package com.example.mementomori.bazyDanych;

import com.example.mementomori.MementoMori;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BazaSpanko {
    // struktura wpisu w bazie danych o pojedyńczym śnie
    public record SpankoEntry(int id, Timestamp start, Timestamp stop) {
        public String getStartString() {
            LocalDateTime lc = start.toLocalDateTime();
            return lc.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        public double getStartHoursDouble() {
            return start.toLocalDateTime().getHour() + start.toLocalDateTime().getMinute() / 60.0;
        }

        public String getStopString() {
            LocalDateTime lc = stop.toLocalDateTime();
            return lc.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        public double getStopHoursDouble() {
            return stop.toLocalDateTime().getHour() + stop.toLocalDateTime().getMinute() / 60.0;
        }
    }

    // dane wpisu o harmonogramie snu danego użytkownika
    public record HarmonogramEntry(Time amimi, Time wakey_wakey) {
        public HarmonogramEntry() {
            // domyślny harmonogram, który zostanie zwrócony, jeśli nie ma wpisu w bazie
            this(Time.valueOf(LocalTime.of(22, 0)), Time.valueOf(LocalTime.of(6, 0)));
        }

        public String getAmimiString() {
            LocalTime lc = amimi.toLocalTime();
            return lc.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        public String getWakeyWakeyString() {
            LocalTime lc = wakey_wakey.toLocalTime();
            return lc.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static final String URL = SQLITE_PREFIX + "data/spanko.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Błąd połączenia z bazą danych.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void initTable() {
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS spanko(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    login TEXT NOT NULL,
                    poczatek DATE NOT NULL,
                    koniec DATE NOT NULL
                );
            """);
            stmt.execute();

            // osobno bo apparently nie można zrobić dwóch tabel w jednym execute
            stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS harmonogram_spanka(
                    login TEXT PRIMARY KEY,
                    amimi TIME NOT NULL,
                    wakey_wakey TIME NOT NULL
                );
            """);
            stmt.execute();
        }
        catch (SQLException e) {
            System.err.println("błąd przygotowania tabeli leków: ");
            e.printStackTrace();
        }
    }

    public static HarmonogramEntry getHarmonogram(String login) {
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT amimi, wakey_wakey FROM harmonogram_spanka WHERE login = ?;");
            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return new HarmonogramEntry(rs.getTime(2), rs.getTime(3));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return new HarmonogramEntry(); // domyślny harmonogram, jeśli coś poszło nie tak albo nie ma wpisu
    }

    public static void setHarmonogram(String login, Time amimi, Time wakey_wakey) {
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT OR REPLACE INTO harmonogram_spanka(login, amimi, wakey_wakey) VALUES (?, ?, ?);");
            stmt.setString(1, login);
            stmt.setTime(2, amimi);
            stmt.setTime(3, wakey_wakey);
            stmt.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final DateTimeFormatter TimestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static Timestamp parseTimestamp(String str) {
        return Timestamp.valueOf(LocalDateTime.parse(str, TimestampFormat));
    }

    public static List<SpankoEntry> getSpanko(Timestamp from_t, Timestamp to_t) {
        List<SpankoEntry> results = new ArrayList<>();
        try(Connection conn = connect()) {

            // wyszukuje wszystkie spanka, które nachodzą na podany przedział czasowy
            // ogranicza wyniki do przedziału
            // czyli jak w bazie jest spanie od 01-27 23:00 01-28 07:00
            // i wyszukujemy od 01-28 00:00 do 01-28 23:59
            // to wynik będzie 01-28 00:00 01-28 07:00
            String from = from_t.toLocalDateTime().format(TimestampFormat);
            String to = to_t.toLocalDateTime().format(TimestampFormat);
            System.out.println("from: " + from);
            System.out.println("to: " + to);
            System.out.println("current user: " + MementoMori.currentUser);
            PreparedStatement stmt = conn.prepareStatement("""
                select id, max(poczatek, ?), min(koniec, ?)
                from spanko
                    where
                        login = ?
                        and (poczatek between ? and ?
                             or koniec between ? and ?);
            """);
            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setString(3, MementoMori.currentUser);
            stmt.setString(4, from);
            stmt.setString(5, to);
            stmt.setString(6, from);
            stmt.setString(7, to);
            System.out.println(stmt);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                results.add(new SpankoEntry(rs.getInt(1), parseTimestamp(rs.getString(2)), parseTimestamp(rs.getString(3))));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static double getTotalSleepHours(Timestamp from_t, Timestamp to_t) {
        try(Connection conn = connect()) {
            // dno mojego człowieczeństwa
            // zwraca sumaryczny czas snu w godzinach z danego przedziału czasowego
            PreparedStatement stmt = conn.prepareStatement("""
                select sum((julianday(min(?, koniec)) - julianday(max(?, poczatek)))*24)
                from spanko
                    where
                        login = ?
                        and (poczatek between ? and ?
                             or koniec between ? and ?);
            """);
            String from = from_t.toLocalDateTime().format(TimestampFormat);
            String to = to_t.toLocalDateTime().format(TimestampFormat);
            stmt.setString(1, to);
            stmt.setString(2, from);
            stmt.setString(3, MementoMori.currentUser);
            stmt.setString(4, from);
            stmt.setString(5, to);
            stmt.setString(6, from);
            stmt.setString(7, to);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getTotalFilledDays(Timestamp from_t, Timestamp to_t) {
        try(Connection conn = connect()) {
            // XDDDDDDDDDDDDDDDD
            // ilość wypełnionych dni w bazie
            // żeby nie liczyć średniej z dni, w których nie ma wpisu
            PreparedStatement stmt = conn.prepareStatement("""
                select count(*) from
                (
                    select
                        strftime('%Y-%m-%d', max(?, poczatek))
                    from spanko
                        where
                        login = ?
                        and (poczatek between ? and ?
                                or koniec between ? and ?)
                    union
                    select
                        strftime('%Y-%m-%d', min(?, koniec))
                    from spanko
                        where
                        login = ?
                        and (poczatek between ? and ?
                                or koniec between ? and ?)
                );
            """);
            String from = from_t.toLocalDateTime().format(TimestampFormat);
            String to = to_t.toLocalDateTime().format(TimestampFormat);
            // boże wybacz, bo zgrzeszyłem
            stmt.setString(1, from);
            stmt.setString(2, MementoMori.currentUser);
            stmt.setString(3, from);
            stmt.setString(4, to);
            stmt.setString(5, from);
            stmt.setString(6, to);
            stmt.setString(7, from);
            stmt.setString(8, MementoMori.currentUser);
            stmt.setString(9, from);
            stmt.setString(10, to);
            stmt.setString(11, from);
            stmt.setString(12, to);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getAverageSleepHours(Timestamp from, Timestamp to) {
        double total_hours = getTotalSleepHours(from, to);
        double total_days = getTotalFilledDays(from, to);
        return total_hours / total_days;
    }

    public static SpankoEntry addSpanko(Timestamp start, Timestamp stop) {
        try(Connection conn = connect()) {
            // wyłączona ochorna ze względu na brak wykresu
//            List<SpankoEntry> colliding_entries = getSpanko(start, stop);
//            if (! colliding_entries.isEmpty()) {
//                throw new IllegalArgumentException("Spanie nie może się pokrywać z innym spaniem.");
//            }

            PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO spanko(login, poczatek, koniec) VALUES (?, ?, ?)"
            );
            String start_str = start.toLocalDateTime().format(TimestampFormat);
            String stop_str = stop.toLocalDateTime().format(TimestampFormat);
            stmt.setString(1, MementoMori.currentUser);
            stmt.setString(2, start_str);
            stmt.setString(3, stop_str);
            stmt.execute();

            stmt = conn.prepareStatement("SELECT last_insert_rowid();");
            ResultSet rs = stmt.executeQuery();

            return new SpankoEntry(rs.getInt(1), start, stop);
        }
        catch (SQLException err) {
            err.printStackTrace();
        }
        return null;
    }

    // nie dodaje możliwości edycji wpisów o spanku
    // tylko usuwanie i dodawanie od nowa

    public static void deleteSpankoByID(int id) {
        try(Connection conn = connect()) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM spanko WHERE id = ?;");
            statement.setInt(1, id);
            statement.executeUpdate();
        }
        catch (SQLException err) {
            err.printStackTrace();
        }
    }

    public static void deleteSpanko(SpankoEntry entry) {
        deleteSpankoByID(entry.id);
    }
}