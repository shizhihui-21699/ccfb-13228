# 印章图像鉴别服务（seal-forensics）

针对合同审核场景：供应商提交的盖章文件中，公章可能是从别的文件抠图粘贴的。
本服务上传扫描件/照片后自动定位印章区域，检测伪造迹象，并与登记的真实印章比对。

## 技术栈

- **Play Framework 3.0 + Scala 2.13**（Web 服务与页面）
- **OpenCV 4.9**（`org.openpnp:opencv`，自带各平台原生库）做图像分析
- **Java2D** 合成样例图片（纸面、合同文字、弧形文字公章）

## 运行

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17   # 任意 JDK 17+ 即可
sbt run
# 打开 http://localhost:9000
```

> 注意：dev 模式修改代码触发热重载后需重启 `sbt run`（OpenCV 原生库不能在新 classloader 重复加载）。

## 功能

- **印章定位**：HSV 红色分割 + 形态学清理 + 轮廓筛选（面积/长宽比/圆度）
- **伪造迹象检测**（每项 0~100 分）：
  - 墨迹边缘：沿质心射线测墨迹→纸面过渡带宽度与均匀度，数字抠图边缘整齐得反常
  - 拼接融合痕迹：印章外 5~65px 径向亮度剖面，粘贴块羽化边界会形成缓变台阶
  - 光照一致性：印面透出纸底亮度 vs 周边纸面亮度偏差
  - 局部重采样：印面 vs 周边纸面噪声强度（MAD，对墨迹邻近像素鲁棒）
  - JPEG 块效应：8x8 网格显著性（与网格偏移无关），区域内外对比发现局部 JPEG 图块
  - 重复印章：同文档两枚印章图像级完全一致 → 复制粘贴
- **登记章比对**：归一化墨迹掩码 + 旋转搜索对齐，输出 IoU、Hu 矩形状距离和差异图（灰=一致，红=登记章有而检材缺，蓝=检材多出）
- **可信度聚合**：加权平均 + 强证据增强（top-2 指标），≥65 高风险，35~65 存疑

## 页面/接口

| 路由 | 说明 |
|---|---|
| `GET /` | 首页：上传检测、样例演示、登记印章库 |
| `POST /analyze` | 上传图片分析（multipart 字段 `image`） |
| `GET /sample/genuine` `GET /sample/forged` | 合成真/伪样例并立即分析 |
| `POST /register` | 登记真章（字段 `name` + `image`） |
| `POST /register-sample` | 一键登记样例真章 |
| `GET /report/:id` / `GET /api/report/:id` | HTML / JSON 报告 |
| `GET /file/:id/:name` | 报告产物（标注图、差异图） |

## 样例合成原理

- **真章样例**：纸面（噪声+纤维+光照梯度）→ 合同文字 → 公章旋转 6.5°、油墨颗粒质感、缺墨点、轻微洇散 → 整图统一 JPEG q92
- **伪造样例**：印章渲染在另一张更亮的"来源纸"上 → 缩放重采样 → 局部 JPEG q50 重压缩 → 羽化椭圆蒙版贴到本文档 → 存 PNG（无整图压缩）

当前基线：真章样例综合风险 0%，伪造样例 75%（高风险）。

## 目录

```
app/services/SealDetector.scala     印章定位
app/services/ForgeryAnalyzer.scala  五项伪造迹象指标
app/services/SealMatcher.scala      登记章比对（IoU/Hu矩/差异图）
app/services/SealRegistry.scala     登记印章库（data/seals/）
app/services/SampleGenerator.scala  样例合成
app/services/Pipeline.scala         分析流水线与标注出图
data/results/<id>/                  每次分析的产物（报告在内存中，重启丢失）
```
