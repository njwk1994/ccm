package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 13:59
 */
public interface ICCMDocumentService {

    /**
     * 获取所有图纸
     *
     * @param pageRequest
     * @return
     */
    IObjectCollection getDocumentsWithPage(PageRequest pageRequest) throws Exception;

    /**
     * 获取所有图纸
     *
     * @param pageRequest
     * @return
     */
    IObjectCollection getAllDocumentsWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception;

    /**
     * 获取所有设计图纸
     *
     * @param documentId 设计图纸ID
     * @return 设计图纸
     * @throws Exception
     */
    IObject getDocumentByOBID(String documentId) throws Exception;

    /**
     * 根据用逗号分割的图纸OBID获取所有图纸
     *
     * @param documentOBIDs
     * @return
     * @throws Exception
     */
    IObjectCollection getDocumentByOBIDs(String documentOBIDs) throws Exception;
}
