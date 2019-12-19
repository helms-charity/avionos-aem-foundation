package com.avionos.aem.foundation.core.resource.impl

import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.testing.mock.sling.ResourceResolverType
import spock.lang.Unroll

@Unroll
class LinkableSpec extends AbstractComponentResourceSpec {

    @Override
    AemContext getAemContext() {
        new AemContextBuilder(ResourceResolverType.JCR_OAK)
            .resourceResolverFactoryActivatorProps(["resource.resolver.mapping": ["/content/:/", "/-/"] as String[]])
            .build()
    }

    def "get href"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.href == "/content/avionos/jcr:content.html"
    }

    def "get mapped href"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getHref(mapped) == href

        where:
        mapped | href
        true   | "/avionos/_jcr_content.html"
        false  | "/content/avionos/jcr:content.html"
    }

    def "get link"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.link.path == "/content/avionos/jcr:content"
    }

    def "get mapped link"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getLink(mapped).path == path

        where:
        mapped | path
        true   | "/content/avionos/jcr:content"
        false  | "/content/avionos/jcr:content"
    }

    def "get link builder"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.linkBuilder.build().path == "/content/avionos/jcr:content"
    }
}