package com.avionos.aem.foundation.injectors.impl

import com.avionos.aem.foundation.core.components.AbstractComponent
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

@Model(adaptables = Resource)
class FoundationModelComponent extends AbstractComponent {

    String getTitle() {
        get("jcr:title", "")
    }
}
