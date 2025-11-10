package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;

abstract class BaseDatabaseServiceTest {

    protected DatabaseService databaseService;

    @BeforeEach
    void setUpDatabase() {
        Path databasePath = Path.of(
                "src", "main", "resources", "database-example", "metadata"
        );
        DatabaseLoader loader = new DatabaseLoader();
        this.databaseService = new InMemoryDatabaseService(loader.loadDatabase(databasePath));
    }
}
