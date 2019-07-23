package com.avionos.aem.foundation.core.resource.predicates

import com.avionos.aem.foundation.core.specs.FoundationSpec
import spock.lang.Unroll

@Unroll
class ResourceTypePredicateSpec extends FoundationSpec {

    def setupSpec() {
        nodeBuilder.content {
            avionos("sling:resourceType": "page")
        }
    }

    def "resource with matching resource type is included"() {
        setup:
        def resource = resourceResolver.getResource("/content/avionos")
        def predicate = new ResourceTypePredicate(resourceType)

        expect:
        predicate.test(resource) == result

        where:
        resourceType | result
        "page"       | true
        "node"       | false
    }
}
