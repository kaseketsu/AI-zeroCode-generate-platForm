package com.itflower.aiplatform.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkdownParser {


    public static List<Map<String, String>> parseMarkdownTables(String markdown) {
        List<Map<String, String>> result = new ArrayList<>();

        if (markdown == null || markdown.trim().isEmpty()) {
            return result;
        }

        String normalized = markdown.replace("\r\n", "\n").replace("\r", "\n").replace("\\n", "\n");
        String[] lines = normalized.split("\\R");

        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            // 尝试识别表头行：以 | 开头，且下一行是分隔行
            if (isPotentialHeaderLine(line) && i + 1 < lines.length && isSeparatorLine(lines[i + 1])) {
                // 解析表头
                List<String> headers = splitRow(line);
                // 跳过分隔行
                i += 2;
                // 读取表格体部分
                while (i < lines.length && isTableBodyLine(lines[i])) {
                    List<String> cells = splitRow(lines[i]);
                    Map<String, String> rowMap = buildRowMap(headers, cells);
                    result.add(rowMap);
                    i++;
                }
                // 继续下一行循环，不再额外自增
                continue;
            }
            // 非表格行，直接跳过
            i++;
        }

        return result;
    }

    /**
     * 判断是否可能是表头行：以 | 开头且包含至少一个 |。
     */
    private static boolean isPotentialHeaderLine(String line) {
        if (line == null) {
            return false;
        }
        String trimmed = line.trim();
        return trimmed.startsWith("|") && trimmed.contains("|");
    }

    /**
     * 判断是否是表格分隔行，如：
     * | --- | --- | 或 | :--- | ---: | :-: |
     * 简单规则：行中只能包含 '|', '-', ':', ' ' 这几种字符。
     */
    private static boolean isSeparatorLine(String line) {
        if (line == null) {
            return false;
        }
        String trimmed = line.trim();
        if (!trimmed.startsWith("|")) {
            return false;
        }
        // 只要包含除 '|', '-', ':', ' ' 之外的字符，就不是分隔行
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c != '|' && c != '-' && c != ':' && !Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否是表格体的行：以 | 开头即可。
     */
    private static boolean isTableBodyLine(String line) {
        if (line == null) {
            return false;
        }
        String trimmed = line.trim();
        return trimmed.startsWith("|");
    }

    /**
     * 按 markdown 表格规则拆分一行：
     * 1. 去掉首尾的竖线
     * 2. 按竖线分割
     * 3. 去掉每个单元格两端空白
     */
    private static List<String> splitRow(String line) {
        List<String> cells = new ArrayList<>();
        if (line == null) {
            return cells;
        }

        String trimmed = line.trim();

        // 去掉首尾的 |（如果存在）
        if (trimmed.startsWith("|")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("|")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }

        String[] parts = trimmed.split("\\|", -1);
        for (String part : parts) {
            cells.add(part.trim());
        }

        return cells;
    }

    /**
     * 根据表头和单元格构建一行的 Map，长度不足时用空串补齐，多余单元格忽略。
     */
    private static Map<String, String> buildRowMap(List<String> headers, List<String> cells) {
        Map<String, String> map = new HashMap<>();
        if (headers == null || headers.isEmpty()) {
            return map;
        }
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            String header = headers.get(i);
            String value = "";
            if (cells != null && i < cells.size()) {
                value = cells.get(i);
            }
            if (header == null) {
                header = "";
            }
            map.put(header, value);
        }
        return map;
    }
}




