# 在线音乐商店 API 文档

本文档详细说明了在线音乐商店后端的 API 接口，涵盖了游客、注册用户和管理员三种角色的所有功能。

## 1. 通用约定

- **根URL**: `http://localhost:8080`
- **认证方式**: 所有需要认证的接口都必须在 HTTP 请求头中携带 `Authorization` 字段，其值为 `Bearer {accessToken}`。
- **响应格式**: 所有接口均返回统一的 JSON 结构：
  ```json
  {
    "code": 200, // 状态码，200表示成功，其他表示失败
    "msg": "操作成功", // 提示信息
    "data": {} // 实际返回的数据
  }
  ```
- **权限说明**:
  - `GUEST`: 游客，无需登录即可访问。
  - `USER`: 注册用户，需要登录。
  - `ADMIN`: 管理员，需要登录且拥有管理员权限。

---

## 2. 认证接口 (/auth)

### 2.1 用户注册

- **功能**: 创建一个新的用户账号。
- **URL**: `/auth/register`
- **方法**: `POST`
- **权限**: `GUEST`
- **请求体**:
  ```json
  {
    "username": "newUser",
    "password": "password123"
  }
  ```
- **成功响应 (200)**: 注册成功后自动登录，并返回 Token。
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "accessToken": "xxx.yyy.zzz",
      "refreshToken": "aaa.bbb.ccc"
    }
  }
  ```
- **失败响应 (400)**:
  ```json
  {
    "code": 400,
    "msg": "Error Code: 11004, Error Info: 用户已存在, External Error Details: 用户名 'newUser' 已被注册",
    "data": null
  }
  ```

### 2.2 用户登录

- **功能**: 用户使用账号密码登录，获取访问令牌。
- **URL**: `/auth/login`
- **方法**: `POST`
- **权限**: `GUEST`
- **请求体**:
  ```json
  {
    "username": "admin",
    "password": "123"
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "accessToken": "xxx.yyy.zzz",
      "refreshToken": "aaa.bbb.ccc"
    }
  }
  ```
- **失败响应 (401)**:
  ```json
  {
    "code": 401,
    "msg": "用户名或密码错误",
    "data": null
  }
  ```

### 2.3 刷新令牌

- **功能**: 使用有效的 Refresh Token 获取新的 Access Token 和 Refresh Token。
- **URL**: `/auth/refresh`
- **方法**: `POST`
- **权限**: `USER` / `ADMIN`
- **请求体**:
  ```json
  {
    "refreshToken": "aaa.bbb.ccc"
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "accessToken": "new_xxx.yyy.zzz",
      "refreshToken": "new_aaa.bbb.ccc"
    }
  }
  ```
- **失败响应 (401)**:
  ```json
  {
    "code": 401,
    "msg": "Refresh Token无效或已过期",
    "data": null
  }
  ```

---

## 3. 用户功能接口

### 3.1 修改个人密码

- **功能**: 已登录用户修改自己的密码。
- **URL**: `/users/me/password`
- **方法**: `PUT`
- **权限**: `USER` / `ADMIN`
- **请求体**:
  ```json
  {
    "oldPassword": "password123",
    "newPassword": "newPassword456"
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "密码修改成功",
    "data": null
  }
  ```
- **失败响应 (400)**:
  ```json
  {
    "code": 400,
    "msg": "旧密码不正确",
    "data": null
  }
  ```

### 3.2 音乐收藏

#### 3.2.1 获取我的收藏列表

- **功能**: 获取当前登录用户收藏的所有音乐。
- **URL**: `/users/me/collections`
- **方法**: `GET`
- **权限**: `USER`
- **查询参数**:
  - `page` (int, optional, default: 1): 页码。
  - `size` (int, optional, default: 10): 每页数量。
- **成功响应 (200)**: 返回分页的音乐列表。
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "total": 1,
      "pages": 1,
      "records": [
        {
          "id": 101,
          "title": "晴天",
          "artist": "周杰伦",
          "album": "叶惠美",
          "price": 1.00,
          "coverUrl": "/images/covers/yehuimei.jpg"
        }
      ]
    }
  }
  ```

#### 3.2.2 添加音乐到收藏

- **功能**: 收藏一首指定的音乐。
- **URL**: `/users/me/collections`
- **方法**: `POST`
- **权限**: `USER`
- **请求体**:
  ```json
  {
    "musicId": 102
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "收藏成功",
    "data": null
  }
  ```
- **失败响应 (404)**:
  ```json
  {
    "code": 404,
    "msg": "音乐不存在",
    "data": null
  }
  ```

#### 3.2.3 从收藏中移除音乐

