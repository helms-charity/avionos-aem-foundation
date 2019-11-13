package com.avionos.aem.foundation.injectors.impl;

import com.avionos.aem.foundation.api.resource.ComponentResource;
import com.avionos.aem.foundation.injectors.annotations.ImageInject;
import com.day.cq.wcm.foundation.Image;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

@Component(service = Injector.class)
@ServiceRanking(4000)
public final class ImageInjector extends AbstractTypedComponentResourceInjector<Image> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    @Override
    public String getName() {
        return ImageInject.NAME;
    }

    @Override
    public Object getComponentResourceValue(final ComponentResource componentResource, final String name,
        final Class<Image> declaredType, final AnnotatedElement element,
        final DisposalCallbackRegistry callbackRegistry) {
        final ImageInject annotation = element.getAnnotation(ImageInject.class);

        final boolean self = annotation != null && annotation.isSelf();

        Resource resource = null;

        if (annotation != null && annotation.inherit()) {
            final Optional<ComponentResource> componentResourceInherit = componentResource.findAncestor(
                cn -> self ? cn.isHasImage() : cn.isHasImage(name));

            if (componentResourceInherit.isPresent()) {
                resource = componentResourceInherit.get().getResource();
            }
        } else {
            resource = componentResource.getResource();
        }

        Object value = null;

        if (resource != null) {
            Image image;

            if (self) {
                image = new Image(resource);
            } else {
                image = new Image(resource, name);
            }

            if (image.hasContent()) {
                if (annotation != null) {
                    if (annotation.selectors().length > 0) {
                        image.setSelector("." + String.join(".", annotation.selectors()));
                    }
                } else {
                    image.setSelector(ImageInject.IMG_SELECTOR);
                }

                value = image;
            }
        }

        return value;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable,
        final AnnotatedElement element) {
        // check if the element has the expected annotation
        final ImageInject annotation = element.getAnnotation(ImageInject.class);

        return annotation != null ? new ImageAnnotationProcessor(annotation) : null;
    }

    static class ImageAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private ImageInject annotation;

        ImageAnnotationProcessor(final ImageInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
