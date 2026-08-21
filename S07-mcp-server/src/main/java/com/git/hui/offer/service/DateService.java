package com.git.hui.offer.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YiHui
 * @date 2025/7/27
 */
@Service
public class DateService {

    private String authToken;

    @Value("${qyplm.base-url:http://139.159.221.11:9002}")
    private String baseUrl;

    private final Environment environment;
    private final ObjectMapper objectMapper;
    private Map<String, String> plmPages = new HashMap<>();

    public DateService(Environment environment, ObjectMapper objectMapper) {
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initPlmPages() {
        List<String> pageJsonList = Binder.get(environment)
                .bind("qyplm.pages", Bindable.listOf(String.class))
                .orElse(new ArrayList<>());
        for (String json : pageJsonList) {
            try {
                Map<String, String> map = objectMapper.readValue(json, Map.class);
                plmPages.putAll(map);
            } catch (Exception e) {
                System.err.println("解析页面配置失败: " + json + ", " + e.getMessage());
            }
        }

        refreshAuthToken();

    }

    private final RestTemplate restTemplate = new RestTemplate();

    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Tool(description = "更新参数值，Map的key为参数id，value为要更新的参数值")
    public String updateParamData(@ToolParam(description = "参数Map") Map<String, String> paramMap) {
        System.out.println("[🔨] 更新参数值，paramMap=" + JsonParser.toJson(paramMap));

        try {
            if (paramMap == null || paramMap.isEmpty()) {
                return "参数Map不能为空";
            }
            List<Map<String, Object>> paramList = new ArrayList<>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", entry.getKey());
                item.put("value", entry.getValue());
                paramList.add(item);
            }
            String requestBody = objectMapper.writeValueAsString(paramList);

            String url = baseUrl + "/qyplmapi/udscfg-plm/task/demandParseParam/updateParamValue";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", authToken)
                    .post(RequestBody.create(requestBody, MediaType.get("application/json; charset=utf-8")))
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("[❌] 更新参数值失败，HTTP状态码: " + response.code());
                    return "更新参数值失败，HTTP状态码: " + response.code();
                }
                ResponseBody body = response.body();
                String content = body != null ? body.string() : "";
                System.out.println("[✅] 参数值更新成功");
                return content;
            }
        } catch (Exception e) {
            System.err.println("[❌] 更新参数值失败: " + e.getMessage());
            return "更新参数值失败: " + e.getMessage();
        }
    }

    @Tool(description = "更新提示信息，每个元素为JSON对象，需包含guid(提示信息主键)/paramId(参数名)/msg(提示内容)")
    public String updateParamTips(@ToolParam(description = "参数提示信息") List<String> tips) {
        System.out.println("[🔨] 更新提示信息，tips=" + JsonParser.toJson(tips));

        return "提示信息更新成功";
    }

    @Tool(description = "传入数据ID，获取参数详情")
    public String getParamMsg(@ToolParam(description = "数据ID") String dataID) {
        System.out.println("[🔨] 获取参数详情，dataID=" + dataID);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/udscfg-plm/task/demandParseParam/getParamMsg")
                    .queryParam("id", dataID)
                    .toUriString();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", authToken)
                    .get()
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("[❌] 获取参数详情失败，HTTP状态码: " + response.code());
                    return "获取参数详情失败，HTTP状态码: " + response.code();
                }
                ResponseBody body = response.body();
                String content = body != null ? body.string() : "";
                System.out.println("[✅] 参数详情获取成功");
                return formatParamMsg(content);
            }
        } catch (Exception e) {
            System.err.println("[❌] 获取参数详情失败: " + e.getMessage());
            return "获取参数详情失败: " + e.getMessage();
        }
    }

    @Tool(description = "传入数据ID，获取文档内容")
    public String getDoc(@ToolParam(description = "数据ID") String dataID) {
        System.out.println("[🔨] 获取文档内容，dataID=" + dataID);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/udscfg-plm/task/demandParseParam/getDoc")
                    .queryParam("id", dataID)
                    .toUriString();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", authToken)
                    .get()
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("[❌] 获取文档失败，HTTP状态码: " + response.code());
                    return "获取文档失败，HTTP状态码: " + response.code();
                }
                ResponseBody body = response.body();
                String content = body != null ? body.string() : "";
                System.out.println("[✅] 文档获取成功");
                return content;
            }
        } catch (Exception e) {
            System.err.println("[❌] 获取文档失败: " + e.getMessage());
            return "获取文档失败: " + e.getMessage();
        }
    }

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

    @Tool(description = "获取PLM系统所有页面的链接")
    public String getPlmPageUrl() {
        try {
            return objectMapper.writeValueAsString(plmPages);
        } catch (Exception e) {
            return "序列化页面链接失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统查询配置管理模块的任务列表，支持分页查询")
    public String retrieveTaskList(
            @ToolParam(description = "页码，从1开始") int pageNum,
            @ToolParam(description = "每页条数") int pageCount) {

        System.out.println("[🔨] 查询任务列表，pageNum=" + pageNum + ", pageCount=" + pageCount);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/udscfg-plm/task/retrieveTaskList")
                    .queryParam("pageCount", pageCount)
                    .queryParam("pageNum", pageNum)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
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

    @Tool(description = "从PLM系统获取AI计价模块的产品列表，支持分页")
    public String retrieveProductList(
            @ToolParam(description = "页码，从1开始") int pageIndex,
            @ToolParam(description = "每页条数") int pageSize) {

        System.out.println("[🔨] 查询产品列表，pageIndex=" + pageIndex + ", pageSize=" + pageSize);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/-param/api/Product")
                    .queryParam("searchText", "")
                    .queryParam("groupCodes", "")
                    .queryParam("pageIndex", pageIndex)
                    .queryParam("pageSize", pageSize)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
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
            String configUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/-param/api/ConfigBank")
                    .queryParam("productCode", productCode)
                    .queryParam("pageIndex", 1)
                    .queryParam("pageSize", 50)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
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
                            baseUrl + "/qyplmapi/-param/api/ConfigBank/" + versionId + "/Param")
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

    @Tool(description = "从PLM系统根据对象名称查询对象实例列表")
    public String retrieveObjectByName(
            @ToolParam(description = "对象名称，如 260524-1") String objectName) {

        System.out.println("[🔨] 查询对象信息，objectName=" + objectName);

        try {
            String url = baseUrl + "/qyplmapi/accessbusiness/item/instance/action/listbatch/page";

            String requestBody = String.format(
                    "{\"pageNum\":1,\"pageSize\":10,\"searchRevisionTypeEnum\":\"ISLATESTONLY\",\"isSearchChildren\":false,\"orderAttributes\":[],\"objectEntries\":[],\"orSegmentGroup\":{\"andSegmentGroups\":[{\"generalSegments\":[{\"operationKey\":\"LIKE\",\"attribute\":\"objectName\",\"value\":\"%s\"}],\"operationKey\":\"AND\"}],\"operationKey\":\"OR\"}}",
                    objectName != null ? objectName.replace("\\", "\\\\").replace("\"", "\\\"") : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            String simplified = simplifyObjectResult(result);
            System.out.println("[✅] 对象信息查询成功");
            return simplified;

        } catch (Exception e) {
            System.err.println("[❌] 查询对象信息失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统根据文件夹名称查询文件夹列表")
    public String retrieveFolderByName(
            @ToolParam(description = "文件夹名称，支持模糊查询") String folderName) {

        System.out.println("[🔨] 查询文件夹信息，folderName=" + folderName);

        try {
            String url = baseUrl + "/qyplmapi/accessbusiness/item/instance/action/listbatch/page";

            String requestBody = String.format(
                    "{\"pageNum\":1,\"pageSize\":10,\"searchRevisionTypeEnum\":\"ISLATESTONLY\",\"isSearchChildren\":false,\"orSegmentGroup\":{\"andSegmentGroups\":[{\"generalSegments\":[{\"operationKey\":\"LIKE\",\"attribute\":\"objectName\",\"value\":\"%s\"}],\"operationKey\":\"AND\"}],\"operationKey\":\"OR\"}}",
                    folderName != null ? folderName.replace("\\", "\\\\").replace("\"", "\\\"") : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            String simplified = simplifyFolderResult(result);
            System.out.println("[✅] 文件夹信息查询成功");
            return simplified;

        } catch (Exception e) {
            System.err.println("[❌] 查询文件夹信息失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统删除对象实例")
    public String deleteObject(
            @ToolParam(description = "对象GUID") String guid,
            @ToolParam(description = "业务对象GUID，对应bizObjectGuid") String businessObjectGuid) {

        System.out.println("[🔨] 删除对象，guid=" + guid + ", businessObjectGuid=" + businessObjectGuid);

        try {
            String url = baseUrl + "/qyplmapi/accessbusiness/item/instance";

            String requestBody = String.format(
                    "{\"bizObjectGuid\":\"%s\",\"guid\":\"%s\"}",
                    businessObjectGuid != null ? businessObjectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    guid != null ? guid.replace("\\", "\\\\").replace("\"", "\\\"") : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 对象删除成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 删除对象失败: " + e.getMessage());
            return "删除失败: " + e.getMessage();
        }
    }

    @Tool(description = "将对象挂载到指定文件夹下")
    public String linkObjectToFolder(
            @ToolParam(description = "文件夹GUID") String folderGuid,
            @ToolParam(description = "文件夹业务对象GUID(bizObjectGuid)") String folderBusinessObjectGuid,
            @ToolParam(description = "对象GUID") String objectGuid,
            @ToolParam(description = "对象业务对象GUID(bizObjectGuid)") String objectBusinessObjectGuid) {

        System.out.println("[🔨] 挂载对象到文件夹，folderGuid=" + folderGuid + ", objectGuid=" + objectGuid);

        try {
            String url = baseUrl + "/qyplmapi/enterprisefolder/folder/linkBatch";

            String requestBody = String.format(
                    "{\"folderObject\":{\"bizObjectGuid\":\"%s\",\"guid\":\"%s\"},\"objectGuidList\":[{\"bizObjectGuid\":\"%s\",\"guid\":\"%s\"}]}",
                    folderBusinessObjectGuid != null ? folderBusinessObjectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    folderGuid != null ? folderGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    objectBusinessObjectGuid != null ? objectBusinessObjectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    objectGuid != null ? objectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 对象挂载成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 挂载对象失败: " + e.getMessage());
            return "挂载失败: " + e.getMessage();
        }
    }

    @Tool(description = "刷新PLM系统的登录token")
    public String refreshAuthToken() {
        System.out.println("[🔨] 开始刷新PLM登录token");

        try {
            String url = baseUrl + "/qyplmapi/permission/user/login";
            String requestBody = "{\"userName\":\"admin\",\"password\":\"Admin000\"}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            String newToken = extractToken(result);
            if (newToken != null) {
                this.authToken = newToken;
                System.out.println("[✅] PLM登录token刷新成功");
                return "token刷新成功";
            } else {
                System.err.println("[❌] 从响应中未解析到token");
                return "token刷新失败：未解析到token";
            }

        } catch (Exception e) {
            System.err.println("[❌] 刷新token失败: " + e.getMessage());
            return "token刷新失败: " + e.getMessage();
        }
    }

    /**
     * 简化文件夹查询结果，只保留关键字段
     */
    private String simplifyFolderResult(String originalJson) {
        if (originalJson == null || originalJson.isEmpty()) {
            return originalJson;
        }
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(originalJson);
            com.fasterxml.jackson.databind.JsonNode data = root.get("data");
            if (data == null || !data.has("list")) {
                return originalJson;
            }
            com.fasterxml.jackson.databind.JsonNode list = data.get("list");
            java.util.List<java.util.Map<String, Object>> simplifiedList = new java.util.ArrayList<>();
            for (com.fasterxml.jackson.databind.JsonNode item : list) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("guid", getText(item, "guid"));
                map.put("businessObjectGuid", getText(item, "businessObjectGuid"));
                map.put("objectName", getText(item, "objectName"));
                map.put("folderType", getText(item, "folderType"));
                map.put("status", getText(item, "status"));
                map.put("ownerUser", getText(item, "ownerUser"));
                map.put("ownerGroup", getText(item, "ownerGroup"));
                map.put("description", getText(item, "description"));
                map.put("createUser", getText(item, "createUser"));
                map.put("createTime", getText(item, "createTime"));
                map.put("updateUser", getText(item, "updateUser"));
                map.put("updateTime", getText(item, "updateTime"));
                simplifiedList.add(map);
            }
            java.util.Map<String, Object> resultData = new java.util.HashMap<>();
            resultData.put("total", getLong(data, "total"));
            resultData.put("list", simplifiedList);
            resultData.put("pageNum", getInt(data, "pageNum"));
            resultData.put("pageSize", getInt(data, "pageSize"));
            resultData.put("pages", getInt(data, "pages"));
            resultData.put("size", getInt(data, "size"));

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("code", getText(root, "code"));
            result.put("msg", getText(root, "msg"));
            result.put("data", resultData);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            System.err.println("[❌] 简化文件夹结果失败: " + e.getMessage());
            return originalJson;
        }
    }

    /**
     * 简化对象查询结果，只保留关键字段
     */
    private String simplifyObjectResult(String originalJson) {
        if (originalJson == null || originalJson.isEmpty()) {
            return originalJson;
        }
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(originalJson);
            com.fasterxml.jackson.databind.JsonNode data = root.get("data");
            if (data == null || !data.has("list")) {
                return originalJson;
            }
            com.fasterxml.jackson.databind.JsonNode list = data.get("list");
            java.util.List<java.util.Map<String, Object>> simplifiedList = new java.util.ArrayList<>();
            for (com.fasterxml.jackson.databind.JsonNode item : list) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("itemId", getText(item, "itemId"));
                map.put("revisionId", getText(item, "revisionId"));
                map.put("objectName", getText(item, "objectName"));
                map.put("fullName", getText(item, "fullName"));
                map.put("status", getText(item, "status$displayString"));
                map.put("ownerUser", getText(item, "ownerUser"));
                map.put("ownerGroup", getText(item, "ownerGroup"));
                map.put("createUser", getText(item, "createUser"));
                map.put("updateUser", getText(item, "updateUser"));
                map.put("createTime", getText(item, "createTime"));
                map.put("updateTime", getText(item, "updateTime"));
                map.put("type", getText(item, "businessObjectName$displayString"));
                map.put("hasBOM", getBoolean(item, "hasBOM"));
                map.put("hasSourceFile", getBoolean(item, "hasSourceFile"));

                String guid = getText(item, "guid");
                String businessObjectGuid = getText(item, "businessObjectGuid");
                String objName = getText(item, "objectName");
                map.put("objectName", String.format("[%s](object=%s/%s/%s)", objName, guid, businessObjectGuid, objName));

                simplifiedList.add(map);
            }
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("code", getText(root, "code"));
            result.put("msg", getText(root, "msg"));
            result.put("data", simplifiedList);
            return objectMapper.writeValueAsString(result) + "\n\n ## 注意事项：\n objectName字段是超链接的格式，回答用户时一定要保证超链接的格式，因为用户需要直接点击跳转";
        } catch (Exception e) {
            System.err.println("[❌] 简化结果失败: " + e.getMessage());
            return originalJson;
        }
    }

    /**
     * 将 getParamMsg 接口返回的 CommonResult 解析为可读的参数详情
     */
    private String formatParamMsg(String json) {
        if (json == null || json.isEmpty()) {
            return "未获取到参数信息";
        }
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(json);
            com.fasterxml.jackson.databind.JsonNode data = root.get("data");
            if (data == null || data.isNull()) {
                String msg = getText(root, "msg");
                return msg != null ? msg : "未找到对应的参数记录";
            }

            String param = getText(data, "param");
            String value = getText(data, "value");
            String motor = getText(data, "motor");
            String fileName = getText(data, "fileName");
            String hint = getText(data, "hint");
            String note = getText(data, "note");
            Integer status = data.get("status") != null && !data.get("status").isNull() ? data.get("status").asInt() : null;

            StringBuilder sb = new StringBuilder();
            sb.append("## 电机型号：").append(motor == null ? "" : motor).append("\n");
            sb.append("## 参数名称：").append(param == null ? "" : param).append("\n");

            sb.append("\n## 参数解读提示信息\n");
            boolean hasTip = false;
            if (hint != null && !hint.isEmpty()) {
                sb.append(hint).append("\n");
                hasTip = true;
            }
            if (!hasTip) {
                sb.append("暂无\n");
            }

            if (note != null && !note.isEmpty()) {
                sb.append("\n## 已经解读出来的参数值和关联信息\n");
                sb.append(note);
            }
//            sb.append("参数值：").append(value == null ? "" : value).append("\n");
//            sb.append("电机：").append(motor == null ? "" : motor).append("\n");
//            sb.append("文档：").append(fileName == null ? "" : fileName).append("\n");
//            sb.append("状态：").append(status != null && status == 1 ? "已完成" : "未完成").append("\n");

            return sb.toString();
        } catch (Exception e) {
            System.err.println("[❌] 解析参数详情失败: " + e.getMessage());
            return json;
        }
    }

    private String getText(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : null;
    }

    private boolean getBoolean(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() && field.asBoolean();
    }

    private long getLong(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asLong() : 0L;
    }

    private int getInt(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asInt() : 0;
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

    /**
     * 从登录响应中提取token
     */
    private String extractToken(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            String tokenKey = "\"token\"";
            int tokenIndex = json.indexOf(tokenKey);
            if (tokenIndex == -1) {
                return null;
            }
            int colonIndex = json.indexOf(":", tokenIndex + tokenKey.length());
            if (colonIndex == -1) {
                return null;
            }
            int start = json.indexOf("\"", colonIndex + 1);
            if (start == -1) {
                return null;
            }
            int end = json.indexOf("\"", start + 1);
            if (end != -1) {
                return json.substring(start + 1, end);
            }
        } catch (Exception e) {
            System.err.println("解析token失败: " + e.getMessage());
        }
        return null;
    }
}
