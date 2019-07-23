package com.avionos.aem.foundation.core.resource.predicates;

import com.avionos.aem.foundation.api.resource.ComponentResource;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ComponentResourcePropertyExistsPredicate implements Predicate<ComponentResource> {

    private final String propertyName;

    public ComponentResourcePropertyExistsPredicate(final String propertyName) {
        this.propertyName = checkNotNull(propertyName);
    }

    @Override
    public boolean test(final ComponentResource componentResource) {
        return componentResource.getResource().getValueMap().containsKey(propertyName);
    }
}
