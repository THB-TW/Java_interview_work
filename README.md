# 金融商品喜好紀錄系統

> 玉山銀行【新進 Java】後端工程師實作題（題目 E）

---

## 專案簡介

本專案為玉山銀行新進 Java 後端工程師實作題的解答實作，主題為「**金融商品喜好紀錄系統**」。

系統允許使用者新增、查詢、修改與刪除個人的喜好金融商品，並透過三層式架構（展示層 → 業務層 → 資料層）實現清晰的職責分離。後端以 **Spring Boot** 建置 RESTful API，搭配 **MariaDB** 關聯式資料庫與 **Stored Procedure** 進行資料存取；前端以 **Vue.js 3** 實作使用者操作介面。

---

## 功能說明

| 功能 | 說明 |
|------|------|
| 查詢喜好清單 | 依使用者 ID 查詢其所有喜好金融商品紀錄 |
| 新增喜好商品 | 選擇商品、輸入購買數量與扣款帳號，自動計算手續費與總扣款金額 |
| 修改喜好商品 | 透過彈跳視窗修改商品、購買數量與扣款帳號 |
| 刪除喜好商品 | 確認後刪除指定喜好紀錄 |
| 商品清單查詢 | 前端下拉選單自動帶入所有可選金融商品 |

---

## 系統架構

本專案採用**三層式架構（Three-Tier Architecture）**：

```
┌─────────────────────────────────┐
│      前端（Vue.js + Vite）       │  ← Web Server 層
│   http://localhost:5173         │
└────────────────┬────────────────┘
                 │ RESTful API（Axios）
┌────────────────▼────────────────┐
│     後端（Spring Boot）          │  ← Application Server 層
│   http://localhost:8080/api     │
│                                 │
│  ┌──────────┐ ┌──────────────┐  │
│  │展示層     │ │  共用層       │  │
│  │Controller│ │ApiResponse   │  │
│  └────┬─────┘ │Exception     │  │
│       │       │WebConfig     │  │
│  ┌────▼─────┐ └──────────────┘  │
│  │業務層     │                   │
│  │Service   │                   │
│  └────┬─────┘                   │
│       │                         │
│  ┌────▼─────┐                   │
│  │資料層     │                   │
│  │Repository│                   │
│  └────┬─────┘                   │
└───────┼─────────────────────────┘
        │ Stored Procedure（JdbcTemplate）
┌───────▼─────────────────────────┐
│   資料庫（MariaDB）               │  ← 資料層
│   fin_like_system               │
└─────────────────────────────────┘
```

### 後端層級職責

| 層級 | 套件路徑 | 職責 |
|------|---------|------|
| 展示層（Presentation） | `controller/` | 接收 HTTP 請求、參數驗證、回應封裝 |
| 業務層（Business） | `service/` | 核心業務邏輯、資料驗證、例外處理 |
| 資料層（Data Access） | `repository/` | 透過 JdbcTemplate 呼叫 Stored Procedure |
| 共用層（Common） | `common/` | 統一回應格式、自訂例外、CORS 設定 |

---

## 技術棧

### 後端

| 技術 | 版本 / 說明 |
|------|------------|
| Java | 21 |
| Spring Boot | 3.x |
| Maven | 建置工具 |
| JdbcTemplate | 資料存取（搭配 Stored Procedure） |
| MariaDB | 關聯式資料庫 |

### 前端

| 技術 | 版本 / 說明 |
|------|------------|
| Vue.js | 3（Composition API） |
| Vite | 前端建置工具 |
| Axios | HTTP 請求（含攔截器統一錯誤處理） |
| Vanilla CSS | 全域樣式管理（`style.css`） |

---

## 專案結構

