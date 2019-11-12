package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import org.apache.sling.api.resource.Resource

import java.util.function.Function

final class ComponentResourceFunctions {

    public static final Function<Resource, ComponentResource> RESOURCE_TO_COMPONENT_RESOURCE = new Function<Resource,
        ComponentResource>() {
        @Override
        ComponentResource apply(Resource resource) {
            new DefaultComponentResource(resource)
        }
    }

    private ComponentResourceFunctions() {

    }
}
