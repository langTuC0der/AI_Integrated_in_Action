---
name: curriculum-generator
description: Guidelines, structural templates, and quality standards for generating new, premium Spring AI and Java curriculum learning materials matching the Session 04 style.
---

# 📚 Spring AI Course Curriculum Generator Guide

This skill guides the AI agent on how to generate new, high-quality, and highly structured learning materials (curriculum sessions and lessons) for the Java and Spring AI course. Whenever a user requests to "tạo học liệu", "soạn giáo án", "viết tài liệu chi tiết", or "generate learning materials" for a session or lesson, follow these instructions.

---

## 1. Quality & Style Guidelines

- **Target Audience:** Java Developers with basic Spring Boot knowledge.
- **Tone:** Professional, engaging, practical, and system-engineer-oriented.
- **Technology Stack:** Always use the latest stable versions of **Spring Boot (3.5.15+)** and **Spring AI (1.1.5+)** unless specified otherwise.
- **Consistency:** Ensure package names match the actual project package (e.g., `com.ai.function_calling` or the specific session package).
- **Code Quality:** All Java code snippets must be clean, syntactically correct, and use modern features (e.g., records, switch expressions, builder patterns). Avoid placeholders; provide full, compile-ready code structures.

---

## 2. Document Structure

Every detailed Session document must follow a standard Markdown format divided into lessons. Each lesson **must** contain exactly 5 sections:

```markdown
# 🔵 LESSON [Number] — [Lesson Title]

## I. LÝ THUYẾT
## II. WORKFLOW DIAGRAM
## III. THỰC HÀNH
## IV. 🎬 NOTEBOOKLM VIDEO PROMPT
## V. 📊 GOOGLE SLIDES PROMPT
```

---

## 3. Section Details & Templates

### SECTION I. LÝ THUYẾT (Theory)
- Provide deep conceptual explanations.
- Use analogies (e.g., comparing stateless LLMs to "waiters with short-term amnesia" and Chat Memory to a "notepad").
- Include comparison tables comparing different approaches (e.g., InMemory vs. Persistent Memory, or Function Beans vs. `@Tool`).
- Use clear bullet points and highlight key takeaways.

### SECTION II. WORKFLOW DIAGRAM
- Include a **Mermaid diagram** (Sequence Diagram, Flowchart, or Architecture Diagram) to visualize the data flow between:
  - 👤 Client / Browser
  - 💻 Spring Boot Application
  - 💾 State / Database / Memory
  - ☁️ LLM Provider / API
- Include an **📸 IMAGE PROMPT** at the end of the section, describing a premium, high-tech, isometric 3D illustration of the concept (using rich aesthetics, neon accents, dark backgrounds) for DALL-E image generation. The visual language is Vietnamese, with only technical terms retained in English.

### SECTION III. THỰC HÀNH (Practice)
- Provide step-by-step Java code snippets to build the feature.
- Follow a clean architectural flow:
  1. **DTOs / Models** (Records, annotations like `@JsonPropertyDescription` to help LLM understand).
  2. **Services / Configurations** (Annotated with `@Service`, `@Configuration`, or `@Component`, using `@Tool`, `ChatClient.builder`, etc.).
  3. **REST Controllers** (Exposing endpoints with proper request mappings).
- Include a **🧪 Hướng dẫn kiểm thử (Testing Lab)** section at the end of the practice, providing exact `curl` or Postman request commands and describing the expected console logs and JSON responses.

### SECTION IV. 🎬 NOTEBOOKLM VIDEO PROMPT
Generate a detailed prompt to feed into Google NotebookLM for audio/video overview generation:
- **Style Prompt:** Define the voice, tone (energetic, system engineer), and language (Vietnamese, keeping English technical terms).
- **Audio Content:** Provide a timed script outline (e.g., `[00:00] Hook`, `[02:00] Concept`, `[05:00] Code Analysis`, `[08:00] Live Demo`, `[11:00] Conclusion`).

### SECTION V. 📊 GOOGLE SLIDES PROMPT
Generate a presentation slide outline:
- **Overall Style:** Define the color palette (e.g., Modern Tech Blue, Dark Developer Theme), fonts, and aesthetics.
- **Slides:** Outline exactly 10 slides, detailing the title, layout, and bullet points for each slide.

---

## 4. Execution Example (For the AI Agent)

When asked: *"Hãy soạn học liệu chi tiết cho Session 12: Xây dựng AI Email Automation Agent"*, the agent should:
1. Load this `curriculum-generator` skill.
2. Structure the output into the 4 specified lessons from the course overview.
3. Apply the 5-section format to each lesson, ensuring Spring Boot 3.5.15 and Spring AI 1.1.5 code patterns (such as mail sender tool calling) are written flawlessly.
