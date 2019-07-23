package com.avionos.aem.foundation.core.link.impl

import com.avionos.aem.foundation.api.link.ImageLink
import com.avionos.aem.foundation.api.link.Link
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultImageLink implements ImageLink {

    @Delegate
    Link link

    String imageSource
}
