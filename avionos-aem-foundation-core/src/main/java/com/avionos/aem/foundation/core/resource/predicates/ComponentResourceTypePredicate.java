package com.avionos.aem.foundation.core.resource.predicates;

import com.avionos.aem.foundation.api.resource.ComponentResource;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ComponentResourceTypePredicate implements Predicate<ComponentResource> {

    /**
     * sling:resourceType property value to filter on.
     */
    private final String resourceType;

    public ComponentResourceTypePredicate(final String resourceType) {
        this.resourceType = checkNotNull(resourceType);
    }

    @Override
    public boolean test(final ComponentResource componentResource) {
        return checkNotNull(componentResource).getResource().isResourceType(resourceType);
    }
}
