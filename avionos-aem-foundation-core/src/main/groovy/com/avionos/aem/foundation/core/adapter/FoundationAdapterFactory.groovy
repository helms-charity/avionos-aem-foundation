package com.avionos.aem.foundation.core.adapter

import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.page.impl.DefaultFoundationPage
import com.avionos.aem.foundation.core.page.impl.DefaultFoundationPageManager
import com.avionos.aem.foundation.core.resource.impl.DefaultComponentResource
import com.day.cq.wcm.api.Page
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component

@Component(service = AdapterFactory, property = [
    "service.description=Avionos Foundation Adapter Factory",
    "adaptables=org.apache.sling.api.resource.Resource",
    "adaptables=org.apache.sling.api.resource.ResourceResolver",
    "adapters=com.avionos.aem.foundation.api.page.FoundationPageManager",
    "adapters=com.avionos.aem.foundation.api.page.FoundationPage",
    "adapters=com.avionos.aem.foundation.api.resource.ComponentResource"
])
final class FoundationAdapterFactory implements AdapterFactory {

    @Override
    <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        def result

        if (adaptable instanceof ResourceResolver) {
            result = getResourceResolverAdapter(adaptable, type)
        } else if (adaptable instanceof Resource) {
            result = getResourceAdapter(adaptable, type)
        } else {
            result = null
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

    private <AdapterType> AdapterType getResourceAdapter(Resource resource, Class<AdapterType> type) {
        def result = null

        if (type == FoundationPage) {
            def page = resource.adaptTo(Page)

            if (page) {
                result = new DefaultFoundationPage(page) as AdapterType
            }
        } else if (type == ComponentResource) {
            result = new DefaultComponentResource(resource) as AdapterType
        }

        result
    }
}