```
Java實作題/
├── src/
│   └── main/
│       ├── java/com/fin/like/
│       │   ├── FinLikeApplication.java       # Spring Boot 啟動入口
│       │   ├── controller/
│       │   │   ├── LikeController.java       # 喜好商品 CRUD API
│       │   │   └── ProductController.java    # 商品查詢 API
│       │   ├── service/
│       │   │   ├── LikeService.java          # 喜好商品業務邏輯
│       │   │   └── ProductService.java       # 商品業務邏輯
│       │   ├── repository/
│       │   │   ├── LikeRepository.java       # 呼叫喜好商品 Stored Procedure
│       │   │   ├── ProductRepository.java    # 商品資料查詢
│       │   │   └── UserRepository.java       # 使用者資料查詢
│       │   ├── dto/
│       │   │   ├── LikeRequest.java          # 新增 / 修改請求 DTO
│       │   │   ├── LikeResponse.java         # 查詢回應 DTO
│       │   │   ├── ProductDto.java           # 商品 DTO
│       │   │   └── UserDto.java              # 使用者 DTO
│       │   ├── common/
│       │   │   ├── response/
│       │   │   │   └── ApiResponse.java      # 統一 API 回應格式
│       │   │   └── exception/
│       │   │       └── BusinessException.java# 自訂業務例外
│       │   └── config/
│       │       └── WebConfig.java            # CORS 跨域設定
│       └── resources/
│           └── application.yml               # 資料庫連線、Port、Log 設定
│
├── backend/
│   └── DB/
│       ├── schema.sql                        # DDL：建立資料表
│       ├── data.sql                          # DML：初始測試資料
│       └── stored_procedures.sql             # 所有 Stored Procedure 定義
│
├── fin-like-ui/                              # Vue.js 前端專案
│   ├── src/
│   │   ├── App.vue                           # 主頁面（查詢、新增、更新、刪除）
│   │   ├── main.js                           # Vue 應用程式入口
│   │   ├── style.css                         # 全域樣式（含 Modal CSS）
│   │   ├── services/
│   │   │   └── likeApi.js                    # API 呼叫函式（getLikes、addLike 等）
│   │   └── utils/
│   │       └── api.js                        # Axios 實例（含 response 攔截器）
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
└── pom.xml                                   # Maven 建置設定
```

---

## 資料表設計

> DDL 詳見：`backend/DB/schema.sql`  
> 初始資料詳見：`backend/DB/data.sql`

### `users`（使用者）

| 欄位 | 型別 | 說明 |
|------|------|------|
| `user_id` | VARCHAR(20) PK | 使用者 ID |
| `user_name` | VARCHAR(50) | 姓名 |
| `email` | VARCHAR(100) | 電子郵件 |
| `account` | VARCHAR(20) | 帳號 |
| `created_at` | DATETIME | 建立時間 |
| `updated_at` | DATETIME | 更新時間 |

### `products`（金融商品）

| 欄位 | 型別 | 說明 |
|------|------|------|
| `product_no` | BIGINT PK AUTO_INCREMENT | 商品編號 |
| `product_name` | VARCHAR(100) | 商品名稱 |
| `price` | DECIMAL(15,2) | 商品單價 |
| `fee_rate` | DECIMAL(5,4) | 手續費率 |
| `created_at` | DATETIME | 建立時間 |
| `updated_at` | DATETIME | 更新時間 |

### `like_list`（喜好紀錄）

| 欄位 | 型別 | 說明 |
|------|------|------|
| `sn` | BIGINT PK AUTO_INCREMENT | 流水號 |
| `user_id` | VARCHAR(20) FK → users | 使用者 ID |
| `product_no` | BIGINT FK → products | 商品編號 |
| `purchase_quantity` | INT | 購買數量 |
| `account` | VARCHAR(20) | 扣款帳號 |
| `total_fee` | DECIMAL(15,2) | 總手續費（自動計算） |
| `total_amount` | DECIMAL(15,2) | 總扣款金額（自動計算） |
| `created_at` | DATETIME | 建立時間 |
| `updated_at` | DATETIME | 更新時間 |

---

## API 說明

所有 API 回應均採統一格式：

```json
{
  "code": "0000",
  "message": "success",
  "data": { ... }
}
```

### 喜好商品 `/api/likes`

| 方法 | 路徑 | 說明 |
|------|------|------|
| GET | `/api/likes?userId={userId}` | 查詢指定使用者的喜好清單 |
| POST | `/api/likes` | 新增喜好商品 |
| PUT | `/api/likes/{sn}` | 修改指定喜好商品資訊 |
| DELETE | `/api/likes/{sn}?userId={userId}` | 刪除指定喜好商品 |

#### POST `/api/likes` 請求範例

```json
{
  "userId": "A1236456789",
  "productNo": 2,
  "purchaseQuantity": 3,
  "account": "1111999666"
}
```

#### GET `/api/likes` 回應範例

```json
{
  "code": "0000",
  "message": "success",
  "data": [
    {
      "sn": 1,
      "productNo": 2,
      "purchaseQuantity": 1,
      "account": "1111999666",
      "totalFee": 200.00,
      "totalAmount": 20200.00
    }
  ]
}
```

### 商品 `/api/products`

| 方法 | 路徑 | 說明 |
|------|------|------|
| GET | `/api/products` | 查詢所有金融商品 |
| GET | `/api/products/{productName}` | 依商品名稱查詢單一商品 |

---

## Stored Procedure 說明

> 定義檔：`backend/DB/stored_procedures.sql`

