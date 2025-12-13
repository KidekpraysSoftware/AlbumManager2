package com.kidekdev.albummanager.ui.dispatcher.event;


import com.kidekdev.albummanager.database.dto.TagDto;
import com.kidekdev.albummanager.ui.dispatcher.AppEvent;

import java.nio.file.Path;
import java.util.Set;

public record AddNewResourceEvent(Path path, Boolean isDynamic) implements AppEvent {

}
