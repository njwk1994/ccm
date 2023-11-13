package ccm.server.excel.entity;

import lombok.Data;

import java.util.List;

@Data
public class ExcelDataContent {

    private String sheetName;

    private List<String> headerList;

    private List<List<String>> content;

    public ExcelDataContent() {
    }

    public ExcelDataContent(String sheetName, List<String> headerList, List<List<String>> content) {
        this.content = content;
        this.headerList = headerList;
        this.sheetName = sheetName;
    }
}
