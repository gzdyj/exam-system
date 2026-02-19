package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Question;
import com.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin
public class QuestionController {
    
    private final QuestionService questionService;
    
    @GetMapping
    public Map<String, Object> getQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword) {
        Page<Question> pageResult = questionService.getQuestionPage(page, limit, type, keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return result;
    }
    
    @GetMapping("/random")
    public Map<String, Object> getRandomQuestions(
            @RequestParam Integer type,
            @RequestParam(defaultValue = "10") Integer count) {
        List<Question> questions = questionService.getRandomQuestions(type, count);
        Map<String, Object> result = new HashMap<>();
        result.put("list", questions);
        return result;
    }
    
    // 获取所有模块列表
    @GetMapping("/modules")
    public Map<String, Object> getModules() {
        List<Map<String, Object>> modules = questionService.getModules();
        Map<String, Object> result = new HashMap<>();
        result.put("list", modules);
        return result;
    }
    
    // 练习模式：按模块获取题目（分页）
    @GetMapping("/practice")
    public Map<String, Object> getPracticeQuestions(
            @RequestParam String module,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        Page<Question> pageResult = questionService.getQuestionsByModule(module, page, limit);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("module", module);
        return result;
    }
    
    // 考试模式：按模块随机抽取指定数量题目
    @GetMapping("/exam")
    public Map<String, Object> getExamQuestions(
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "100") Integer count) {
        List<Question> questions = questionService.getRandomQuestionsByModule(module, count);
        Map<String, Object> result = new HashMap<>();
        result.put("list", questions);
        result.put("total", questions.size());
        result.put("module", module != null ? module : "全部");
        return result;
    }
    
    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable Long id) {
        return questionService.getById(id);
    }
    
    @PostMapping
    public Map<String, Object> addQuestion(@RequestBody Question question) {
        questionService.save(question);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "添加成功");
        return result;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        questionService.updateById(question);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteQuestion(@PathVariable Long id) {
        questionService.removeById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
