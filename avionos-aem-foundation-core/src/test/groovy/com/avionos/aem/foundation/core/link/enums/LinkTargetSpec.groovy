package com.avionos.aem.foundation.core.link.enums

import com.avionos.aem.foundation.api.link.enums.LinkTarget
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class LinkTargetSpec extends Specification {

    def "for invalid target"() {
        when:
        LinkTarget.forTarget("")

        then:
        thrown(IllegalArgumentException)
    }

    def "for target"() {
        expect:
        LinkTarget.forTarget(target) == linkTarget

        where:
        target    | linkTarget
        "_none"   | LinkTarget.SELF
        "_self"   | LinkTarget.SELF
        "_blank"  | LinkTarget.BLANK
        "_parent" | LinkTarget.PARENT
        "_top"    | LinkTarget.TOP
    }
}
