package brooklyn.entity.flyway;

import static com.google.common.base.Preconditions.checkNotNull;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.entity.annotation.Effector;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.ConfigKeys;

import com.google.api.client.util.Throwables;

public class FlywayEntity extends AbstractEntity {
    
    public static Logger LOG = LoggerFactory.getLogger(FlywayEntity.class);
    
    ConfigKey<String> DB_URL = ConfigKeys.newConfigKey(String.class, "dbUrl", "database URL");
    ConfigKey<String> USER = ConfigKeys.newConfigKey(String.class, "dbUser", "database user");
    ConfigKey<String> PASSWORD = ConfigKeys.newConfigKey(String.class, "dbPassword", "database password");
    ConfigKey<String> SCRIPT_LOCATIONS = ConfigKeys.newConfigKey(String.class, "scriptLocations", "script locations");
    
    @Effector(description = "Migrates the scripts in scriptLocations into the database at dbUrl using dbUser with password dbPassword")
    public void migrate() {
        try {
            System.out.println("=============================");
            System.out.println("about to migrate with the following: DB_URL: " + getConfig(DB_URL) + " USER: "
                    + getConfig(USER) + " PASSWORD: " + getConfig(PASSWORD) + " SCRIPT_LOCATIONS: " + getConfig(SCRIPT_LOCATIONS));
            System.out.println("=============================");
            checkNotNull(DB_URL, "DB_URL");
            checkNotNull(USER, "dbUser");
            checkNotNull(PASSWORD, "dbPassword");
            checkNotNull(SCRIPT_LOCATIONS, "script.locations");
            
            Flyway flyway = new Flyway();
            flyway.setDataSource(getConfig(DB_URL), getConfig(USER), getConfig(PASSWORD));
            String scriptLocations = getConfig(SCRIPT_LOCATIONS);
            flyway.setLocations(scriptLocations.split(","));

            flyway.migrate();
        } catch (RuntimeException e) {
            LOG.warn("Error migrating scripts in '" + SCRIPT_LOCATIONS + "' to " + DB_URL + " as " + USER + "; rethrowing...", e);
            throw Throwables.propagate(e);
        }
    }
    
}
