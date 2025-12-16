package com.kidekdev.albummanager.ui.controller.event;

import com.kidekdev.albummanager.database.dto.DynamicResourceDto;
import com.kidekdev.albummanager.ui.context.DatabaseHolder;
import com.kidekdev.albummanager.ui.dispatcher.EventHandlerComponent;
import com.kidekdev.albummanager.ui.dispatcher.OnEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.AddNewDynamicResourceEvent;
import com.kidekdev.albummanager.ui.dispatcher.event.RefreshDynamicResourceEvent;
import com.kidekdev.albummanager.ui.dynamicresource.ResourceTypeDialog;
import javafx.scene.control.Dialog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.kidekdev.albummanager.database.type.ResourceType;

import java.util.List;
import java.util.Optional;

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
    public void refreshDynamicResourceState(AddNewDynamicResourceEvent event) {
        List<DynamicResourceDto> dtoList = DatabaseHolder.dynamicResource.findAll();
        dtoList.forEach(dto-> {




        });
    }
}
