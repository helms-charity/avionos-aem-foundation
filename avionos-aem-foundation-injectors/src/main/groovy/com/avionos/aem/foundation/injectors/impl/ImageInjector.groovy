package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.injectors.annotations.ImageInject
import com.day.cq.wcm.foundation.Image
import groovy.transform.TupleConstructor
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2
import org.osgi.service.component.annotations.Component

import java.lang.reflect.AnnotatedElement
import java.util.function.Predicate

@Component(service = Injector, property = [
    "service.ranking:Integer=4000"
])
class ImageInjector extends AbstractTypedComponentResourceInjector<Image> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    @Override
    String getName() {
        ImageInject.NAME
    }

    @Override
    Object getValue(ComponentResource componentResource, String name, Class<Image> declaredType,
        AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
        def imageAnnotation = element.getAnnotation(ImageInject)
        def self = imageAnnotation ? imageAnnotation.self : false

        Image image
        Resource resource

        if (imageAnnotation?.inherit()) {
            def componentResourceInherit = componentResource.findAncestor(new Predicate<ComponentResource>() {
                @Override
                boolean test(ComponentResource cn) {
                    self ? cn.hasImage : cn.isHasImage(name)
                }
            })

            if (componentResourceInherit.present) {
                resource = componentResourceInherit.get().resource
            }
        } else {
            resource = componentResource.resource
        }

        def value = null

        if (resource) {
            if (self) {
                image = new Image(resource)
            } else {
                image = new Image(resource, name)
            }

            if (image.hasContent()) {
                if (imageAnnotation) {
                    if (imageAnnotation.selectors()) {
                        image.setSelector("." + imageAnnotation.selectors().join("."))
                    }
                } else {
                    image.setSelector(ImageInject.IMG_SELECTOR)
                }

                value = image
            }
        }

        value
    }

    @Override
    InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        // check if the element has the expected annotation
        def annotation = element.getAnnotation(ImageInject)

        annotation ? new ImageAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class ImageAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        ImageInject annotation

        @Override
        InjectionStrategy getInjectionStrategy() {
            annotation.injectionStrategy()
        }
    }
}
