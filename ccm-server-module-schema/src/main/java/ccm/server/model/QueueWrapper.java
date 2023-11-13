package ccm.server.model;

import ccm.server.enums.deferredMethodType;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.IObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class QueueWrapper {
    private deferredMethodType deferredMethodType;
    private final List<IObjectCollection> queues = new ArrayList<>();
    private final HashSet<IObject> processedItems = new HashSet<>();
    private long size = 0;
    private final int queueSize = 10000;

    public QueueWrapper(ConcurrentLinkedQueue<IObject> sourceItems, deferredMethodType deferredMethodType) {
        this.deferredMethodType = deferredMethodType;
        this.init(sourceItems);
    }

    private void init(ConcurrentLinkedQueue<IObject> sourceItems) {
        if (sourceItems != null) {
            int index = 0;
            IObjectCollection queueItems = null;
            while (sourceItems.size() > 0) {
                IObject current = sourceItems.poll();
                this.size++;
                if (!this.processedItems.contains(current)) {
                    this.processedItems.add(current);
                    index++;
                    if (queueItems == null)
                        queueItems = new ObjectCollection();
                    queueItems.append(current);
                } else {
                    switch (this.deferredMethodType) {
                        case Process:
                            current.ClassBase().ProcessMethods().clear();
                            break;
                        case Completed:
                            current.ClassBase().CompletedMethods().clear();
                            break;
                        case Processed:
                            current.ClassBase().ProcessedMethods().clear();
                            break;
                        case Processing:
                            current.ClassBase().CancelMethods().clear();
                            break;
                        case PreProcess:
                            current.ClassBase().PreProcessMethods().clear();
                            break;
                        case PostProcessed:
                            current.ClassBase().PostProcessedMethods().clear();
                            break;
                    }
                }
                if (index == this.queueSize || sourceItems.size() == 0) {
                    index = 0;
                    this.queues.add(queueItems);
                    queueItems = null;
                }
            }
        }
    }
}
