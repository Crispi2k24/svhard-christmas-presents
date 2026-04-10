package pl.svhard.christmas.presents.database;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class DatabaseConfig extends OkaeriConfig implements DatabaseSettings {

    @Comment({
        "Typ sterownika bazy danych (np. SQLITE, H2, MYSQL, MARIADB, POSTGRESQL).",
        "Określa typ bazy danych do użycia."
    })
    public DatabaseDriverType databaseType = DatabaseDriverType.SQLITE;

    @Comment({"Hostname serwera bazy danych.", "Dla lokalnych baz danych to zazwyczaj 'localhost'."})
    public String hostname = "localhost";

    @Comment({
        "Numer portu serwera bazy danych. Popularne porty:",
        " - MySQL: 3306",
        " - PostgreSQL: 5432",
        " - H2: Nie dotyczy (bazowa plikowa)",
        " - SQLite: Nie dotyczy (bazowa plikowa)"
    })
    public int port = 3306;

    @Comment("Nazwa bazy danych do połączenia. To nazwa konkretnej instancji bazy danych.")
    public String database = "christmas_presents";

    @Comment("Nazwa użytkownika do połączenia z bazą danych. To konto używane do autentykacji.")
    public String username = "root";

    @Comment("Hasło do połączenia z bazą danych. To hasło dla określonego konta użytkownika.")
    public String password = "password";

    @Comment("Włącz SSL dla połączenia z bazą danych. Ustaw na true aby używać SSL/TLS dla bezpiecznych połączeń.")
    public boolean ssl;

    @Comment("Rozmiar puli połączeń. Określa maksymalną liczbę połączeń w puli.")
    public int poolSize = 16;

    @Comment("Timeout połączenia w milisekundach. To maksymalny czas oczekiwania na połączenie z puli.")
    public int timeout = 30000;

    @Override
    public DatabaseDriverType databaseType() {
        return this.databaseType;
    }

    @Override
    public String hostname() {
        return this.hostname;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public String database() {
        return this.database;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public boolean ssl() {
        return this.ssl;
    }

    @Override
    public int poolSize() {
        return this.poolSize;
    }

    @Override
    public int timeout() {
        return this.timeout;
    }
}
