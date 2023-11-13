package org.jeecg.modules.flowform.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DicItemModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dictName;

    private String dictCode;

    private String itemText;

    private String itemValue;
}
