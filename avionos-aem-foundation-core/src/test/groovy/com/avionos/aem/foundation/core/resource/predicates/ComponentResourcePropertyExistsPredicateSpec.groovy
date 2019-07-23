package com.avionos.aem.foundation.core.resource.predicates

import com.avionos.aem.foundation.core.resource.impl.DefaultComponentResource
import com.avionos.aem.foundation.core.specs.FoundationSpec
import org.apache.sling.api.resource.NonExistingResource

class ComponentResourcePropertyExistsPredicateSpec extends FoundationSpec {

    def setupSpec() {
        nodeBuilder.content {
            citytechinc("jcr:title": "CITYTECH, Inc.")
        }
    }

    def "resource where property exists is included"() {
        setup:
        def componentResource = getComponentResource("/content/citytechinc")
        def predicate = new ComponentResourcePropertyExistsPredicate("jcr:title")

        expect:
        predicate.test(componentResource)
    }

    def "resource where property does not exist is not included"() {
        setup:
        def componentResource = getComponentResource("/content/citytechinc")
        def predicate = new ComponentResourcePropertyExistsPredicate("jcr:description")

        expect:
        !predicate.test(componentResource)
    }

    def "resource for non-existing resource is not included"() {
        setup:
        def resource = new NonExistingResource(resourceResolver, "/content/non-existing")
        def componentResource = new DefaultComponentResource(resource)
        def predicate = new ComponentResourcePropertyExistsPredicate("propertyName")

        expect:
        !predicate.test(componentResource)
    }
}
