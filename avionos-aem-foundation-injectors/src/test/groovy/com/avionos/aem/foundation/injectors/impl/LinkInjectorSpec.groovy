package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.link.Link
import com.avionos.aem.foundation.injectors.annotations.LinkInject
import com.avionos.aem.foundation.injectors.specs.SlingModelSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model

class LinkInjectorSpec extends SlingModelSpec {

    @Model(adaptables = Resource, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class Component {

        @LinkInject
        Link link1

        @LinkInject(titleProperty = "jcr:title")
        Link link2

        @LinkInject(inherit = true)
        Link link3
    }

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content" {
                    component("jcr:title": "Testing Component",
                        link1: "/content/avionos",
                        link2: "/content/avionos",
                        link3: "/content/avionos")
                }
                child {
                    "jcr:content" { component() }
                }
            }
        }
    }

    def "link is null if component node is null"() {
        setup:
        def resource = resourceResolver.resolve("/content/avionos/jcr:content/component/sub")
        def component = resource.adaptTo(Component)

        expect:
        !component.link1
    }

    def "link has correct path value"() {
        setup:
        def resource = getResource("/content/avionos/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link1.path == "/content/avionos"
    }

    def "link has correct path value and title"() {
        setup:
        def resource = getResource("/content/avionos/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link2.path == "/content/avionos"
        component.link2.title == "Testing Component"
    }

    def "inherited link has correct path value"() {
        setup:
        def resource = getResource("/content/avionos/child/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link3.path == "/content/avionos"
    }
}
