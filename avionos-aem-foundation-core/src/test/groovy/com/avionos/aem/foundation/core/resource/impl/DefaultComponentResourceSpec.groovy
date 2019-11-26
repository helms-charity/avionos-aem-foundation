package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class DefaultComponentResourceSpec extends AbstractComponentResourceSpec {

    def "to string"() {
        setup:
        def componentResource = getComponentResource("/content/lagers/jcr:content/spaten")

        expect:
        componentResource.toString() == "DefaultComponentResource{path=/content/lagers/jcr:content/spaten, properties={sling:resourceType=de, jcr:primaryType=nt:unstructured}}"
    }

    def "get id"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.id == id

        where:
        path                                      | id
        "/content/avionos"                        | "content-avionos"
        "/content/avionos/jcr:content"            | "content-avionos"
        "/content/avionos/jcr:content/malort/one" | "malort-one"
        "/"                                       | ""
    }

    def "get index"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/jcr:content/morland")

        expect:
        componentResource.index == 2
    }

    def "get index for resource type"() {
        setup:
        def componentResource = getComponentResource("/content/lagers/jcr:content/stiegl")

        expect:
        componentResource.getIndex("de") == 0
    }

    def "get path"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.path == "/content/avionos/jcr:content"
    }

    def "get resource"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.resource.path == "/content/avionos/jcr:content"
    }

    def "get component resource at relative path"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getComponentResource("whiskey").present
        !componentResource.getComponentResource("vodka").present
    }

    def "get component resources"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.componentResources.size() == size

        where:
        path                                | size
        "/content/avionos/jcr:content"      | 8
        "/content/avionos/jcr:content/beer" | 0
    }

    def "get component resources for predicate"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content/malort")
        def predicate = new Predicate<ComponentResource>() {
            @Override
            boolean test(ComponentResource input) {
                input.resource.resourceType == "tew"
            }
        }

        expect:
        componentResource.getComponentResources(predicate).size() == 1
    }

    def "get component resources at relative path"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getComponentResources(relativePath).size() == size

        where:
        relativePath | size
        "whiskey"    | 0
        "malort"     | 2
    }

    def "get component resources at relative path for resource type"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getComponentResources("malort", resourceType).size() == size

        where:
        resourceType   | size
        "non-existent" | 0
        "tew"          | 1
    }

    def "get component resources at relative path for predicate"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/lace/jcr:content")
        def predicate = new Predicate<ComponentResource>() {
            @Override
            boolean test(ComponentResource input) {
                input.resource.resourceType == "unknown"
            }
        }

        expect:
        componentResource.getComponentResources("parent", predicate).size() == 2
    }

    def "get parent"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.parent.get().path == parentPath

        where:
        path                           | parentPath
        "/content/avionos/jcr:content" | "/content/avionos"
        "/content/avionos"             | "/content"
    }

    def "get parent returns null for root resource"() {
        setup:
        def componentResource = getComponentResource("/")

        expect:
        !componentResource.parent.present
    }
}