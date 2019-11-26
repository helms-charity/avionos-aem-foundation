package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.day.cq.dam.api.Asset
import org.apache.sling.api.resource.Resource
import spock.lang.Unroll

@Unroll
class InheritableSpec extends AbstractComponentResourceSpec {

    def "get as resource inherited"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/lace/jcr:content")

        expect:
        componentResource.getAsResourceInherited("otherPagePath").present

        and:
        !componentResource.getAsResourceInherited("nonExistentProperty").present
    }

    def "get as resource list inherited"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/lace/jcr:content")

        expect:
        componentResource.getAsResourceListInherited("pagePaths").size() == 2

        and:
        componentResource.getAsResourceListInherited("nonExistentPagePath").empty

        where:
        path                                 | size
        "/content/ales/esb/lace/jcr:content" | 2
        "/content/ales/esb/jcr:content"      | 2
        "/content/ales/jcr:content"          | 0
    }

    def "get as type inherited"() {
        setup:
        def componentResource = getComponentResource("/content/lagers/jcr:content/dynamo")

        expect:
        componentResource.getAsTypeInherited("related", type).present == result

        where:
        type              | result
        Resource          | true
        ComponentResource | true
        Asset             | false
    }

    def "get as type list inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getAsTypeListInherited("related", type).size() == size

        where:
        path                                 | type              | size
        "/content/lagers/jcr:content/stiegl" | ComponentResource | 2
        "/content/lagers/jcr:content/stiegl" | Asset             | 0
    }

    def "get as href inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getAsHrefInherited(propertyName).get() == href

        where:
        path                                 | propertyName    | href
        "/content/ales/esb/jcr:content"      | "otherPagePath" | "/content/avionos.html"
        "/content/ales/esb/suds/jcr:content" | "otherPagePath" | ""
        "/content/ales/esb/lace/jcr:content" | "otherPagePath" | "/content/avionos.html"
        "/content/ales/esb/jcr:content"      | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/suds/jcr:content" | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/lace/jcr:content" | "externalPath"  | "http://www.reddit.com"
    }

    def "get as href inherited returns absent where appropriate"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        !componentResource.getAsHrefInherited(propertyName).present

        where:
        path                                 | propertyName
        "/content/ales/esb/jcr:content"      | "nonExistentPath"
        "/content/ales/esb/suds/jcr:content" | "nonExistentPath"
    }

    def "get as link inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getAsLinkInherited(propertyName).get().href == href

        where:
        path                                 | propertyName    | href
        "/content/ales/esb/jcr:content"      | "otherPagePath" | "/content/avionos.html"
        "/content/ales/esb/suds/jcr:content" | "otherPagePath" | ""
        "/content/ales/esb/lace/jcr:content" | "otherPagePath" | "/content/avionos.html"
        "/content/ales/esb/jcr:content"      | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/suds/jcr:content" | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/lace/jcr:content" | "externalPath"  | "http://www.reddit.com"
    }

    def "get as link inherited returns absent where appropriate"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        !componentResource.getAsLinkInherited("nonExistentPath").present

        where:
        path                                 | propertyName
        "/content/ales/esb/jcr:content"      | "nonExistentPath"
        "/content/ales/esb/suds/jcr:content" | "nonExistentPath"
    }

    def "get as page inherited"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/lace/jcr:content")

        expect:
        componentResource.getAsPageInherited("otherPagePath").get().path == "/content/avionos"

        and:
        !componentResource.getAsPageInherited("nonExistentPagePath").present
    }

    def "get as page list inherited"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/lace/jcr:content")

        expect:
        componentResource.getAsPageListInherited("pagePaths").size() == 2

        and:
        componentResource.getAsPageListInherited("nonExistentPagePath").empty

        where:
        path                                 | size
        "/content/ales/esb/lace/jcr:content" | 2
        "/content/ales/esb/jcr:content"      | 2
        "/content/ales/jcr:content"          | 0
    }

    def "get component resource inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getComponentResourceInherited("child1").get().path == inheritedNodePath

        where:
        path                                                       | inheritedNodePath
        "/content/ales/esb/suds/pint/keg/jcr:content/container"    | "/content/ales/esb/suds/jcr:content/container/child1"
        "/content/ales/esb/suds/pint/barrel/jcr:content/container" | "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1"
    }

    def "get component resource inherited is absent when ancestor not found"() {
        expect:
        !getComponentResource("/content/ales/esb/jcr:content").getComponentResourceInherited("child1").present
    }

    def "get component resources inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.componentResourcesInherited.size() == size

        where:
        path                                                       | size
        "/content/ales/esb/suds/pint/jcr:content/container"        | 2
        "/content/ales/esb/suds/pint/keg/jcr:content/container"    | 2
        "/content/ales/esb/suds/pint/barrel/jcr:content/container" | 1
        "/content/ales/esb/bar/tree/jcr:content/wood/container"    | 3
    }

    def "get component resources inherited for relative path"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getComponentResourcesInherited("container").size() == size

        where:
        path                                             | size
        "/content/ales/esb/suds/pint/jcr:content"        | 2
        "/content/ales/esb/suds/pint/keg/jcr:content"    | 0
        "/content/ales/esb/suds/pint/barrel/jcr:content" | 1
        "/content/ales/esb/bar/tree/jcr:content/wood"    | 3
    }

    def "get inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getInherited(propertyName, "") == propertyValue

        where:
        path                                                              | propertyName  | propertyValue
        "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1" | "jcr:title"   | "Zeus"
        "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1" | "nonExistent" | ""
        "/content/ales/esb/jcr:content/fullers"                           | "any"         | ""
    }

    def "get inherited optional"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/lace/jcr:content")

        expect:
        componentResource.getInherited("otherPagePath", String).present
        !componentResource.getInherited("nonExistentProperty", String).present
    }

    def "get tags inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getTagsInherited("tags").size() == size

        where:
        path                                  | size
        "/content/ales/esb/jcr:content"       | 1
        "/content/ales/esb/suds/jcr:content"  | 1
        "/content/avionos/jcr:content/malort" | 0
    }
}