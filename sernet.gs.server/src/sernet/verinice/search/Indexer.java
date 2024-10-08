/*******************************************************************************
 * Copyright (c) 2015 Daniel Murygin.
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.elasticsearch.common.collect.Lists;
import org.springframework.beans.factory.ObjectFactory;

import sernet.gs.server.security.DummyAuthenticationRunnable;
import sernet.gs.service.ServerInitializer;
import sernet.gs.service.TimeFormatter;
import sernet.verinice.concurrency.ClosableCompletionService;
import sernet.verinice.concurrency.CustomNamedThreadGroupFactory;
import sernet.verinice.concurrency.TrackableCompletionService;
import sernet.verinice.interfaces.IBaseDao;
import sernet.verinice.interfaces.IElementTitleCache;
import sernet.verinice.model.bp.elements.ItNetwork;
import sernet.verinice.model.bsi.ITVerbund;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.model.iso27k.Organization;

/**
 * Creates Elasticsearch index for verinice.
 *
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 * @author Benjamin Weißenfels <bw[at]sernet[dot]de>
 */
public class Indexer {

    private static final Logger LOG = Logger.getLogger(Indexer.class);

    private static final String HQL_LOAD_IDS = "select dbId from CnATreeElement";

    private static final int INDEXING_CHUNK_SIZE = 50;

    private IBaseDao<CnATreeElement, Integer> elementDao;

    private IElementTitleCache titleCache;

    private long indexingStart;

    /**
     * Factory to create {@link IndexThread} instances configured in
     * veriniceserver-search-base.xml
     */
    private ObjectFactory indexThreadFactory;

    /**
     * Creates an index in an non blocking way, means this method creates all
     * necessary index threads and returns immediately. It gives no guarantee
     * that an index is available after this method is finished. The index will
     * be available after an arbitrary amount of time.
     *
     * <p>
     * If you are using log4j in debug method the time consumption of the index
     * process is logged, but the non blocking behavior will stay the same.
     * </p>
     *
     *
     * <p>
     * If you need to know, when indexing is finished take a look at
     * {@link #blockingIndexing()}
     * </p>
     *
     */
    public void nonBlockingIndexing() {
        runIndexingThread();
    }

    private void runIndexingThread() {
        DummyAuthenticationRunnable dummyAuthenticationRunnable = new DummyAuthenticationRunnableExtension();
        ThreadFactory threadFactory = new CustomNamedThreadGroupFactory("index");
        ExecutorService exeService = Executors.newSingleThreadExecutor(threadFactory);
        exeService.execute(dummyAuthenticationRunnable);
        exeService.shutdown();
    }

    private ClosableCompletionService<List<IndexedElementDetails>> doIndex(
            boolean logIndexedElementDetails) {

        indexingStart = System.currentTimeMillis();

        ClosableCompletionService<List<IndexedElementDetails>> completionService = TrackableCompletionService
                .newInstance();
        List<String> allIDs = geAllCnATreeElementIDS();

        if (LOG.isInfoEnabled()) {
            LOG.info("Elements: " + allIDs.size() + ", start indexing...");
        }

        getTitleCache().load(ITVerbund.TYPE_ID_HIBERNATE, Organization.TYPE_ID, ItNetwork.TYPE_ID);
        Collection<IndexThread> indexThreads = createIndexThreadsByIDs(allIDs,
                logIndexedElementDetails);
        for (IndexThread indexThread : indexThreads) {
            completionService.submit(indexThread);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("All threads created ans submitted to completion service.");
        }

        completionService.shutDown();
        return completionService;
    }

    @SuppressWarnings("unchecked")
    private List<String> geAllCnATreeElementIDS() {
        List<String> allUuids;

        ServerInitializer.inheritVeriniceContextState();
        allUuids = getElementDao().findByQuery(HQL_LOAD_IDS, null);

        return allUuids;
    }

