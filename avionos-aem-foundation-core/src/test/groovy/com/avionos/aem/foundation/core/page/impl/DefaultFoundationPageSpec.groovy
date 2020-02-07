package com.avionos.aem.foundation.core.page.impl

import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.enums.TitleType
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.page.predicates.TemplatePredicate
import com.avionos.aem.foundation.core.specs.FoundationSpec
import com.day.cq.wcm.api.NameConstants
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class DefaultFoundationPageSpec extends FoundationSpec {

    private static final Predicate<FoundationPage> TRUE = new Predicate<FoundationPage>() {
        @Override
        boolean test(FoundationPage pageDecorator) {
            true
        }
    }

    def setupSpec() {
        pageBuilder.content {
            avionos("Avionos") {
                "jcr:content"(otherPagePath: "/content/ales/esb", pageTitle: "Page Title",
                    navTitle: "Navigation Title") {
                    component {
                        one("sling:resourceType": "one")
                        two("sling:resourceType": "two")
                    }
                }
                child1 {
                    "jcr:content"(hideInNav: true, "cq:template": "template")
                    grandchild()
                }
                child2 {
                    "jcr:content"(pageTitle: "Child 2", "jcr:title": "Also Child 2") {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                    }
                }
            }
            other()
            inheritance {
                "jcr:content"("jcr:title": "Inheritance") {
                    component("jcr:title": "Component", "number": 5, "boolean": false) {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                    }
                }
                child {
                    "jcr:content" {
                        component()
                        other()
                    }
                    sub()
                }
            }
            pageTitles {
                "jcr:content"("jcr:title": "Titles")
                noPageNoNavigationTitle("jcr:title": "Simple Title")
                pageTitleNoNavigationTitle("jcr:title": "Simple Title", pageTitle: "Page Title")
                pageTitleNavigationTitle("jcr:title": "Simple Title", pageTitle: "Page Title", navTitle: "Navigation Title")
            }
        }

        nodeBuilder.content {
            avionos {
                empty(NameConstants.NT_PAGE)
            }
            dam("sling:Folder") {
                image("dam:Asset") {
                    "jcr:content" {
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
            }
        }
    }

    def "get link"() {
        setup:
        def link = getPage("/content/avionos").link

        expect:
        link.title == "Avionos"
        link.href == "/content/avionos.html"
    }

    def "get link for title type"() {
        setup:
        def link = getPage(path).getLink(TitleType.NAVIGATION_TITLE)

        expect:
        link.title == title

        where:
        path                   | title
        "/content/avionos"     | "Navigation Title"
        "/content/inheritance" | "Inheritance"
    }

    def "get navigation link"() {
        setup:
        def link = getPage("/content/avionos").getNavigationLink(active)

        expect:
        link.active == active

        where:
        active << [true, false]
    }

    def "get href"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.href == "/content/avionos.html"
    }

    def "get absolute parent"() {
        setup:
        def page = getPage("/content/avionos/child1")

        expect:
        page.getAbsoluteParent(level).path == absoluteParentPath

        where:
        level | absoluteParentPath
        0     | "/content"
        1     | "/content/avionos"
        2     | "/content/avionos/child1"
    }

    def "get parent"() {
        setup:
        def page = getPage("/content/avionos/child1")

        expect:
        page.getParent(level).path == parentPath

        where:
        level | parentPath
        2     | "/content"
        1     | "/content/avionos"
        0     | "/content/avionos/child1"
    }

    def "adapt to"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.adaptTo(ComponentResource).path == "/content/avionos/jcr:content"
    }

    def "adapt to returns null"() {
        setup:
        def page = getPage("/content/avionos/empty")

        expect:
        !page.adaptTo(ComponentResource)
    }

    def "as map"() {
        setup:
        def page = getPage(path)

        expect:
        page.asMap().containsKey(propertyName) == result

        where:
        path                     | propertyName          | result
        "/content/avionos"       | "otherPagePath"       | true
        "/content/avionos"       | "nonExistentProperty" | false
        "/content/avionos/empty" | "otherPagePath"       | false
    }

    def "get"() {
        setup:
        def page = getPage(path)

        expect:
        page.get(propertyName, defaultValue) == result

        where:
        path                     | propertyName          | defaultValue | result
        "/content/avionos"       | "otherPagePath"       | ""           | "/content/ales/esb"
        "/content/avionos"       | "nonExistentProperty" | ""           | ""
        "/content/avionos/empty" | "otherPagePath"       | "/content"   | "/content"
    }

    def "get optional"() {
        setup:
        def page = getPage(path)

        expect:
        page.get(propertyName, String).present == result

        where:
        path                     | propertyName          | result
        "/content/avionos"       | "otherPagePath"       | true
        "/content/avionos"       | "nonExistentProperty" | false
        "/content/avionos/empty" | "otherPagePath"       | false
    }

    def "get as href"() {
        setup:
        def page = getPage(path)

        expect:
        page.getAsHref(propertyName).present == result

        where:
        path                     | propertyName          | result
        "/content/avionos"       | "otherPagePath"       | true
        "/content/avionos"       | "nonExistentProperty" | false
        "/content/avionos/empty" | "otherPagePath"       | false
    }

    def "get as href strict"() {
        setup:
        def page = getPage(path)

        expect:
        page.getAsHref(propertyName, true).present == result

        where:
        path                     | propertyName          | result
        "/content/avionos"       | "otherPagePath"       | true
        "/content/avionos"       | "nonExistentProperty" | false
        "/content/avionos/empty" | "otherPagePath"       | false
    }

    def "get as mapped href"() {
        setup:
        def page = getPage(path)

        expect:
        page.getAsHref(propertyName, false, true).present == result

        where:
        path                     | propertyName          | result
        "/content/avionos"       | "otherPagePath"       | true
        "/content/avionos"       | "nonExistentProperty" | false
        "/content/avionos/empty" | "otherPagePath"       | false
    }

    def "get as mapped href strict"() {
        setup:
        def page = getPage(path)

        expect:
        page.getAsHref(propertyName, true, true).present == result

        where:
        path                     | propertyName          | result
        "/content/avionos"       | "otherPagePath"       | true
        "/content/avionos"       | "nonExistentProperty" | false
        "/content/avionos/empty" | "otherPagePath"       | false
    }

    def "find ancestor optional"() {
        setup:
        def page = getPage(path)
        def predicate = new Predicate<FoundationPage>() {
            @Override
            boolean test(FoundationPage input) {
                input.title == "Avionos"
            }
        }

        expect:
        page.findAncestor(predicate, excludeCurrentResource).present == isPresent

        where:
        path                      | excludeCurrentResource | isPresent
        "/content/avionos"        | false                  | true
        "/content/avionos/child1" | false                  | true
        "/content/other"          | false                  | false
        "/content/avionos"        | true                   | false
        "/content/avionos/child1" | true                   | true
        "/content/other"          | true                   | false
    }

    def "find ancestor with property"() {
        setup:
        def page = getPage(path)
        def ancestorPageOptional = page.findAncestorWithProperty("jcr:title", excludeCurrentResource)

        expect:
        ancestorPageOptional.get().path == ancestorPath

        where:
        path                             | excludeCurrentResource | ancestorPath
        "/content/inheritance"           | false                  | "/content/inheritance"
        "/content/inheritance/child"     | false                  | "/content/inheritance"
        "/content/inheritance/child/sub" | false                  | "/content/inheritance"
        "/content/inheritance/child"     | true                   | "/content/inheritance"
        "/content/inheritance/child/sub" | true                   | "/content/inheritance"
    }

    def "find ancestor returns absent"() {
        setup:
        def page = getPage(path)
        def ancestorPageOptional = page.findAncestorWithProperty("jcr:description", excludeCurrentResource)

        expect:
        !ancestorPageOptional.present

        where:
        path                             | excludeCurrentResource
        "/content/inheritance"           | false
        "/content/inheritance/child"     | false
        "/content/inheritance/child/sub" | false
        "/content/inheritance"           | true
        "/content/inheritance/child"     | true
        "/content/inheritance/child/sub" | true
    }

    def "find ancestor with property value"() {
        setup:
        def page = getPage("/content/inheritance/child/sub")

        expect:
        page.findAncestorWithPropertyValue("jcr:title", "Inheritance").get().path == "/content/inheritance"

        and:
        page.findAncestorWithPropertyValue("jcr:title", "Inheritance", true).get().path == "/content/inheritance"
    }

    def "find ancestor with property value returns absent"() {
        setup:
        def page = getPage("/content/inheritance/child/sub")
        def ancestorPageOptional = page.findAncestorWithPropertyValue("jcr:title", "Foo")

        expect:
        !ancestorPageOptional.present
    }

    def "get template path"() {
        setup:
        def page = getPage(path)

        expect:
        page.templatePath == templatePath

        where:
        path                      | templatePath
        "/content/avionos"        | null
        "/content/avionos/child1" | "template"
    }

    def "get component resource"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.componentResource.get().path == "/content/avionos/jcr:content"
    }

    def "get component resource returns absent optional for page with no jcr:content node"() {
        setup:
        def page = getPage("/content/avionos/empty")

        expect:
        !page.componentResource.present
    }

    def "get component resource at relative path"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.getComponentResource("component/one").get().path == "/content/avionos/jcr:content/component/one"
    }

    def "adapt to component resource"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.adaptTo(ComponentResource).path == "/content/avionos/jcr:content"
    }

    def "adapt to resource for page with no jcr:content node returns null"() {
        setup:
        def page = getPage("/content/avionos/empty")

        expect:
        !page.adaptTo(ComponentResource)
    }

    def "get child"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.getChild("child2").get().title == "Also Child 2"
    }

    def "get children"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.children.size() == 3
    }

    def "list children"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.listChildPages().size() == 3
    }

    def "get displayable children"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.getChildren(true).size() == 1
    }

    def "get children filtered for predicate"() {
        setup:
        def page = getPage("/content/avionos")
        def predicate = new TemplatePredicate("template")

        expect:
        page.getChildren(predicate).size() == 1
    }

    def "list children filtered for predicate"() {
        setup:
        def page = getPage("/content/avionos")
        def predicate = new TemplatePredicate("template")

        expect:
        page.listChildPages(predicate).size() == 1
    }

    def "list children recursively, filtered for predicate"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.listChildPages(predicate, true).size() == size

        where:
        predicate                         | size
        new TemplatePredicate("template") | 1
        TRUE                              | 4
    }

    def "find descendants"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.findDescendants(TRUE).size() == 4
    }

    def "get properties"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.properties.keySet().containsAll(["jcr:title", "otherPagePath"])
    }

    def "get properties for page with no jcr:content node"() {
        setup:
        def page = getPage("/content/avionos/empty")

        expect:
        page.properties.isEmpty()
    }

    def "get properties at relative path"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.getProperties("component/one").containsKey("sling:resourceType")
    }

    def "get title"() {
        setup:
        def page = getPage("/content/avionos")

        expect:
        page.getTitle(titleType).get() == title

        where:
        titleType                  | title
        TitleType.TITLE            | "Avionos"
        TitleType.NAVIGATION_TITLE | "Navigation Title"
        TitleType.PAGE_TITLE       | "Page Title"
    }

    def "get title returns absent where appropriate"() {
        setup:
        def page = getPage("/content/avionos/child1")

        expect:
        !page.getTitle(titleType).present

        where:
        titleType << [
            TitleType.NAVIGATION_TITLE,
            TitleType.PAGE_TITLE
        ]
    }

    def "get pageTitle falls back where appropriate"() {
        setup:
        def page = getPage(pagePath)

        expect:
        page.getPageTitle() == title

        where:
        pagePath                                         | title
        "/content/pageTitles/pageTitleNoNavigationTitle" | "Page Title"
        "/content/pageTitles/noPageNoNavigationTitle"    | "Simple Title"
    }

    def "get navigationTitle falls back where appropriate"() {
        setup:
        def page = getPage(pagePath)

        expect:
        page.getNavigationTitle() == title

        where:
        pagePath                                         | title
        "/content/pageTitles/pageTitleNavigationTitle"   | "Navigation Title"
        "/content/pageTitles/pageTitleNoNavigationTitle" | "Page Title"
        "/content/pageTitles/noPageNoNavigationTitle"    | "Simple Title"
    }
}
