package com.avionos.aem.foundation.core.link.impl

import com.avionos.aem.foundation.api.link.Link
import com.avionos.aem.foundation.api.link.NavigationLink
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [Link])
class DefaultNavigationLink implements NavigationLink {

    @Delegate
    Link link

    boolean active

    List<NavigationLink> children
}
