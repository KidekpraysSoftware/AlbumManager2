package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.loader.DatabaseLoader;
import com.kidekdev.albummanager.database.loader.DatabaseLoadResult;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;

abstract class BaseDatabaseServiceTest {

    protected DatabaseService databaseService;

    @BeforeEach
    void setUpDatabase() {
        Path databasePath = Path.of(
                "src", "main", "resources", "com", "kidekdev", "albummanager", "database-example", "metadata"
        );
        DatabaseLoader loader = new DatabaseLoader();
        DatabaseLoadResult loadResult = loader.loadDatabase(databasePath);
        this.databaseService = new InMemoryDatabaseService(loadResult);
    }
}
