package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.injectors.specs.SlingModelSpec
import spock.lang.Unroll

@Unroll
class FoundationModelComponentSpec extends SlingModelSpec {

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "get title from component"() {
        setup:
        def component = getResource("/content/avionos/jcr:content/component").adaptTo(FoundationModelComponent)

        expect:
        component.title == "Testing Component"
    }
}
