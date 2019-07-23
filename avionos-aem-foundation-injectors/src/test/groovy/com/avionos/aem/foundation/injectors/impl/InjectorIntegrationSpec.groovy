package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.injectors.specs.SlingModelSpec
import com.day.cq.tagging.TagManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

class InjectorIntegrationSpec extends SlingModelSpec {

    @Model(adaptables = SlingHttpServletRequest)
    static class InjectorIntegrationComponent {

        @Inject
        FoundationPageManager pageManager

        @Inject
        TagManager tagManager
    }

    def "injected values from multiple injectors are correct types"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(InjectorIntegrationComponent)

        expect:
        model.pageManager instanceof FoundationPageManager

        and:
        model.tagManager instanceof TagManager
    }
}
