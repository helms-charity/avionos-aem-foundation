package com.avionos.aem.foundation.core.adapter

import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.core.specs.FoundationSpec
import spock.lang.Unroll

@Unroll
class FoundationAdapterFactorySpec extends FoundationSpec {

    def "get resource resolver adapter for valid type returns non-null"() {
        expect:
        new FoundationAdapterFactory().getAdapter(resourceResolver, FoundationPageManager)
    }
}
