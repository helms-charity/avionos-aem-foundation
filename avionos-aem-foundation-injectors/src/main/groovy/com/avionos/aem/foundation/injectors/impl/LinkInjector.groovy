package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.link.Link
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.link.builders.factory.LinkBuilderFactory
import com.avionos.aem.foundation.injectors.annotations.LinkInject
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
import java.util.function.Function

@Component(service = Injector, property = [
    "service.ranking:Integer=4000"
])
class LinkInjector extends AbstractTypedComponentResourceInjector<Link> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    @Override
    String getName() {
        LinkInject.NAME
    }

    @Override
    Object getValue(ComponentResource componentResource, String name, Class<Link> declaredType,
        AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        def injectAnnotation = element.getAnnotation(LinkInject)

        Optional<String> pathOptional

        String title = null

        if (injectAnnotation) {
            if (injectAnnotation.inherit()) {
                pathOptional = componentResource.getInherited(name, String)

                if (injectAnnotation.titleProperty()) {
                    title = componentResource.getInherited(injectAnnotation.titleProperty(), String).orElse(null)
                }
            } else {
                pathOptional = componentResource.get(name, String)

                if (injectAnnotation.titleProperty()) {
                    title = componentResource.get(injectAnnotation.titleProperty(), String).orElse(null)
                }
            }
        } else {
            pathOptional = componentResource.get(name, String)
        }

        pathOptional.map(new Function<String, Link>() {
            @Override
            Link apply(String path) {
                LinkBuilderFactory.forPath(path).setTitle(title).build()
            }
        }).orElse(null)
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        // check if the element has the expected annotation
        def annotation = element.getAnnotation(LinkInject)

        annotation ? new LinkAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class LinkAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        LinkInject annotation

        @Override
        InjectionStrategy getInjectionStrategy() {
            annotation.injectionStrategy()
        }
    }
}
