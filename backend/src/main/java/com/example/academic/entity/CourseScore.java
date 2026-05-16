package com.example.academic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course_score")
public class CourseScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private String semester;
    private String courseName;
    private BigDecimal score;
    private BigDecimal gpa;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
