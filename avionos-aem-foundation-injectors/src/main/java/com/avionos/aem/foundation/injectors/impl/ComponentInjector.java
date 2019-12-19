package com.avionos.aem.foundation.injectors.impl;

import com.avionos.aem.foundation.api.page.FoundationPage;
import com.avionos.aem.foundation.api.page.FoundationPageManager;
import com.avionos.aem.foundation.api.resource.ComponentResource;
import com.avionos.aem.foundation.injectors.utils.FoundationInjectorUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.collect.ImmutableList;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Injector for objects derived from the current component context.
 */
@Component(service = Injector.class)
@ServiceRanking(Integer.MIN_VALUE)
public final class ComponentInjector implements Injector {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentInjector.class);

    private static final List<Class> REQUEST_INJECTABLES = ImmutableList.of(
        SlingHttpServletRequest.class,
        WCMMode.class
    );

    private static final List<Class> RESOURCE_INJECTABLES = ImmutableList.of(
        Resource.class,
        ResourceResolver.class,
        ValueMap.class,
        ComponentResource.class,
        Page.class,
        FoundationPage.class,
        PageManager.class,
        FoundationPageManager.class
    );

    @Override
    public String getName() {
        return "component";
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
        final DisposalCallbackRegistry registry) {
        Object value = null;

        if (type instanceof Class) {
            final Class clazz = (Class) type;

            if (REQUEST_INJECTABLES.contains(clazz)) {
                value = getValueForRequest(clazz, adaptable);
            } else if (RESOURCE_INJECTABLES.contains(clazz)) {
                value = getValueForResource(clazz, adaptable);
            }
        }

        return value;
    }

    private Object getValueForRequest(final Class clazz, final Object adaptable) {
        final SlingHttpServletRequest request = FoundationInjectorUtils.getRequest(adaptable);

        Object value = null;

        if (request != null) {
            if (clazz == SlingHttpServletRequest.class) {
                value = request;
            } else if (clazz == WCMMode.class) {
                value = WCMMode.fromRequest(request);
            } else {
                LOG.debug("class : {} is not supported by this injector", clazz.getName());
            }

            LOG.debug("injecting class : {} with instance : {}", clazz.getName(), value);
        }

        return value;
    }

    private Object getValueForResource(final Class clazz, final Object adaptable) {
        final Resource resource = FoundationInjectorUtils.getResource(adaptable);

        Object value = null;

        if (resource != null) {
            if (clazz == Resource.class) {
                value = resource;
            } else if (clazz == ResourceResolver.class) {
                value = resource.getResourceResolver();
            } else if (clazz == ValueMap.class) {
                value = resource.getValueMap();
            } else if (clazz == ComponentResource.class) {
                value = resource.adaptTo(ComponentResource.class);
            } else if (clazz == FoundationPage.class || clazz == Page.class) {
                value = resource.getResourceResolver().adaptTo(FoundationPageManager.class).getContainingPage(resource);
            } else if (clazz == FoundationPageManager.class || clazz == PageManager.class) {
                value = resource.getResourceResolver().adaptTo(FoundationPageManager.class);
            } else {
                LOG.debug("class : {} is not supported by this injector", clazz.getName());
            }

            LOG.debug("injecting class : {} with instance : {}", clazz.getName(), value);
        }

        return value;
    }
}
