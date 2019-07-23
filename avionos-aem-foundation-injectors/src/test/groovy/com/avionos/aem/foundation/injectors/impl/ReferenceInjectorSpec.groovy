package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.injectors.annotations.ReferenceInject
import com.avionos.aem.foundation.injectors.specs.SlingModelSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model

import javax.inject.Named

class ReferenceInjectorSpec extends SlingModelSpec {

    @Model(adaptables = Resource, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class Component {

        @ReferenceInject
        Resource singleResource

        @ReferenceInject
        FoundationPage singlePage

        @ReferenceInject
        List<Resource> multipleResources

        @ReferenceInject
        List<FoundationPage> multiplePages

        @ReferenceInject(inherit = true)
        Resource inheritedSingleResource

        @ReferenceInject(inherit = true)
        FoundationPage inheritedSinglePage

        @ReferenceInject(inherit = true)
        List<Resource> inheritedMultipleResources

        @ReferenceInject(inherit = true)
        List<FoundationPage> inheritedMultiplePages

        @ReferenceInject
        @Named("mySingleResource")
        Resource namedSingleResource

        @ReferenceInject(inherit = true)
        @Named("inheritedMySingleResource")
        Resource inheritedNamedSingleResource

    }

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component(
                        inheritedSingleResource: "/content/citytechinc/page1",
                        inheritedSinglePage: "/content/citytechinc/page1",
                        inheritedMultipleResources: [
                            "/content/citytechinc/page1",
                            "/content/citytechinc/page2",
                            "/content/citytechinc/page3"
                        ],
                        inheritedMultiplePages: [
                            "/content/citytechinc/page1",
                            "/content/citytechinc/page2",
                            "/content/citytechinc/page3"
                        ],
                        inheritedMySingleResource: "/content/citytechinc/page1"
                    )
                    unconfiguredComponent()
                }
                page1 {
                    "jcr:content" {}
                }
                page2 {
                    "jcr:content" {}
                }
                page3 {
                    "jcr:content" {}
                }
                componentPage {
                    "jcr:content" {
                        component(
                            singleResource: "/content/citytechinc/page1",
                            singlePage: "/content/citytechinc/page1",
                            multipleResources: [
                                "/content/citytechinc/page1",
                                "/content/citytechinc/page2",
                                "/content/citytechinc/page3"
                            ],
                            multiplePages: [
                                "/content/citytechinc/page1",
                                "/content/citytechinc/page2",
                                "/content/citytechinc/page3"
                            ],
                            mySingleResource: "/content/citytechinc/page1"
                        )
                    }
                }
            }
        }
    }

    def "all properties should be null if component is unconfigured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/jcr:content/unconfiguredComponent")
        def component = resource.adaptTo(Component)

        expect:
        !component.singleResource
        !component.singlePage
        !component.multipleResources
        !component.multiplePages
        !component.inheritedSingleResource
        !component.inheritedSinglePage
        !component.inheritedMultipleResources
        !component.inheritedMultiplePages
        !component.namedSingleResource
    }

    def "component has a single Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.singleResource instanceof Resource
        component.singleResource.path == "/content/citytechinc/page1"
    }

    def "component has a single FoundationPage configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.singlePage instanceof FoundationPage
        component.singlePage.path == "/content/citytechinc/page1"
    }

    def "component has multiple Resources configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.multipleResources.size() == 3
        component.multipleResources[0] instanceof Resource
        component.multipleResources[1] instanceof Resource
        component.multipleResources[2] instanceof Resource
        component.multipleResources[0].path == "/content/citytechinc/page1"
        component.multipleResources[1].path == "/content/citytechinc/page2"
        component.multipleResources[2].path == "/content/citytechinc/page3"
    }

    def "component has multiple FoundationPages configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.multiplePages.size() == 3
        component.multiplePages[0] instanceof FoundationPage
        component.multiplePages[1] instanceof FoundationPage
        component.multiplePages[2] instanceof FoundationPage
        component.multiplePages[0].path == "/content/citytechinc/page1"
        component.multiplePages[1].path == "/content/citytechinc/page2"
        component.multiplePages[2].path == "/content/citytechinc/page3"
    }

    def "component has a single inherited Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedSingleResource instanceof Resource
        component.inheritedSingleResource.path == "/content/citytechinc/page1"
    }

    def "component has a single inherited FoundationPage configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedSinglePage instanceof FoundationPage
        component.inheritedSinglePage.path == "/content/citytechinc/page1"
    }

    def "component has multiple inherited Resources configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedMultipleResources.size() == 3
        component.inheritedMultipleResources[0] instanceof Resource
        component.inheritedMultipleResources[1] instanceof Resource
        component.inheritedMultipleResources[2] instanceof Resource
        component.inheritedMultipleResources[0].path == "/content/citytechinc/page1"
        component.inheritedMultipleResources[1].path == "/content/citytechinc/page2"
        component.inheritedMultipleResources[2].path == "/content/citytechinc/page3"
    }

    def "component has multiple inherited FoundationPages configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedMultiplePages.size() == 3
        component.inheritedMultiplePages[0] instanceof FoundationPage
        component.inheritedMultiplePages[1] instanceof FoundationPage
        component.inheritedMultiplePages[2] instanceof FoundationPage
        component.inheritedMultiplePages[0].path == "/content/citytechinc/page1"
        component.inheritedMultiplePages[1].path == "/content/citytechinc/page2"
        component.inheritedMultiplePages[2].path == "/content/citytechinc/page3"
    }

    def "component has a named single Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.namedSingleResource instanceof Resource
        component.namedSingleResource.path == "/content/citytechinc/page1"
    }

    def "component has a named single inherited Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedNamedSingleResource instanceof Resource
        component.inheritedNamedSingleResource.path == "/content/citytechinc/page1"
    }

}
