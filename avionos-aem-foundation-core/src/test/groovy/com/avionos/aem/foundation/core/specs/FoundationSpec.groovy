package com.avionos.aem.foundation.core.specs

import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.adapter.FoundationAdapterFactory
import com.avionos.aem.foundation.core.page.impl.DefaultFoundationPage
import com.avionos.aem.foundation.core.resource.impl.DefaultComponentResource
import com.icfolson.aem.prosper.specs.ProsperSpec

/**
 * Spock specification for testing Avionos AEM Foundation-based components and services.
 */
abstract class FoundationSpec extends ProsperSpec {

    def setupSpec() {
        slingContext.registerAdapterFactory(new FoundationAdapterFactory())
        slingContext.addModelsForClasses(DefaultComponentResource, DefaultFoundationPage)
    }

    ComponentResource getComponentResource(String path) {
        resourceResolver.resolve(path).adaptTo(ComponentResource)
    }

    @Override
    FoundationPage getPage(String path) {
        pageManager.getPage(path)
    }

    @Override
    FoundationPageManager getPageManager() {
        resourceResolver.adaptTo(FoundationPageManager)
    }
}
