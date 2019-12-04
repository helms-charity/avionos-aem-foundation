package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyExistsPredicate
import groovy.transform.TupleConstructor
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class TraversableSpec extends AbstractComponentResourceSpec {

    @TupleConstructor
    static class TestPredicate implements Predicate<ComponentResource> {

        String propertyValue

        @Override
        boolean test(ComponentResource componentResource) {
            componentResource.get("jcr:title", "") == propertyValue
        }
    }

    def "find ancestor for predicate"() {
        setup:
        def componentResource = getComponentResource(path)
        def predicate = new TestPredicate(propertyValue)
        def ancestorNodeOptional = componentResource.findAncestor(predicate, excludeCurrentResource)

        expect:
        ancestorNodeOptional.present == isPresent

        where:
        path                                               | propertyName | propertyValue   | excludeCurrentResource | isPresent
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | true                   | false
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | false                  | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | true                   | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | false                  | true
    }

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
        def componentResource = getComponentResource(path)
        def ancestorNodeOptional = componentResource.findAncestorWithPropertyValue(propertyName, propertyValue)

        expect:
        ancestorNodeOptional.present == isPresent

        where:
        path                                               | propertyName | propertyValue   | isPresent
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | true
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Komponent"     | false
        "/content/inheritance/jcr:content/component"       | "number"     | Long.valueOf(5) | true
        "/content/inheritance/jcr:content/component"       | "boolean"    | false           | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Komponent"     | false
        "/content/inheritance/child/jcr:content/component" | "number"     | Long.valueOf(5) | true
        "/content/inheritance/child/jcr:content/component" | "boolean"    | false           | true
    }

    def "find ancestor with property value excluding current resource"() {
        setup:
        def componentResource = getComponentResource(path)
        def ancestorNodeOptional = componentResource.findAncestorWithPropertyValue(propertyName, propertyValue, excludeCurrentResource)

        expect:
        ancestorNodeOptional.present == isPresent

        where:
        path                                               | propertyName | propertyValue   | excludeCurrentResource | isPresent
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | true                   | false
        "/content/inheritance/jcr:content/component"       | "number"     | Long.valueOf(5) | true                   | false
        "/content/inheritance/jcr:content/component"       | "boolean"    | false           | true                   | false
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | false                  | true
        "/content/inheritance/jcr:content/component"       | "number"     | Long.valueOf(5) | false                  | true
        "/content/inheritance/jcr:content/component"       | "boolean"    | false           | false                  | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | true                   | true
        "/content/inheritance/child/jcr:content/component" | "number"     | Long.valueOf(5) | true                   | true
        "/content/inheritance/child/jcr:content/component" | "boolean"    | false           | true                   | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | false                  | true
        "/content/inheritance/child/jcr:content/component" | "number"     | Long.valueOf(5) | false                  | true
        "/content/inheritance/child/jcr:content/component" | "boolean"    | false           | false                  | true
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