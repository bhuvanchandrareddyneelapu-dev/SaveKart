package com.savekart.controller;

import com.savekart.service.AIShoppingAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AIAssistantController {

    @Autowired
    private AIShoppingAssistantService aiShoppingAssistantService;

    @PostMapping("/assistant")
    public ResponseEntity<Map<String, Object>> processAiQuery(@RequestBody Map<String, String> payload) {
        String query = payload.getOrDefault("query", "");
        return ResponseEntity.ok(aiShoppingAssistantService.processQuery(query));
    }
}