- **功能**: 取消收藏一首指定的音乐。
- **URL**: `/users/me/collections/{musicId}`
- **方法**: `DELETE`
- **权限**: `USER`
- **路径参数**:
  - `musicId` (int, required): 要取消收藏的音乐ID。
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "已取消收藏",
    "data": null
  }
  ```

### 3.3 购物车管理

#### 3.3.1 查看我的购物车

- **功能**: 查看当前用户购物车中的所有商品。
- **URL**: `/users/me/cart`
- **方法**: `GET`
- **权限**: `USER`
- **成功响应 (200)**: 返回购物车项目列表。
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": [
      {
        "cartItemId": 1,
        "musicId": 101,
        "title": "晴天",
        "artist": "周杰伦",
        "price": 1.00,
        "quantity": 2
      }
    ]
  }
  ```

#### 3.3.2 添加商品到购物车

- **功能**: 将一件音乐商品添加到购物车。
- **URL**: `/users/me/cart`
- **方法**: `POST`
- **权限**: `USER`
- **请求体**:
  ```json
  {
    "musicId": 102,
    "quantity": 1
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "已添加到购物车",
    "data": null
  }
  ```

#### 3.3.3 修改购物车商品数量

- **功能**: 修改购物车中某个商品的数量。
- **URL**: `/users/me/cart/items/{cartItemId}`
- **方法**: `PUT`
- **权限**: `USER`
- **路径参数**:
  - `cartItemId` (int, required): 购物车项的ID。
- **请求体**:
  ```json
  {
    "quantity": 3
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "购物车已更新",
    "data": null
  }
  ```

#### 3.3.4 从购物车移除商品

- **功能**: 从购物车中删除一个或多个商品项。
- **URL**: `/users/me/cart/items`
- **方法**: `DELETE`
- **权限**: `USER`
- **请求体**:
  ```json
  {
    "cartItemIds": [1, 2]
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "商品已从购物车移除",
    "data": null
  }
  ```

### 3.4 订单管理

#### 3.4.1 提交订单

- **功能**: 从购物车中的商品创建一张新订单。
- **URL**: `/orders`
- **方法**: `POST`
- **权限**: `USER`
- **请求体**:
  ```json
  {
    "cartItemIds": [1, 2],
    "shippingAddress": "某某省某某市某某区某某街道123号"
  }
  ```
- **成功响应 (200)**: 返回新创建的订单号。
  ```json
  {
    "code": 200,
    "msg": "下单成功",
    "data": {
      "orderNo": "2025093012345678"
    }
  }
  ```

#### 3.4.2 查询我的订单列表

- **功能**: 获取当前用户的所有订单。
- **URL**: `/orders`
- **方法**: `GET`
- **权限**: `USER`
- **查询参数**:
  - `page` (int, optional, default: 1): 页码。
  - `size` (int, optional, default: 10): 每页数量。
- **成功响应 (200)**: 返回分页的订单列表。
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "total": 1,
      "pages": 1,
      "records": [
        {
          "orderNo": "2025093012345678",
          "userId": 2,
          "username": "testUser",
          "totalAmount": 1.00,
          "status": "PAID",
          "createTime": "2025-09-30T10:00:00"
        }
      ]
    }
  }
  ```

#### 3.4.3 查询订单详情

- **功能**: 查询某笔订单的详细信息。
- **URL**: `/orders/{orderNo}`
- **方法**: `GET`
- **权限**: `USER`
- **路径参数**:
  - `orderNo` (string, required): 订单号。
- **成功响应 (200)**: 返回订单的详细信息，包括商品列表。

---

## 4. 游客与公共功能接口

### 4.1 音乐浏览与搜索

- **功能**: 游客和用户浏览、搜索音乐。
- **URL**: `/music`
- **方法**: `GET`
- **权限**: `GUEST`
- **查询参数**:
  - `keyword` (string, optional): 歌名、歌手、专辑名的模糊搜索关键字。
  - `categoryId` (int, optional): 按音乐分类ID筛选。
  - `page` (int, optional, default: 1): 页码。
  - `size` (int, optional, default: 10): 每页数量。
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "total": 1,
      "pages": 1,
      "records": [
        {
          "id": 101,
          "title": "晴天",
          "artist": "周杰伦",
          "album": "叶惠美",
          "price": 1.00,
          "coverUrl": "/images/covers/yehuimei.jpg"
        }
      ]
    }
  }
  ```

### 4.2 查看音乐详情

- **功能**: 游客和用户查看单首音乐的详细信息。
- **URL**: `/music/{id}`
- **方法**: `GET`
- **权限**: `GUEST`
- **路径参数**:
  - `id` (int, required): 音乐ID。
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "id": 101,
      "title": "晴天",
      "artist": "周杰伦",
      "album": "叶惠美",
      "genre": "流行",
      "releaseDate": "2003-07-31",
      "price": 1.00,
      "lyrics": "[00:01.00]故事的小黄花...",
      "coverUrl": "/images/covers/yehuimei.jpg",
      "audioUrl": "/audio/jay/qingtian.mp3"
    }
  }
  ```

### 4.3 查看音乐分类

- **功能**: 游客和用户获取所有音乐分类。
- **URL**: `/categories`
- **方法**: `GET`
- **权限**: `GUEST`
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": [
      {
        "id": 1,
        "name": "华语流行",
        "description": "最新最热的华语流行音乐"
      },
      {
        "id": 2,
        "name": "摇滚",
        "description": "经典与现代摇滚乐"
      }
    ]
  }
  ```

