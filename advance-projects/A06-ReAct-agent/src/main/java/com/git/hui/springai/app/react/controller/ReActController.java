package com.git.hui.springai.app.react.controller;

import com.git.hui.springai.app.react.service.LlmService;
import com.git.hui.springai.app.react.simple.CalculatorTools;
import com.git.hui.springai.app.react.simple.SimpleReActAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReAct Agent HTTP 接口
 */
@RestController
@RequestMapping("/api/react")
public class ReActController {
    private final ChatClient chatClient;
    private final CalculatorTools calculatorTools;

    public ReActController(LlmService llmService) {
        this.chatClient = llmService.getChatClient(null);
        this.calculatorTools = new CalculatorTools();
    }

    /**
     * 执行 ReAct Agent
     * @param question 用户问题
     * @return 处理结果
     */
    @GetMapping("/ask")
    public String ask(@RequestParam("question") String question) {
        List<ToolCallback> tools = calculatorTools.getTools();
        SimpleReActAgent agent = new SimpleReActAgent(chatClient, tools);
        return agent.run(question);
    }
}