package ccm.server.schema.interfaces.generated;

import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.seqType;
import ccm.server.schema.interfaces.ICIMRevisionScheme;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public abstract class ICIMRevisionSchemeBase extends InterfaceDefault implements ICIMRevisionScheme {

    public ICIMRevisionSchemeBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMRevisionScheme.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMMajorSequence(String majorSequence) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequence.toString(), majorSequence);
    }

    @Override
    public String getCIMMajorSequence() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequence.toString());
        return property != null ? property.Value().toString() : "";
    }

    @Override
    public void setCIMMajorSequenceMinLength(Integer minLength) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequenceMinLength.toString(), minLength);
    }

    @Override
    public Integer getCIMMajorSequenceMinLength() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequenceMinLength.toString());
        return property != null ? Integer.parseInt(property.Value().toString()) : 0;
    }

    @Override
    public void setCIMMajorSequencePadChar(String padChar) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequencePadChar.toString(), padChar);
    }

    @Override
    public String getCIMMajorSequencePadChar() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequencePadChar.toString());
        return property != null ? property.Value().toString() : "";
    }

    @Override
    public void setCIMMajorSequenceType(seqType sequenceType) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequenceType.toString(), sequenceType.toString());
    }

    @Override
    public seqType getCIMMajorSequenceType() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMajorSequenceType.toString());
        return property != null ? seqType.valueOf(property.Value().toString()) : seqType.EN_Numeric;
    }

    @Override
    public void setCIMMinorSequence(String minorSequence) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequence.toString(), minorSequence);
    }

    @Override
    public String getCIMMinorSequence() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequence.toString());
        return property != null ? property.Value().toString() : "";
    }

    @Override
    public void setCIMMinorSequenceMinLength(Integer sequenceMinLength) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequenceMinLength.toString(), sequenceMinLength);
    }

    @Override
    public Integer getCIMMinorSequenceMinLength() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequenceMinLength.toString());
        return property != null ? Integer.parseInt(property.Value().toString()) : 0;
    }

    @Override
    public void setCIMMinorSequencePadChar(String minorSequencePadChar) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequencePadChar.toString(), minorSequencePadChar);
    }

    @Override
    public String getCIMMinorSequencePadChar() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequencePadChar.toString());
        return property != null ? property.Value().toString() : "";
    }

    @Override
    public void setCIMMinorSequenceType(seqType minorSequenceType) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequenceType.toString(), minorSequenceType.toString());
    }

    @Override
    public seqType getCIMMinorSequenceType() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionScheme.toString(), propertyDefinitionType.CIMMinorSequenceType.toString());
        return property != null ? seqType.valueOf(property.Value().toString()) : seqType.EN_Numeric;
    }

    @Override
    public String getNextMajorRevisionValue(String pstrCurrMajorRevision) throws Exception {
        String result = "";
        if (this.getCIMMajorSequenceType().equals(seqType.EN_Numeric)) {
            result = this.getNextNumericSeqNo(pstrCurrMajorRevision, getCIMMajorSequencePadChar(), getCIMMajorSequenceMinLength(), true);
        } else {
            result = this.getNextSequenceNo(pstrCurrMajorRevision, getCIMMajorSequence().split(","));
        }
        return result;
    }

    @Override
    public String getNextNumericSeqNo(String pstrCurrentRevision, String pstrPadChar, Integer pintMinLen, Boolean pblnMajor) {
        Integer num = null;
        if (StringUtils.isEmpty(pstrCurrentRevision)) {
            num = pblnMajor ? ((StringUtils.isEmpty(getCIMMajorSequence()) ? 1 : Integer.parseInt(getCIMMajorSequence()))) : ((StringUtils.isEmpty(getCIMMinorSequence()) ? 1 : Integer.parseInt(getCIMMinorSequence())));
        } else {
            num = Integer.parseInt(pstrCurrentRevision);
            num = num + 1;
        }
        if (num.toString().length() < pintMinLen) {
            if (!StringUtils.isEmpty(pstrPadChar)) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < pintMinLen - num.toString().length(); i++) {
                    stringBuilder.append(pstrPadChar);
                }
                stringBuilder.append(num.toString());
                return stringBuilder.toString();
            }
        }
        return num.toString();
    }

    @Override
    public String getNextSequenceNo(String pstrCurrentRevision, String[] parrRevSeq) throws Exception {
        if (StringUtils.isEmpty(pstrCurrentRevision)) return parrRevSeq[0];
        int index = Arrays.asList(parrRevSeq).indexOf(pstrCurrentRevision);
        if (index == parrRevSeq.length - 1) {
            throw new Exception("已经到达序列最后位置,没有下一个元素可以使用!");
        }
        return parrRevSeq[index + 1];
    }

    @Override
    public String getNextMinorRevisionValue(String pstrCurrMinorRevision) throws Exception {
        String result = "";
        if (this.getCIMMinorSequenceType().equals(seqType.EN_Numeric)) {
            result = this.getNextNumericSeqNo(pstrCurrMinorRevision, getCIMMinorSequencePadChar(), getCIMMinorSequenceMinLength(), false);
        } else {
            result = this.getNextSequenceNo(pstrCurrMinorRevision, getCIMMinorSequence().split(","));
        }
        return result;
    }

    @Override
    public String getFirstSequenceNo(Boolean pblnMajor) {
        return pblnMajor ? getCIMMajorSequence().split(",")[0].trim() : getCIMMinorSequence().split(",")[0].trim();
    }

    @Override
    public String getFirstRevisionValue(Boolean pblnMajor) {
        if (pblnMajor) {
            switch (getCIMMajorSequenceType()) {
                case EN_Sequence:
                    return this.getFirstSequenceNo(true);
                case EN_Numeric:
                    return this.getFirstNumericSeqNo(true);
                default:
                    return null;
            }
        } else {
            switch (getCIMMinorSequenceType()) {
                case EN_Numeric:
                    return this.getFirstNumericSeqNo(false);
                case EN_Sequence:
                    return this.getFirstSequenceNo(false);
                default:
                    return null;
            }
        }
    }

    @Override
    public String getFirstNumericSeqNo(Boolean pblnMajor) {
        int num = pblnMajor ? (StringUtils.isEmpty(getCIMMajorSequence()) ? 1 : Integer.parseInt(getCIMMajorSequence())) : ((StringUtils.isEmpty(getCIMMinorSequence()) ? 1 : Integer.parseInt(getCIMMinorSequence())));
        String lstrPadChar = pblnMajor ? getCIMMajorSequencePadChar() : getCIMMinorSequencePadChar();
        Integer lintMinLen = pblnMajor ? getCIMMajorSequenceMinLength() : getCIMMinorSequenceMinLength();
        if (Integer.toString(num).length() < lintMinLen) {
            if (!StringUtils.isEmpty(lstrPadChar)) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < lintMinLen - Integer.toString(num).length(); i++) {
                    stringBuilder.append(lstrPadChar);
                }
                stringBuilder.append(Integer.toString(num));
                return stringBuilder.toString();
            }
        }
        return Integer.toString(num);
    }

    @Override
    public Boolean isMinorOrMajorRevSupported(String pstrRev, Boolean pblnMajor) {
        if (StringUtils.isEmpty(pstrRev)) return true;
        seqType seqType = pblnMajor ? getCIMMajorSequenceType() : getCIMMinorSequenceType();
        Integer lintMinLen = pblnMajor ? getCIMMajorSequenceMinLength() : getCIMMinorSequenceMinLength();
        String seq = pblnMajor ? getCIMMajorSequence() : getCIMMinorSequence();
        if (seqType != null) {
            switch (seqType) {
                case EN_Numeric:
                    try {
                        int i = Integer.parseInt(pstrRev);
                        if (pstrRev.length() >= lintMinLen) return true;
                    } catch (Exception ex) {
                        return false;
                    }
                case EN_Sequence:
                    if (StringUtils.isEmpty(seq)) return false;
                    if (Arrays.asList(seq.split(",")).contains(pstrRev)) return true;
            }
        }
        return false;
    }
}
