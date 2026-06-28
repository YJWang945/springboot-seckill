# 秒杀系统前端重设计实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将全部5个前端页面重写为京东秒杀风格，红金配色、卡片布局、品牌感强。

**Architecture:** 新增 `seckill.css` 作为统一样式表，重写 5 个 Thymeleaf 模板 + 2 个静态 htm 页面。保持所有 `th:` 表达式、JS 函数名、表单字段 ID 不变，只改 HTML 结构和 CSS。

**Tech Stack:** Thymeleaf + Bootstrap 3.3 + jQuery + Layer + 自定义 CSS

---

### Task 1: 新建 seckill.css 统一样式表

**Files:**
- Create: `src/main/resources/static/css/seckill.css`

- [ ] **Step 1: 创建 seckill.css**

```css
/* ========== 京东秒杀风格样式表 ========== */

/* --- 全局 --- */
body {
    background: #f5f5f5;
    font-family: "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
    min-height: 100vh;
}
a { color: #666; }
a:hover { color: #c91623; text-decoration: none; }

/* --- 顶部导航 --- */
.seckill-header {
    background: #222;
    height: 52px;
    line-height: 52px;
    padding: 0 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.seckill-header .logo {
    color: #fff;
    font-size: 22px;
    font-weight: bold;
    letter-spacing: 2px;
}
.seckill-header .logo i { color: #c91623; font-style: normal; }
.seckill-header .user-info { color: #ccc; font-size: 13px; }
.seckill-header .user-info .nickname { color: #fff; margin-right: 15px; }
.seckill-header .user-info a { color: #ccc; text-decoration: none; }
.seckill-header .user-info a:hover { color: #c91623; }

/* --- 秒杀横幅 --- */
.seckill-banner {
    background: linear-gradient(135deg, #c91623 0%, #e1251b 100%);
    padding: 30px 40px;
    text-align: center;
    color: #fff;
}
.seckill-banner .title {
    font-size: 32px;
    font-weight: bold;
    letter-spacing: 4px;
}
.seckill-banner .title .icon-flash { font-size: 36px; }
.seckill-banner .subtitle {
    font-size: 14px;
    opacity: 0.9;
    margin-top: 6px;
}

/* --- 倒计时 --- */
.countdown-box {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    margin: 15px 0;
}
.countdown-box .label { color: #ffd6d6; font-size: 14px; margin-right: 8px; }
.countdown-box .time-block {
    background: #222;
    color: #fff;
    font-size: 24px;
    font-weight: bold;
    padding: 4px 10px;
    border-radius: 4px;
    min-width: 38px;
    text-align: center;
}
.countdown-box .colon {
    font-size: 24px;
    font-weight: bold;
    color: #fff;
}

/* --- 商品卡片网格 --- */
.seckill-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 25px 15px;
}
.seckill-card-row {
    display: flex;
    flex-wrap: wrap;
    gap: 18px;
    justify-content: center;
}
.seckill-card {
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    width: 30%;
    min-width: 280px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.06);
    transition: transform 0.2s, box-shadow 0.2s;
}
.seckill-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(0,0,0,0.12);
}
.seckill-card .card-img {
    width: 100%;
    height: 220px;
    object-fit: contain;
    background: #fafafa;
    padding: 15px;
}
.seckill-card .card-body { padding: 12px 16px 16px; }
.seckill-card .card-title {
    font-size: 15px;
    color: #333;
    height: 40px;
    overflow: hidden;
    line-height: 1.4;
}
.seckill-card .card-price {
    display: flex;
    align-items: baseline;
    gap: 10px;
    margin: 10px 0;
}
.seckill-card .card-price .seckill-price {
    font-size: 26px;
    font-weight: bold;
    color: #e1251b;
}
.seckill-card .card-price .seckill-price .unit { font-size: 14px; }
.seckill-card .card-price .origin-price {
    font-size: 13px;
    color: #999;
    text-decoration: line-through;
}
.seckill-card .card-stock { margin-bottom: 12px; }
.seckill-card .card-stock .stock-label {
    font-size: 12px;
    color: #999;
    margin-bottom: 4px;
}
.seckill-card .card-stock .progress {
    height: 6px;
    margin-bottom: 0;
    background: #ffe0e0;
    border-radius: 3px;
}
.seckill-card .card-stock .progress-bar {
    background: linear-gradient(90deg, #c91623, #e1251b);
    border-radius: 3px;
}
.seckill-card .btn-seckill {
    width: 100%;
    padding: 10px 0;
    font-size: 16px;
    font-weight: bold;
    color: #fff;
    background: linear-gradient(180deg, #e1251b, #c91623);
    border: none;
    border-radius: 20px;
    cursor: pointer;
    transition: opacity 0.2s;
}
.seckill-card .btn-seckill:hover { opacity: 0.9; color: #fff; }
.seckill-card .btn-seckill.disabled {
    background: #ccc;
    cursor: not-allowed;
}

/* --- 登录页 --- */
.login-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    background: #f5f5f5;
}
.login-banner {
    width: 100%;
    background: linear-gradient(135deg, #c91623 0%, #e1251b 100%);
    padding: 25px 0;
    text-align: center;
    color: #fff;
    font-size: 28px;
    font-weight: bold;
    letter-spacing: 3px;
    margin-bottom: 30px;
}
.login-card {
    background: #fff;
    border-radius: 10px;
    padding: 35px 40px;
    width: 400px;
    max-width: 90%;
    box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}
.login-card .login-title {
    text-align: center;
    font-size: 20px;
    color: #333;
    margin-bottom: 25px;
    font-weight: bold;
}
.login-card .form-control {
    height: 44px;
    border-radius: 6px;
    border: 1px solid #ddd;
    margin-bottom: 16px;
    font-size: 14px;
}
.login-card .form-control:focus {
    border-color: #c91623;
    box-shadow: 0 0 0 2px rgba(201,22,35,0.15);
}
.login-card .btn-login {
    width: 100%;
    height: 46px;
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    background: linear-gradient(180deg, #e1251b, #c91623);
    border: none;
    border-radius: 23px;
    cursor: pointer;
    margin-top: 8px;
}
.login-card .btn-login:hover { opacity: 0.9; color: #fff; }
.login-card .btn-reset {
    width: 100%;
    height: 40px;
    background: #f5f5f5;
    border: 1px solid #ddd;
    border-radius: 20px;
    color: #666;
    margin-bottom: 10px;
}

/* --- 商品详情 --- */
.detail-container {
    max-width: 1000px;
    margin: 30px auto;
    padding: 0 15px;
}
.detail-card {
    background: #fff;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 2px 16px rgba(0,0,0,0.06);
    display: flex;
    flex-wrap: wrap;
}
.detail-img-side {
    width: 420px;
    max-width: 100%;
    padding: 30px;
    background: #fafafa;
    text-align: center;
}
.detail-img-side img {
    max-width: 100%;
    max-height: 360px;
    object-fit: contain;
}
.detail-info-side {
    flex: 1;
    padding: 30px 35px;
    min-width: 300px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}
.detail-info-side .goods-name {
    font-size: 22px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
}
.detail-info-side .price-row {
    background: #fff5f5;
    padding: 15px 20px;
    border-radius: 8px;
    margin-bottom: 20px;
}
.detail-info-side .price-row .seckill-price {
    font-size: 32px;
    font-weight: bold;
    color: #e1251b;
}
.detail-info-side .price-row .seckill-price .unit { font-size: 16px; }
.detail-info-side .price-row .origin-price {
    font-size: 14px;
    color: #999;
    text-decoration: line-through;
    margin-left: 12px;
}
.detail-info-side .info-item {
    font-size: 14px;
    color: #666;
    margin-bottom: 10px;
}
.detail-info-side .info-item span { color: #333; font-weight: bold; }
.detail-info-side .btn-buy {
    width: 100%;
    padding: 14px 0;
    font-size: 20px;
    font-weight: bold;
    color: #fff;
    background: linear-gradient(180deg, #e1251b, #c91623);
    border: none;
    border-radius: 25px;
    cursor: pointer;
    margin-top: 15px;
}
.detail-info-side .btn-buy:hover { opacity: 0.9; color: #fff; }
.detail-info-side .btn-buy.disabled {
    background: #ccc;
    cursor: not-allowed;
}

/* --- 订单详情 --- */
.order-container {
    max-width: 800px;
    margin: 30px auto;
    padding: 0 15px;
}
.order-success-header {
    text-align: center;
    padding: 40px 0 25px;
}
.order-success-header .check-icon {
    display: inline-block;
    width: 70px;
    height: 70px;
    line-height: 70px;
    border-radius: 50%;
    background: #c91623;
    color: #fff;
    font-size: 36px;
    margin-bottom: 15px;
}
.order-success-header h3 { color: #c91623; font-weight: bold; }
.order-card {
    background: #fff;
    border-radius: 10px;
    padding: 25px 30px;
    box-shadow: 0 2px 16px rgba(0,0,0,0.06);
}
.order-card .order-row {
    display: flex;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
    font-size: 14px;
}
.order-card .order-row:last-child { border-bottom: none; }
.order-card .order-row .label { color: #999; width: 90px; flex-shrink: 0; font-weight: normal; }
.order-card .order-row .value { color: #333; }
.order-card .btn-pay {
    width: 200px;
    padding: 12px 0;
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    background: linear-gradient(180deg, #e1251b, #c91623);
    border: none;
    border-radius: 22px;
    cursor: pointer;
    margin-top: 20px;
}
.order-card .btn-pay:hover { opacity: 0.9; color: #fff; }

/* --- 秒杀失败页 --- */
.fail-container {
    text-align: center;
    padding: 80px 0;
}
.fail-container .fail-icon {
    font-size: 70px;
    color: #ddd;
    margin-bottom: 20px;
}
.fail-container h3 { color: #666; margin-bottom: 25px; }
.fail-container .btn-back {
    display: inline-block;
    padding: 12px 40px;
    font-size: 16px;
    color: #fff;
    background: linear-gradient(180deg, #e1251b, #c91623);
    border: none;
    border-radius: 22px;
    text-decoration: none;
}
.fail-container .btn-back:hover { color: #fff; opacity: 0.9; }

/* --- 响应式 --- */
@media (max-width: 768px) {
    .seckill-header { padding: 0 15px; }
    .seckill-header .logo { font-size: 18px; }
    .seckill-banner { padding: 20px 15px; }
    .seckill-banner .title { font-size: 22px; }
    .seckill-card { width: 90%; min-width: auto; }
    .seckill-card .card-img { height: 180px; }
    .countdown-box .time-block { font-size: 18px; padding: 3px 7px; min-width: 28px; }
    .countdown-box .colon { font-size: 18px; }
    .detail-card { flex-direction: column; }
    .detail-img-side { width: 100%; }
    .login-card { width: 90%; padding: 25px 20px; }
}
```

