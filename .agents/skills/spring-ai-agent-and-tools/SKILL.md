---
name: spring-ai-agent-and-tools
description: Expert guidelines and code patterns for building AI Agents, configuring Chat Memory, and registering Java methods as AI Tools using Spring AI (1.1.5 or newer) and Spring Boot.
---

# 🤖 Spring AI Agent & Tools (Version 1.1.5+)

This skill provides standard guidelines, architecture, and code templates for building AI Agents, managing conversational memory, and implementing declarative Tool Calling in the Spring Boot ecosystem.

---

## 1. Core Principles

### AI Agent (ReAct Pattern)
An AI Agent operates in a loop: **Thought (Reasoning) -> Action (Acting) -> Observation (Observing)**. It evaluates user intent, invokes external tools (databases, APIs), processes the output, and formulates the next step or final answer.

### Chat Memory
LLMs are stateless. To maintain context across a conversation, you must store and resend previous messages. 
- Use `ChatMemoryRepository` to persist messages.
- Use `MessageChatMemoryAdvisor` to automatically append history to prompts.
- **Critical Requirement (Spring AI 1.1.x+):** You **must** provide the `ChatMemory.CONVERSATION_ID` parameter during execution. Leaving it out throws an `IllegalArgumentException` at runtime.

---

## 2. Standard Code Patterns

### A. Configuring Chat Memory (`AgentConfig.java`)
Register a `ChatMemoryRepository` bean (e.g., in-memory for testing, or database-backed for production).

```java
package com.ai.function_calling.config;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }
}
```

### B. Building the Agent with Memory (`AgentChatService.java`)
Configure `ChatClient` with `MessageChatMemoryAdvisor` at build time, and supply the `CONVERSATION_ID` at runtime.

```java
package com.ai.function_calling.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class AgentChatService {

    private final ChatClient chatClient;

    public AgentChatService(ChatModel chatModel, ChatMemoryRepository chatMemoryRepository) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("Bạn là trợ lý ảo lịch sự. Hãy giúp khách hàng đặt phòng.")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemoryRepository).build())
                .build();
    }

    public String chat(String chatId, String message) {
        return this.chatClient.prompt()
                .user(message)
                // Mandatory parameter in Spring AI 1.1.x
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
```

---

## 3. Declarative Tool Calling (`@Tool` Annotation)

Instead of manually writing functional interfaces, use the `@Tool` annotation to expose standard Java methods as tools to the LLM.

### A. Defining the Tool Service (`HotelBookingService.java`)
Annotate methods in any `@Service` or `@Component` with `@Tool`. Use descriptive method names and parameters, or write clear Javadocs, because Spring AI uses them to generate the JSON Schema for the LLM.

```java
package com.ai.function_calling.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HotelBookingService {

    @Tool(description = "Kiểm tra số lượng phòng trống của một loại phòng cụ thể (STANDARD, DELUXE, SUITE)")
    public String checkRoomAvailability(String roomType) {
        log.info("🎯 Tool checkRoomAvailability được gọi cho loại: {}", roomType);
        // Business logic here
        return "Phòng loại " + roomType.toUpperCase() + " còn trống 3 phòng.";
    }

    @Tool(description = "Thực hiện đặt phòng và lưu thông tin vào hệ thống.")
    public String createBooking(String customerName, String roomType, String checkInDate, int nights) {
        log.info("🎯 Tool createBooking được gọi cho: {}, {}", customerName, roomType);
        // Business logic here
        return "Đặt phòng thành công! Mã đặt chỗ: BK-9921.";
    }
}
```

### B. Registering Tools in ChatClient
Pass the service instance directly to the `.defaultTools()` or `.tools()` method on the `ChatClient` builder.

```java
package com.ai.function_calling.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class HotelAgentService {

    private final ChatClient chatClient;

    public HotelAgentService(
            ChatModel chatModel,
            ChatMemoryRepository chatMemoryRepository,
            HotelBookingService hotelBookingService) {

        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                    Bạn là Trợ lý ảo đặt phòng tại khách sạn Rikkei Luxury Hotel.
                    Nhiệm vụ của bạn là kiểm tra thông tin phòng trống và hoàn tất việc đặt phòng.
                    Quy trình:
                    1. Kiểm tra phòng trống bằng checkRoomAvailability.
                    2. Nếu phòng trống còn, yêu cầu tên khách hàng và xác nhận trước khi gọi createBooking.
                    """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemoryRepository).build())
                .defaultTools(hotelBookingService) // Automatically scans and registers @Tool methods
                .build();
    }

    public String chatWithAgent(String chatId, String message) {
        return this.chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
```

---

## 4. Best Practices

1. **Explicit Descriptions:** Always provide a clear description in `@Tool(description = "...")`. If the description is vague, the LLM will fail to select the tool or will supply incorrect parameters.
2. **Strict Types:** Keep tool method parameters simple (primitive types, `String`, `LocalDate`, `int`) to ensure accurate JSON mapping by the LLM.
3. **Conversational Flow:** Direct the LLM using a clear System Prompt so it coordinates multiple tools in a logical sequence (e.g., check availability first, then calculate price, and finally create the booking upon confirmation).
4. **Error Handling:** Gracefully handle exceptions in tool methods. Spring AI will feed the error message back to the LLM, allowing it to ask the user for clarification or retry with corrected arguments.
