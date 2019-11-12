package com.avionos.aem.foundation.core.link.builders.impl

import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.page.enums.TitleType
import com.avionos.aem.foundation.core.link.builders.factory.LinkBuilderFactory
import com.avionos.aem.foundation.core.specs.FoundationSpec
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import spock.lang.Unroll

@Unroll
class DefaultLinkBuilderSpec extends FoundationSpec {

    class MappingResourceResolver implements ResourceResolver {

        static final def MAP = [
            "/content/us.html": "/content/us/home.html",
            "/content/about.html": "http://www.olsondigital.com/about.html"
        ]

        @Delegate
        ResourceResolver resourceResolver

        MappingResourceResolver(resourceResolver) {
            this.resourceResolver = resourceResolver
        }

        @Override
        String map(String path) {
            MAP[path] ?: path
        }
    }

    def setupSpec() {
        pageBuilder.content {
            global("Global") {
                "jcr:content"(navTitle: "Global Navigation")
            }
            us("US") {
                "jcr:content"()
            }
            de("DE") {
                "jcr:content"(redirectTarget: "/content/global")
            }
        }

        session.getNode("/content").addNode("se", NameConstants.NT_PAGE)
        session.save()
    }

    def "build link for existing link"() {
        setup:
        def link = LinkBuilderFactory.forPath("/content/global").build()

        expect:
        LinkBuilderFactory.forLink(link).build() == link
    }

    def "build link for page"() {
        setup:
        def page = resourceResolver.adaptTo(FoundationPageManager).getPage("/content/global")
        def link = LinkBuilderFactory.forPage(page).build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global.html"
        link.extension == "html"
        link.title == "Global"
    }

    def "build link for page with no jcr:content node"() {
        setup:
        def page = resourceResolver.adaptTo(FoundationPageManager).getPage("/content/se")
        def link = LinkBuilderFactory.forPage(page).build()

        expect:
        link.path == "/content/se"
        link.href == "/content/se.html"
        link.extension == "html"
        link.title == ""
    }

    def "build link for page with redirect"() {
        setup:
        def page = resourceResolver.adaptTo(FoundationPageManager).getPage("/content/de")
        def link = LinkBuilderFactory.forPage(page).build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global.html"
        link.extension == "html"
        link.title == "DE"
    }

    def "build link for page with navigation title"() {
        setup:
        def page = resourceResolver.adaptTo(FoundationPageManager).getPage("/content/global")
        def link = LinkBuilderFactory.forPage(page, TitleType.NAVIGATION_TITLE).build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global.html"
        link.extension == "html"
        link.title == "Global Navigation"
    }

    def "build link for page without navigation title"() {
        setup:
        def page = resourceResolver.adaptTo(FoundationPageManager).getPage("/content/us")
        def link = LinkBuilderFactory.forPage(page, TitleType.NAVIGATION_TITLE).build()

        expect:
        link.path == "/content/us"
        link.href == "/content/us.html"
        link.extension == "html"
        link.title == "US"
    }

    def "build link for mapped page"() {
        setup:
        def resource = Mock(Resource)

        resource.resourceResolver >> new MappingResourceResolver(resourceResolver)

        def page = [
            getContentResource: { resource },
            getProperties: { ValueMap.EMPTY },
            getTitle: { "" },
            getPath: { path }
        ] as Page

        def link = LinkBuilderFactory.forPage(page, mapped).build()

        expect:
        link.href == mappedHref

        where:
        path              | mappedHref              | mapped
        "/content/us"     | "/content/us/home.html" | true
        "/content/us"     | "/content/us.html"      | false
        "/content/global" | "/content/global.html"  | true
        "/content/global" | "/content/global.html"  | false
    }

    def "build link for resource"() {
        setup:
        def resource = getResource("/content/global/jcr:content")

        expect:
        LinkBuilderFactory.forResource(resource).build().path == "/content/global/jcr:content"
    }

    def "build link for mapped resource"() {
        setup:
        def resource = Mock(Resource)

        resource.resourceResolver >> new MappingResourceResolver(resourceResolver)
        resource.path >> "/content/about"

        def link = LinkBuilderFactory.forResource(resource, true).build()

        expect:
        link.path == "/content/about"
        link.href == "http://www.olsondigital.com/about.html"
    }

