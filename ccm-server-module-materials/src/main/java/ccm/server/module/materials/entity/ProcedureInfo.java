package ccm.server.module.materials.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 存储过程类
 *
 * @author HuangTao
 * @version 1.0
 * @since 2021/10/14 15:24
 */
public class ProcedureInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存储过程方法 doProcedure() doProcedure(?,?,?)
     */
    private String procedure;

    /**
     * 条件List
     */
    private List<ParamInfo> params = new ArrayList<ParamInfo>();

    /**
     * 是否为带参存储过程
     *
     * @return
     */
    public boolean withParam() {
        return params.size() > 0;
    }

    public ProcedureInfo() {
    }

    public ProcedureInfo(String procedure) {
        this.procedure = procedure;
    }

    public ProcedureInfo(String procedure, List<ParamInfo> params) {
        this.procedure = procedure;
        this.params = params;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public List<ParamInfo> getParams() {
        return params;
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }
}
