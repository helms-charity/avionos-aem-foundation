package com.avionos.aem.foundation.core.adapter

import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.core.page.impl.DefaultFoundationPageManager
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component

@Component(service = AdapterFactory, property = [
    "service.description=Avionos Foundation Adapter Factory",
    "adaptables=org.apache.sling.api.resource.ResourceResolver",
    "adapters=com.avionos.aem.foundation.api.page.FoundationPageManager"
])
final class FoundationAdapterFactory implements AdapterFactory {

    @Override
    <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        def result = null

        if (adaptable instanceof ResourceResolver) {
            result = getResourceResolverAdapter(adaptable, type)
        }

        result
    }

    private <AdapterType> AdapterType getResourceResolverAdapter(ResourceResolver resourceResolver,
        Class<AdapterType> type) {
        def result = null

        if (type == FoundationPageManager) {
            result = new DefaultFoundationPageManager(resourceResolver) as AdapterType
        }

        result
    }
}