| Stored Procedure | 用途 |
|------------------|------|
| `sp_add_like` | 新增喜好商品，自動從 `products` 查出價格與手續費率後計算 `total_fee` 與 `total_amount` |
| `sp_get_like_list` | 依 `user_id` 查詢喜好清單 |
| `sp_update_like` | 更新喜好商品資訊，並重新計算手續費與總金額 |
| `sp_delete_like` | 依 `sn` 與 `user_id` 刪除喜好紀錄（防止跨用戶誤刪） |

所有資料庫操作均透過 Stored Procedure 執行，後端使用 `JdbcTemplate` 的 **Parameterized Query（`?` 佔位符）** 呼叫，可有效防止 **SQL Injection** 攻擊。

---

## 安裝與執行方式

### 環境需求

- Java 21+
- Maven 3.8+
- MariaDB（或 MySQL）
- Node.js 18+（npm）

### 1. 資料庫初始化

```sql
-- 依序執行以下檔案
source backend/DB/schema.sql
source backend/DB/data.sql
source backend/DB/stored_procedures.sql
```

或使用 MySQL CLI：

```bash
mysql -u root -p < backend/DB/schema.sql
mysql -u root -p fin_like_system < backend/DB/data.sql
mysql -u root -p fin_like_system < backend/DB/stored_procedures.sql
```

### 2. 設定後端資料庫連線

編輯 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/fin_like_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Taipei
    username: root
    password: 你的密碼
    driver-class-name: org.mariadb.jdbc.Driver
```

### 3. 啟動後端

```bash
# 於專案根目錄執行
./mvnw spring-boot:run
```

後端啟動後預設監聽：`http://localhost:8080`

### 4. 啟動前端

```bash
cd fin-like-ui
npm install
npm run dev
```

前端啟動後預設監聽：`http://localhost:5173`

---

## 測試方式

### 後端 API 測試（使用 curl）

```bash
# 查詢喜好清單
curl -X GET "http://localhost:8080/api/likes?userId=A1236456789"

# 新增喜好商品
curl -X POST "http://localhost:8080/api/likes" \
  -H "Content-Type: application/json" \
  -d '{"userId":"A1236456789","productNo":1,"purchaseQuantity":2,"account":"1111999666"}'

# 修改喜好商品（sn=1）
curl -X PUT "http://localhost:8080/api/likes/1" \
  -H "Content-Type: application/json" \
  -d '{"userId":"A1236456789","productNo":2,"purchaseQuantity":3,"account":"1111999666"}'

# 刪除喜好商品（sn=1）
curl -X DELETE "http://localhost:8080/api/likes/1?userId=A1236456789"
```

### 前端操作流程

1. 開啟瀏覽器，進入 `http://localhost:5173`
2. 在**查詢喜好清單**區塊輸入使用者 ID（預設 `A1236456789`），點擊「查詢」
3. 在**新增喜好商品**區塊填入資料，點擊「新增」
4. 清單中點擊「**更新**」按鈕，於彈跳視窗中修改商品資訊後確認
5. 清單中點擊「**刪除**」按鈕，確認後刪除該筆喜好紀錄

---

## 注意事項

### SQL Injection 防護

後端使用 `JdbcTemplate` 的 Parameterized Query（`?` 佔位符）呼叫所有 Stored Procedure，禁止直接拼接 SQL 字串，有效避免 SQL Injection 攻擊。

```java
// 正確：使用參數化查詢
String sql = "CALL sp_add_like(?, ?, ?, ?)";
jdbcTemplate.update(sql, userId, productNo, purchaseQuantity, account);
```

### XSS 防護

- 後端：Spring Boot 預設對 JSON 回應進行跳脫處理
- 前端：Vue.js 採用 `{{ }}` 模板語法，自動進行 HTML 跳脫，避免惡意腳本注入

### Transaction 保護

`LikeService` 中的新增、修改、刪除操作均標記 `@Transactional`，確保多資料表操作（如涉及 `like_list` 與 `products` 的聯合計算）在發生例外時能正確 Rollback，維持資料一致性。

### CORS 設定

`WebConfig.java` 已設定允許以下來源進行跨域請求：

- `http://localhost:5173`（Vite 前端開發）
- `http://localhost:3000`
- `http://localhost:8081`

---

## 可擴充方向

- **引入 Spring Security**：加入 JWT 驗證機制，保護 API 端點
- **使用者登入 / 登出**：結合 `users` 資料表實作完整的身份驗證流程
- **分頁查詢**：對喜好清單加入分頁（Pagination）與排序功能
- **容器化部署**：以 Docker Compose 同時管理後端、前端與 MariaDB 服務
- **前端路由**：使用 Vue Router 分離查詢、新增等功能至不同頁面

---

## 開發者

- **姓名**：陳翰陞
- **GitHub**：[THB-TW/Java_interview_work](https://github.com/THB-TW/Java_interview_work)
