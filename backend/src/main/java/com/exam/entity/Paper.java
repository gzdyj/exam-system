package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_paper")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title; // 试卷标题
    private String questionIds; // 题目ID列表，逗号分隔
    private Integer totalScore; // 总分
    private Integer duration; // 考试时长（分钟）
    private Long createBy;
    private Integer status; // 1-启用 0-禁用
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
