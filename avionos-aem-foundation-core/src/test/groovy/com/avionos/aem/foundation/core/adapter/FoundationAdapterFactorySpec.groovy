package com.avionos.aem.foundation.core.adapter

import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.core.specs.FoundationSpec
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class FoundationAdapterFactorySpec extends FoundationSpec {

    @Shared
    FoundationAdapterFactory adapterFactory = new FoundationAdapterFactory()

    def setupSpec() {
        pageBuilder.content {
            home()
        }
    }

    def "resource adapt to foundation page"() {
        setup:
        def resource = resourceResolver.getResource("/content/home")

        expect:
        adapterFactory.getAdapter(resource, FoundationPage)
    }

    def "resource adapt to page returns null for non-page node"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, FoundationPage)
    }

    def "get resource adapter for invalid type returns null"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, String)
    }

    def "get resource resolver adapter for valid type returns non-null"() {
        expect:
        adapterFactory.getAdapter(resourceResolver, FoundationPageManager)
    }

    def "get resource resolver adapter for invalid type returns null"() {
        expect:
        !adapterFactory.getAdapter(resourceResolver, String)
    }

    def "get invalid adapter returns null"() {
        expect:
        !adapterFactory.getAdapter("", String)
    }
}
