package com.opinta.temp;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StartHsqlDbManager {
    private static final String USER = "sa";
    private static final String PASSWORD = USER;

    @PostConstruct
    public void startDBManager() {
        DatabaseManagerSwing.main(
                new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", USER, "--password", PASSWORD});
    }
}