- [ ] **Step 2: 确认文件创建无误**

Run: `ls -la src/main/resources/static/css/seckill.css`

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/static/css/seckill.css
git commit -m "feat: add JD-style seckill CSS stylesheet"
```

---

### Task 2: 重写登录页 login.html

**Files:**
- Modify: `src/main/resources/templates/login.html`

> **不变量：** 保留所有 th:src 引用、JS 函数名 `login()` / `doLogin()`、表单字段 `mobile` / `password`、验证逻辑、md5.js 加密逻辑。只改 HTML 结构和引用 seckill.css。

- [ ] **Step 1: 替换 login.html**

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>秒杀系统 - 登录</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script type="text/javascript" th:src="@{/js/jquery.min.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/bootstrap/css/bootstrap.min.css}"/>
    <script type="text/javascript" th:src="@{/bootstrap/js/bootstrap.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/jquery.validate.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/localization/messages_zh.min.js}"></script>
    <script type="text/javascript" th:src="@{/layer/layer.js}"></script>
    <script type="text/javascript" th:src="@{/js/md5.min.js}"></script>
    <script type="text/javascript" th:src="@{/js/common.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/css/seckill.css}"/>
</head>
<body class="login-page">

<div class="login-banner">
    <span>&#9879; 秒杀</span>
</div>

<div class="login-card">
    <div class="login-title">用户登录</div>
    <form name="loginForm" id="loginForm" method="post">
        <input id="mobile" name="mobile" class="form-control" type="text" placeholder="请输入手机号码"
               required="true" minlength="11" maxlength="11"/>
        <input id="password" name="password" class="form-control" type="password" placeholder="请输入密码"
               required="true" minlength="6" maxlength="16"/>
        <button class="btn-login" type="submit" onclick="login()">登 录</button>
    </form>
    <button class="btn-reset" type="reset" onclick="reset()" style="width:100%;margin-top:10px;">重 置</button>
</div>

</body>
<script>
    function login() {
        $("#loginForm").validate({
            submitHandler: function (form) {
                doLogin();
            }
        });
    }
    function doLogin() {
        g_showLoading();
        var inputPass = $("#password").val();
        var salt = g_passsword_salt;
        var str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        var password = md5(str);
        $.ajax({
            url: "/login/do_login",
            type: "POST",
            data: {
                mobile: $("#mobile").val(),
                password: password
            },
            success: function (data) {
                layer.closeAll();
                if (data.code == 0) {
                    layer.msg("登录成功");
                    window.location.href = "/goods/to_list";
                } else {
                    layer.msg(data.msg);
                }
            },
            error: function () {
                layer.closeAll();
            }
        });
    }
</script>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/login.html
git commit -m "feat: redesign login page with JD seckill style"
```

