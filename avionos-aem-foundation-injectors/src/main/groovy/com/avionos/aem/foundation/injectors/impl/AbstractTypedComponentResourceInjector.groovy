package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector

import javax.inject.Named
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class AbstractTypedComponentResourceInjector<T> implements Injector, ModelTrait {

    abstract Object getValue(ComponentResource componentResource, String name, Class<T> declaredType,
        AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry)

    @Override
    final Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def clazz = (getClass().genericSuperclass as ParameterizedType).actualTypeArguments[0]

        def value = null

        if (declaredType == clazz) {
            def componentResource = getResource(adaptable)?.adaptTo(ComponentResource)

            if (componentResource) {
                value = getValue(componentResource, element.getAnnotation(Named)?.value() ?: name, declaredType,
                    element, callbackRegistry)
            }
        }

        value
    }
}
