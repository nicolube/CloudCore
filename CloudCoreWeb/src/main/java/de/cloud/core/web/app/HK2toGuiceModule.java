package de.cloud.core.web.app;

import com.google.inject.Injector;
import de.cloud.core.common.DatabaseProvider;
import de.cloud.core.web.app.config.Config;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class HK2toGuiceModule extends AbstractBinder {
    private Injector guiceInjector;

    public HK2toGuiceModule(Injector guiceInjector) {
        this.guiceInjector = guiceInjector;
    }

    @Override
    protected void configure() {
        add(DatabaseProvider.class);
        add(WebApplication.class);
        add(Config.class);
        add(ImmutableContextSet.class);
        add(QueryOptions.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void add(Class clazz) {
        bindFactory(new ServiceFactory<>(clazz)).to(clazz);
    }

    private class ServiceFactory<T> implements Factory<T> {

        private final Class<T> serviceClass;

        public ServiceFactory(Class<T> serviceClass) {

            this.serviceClass = serviceClass;
        }

        @Override
        public T provide() {
            return guiceInjector.getInstance(serviceClass);
        }

        @Override
        public void dispose(T versionResource) {
        }
    }
}