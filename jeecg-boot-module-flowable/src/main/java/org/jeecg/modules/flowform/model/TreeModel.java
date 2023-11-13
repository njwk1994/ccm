package org.jeecg.modules.flowform.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TreeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String key;

    private Slot slots;

    public Slot getSlots() {
        return new Slot();
    }

    private List<TreeModel> children;

    @Data
    private class Slot {

        private String icon;

        public String getIcon() {
            return "default";
        }
    }
}


