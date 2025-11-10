package com.kidekdev.albummanager.database.loader;

import com.kidekdev.albummanager.database.model.DataBase;
import com.kidekdev.albummanager.database.utils.DatabaseUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseLoaderTest {

    private final DatabaseLoader loader = new DatabaseLoader();

    @Test
    void loadDatabaseLoadsSupportedEntities(@TempDir Path tempDir) throws IOException {
        UUID resourceId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();
        UUID importId = UUID.randomUUID();
        UUID journalId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID viewId = UUID.randomUUID();

        writeYaml(tempDir.resolve("resource").resolve(resourceId + ".yaml"), """
                id: %s
                isActive: true
                hash: "hash"
                resourceType: IMAGE
                extension: JPG
                sizeBytes: 512
                """.formatted(resourceId));

        writeYaml(tempDir.resolve("album").resolve(albumId + ".yaml"), """
                id: %s
                isActive: true
                name: "Test album"
                type: DRAFT
                resources: ["%s"]
                """.formatted(albumId, resourceId));

        writeYaml(tempDir.resolve("folder").resolve(folderId + ".yaml"), """
                id: %s
                path: "folder/path"
                resourceType: IMAGE
                """.formatted(folderId));

        writeYaml(tempDir.resolve("import").resolve(importId + ".yaml"), """
                id: %s
                path: "import/path"
                resourceTypes: [IMAGE]
                """.formatted(importId));

        writeYaml(tempDir.resolve("journal").resolve(journalId + ".yaml"), """
                id: %s
                isActive: true
                entries:
                  first:
                    type: USER
                    text: "Hello"
                """.formatted(journalId));

        writeYaml(tempDir.resolve("project").resolve(projectId + ".yaml"), """
                id: %s
                isActive: true
                name: "Project"
                status: INPROGRESS
                resources: ["%s"]
                """.formatted(projectId, resourceId));

        writeYaml(tempDir.resolve("view").resolve(viewId + ".yaml"), """
                id: %s
                isActive: true
                name: "My view"
                """.formatted(viewId));

        Files.createDirectories(tempDir.resolve("global"));
        Files.writeString(tempDir.resolve("global").resolve("taggroups.yaml"), """
                tagGroups:
                  mood: chill
                """.stripIndent(), StandardCharsets.UTF_8);

        Files.createDirectories(tempDir.resolve("resource"));
        Files.writeString(tempDir.resolve("resource").resolve("not-a-uuid.yaml"), "should be ignored", StandardCharsets.UTF_8);

        DataBase database = loader.loadDatabase(tempDir);

        assertThat(database.resources()).containsKey(resourceId);
        assertThat(database.albums()).containsKey(albumId);
        assertThat(database.folders()).containsKey(folderId);
        assertThat(database.imports()).containsKey(importId);
        assertThat(database.journals()).containsKey(journalId);
        assertThat(database.projects()).containsKey(projectId);
        assertThat(database.views()).containsKey(viewId);
        assertThat(database.pathIndex()).hasSize(7);
        assertThat(database.globalTagGroups().getTagGroups()).containsEntry("mood", "chill");
    }

    @Test
    void createDatabaseInitializesRequiredStructure(@TempDir Path tempDir) throws IOException {
        Path root = tempDir.resolve("database");

        DataBase database = loader.createDatabase(root);

        assertThat(database.pathIndex()).isEmpty();
        assertThat(database.globalTagGroups().getTagGroups()).isEmpty();

        Set<String> requiredFolders = DatabaseUtils.getDatabaseFolders();
        for (String folder : requiredFolders) {
            assertThat(root.resolve(folder)).exists().isDirectory();
        }

        Path tagGroupsFile = root.resolve("global").resolve("taggroups.yaml");
        assertThat(tagGroupsFile).exists();
        assertThat(Files.readString(tagGroupsFile)).isEqualTo("tagGroups: {}\n");
    }

    @Test
    void loadDatabaseFailsWhenUuidIsDuplicated(@TempDir Path tempDir) throws IOException {
        UUID duplicateId = UUID.randomUUID();

        writeYaml(tempDir.resolve("resource").resolve(duplicateId + ".yaml"), "id: %s\n".formatted(duplicateId));
        writeYaml(tempDir.resolve("album").resolve(duplicateId + ".yaml"), "id: %s\n".formatted(duplicateId));

        Files.createDirectories(tempDir.resolve("global"));
        Files.writeString(tempDir.resolve("global").resolve("taggroups.yaml"), "tagGroups: {}\n", StandardCharsets.UTF_8);

        assertThatThrownBy(() -> loader.loadDatabase(tempDir))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(duplicateId.toString());
    }

    private void writeYaml(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }
}
