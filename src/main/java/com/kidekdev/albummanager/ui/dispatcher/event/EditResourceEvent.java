package com.kidekdev.albummanager.ui.dispatcher.event;

import com.kidekdev.albummanager.ui.dispatcher.AppEvent;

import java.util.UUID;

public record EditResourceEvent(UUID id) implements AppEvent {
}
