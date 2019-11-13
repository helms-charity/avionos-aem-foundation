package com.avionos.aem.foundation.injectors.impl;

import com.avionos.aem.foundation.api.resource.ComponentResource;
import com.avionos.aem.foundation.injectors.utils.FoundationInjectorUtils;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractTypedComponentResourceInjector<T> implements Injector {

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final Type clazz = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        Object value = null;

        if (declaredType == clazz) {
            value = Optional.ofNullable(FoundationInjectorUtils.getResource(adaptable))
                .map(resource -> resource.adaptTo(ComponentResource.class))
                .map(componentResource -> {
                    final Named namedAnnotation = element.getAnnotation(Named.class);

                    return getComponentResourceValue(
                        componentResource,
                        Optional.ofNullable(namedAnnotation).map(Named :: value).orElse(name),
                        (Class<T>) declaredType,
                        element,
                        callbackRegistry
                    );
                })
                .orElse(null);
        }

        return value;
    }

    protected abstract Object getComponentResourceValue(final ComponentResource componentResource, final String name,
        final Class<T> declaredType, final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry);
}
