package com.avionos.aem.foundation.core.adapter;

import com.avionos.aem.foundation.api.page.FoundationPage;
import com.avionos.aem.foundation.api.page.FoundationPageManager;
import com.avionos.aem.foundation.core.page.impl.DefaultFoundationPageManager;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

@Component(service = AdapterFactory.class, property = {
    "adaptables=org.apache.sling.api.resource.Resource",
    "adaptables=org.apache.sling.api.resource.ResourceResolver",
    "adapters=com.avionos.aem.foundation.api.page.FoundationPageManager",
    "adapters=com.avionos.aem.foundation.api.page.FoundationPage"
})
@ServiceDescription("Avionos Foundation Adapter Factory")
public final class FoundationAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(final Object adaptable, final Class<AdapterType> type) {
        AdapterType result = null;

        if (adaptable instanceof ResourceResolver) {
            result = getResourceResolverAdapter((ResourceResolver) adaptable, type);
        } else if (adaptable instanceof Resource) {
            result = getResourceAdapter((Resource) adaptable, type);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getResourceResolverAdapter(final ResourceResolver resourceResolver,
        final Class<AdapterType> type) {
        AdapterType result = null;

        if (type == FoundationPageManager.class) {
            result = (AdapterType) new DefaultFoundationPageManager(resourceResolver);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getResourceAdapter(final Resource resource, final Class<AdapterType> type) {
        AdapterType result = null;

        if (type == FoundationPage.class) {
            final Page page = resource.adaptTo(Page.class);

            if (page != null) {
                result = (AdapterType) page.adaptTo(FoundationPage.class);
            }
        }

        return result;
    }
}
