package com.avionos.aem.foundation.core.link.impl

import com.avionos.aem.foundation.api.link.Link

import java.util.function.Function

final class LinkFunctions {

    static final Function<Link, String> LINK_TO_HREF = new Function<Link, String>() {
        @Override
        String apply(Link link) {
            link.href
        }
    }

    private LinkFunctions() {

    }
}
