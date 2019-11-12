package com.avionos.aem.foundation.core.link.impl

import com.avionos.aem.foundation.api.link.Link
import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includeNames = true, includePackage = false)
class DefaultLink implements Link {

    String path

    String extension

    String suffix

    String href

    List<String> selectors

    String queryString

    boolean external

    String target

    String title

    Map<String, String> properties

    boolean active

    List<Link> children

    @Override
    boolean isEmpty() {
        !href
    }
}
