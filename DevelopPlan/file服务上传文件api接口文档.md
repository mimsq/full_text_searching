# 文件上传接口 API 文档


## 1. 接口信息
- **接口名称**：文件上传接口  
- **请求路径**：`/upload`  
- **请求方法**：`POST`  
- **内容类型**：`multipart/form-data`（用于传输文件二进制数据）  
- **功能描述**：接收文件并存储，生成文件元数据，支持上传后自动触发处理任务  


## 2. 请求参数

### 2.1 路径参数
无


### 2.2 请求头（Header）
| 参数名 | 类型 | 是否必传 | 说明 |
|--------|------|----------|------|
| `uid` | String | 条件必传 | 当 `sourceType=1` 时必传，代表租户人员的唯一标识（用于身份校验） |
| 其他会话信息 | String | 条件必传 | 当 `sourceType=0` 或空时必传，如 Cookie 或 Token（用于验证前端用户登录状态） |


### 2.3 请求体（Form Data）
| 参数名 | 类型 | 是否必传 | 说明 |
|--------|------|----------|------|
| `file` | File | 是 | 待上传的文件（二进制数据，单次仅支持上传1个文件） |
| `st`（sourceType） | String | 是 | 上传来源类型：<br>- `1`：租户人员上传<br>- `0` 或空：前端用户上传 |
| `persistentProcess` | String | 否 | 上传后自动触发的处理任务配置（格式需符合 `ProcessTask` 解析要求，如格式转换、内容提取等） |


## 3. 响应参数
响应格式为 JSON，返回 `FileCallResult` 对象，结构如下：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `ret` | Integer | 结果状态码：<br>- `0`：成功<br>- 非0：失败 |
| `msg` | String | 响应消息（如“上传成功”“文件不存在”等） |
| `id` | String | 文件唯一标识（数据库中 `FileObject` 的 ID，用于后续查询/处理） |
| `storeId` | String | 文件存储标识（用于文件实际内容的存储定位） |
| `fileName` | String | 文件名（不含扩展名） |
| `fileSize` | Long | 文件大小（单位：字节） |
| `fileSuffix` | String | 文件扩展名（如 `pdf`、`jpg`） |
| `fileType` | String | 文件类型（如 `IMAGE`、`DOCUMENT`，由系统根据扩展名解析） |
| `fileExtInfo` | String | 文件扩展信息（JSON 格式，如图片尺寸、文档页数等） |


## 4. 示例请求

### 4.1 租户人员上传（sourceType=1）
```http
POST /upload HTTP/1.1
Host: [服务器域名]
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
uid: 123456  # 租户人员ID

----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="st"
1
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="test.pdf"
Content-Type: application/pdf

[文件二进制数据]
----WebKitFormBoundary7MA4YWxkTrZu0gW
```


### 4.2 前端用户上传（sourceType=0）
```http
POST /upload HTTP/1.1
Host: [服务器域名]
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
Cookie: SESSIONID=abc123  # 前端用户会话ID

----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="st"
0
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="image.jpg"
Content-Type: image/jpeg

[文件二进制数据]
----WebKitFormBoundary7MA4YWxkTrZu0gW
```


## 5. 示例响应

### 5.1 上传成功
```json
{
  "ret": 0,
  "msg": "上传成功！",
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "storeId": "store_123456789",
  "fileName": "test",
  "fileSize": 102400,
  "fileSuffix": "pdf",
  "fileType": "DOCUMENT",
  "fileExtInfo": "{\"pages\": 5, \"author\": \"test\"}"
}
```


### 5.2 上传失败（文件不存在）
```json
{
  "ret": 1,
  "msg": "请至少上传一个文件！",
  "id": null,
  "storeId": null
}
```


## 6. 错误码说明
| 错误场景 | 典型响应消息 | 处理建议 |
|----------|--------------|----------|
| 未传文件 | "请至少上传一个文件！" | 检查请求是否携带 `file` 参数 |
| 身份校验失败 | "上传失败,未登录！" | 确认 `uid` 或会话信息是否正确 |
| 文件存储异常 | "保存文件异常！" | 检查文件大小是否超限，或联系服务器管理员 |
| 处理任务提交失败 | "上传成功,发送预处理任务失败！" | 文件已上传，可后续手动调用 `/process` 接口处理 |


## 7. 调用说明
1. 支持跨服务调用，需确保调用方有权限访问接口域名。  
2. 单次上传仅支持1个文件，若需批量上传，需多次调用接口。  
3. 上传成功后，建议保存返回的 `id` 和 `storeId`，用于后续查询（如 `/files/{id}`）或处理（如 `/process`）。  
4. 若需上传后自动处理文件，需正确传递 `persistentProcess` 参数（格式需与服务端 `ProcessTask.parse()` 方法兼容）。