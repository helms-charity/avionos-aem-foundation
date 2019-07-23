package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.osgi.service.component.annotations.Component

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

@Component(service = Injector, property = [
    "service.ranking:Integer=4000"
])
class EnumInjector extends AbstractComponentResourceInjector {

    @Override
    String getName() {
        "enum"
    }

    @Override
    Object getValue(ComponentResource componentResource, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def value = null

        if (declaredType instanceof Class && declaredType.enum) {
            Optional<String> enumString = componentResource.get(name, String)

            if (enumString.present) {
                value = declaredType[enumString.get()]
            }
        }

        value
    }
}
