package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Paper;
import com.exam.entity.Question;
import com.exam.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
@CrossOrigin
public class PaperController {
    
    private final PaperService paperService;
    
    @GetMapping
    public Map<String, Object> getPapers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String title) {
        Page<Paper> pageResult = paperService.getPaperPage(page, limit, title);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return result;
    }
    
    @GetMapping("/{id}")
    public Paper getPaper(@PathVariable Long id) {
        return paperService.getById(id);
    }
    
    @GetMapping("/{id}/questions")
    public List<Question> getPaperQuestions(@PathVariable Long id) {
        return paperService.getPaperQuestions(id);
    }
    
    @PostMapping
    public Map<String, Object> addPaper(@RequestBody Paper paper) {
        paperService.addPaper(paper);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "添加成功");
        return result;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updatePaper(@PathVariable Long id, @RequestBody Paper paper) {
        paper.setId(id);
        paperService.updatePaper(paper);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deletePaper(@PathVariable Long id) {
        paperService.removeById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