---

### Task 3: 重写商品列表页 goods_list.html

**Files:**
- Modify: `src/main/resources/templates/goods_list.html`

> **不变量：** 保留 `th:each` 循环、`th:text` / `th:src` / `th:href` 表达式。`goodsList` 变量名不变。Redis 缓存逻辑不变（GoodsController 渲染此模板然后缓存）。

- [ ] **Step 1: 替换 goods_list.html**

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>秒杀商品列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script type="text/javascript" th:src="@{/js/jquery.min.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/bootstrap/css/bootstrap.min.css}"/>
    <script type="text/javascript" th:src="@{/bootstrap/js/bootstrap.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/jquery.validate.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/localization/messages_zh.min.js}"></script>
    <script type="text/javascript" th:src="@{/layer/layer.js}"></script>
    <script type="text/javascript" th:src="@{/js/md5.min.js}"></script>
    <script type="text/javascript" th:src="@{/js/common.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/css/seckill.css}"/>
</head>
<body>

<!-- 顶部导航 -->
<header class="seckill-header">
    <div class="logo"><i>&#9879;</i> 秒杀</div>
    <div class="user-info">
        <span class="nickname" th:if="${user != null}" th:text="${user.nickname}"></span>
        <a th:if="${user == null}" href="/login/to_login">请登录</a>
    </div>
