package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.injectors.annotations.InheritInject
import groovy.transform.TupleConstructor
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.service.component.annotations.Component

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Component(service = Injector, property = [
    "service.ranking:Integer=4000"
])
class InheritInjector extends AbstractComponentResourceInjector implements InjectAnnotationProcessorFactory2 {

    @Override
    String getName() {
        InheritInject.NAME
    }

    @Override
    Object getValue(ComponentResource componentResource, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        if (element.getAnnotation(InheritInject)) {
            if (isParameterizedListType(declaredType)) {
                def typeClass = getActualType((ParameterizedType) declaredType)

                value = componentResource.getComponentResourcesInherited(name).collect { node ->
                    node.resource.adaptTo(typeClass)
                }
            } else if (declaredType instanceof Class && declaredType.enum) {
                def enumString = componentResource.getInherited(name, String)

                value = enumString.present ? declaredType[enumString.get()] : null
            } else {
                value = componentResource.getInherited(name, declaredType as Class).orElse(null)

                if (!value) {
                    value = componentResource.getComponentResourceInherited(name).orElse(null)?.resource?.adaptTo(
                        declaredType as Class)
                }
            }
        }

        value
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        def annotation = element.getAnnotation(InheritInject)

        annotation ? new InheritAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class InheritAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        InheritInject annotation

        @Override
        InjectionStrategy getInjectionStrategy() {
            annotation.injectionStrategy()
        }
    }
}
