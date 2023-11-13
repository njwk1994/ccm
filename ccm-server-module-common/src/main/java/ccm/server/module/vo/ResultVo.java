package ccm.server.module.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
public class ResultVo<T> implements Serializable {
    private static final long serializableId = 1L;
    private int code;

    private String message;

    private int total;

    private int pageIndex;

    private boolean success;

    private T result;

    public ResultVo<T> success(T result) {
        ResultVo<T> resultVo = new ResultVo<T>();
        resultVo.result = result;
        resultVo.success = true;
        resultVo.code = 200;
        resultVo.message = "成功!";
        return resultVo;
    }

    public void successResult(T result) {
        this.message = "成功!";
        this.result = result;
        this.code = 200;
        this.success = true;
    }

    public void errorResult(String message) {
        this.message = message;
        this.code = 200;
        this.success = false;
    }

    public ResultVo<T> error(String message) {
        ResultVo<T> resultVo = new ResultVo<>();
        resultVo.success = false;
        resultVo.code = 500;
        resultVo.message = message;
        return resultVo;
    }

}
