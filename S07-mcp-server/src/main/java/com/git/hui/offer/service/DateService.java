package com.git.hui.offer.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author YiHui
 * @date 2025/7/27
 */
@Service
public class DateService {

    private static final String TASK_API_URL = "http://139.159.221.11:9002/qyplmapi/udscfg-plm/task/retrieveTaskList";
    private static final String AUTH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2YzgyMTM2ZTg0ZGM0ZjBkYmY4OTFiZWNkMDRiODA1ZiIsInN1YiI6IjIwMDAwNzQ3NzU1MTk0Mjg2MTAiLCJpc3MiOiJodWFuZyIsImlhdCI6MTc3OTI0MDA2MCwidXNlck5hbWUiOiJyZW4uamlhbmciLCJmdWxsTmFtZSI6IuWnnOmfpyIsImV4cCI6MTc3OTI4MzI2MH0.6eclQdAdL-ayIeeI7-5kAX_0GFCo9XO3uW96SVrm0nA";

    private final RestTemplate restTemplate = new RestTemplate();

    @Tool(description = "传入时区，返回对应时区的当前时间给用户")
    public String getTimeByZoneId(@ToolParam(description = "需要查询时间的时区") ZoneId area) {
        // 根据系统当前时间，获取指定时区的时间
        ZonedDateTime time = ZonedDateTime.now(area);

        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ans = time.format(formatter);
        System.out.println("传入的时区是：" + area + "-" + ans);
        return ans;
    }

    @Tool(description = "从PLM系统查询任务列表，支持分页查询")
    public String retrieveTaskList(
            @ToolParam(description = "页码，从1开始") int pageNum,
            @ToolParam(description = "每页条数") int pageCount) {

        System.out.println("[🔨] 查询任务列表，pageNum=" + pageNum + ", pageCount=" + pageCount);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(TASK_API_URL)
                    .queryParam("pageCount", pageCount)
                    .queryParam("pageNum", pageNum)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", AUTH_TOKEN);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 任务列表查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询任务列表失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统查询产品列表，支持分页和搜索")
    public String retrieveProductList(
            @ToolParam(description = "搜索文本，可为空") String searchText,
            @ToolParam(description = "分组代码，可为空") String groupCodes,
            @ToolParam(description = "页码，从1开始") int pageIndex,
            @ToolParam(description = "每页条数") int pageSize) {

        System.out.println("[🔨] 查询产品列表，searchText=" + searchText + ", groupCodes=" + groupCodes + ", pageIndex=" + pageIndex + ", pageSize=" + pageSize);

        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://139.159.221.11:9002/qyplmapi/-param/api/Product")
                    .queryParam("searchText", searchText != null ? searchText : "")
                    .queryParam("groupCodes", groupCodes != null ? groupCodes : "")
                    .queryParam("pageIndex", pageIndex)
                    .queryParam("pageSize", pageSize)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", AUTH_TOKEN);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 产品列表查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询产品列表失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "获取产品下的参数列表，需要先查询产品版本，再根据版本ID获取参数")
    public String retrieveProductParams(
            @ToolParam(description = "产品编码，如 PD00002") String productCode,
            @ToolParam(description = "搜索文本，可为空") String searchText,
            @ToolParam(description = "分组代码，可为空") String groupCodes,
            @ToolParam(description = "页码，从1开始") int pageIndex,
            @ToolParam(description = "每页条数") int pageSize) {

        System.out.println("[🔨] 查询产品参数，productCode=" + productCode);

        try {
            // 第一步：获取产品版本信息
            String configUrl = UriComponentsBuilder.fromHttpUrl("http://139.159.221.11:9002/qyplmapi/-param/api/ConfigBank")
                    .queryParam("productCode", productCode)
                    .queryParam("pageIndex", 1)
                    .queryParam("pageSize", 50)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", AUTH_TOKEN);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<String> configResponse = restTemplate.exchange(
                    configUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String configResult = configResponse.getBody();
            System.out.println("[✅] 产品版本信息查询成功");

            // 解析版本ID（简化处理，实际应该使用 JSON 解析）
            String versionId = extractVersionId(configResult);
            if (versionId == null) {
                return "未找到产品版本信息";
            }
            System.out.println("[ℹ️] 获取到版本ID: " + versionId);

            // 第二步：根据版本ID获取参数列表
            String paramUrl = UriComponentsBuilder.fromHttpUrl(
                            "http://139.159.221.11:9002/qyplmapi/-param/api/ConfigBank/" + versionId + "/Param")
                    .queryParam("searchText", searchText != null ? searchText : "")
                    .queryParam("groupCodes", groupCodes != null ? groupCodes : "")
                    .queryParam("pageIndex", pageIndex)
                    .queryParam("pageSize", pageSize)
                    .toUriString();

            ResponseEntity<String> paramResponse = restTemplate.exchange(
                    paramUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String result = paramResponse.getBody();
            System.out.println("[✅] 产品参数列表查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询产品参数失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    /**
     * 从版本信息响应中提取第一个版本数据的ID
     */
    private String extractVersionId(String configResult) {
        if (configResult == null || configResult.isEmpty()) {
            return null;
        }
        try {
            // 简单字符串提取，查找第一个 "id": "xxx" 的模式
            int idIndex = configResult.indexOf("\"id\":");
            if (idIndex == -1) {
                return null;
            }
            int start = configResult.indexOf("\"", idIndex + 5);
            int end = configResult.indexOf("\"", start + 1);
            if (start != -1 && end != -1) {
                return configResult.substring(start + 1, end);
            }
        } catch (Exception e) {
            System.err.println("解析版本ID失败: " + e.getMessage());
        }
        return null;
    }
}