### 4.4 权限限制说明
- 当游客尝试访问需要登录的接口时（如收藏、加入购物车等），系统将返回 `401 Unauthorized` 错误。前端应捕获此错误并引导用户至登录页面。

---

## 5. 管理员功能接口 (/admin)

### 5.1 音乐管理

#### 5.1.1 获取音乐列表 (Admin)
- **URL**: `/admin/music`
- **方法**: `GET`
- **权限**: `ADMIN`
- **查询参数**:
  - `keyword` (string, optional): 歌名、歌手、专辑名的模糊搜索关键字。
  - `categoryId` (int, optional): 按音乐分类ID筛选。
  - `status` (string, optional): 按状态筛选 (`PUBLISHED`, `DRAFT`).
  - `page` (int, optional, default: 1): 页码。
  - `size` (int, optional, default: 10): 每页数量。
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "total": 2,
      "pages": 1,
      "records": [
        {
          "id": 101,
          "title": "晴天",
          "artist": "周杰伦",
          "album": "叶惠美",
          "price": 1.00,
          "status": "PUBLISHED",
          "createTime": "2025-09-29T11:00:00"
        },
        {
          "id": 102,
          "title": "夜曲",
          "artist": "周杰伦",
          "album": "十一月的肖邦",
          "price": 1.00,
          "status": "DRAFT",
          "createTime": "2025-09-30T14:00:00"
        }
      ]
    }
  }
  ```

#### 5.1.2 添加新音乐
- **URL**: `/admin/music`
- **方法**: `POST`
- **权限**: `ADMIN`
- **请求体**:
  ```json
  {
    "title": "夜曲",
    "artist": "周杰伦",
    "album": "十一月的肖邦",
    "categoryId": 1,
    "price": 1.00,
    "releaseDate": "2005-11-01",
    "lyrics": "[00:01.00]一群嗜血的蚂蚁...",
    "status": "PUBLISHED" // PUBLISHED, DRAFT
  }
  ```
- **成功响应 (201)**:
  ```json
  {
    "code": 201,
    "msg": "创建成功",
    "data": {
      "id": 102,
      "title": "夜曲",
      "artist": "周杰伦",
      "album": "十一月的肖邦",
      "categoryId": 1,
      "price": 1.00,
      "releaseDate": "2005-11-01",
      "status": "PUBLISHED"
    }
  }
  ```

#### 5.1.3 修改音乐信息
- **URL**: `/admin/music/{id}`
- **方法**: `PUT`
- **权限**: `ADMIN`
- **请求体**: (同上，包含所有要更新的字段)
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "更新成功",
    "data": {
      "id": 102,
      "title": "夜曲",
      "artist": "周杰伦",
      "album": "十一月的肖邦",
      "categoryId": 1,
      "price": 1.20,
      "releaseDate": "2005-11-01",
      "status": "PUBLISHED"
    }
  }
  ```

#### 5.1.4 删除音乐
- **URL**: `/admin/music/{id}`
- **方法**: `DELETE`
- **权限**: `ADMIN`
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "删除成功",
    "data": null
  }
  ```

### 5.2 音乐分类管理

#### 5.2.1 获取分类列表 (Admin)
- **URL**: `/admin/categories`
- **方法**: `GET`
- **权限**: `ADMIN`
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": [
      {
        "id": 1,
        "name": "华语流行",
        "description": "最新最热的华语流行音乐",
        "musicCount": 50
      },
      {
        "id": 2,
        "name": "摇滚",
        "description": "经典与现代摇滚乐",
        "musicCount": 35
      }
    ]
  }
  ```

#### 5.2.2 添加新分类
- **URL**: `/admin/categories`
- **方法**: `POST`
- **权限**: `ADMIN`
- **请求体**:
  ```json
  {
    "name": "民谣",
    "description": "清新质朴的民谣歌曲"
  }
  ```
- **成功响应 (201)**:
  ```json
  {
    "code": 201,
    "msg": "创建成功",
    "data": {
      "id": 3,
      "name": "民谣",
      "description": "清新质朴的民谣歌曲"
    }
  }
  ```

