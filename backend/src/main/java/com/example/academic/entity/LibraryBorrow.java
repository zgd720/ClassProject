package com.example.academic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("library_borrow")
public class LibraryBorrow {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private LocalDate borrowDate;
    private String bookName;
    private String category;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
