package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.day.cq.dam.api.Asset
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.api.resource.Resource
import org.apache.sling.testing.mock.sling.ResourceResolverType
import spock.lang.Unroll

@Unroll
class AccessibleSpec extends AbstractComponentResourceSpec {

    @Override
    AemContext getAemContext() {
        new AemContextBuilder(ResourceResolverType.JCR_OAK)
            .resourceResolverFactoryActivatorProps(["resource.resolver.mapping": ["/content/:/", "/-/"] as String[]])
            .build()
    }

    def "as map"() {
        setup:
        def map = getComponentResource("/content/avionos/jcr:content").asMap()

        expect:
        map["jcr:title"] == "Avionos"
        map["otherPagePath"] == "/content/ales/esb"
    }

    def "get"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.get(propertyName, defaultValue) == result

        where:
        propertyName          | defaultValue | result
        "otherPagePath"       | ""           | "/content/ales/esb"
        "nonExistentProperty" | ""           | ""
    }

    def "get optional"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.get(propertyName, type).present == result

        where:
        propertyName          | type    | result
        "otherPagePath"       | String  | true
        "otherPagePath"       | Integer | false
        "nonExistentProperty" | String  | false
    }

    def "get as list"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getAsList("multiValue", String) == ["one", "two"]
    }

    def "get as href"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getAsHref(propertyName).get() == href

        where:
        propertyName    | href
        "otherPagePath" | "/content/ales/esb.html"
        "externalPath"  | "http://www.reddit.com"
    }

    def "get as href strict"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getAsHref(propertyName, true).get() == href

        where:
        propertyName          | href
        "otherPagePath"       | "/content/ales/esb.html"
        "nonExistentPagePath" | "/content/home"
        "externalPath"        | "http://www.reddit.com"
    }

    def "get as href returns absent where appropriate"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        !componentResource.getAsHref(propertyName).present

        where:
        propertyName << ["beer", ""]
    }

    def "get as mapped href"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getAsHref("otherPagePath", false, true).get() == "/ales/esb.html"
    }

    def "get as mapped href strict"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getAsHref(propertyName, true, true).get() == href

        where:
        propertyName          | href
        "otherPagePath"       | "/ales/esb.html"
        "nonExistentPagePath" | "/home"
        "externalPath"        | "http://www.reddit.com"
    }

    def "get as href for null"() {
        when:
        getComponentResource("/content/avionos/jcr:content").getAsHref(null)

        then:
        thrown NullPointerException
    }

    def "get as link"() {
        setup:
        def link = getComponentResource("/content/avionos/jcr:content").getAsLink("otherPagePath").get()

        expect:
        link.path == "/content/ales/esb"
    }

    def "get as link strict"() {
        setup:
        def link = getComponentResource("/content/avionos/jcr:content").getAsLink("nonExistentPagePath", true).get()

        expect:
        link.path == "/content/home"
        link.external
        link.extension == ""
    }

    def "get as mapped link"() {
        setup:
        def link = getComponentResource("/content/avionos/jcr:content").getAsLink("otherPagePath", false, true).get()

        expect:
        link.path == "/content/ales/esb"
        link.href == "/ales/esb.html"
    }

    def "get as mapped link strict"() {
        setup:
        def link = getComponentResource("/content/avionos/jcr:content").getAsLink("nonExistentPagePath", true,
            true).get()

        expect:
        link.path == "/home"
        link.external
        link.extension == ""
    }

    def "get as link for null"() {
        when:
        getComponentResource("/content/avionos/jcr:content").getAsLink(null)

        then:
        thrown NullPointerException
    }

    def "get as link for non-existent property"() {
        setup:
        def linkOptional = getComponentResource("/content/avionos/jcr:content").getAsLink("beer")

        expect:
        !linkOptional.present
    }

    def "get as page"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getAsPage("otherPagePath").get().path == "/content/ales/esb"
        !componentResource.getAsPage("nonExistentProperty").present
    }

    def "get as page list"() {
        setup:
        def componentResource = getComponentResource("/content/ales/esb/jcr:content")

        expect:
        componentResource.getAsPageList(propertyName).size() == size

        where:
        propertyName          | size
        "pagePaths"           | 2
        "nonExistentProperty" | 0
    }

    def "get as resource"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getAsResource("related").present == result

        where:
        path                                 | result
        "/content/lagers/jcr:content/dynamo" | true
        "/content/lagers/jcr:content"        | false
    }

    def "get as resource list"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getAsResourceList("related").size() == size

        where:
        path                                 | size
        "/content/lagers/jcr:content/stiegl" | 2
        "/content/lagers/jcr:content/spaten" | 0
    }

    def "get as type"() {
        setup:
        def componentResource = getComponentResource("/content/lagers/jcr:content/dynamo")

        expect:
        componentResource.getAsType("related", type).present == result

        where:
        type              | result
        Resource          | true
        ComponentResource | true
        Asset             | false
    }

    def "get as type list"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getAsTypeList("related", type).size() == size

        where:
        path                                 | type              | size
        "/content/lagers/jcr:content/stiegl" | ComponentResource | 2
        "/content/lagers/jcr:content/stiegl" | Asset             | 0
        "/content/lagers/jcr:content/spaten" | ComponentResource | 0
        "/content/lagers/jcr:content/spaten" | Asset             | 0
    }

    def "get image reference"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.imageReference.present == isPresent

        where:
        path                            | isPresent
        "/content/avionos/jcr:content"  | true
        "/content/ales/esb/jcr:content" | false
    }

    def "get self image reference"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getImageReference(isSelf).present == isPresent

        where:
        path                                       | isSelf | isPresent
        "/content/avionos/jcr:content"             | false  | true
        "/content/avionos/jcr:content"             | true   | true
        "/content/ales/esb/jcr:content"            | false  | false
        "/content/ales/esb/jcr:content"            | true   | false
        "/content/ales/esb/jcr:content/greeneking" | true   | false
        "/content/ales/esb/jcr:content/greeneking" | false  | true
    }

    def "get named image reference"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getImageReference("nsfwImage").get() == "omg.png"

        and:
        !componentResource.getImageReference("sfwImage").present
    }

    def "has image"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.hasImage == hasImage

        where:
        path                                       | hasImage
        "/content/avionos/jcr:content"             | true
        "/content/ales/esb/jcr:content"            | false
        "/content/ales/esb/jcr:content/greeneking" | true
    }

    def "has named image"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.isHasImage(name) == hasImage

        where:
        path                            | name          | hasImage
        "/content/avionos/jcr:content"  | "image"       | true
        "/content/avionos/jcr:content"  | "secondimage" | true
        "/content/avionos/jcr:content"  | "thirdimage"  | false
        "/content/avionos/jcr:content"  | "fourthimage" | false
        "/content/ales/esb/jcr:content" | "image"       | false
    }

    def "get image rendition returns absent"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        !componentResource.getImageRendition("sfwImage", "").present
    }

    def "get image rendition"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        !componentResource.getImageRendition("").present
    }

    def "get named image rendition"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getImageRendition(name, renditionName).present == result

        where:
        name                  | renditionName | result
        "secondimage"         | ""            | false
        "imageWithRenditions" | "one"         | true
        "imageWithRenditions" | "four"        | false
    }

    def "get tags"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getTags("tags").size() == size

        where:
        path                                  | size
        "/content/avionos/jcr:content"        | 2
        "/content/avionos/jcr:content/malort" | 0
    }
}