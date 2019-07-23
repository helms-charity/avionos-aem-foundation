package com.avionos.aem.foundation.core.resource.predicates;

import org.apache.sling.api.resource.Resource;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ResourceTypePredicate implements Predicate<Resource> {

    /**
     * sling:resourceType property value to filter on.
     */
    private final String resourceType;

    public ResourceTypePredicate(final String resourceType) {
        this.resourceType = checkNotNull(resourceType);
    }

    @Override
    public boolean test(final Resource resource) {
        return resourceType.equals(checkNotNull(resource).getResourceType());
    }
}
