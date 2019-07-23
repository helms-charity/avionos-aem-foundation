package com.avionos.aem.foundation.core.resource

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.specs.FoundationSpec

class DelegateComponentResourceSpec extends FoundationSpec {

    class TestComponentResource extends DelegateComponentResource {

        TestComponentResource(ComponentResource componentResource) {
            super(componentResource)
        }

        def getTitle() {
            get("jcr:title", "")
        }
    }

    def setupSpec() {
        pageBuilder.content {
            home("Home")
        }
    }

    def "delegate"() {
        setup:
        def node = getComponentResource("/content/home/jcr:content")
        def delegatingComponentResource = new TestComponentResource(node)

        expect:
        delegatingComponentResource.title == "Home"
    }
}
