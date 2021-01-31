package de.cloud.core.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class EventBasedExecutions implements Listener {

    private final MainBukkit plugin;
    private final Map<Class<? extends Event>, List<EventExecutorTask>> executionMap;
    private final HashSet<Class<? extends Event>> registeredEvents;
    private final EventExecutor executor;
    private final Listener dummyListener;

    public EventBasedExecutions(MainBukkit plugin) {
        this.plugin = plugin;
        this.executionMap = new HashMap<>();
        this.registeredEvents = new HashSet<>();
        this.dummyListener = new Listener() {};

        this.executor = (listener, event) ->
                this.executionMap.entrySet().removeIf(classListEntry -> {
                    if (event.getClass().equals(classListEntry.getKey())) {
                        classListEntry.getValue().removeIf(executorTask -> executorTask.execute(event));
                        return classListEntry.getValue().isEmpty();
                    }
                    return false;
                });
    }

    public void scheduleExecution(EventExecutorTask executor) {
        Type genericInterface = executor.getClass().getGenericInterfaces()[0];
        System.out.println(genericInterface.getTypeName());
        final Class<? extends Event> clazz = (Class<? extends Event>) ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        if (!this.registeredEvents.contains(clazz)) {
            Bukkit.getPluginManager().registerEvent(clazz, dummyListener, EventPriority.NORMAL, this.executor, this.plugin);
            this.registeredEvents.add(clazz);
        }
        List<EventExecutorTask> executors = this.executionMap.get(clazz);
        if (executors == null) {
            executors = new ArrayList<>();
            executors.add(executor);
            this.executionMap.put(clazz, executors);
            return;
        }
        executors.add(executor);
        this.executionMap.put(clazz, executors);
    }

    public interface EventExecutorTask<E extends Event> {
        boolean execute(E event);
    }
}
