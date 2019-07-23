package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector

import javax.inject.Named
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

abstract class AbstractComponentResourceInjector implements Injector, ModelTrait {

    abstract Object getValue(ComponentResource componentResource, String name, Type declaredType,
        AnnotatedElement element, DisposalCallbackRegistry callbackRegistry)

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        def componentResource = getResource(adaptable)?.adaptTo(ComponentResource)

        if (componentResource) {
            value = getValue(componentResource, element.getAnnotation(Named)?.value() ?: name, declaredType, element,
                callbackRegistry)
        }

        value
    }
}
