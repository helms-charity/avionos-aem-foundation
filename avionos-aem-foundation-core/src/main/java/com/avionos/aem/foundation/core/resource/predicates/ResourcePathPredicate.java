package com.avionos.aem.foundation.core.resource.predicates;

import org.apache.sling.api.resource.Resource;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ResourcePathPredicate implements Predicate<Resource> {

    /**
     * JCR path to match against predicate input.
     */
    private final String path;

    public ResourcePathPredicate(final String path) {
        this.path = checkNotNull(path);
    }

    @Override
    public boolean test(final Resource resource) {
        return path.equals(checkNotNull(resource).getPath());
    }
}
