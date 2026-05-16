package com.example.academic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("consumption")
public class Consumption {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private LocalDateTime consumeTime;
    private BigDecimal amount;
    private String type;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
