package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyExistsPredicate
import spock.lang.Unroll

@Unroll
class TraversableSpec extends AbstractComponentResourceSpec {

    def "find ancestor with property"() {
        setup:
        def componentResource = getComponentResource(path)
        def ancestorNodeOptional = componentResource.findAncestorWithProperty("jcr:title", excludeCurrentResource)

        expect:
        ancestorNodeOptional.get().path == ancestorPath

        where:
        path                                               | excludeCurrentResource | ancestorPath
        "/content/inheritance/jcr:content"                 | false                  | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content"           | false                  | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content/component" | false                  | "/content/inheritance/jcr:content/component"
        "/content/inheritance/child/jcr:content"           | true                   | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content/component" | true                   | "/content/inheritance/jcr:content/component"
    }

    def "find ancestor with property returns absent when current resource excluded"() {
        setup:
        def componentResource = getComponentResource("/content/inheritance/jcr:content")
        def ancestorNodeOptional = componentResource.findAncestorWithProperty("jcr:title", true)

        expect:
        !ancestorNodeOptional.present
    }

    def "find ancestor returns absent"() {
        setup:
        def componentResource = getComponentResource(path)
        def ancestorNodeOptional = componentResource.findAncestorWithProperty("jcr:description")

        expect:
        !ancestorNodeOptional.present

        where:
        path << [
            "/content/inheritance/child/jcr:content",
            "/content/inheritance/child/jcr:content/component"
        ]
    }

    def "find ancestor with property value"() {
        setup:
        def componentResource = getComponentResource("/content/inheritance/child/jcr:content/component")
        def ancestorNodeOptional = componentResource.findAncestorWithPropertyValue(propertyName, propertyValue)

        expect:
        ancestorNodeOptional.get().path == "/content/inheritance/jcr:content/component"

        where:
        propertyName | propertyValue
        "jcr:title"  | "Component"
        "number"     | Long.valueOf(5)
        "boolean"    | false
    }

    def "find ancestor with property value returns absent"() {
        setup:
        def componentResource = getComponentResource("/content/inheritance/child/jcr:content/component")
        def ancestorNodeOptional = componentResource.findAncestorWithPropertyValue("jcr:title", "Komponent")

        expect:
        !ancestorNodeOptional.present
    }

    def "find descendants"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")
        def predicate = new ComponentResourcePropertyExistsPredicate("sling:resourceType")

        expect:
        componentResource.findDescendants(predicate).size() == 3
    }
}