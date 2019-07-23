package com.avionos.aem.foundation.injectors.impl

import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.service.component.annotations.Component

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Component(service = Injector, property = [
    "service.ranking:Integer=999"
])
class ModelListInjector implements Injector, ModelTrait {

    @Override
    String getName() {
        "model-list"
    }

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        def resource = getResource(adaptable)

        if (resource && isParameterizedListType(declaredType)) {
            def typeClass = getActualType((ParameterizedType) declaredType)

            def childResource = resource.getChild(name)

            if (childResource) {
                value = childResource.children.collect {
                    grandChildResource -> grandChildResource.adaptTo(typeClass)
                }.findAll()

                if (!value) {
                    value = null
                }
            }
        }

        value
    }
}
