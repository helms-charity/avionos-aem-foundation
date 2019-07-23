package com.avionos.aem.foundation.core.servlets

import com.avionos.aem.foundation.api.request.ComponentServletRequest
import com.avionos.aem.foundation.core.specs.FoundationSpec
import groovy.json.JsonBuilder

import javax.servlet.ServletException

class AbstractComponentServletSpec extends FoundationSpec {

    static final def MAP = [one: 1, two: 2]

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "process get"() {
        setup:
        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        def servlet = new AbstractComponentServlet() {
            @Override
            protected void processGet(ComponentServletRequest request) throws ServletException, IOException {
                new JsonBuilder(MAP).writeTo(request.slingResponse.writer)
            }
        }

        when:
        servlet.doGet(slingRequest, slingResponse)

        then:
        slingResponse.outputAsString == new JsonBuilder(MAP).toString()
    }
}