</header>

<!-- 秒杀横幅 -->
<div class="seckill-banner">
    <div class="title"><span class="icon-flash">&#9889;</span> 限时秒杀</div>
    <div class="subtitle">全场超低价，手慢无！</div>
</div>

<!-- 商品卡片区 -->
<div class="seckill-container">
    <div class="seckill-card-row">
        <div class="seckill-card" th:each="goods : ${goodsList}">
            <a th:href="'/goods_detail.htm?goodsId=' + ${goods.id}">
                <img class="card-img" th:src="@{${goods.goodsImg}}" th:alt="${goods.goodsName}"/>
            </a>
            <div class="card-body">
                <div class="card-title" th:text="${goods.goodsName}"></div>
                <div class="card-price">
                    <span class="seckill-price">
                        <span class="unit">￥</span><span th:text="${goods.seckillPrice}"></span>
                    </span>
                    <span class="origin-price" th:text="'￥' + ${goods.goodsPrice}"></span>
                </div>
                <div class="card-stock">
                    <div class="stock-label" th:text="'剩余 ' + ${goods.stockCount} + ' 件'"></div>
                </div>
                <a th:href="'/goods_detail.htm?goodsId=' + ${goods.id}" class="btn btn-seckill">立即秒杀</a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/goods_list.html
git commit -m "feat: redesign goods list with JD-style card grid"
```

---

### Task 4: 重写商品详情页 goods_detail.html

**Files:**
- Modify: `src/main/resources/templates/goods_detail.html`

> **不变量：** 保留 `th:if` 状态判断（seckillStatus 0/1/2）、`#dates.format`、倒计时逻辑。`seckillForm` 表单保持 `method="post" action="/seckill/do_seckill"`。

- [ ] **Step 1: 替换 goods_detail.html**

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>商品详情</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script type="text/javascript" th:src="@{/js/jquery.min.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/bootstrap/css/bootstrap.min.css}"/>
    <script type="text/javascript" th:src="@{/bootstrap/js/bootstrap.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/jquery.validate.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/localization/messages_zh.min.js}"></script>
    <script type="text/javascript" th:src="@{/layer/layer.js}"></script>
    <script type="text/javascript" th:src="@{/js/md5.min.js}"></script>
    <script type="text/javascript" th:src="@{/js/common.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/css/seckill.css}"/>
</head>
<body>

