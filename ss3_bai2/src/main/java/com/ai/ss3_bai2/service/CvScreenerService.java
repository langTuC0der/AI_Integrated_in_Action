package com.ai.ss3_bai2.service;


import com.ai.ss3_bai2.model.CandidateEvaluation;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CvScreenerService {
    private final ChatModel chatModel;
    // Sử dụng model dự phòng OpenAI cho tác vụ trích xuất cấu trúc dữ liệu phức tạp
    public CvScreenerService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    public CandidateEvaluation evaluateCV(String cvText, String jobDescription){
        BeanOutputConverter<CandidateEvaluation> converter = new BeanOutputConverter<>(CandidateEvaluation.class);
        // 2. Định nghĩa Prompt Template hướng dẫn chi tiết cho AI
        String promptText = """
            Bạn là một chuyên gia tuyển dụng nhân sự công nghệ thông tin (IT Recruiter).
            Nhiệm vụ của bạn là phân tích văn bản CV của ứng viên dưới đây và đánh giá độ phù hợp của họ đối với Mô tả công việc (Job Description - JD) được cung cấp.
            
            Hãy trích xuất thông tin cá nhân, kỹ năng, các dự án trong quá khứ và chấm điểm mức độ phù hợp trên thang điểm 10.
            
            Yêu Cầu Đặc Biệt:
            - Điểm số suitabilityScore phải phản ánh chính xác kinh nghiệm thực tế ghi trong CV so với yêu cầu của JD.
            - Nếu thông tin nào không tìm thấy (như số điện thoại, email), hãy để giá trị null.
            - Phân tích kỹ thuật các dự án ứng viên đã thực hiện và đưa vào pastProjects.
            
            Mô tả công việc (JD):
            {jd}
            
            Văn bản CV của ứng viên:
            {cv}
            
            {formatInstructions}
            """;
        PromptTemplate template = new PromptTemplate(promptText);
        Prompt prompt = template.create(
          Map.of(
                  "jd",jobDescription,
                  "cv", cvText,
                  "formatInstructions", converter.getFormat()
          )
        );
        // 3. Gọi LLM
        String rawResponse = chatModel.call(prompt).getResult().getOutput().getText();

        // 4. Convert kết quả thô sang Java Object và trả về
        return converter.convert(rawResponse);
    }
}
