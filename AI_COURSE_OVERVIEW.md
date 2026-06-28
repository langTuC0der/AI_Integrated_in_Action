# 🤖 AI Integrated in Action — Tổng quan môn học

> **📌 File này dành cho AI đọc trước mỗi buổi dạy để nắm context môn học**  
> **Cập nhật lần cuối:** 2026-06-17  
> **Đối tượng học viên:** Lập trình viên Java / Sinh viên biết Spring Boot cơ bản  
> **Mục tiêu môn học:** Xây dựng các hệ thống AI thực chiến — từ chatbot, agent thông minh, RAG, voice AI đến computer vision — bằng Java & Spring AI

---

## 🗺️ Lộ trình 13 Sessions (tổng quan)

| Session | Tên | Loại | Trạng thái |
|---------|-----|------|-----------|
| S01 | Định hướng môn học | Lý thuyết | - |
| S02 | Tích hợp LLM & Chuẩn hóa dữ liệu với Spring AI | Lý thuyết + Demo | - |
| S03 | [Thực hành: LLM & Spring AI](file:///c:/Users/MChinh/Desktop/rikkei/AI/Session03-Spring-AI-Practice.md) | Thực hành | Hoàn thành |
| S04 | Xây dựng AI Agent & Function Calling | Lý thuyết + Demo | - |
| S05 | Vector Database & RAG | Lý thuyết + Demo | - |
| S06 | Thực hành: Vector DB, RAG, GraphQL | Thực hành | - |
| S07 | Hackathon | Project | - |
| S08 | Giám sát AI với Langfuse (LLMOps) | Lý thuyết + Demo | - |
| S09 | Thực hành: Langfuse Monitoring | Thực hành | - |
| S10 | Kiến trúc MCP (Model Context Protocol) | Lý thuyết + Demo | - |
| S11 | Thực hành cấu hình MCP | Thực hành | - |
| S12 | Xây dựng AI Email Automation Agent | Lý thuyết + Demo | - |
| S13 | Thực hành xây dựng AI Email Automation Agent | Thực hành | - |

---

## 📋 Chi tiết từng Session & Lesson

---

### 🎯 Session 01 — Định hướng môn học

- Giới thiệu tổng quan nội dung & kiến thức môn học
- Tại sao cần học môn này — hậu quả nếu không học
- Không học → không thể tích hợp AI vào sản phẩm thực tế, mất lợi thế cạnh tranh

---

### 🔧 Session 02 — Tích hợp LLM & Chuẩn hóa dữ liệu với Spring AI

#### Lesson 01 — Kiến trúc Spring AI & Loosely Coupled Design
- Tokenization, Context Window, Hyperparameters
- Tính toán chi phí AI (token pricing)
- Spring AI Abstraction: `ChatModel`, `Prompt`, `ChatResponse`
- Tư duy Loosely Coupled → dễ switch model mà không sửa code logic

#### Lesson 02 — REST Controller & Stream API
- Viết endpoint nhận yêu cầu từ client → chuyển sang LLM API
- Xử lý Streaming (Server-Sent Events)
- Tuning Hyperparameters: Temperature, Top-P qua `ChatOptions`

#### Lesson 03 — Chuẩn hóa Output với Structured Output API
- `StructuredOutputConverter`, `BeanOutputConverter`
- Parse JSON response từ LLM → Java Record / POJO (type-safe)

#### Lesson 04 — Module trích xuất dữ liệu (ETL với AI)
- REST Controller nhận prompt từ Client
- Gọi LLM API qua Spring AI
- Dùng `BeanOutputConverter` → AI trả về Java Record `BookingRequest`
- Lưu thẳng vào Database

---

### 💻 Session 03 — [Thực hành: LLM & Spring AI](file:///c:/Users/MChinh/Desktop/rikkei/AI/Session03-Spring-AI-Practice.md)

- Áp dụng kiến thức Session 02 vào bài tập thực tế có hướng dẫn chi tiết
- **Bài tập 1:** Multi-Model & Fallback Mechanism (Xử lý lỗi & Tự động chuyển đổi Model)
- **Bài tập 2:** CV Screener (Structured Output tự động trích xuất thông tin ứng viên)
- **Bài tập 3:** Invoice ETL Pipeline & Self-Correction (Trích xuất & tự động sửa lỗi dữ liệu hóa đơn)
- **Bài tập 4:** AI Safety & Security (Phòng chống Prompt Injection & Rate Limiting)

---

### 🤖 Session 04 — AI Agent & Function Calling

#### Lesson 01 — Kiến trúc AI Agent & Chat Memory
- Vòng lặp Reasoning của Agent
- Chat Memory: lưu context hội thoại, nhớ thông tin qua nhiều lượt

#### Lesson 02 — Cơ chế Function Calling trong Spring AI
- LLM nhận diện Intent → chọn Function phù hợp
- Spring AI gọi Java Method → trả kết quả về LLM → LLM tổng hợp Response

#### Lesson 03 — Đóng gói Spring Boot Service thành Tool
- Annotation `@Tool` / `@Bean` để đăng ký function với Agent
- Function Schema: mô tả tham số để LLM biết khi nào gọi

#### Lesson 04 — AI Booking Agent thực chiến
- Luồng: Nhận diện Intent → Chat Memory nhớ ngày → `getRoomAvailability()` check DB → `calculateTotalPrice()` tính tiền → Tổng hợp trả lời khách
- Ví dụ use case: đặt phòng khách sạn tự động

---

### 🧠 Session 05 — Vector Database & RAG

#### Lesson 01 — Embeddings (Vector hóa văn bản)
- Text → Embedding Model → Vector (768+ chiều)
- Embedding model: OpenAI text-embedding-ada hoặc Gemini

#### Lesson 02 — Cosine Similarity
- Đo độ tương đồng giữa 2 vector
- Top-K search: tìm K tài liệu liên quan nhất với query

#### Lesson 03 — Vector Database
- **PGVector** (SQL + Vector) → dùng Supabase free → ✅ phù hợp Java
- Milvus (dedicated, hiệu năng cao)
- Pinecone (cloud managed, dễ dùng)

#### Lesson 04 — RAG Chatbot từ tài liệu nội bộ
- Upload PDF/Markdown → Đọc & Chunking → Embedding → Lưu vào PGVector
- Khi user hỏi: Query → Embedding → Cosine Search → Top-K chunks → Đưa vào Prompt → LLM → Trả lời trong phạm vi tài liệu

---

### 💻 Session 06 — Thực hành: Vector DB, RAG, GraphQL với AI

- Xây dựng chatbot RAG hoàn chỉnh
- GraphQL endpoint cho AI queries

---

### 🏆 Session 07 — Hackathon

- Áp dụng toàn bộ kiến thức S01–S06
- 1 buổi full → sản phẩm AI MVP có thể demo

---

### 📊 Session 08 — Giám sát AI với Langfuse (LLMOps)

> 💰 Chi phí $0 — Docker self-host hoặc Hobby Plan (50k req/month free)

#### Lesson 01 — Kiến trúc LLMOps & Cấu hình Langfuse
- LLMOps là gì, tại sao cần giám sát AI
- Cài đặt và cấu hình Langfuse

#### Lesson 02 — LLM Tracing (Truy vết API)
- Langfuse SDK tích hợp vào Spring Boot
- Ghi nhận: latency, token count, cost per request

#### Lesson 03 — Quản lý Chi phí & Luồng AI Agent
- Dashboard: token usage, cost per request
- Trace luồng function calls của Agent

#### Lesson 04 — Quản lý Prompt tập trung & Đánh giá Output
- Langfuse Prompt Registry: version control cho Prompt
- A/B test prompt versions
- Evaluate output quality (LLM-as-judge)

---

### 💻 Session 09 — Thực hành: Langfuse Monitoring

---

### 🔌 Session 10 — Kiến trúc MCP (Model Context Protocol)

#### Lesson 01 — Tổng quan Kiến trúc MCP & Cấu hình Client
- Hiểu giao thức MCP, 3 thành phần cốt lõi (Prompts, Resources, Tools)
- Thực hành cấu hình các MCP Server mã nguồn mở có sẵn (GitHub, Postgres, FileSystem) vào các AI Client thực chiến như Antigravity IDE

#### Lesson 02 — Xây dựng MCP Server kết nối Hệ thống Nội bộ
- Sử dụng SDK (Java, Spring Boot) viết một MCP Server chạy qua giao thức chuẩn Stdio
- Khai báo các Resources để AI đọc dữ liệu trực tiếp từ Database nội bộ (PostgreSQL) hoặc file cấu hình hệ thống một cách an toàn

#### Lesson 03 — Tích hợp MCP Server với Spring AI (Java Ecosystem)
- Kết nối ứng dụng Spring Boot hiện tại với hệ sinh thái MCP Server
- Cơ chế định tuyến và chuyển đổi dữ liệu từ giao thức MCP sang cấu trúc ChatModel và ChatOptions của Spring AI

#### Lesson 04 — Xây dựng AI Data Analyst Agent qua MCP Tools
- Khai báo Tools trên MCP Server cho phép AI thực thi các câu lệnh chỉnh sửa dữ liệu hoặc kích hoạt API bên thứ ba
- Xây dựng một Agent tự đọc dữ liệu DB qua Resource → Tự phân tích → Tự dùng Tool xuất file báo cáo định dạng Markdown/Excel trả về hệ thống

---

### 💻 Session 11 — Thực hành cấu hình MCP

---

### 📧 Session 12 — Xây dựng AI Email Automation Agent

#### Lesson 01 — Cấu hình Hạ tầng Gửi Mail & Spring Boot Mail
- Đăng ký Google App Password, cấu hình giao thức SMTP (spring-boot-starter-mail) trong Spring Boot
- Viết dịch vụ Java thuần để gửi email định dạng Text và HTML Template (Thymeleaf) đảm bảo hạ tầng chạy thông suốt

#### Lesson 02 — Thiết kế Email Agent Core & Nhận diện Intent
- Xây dựng AI Agent nhận đầu vào là câu lệnh tự nhiên của User (ví dụ: "Gửi báo cáo doanh thu cho anh Quang")
- Sử dụng Spring AI Prompt Template để huấn luyện Agent tự bóc tách thông tin: Người nhận (To), Tiêu đề (Subject), và tóm tắt nội dung cần soạn thảo

#### Lesson 03 — Đóng gói SMTP Service thành Tool Call cho AI
- Sử dụng tính năng Function Calling của Spring AI (hoặc định nghĩa MCP Tool) để đóng gói hàm gửi Email của Java thành một công cụ
- Huấn luyện Agent biết cách tự động kích hoạt hàm này khi thu thập đủ thông tin từ người dùng

#### Lesson 04 — Xây dựng Hệ thống Trợ lý Thư ký Email Tự động (AI Email Secretary)
- Xây dựng một quy trình hoàn chỉnh: Agent tiếp nhận yêu cầu → Tự gọi API/Database nội bộ lấy dữ liệu → Tự tổng hợp và soạn thảo văn bản email chuyên nghiệp → Tự động kích hoạt Tool gửi email qua Gmail SMTP → Trả về trạng thái thông báo thành công cho Client

---

### 💻 Session 13 — Thực hành xây dựng AI Email Automation Agent

---

## 🛠️ Stack công nghệ tổng hợp

| Lớp | Công nghệ | Chi phí |
|-----|----------|---------|
| Framework | Spring Boot + Spring AI | Free |
| LLM | OpenAI GPT / Google Gemini Free Tier | Free / rất thấp |
| Vector DB | PGVector + Supabase | Free |
| Monitoring | Langfuse (Docker / Hobby Plan) | Free |
| STT/TTS | OpenAI Whisper (self-hosted) | Free |
| Computer Vision | OpenCV + YOLO | Free |

---

## 💡 Nhận xét & Đề xuất bổ sung (từ buổi review 2026-06-17)

### 🔴 Ưu tiên cao — Bắt buộc bổ sung

**1. Security & AI Safety (hiện chưa có)**
- Prompt Injection attacks — tấn công phổ biến nhất với AI app
- Input validation — sanitize prompt trước khi gọi LLM
- Rate limiting — tránh bị lạm dụng API key
- Secret management — không hardcode API key
- → Gợi ý thêm vào Session 02 hoặc Session 04

### 🟠 Ưu tiên trung bình — Nên có

**2. Image Generation lessons (Session 13 đề cập nhưng chưa có lesson)**
- Session 13 tên "Face Detection, Image Generation" nhưng S12 không có lesson nào về Image Generation
- Gợi ý: DALL-E / Stable Diffusion qua Spring AI
- Use case: tạo thumbnail, avatar từ mô tả text

**3. GraphQL lesson trong S05**
- Session 06 thực hành GraphQL với AI nhưng S05 chưa dạy GraphQL
- Gợi ý: Thêm Lesson 05 vào S05 — GraphQL schema cho AI query endpoints

**4. Error Handling trong AI calls**
- Timeout, retry, fallback model
- AI call thực tế hay lỗi — cần handle chuẩn
- Gợi ý: Thêm vào S02 L02 hoặc S04 L01

### 🟢 Ưu tiên thấp — Tốt nếu có

**5. CI/CD & Deployment**
- Docker hóa ứng dụng Spring AI
- Deploy lên cloud free tier (Railway/Render)
- Environment config cho AI keys (prod vs dev)

**6. Performance & Caching**
- Semantic caching (cache câu hỏi tương tự)
- Redis cache cho embedding results
- → Gợi ý thêm vào Session 08 (LLMOps)

---

## 📝 Ghi chú cho AI trợ giảng

- Học viên đang học **Java + Spring Boot** — giải thích concept qua lens Java
- Ưu tiên **code example thực tế** hơn lý thuyết trừu tượng
- Mỗi lesson nên có **1 use case thực tế** liên quan đến doanh nghiệp (khách sạn, chấm công, chatbot nội bộ...)
- Stack hoàn toàn **free** — không đề xuất tool tính phí nếu không cần thiết
- Hackathon ở S07 → các session trước cần đảm bảo học viên đủ kiến thức để tự build