<header class="seckill-header">
    <div class="logo"><a href="/goods/to_list" style="color:#fff;">&#9879; 秒杀</a></div>
    <div class="user-info">
        <span class="nickname" th:if="${user != null}" th:text="${user.nickname}"></span>
        <a th:if="${user == null}" href="/login/to_login">请登录</a>
    </div>
</header>

<div class="detail-container">
    <div th:if="${user eq null}" style="text-align:center;padding:20px;">
        <span>您还没有登录，请<a href="/login/to_login">登录</a>后再操作</span>
    </div>

    <div class="detail-card" th:if="${goods != null}">
        <div class="detail-img-side">
            <img th:src="@{${goods.goodsImg}}" th:alt="${goods.goodsName}"/>
        </div>
        <div class="detail-info-side">
            <div class="goods-name" th:text="${goods.goodsName}"></div>

            <div class="price-row">
                <span class="seckill-price">
                    <span class="unit">￥</span><span th:text="${goods.seckillPrice}"></span>
                </span>
                <span class="origin-price" th:text="'￥' + ${goods.goodsPrice}"></span>
            </div>

            <div class="info-item">
                秒杀时间：<span th:text="${#dates.format(goods.startDate, 'yyyy-MM-dd HH:mm:ss')}"></span>
            </div>
            <div class="info-item">
                库存数量：<span th:text="${goods.stockCount}"></span>
            </div>

            <!-- 倒计时区 -->
            <input type="hidden" id="remainSeconds" th:value="${remainSeconds}"/>
            <div id="seckillTip" style="margin:15px 0;font-size:15px;">
                <span th:if="${seckillStatus eq 0}">
                    <span style="color:#666;">距离开始：</span>
                    <span id="countDown" style="color:#c91623;font-size:20px;font-weight:bold;"
                          th:text="${remainSeconds}"></span>
                    <span style="color:#666;">秒</span>
                </span>
                <span th:if="${seckillStatus eq 1}" style="color:#c91623;font-weight:bold;">秒杀进行中</span>
                <span th:if="${seckillStatus eq 2}" style="color:#999;">秒杀已结束</span>
            </div>

            <form id="seckillForm" method="post" action="/seckill/do_seckill">
                <input type="hidden" name="goodsId" th:value="${goods.id}"/>
                <button class="btn-buy" type="submit" id="buyButton"
                        th:classappend="${seckillStatus != 1} ? 'disabled'">
                    立即秒杀
                </button>
            </form>
        </div>
    </div>
</div>

</body>
<script>
    $(function () {
        countDown();
    });
    function countDown() {
        var remainSeconds = $("#remainSeconds").val();
        var timeout;
        if (remainSeconds > 0) {
            timeout = setTimeout(function () {
                $("#countDown").text(remainSeconds - 1);
                $("#remainSeconds").val(remainSeconds - 1);
                countDown();
            }, 1000);
        } else if (remainSeconds == 0) {
            $("#buyButton").attr("disabled", false);
            $("#buyButton").removeClass("disabled");
            if (timeout) { clearTimeout(timeout); }
            $("#seckillTip").html('<span style="color:#c91623;font-weight:bold;">秒杀进行中</span>');
        } else {
            $("#seckillTip").html('<span style="color:#999;">秒杀已经结束</span>');
        }
    }
</script>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/goods_detail.html
git commit -m "feat: redesign goods detail with split layout"
```

---

### Task 5: 重写静态商品详情页 goods_detail.htm

**Files:**
- Modify: `src/main/resources/static/goods_detail.htm`

> **不变量：** 保留 JS 函数 `render(detail)` / `getDetail()` / `doSeckill()` / `getSeckillResult()` / `countDown()`。数据从 API `/goods/detail/{goodsId}` 获取。页面路径是 `goods_detail.htm?goodsId=X`。

- [ ] **Step 1: 替换 goods_detail.htm**

```html
<!DOCTYPE HTML>
<html>
<head>
    <title>商品详情</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/bootstrap/css/bootstrap.min.css"/>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/jquery-validation/jquery.validate.min.js"></script>
    <script type="text/javascript" src="/jquery-validation/localization/messages_zh.min.js"></script>
    <script type="text/javascript" src="/layer/layer.js"></script>
    <script type="text/javascript" src="/js/md5.min.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <link rel="stylesheet" type="text/css" href="/css/seckill.css"/>
</head>
<body>

<header class="seckill-header">
    <div class="logo"><a href="/goods/to_list" style="color:#fff;">&#9879; 秒杀</a></div>
    <div class="user-info">
        <span class="nickname" id="userNickname"></span>
        <span id="userTip" style="color:#ccc;">请登录</span>
    </div>
