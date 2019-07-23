package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyExistsPredicate
import com.avionos.aem.foundation.core.specs.FoundationSpec
import com.day.cq.dam.api.Asset
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class DefaultComponentResourceSpec extends FoundationSpec {

    def setupSpec() {
        pageBuilder.content {
            avionos("Avionos") {
                "jcr:content"(otherPagePath: "/content/ales/esb", nonExistentPagePath: "/content/home",
                    externalPath: "http://www.reddit.com", multiValue: ["one", "two"],
                    fileReference: "/content/dam/image") {
                    image(fileReference: "/content/dam/image")
                    secondimage(fileReference: "/content/dam/image")
                    thirdimage()
                    nsfwImage(fileReference: "omg.png")
                    imageWithRenditions(fileReference: "/content/dam/image-renditions")
                    beer(label: "orval", abv: "9.0", oz: "12")
                    whiskey("sling:resourceType": "rye")
                    malort {
                        one("sling:resourceType": "won")
                        two("sling:resourceType": "tew")
                    }
                }
            }
            ales {
                esb("ESB") {
                    "jcr:content"(otherPagePath: "/content/avionos", externalPath: "http://www.reddit.com") {
                        // image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                        fullers("sling:resourceType": "bitter")
                        morland("sling:resourceType": "bitter")
                        greeneking("sling:resourceType": "bitter") {
                            image(fileReference: "/content/dam/image")
                        }
                    }
                    suds {
                        "jcr:content"(otherPagePath: "") {
                            container {
                                child1("jcr:title": "Zeus")
                                child2()
                            }
                        }
                        pint {
                            keg { "jcr:content" { container() } }
                            barrel {
                                "jcr:content" { container { child1() } }
                            }
                        }
                    }
                    bar {
                        "jcr:content" {
                            wood {
                                container {
                                    pine()
                                    spruce()
                                    maple()
                                }
                            }
                        }
                        tree { "jcr:content" { wood() } }
                    }
                    lace {
                        "jcr:content"() {
                            parent {
                                child1("sling:resourceType": "unknown")
                                child2("sling:resourceType": "unknown")
                                child3("sling:resourceType": "known")
                            }
                        }
                    }
                }
            }
            lagers {
                "jcr:content"(otherPagePath: "/content/avionos") {
                    dynamo("sling:resourceType": "us", related: "/content/lagers/jcr:content/spaten")
                    stiegl("sling:resourceType": "de")
                    spaten("sling:resourceType": "de")
                }
            }
            inheritance {
                "jcr:content"("jcr:title": "Inheritance") {
                    component("jcr:title": "Component", "number": 5, "boolean": false) {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                        insidecomponent(fileReference: "/content/dam/image")
                    }
                }
                child {
                    "jcr:content" {
                        component() { insidecomponent() }
                        other()
                    }
                }
            }
        }

        nodeBuilder.content {
            dam("sling:Folder") {
                image("dam:Asset") {
                    "jcr:content"("jcr:data": "data") {
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
                "image-renditions"("dam:Asset") {
                    "jcr:content"("jcr:data": "data") {
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                            one("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
            }
        }
    }

    def "to string"() {
        setup:
        def componentResource = getComponentResource("/content/lagers/jcr:content/stiegl")

        expect:
        componentResource.toString() == "DefaultComponentResource{path=/content/lagers/jcr:content/stiegl, " +
            "properties={sling:resourceType=de, jcr:primaryType=nt:unstructured}}"
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

    def "get as type"() {
        setup:
        def componentResource = getComponentResource("/content/lagers/jcr:content/dynamo")

        expect:
        componentResource.getAsType("related", type).present == result

        where:
        type              | result
        ComponentResource | true
        Asset             | false
    }

    def "get href"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.href == "/content/avionos/jcr:content.html"
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

    def "get named image reference"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.getImageReference("nsfwImage").get() == "omg.png"
        !componentResource.getImageReference("sfwImage").present
    }

    def "get link"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.link.path == "/content/avionos/jcr:content"
    }

    def "get link builder"() {
        setup:
        def componentResource = getComponentResource("/content/avionos/jcr:content")

        expect:
        componentResource.linkBuilder.build().path == "/content/avionos/jcr:content"
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

    def "has image"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.hasImage == hasImage

        where:
        path                            | hasImage
        "/content/avionos/jcr:content"  | true
        "/content/ales/esb/jcr:content" | false
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
        "/content/inheritance/child/jcr:content/component" | false                  |
            "/content/inheritance/jcr:content/component"
        "/content/inheritance/child/jcr:content"           | true                   | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content/component" | true                   |
            "/content/inheritance/jcr:content/component"
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
        !componentResource.getAsPageInherited("nonExistentPagePath").present
    }

    def "get component resource inherited"() {
        setup:
        def componentResource = getComponentResource(path)

        expect:
        componentResource.getComponentResourceInherited("child1").get().path == inheritedNodePath

        where:
        path                                                       | inheritedNodePath
        "/content/ales/esb/suds/pint/keg/jcr:content/container"    |
            "/content/ales/esb/suds/jcr:content/container/child1"
        "/content/ales/esb/suds/pint/barrel/jcr:content/container" |
            "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1"
    }

    def "get component resource inherited is absent when ancestor not found"() {
        expect:
        !getComponentResource("/content/ales/esb/jcr:content").getComponentResourceInherited("child1").present
    }

    def "get component resources inherited"() {
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
}