package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_exam_record")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long paperId;
    private String answers; // 用户答案，JSON格式
    private Integer score; // 得分
    private Integer status; // 1-已完成 0-进行中
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