#### 5.2.3 修改分类信息
- **URL**: `/admin/categories/{id}`
- **方法**: `PUT`
- **权限**: `ADMIN`
- **请求体**: (同上)
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "更新成功",
    "data": {
      "id": 3,
      "name": "民谣",
      "description": "清新质朴的民谣歌曲，适合安静聆听"
    }
  }
  ```

#### 5.2.4 删除分类
- **URL**: `/admin/categories/{id}`
- **方法**: `DELETE`
- **权限**: `ADMIN`
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "删除成功",
    "data": null
  }
  ```

### 5.3 订单管理

#### 5.3.1 查询订单列表
- **URL**: `/admin/orders`
- **方法**: `GET`
- **权限**: `ADMIN`
- **查询参数**:
  - `userId` (int, optional): 按用户ID筛选。
  - `status` (string, optional): 按订单状态筛选 (e.g., `PENDING`, `PAID`, `SHIPPED`, `CANCELLED`)。
  - `page` (int, optional, default: 1): 页码。
  - `size` (int, optional, default: 10): 每页数量。
- **成功响应 (200)**: 返回分页的订单列表。
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "total": 1,
      "records": [
        {
          "orderNo": "2025093012345678",
          "userId": 2,
          "username": "testUser",
          "totalAmount": 1.00,
          "status": "PAID",
          "createTime": "2025-09-30T10:00:00"
        }
      ]
    }
  }
  ```

#### 5.3.2 处理订单状态
- **URL**: `/admin/orders/{orderNo}/status`
- **方法**: `PUT`
- **权限**: `ADMIN`
- **请求体**:
  ```json
  {
    "status": "SHIPPED"
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "订单状态已更新",
    "data": null
  }
  ```

#### 5.3.3 删除订单
- **URL**: `/admin/orders/{orderNo}`
- **方法**: `DELETE`
- **权限**: `ADMIN`
- **说明**: 通常为逻辑删除。
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "删除成功",
    "data": null
  }
  ```

### 5.4 系统用户管理

#### 5.4.1 获取用户列表
- **URL**: `/admin/users`
- **方法**: `GET`
- **权限**: `ADMIN`
- **查询参数**:
  - `username` (string, optional): 按用户名模糊搜索。
  - `roleId` (int, optional): 按角色ID筛选。
- **成功响应 (200)**: 返回分页的用户列表。
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "total": 2,
      "records": [
        {"id": 1, "username": "admin", "role": "ROLE_ADMIN", "status": 1, "createTime": "2025-09-29T10:00:00"},
        {"id": 2, "username": "testUser", "role": "ROLE_USER", "status": 1, "createTime": "2025-09-30T10:00:00"}
      ]
    }
  }
  ```

#### 5.4.2 添加新用户
- **URL**: `/admin/users`
- **方法**: `POST`
- **权限**: `ADMIN`
- **请求体**:
  ```json
  {
    "username": "newUserAdmin",
    "password": "password123",
    "roleId": 1, // 1 for ADMIN, 2 for USER
    "status": 1 // 1 for active, 0 for inactive
  }
  ```
- **成功响应 (201)**:
  ```json
  {
    "code": 201,
    "msg": "用户创建成功",
    "data": {
      "id": 3,
      "username": "newUserAdmin",
      "role": "ROLE_ADMIN",
      "status": 1
    }
  }
  ```

#### 5.4.3 修改用户信息
- **URL**: `/admin/users/{id}`
- **方法**: `PUT`
- **权限**: `ADMIN`
- **请求体**:
  ```json
  {
    "roleId": 2,
    "status": 0
  }
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "更新成功",
    "data": {
      "id": 3,
      "username": "newUserAdmin",
      "role": "ROLE_USER",
      "status": 0
    }
  }
  ```

#### 5.4.4 删除用户
- **URL**: `/admin/users/{id}`
- **方法**: `DELETE`
- **权限**: `ADMIN`
- **说明**: 通常为逻辑删除。
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "删除成功",
    "data": null
  }
  ```

### 5.5 网站信息配置

#### 5.5.1 获取所有网站配置
- **URL**: `/admin/website-config`
- **方法**: `GET`
- **权限**: `ADMIN`
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": [
      {
        "id": 1,
        "configKey": "website_name",
        "configValue": "音乐商城",
        "configDesc": "网站的名称"
      },
      {
        "id": 2,
        "configKey": "service_phone",
        "configValue": "400-888-8888",
        "configDesc": "客服联系电话"
      }
    ]
  }
  ```

#### 5.5.2 批量更新网站配置
- **URL**: `/admin/website-config`
- **方法**: `PUT`
- **权限**: `ADMIN`
- **请求体**:
  ```json
  [
    { "configKey": "website_name", "configValue": "新音乐商城" },
    { "configKey": "service_phone", "configValue": "400-999-9999" }
  ]
  ```
- **成功响应 (200)**:
  ```json
  {
    "code": 200,
    "msg": "网站配置已更新",
    "data": null
  }
  ```
