package com.kidekdev.albummanager.ui.controller.event;

import com.kidekdev.albummanager.database.dto.DynamicResourceDto;
import com.kidekdev.albummanager.database.dto.ResourceDto;
import com.kidekdev.albummanager.ui.context.ControllerHolder;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventHandlerComponent;
import com.kidekdev.albummanager.ui.dispatcher.OnEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewDynamicResourceEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.RefreshDynamicResourceEvent;
import com.kidekdev.albummanager.ui.dto.PathInfo;
import com.kidekdev.albummanager.ui.dynamicresource.ResourceTypeDialog;
import com.kidekdev.albummanager.ui.utils.FileUtils;
import com.kidekdev.albummanager.ui.utils.HashUtils;
import javafx.scene.control.Dialog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.kidekdev.albummanager.database.type.ResourceType;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@EventHandlerComponent
public class DynamicResourceEventController {

    @SneakyThrows
    @OnEvent(AddNewDynamicResourceEvent.class)
    public void addNewDynamicResource(AddNewDynamicResourceEvent event) {
        log.info("Старт добавления динамического ресурса");
        Dialog<ResourceType> dialog = ResourceTypeDialog.create();
        Optional<ResourceType> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        ResourceType resourceType = result.get();

        DynamicResourceDto dto = DynamicResourceDto.builder()
                .path(event.path().toString())
                .resourceType(resourceType)
                .build();
        DatabaseHolder.dynamicResource.save(dto);
    }

    @SneakyThrows
    @OnEvent(RefreshDynamicResourceEvent.class)
    public void refreshDynamicResourceState(RefreshDynamicResourceEvent event) {
        List<DynamicResourceDto> dtoList = DatabaseHolder.dynamicResource.findAll();
        Map<String, String> hashOwners = new HashMap<>();

        dtoList.forEach(dto -> {
            Path rootPath = Path.of(dto.path());
            Set<String> extensions = ResourceType.getExtensions(dto.resourceType());
            List<Path> resources = FileUtils.findFilesByExtensions(rootPath, extensions);
            List<ResourceFileState> fileStates = resources.stream()
                    .map(path -> new ResourceFileState(path, HashUtils.sha256(path), FileUtils.getEarliestFileTime(path.toFile())))
                    .toList();

            fileStates.forEach(state -> {
                String ownerKey = Optional.ofNullable(dto.id())
                        .map(UUID::toString)
                        .orElse(dto.path());
                String previousOwner = hashOwners.putIfAbsent(state.hash(), ownerKey);
                if (previousOwner != null && !previousOwner.equals(ownerKey)) {
                    throw new IllegalStateException("Пересечение хешей между динамическими ресурсами: " + state.hash());
                }
            });

            if (fileStates.isEmpty()) {
                return;
            }

            Map<String, ResourceDto> existingByHash = DatabaseHolder.resource.findAllByHash(
                            fileStates.stream().map(ResourceFileState::hash).toList())
                    .stream()
                    .collect(Collectors.toMap(ResourceDto::hash, r -> r));

            List<ResourceFileState> newResources = fileStates.stream()
                    .filter(state -> !existingByHash.containsKey(state.hash()))
                    .toList();

            List<ResourceDto> existingResources = new ArrayList<>(existingByHash.values());

            if (newResources.isEmpty()) {
                return;
            }

            if (existingResources.isEmpty()) {
                activateLatestAndSave(newResources, dto.resourceType(), null);
                return;
            }

            existingResources.forEach(resource -> DatabaseHolder.resource.deactivate(resource.id()));
            ResourceDto latestExisting = resolveLatestExisting(existingResources);
            activateLatestAndSave(newResources, dto.resourceType(), latestExisting);
        });

        if (ControllerHolder.mainTabController != null) {
            ControllerHolder.mainTabController.updateMainResourceList();
        }
    }

    private ResourceDto resolveLatestExisting(List<ResourceDto> resources) {
        return resources.stream()
                .max(Comparator.comparing(ResourceDto::importedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ResourceDto::fileCreationTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private void activateLatestAndSave(List<ResourceFileState> newResources, ResourceType resourceType, ResourceDto template) {
        if (newResources.isEmpty()) {
            return;
        }

        ResourceFileState newest = newResources.stream()
                .max(Comparator.comparing(ResourceFileState::creationTime))
                .orElseThrow();

        newResources.forEach(state -> {
            boolean isActive = newest.equals(state);
            ResourceDto dto = buildResourceDto(state, resourceType, template, isActive);
            DatabaseHolder.resource.save(dto);
        });
    }

    private ResourceDto buildResourceDto(ResourceFileState state, ResourceType resourceType, ResourceDto template, boolean isActive) {
        PathInfo pathInfo = FileUtils.getPathInfo(state.path());
        String authorName = template != null && template.authorName() != null
                ? template.authorName()
                : "Неизвестен";
        String resourceName = template != null && template.resourceName() != null
                ? template.resourceName()
                : pathInfo.fileName();

        return ResourceDto.builder()
                .resourceName(resourceName)
                .authorName(authorName)
                .description(template != null ? template.description() : null)
                .isActive(isActive)
                .path(state.path().toString())
                .isDynamic(true)
                .hash(state.hash())
                .resourceType(resourceType)
                .extension(pathInfo.extension())
                .fileCreationTime(state.creationTime())
                .tags(template != null && template.tags() != null ? template.tags() : Set.of())
                .journal(template != null ? template.journal() : null)
                .build();
    }

    private record ResourceFileState(Path path, String hash, OffsetDateTime creationTime) {
    }
}
