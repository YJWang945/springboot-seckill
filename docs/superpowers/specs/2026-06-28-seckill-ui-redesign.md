# 秒杀系统前端重设计

## 目标
模仿京东秒杀风格，重写全部前端页面，提升视觉档次和用户体验。

## 技术约束
- 保持 Thymeleaf + Bootstrap 3 + jQuery
- 不动后端代码
- 复用现有静态资源（jquery、layer、md5、bootstrap、validation）

## 配色方案
| 用途 | 色值 |
|------|------|
| 主色 | #c91623 |
| 价格 | #e1251b |
| 金色点缀 | #ff9900 |
| 导航 | #222 |
| 背景 | #f5f5f5 |

## 页面改动

### 1. 登录页 (login.html)
- 顶部红色品牌 banner + 居中白色登录卡片
- 圆角输入框，红色登录按钮
- 浅灰背景

### 2. 商品列表页 (goods_list.html)
- 新增顶部导航栏（秒杀logo + 用户信息）
- 秒杀横幅：大标题 + 主倒计时
- 商品卡片网格（3列），每卡片：图片 + 名称 + 原价划线 + 秒杀价红字 + 库存进度条 + 秒杀按钮
- 替换原有 table

### 3. 商品详情页 (goods_detail.html + goods_detail.htm)
- 顶部导航栏 + 左右分栏布局
- 左：大图；右：商品名 + 价格区 + 倒计时 + 秒杀按钮
- 倒计时用红底白字大数码
- 按钮红色圆角大按钮

### 4. 订单详情页 (order_detail.html + order_detail.htm)
- 成功图标 + 订单信息卡片
- 红色支付按钮

### 5. 秒杀失败页 (seckill_fail.html)
- 居中提示 + 返回商品列表按钮

## 文件清单
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/goods_list.html`
- `src/main/resources/templates/goods_detail.html`
- `src/main/resources/templates/order_detail.html`
- `src/main/resources/templates/seckill_fail.html`
- `src/main/resources/static/goods_detail.htm`
- `src/main/resources/static/order_detail.htm`
- `src/main/resources/static/css/seckill.css` (新增)

## 验收标准
- 所有页面风格统一
- 移动端响应式不崩
- 秒杀流程（登录→列表→详情→秒杀→订单）完整可用
- 不破坏现有后端缓存逻辑
