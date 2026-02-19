package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Paper;
import com.exam.entity.Question;
import com.exam.mapper.PaperMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperService extends ServiceImpl<PaperMapper, Paper> {
    
    private final QuestionService questionService;
    
    public Page<Paper> getPaperPage(Integer page, Integer limit, String title) {
        Page<Paper> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Paper::getTitle, title);
        }
        wrapper.orderByDesc(Paper::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    public void addPaper(Paper paper) {
        // 计算总分
        List<Question> questions = questionService.getQuestionsByIds(paper.getQuestionIds());
        int totalScore = questions.stream().mapToInt(Question::getScore).sum();
        paper.setTotalScore(totalScore);
        this.save(paper);
    }
    
    public void updatePaper(Paper paper) {
        if (paper.getQuestionIds() != null && !paper.getQuestionIds().isEmpty()) {
            List<Question> questions = questionService.getQuestionsByIds(paper.getQuestionIds());
            int totalScore = questions.stream().mapToInt(Question::getScore).sum();
            paper.setTotalScore(totalScore);
        }
        this.updateById(paper);
    }
    
    public List<Question> getPaperQuestions(Long paperId) {
        Paper paper = this.getById(paperId);
        if (paper == null) {
            return List.of();
        }
        return questionService.getQuestionsByIds(paper.getQuestionIds());
    }
}
