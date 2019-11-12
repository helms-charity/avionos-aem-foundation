package com.avionos.aem.foundation.core.page.impl;

import com.avionos.aem.foundation.api.page.FoundationPage;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class FoundationPageIterator implements Iterator<FoundationPage> {

    private FoundationPage nextPage;

    private final Iterator<Resource> base;

    private final Predicate<FoundationPage> predicate;

    FoundationPageIterator(final Iterator<Resource> base, final Predicate<FoundationPage> predicate) {
        this.base = base;
        this.predicate = predicate;

        seek();
    }

    @Override
    public boolean hasNext() {
        return nextPage != null;
    }

    @Override
    public FoundationPage next() {
        if (nextPage != null) {
            return seek();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private FoundationPage seek() {
        FoundationPage prev = nextPage;

        nextPage = null;

        while (base.hasNext() && nextPage == null) {
            final Resource resource = base.next();

            nextPage = resource.adaptTo(FoundationPage.class);

            if (nextPage != null && predicate != null && !predicate.test(nextPage)) {
                nextPage = null;
            }
        }

        return prev;
    }
}
