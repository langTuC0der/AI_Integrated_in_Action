// Interactive Learning Page Logic
document.addEventListener("DOMContentLoaded", () => {
  // Navigation State
  let currentSession = "home"; // "home", "s4", "s5"
  let currentLesson = 1;       // 1 to 4
  
  // Elements
  const sidebar = document.querySelector(".sidebar");
  const mainContent = document.querySelector(".main-content");
  
  // Initialize Page State
  switchSession("home");

  // Session Selector Handler
  document.querySelectorAll(".session-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const session = btn.getAttribute("data-session");
      switchSession(session);
    });
  });

  // Logo Brand click redirects to Home
  document.querySelector(".brand").addEventListener("click", () => {
    switchSession("home");
  });

  // Switch Session Logic
  function switchSession(session) {
    currentSession = session;
    
    // Update active button state in sidebar
    document.querySelectorAll(".session-btn").forEach(btn => {
      btn.classList.toggle("active", btn.getAttribute("data-session") === session);
    });

    // Update Header Meta and visibility
    const headerMeta = document.getElementById("header-session-title");
    const headerPills = document.querySelectorAll(".header .tech-pill");
    
    if (session === "home") {
      headerMeta.textContent = "Hệ thống học liệu AI Integrated in Action";
      headerPills.forEach(p => p.style.display = "none");

      // Hide sidebar subnavs
      document.querySelectorAll(".lesson-navigation").forEach(nav => {
        nav.style.display = "none";
      });

      // Show home view section, hide all lesson sections
      document.querySelectorAll(".lesson-section").forEach(sec => {
        sec.classList.remove("active");
      });
      document.getElementById("home_view").classList.add("active");
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      headerPills.forEach(p => p.style.display = "inline-block");
      if (session === "s4") {
        headerMeta.textContent = "Session 04 — AI Agent & Function Calling";
      } else {
        headerMeta.textContent = "Session 05 — Vector Database & RAG";
      }

      // Toggle Navigation Items in Sidebar
      document.querySelectorAll(".lesson-navigation").forEach(nav => {
        nav.style.display = nav.getAttribute("id") === `${session}-nav` ? "flex" : "none";
      });

      // Hide home view
      document.getElementById("home_view").classList.remove("active");

      // Switch to first lesson of this session
      switchLesson(session, 1);
    }
  }

  // Switch Lesson Logic
  function switchLesson(session, lessonNum) {
    currentLesson = parseInt(lessonNum);
    const lessonId = `${session}_l${lessonNum}`;

    // Update active state in sidebar nav
    document.querySelectorAll(".lesson-nav-btn").forEach(btn => {
      btn.classList.toggle("active", btn.getAttribute("data-lesson") === lessonId);
    });

    // Hide all lesson sections, show active one
    document.querySelectorAll(".lesson-section").forEach(sec => {
      sec.classList.toggle("active", sec.getAttribute("id") === lessonId);
    });

    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });

    // Initialize/Render Mermaid diagrams in the active lesson
    setTimeout(() => {
      initializeMermaidForLesson(lessonId);
    }, 100);

    // Initialize Simulator for the active lesson
    initSimulatorForLesson(session, lessonNum);
  }

  // Bind homepage cards button clicks
  document.querySelectorAll(".course-card:not(.locked) .card-action-btn, .course-card:not(.locked)").forEach(card => {
    card.addEventListener("click", (e) => {
      // Find the card container
      const cardContainer = card.closest(".course-card");
      if (cardContainer.classList.contains("locked")) return;
      
      const session = cardContainer.getAttribute("data-session");
      switchSession(session);
      e.stopPropagation();
    });
  });

  // Setup Sidebar Lesson Clicks
  document.querySelectorAll(".lesson-nav-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const lessonData = btn.getAttribute("data-lesson"); // e.g., "s4_l2"
      const [session, lessonPart] = lessonData.split("_");
      const lessonNum = lessonPart.replace("l", "");
      switchLesson(session, lessonNum);
    });
  });

  // Tab switching logic within lessons
  document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const tabId = btn.getAttribute("data-tab");
      const lessonSection = btn.closest(".lesson-section");
      
      // Update tab buttons state
      lessonSection.querySelectorAll(".tab-btn").forEach(tBtn => {
        tBtn.classList.toggle("active", tBtn === btn);
      });

      // Update tab panes state
      lessonSection.querySelectorAll(".tab-pane").forEach(pane => {
        pane.classList.toggle("active", pane.getAttribute("id") === `${lessonSection.id}_pane_${tabId}`);
      });

      // Re-render Mermaid if switching to diagrams tab
      if (tabId === "diagram") {
        setTimeout(() => {
          initializeMermaidForLesson(lessonSection.id);
        }, 100);
      }
    });
  });

  // Prompt Drawer Toggle
  document.querySelectorAll(".prompt-drawer-header").forEach(header => {
    header.addEventListener("click", () => {
      const body = header.nextElementSibling;
      const arrow = header.querySelector("i");
      
      if (body.style.display === "none" || !body.style.display) {
        body.style.display = "block";
        arrow.style.transform = "rotate(180deg)";
      } else {
        body.style.display = "none";
        arrow.style.transform = "rotate(0deg)";
      }
    });
  });

  // Copy Code Functionality
  window.copyCode = function(button, elementId) {
    const codeElement = document.getElementById(elementId);
    if (!codeElement) return;

    const originalHTML = button.innerHTML;
    
    // Copy to clipboard
    navigator.clipboard.writeText(codeElement.textContent).then(() => {
      button.innerHTML = '<i class="fas fa-check"></i> Đã copy!';
      button.style.borderColor = "var(--success)";
      button.style.color = "var(--success)";

      setTimeout(() => {
        button.innerHTML = originalHTML;
        button.style.borderColor = "";
        button.style.color = "";
      }, 2000);
    }).catch(err => {
      console.error("Lỗi copy: ", err);
    });
  };

  // Mermaid Render Safe Trigger
  function initializeMermaidForLesson(lessonId) {
    const container = document.getElementById(lessonId);
    if (!container) return;
    
    // Find unrendered mermaid blocks inside the visible tab
    const mermaidPane = container.querySelector(".tab-pane.active .mermaid");
    if (mermaidPane && !mermaidPane.hasAttribute("data-processed")) {
      try {
        if (window.mermaid) {
          window.mermaid.init(undefined, mermaidPane);
        }
      } catch (err) {
        console.error("Mermaid render error:", err);
      }
    }
  }

  // --- INTERACTIVE SIMULATOR SYSTEM ---
  const simulatorScenarios = {
    s4_l1: {
      title: "Simulate: Chat Memory Flow",
      nodes: ["user", "spring", "llm", "db"],
      scenarios: [
        { label: "Turn 1: User says Hello", value: "s4_l1_t1" },
        { label: "Turn 2: Book Deluxe Room", value: "s4_l1_t2" }
      ],
      run: runS4L1Simulation
    },
    s4_l2: {
      title: "Simulate: Function Calling Mechanics",
      nodes: ["user", "spring", "llm"],
      scenarios: [
        { label: "Convert 100 USD to VND", value: "s4_l2_run" }
      ],
      run: runS4L2Simulation
    },
    s4_l3: {
      title: "Simulate: Spring Service as @Tool Scan",
      nodes: ["spring", "llm"],
      scenarios: [
        { label: "Scan CustomerSupportService & Query C002 Points", value: "s4_l3_run" }
      ],
      run: runS4L3Simulation
    },
    s4_l4: {
      title: "Simulate: Multi-Tool Booking Agent (Practice)",
      nodes: ["user", "spring", "llm", "db"],
      scenarios: [
        { label: "Flow: Book room Deluxe 2 nights", value: "s4_l4_run" }
      ],
      run: runS4L4Simulation
    },
    s5_l1: {
      title: "Simulate: Text Vectorization",
      nodes: ["user", "spring", "llm"],
      scenarios: [
        { label: "Vectorize text: 'Rikkei Education'", value: "s5_l1_run" }
      ],
      run: runS5L1Simulation
    },
    s5_l2: {
      title: "Simulate: Cosine Similarity",
      nodes: ["spring"],
      scenarios: [
        { label: "Calculate angle between Vector A & B", value: "s5_l2_run" }
      ],
      run: runS5L2Simulation
    },
    s5_l3: {
      title: "Simulate: PGVector HNSW Indexing Search",
      nodes: ["spring", "db"],
      scenarios: [
        { label: "Semantic query: 'hút thuốc phạt bao nhiêu?'", value: "s5_l3_run" }
      ],
      run: runS5L3Simulation
    },
    s5_l4: {
      title: "Simulate: Complete RAG Chatbot Flow (Practice)",
      nodes: ["user", "spring", "llm", "db"],
      scenarios: [
        { label: "Ask hotel policies & Get AI Answer", value: "s5_l4_run" }
      ],
      run: runS5L4Simulation
    }
  };

  let activeAnimationTimeout = null;

  function initSimulatorForLesson(session, lessonNum) {
    const lessonId = `${session}_l${lessonNum}`;
    const config = simulatorScenarios[lessonId];
    if (!config) return;

    const simContainer = document.getElementById(`${lessonId}_simulator`);
    if (!simContainer) return;

    simContainer.querySelector(".sim-title-text").textContent = config.title;

    const visualizer = simContainer.querySelector(".sim-visualizer");
    visualizer.querySelectorAll(".sim-node").forEach(node => {
      const nodeId = node.getAttribute("data-node");
      if (config.nodes.includes(nodeId)) {
        node.style.display = "block";
      } else {
        node.style.display = "none";
      }
      node.style.borderColor = "var(--border-glass)";
      node.style.boxShadow = "none";
    });

    const select = simContainer.querySelector(".scenario-select");
    select.innerHTML = "";
    config.scenarios.forEach(sc => {
      const opt = document.createElement("option");
      opt.value = sc.value;
      opt.textContent = sc.label;
      select.appendChild(opt);
    });

    const chatArea = simContainer.querySelector(".sim-chat-area");
    chatArea.innerHTML = `<div class="sim-msg system">[HỆ THỐNG]: Bấm nút 'RUN SIMULATOR' để xem luồng dữ liệu chạy trực quan.</div>`;

    const svgConnections = simContainer.querySelector(".sim-svg-connections");
    if (svgConnections) {
      svgConnections.querySelectorAll(".sim-connection-path").forEach(path => {
        path.classList.remove("active-path");
      });
    }

    const runBtn = simContainer.querySelector(".sim-send-btn");
    runBtn.onclick = () => {
      if (activeAnimationTimeout) {
        clearTimeout(activeAnimationTimeout);
      }
      runBtn.disabled = true;
      runBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Running...';
      chatArea.innerHTML = "";
      
      if (svgConnections) {
        svgConnections.querySelectorAll(".sim-connection-path").forEach(path => {
          path.classList.remove("active-path");
        });
      }

      config.run(simContainer, select.value, () => {
        runBtn.disabled = false;
        runBtn.innerHTML = '<i class="fas fa-play"></i> RUN SIMULATOR';
      });
    };
  }

  function animatePacket(simContainer, fromNode, toNode, color, duration, callback) {
    const visualizer = simContainer.querySelector(".sim-visualizer");
    const pFrom = visualizer.querySelector(`.node-${fromNode}`);
    const pTo = visualizer.querySelector(`.node-${toNode}`);
    const packet = simContainer.querySelector(".sim-packet");

    if (!pFrom || !pTo || !packet) {
      if (callback) callback();
      return;
    }

    const pathId = `${fromNode}-${toNode}`;
    const reversePathId = `${toNode}-${fromNode}`;
    const connection = visualizer.querySelector(`#path-${pathId}`) || visualizer.querySelector(`#path-${reversePathId}`);
    if (connection) {
      connection.classList.add("active-path");
    }

    const rVisualizer = visualizer.getBoundingClientRect();
    const rFrom = pFrom.getBoundingClientRect();
    const rTo = pTo.getBoundingClientRect();

    const startX = rFrom.left - rVisualizer.left + rFrom.width / 2 - 6;
    const startY = rFrom.top - rVisualizer.top + rFrom.height / 2 - 6;
    const endX = rTo.left - rVisualizer.left + rTo.width / 2 - 6;
    const endY = rTo.top - rVisualizer.top + rTo.height / 2 - 6;

    packet.style.background = color;
    packet.style.boxShadow = `0 0 10px ${color}`;
    packet.style.left = `${startX}px`;
    packet.style.top = `${startY}px`;
    packet.style.display = "block";
    packet.style.transition = "none";

    packet.offsetHeight;

    packet.style.transition = `left ${duration}ms linear, top ${duration}ms linear`;
    packet.style.left = `${endX}px`;
    packet.style.top = `${endY}px`;

    pFrom.style.borderColor = color;
    pFrom.style.boxShadow = `0 0 15px ${color}88`;

    activeAnimationTimeout = setTimeout(() => {
      packet.style.display = "none";
      pFrom.style.borderColor = "var(--border-glass)";
      pFrom.style.boxShadow = "none";
      
      pTo.style.borderColor = color;
      pTo.style.boxShadow = `0 0 15px ${color}88`;
      
      if (connection) {
        connection.classList.remove("active-path");
      }

      setTimeout(() => {
        pTo.style.borderColor = "var(--border-glass)";
        pTo.style.boxShadow = "none";
      }, 500);

      if (callback) callback();
    }, duration);
  }

  function addLog(chatArea, type, text) {
    const msg = document.createElement("div");
    msg.className = `sim-msg ${type}`;
    
    let prefix = "";
    if (type === "user") prefix = "👤 User: ";
    if (type === "ai") prefix = "🤖 AI Agent: ";
    if (type === "system") prefix = "[SYSTEM]: ";
    if (type === "tool") prefix = "⚙️ [Spring Tool]: ";

    msg.textContent = prefix + text;
    chatArea.appendChild(msg);
    chatArea.scrollTop = chatArea.scrollHeight;
  }

  // --- S4 L1 SIMULATOR FLOW ---
  function runS4L1Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");

    if (val === "s4_l1_t1") {
      addLog(chatArea, "user", "Xin chào!");
      
      animatePacket(simContainer, "user", "spring", "var(--primary)", 1200, () => {
        addLog(chatArea, "system", "Spring AI Client nhận message. Đang khởi tạo/lấy chat history từ Database.");
        
        animatePacket(simContainer, "spring", "db", "var(--success)", 1000, () => {
          addLog(chatArea, "system", "Kết quả tìm kiếm Chat Memory: [Trống] (Đây là tin nhắn đầu tiên).");
          addLog(chatArea, "system", "Spring Boot đóng gói message gửi tới LLM Model.");
          
          animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
            addLog(chatArea, "system", "LLM xử lý và trả về phản hồi: 'Chào bạn, Rikkei Hotel có thể giúp gì cho bạn?'");
            
            animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
              addLog(chatArea, "system", "Spring Boot lưu tin nhắn vừa rồi của User & AI vào Database.");
              
              animatePacket(simContainer, "spring", "db", "var(--success)", 1000, () => {
                addLog(chatArea, "system", "Đã lưu vào Redis/RAM: User='Xin chào', AI='Chào bạn, Rikkei Hotel...'");
                
                animatePacket(simContainer, "spring", "user", "var(--primary)", 1000, () => {
                  addLog(chatArea, "ai", "Chào bạn, Rikkei Hotel có thể giúp gì cho bạn?");
                  onComplete();
                });
              });
            });
          });
        });
      });
    } else {
      addLog(chatArea, "system", "Lịch sử trong DB: User='Xin chào', AI='Chào bạn, Rikkei Hotel...'");
      addLog(chatArea, "user", "Đặt cho tôi phòng Deluxe.");
      
      animatePacket(simContainer, "user", "spring", "var(--primary)", 1200, () => {
        addLog(chatArea, "system", "Đọc Chat Memory từ Database...");
        
        animatePacket(simContainer, "spring", "db", "var(--success)", 1000, () => {
          addLog(chatArea, "system", "Nhận được lịch sử lượt chat trước đó. Đang bồi đắp vào Prompt gửi đi.");
          
          animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
            addLog(chatArea, "system", "LLM xử lý (ghi nhớ tên và loại phòng deluxe). AI phản hồi: 'Tôi ghi nhận yêu cầu phòng Deluxe. Cho hỏi tên bạn là gì?'");
            
            animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
              addLog(chatArea, "system", "Lưu tin nhắn mới vào Chat Memory...");
              
              animatePacket(simContainer, "spring", "db", "var(--success)", 1000, () => {
                animatePacket(simContainer, "spring", "user", "var(--primary)", 1000, () => {
                  addLog(chatArea, "ai", "Tôi ghi nhận yêu cầu đặt phòng Deluxe của bạn. Bạn vui lòng cung cấp tên của mình để hoàn tất đặt phòng nhé.");
                  onComplete();
                });
              });
            });
          });
        });
      });
    }
  }

  // --- S4 L2 SIMULATOR FLOW ---
  function runS4L2Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    addLog(chatArea, "user", "Quy đổi 100 USD sang VND.");

    animatePacket(simContainer, "user", "spring", "var(--primary)", 1200, () => {
      addLog(chatArea, "system", "Spring AI Client quét metadata và gửi danh sách tool JSON Schema sang LLM: [exchangeCurrency(amount, from, to)].");
      
      animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1400, () => {
        addLog(chatArea, "system", "LLM phân tích câu hỏi -> Nhận dạng cần dùng Tool 'exchangeCurrency' với tham số: { amount: 100, from: 'USD', to: 'VND' }.");
        addLog(chatArea, "system", "LLM trả về chỉ thị: CALL TOOL [exchangeCurrency].");
        
        animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
          addLog(chatArea, "system", "Spring Boot cục bộ thực thi hàm Java: exchangeCurrency(100, 'USD', 'VND').");
          
          setTimeout(() => {
            addLog(chatArea, "tool", "Thực thi hoàn tất. Trả về kết quả: '2,540,000 VND'.");
            addLog(chatArea, "system", "Spring gửi kết quả của hàm Java ngược lại cho LLM tổng hợp ngôn ngữ.");
            
            animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
              addLog(chatArea, "system", "LLM tổng hợp dữ liệu và viết câu trả lời hoàn chỉnh.");
              
              animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
                animatePacket(simContainer, "spring", "user", "var(--primary)", 1000, () => {
                  addLog(chatArea, "ai", "100 USD quy đổi sang tiền Việt Nam Đồng hiện tại bằng khoảng 2,540,000 VND.");
                  onComplete();
                });
              });
            });
          }, 1000);
        });
      });
    });
  }

  // --- S4 L3 SIMULATOR FLOW ---
  function runS4L3Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    addLog(chatArea, "system", "Bắt đầu quét Component Scan của Spring Boot...");
    addLog(chatArea, "system", "Tìm thấy class CustomerSupportService được chú thích @Service.");
    addLog(chatArea, "system", "Đang quét và đăng ký các method @Tool: [getCustomerDetails, calculatePointsToUpgrade].");

    setTimeout(() => {
      addLog(chatArea, "system", "Mock query: User hỏi 'Khách hàng C002 còn thiếu bao nhiêu điểm để lên hạng?'");
      
      animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
        addLog(chatArea, "system", "LLM phát hiện câu hỏi khớp mô tả Tool 'calculatePointsToUpgrade' cho ID 'C002'.");
        
        animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
          addLog(chatArea, "tool", "Gọi hàm: CustomerSupportService.calculatePointsToUpgrade('C002')");
          addLog(chatArea, "tool", "Kết quả trả về: 'Cần thêm 550 điểm để nâng lên hạng VIP'");
          addLog(chatArea, "system", "Spring gửi kết quả trả về cho LLM.");
          
          animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
            animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
              addLog(chatArea, "system", "Trả kết quả: 'Khách hàng C002 cần tích lũy thêm 550 điểm để lên hạng VIP'.");
              onComplete();
            });
          });
        });
      });
    }, 1200);
  }

  // --- S4 L4 SIMULATOR FLOW ---
  function runS4L4Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    addLog(chatArea, "user", "Tôi muốn đặt phòng Deluxe 2 đêm.");

    animatePacket(simContainer, "user", "spring", "var(--primary)", 1200, () => {
      addLog(chatArea, "system", "Spring Agent gửi context và các tool: checkRoomAvailability, calculateTotalPrice, createBooking sang LLM.");
      
      animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1400, () => {
        addLog(chatArea, "system", "LLM phân tích -> Cần kiểm tra phòng trống trước. Yêu cầu gọi checkRoomAvailability('DELUXE').");
        
        animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
          addLog(chatArea, "system", "Spring kích hoạt Tool kiểm tra cơ sở dữ liệu.");
          
          animatePacket(simContainer, "spring", "db", "var(--success)", 1000, () => {
            addLog(chatArea, "tool", "checkRoomAvailability('DELUXE') -> 'Phòng DELUXE còn trống 3 phòng.'");
            addLog(chatArea, "system", "Spring trả dữ liệu về LLM. LLM tiếp tục quyết định tính giá.");
            
            animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
              addLog(chatArea, "system", "LLM phát hiện có 2 đêm -> cần tính tổng tiền. Yêu cầu gọi calculateTotalPrice('DELUXE', 2).");
              
              animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
                animatePacket(simContainer, "spring", "db", "var(--success)", 1000, () => {
                  addLog(chatArea, "tool", "calculateTotalPrice('DELUXE', 2) -> '3,000,000 VND (1.5M/đêm)'");
                  addLog(chatArea, "system", "Gửi kết quả giá tiền về LLM.");
                  
                  animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1200, () => {
                    addLog(chatArea, "system", "LLM tổng hợp thông tin, chuẩn bị đặt phòng và hỏi xác nhận từ User.");
                    
                    animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
                      animatePacket(simContainer, "spring", "user", "var(--primary)", 1000, () => {
                        addLog(chatArea, "ai", "Phòng Deluxe hiện đang còn trống. Tổng chi phí cho 2 đêm của bạn là 3,000,000 VND. Bạn vui lòng xác nhận và cung cấp tên để tôi tạo đơn đặt phòng nhé.");
                        onComplete();
                      });
                    });
                  });
                });
              });
            });
          });
        });
      });
    });
  }

  // --- S5 L1 SIMULATOR FLOW ---
  function runS5L1Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    addLog(chatArea, "system", "Nhận văn bản thô: 'Rikkei Education'.");
    
    animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1500, () => {
      addLog(chatArea, "system", "Mô hình text-embedding-3-small đã xử lý văn bản.");
      addLog(chatArea, "system", "Mã hóa từ ngữ ngữ nghĩa thành các chỉ số tọa độ toán học.");
      
      animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1200, () => {
        addLog(chatArea, "system", "Đã nhận mảng vector: [0.0123, -0.0984, 0.4120, -0.1567, ... (1536 dimensions)]");
        onComplete();
      });
    });
  }

  // --- S5 L2 SIMULATOR FLOW ---
  function runS5L2Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    
    addLog(chatArea, "system", "Vector A (Query): [0.24, -0.12, 0.85]");
    addLog(chatArea, "system", "Vector B (Document): [0.22, -0.15, 0.81]");
    addLog(chatArea, "system", "Công thức: CosineSimilarity = (A · B) / (||A|| * ||B||)");

    setTimeout(() => {
      const dotProduct = (0.24 * 0.22) + (-0.12 * -0.15) + (0.85 * 0.81);
      const normA = Math.sqrt(0.24*0.24 + 0.12*0.12 + 0.85*0.85);
      const normB = Math.sqrt(0.22*0.22 + 0.15*0.15 + 0.81*0.81);
      const sim = dotProduct / (normA * normB);
      
      addLog(chatArea, "system", `Tích vô hướng (Dot Product) = ${dotProduct.toFixed(4)}`);
      addLog(chatArea, "system", `Độ dài ||A|| = ${normA.toFixed(4)}, ||B|| = ${normB.toFixed(4)}`);
      addLog(chatArea, "system", `Độ tương đồng Cosine Similarity = ${sim.toFixed(6)}`);
      addLog(chatArea, "system", `Góc theta: ~${(Math.acos(sim) * 180 / Math.PI).toFixed(2)} độ. Hai nội dung có độ tương đồng ngữ nghĩa cực kỳ cao (~${(sim*100).toFixed(1)}%).`);
      onComplete();
    }, 1500);
  }

  // --- S5 L3 SIMULATOR FLOW ---
  function runS5L3Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    addLog(chatArea, "system", "Bắt đầu truy vấn ngữ nghĩa: 'hút thuốc phạt bao nhiêu?'");
    addLog(chatArea, "system", "Bước 1: Vector hóa câu hỏi thành Query Vector (V_q).");

    setTimeout(() => {
      animatePacket(simContainer, "spring", "db", "var(--success)", 1200, () => {
        addLog(chatArea, "system", "PostgreSQL kích hoạt index HNSW, tìm kiếm lân cận gần đúng (ANN) trên bảng 'documents'.");
        addLog(chatArea, "system", "Tính khoảng cách toán học <=> giữa Query Vector và hàng triệu bản ghi lưu trữ.");
        
        setTimeout(() => {
          addLog(chatArea, "system", "Lọc lấy Top-K (K=1) có điểm Cosine Similarity > 0.70.");
          
          animatePacket(simContainer, "db", "spring", "var(--success)", 1000, () => {
            addLog(chatArea, "system", "Kết quả tìm thấy: ID='policy-01', Content='Khách sạn cấm hút thuốc, vi phạm phạt 2,000,000 VND', Score='0.893'");
            onComplete();
          });
        }, 1000);
      });
    }, 1000);
  }

  // --- S5 L4 SIMULATOR FLOW ---
  function runS5L4Simulation(simContainer, val, onComplete) {
    const chatArea = simContainer.querySelector(".sim-chat-area");
    addLog(chatArea, "user", "Ở đây hút thuốc có bị phạt không?");

    animatePacket(simContainer, "user", "spring", "var(--primary)", 1200, () => {
      addLog(chatArea, "system", "Khởi chạy QuestionAnswerAdvisor. Vector hóa câu hỏi User.");
      addLog(chatArea, "system", "Truy cập Vector Database (PGVector) để tìm tài liệu liên quan.");
      
      animatePacket(simContainer, "spring", "db", "var(--success)", 1200, () => {
        addLog(chatArea, "system", "Database trả về tài liệu: 'Khách sạn Rikkei cấm hút thuốc hoàn toàn. Vi phạm phạt hành chính 2,000,000 VND.'");
        
        animatePacket(simContainer, "db", "spring", "var(--success)", 1000, () => {
          addLog(chatArea, "system", "Bồi đắp Prompt mới: Hợp nhất câu hỏi của User cùng ngữ cảnh quy định phạt tiền.");
          addLog(chatArea, "system", "Gửi Prompt hoàn chỉnh sang LLM (Gemini/OpenAI).");
          
          animatePacket(simContainer, "spring", "llm", "var(--secondary)", 1400, () => {
            addLog(chatArea, "system", "LLM đọc kỹ ngữ cảnh và trả lời câu hỏi dựa trên tài liệu.");
            
            animatePacket(simContainer, "llm", "spring", "var(--secondary)", 1000, () => {
              animatePacket(simContainer, "spring", "user", "var(--primary)", 1000, () => {
                addLog(chatArea, "ai", "Có bạn nhé. Khách sạn Rikkei cấm hút thuốc hoàn toàn trong phòng nghỉ. Nếu vi phạm quy định này, bạn sẽ bị phạt hành chính 2,000,000 VND.");
                onComplete();
              });
            });
          });
        });
      });
    });
  }
});
