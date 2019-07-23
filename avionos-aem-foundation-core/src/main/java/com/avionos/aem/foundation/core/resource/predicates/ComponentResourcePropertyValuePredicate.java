package com.avionos.aem.foundation.core.resource.predicates;

import com.avionos.aem.foundation.api.resource.ComponentResource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ComponentResourcePropertyValuePredicate<T> implements Predicate<ComponentResource> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentResourcePropertyValuePredicate.class);

    private final String propertyName;

    private final T propertyValue;

    public ComponentResourcePropertyValuePredicate(final String propertyName, final T propertyValue) {
        this.propertyName = checkNotNull(propertyName);
        this.propertyValue = checkNotNull(propertyValue);
    }

    @Override
    public boolean test(final ComponentResource componentResource) {
        final ValueMap properties = checkNotNull(componentResource).asMap();

        boolean result = false;

        if (properties.containsKey(propertyName)) {
            result = properties.get(propertyName, propertyValue.getClass()).equals(propertyValue);

            LOG.debug("property name = {}, value = {}, result = {} for component node = {}", propertyName,
                propertyValue, result, componentResource);
        } else {
            LOG.debug("property name = {}, does not exist for component node = {}", propertyName, componentResource);
        }

        return result;
    }
}
