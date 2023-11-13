package ccm.server.model;

import ccm.server.enums.ExpansionMode;
import ccm.server.enums.orderMode;
import ccm.server.util.CommonUtility;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Data
public class OrderByWrapper {
    private orderMode orderMode = ccm.server.enums.orderMode.asc;
    private String identity;
    private ExpansionMode expansionMode = ExpansionMode.none;

    //relationship->>>+/-XXXXX.XX<<>>asc
    //none->>>XX<<>>asc
    //relatedObject->>>+/-XXXXX.XX<<>>asc
    public OrderByWrapper(String orderParam) {
        if (!StringUtils.isEmpty(orderParam)) {
            String[] strings = orderParam.split("<<>>");
            if (strings.length == 1) {
                // not order info provided so set asc mode as default
                this.setOrderMode(ccm.server.enums.orderMode.asc);
                //try to set identity and expansion mode
                this.setExpansionModeAndIdentity(orderParam);
            } else {
                //set order mode with specified string
                String orderModeWrapper = strings[1];
                this.translateToOrderMode(orderModeWrapper);
                this.setExpansionModeAndIdentity(strings[0]);
            }
        }
    }

    protected void onSetIdentityAndExpansionMode(String identity) {
        String[] strings = identity.split("\\.");
        if (strings.length == 1) {
            if (identity.startsWith("+") || identity.startsWith("-")) {
                //start with direction character so set expansion mode to be related object
                this.setExpansionMode(ExpansionMode.relatedObject);
                //to set get related object's Name as default in case of missing it
                this.setIdentity(identity + ".Name");
            } else {
                // no direction character found so set expansion to be none as self-property
                this.setExpansionMode(ExpansionMode.none);
                //keep identity as it is
                this.setIdentity(identity);
            }
        } else {
            //. as connection character for combination of relationship definition with specified property definition
            this.setExpansionMode(ExpansionMode.relatedObject);
            //if start with direction character then keep identity as it is, otherwise use + as default direction character
            if (identity.startsWith("+") || identity.startsWith("-"))
                this.setIdentity(identity);
            else
                this.setIdentity("+" + identity);
        }
    }

    protected void setExpansionModeAndIdentity(String expansionModePlusIdentity) {
        if (!StringUtils.isEmpty(expansionModePlusIdentity)) {
            String[] strings = expansionModePlusIdentity.split("->>>");
            if (strings.length == 1) {
                this.onSetIdentityAndExpansionMode(expansionModePlusIdentity);
            } else {
                this.onSetIdentityAndExpansionMode(strings[1]);
                //force to set expansion mode with provided info
                this.setExpansionMode(ExpansionMode.valueOf(strings[0]));
            }
        }
    }

    public LiteCriteria liteCriteria() {
        return CommonUtility.getLiteCriteria(this.expansionMode, this.identity);
    }

    public void setOrderMode(orderMode orderMode) {
        this.orderMode = orderMode;
    }

    public void setOrderMode(Boolean ascOrNot) {
        if (ascOrNot == null)
            this.orderMode = ccm.server.enums.orderMode.none;
        else if (ascOrNot)
            this.orderMode = ccm.server.enums.orderMode.asc;
        else
            this.orderMode = ccm.server.enums.orderMode.desc;
    }

    protected void translateToOrderMode(String orderModeWrapper) {
        if (StringUtils.isEmpty(orderModeWrapper))
            this.orderMode = ccm.server.enums.orderMode.none;
        else {
            orderModeWrapper = orderModeWrapper.toUpperCase(Locale.ROOT);
            if (orderModeWrapper.startsWith("ASC") || orderModeWrapper.startsWith("A"))
                this.orderMode = ccm.server.enums.orderMode.asc;
            else if (orderModeWrapper.startsWith("DESC") || orderModeWrapper.startsWith("D"))
                this.orderMode = ccm.server.enums.orderMode.desc;
            else if (orderModeWrapper.startsWith("NULL"))
                this.orderMode = ccm.server.enums.orderMode.none;
            else if (orderModeWrapper.startsWith("TRUE") || orderModeWrapper.startsWith("T"))
                this.orderMode = ccm.server.enums.orderMode.asc;
            else if (orderModeWrapper.startsWith("FALSE") || orderModeWrapper.startsWith("F"))
                this.orderMode = ccm.server.enums.orderMode.desc;
        }
    }

    public OrderByWrapper(orderMode orderMode, String expansionModePlusIdentity) {
        this.orderMode = orderMode;
        if (this.orderMode == null)
            this.orderMode = ccm.server.enums.orderMode.none;
        this.setExpansionModeAndIdentity(expansionModePlusIdentity);
    }

    public OrderByWrapper(orderMode orderMode, String identity, ExpansionMode expansionMode) {
        this.orderMode = orderMode;
        if (this.orderMode == null)
            this.orderMode = ccm.server.enums.orderMode.none;
        this.identity = identity;
        this.expansionMode = expansionMode;
    }

    public String expansionModePlusIdentity() {
        return this.expansionMode.toString() + "->>>" + this.identity;
    }


    @Override
    public String toString() {
        return this.expansionMode.toString() + "->>>" + this.identity;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
