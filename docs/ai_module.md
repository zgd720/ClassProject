# AI模块说明文档

## 模型信息
- **模型名称**：GLM-4-Flash
- **API提供方**：智谱AI (BigModel)
- **API地址**：`https://open.bigmodel.cn/api/paas/v4/chat/completions`
- **认证方式**：Bearer Token (API Key)
- **免费额度**：智谱AI为新用户提供免费额度，GLM-4-Flash模型当前免费

## Prompt设计

### 模板
```
你是一名大学辅导员，请根据以下学生数据给出学习建议：
平均绩点：{gpa}，挂科数：{failCount}，本学期借阅次数：{borrowCount}，日均消费金额：{avgConsume}元。
要求：建议具体、鼓励性强，不超过200字。
```

### 请求示例
```json
{
  "model": "glm-4-flash",
  "messages": [
    {
      "role": "user",
      "content": "你是一名大学辅导员，请根据以下学生数据给出学习建议：\n平均绩点：3.2，挂科数：0，本学期借阅次数：5，日均消费金额：28.50元。\n要求：建议具体、鼓励性强，不超过200字。"
    }
  ],
  "max_tokens": 300,
  "temperature": 0.7
}
```

### 响应示例
```
该同学整体表现良好，绩点3.2处于中上水平，没有挂科记录说明基础扎实。借阅5本图书反映出一定的自主学习意识，但可以进一步提升阅读量。日均消费28.5元属于正常范围，生活规律。

建议：1) 在保持现有成绩的基础上，挑战更高难度的专业课程，争取绩点突破3.5；2) 增加专业相关书籍的借阅频率，拓展知识深度；3) 保持当前良好的生活习惯，适当参加学术讲座和竞赛活动，提升综合竞争力。相信你一定能取得更大进步！
```

## 调用流程
1. 前端点击"生成AI建议"按钮，POST `/api/ai/suggestion` 传入学号
2. 后端收集学生数据：平均绩点、挂科数、借阅次数、日均消费金额
3. 构造Prompt字符串（已硬编码在`AiServiceImpl.java`中）
4. 通过RestTemplate向智谱AI API发送HTTPS POST请求
5. 设置5秒超时，解析响应JSON中的`choices[0].message.content`
6. 返回建议文本给前端展示

## 降级策略
以下情况触发降级，返回固定兜底文案：
- API调用超时（>5秒）
- 网络连接失败
- API返回非200状态码
- 响应JSON解析失败
- API Key无效或配额耗尽

### 兜底文案
> 当前无法获取AI建议，请稍后再试。建议您多与任课老师沟通，并合理安排学习时间。

## 限流与成本控制
- **并发控制**：前端按钮点击后禁用，避免重复提交
- **API配额**：GLM-4-Flash当前免费，但建议关注智谱AI官网的配额变更公告
- **缓存建议**（未实现）: 可考虑对同一学生的建议缓存24小时，避免重复调用

## 配置说明
在 `application.yml` 中配置：
```yaml
ai:
  api:
    url: https://open.bigmodel.cn/api/paas/v4/chat/completions
    key: your-api-key-here  # 替换为实际API Key
    model: glm-4-flash
```
