package de.lightfall.core.bukkit;

import de.lightfall.core.api.channelhandeler.documents.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public class EventBasedExecutions implements Listener {

    private final MainBukkit plugin;
    private final Map<Class, List<EventExecutorTask>> executionMap;
    private final HashSet<Class<? extends Event>> registeredEvents;
    private final EventExecutor executor;
    private final Listener dummyListener;

    public EventBasedExecutions(MainBukkit plugin) {
        this.plugin = plugin;
        this.executionMap = new HashMap<>();
        this.registeredEvents = new HashSet<>();
        this.dummyListener = new Listener() {};

        this.executor = (listener, event) -> {
            this.executionMap.entrySet().removeIf(classListEntry -> {
                if (event.getClass().equals(classListEntry.getKey())) {
                    classListEntry.getValue().forEach(executorTask -> executorTask.execute(event));
                    return true;
                }
                return false;
            });
        };
    }

    public void scheduleExecution(EventExecutorTask executor) {
        final Class<? extends Event> clazz = (Class) ((ParameterizedType) executor.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        if (!registeredEvents.contains(clazz)) {
            Bukkit.getPluginManager().registerEvent(clazz, dummyListener, EventPriority.NORMAL, this.executor, this.plugin);
        }
        List<EventExecutorTask> executors = this.executionMap.get(clazz);
        if (executors == null) {
            executors = new ArrayList<>();
            executors.add(executor);
            this.executionMap.put(clazz, executors);
        }
        executors.add(executor);
        this.executionMap.put(clazz, executors);
    }

    public interface EventExecutorTask <E extends Event>{
        public void execute(E event);
    }
}
