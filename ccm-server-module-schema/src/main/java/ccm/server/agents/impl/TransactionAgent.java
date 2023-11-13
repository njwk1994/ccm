package ccm.server.agents.impl;

import ccm.server.context.DBContext;
import ccm.server.module.service.base.IService;
import ccm.server.shared.ISharedLocalService;
import ccm.server.transactions.ITransaction;
import ccm.server.util.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service()
@Slf4j
public class TransactionAgent {
    private final static ThreadLocal<ITransaction> transactionThreadLocal = new ThreadLocal<>();
    @Autowired
    private DBContext dbContext;
    @Autowired
    private ISharedLocalService sharedLocalService;
    @Autowired
    private List<ITransaction> transactionList;

    public ITransaction currentTransaction() {
        if (transactionThreadLocal.get() != null)
            return transactionThreadLocal.get();
        if (CommonUtility.hasValue(transactionList)) {
            int integer = transactionList.stream().map(IService::getPriority).findFirst().orElse(-1);
            if (integer > -1) {
                ITransaction transaction = transactionList.stream().filter(c -> c.getPriority() == integer).findFirst().orElse(null);
                ITransaction iTransaction = transaction.instantiate();
                iTransaction.setService(this.dbContext);
                iTransaction.setService(this.sharedLocalService);
                transactionThreadLocal.set(iTransaction);
                return iTransaction;
            }
        }
        return null;
    }
}
