package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.injectors.annotations.TagInject
import com.day.cq.tagging.Tag
import com.day.cq.tagging.TagManager
import groovy.transform.TupleConstructor
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.service.component.annotations.Component

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

@Component(service = Injector, property = [
    "service.ranking:Integer=800"
])
class TagInjector extends AbstractComponentResourceInjector implements InjectAnnotationProcessorFactory2,
    AcceptsNullName, ModelTrait {

    @Override
    Object getValue(ComponentResource componentResource, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def annotation = element.getAnnotation(TagInject)
        def declaredClass = getDeclaredClassForDeclaredType(declaredType)

        def value = null

        if (declaredClass == Tag) {
            def tagManager = componentResource.resource.resourceResolver.adaptTo(TagManager)
            def tagIds = annotation && annotation.inherit() ? componentResource.getAsListInherited(name,
                String) : componentResource.getAsList(name, String)
            def tags = tagIds.collect { tagManager.resolve(it) }.findAll()

            if (tags) {
                value = isDeclaredTypeCollection(declaredType) ? tags : tags[0]
            }
        }

        value
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        def annotation = element.getAnnotation(TagInject)

        annotation ? new TagAnnotationProcessor(annotation) : null
    }

    @Override
    String getName() {
        TagInject.NAME
    }

    @TupleConstructor
    private static class TagAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        TagInject annotation

        @Override
        InjectionStrategy getInjectionStrategy() {
            annotation.injectionStrategy()
        }
    }
}
