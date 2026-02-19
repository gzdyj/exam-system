package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String content; // 题目内容
    private Integer type; // 1-选择题 2-判断题
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer; // 正确答案
    private Integer score; // 分值
    private String analysis; // 解析
    private Long createBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