</header>

<div class="detail-container">
    <div class="detail-card">
        <div class="detail-img-side">
            <img id="goodsImg" src="" alt=""/>
        </div>
        <div class="detail-info-side">
            <div class="goods-name" id="goodsName"></div>

            <div class="price-row">
                <span class="seckill-price">
                    <span class="unit">￥</span><span id="seckillPrice"></span>
                </span>
                <span class="origin-price" id="goodsPrice"></span>
            </div>

            <div class="info-item">秒杀时间：<span id="startTime"></span></div>
            <div class="info-item">库存数量：<span id="stockCount"></span></div>

            <input type="hidden" id="remainSeconds"/>
            <div id="seckillTip" style="margin:15px 0;font-size:15px;"></div>

            <input type="hidden" name="goodsId" id="goodsId"/>
            <button class="btn-buy disabled" type="button" id="buyButton" onclick="doSeckill()">立即秒杀</button>
        </div>
    </div>
</div>

</body>
<script>
    function doSeckill() {
        $.ajax({
            url: "/seckill/do_seckill",
            type: "POST",
            data: { goodsId: $("#goodsId").val() },
            success: function (data) {
                if (data.code == 0) {
                    getSeckillResult($("#goodsId").val());
                } else {
                    layer.msg(data.msg);
                }
            },
            error: function () { layer.msg("客户端请求有误"); }
        });
    }

    function getSeckillResult(goodsId) {
        g_showLoading();
        $.ajax({
            url: "/seckill/result",
            type: "GET",
            data: { goodsId: $("#goodsId").val() },
            success: function (data) {
                if (data.code == 0) {
                    var result = data.data;
                    if (result < 0) {
                        layer.msg("对不起，秒杀失败");
                    } else if (result == 0) {
                        setTimeout(function () { getSeckillResult(goodsId); }, 50);
                    } else {
                        layer.confirm("恭喜你，秒杀成功！查看订单？", { btn: ["确定", "取消"] },
                            function () { window.location.href = "/order_detail.htm?orderId=" + result; },
                            function () { layer.closeAll(); });
                    }
                } else {
                    layer.msg(data.msg);
                }
            },
            error: function () { layer.msg("客户端请求有误"); }
        });
    }

    function render(detail) {
        var goods = detail.goods;
        var user = detail.user;
        if (user) {
            $("#userTip").hide();
            $("#userNickname").text(user.nickname);
        }
        $("#goodsName").text(goods.goodsName);
        $("#goodsImg").attr("src", goods.goodsImg);
        $("#startTime").text(new Date(goods.startDate).format("yyyy-MM-dd hh:mm:ss"));
        $("#remainSeconds").val(detail.remainSeconds);
        $("#goodsId").val(goods.id);
        $("#seckillPrice").text(goods.seckillPrice);
        $("#goodsPrice").text("￥" + goods.goodsPrice);
        $("#stockCount").text(goods.stockCount);
        countDown();
    }

    function getDetail() {
        var goodsId = g_getQueryString("goodsId");
        $.ajax({
            url: "/goods/detail/" + goodsId,
            type: "GET",
            success: function (data) {
                if (data.code == 0) { render(data.data); }
                else { layer.msg(data.msg); }
            },
            error: function () { layer.msg("客户端请求有误"); }
        });
    }

    function countDown() {
        var remainSeconds = $("#remainSeconds").val();
        var timeout;
        if (remainSeconds > 0) {
            $("#buyButton").addClass("disabled").attr("disabled", true);
            $("#seckillTip").html(
                '<span style="color:#666;">距离开始：</span>' +
                '<span id="countDown" style="color:#c91623;font-size:20px;font-weight:bold;">' +
                remainSeconds + '</span><span style="color:#666;">秒</span>'
            );
            timeout = setTimeout(function () {
                var sec = parseInt($("#remainSeconds").val()) - 1;
                $("#remainSeconds").val(sec);
                countDown();
            }, 1000);
        } else if (remainSeconds == 0) {
            $("#buyButton").removeClass("disabled").attr("disabled", false);
            $("#seckillTip").html('<span style="color:#c91623;font-weight:bold;">秒杀进行中</span>');
        } else {
            $("#buyButton").addClass("disabled").attr("disabled", true);
            $("#seckillTip").html('<span style="color:#999;">秒杀已经结束</span>');
        }
    }

    $(function () {
        getDetail();
    });
