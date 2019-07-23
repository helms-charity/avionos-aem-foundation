package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.specs.FoundationSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

import static org.osgi.framework.Constants.SERVICE_RANKING

class AdaptableInjectorSpec extends FoundationSpec {

    @Model(adaptables = SlingHttpServletRequest, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class AdaptableModel {

        @Inject
        FoundationPageManager pageManager

        @Inject
        ComponentResource componentResource
    }

    def setupSpec() {
        slingContext.registerInjectActivateService(new ResourceResolverAdaptableInjector(),
            [(SERVICE_RANKING): Integer.MIN_VALUE])
        slingContext.addModelsForPackage(this.class.package.name)
    }

    def "get value returns null for invalid adapter type"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(AdaptableModel)

        expect:
        !model.componentResource
    }

    def "get value returns non-null for valid adapter type"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(AdaptableModel)

        expect:
        model.pageManager
    }
}
