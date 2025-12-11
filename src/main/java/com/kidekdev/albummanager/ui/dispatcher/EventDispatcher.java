package com.kidekdev.albummanager.ui.dispatcher;

import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

@UtilityClass
public class EventDispatcher {

    private final Map<Class<? extends AppEvent>, List<HandlerMethod>> handlers = new HashMap<>();

    public void dispatch(AppEvent event) {
        List<HandlerMethod> methods = handlers.get(event.getClass());
        if (methods != null) {
            for (HandlerMethod handler : methods) {
                try {
                    handler.method.invoke(handler.instance, event);
                } catch (Exception e) {
                    System.err.println("Ошибка при обработке события: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void scanAndRegisterHandlers(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> handlerClasses = reflections.getTypesAnnotatedWith(EventHandlerComponent.class);

        for (Class<?> clazz : handlerClasses) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                register(instance);
            } catch (Exception e) {
                System.err.println("Не удалось создать экземпляр " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    private void register(Object handlerInstance) {
        for (Method method : handlerInstance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                Class<? extends AppEvent> eventType = method.getAnnotation(OnEvent.class).value();
                method.setAccessible(true); // на случай private
                handlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                        .add(new HandlerMethod(handlerInstance, method));
            }
        }
    }

    private record HandlerMethod(Object instance, Method method) {
    }
}