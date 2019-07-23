package com.avionos.aem.foundation.core.page.predicates;

import com.avionos.aem.foundation.api.page.FoundationPage;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Predicate that filters on the value of the page's cq:template property.
 */
public final class TemplatePredicate implements Predicate<FoundationPage> {

    private final String templatePath;

    public TemplatePredicate(final FoundationPage page) {
        checkNotNull(page);

        templatePath = page.getTemplatePath();
    }

    public TemplatePredicate(final String templatePath) {
        checkNotNull(templatePath);

        this.templatePath = templatePath;
    }

    @Override
    public boolean test(final FoundationPage page) {
        return templatePath.equals(page.getTemplatePath());
    }
}
