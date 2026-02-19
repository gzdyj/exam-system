package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService extends ServiceImpl<QuestionMapper, Question> {
    
    public Page<Question> getQuestionPage(Integer page, Integer limit, Integer type, String keyword) {
        Page<Question> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Question::getType, type);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getContent, keyword);
        }
        wrapper.orderByDesc(Question::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    public List<Question> getRandomQuestions(Integer type, Integer count) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getType, type);
        wrapper.last("ORDER BY RAND() LIMIT " + count);
        return this.list(wrapper);
    }
    
    public List<Question> getQuestionsByIds(String questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Question::getId, questionIds.split(","));
        return this.list(wrapper);
    }
    
    // 获取所有模块列表（按analysis字段分组）
    public List<Map<String, Object>> getModules() {
        List<Question> questions = this.list();
        Map<String, Integer> moduleCount = new HashMap<>();
        
        for (Question q : questions) {
            String module = q.getAnalysis();
            if (module != null && !module.isEmpty()) {
                moduleCount.merge(module, 1, Integer::sum);
            }
        }
        
        List<Map<String, Object>> modules = new ArrayList<>();
        moduleCount.forEach((name, count) -> {
            Map<String, Object> module = new HashMap<>();
            module.put("name", name);
            module.put("count", count);
            modules.add(module);
        });
        
        // 按模块名称排序
        modules.sort((a, b) -> {
            String nameA = (String) a.get("name");
            String nameB = (String) b.get("name");
            if (nameA.startsWith("模块") && nameB.startsWith("模块")) {
                return nameA.compareTo(nameB);
            }
            return nameA.compareTo(nameB);
        });
        
        return modules;
    }
    
    // 练习模式：按模块获取题目（分页）
    public Page<Question> getQuestionsByModule(String module, Integer page, Integer limit) {
        Page<Question> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getAnalysis, module);
        wrapper.orderByAsc(Question::getId);
        return this.page(pageParam, wrapper);
    }
    
    // 考试模式：按模块随机抽取指定数量题目
    public List<Question> getRandomQuestionsByModule(String module, Integer count) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty() && !"全部".equals(module)) {
            wrapper.eq(Question::getAnalysis, module);
        }
        wrapper.last("ORDER BY RAND() LIMIT " + count);
        return this.list(wrapper);
    }
}
