package com.avionos.aem.foundation.injectors.specs

import com.avionos.aem.foundation.core.specs.FoundationSpec
import com.avionos.aem.foundation.injectors.impl.ComponentInjector
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
            registerInjector(new ComponentInjector(), Integer.MIN_VALUE)
            registerInjector(new ResourceResolverAdaptableInjector(), Integer.MAX_VALUE)
            registerInjector(new TagInjector())
            registerInjector(new EnumInjector())
            registerInjector(new ImageInjector())
            registerInjector(new InheritInjector())
            registerInjector(new LinkInjector())
            registerInjector(new ReferenceInjector())
            registerInjector(new ModelListInjector())
            registerInjector(new ValueMapFromRequestInjector())
        }
    }
}
