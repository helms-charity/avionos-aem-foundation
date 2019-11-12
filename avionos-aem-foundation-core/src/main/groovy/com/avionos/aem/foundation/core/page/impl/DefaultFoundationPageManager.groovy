package com.avionos.aem.foundation.core.page.impl

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMException
import com.google.common.base.Stopwatch
import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.core.page.predicates.TemplatePredicate
import com.avionos.aem.foundation.core.utils.PathUtils
import groovy.util.logging.Slf4j
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

import javax.jcr.RepositoryException
import javax.jcr.query.Query
import javax.jcr.query.Row
import java.util.function.Predicate

import static com.google.common.base.Preconditions.checkNotNull
import static java.util.concurrent.TimeUnit.MILLISECONDS

@Slf4j("LOG")
class DefaultFoundationPageManager implements FoundationPageManager {

    private final ResourceResolver resourceResolver

    @Delegate
    private final PageManager pageManager

    DefaultFoundationPageManager(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver

        pageManager = resourceResolver.adaptTo(PageManager)
    }

    @Override
    List<FoundationPage> findPages(String rootPath, Collection<String> tagIds, boolean matchOne) {
        checkNotNull(rootPath)
        checkNotNull(tagIds)

        LOG.debug("path = {}, tag IDs = {}", rootPath, tagIds)

        def stopwatch = Stopwatch.createStarted()

        def iterator = resourceResolver.adaptTo(TagManager).find(rootPath, tagIds as String[], matchOne)

        def pages = []

        iterator*.each { resource ->
            if (JcrConstants.JCR_CONTENT == resource.name) {
                def page = getPage(resource.parent.path)

                if (page) {
                    pages.add(page)
                }
            }
        }

        LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS))

        pages
    }

    @Override
    List<FoundationPage> search(Query query) {
        search(query, -1)
    }

    @Override
    List<FoundationPage> search(Query query, int limit) {
        checkNotNull(query)

        LOG.debug("query statement = {}", query.statement)

        def stopwatch = Stopwatch.createStarted()

        def pages = []

        int count = 0

        try {
            def paths = [] as Set

            query.execute().rows.each { Row row ->
                if (limit == -1 || count < limit) {
                    def path = row.path

                    LOG.debug("result path = {}", path)

                    def pagePath = PathUtils.getPagePath(path)

                    // ensure no duplicate pages are added
                    if (!paths.contains(pagePath)) {
                        paths.add(pagePath)

                        def page = getFoundationPage(path)

                        if (page) {
                            pages.add(page)
                            count++
                        } else {
                            LOG.error("result is null for path = {}", path)
                        }
                    }
                }
            }

            stopwatch.stop()

            LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS))
        } catch (RepositoryException re) {
            LOG.error("error finding pages for query = ${query.statement}", re)
        }

        pages
    }

    @Override
    List<FoundationPage> findPages(String rootPath, String template) {
        findPages(rootPath, new TemplatePredicate(template))
    }

    @Override
    List<FoundationPage> findPages(String rootPath, Predicate<FoundationPage> predicate) {
        def page = getPage(checkNotNull(rootPath))

        def stopwatch = Stopwatch.createStarted()

        def result = page ? page.findDescendants(predicate) : []

        stopwatch.stop()

        LOG.debug("found {} result(s) in {}ms", result.size(), stopwatch.elapsed(MILLISECONDS))

        result
    }

    @Override
    FoundationPage copy(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict) throws WCMException {
        getFoundationPage(pageManager.copy(page, destination, beforeName, shallow, resolveConflict))
    }

    @Override
    FoundationPage copy(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict, boolean autoSave) throws WCMException {
        getFoundationPage(pageManager.copy(page, destination, beforeName, shallow, resolveConflict, autoSave))
    }

    @Override
    FoundationPage create(String parentPath, String pageName, String template,
        String title) throws WCMException {
        getFoundationPage(pageManager.create(parentPath, pageName, template, title))
    }

    @Override
    FoundationPage create(String parentPath, String pageName, String template,
        String title, boolean autoSave) throws WCMException {
        getFoundationPage(pageManager.create(parentPath, pageName, template, title, autoSave))
    }

    @Override
    FoundationPage getContainingPage(Resource resource) {
        getFoundationPage(pageManager.getContainingPage(resource))
    }

    @Override
    FoundationPage getContainingPage(String path) {
        getFoundationPage(pageManager.getContainingPage(path))
    }

    @Override
    FoundationPage getPage(Page page) {
        getFoundationPage(page)
    }

    @Override
    FoundationPage getPage(String path) {
        getFoundationPage(checkNotNull(path))
    }

    @Override
    FoundationPage move(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict, String[] adjustRefs) throws WCMException {
        getFoundationPage(pageManager.move(page, destination, beforeName, shallow, resolveConflict, adjustRefs))
    }

    @Override
    FoundationPage move(Page page, String destination, String beforeName, boolean shallow,
        boolean resolveConflict, String[] adjustRefs, String[] publishRefs) throws WCMException {
        getFoundationPage(pageManager.move(page, destination, beforeName, shallow, resolveConflict, adjustRefs, publishRefs))
    }

    @Override
    FoundationPage restore(String path, String revisionId) throws WCMException {
        getFoundationPage(pageManager.restore(path, revisionId))
    }

    @Override
    FoundationPage restoreTree(String path, Calendar date) throws WCMException {
        getFoundationPage(pageManager.restoreTree(path, date))
    }

    // internals

    private FoundationPage getFoundationPage(String path) {
        getFoundationPage(pageManager.getPage(path))
    }

    private FoundationPage getFoundationPage(Page page) {
        page ? page.adaptTo(FoundationPage.class) : null
    }
}