</script>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/static/goods_detail.htm
git commit -m "feat: redesign static goods detail page with JD style"
```

---

### Task 6: 重写订单详情页 order_detail.html

**Files:**
- Modify: `src/main/resources/templates/order_detail.html`

> **不变量：** 保留所有 `th:text` / `th:if` 表达式。`orderInfo`、`goods` 变量名不变。订单状态 0-5 的判断逻辑保留。

- [ ] **Step 1: 替换 order_detail.html**

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>订单详情</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script type="text/javascript" th:src="@{/js/jquery.min.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/bootstrap/css/bootstrap.min.css}"/>
    <script type="text/javascript" th:src="@{/bootstrap/js/bootstrap.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/jquery.validate.min.js}"></script>
    <script type="text/javascript" th:src="@{/jquery-validation/localization/messages_zh.min.js}"></script>
    <script type="text/javascript" th:src="@{/layer/layer.js}"></script>
    <script type="text/javascript" th:src="@{/js/md5.min.js}"></script>
    <script type="text/javascript" th:src="@{/js/common.js}"></script>
    <link rel="stylesheet" type="text/css" th:href="@{/css/seckill.css}"/>
</head>
<body>

<header class="seckill-header">
    <div class="logo"><a href="/goods/to_list" style="color:#fff;">&#9879; 秒杀</a></div>
</header>

<div class="order-container">
    <div class="order-success-header">
        <div class="check-icon">&#10003;</div>
        <h3>恭喜你，秒杀成功！</h3>
    </div>

    <div class="order-card">
        <div class="order-row">
            <span class="label">商品名称</span>
            <span class="value" th:text="${goods.goodsName}"></span>
        </div>
        <div class="order-row">
            <span class="label">商品图片</span>
            <span class="value"><img th:src="@{${goods.goodsImg}}" width="80" height="80"/></span>
        </div>
        <div class="order-row">
            <span class="label">订单价格</span>
            <span class="value" style="color:#c91623;font-weight:bold;" th:text="'￥' + ${orderInfo.goodsPrice}"></span>
        </div>
        <div class="order-row">
            <span class="label">下单时间</span>
            <span class="value" th:text="${#dates.format(orderInfo.createDate, 'yyyy-MM-dd HH:mm:ss')}"></span>
        </div>
        <div class="order-row">
            <span class="label">订单状态</span>
            <span class="value">
                <span th:if="${orderInfo.status eq 0}" style="color:#e1251b;">未支付</span>
                <span th:if="${orderInfo.status eq 1}" style="color:#ff9900;">待发货</span>
                <span th:if="${orderInfo.status eq 2}">已发货</span>
                <span th:if="${orderInfo.status eq 3}">已收货</span>
                <span th:if="${orderInfo.status eq 4}" style="color:#999;">已退款</span>
                <span th:if="${orderInfo.status eq 5}">已完成</span>
            </span>
        </div>
        <div class="order-row">
            <span class="label">收货人</span>
            <span class="value">延宇振 18812151525</span>
        </div>
        <div class="order-row">
            <span class="label">收货地址</span>
            <span class="value">广东省广州市</span>
        </div>
        <div style="text-align:center;">
            <button class="btn-pay" type="submit" id="payButton">立即支付</button>
        </div>
    </div>
</div>

</body>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/order_detail.html
git commit -m "feat: redesign order detail page with success header"
```

---

### Task 7: 重写静态订单详情页 order_detail.htm

**Files:**
- Modify: `src/main/resources/static/order_detail.htm`

> **不变量：** 保留 JS 函数 `render(detail)` / `getOrderDetail()`。API `/order/detail?orderId=X` 不变。

- [ ] **Step 1: 替换 order_detail.htm**

