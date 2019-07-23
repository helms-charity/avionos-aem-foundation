package com.avionos.aem.foundation.injectors.specs

import com.avionos.aem.foundation.core.specs.FoundationSpec
import com.avionos.aem.foundation.injectors.impl.ComponentInjector
import com.avionos.aem.foundation.injectors.impl.ContentPolicyInjector
import com.avionos.aem.foundation.injectors.impl.EnumInjector
import com.avionos.aem.foundation.injectors.impl.ImageInjector
import com.avionos.aem.foundation.injectors.impl.InheritInjector
import com.avionos.aem.foundation.injectors.impl.LinkInjector
import com.avionos.aem.foundation.injectors.impl.ModelListInjector
import com.avionos.aem.foundation.injectors.impl.ReferenceInjector
import com.avionos.aem.foundation.injectors.impl.ResourceResolverAdaptableInjector
import com.avionos.aem.foundation.injectors.impl.TagInjector
import com.avionos.aem.foundation.injectors.impl.ValueMapFromRequestInjector

/**
 * Specs may extend this class to support injection of Avionos AEM Foundation dependencies in Sling model-based
 * components.
 */
abstract class SlingModelSpec extends FoundationSpec {

    /**
     * Register default Avionos AEM Foundation injectors and all <code>@Model>/code>-annotated classes for the
     * current package.
     */
    def setupSpec() {
        registerDefaultInjectors()

        slingContext.addModelsForPackage(this.class.package.name)
    }

    /**
     * Register the default set of Avionos AEM Foundation injector services.
     */
    void registerDefaultInjectors() {
        slingContext.with {
            registerInjector(new ComponentInjector(), Integer.MAX_VALUE)
            registerInjector(new ResourceResolverAdaptableInjector(), Integer.MIN_VALUE)
            registerInjector(new ContentPolicyInjector(), Integer.MAX_VALUE)
            registerInjector(new TagInjector(), 800)
            registerInjector(new EnumInjector(), 4000)
            registerInjector(new ImageInjector(), 4000)
            registerInjector(new InheritInjector(), 4000)
            registerInjector(new LinkInjector(), 4000)
            registerInjector(new ReferenceInjector(), 4000)
            registerInjector(new ModelListInjector(), 999)
            registerInjector(new ValueMapFromRequestInjector(), 2500)
        }
    }
}
