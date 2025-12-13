package com.kidekdev.albummanager.ui.dispatcher.event;


import com.kidekdev.albummanager.ui.dispatcher.AppEvent;

import java.nio.file.Path;

public record IgnoreNewResourceEvent(Path path) implements AppEvent {

}
