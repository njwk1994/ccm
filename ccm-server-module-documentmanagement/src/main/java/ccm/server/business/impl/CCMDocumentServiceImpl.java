package ccm.server.business.impl;

import ccm.server.business.ICCMDocumentService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.operator;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.DocumentUtils;
import ccm.server.utils.PageUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:00
 */
@Service
public class CCMDocumentServiceImpl implements ICCMDocumentService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /**
     * 获取所有图纸
     *
     * @param pageRequest
     * @return
     */
    @Override
    public IObjectCollection getDocumentsWithPage(PageRequest pageRequest) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DocumentUtils.I_DOCUMENT);
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    /**
     * 获取所有图纸
     *
     * @param items
     * @param pageRequest
     * @return
     */
    @Override
    public IObjectCollection getAllDocumentsWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DocumentUtils.I_DOCUMENT);
        // 添加条件
        for (ObjectItemDTO item : items.getItems()) {
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, "",
                    item.getDefUID(), operator.equal, item.getDisplayValue().toString());
        }
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    /**
     * 根据设计图纸ID获取设计图纸
     *
     * @param documentOBID 设计图纸OBID
     * @return 设计图纸
     * @throws Exception
     */
    @Override
    public IObject getDocumentByOBID(String documentOBID) throws Exception {
        if (StringUtils.isBlank(documentOBID)) {
            throw new Exception("图纸OBID不可为空!");
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DocumentUtils.I_DOCUMENT);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, documentOBID);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    /**
     * 根据用逗号分割的图纸OBID获取所有图纸
     *
     * @param documentOBIDs
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getDocumentByOBIDs(String documentOBIDs) throws Exception {
        if (StringUtils.isEmpty(documentOBIDs)) {
            throw new Exception("图纸OBID不可为空!");
        }
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = iQueryEngine.start();
        iQueryEngine.addInterfaceForQuery(queryRequest, DocumentUtils.I_DOCUMENT);
        iQueryEngine.addOBIDForQuery(queryRequest, operator.in, documentOBIDs);
        return iQueryEngine.query(queryRequest);
    }
}
