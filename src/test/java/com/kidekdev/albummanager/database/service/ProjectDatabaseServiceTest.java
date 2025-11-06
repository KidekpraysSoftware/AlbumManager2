package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.common.WorkflowStatus;
import com.kidekdev.albummanager.database.model.project.ProjectDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID PROJECT_FILE_ID = UUID.fromString("5f2dd787-e3ce-45f3-aa25-018a6c5cfd24");

    @Test
    void getAllProjectsReturnsExampleProject() {
        List<ProjectDto> projects = databaseService.getAllProjects();

        assertThat(projects)
                .hasSize(1)
                .first()
                .extracting(ProjectDto::getName)
                .isEqualTo("Life Holder");
    }

    @Test
    void getProjectByIdUsesDatabaseIdentifier() {
        Optional<ProjectDto> maybeProject = databaseService.getProjectById(PROJECT_FILE_ID);

        assertThat(maybeProject).isPresent();
        ProjectDto project = maybeProject.orElseThrow();
        assertThat(project.getStatus()).isEqualTo(WorkflowStatus.INPROGRESS);
        assertThat(project.getResources()).hasSize(3);
        assertThat(project.getId()).isEqualTo(UUID.fromString("bb75ba42-0257-422f-a90e-cd1066fdd9f2"));
    }

    @Test
    void saveProjectStoresNewProject() {
        UUID newProjectId = UUID.randomUUID();
        ProjectDto newProject = new ProjectDto(
                newProjectId,
                true,
                "Demo Project",
                WorkflowStatus.CREATED,
                List.of(),
                1700000000000L,
                "Test project",
                null
        );

        SaveResult result = databaseService.saveProject(newProject);

        assertThat(result.isSuccess()).isTrue();
        assertThat(databaseService.getProjectById(newProjectId)).contains(newProject);
    }

    @Test
    void saveProjectRejectsNull() {
        SaveResult result = databaseService.saveProject(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.message()).contains("project must not be null");
    }
}
