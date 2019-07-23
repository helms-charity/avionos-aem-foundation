package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.injectors.specs.SlingModelSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

class ModelListInjectorSpec extends SlingModelSpec {

    @Model(adaptables = Resource)
    static class ValueMapInjectorModel {

        @Inject
        String name
    }

    @Model(adaptables = SlingHttpServletRequest)
    static class ListModel {

        @Inject
        List<ValueMapInjectorModel> rush
    }

    def setupSpec() {
        pageBuilder.content {
            avionos {
                "jcr:content" {
                    component {
                        rush {
                            drums(name: "Neil")
                            bass(name: "Geddy")
                            guitar(name: "Alex")
                        }
                    }
                }
            }
        }
    }

    def "inject list of models"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/avionos/jcr:content/component"
        }

        def model = request.adaptTo(ListModel)

        expect:
        model.rush.size() == 3
        model.rush*.name == ["Neil", "Geddy", "Alex"]
    }
}
