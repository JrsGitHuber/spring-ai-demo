---
name: login-api
description: 调用登录接口获取Token，使用POST方式发送固定用户名密码，返回token字段
---

# 登录接口调用 Skill

此 Skill 用于调用登录接口获取认证 Token。

## 使用方式

直接调用此 Skill 的 index.js，会自动发送登录请求并返回 token。

## 接口配置

- URL: `http://139.159.221.11:9002/qyplmapi/permission/user/login`
- 方法: POST
- Content-Type: application/json
- Body: `{"userName": "ren.jiang", "password": "Uds88888"}`

## 返回格式

成功时返回 JSON:
```json
{
    "success": true,
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "message": "登录成功"
}
```

失败时返回 JSON:
```json
{
    "success": false,
    "message": "错误原因"
}
```

## 执行脚本
本技能的可执行脚本位于同目录下的 index.js。