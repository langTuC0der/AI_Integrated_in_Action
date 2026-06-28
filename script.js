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
  }

  // Bind homepage cards button clicks
  document.querySelectorAll(".course-card:not(.locked) .card-action-btn, .course-card:not(.locked)").forEach(card => {
    card.addEventListener("click", (e) => {
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
});