    def "build link for path"() {
        setup:
        def builder = LinkBuilderFactory.forPath("/content")

        builder.extension = extension
        builder.suffix = suffix
        builder.scheme = scheme
        builder.host = host
        builder.port = port
        builder.secure = secure

        def link = builder.build()

        expect:
        link.href == href

        where:
        extension | suffix    | scheme | host        | port | secure | href
        null      | ""        | null   | "localhost" | 0    | false  | "http://localhost/content.html"
        null      | "/suffix" | null   | "localhost" | 0    | false  | "http://localhost/content.html/suffix"
        ""        | ""        | null   | "localhost" | 0    | false  | "http://localhost/content"
        ""        | "/suffix" | null   | "localhost" | 0    | false  | "http://localhost/content/suffix"
        "html"    | ""        | null   | "localhost" | 0    | false  | "http://localhost/content.html"
        "html"    | "/suffix" | null   | "localhost" | 0    | false  | "http://localhost/content.html/suffix"
        "json"    | ""        | null   | "localhost" | 0    | false  | "http://localhost/content.json"
        null      | ""        | null   | "localhost" | 4502 | false  | "http://localhost:4502/content.html"
        null      | ""        | null   | "localhost" | 0    | true   | "https://localhost/content.html"
        null      | ""        | "ftp"  | "localhost" | 0    | false  | "ftp://localhost/content.html"
        null      | ""        | "ftp"  | "localhost" | 0    | true   | "ftp://localhost/content.html"
    }

    def "build link and set external"() {
        setup:
        def builder = LinkBuilderFactory.forPath("/content")

        builder.external = external

        def link = builder.build()

        expect:
        link.href == href

        where:
        external | href
        true     | "/content"
        false    | "/content.html"
    }

    def "build link for path with selectors"() {
        setup:
        def link = LinkBuilderFactory.forPath(path).addSelectors(selectors).build()

        expect:
        link.href == href

        where:
        path                    | selectors  | href
        "/content"              | []         | "/content.html"
        "/content"              | ["a"]      | "/content.a.html"
        "/content"              | ["a", "b"] | "/content.a.b.html"
        "http://www.reddit.com" | ["a", "b"] | "http://www.reddit.com"
    }

    def "build link for path with scheme"() {
        setup:
        def link = LinkBuilderFactory.forPath(path)
            .setScheme(scheme)
            .setOpaque(opaque)
            .build()

        expect:
        link.href == href

        where:
        path                    | scheme   | opaque | href
        "/content"              | "http"   | false  | "/content.html"
        "+48957228989"          | "tel"    | true   | "tel:+48957228989"
        "http://www.reddit.com" | ""       | false  | "http://www.reddit.com"
        "http://www.reddit.com" | ""       | true   | "http://www.reddit.com"
        "https://reddit.com"    | "ftp"    | true   | "ftp:https://reddit.com"
        "https://reddit.com"    | "ftp"    | false  | "ftp://https://reddit.com"
        "someone@domain.com"    | "mailto" | true   | "mailto:someone@domain.com"
    }

    def "build link for link and set protocol"() {
        setup:
        def link = LinkBuilderFactory.forPath(path).build()

        expect:
        LinkBuilderFactory.forLink(link).setScheme(scheme).setOpaque(opaque).build().href == href

        where:
        path                    | scheme   | opaque | href
        "/content"              | "http"   | false  | "/content.html"
        "+48957228989"          | "tel"    | true   | "tel:+48957228989"
        "http://www.reddit.com" | ""       | false  | "http://www.reddit.com"
        "http://www.reddit.com" | ""       | true   | "http://www.reddit.com"
        "www.reddit.com"        | "https"  | false  | "https://www.reddit.com"
        "https://reddit.com"    | "ftp"    | true   | "ftp:https://reddit.com"
        "someone@domain.com"    | "mailto" | true   | "mailto:someone@domain.com"
    }

    def "build link for path with parameters"() {
        setup:
        def link = LinkBuilderFactory.forPath("/content").addParameters(parameters).build()

        expect:
        link.href == href
        link.queryString == queryString

        where:
        parameters           | href                    | queryString
        [:]                  | "/content.html"         | ""
        ["a": "1"]           | "/content.html?a=1"     | "?a=1"
        ["a": "1", "b": "2"] | "/content.html?a=1&b=2" | "?a=1&b=2"
    }

    def "build link for path with same-name parameters"() {
        setup:
        def builder = LinkBuilderFactory.forPath("/content")

        when:
        builder.addParameter("a", "1")
        builder.addParameter("a", "2")
        builder.addParameter("a", "3")

        then:
        def link = builder.build()

        expect:
        link.queryString == "?a=1&a=2&a=3"
    }

    def "build link without children"() {
        setup:
        def navigationLink = LinkBuilderFactory.forPath("/content/global").build()

        expect:
        !navigationLink.children
    }

    def "build link without children with active state"() {
        setup:
        def navigationLink = LinkBuilderFactory.forPath("/content/global").setActive(active).build()

        expect:
        navigationLink.active == active

        where:
        active << [true, false]
    }

    def "build link with children"() {
        setup:
        def builder = LinkBuilderFactory.forPath("/content/global")

        builder.addChild(LinkBuilderFactory.forPath("/content/1").build())
        builder.addChild(LinkBuilderFactory.forPath("/content/2").build())
        builder.addChild(LinkBuilderFactory.forPath("/content/3").build())

        def navigationLink = builder.build()

        expect:
        navigationLink.children.size() == 3
        navigationLink.children*.path == ["/content/1", "/content/2", "/content/3"]
    }
}