    private void logNonBlockingIndexingTermination(
            final ClosableCompletionService<List<IndexedElementDetails>> completionService,
            boolean logIndexedElementDetails) {
        if (LOG.isInfoEnabled()) {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ServerInitializer.inheritVeriniceContextState();
                    awaitIndexingTermination(completionService, logIndexedElementDetails);
                    printIndexingTimeConsumption();
                }
            });
            executor.shutdown();
        }
    }

    private void printIndexingTimeConsumption() {
        long end = System.currentTimeMillis();
        long ms = end - indexingStart;
        LOG.info("Index created, runtime: " + TimeFormatter.getHumanRedableTime(ms));
    }

    /**
     * Creates an elastic search in a blocking manner. After this method is
     * finished the index will be available.
     *
     * <p>
     * *Note*: The indexing is done concurrently, so it is still fast. The only
     * restriction of this blocking method is, that is waiting until the last
     * {@link IndexThread} is completed.
     * </p>
     *
     * <p>
     * If you want to test indexing with junit, this is the method to go.
     * </p>
     *
     */
    public void blockingIndexing() {
        try {
            doBlockingIndexing();
        } catch (Exception e) {
            LOG.error("blocking indexing failed: " + e.getLocalizedMessage(), e);
        }
    }

    private void doBlockingIndexing() {

        ServerInitializer.inheritVeriniceContextState();
        boolean logIndexedElementDetails = LOG.isDebugEnabled();
        ClosableCompletionService<List<IndexedElementDetails>> completionService = doIndex(
                logIndexedElementDetails);

        // This call causes the blocking since it takes every completed task
        // from the executor queue.
        awaitIndexingTermination(completionService, logIndexedElementDetails);

        printIndexingTimeConsumption();
    }

    private static void awaitIndexingTermination(
            ClosableCompletionService<List<IndexedElementDetails>> completionService,
            boolean logIndexedElementDetails) {
        while (!completionService.isClosed()) {
            try {
                Future<List<IndexedElementDetails>> future = completionService.poll(500l,
                        TimeUnit.MILLISECONDS);
                if (logIndexedElementDetails && future != null) {
                    // if the last element was removed from the queue in the
                    // previous iteration and the current iteration started
                    // before the executor could properly terminate, the queue
                    // will be empty. We should be able to exit from the loop
                    // after the current iteration.
                    List<IndexedElementDetails> elements = future.get();
                    for (IndexedElementDetails details : elements) {
                        LOG.debug("element was indexed " + details.getTitle() + " - uuid "
                                + details.getUuid());
                    }
                }
            } catch (Exception e) {
                LOG.error("Indexing failed for an element", e);
            }
        }
    }

    private Collection<IndexThread> createIndexThreadsByIDs(List<String> allIDs,
            boolean logIndexedElementDetails) {
        List<List<String>> chunks = Lists.partition(allIDs, INDEXING_CHUNK_SIZE);
        Collection<IndexThread> indexThreads = new ArrayList<>(chunks.size());
        for (List<String> chunk : chunks) {
            IndexThread indexThread = (IndexThread) indexThreadFactory.getObject();
            indexThread.setIDs(chunk);
            indexThread.setReturnIndexedElementDetails(logIndexedElementDetails);
            indexThreads.add(indexThread);
        }
        return indexThreads;
    }

    private final class DummyAuthenticationRunnableExtension extends DummyAuthenticationRunnable {
        @Override
        public void doRun() {
            try {
                boolean logIndexedElementDetails = LOG.isDebugEnabled();
                ClosableCompletionService<List<IndexedElementDetails>> completionService = doIndex(
                        logIndexedElementDetails);
                logNonBlockingIndexingTermination(completionService, logIndexedElementDetails);
            } catch (Exception e) {
                LOG.error("Error while indexing elements.", e);
            }
        }
    }

    public ObjectFactory getIndexThreadFactory() {
        return indexThreadFactory;
    }

    public void setIndexThreadFactory(ObjectFactory indexThreadFactory) {
        this.indexThreadFactory = indexThreadFactory;
    }

    public IBaseDao<CnATreeElement, Integer> getElementDao() {
        return elementDao;
    }

    public void setElementDao(IBaseDao<CnATreeElement, Integer> elementDao) {
        this.elementDao = elementDao;
    }

    public IElementTitleCache getTitleCache() {
        return titleCache;
    }

    public void setTitleCache(IElementTitleCache titleCache) {
        this.titleCache = titleCache;
    }
}
