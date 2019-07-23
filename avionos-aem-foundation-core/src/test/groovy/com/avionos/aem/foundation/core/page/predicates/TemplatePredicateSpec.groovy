package com.avionos.aem.foundation.core.page.predicates

import com.avionos.aem.foundation.core.specs.FoundationSpec

class TemplatePredicateSpec extends FoundationSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
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
        def page = getPage("/content/citytechinc/child2")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.test(page)
    }

    def "template matches page template"() {
        setup:
        def page = getPage("/content/citytechinc/child1")
        def predicate = new TemplatePredicate("template")
        def predicateForPage = new TemplatePredicate(page)

        expect:
        predicate.test(page) && predicateForPage.test(page)
    }

    def "template does not match page template"() {
        setup:
        def page = getPage("/content/citytechinc")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.test(page)
    }
}
