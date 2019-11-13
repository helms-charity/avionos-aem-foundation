package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.specs.FoundationSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class ComponentInjectorSpec extends FoundationSpec {

    @Shared
    ComponentInjector injector = new ComponentInjector()

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
                page1 {
                    "jcr:content" {
                        component()
                    }
                }
            }
        }
    }

    def "get value from resource for valid type returns non-null value"() {
        setup:
        def resource = getResource("/content/avionos/jcr:content/component")
        def value = injector.getValue(resource, null, type, null, null)

        expect:
        value

        where:
        type << [Resource, ResourceResolver, ValueMap, ComponentResource, FoundationPage, FoundationPageManager]
    }

    def "get value from resource for invalid type returns null value"() {
        setup:
        def resource = getResource("/content/avionos/jcr:content/component")
        def value = injector.getValue(resource, null, String, null, null)

        expect:
        !value
    }
}
