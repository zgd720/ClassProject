package com.example.academic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("risk_log")
public class RiskLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private String riskLevel;
    private String reason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
