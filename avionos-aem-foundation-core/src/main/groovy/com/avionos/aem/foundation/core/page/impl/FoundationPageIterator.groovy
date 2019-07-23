package com.avionos.aem.foundation.core.page.impl

import com.avionos.aem.foundation.api.page.FoundationPage
import org.apache.sling.api.resource.Resource

import java.util.function.Predicate

class FoundationPageIterator implements Iterator<FoundationPage> {

    private FoundationPage next

    private final Iterator<Resource> base

    private final Predicate<FoundationPage> predicate

    FoundationPageIterator(Iterator<Resource> base, Predicate<FoundationPage> predicate) {
        this.base = base
        this.predicate = predicate

        seek()
    }

    @Override
    boolean hasNext() {
        next
    }

    @Override
    FoundationPage next() {
        if (next) {
            seek()
        } else {
            throw new NoSuchElementException()
        }
    }

    @Override
    void remove() {
        throw new UnsupportedOperationException()
    }

    private FoundationPage seek() {
        def prev = next

        next = null

        while (base.hasNext() && !next) {
            def resource = base.next()

            next = resource.adaptTo(FoundationPage)

            if (next && predicate && !predicate.test(next)) {
                next = null
            }
        }

        prev
    }
}
