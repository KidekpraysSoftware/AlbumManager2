package com.kidekdev.albummanager.ui.controller.event;

import com.kidekdev.albummanager.common.OperationResult;
import com.kidekdev.albummanager.database.type.ResourceType;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventHandlerComponent;
import com.kidekdev.albummanager.ui.dispatcher.OnEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewResourceEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Slf4j
@EventHandlerComponent
public class ResourceEventController {

    @OnEvent(AddNewResourceEvent.class)
    public void addNewResource(AddNewResourceEvent event) {
        Path path = event.path();
        log.info("Добавление нового ресурса {}", path);
//        ResourceDto resourceDto = ResourceDto.builder()
//                .id(UUID.randomUUID())
//                .isActive(true)
//                .path(path.toString())
//                .hash("Случайный хеш")
//                .resourceType(ResourceType.TRACK) //todo заменить определителем ресурса
//                .extension(ResourceExtension.MP3)
//                .description(null)
//                .sizeBytes(123L)
//                .importedAt(123L)
//                .created(123L)
//                .modified(123L)
//                .tags(List.of("Тег 1", "Тег 2"))
//                .metadata(null)
//                .build();
//        OperationResult result = DatabaseHolder.databaseFacade.saveResource(resourceDto);
//        if (result.isSuccess()) {
//            log.info("Ресурс сохранен {}", resourceDto);
//        } else {
//            log.error("Не удалось сохранить ресурс {}", result);
//        }

    }
}
