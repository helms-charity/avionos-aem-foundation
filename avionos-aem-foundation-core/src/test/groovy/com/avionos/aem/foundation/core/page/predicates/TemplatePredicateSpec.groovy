package com.avionos.aem.foundation.core.page.predicates

import com.avionos.aem.foundation.core.specs.FoundationSpec

class TemplatePredicateSpec extends FoundationSpec {

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content"("cq:template": "homepage")
                child1 {
                    "jcr:content"("cq:template": "template")
                }
                child2()
            }
        }
    }

    def "page has no template property"() {
        setup:
        def page = getPage("/content/avionos/child2")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.test(page)
    }

    def "template matches page template"() {
        setup:
        def page = getPage("/content/avionos/child1")
        def predicate = new TemplatePredicate("template")
        def predicateForPage = new TemplatePredicate(page)

        expect:
        predicate.test(page) && predicateForPage.test(page)
    }

    def "template does not match page template"() {
        setup:
        def page = getPage("/content/avionos")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.test(page)
    }
}
