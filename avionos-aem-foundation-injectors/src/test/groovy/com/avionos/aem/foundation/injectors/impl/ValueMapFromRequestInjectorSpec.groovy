package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.injectors.specs.SlingModelSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.Optional

import javax.inject.Inject

class ValueMapFromRequestInjectorSpec extends SlingModelSpec {

    @Model(adaptables = SlingHttpServletRequest)
    static class ValueMapInjectorModel {

        @Inject
        String name

        @Inject
        @Optional
        String title
    }

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content" {
                    component(name: "Avionos AEM Foundation")
                }
            }
        }

        slingContext.registerInjectActivateService(new ValueMapFromRequestInjector())
    }

    def "inject values for component"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/avionos/jcr:content/component"
        }

        def model = request.adaptTo(ValueMapInjectorModel)

        expect:
        model.name == "Avionos AEM Foundation"

        and:
        !model.title
    }
}
