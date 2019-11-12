package com.avionos.aem.foundation.core.resource.predicates

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.resource.impl.DefaultComponentResource
import com.avionos.aem.foundation.core.specs.FoundationSpec
import org.apache.sling.api.resource.NonExistingResource

class ComponentResourcePropertyExistsPredicateSpec extends FoundationSpec {

    def setupSpec() {
        nodeBuilder.content {
            avionos("jcr:title": "Avionos")
        }
    }

    def "resource where property exists is included"() {
        setup:
        def componentResource = getComponentResource("/content/avionos")
        def predicate = new ComponentResourcePropertyExistsPredicate("jcr:title")

        expect:
        predicate.test(componentResource)
    }

    def "resource where property does not exist is not included"() {
        setup:
        def componentResource = getComponentResource("/content/avionos")
        def predicate = new ComponentResourcePropertyExistsPredicate("jcr:description")

        expect:
        !predicate.test(componentResource)
    }

    def "resource for non-existing resource is not included"() {
        setup:
        def resource = new NonExistingResource(resourceResolver, "/content/non-existing")
        def componentResource = resource.adaptTo(ComponentResource)
        def predicate = new ComponentResourcePropertyExistsPredicate("propertyName")

        expect:
        !predicate.test(componentResource)
    }
}
