package com.microservice.test_ai.service;

import com.microservice.test_ai.config.TeacherStylePrompt;
import com.microservice.test_ai.model.RagChunk;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class Session02RagService {

    private static final String DOCUMENT_PATH = "classpath:rag/Session02-Spring-AI-LLM.md";
    private static final int MAX_CHUNK_LENGTH = 2_800;
    private static final int TOP_K = 4;
    private static final Pattern HEADING_PATTERN = Pattern.compile("^#{1,6}\\s+.*");
    private static final Pattern NON_WORD_PATTERN = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit}]+");
    private static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "how", "in", "is", "it", "of",
        "on", "or", "that", "the", "this", "to", "what", "when", "where", "with",
        "anh", "ban", "bang", "bi", "cac", "cai", "can", "cho", "co", "cua", "da", "de", "den", "duoc",
        "gi", "hay", "hoi", "khong", "la", "lam", "mot", "nao", "nay", "neu", "nhung", "noi", "o",
        "ra", "sao", "se", "su", "ta", "tai", "thi", "trong", "tu", "va", "ve", "voi"
    );

    private final ChatModel chatModel;
    private final ResourceLoader resourceLoader;
    private final List<IndexedChunk> indexedChunks = new ArrayList<>();

    public Session02RagService(ChatModel chatModel, ResourceLoader resourceLoader) {
        this.chatModel = chatModel;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    void loadKnowledgeBase() throws IOException {
        Resource resource = resourceLoader.getResource(DOCUMENT_PATH);
        String markdown = resource.getContentAsString(StandardCharsets.UTF_8);
        List<RawChunk> rawChunks = splitMarkdown(markdown);

        indexedChunks.clear();
        for (int i = 0; i < rawChunks.size(); i++) {
            RawChunk chunk = rawChunks.get(i);
            indexedChunks.add(new IndexedChunk(
                i + 1,
                chunk.title(),
                chunk.content(),
                tokenize(chunk.title() + "\n" + chunk.content())
            ));
        }
    }

    public Map<String, Object> answer(String question) {
        List<RagChunk> chunks = retrieve(question);
        String context = buildContext(chunks);

        List<org.springframework.ai.chat.messages.Message> messages = List.of(
            new SystemMessage(TeacherStylePrompt.VIETNAMESE_AI_LECTURER + """

                Vai trò bổ sung: bạn là giảng viên phụ trách Session 02: Tích hợp LLM & Chuẩn hóa dữ liệu với Spring AI.
                Chỉ trả lời dựa trên phần CONTEXT được cung cấp từ tài liệu Session 02.
                Nếu context không đủ thông tin, hãy nói rõ là tài liệu Session 02 chưa có thông tin đó.
                Khi giải thích, hãy dẫn dắt như một bài giảng ngắn: khái niệm, lý do, ví dụ áp dụng.
                """),
            new UserMessage("""
                CONTEXT:
                %s

                CÂU HỎI:
                %s
                """.formatted(context, question))
        );

        ChatResponse response = chatModel.call(new Prompt(messages));
        String content = response.getResult().getOutput().getText();

        return Map.of(
            "content", content,
            "sources", chunks
        );
    }

    public List<RagChunk> retrieve(String question) {
        Set<String> queryTerms = tokenize(question);
        if (queryTerms.isEmpty()) {
            return List.of();
        }

        return indexedChunks.stream()
            .map(chunk -> new RagChunk(
                chunk.id(),
                chunk.title(),
                preview(chunk.content()),
                score(queryTerms, chunk)
            ))
            .filter(chunk -> chunk.score() > 0)
            .sorted(Comparator.comparingDouble(RagChunk::score).reversed())
            .limit(TOP_K)
            .toList();
    }

    public int chunkCount() {
        return indexedChunks.size();
    }

    private String buildContext(List<RagChunk> chunks) {
        if (chunks.isEmpty()) {
            return "Không tìm thấy đoạn tài liệu liên quan.";
        }

        StringBuilder context = new StringBuilder();
        for (RagChunk source : chunks) {
            IndexedChunk fullChunk = indexedChunks.get(source.id() - 1);
            context.append("[Nguồn ")
                .append(source.id())
                .append("] ")
                .append(fullChunk.title())
                .append("\n")
                .append(fullChunk.content())
                .append("\n\n");
        }
        return context.toString();
    }

    private List<RawChunk> splitMarkdown(String markdown) {
        List<RawChunk> chunks = new ArrayList<>();
        String currentTitle = "Session 02";
        StringBuilder current = new StringBuilder();

        for (String line : markdown.split("\\R")) {
            if (HEADING_PATTERN.matcher(line).matches() && current.length() > 0) {
                flushChunk(chunks, currentTitle, current.toString());
                current.setLength(0);
                currentTitle = cleanHeading(line);
            } else if (HEADING_PATTERN.matcher(line).matches()) {
                currentTitle = cleanHeading(line);
            }

            current.append(line).append('\n');
            if (current.length() >= MAX_CHUNK_LENGTH) {
                flushChunk(chunks, currentTitle, current.toString());
                current.setLength(0);
            }
        }

        flushChunk(chunks, currentTitle, current.toString());
        return chunks;
    }

    private void flushChunk(List<RawChunk> chunks, String title, String content) {
        String trimmed = content.trim();
        if (!trimmed.isBlank()) {
            chunks.add(new RawChunk(title, trimmed));
        }
    }

    private String cleanHeading(String line) {
        return line.replaceFirst("^#{1,6}\\s+", "").trim();
    }

    private Set<String> tokenize(String text) {
        String normalized = Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        String[] parts = NON_WORD_PATTERN.split(normalized);
        Set<String> terms = new LinkedHashSet<>();
        for (String part : parts) {
            if (part.length() >= 3 && !STOP_WORDS.contains(part)) {
                terms.add(part);
            }
        }
        return terms;
    }

    private double score(Set<String> queryTerms, IndexedChunk chunk) {
        double score = 0;
        Set<String> contentTerms = chunk.terms();
        Set<String> titleTerms = tokenize(chunk.title());

        for (String term : queryTerms) {
            if (contentTerms.contains(term)) {
                score += 1.0;
            }
            if (titleTerms.contains(term)) {
                score += 1.5;
            }
            if (containsPhrase(chunk.content(), term)) {
                score += 0.2;
            }
        }

        return score / Math.sqrt(Math.max(contentTerms.size(), 1));
    }

    private boolean containsPhrase(String content, String term) {
        return normalizeForSearch(content).contains(term);
    }

    private String normalizeForSearch(String text) {
        return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
    }

    private String preview(String content) {
        String compact = content.replaceAll("\\s+", " ").trim();
        if (compact.length() <= 500) {
            return compact;
        }
        return compact.substring(0, 500) + "...";
    }

    private record RawChunk(String title, String content) {}

    private record IndexedChunk(int id, String title, String content, Set<String> terms) {}
}
