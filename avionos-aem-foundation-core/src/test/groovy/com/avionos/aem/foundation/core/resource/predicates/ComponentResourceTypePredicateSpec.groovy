package com.avionos.aem.foundation.core.resource.predicates

import com.avionos.aem.foundation.core.specs.FoundationSpec

class ComponentResourceTypePredicateSpec extends FoundationSpec {

    def setupSpec() {
        nodeBuilder.sabbath("sling:resourceType": "black") {
            paranoid()
        }
    }

    def "exception thrown when resource type is null"() {
        when:
        new ComponentResourceTypePredicate(null)

        then:
        thrown(NullPointerException)
    }

    def "resource with matching resource type is included"() {
        setup:
        def componentResource = getComponentResource("/sabbath")
        def predicate = new ComponentResourceTypePredicate("black")

        expect:
        predicate.test(componentResource)
    }

    def "resource with non-matching resource type is not included"() {
        setup:
        def componentResource = getComponentResource("/sabbath")
        def predicate = new ComponentResourceTypePredicate("purple")

        expect:
        !predicate.test(componentResource)
    }

    def "resource with no resource type is not included"() {
        setup:
        def componentResource = getComponentResource("/sabbath/paranoid")
        def predicate = new ComponentResourceTypePredicate("purple")

        expect:
        !predicate.test(componentResource)
    }
}
