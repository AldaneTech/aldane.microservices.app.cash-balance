package net.aldane.cash_balance;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

class UserLanguageMigrationTest {
    @Test void addsSpanishDefaultsWithoutLosingExistingUsersAndCanRunTwice() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:language-migration;MODE=PostgreSQL");
             var statement = connection.createStatement()) {
            statement.execute("create table public.\"user\" (id int primary key, username varchar(255))");
            statement.execute("insert into public.\"user\" (id, username) values (1, 'existing')");
            String migration;
            try (var resource = getClass().getResourceAsStream("/db.changelog/migrate-user-language.sql")) {
                assertNotNull(resource);
                migration = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
            }
            statement.execute(migration);
            statement.execute(migration);
            try (var rows = statement.executeQuery("select username, language from public.\"user\" where id = 1")) {
                assertTrue(rows.next()); assertEquals("existing", rows.getString(1)); assertEquals("es", rows.getString(2));
            }
            statement.execute("insert into public.\"user\" (id, username) values (2, 'new')");
            try (var rows = statement.executeQuery("select language from public.\"user\" where id = 2")) {
                assertTrue(rows.next()); assertEquals("es", rows.getString(1));
            }
        }
    }
}
