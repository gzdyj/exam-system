package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.ExamRecord;
import com.exam.entity.Paper;
import com.exam.entity.Question;
import com.exam.mapper.ExamRecordMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamRecordService extends ServiceImpl<ExamRecordMapper, ExamRecord> {
    
    private final PaperService paperService;
    private final QuestionService questionService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Page<ExamRecord> getExamRecordPage(Integer page, Integer limit, Long userId, Integer status) {
        Page<ExamRecord> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ExamRecord::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(ExamRecord::getStatus, status);
        }
        wrapper.orderByDesc(ExamRecord::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    public ExamRecord startExam(Long userId, Long paperId) {
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setPaperId(paperId);
        record.setStatus(0);
        record.setStartTime(LocalDateTime.now());
        record.setScore(0);
        this.save(record);
        return record;
    }
    
    public void submitExam(Long recordId, String answers) {
        ExamRecord record = this.getById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        record.setAnswers(answers);
        record.setStatus(1);
        record.setSubmitTime(LocalDateTime.now());
        
        // 自动评分
        int score = calculateScore(record);
        record.setScore(score);
        
        this.updateById(record);
    }
    
    public int calculateScore(ExamRecord record) {
        Paper paper = paperService.getById(record.getPaperId());
        if (paper == null || paper.getQuestionIds() == null) {
            return 0;
        }
        
        List<Question> questions = questionService.getQuestionsByIds(paper.getQuestionIds());
        Map<String, String> userAnswers;
        try {
            userAnswers = objectMapper.readValue(record.getAnswers(), new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            return 0;
        }
        
        int totalScore = 0;
        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId().toString());
            if (userAnswer != null && userAnswer.equalsIgnoreCase(question.getAnswer())) {
                totalScore += question.getScore();
            }
        }
        return totalScore;
    }
}
