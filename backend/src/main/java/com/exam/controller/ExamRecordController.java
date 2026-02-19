package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.ExamRecord;
import com.exam.service.ExamRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-records")
@RequiredArgsConstructor
@CrossOrigin
public class ExamRecordController {
    
    private final ExamRecordService examRecordService;
    
    @GetMapping
    public Map<String, Object> getExamRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        Page<ExamRecord> pageResult = examRecordService.getExamRecordPage(page, limit, userId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return result;
    }
    
    @GetMapping("/{id}")
    public ExamRecord getExamRecord(@PathVariable Long id) {
        return examRecordService.getById(id);
    }
    
    @PostMapping("/start")
    public Map<String, Object> startExam(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long paperId = params.get("paperId");
        ExamRecord record = examRecordService.startExam(userId, paperId);
        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        return result;
    }
    
    @PostMapping("/{id}/submit")
    public Map<String, Object> submitExam(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String answers = params.get("answers");
        examRecordService.submitExam(id, answers);
        
        ExamRecord record = examRecordService.getById(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "提交成功");
        result.put("score", record.getScore());
        return result;
    }
}