```html
<!DOCTYPE HTML>
<html>
<head>
    <title>订单详情</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/bootstrap/css/bootstrap.min.css"/>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/jquery-validation/jquery.validate.min.js"></script>
    <script type="text/javascript" src="/jquery-validation/localization/messages_zh.min.js"></script>
    <script type="text/javascript" src="/layer/layer.js"></script>
    <script type="text/javascript" src="/js/md5.min.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <link rel="stylesheet" type="text/css" href="/css/seckill.css"/>
</head>
<body>

<header class="seckill-header">
    <div class="logo"><a href="/goods/to_list" style="color:#fff;">&#9879; 秒杀</a></div>
</header>

<div class="order-container">
    <div class="order-success-header">
        <div class="check-icon">&#10003;</div>
        <h3>恭喜你，秒杀成功！</h3>
    </div>

    <div class="order-card">
        <div class="order-row">
            <span class="label">商品名称</span>
            <span class="value" id="goodsName"></span>
        </div>
        <div class="order-row">
            <span class="label">商品图片</span>
            <span class="value"><img id="goodsImg" src="" width="80" height="80"/></span>
        </div>
        <div class="order-row">
            <span class="label">订单价格</span>
            <span class="value" id="orderPrice" style="color:#c91623;font-weight:bold;"></span>
        </div>
        <div class="order-row">
            <span class="label">下单时间</span>
            <span class="value" id="createDate"></span>
        </div>
        <div class="order-row">
            <span class="label">订单状态</span>
            <span class="value" id="orderStatus"></span>
        </div>
        <div class="order-row">
            <span class="label">收货人</span>
            <span class="value">XXX 18812341234</span>
        </div>
        <div class="order-row">
            <span class="label">收货地址</span>
            <span class="value">北京市昌平区回龙观龙博一区</span>
        </div>
        <div style="text-align:center;">
            <button class="btn-pay" type="submit" id="payButton">立即支付</button>
        </div>
    </div>
</div>

</body>
<script>
    function render(detail) {
        var goods = detail.goods;
        var order = detail.order;
        $("#goodsName").text(goods.goodsName);
        $("#goodsImg").attr("src", goods.goodsImg);
        $("#orderPrice").text("￥" + order.goodsPrice);
        $("#createDate").text(new Date(order.createDate).format("yyyy-MM-dd hh:mm:ss"));
        var status = "";
        if (order.status == 0) { status = "未支付"; }
        else if (order.status == 1) { status = "待发货"; }
        else if (order.status == 2) { status = "已发货"; }
        else if (order.status == 3) { status = "已收货"; }
        else if (order.status == 4) { status = "已退款"; }
        else if (order.status == 5) { status = "已完成"; }
        $("#orderStatus").text(status);
    }

    function getOrderDetail() {
        var orderId = g_getQueryString("orderId");
        $.ajax({
            url: "/order/detail",
            type: "GET",
            data: { orderId: orderId },
            success: function (data) {
                if (data.code == 0) { render(data.data); }
                else { layer.msg(data.msg); }
            },
            error: function () { layer.msg("客户端请求有误"); }
        });
    }

    $(function () { getOrderDetail(); });
</script>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/static/order_detail.htm
git commit -m "feat: redesign static order detail page with JD style"
```

---

### Task 8: 重写秒杀失败页 seckill_fail.html

**Files:**
- Modify: `src/main/resources/templates/seckill_fail.html`

> **不变量：** 保留 `th:text="${errmsg}"` 表达式。

- [ ] **Step 1: 替换 seckill_fail.html**

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>秒杀失败</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" type="text/css" th:href="@{/bootstrap/css/bootstrap.min.css}"/>
    <link rel="stylesheet" type="text/css" th:href="@{/css/seckill.css}"/>
</head>
<body>

<header class="seckill-header">
    <div class="logo"><a href="/goods/to_list" style="color:#fff;">&#9879; 秒杀</a></div>
</header>

<div class="fail-container">
    <div class="fail-icon">&#9785;</div>
    <h3>秒杀失败：<span th:text="${errmsg}"></span></h3>
    <a href="/goods/to_list" class="btn-back">返回商品列表</a>
</div>

</body>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/seckill_fail.html
git commit -m "feat: redesign seckill fail page"
```

---

### Task 9: 验证与推送

- [ ] **Step 1: 确认所有文件存在**

```bash
ls -la src/main/resources/static/css/seckill.css
ls -la src/main/resources/templates/login.html
ls -la src/main/resources/templates/goods_list.html
ls -la src/main/resources/templates/goods_detail.html
ls -la src/main/resources/templates/order_detail.html
ls -la src/main/resources/templates/seckill_fail.html
ls -la src/main/resources/static/goods_detail.htm
ls -la src/main/resources/static/order_detail.htm
```

- [ ] **Step 2: 查看所有变更**

```bash
git status
git diff --stat
```

- [ ] **Step 3: 推送到 GitHub**

```bash
git push origin master
```
