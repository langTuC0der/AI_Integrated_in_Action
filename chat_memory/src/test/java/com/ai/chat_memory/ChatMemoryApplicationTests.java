package com.ai.chat_memory;

import org.junit.jupiter.api.Test;

class ChatMemoryApplicationTests {

    @Test
    void contextLoads() {
        try {
            Class<?> clazz = Class.forName("org.springframework.ai.chat.memory.MessageWindowChatMemory");
            System.out.println("=== MessageWindowChatMemory Methods ===");
            for (java.lang.reflect.Method m : clazz.getMethods()) {
                if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                    System.out.println("Static: " + m.toString());
                } else {
                    System.out.println("Instance: " + m.toString());
                }
            }
            System.out.println("=== Declared Classes ===");
            for (Class<?> c : clazz.getDeclaredClasses()) {
                System.out.println("Inner: " + c.getName());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
