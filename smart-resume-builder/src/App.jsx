import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  Leaf, Eye, Lightbulb, Info, Sun, Moon, Rocket, UserRound,
  FileText, Settings, GraduationCap, Briefcase, Award, Languages,
  Download, Mail, MapPin, Phone, Plus, Trash2, ChevronDown,
  ChevronUp, CheckCircle2, ShieldCheck, Code2, Menu, X, RefreshCw,
  Maximize2, PanelLeftClose, PanelLeftOpen
} from "lucide-react";
import "./App.css";

const API = "https://resume-craft-ghx8.onrender.com/api/resume";

const initialResume = {
  fullName: "",
  jobTitle: "",
  email: "",
  phone: "",
  location: "",
  summary: "",
  skills: "",
  education: [],
  projects: [],
  workExperience: [],
  certifications: [],
  languages: [],
};

const sections = [
  { id: "personal", label: "Personal", icon: UserRound, target: "personal-section" },
  { id: "summary", label: "Summary", icon: FileText, target: "summary-section" },
  { id: "skills", label: "Skills", icon: Rocket, target: "skills-section" },
  { id: "education", label: "Education", icon: GraduationCap, target: "section-education" },
  { id: "workExperience", label: "Experience", icon: Briefcase, target: "section-workExperience" },
  { id: "projects", label: "Projects", icon: Code2, target: "section-projects" },
  { id: "certifications", label: "Certifications", icon: Award, target: "section-certifications" },
  { id: "languages", label: "Languages", icon: Languages, target: "section-languages" },
];

function SocialIcon({ kind }) {
  if (kind === "github") return <svg viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M12 .9a11.1 11.1 0 0 0-3.51 21.63c.56.1.76-.24.76-.54v-2.1c-3.1.67-3.76-1.32-3.76-1.32-.5-1.28-1.23-1.62-1.23-1.62-1.01-.69.08-.68.08-.68 1.12.08 1.71 1.15 1.71 1.15 1 .1.76 2.15 3.37 1.53.1-.72.39-1.21.7-1.49-2.48-.28-5.09-1.24-5.09-5.53 0-1.22.44-2.22 1.15-3-.12-.28-.5-1.42.11-2.96 0 0 .94-.3 3.05 1.15a10.6 10.6 0 0 1 5.55 0c2.11-1.45 3.05-1.15 3.05-1.15.61 1.54.23 2.68.11 2.96.72.78 1.15 1.78 1.15 3 0 4.3-2.61 5.24-5.1 5.52.4.35.75 1.02.75 2.06v3.08c0 .3.2.65.77.54A11.1 11.1 0 0 0 12 .9Z" /></svg>;
  if (kind === "linkedin") return <svg viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M20.45 2H3.55C2.69 2 2 2.68 2 3.52v16.96c0 .84.69 1.52 1.55 1.52h16.9c.86 0 1.55-.68 1.55-1.52V3.52c0-.84-.69-1.52-1.55-1.52ZM7.93 18.7H4.98V9.2h2.95v9.5ZM6.45 7.9a1.71 1.71 0 1 1 .02-3.42 1.71 1.71 0 0 1-.02 3.42ZM19.02 18.7h-2.94v-4.62c0-1.1-.02-2.52-1.54-2.52-1.55 0-1.79 1.2-1.79 2.44v4.7H9.81V9.2h2.82v1.3h.04c.39-.74 1.35-1.52 2.78-1.52 2.98 0 3.57 1.96 3.57 4.51v5.21Z" /></svg>;
  return <Mail size={17} />;
}

function App() {
  const [resume, setResume] = useState(initialResume);
  const [recipientEmail, setRecipientEmail] = useState("");
  const [loading, setLoading] = useState("");
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [dark, setDark] = useState(false);
  const [collapsed, setCollapsed] = useState({});
  const [mobileMenu, setMobileMenu] = useState(false);
  const [activeSection, setActiveSection] = useState("personal");
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewState, setPreviewState] = useState("idle");
  const [previewError, setPreviewError] = useState("");
  const [previewMode, setPreviewMode] = useState(false);
  const [refreshPreview, setRefreshPreview] = useState(0);
  const [sidebarCompact, setSidebarCompact] = useState(false);
  const previewUrlRef = useRef("");
  const requestIdRef = useRef(0);

  const showMessage = useCallback((text, type = "success") => {
    setMessage(text);
    setMessageType(type);
  }, []);

  // Automatically dismiss success and error notifications after 3 seconds.
  // The existing close button can still dismiss a notification immediately.
  useEffect(() => {
    if (!message) return undefined;

    const timer = window.setTimeout(() => {
      setMessage("");
    }, 3000);

    return () => window.clearTimeout(timer);
  }, [message]);

  const change = (event) => {
    const { name, value } = event.target;
    setResume((previous) => ({ ...previous, [name]: value }));
  };

  const updateItem = (section, index, field, value) => {
    setResume((previous) => ({
      ...previous,
      [section]: previous[section].map((item, i) => i === index ? { ...item, [field]: value } : item),
    }));
  };

  const addItem = (section, item) => {
    setResume((previous) => ({ ...previous, [section]: [...previous[section], item] }));
    setActiveSection(section);
  };

  const removeItem = (section, index) => {
    setResume((previous) => ({ ...previous, [section]: previous[section].filter((_, i) => i !== index) }));
  };

  const toggle = (section) => setCollapsed((previous) => ({ ...previous, [section]: !previous[section] }));

  const scrollTo = (id) => {
    const target = document.getElementById(id);
    if (target) target.scrollIntoView({ behavior: "smooth", block: "nearest" });
    setMobileMenu(false);
  };

  const hasResumeContent = useMemo(() => Boolean(
    resume.fullName.trim() || resume.jobTitle.trim() || resume.email.trim() || resume.summary.trim() || resume.skills.trim() ||
    resume.education.length || resume.projects.length || resume.workExperience.length || resume.certifications.length || resume.languages.length
  ), [resume]);

  const createPdfPreview = useCallback(async (silent = true) => {
    const requestId = ++requestIdRef.current;
    setPreviewState("loading");
    setPreviewError("");
    try {
      const response = await fetch(`${API}/generate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(resume),
      });
      if (!response.ok) throw new Error((await response.text()) || `PDF preview failed (${response.status}).`);
      const blob = await response.blob();
      if (requestId !== requestIdRef.current) return;
      const nextUrl = URL.createObjectURL(blob);
      if (previewUrlRef.current) URL.revokeObjectURL(previewUrlRef.current);
      previewUrlRef.current = nextUrl;
      setPreviewUrl(nextUrl);
      setPreviewState("ready");
    } catch (error) {
      if (requestId !== requestIdRef.current) return;
      setPreviewState("error");
      setPreviewError(error.message || "Could not load PDF preview. Check that the backend is running on port 8080.");
      if (!silent) showMessage("Could not create the PDF preview. Make sure Spring Boot is running on port 8080.", "error");
    }
  }, [resume, showMessage]);

  // Debounced live preview uses the actual PDF returned by the existing backend endpoint.
  useEffect(() => {
    if (!hasResumeContent) return undefined;
    const timer = window.setTimeout(() => createPdfPreview(true), 1000);
    return () => window.clearTimeout(timer);
  }, [resume, hasResumeContent, createPdfPreview, refreshPreview]);

  useEffect(() => () => {
    requestIdRef.current += 1;
    if (previewUrlRef.current) URL.revokeObjectURL(previewUrlRef.current);
  }, []);

  async function downloadResume() {
    if (loading) return;
    setLoading("pdf");
    try {
      const response = await fetch(`${API}/generate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(resume),
      });
      if (!response.ok) throw new Error((await response.text()) || "PDF generation failed.");
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "resume.pdf";
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.setTimeout(() => URL.revokeObjectURL(url), 1200);
      showMessage("Your backend-generated resume PDF has been downloaded.");
    } catch (error) {
      showMessage(error.message || "Unable to generate the PDF. Check that the backend is running.", "error");
    } finally {
      setLoading("");
    }
  }

  async function emailResume() {
    if (loading) return;
    if (!recipientEmail.trim()) {
      showMessage("Please enter the recipient's email address.", "error");
      return;
    }
    setLoading("email");
    try {
      const response = await fetch(`${API}/generate-and-email`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: recipientEmail.trim(), resume }),
      });
      const result = await response.text();
      if (!response.ok) throw new Error(result || "Email sending failed.");
      showMessage("Your resume email request was accepted. Check the recipient's inbox.");
    } catch (error) {
      showMessage(error.message || "Unable to send your resume.", "error");
    } finally {
      setLoading("");
    }
  }

  const sectionTitle = (id, Icon, title, subtitle) => (
    <div className="section-heading">
      <div className="section-icon"><Icon size={21} /></div>
      <div className="section-heading-text"><h2>{title}</h2><p>{subtitle}</p></div>
      <button type="button" className="collapse-button" onClick={() => toggle(id)} aria-label={`${collapsed[id] ? "Expand" : "Collapse"} ${title}`} aria-expanded={!collapsed[id]}>
        {collapsed[id] ? <ChevronDown size={18} /> : <ChevronUp size={18} />}
      </button>
    </div>
  );

  const field = (label, name, placeholder = "", type = "text", required = false) => (
    <label className="field" key={name}>
      <span>{label}{required && <b className="required"> *</b>}</span>
      <input name={name} type={type} value={resume[name]} onChange={change} placeholder={placeholder} required={required} />
    </label>
  );

  const repeatSection = (section, Icon, title, subtitle, fields, blankItem) => (
    <section className="form-card" key={section} id={`section-${section}`}>
      {sectionTitle(section, Icon, title, subtitle)}
      {!collapsed[section] && <div className="repeat-content">
        {resume[section].map((item, index) => <div className={`repeat-row repeat-row-${section}`} key={`${section}-${index}`}>
          {fields.map((f) => <label className={`field ${f.multiline ? "field-wide" : ""}`} key={f.name}>
            <span>{f.label}{f.required && <b className="required"> *</b>}</span>
            {f.type === "select" ? (
              <select value={item[f.name] || ""} onChange={(e) => updateItem(section, index, f.name, e.target.value)}>
                <option value="">Choose proficiency</option>
                {(f.options || []).map((option) => <option key={option} value={option}>{option}</option>)}
              </select>
            ) : f.multiline ? (
              <textarea rows="2" value={item[f.name] || ""} placeholder={f.placeholder} onChange={(e) => updateItem(section, index, f.name, e.target.value)} />
            ) : (
              <input type={f.type || "text"} value={item[f.name] || ""} placeholder={f.placeholder} onChange={(e) => updateItem(section, index, f.name, e.target.value)} />
            )}
          </label>)}
          <button type="button" className="delete-button" onClick={() => removeItem(section, index)} aria-label={`Remove ${title} item ${index + 1}`}><Trash2 size={16} /></button>
        </div>)}
        <button type="button" className="add-button" onClick={() => addItem(section, { ...blankItem })}><Plus size={16} /> Add {title}</button>
      </div>}
    </section>
  );

  return (
    <div className={`app-shell ${dark ? "dark-mode" : ""} ${previewMode ? "preview-focus" : ""}`}>

      <header className="topbar">
        <a
          className="brand"
          href="#top"
          onClick={(e) => {
            e.preventDefault();
            scrollTo("top");
          }}
        >
          <img
            src="/favicon.svg"
            alt="ResumeCraft logo"
            className="brand-logo"
          />

          <span className="brand-text">
            <strong>ResumeCraft</strong>
            <small>Build · Preview · Download</small>
          </span>
        </a>

        <button
          className="mobile-menu-button"
          type="button"
          onClick={() => setMobileMenu(!mobileMenu)}
          aria-label="Toggle navigation"
        >
          {mobileMenu ? <X /> : <Menu />}
        </button>

        <nav className={`top-nav ${mobileMenu ? "menu-open" : ""}`}>
          <button
            type="button"
            className={!previewMode ? "nav-active" : ""}
            onClick={() => {
              setPreviewMode(false);
              scrollTo("builder");
            }}
          >
            <FileText size={16} /> Build
          </button>

          <button
            type="button"
            className={previewMode ? "nav-active" : ""}
            onClick={() => {
              setPreviewMode(true);
              if (!previewUrl) createPdfPreview(false);
            }}
          >
            <Eye size={17} /> Preview
          </button>

          <button type="button" onClick={() => scrollTo("tips-card")}>
            <Lightbulb size={17} /> Tips
          </button>

          <button type="button" onClick={() => scrollTo("profile-card")}>
            <Info size={17} /> About
          </button>
        </nav>

        <button
          type="button"
          className="theme-toggle"
          onClick={() => setDark((value) => !value)}
          aria-label="Toggle color theme"
        >
          {dark ? <Sun size={18} /> : <Moon size={18} />}
        </button>

        <div className="top-tagline">
          <span>Build your career <Rocket size={16} /></span>
          <small>One resume at a time</small>
        </div>
      </header>


      <main className="workspace" id="top">
        <aside className={`left-rail ${sidebarCompact ? "rail-compact" : ""}`}>
          <div className="rail-nav">
            {sections.map(({ id, label, icon: Icon, target }) => <button key={id} type="button" className={activeSection === id ? "rail-link rail-active" : "rail-link"} title={label} onClick={() => { setActiveSection(id); scrollTo(target); setPreviewMode(false); }}>
              <Icon size={21} /><span>{label}</span>
            </button>)}
          </div>

          <section className="profile-card" id="profile-card">
            <div className="profile-avatar">
              <img src="/profile.jpg" alt="Mohit Kumar Pani" />
            </div>

            <h2 className="profile-name">Mohit Kumar Pani</h2>

            <span className="profile-role">Full Stack Developer</span>

            <p className="profile-caption">
              Building useful tools for brighter careers.
            </p>

            <div className="profile-socials">
              <a
                className="social github"
                href="https://github.com/mohitkpani"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="GitHub profile"
              >
                <SocialIcon kind="github" />
              </a>

              <a
                className="social linkedin"
                href="https://www.linkedin.com/in/mohitpani007/"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="LinkedIn profile"
              >
                <SocialIcon kind="linkedin" />
              </a>

              <a
                className="social email"
                href="mailto:mytechlabs.tech@gmail.com"
                aria-label="Email"
              >
                <Mail size={17} />
              </a>
            </div>

            <div className="profile-quote">
              <Leaf size={17} />
              “Build your career one resume at a time.”
            </div>
          </section>

          <button className="rail-collapse" type="button" onClick={() => setSidebarCompact((v) => !v)} aria-label="Toggle sidebar width">{sidebarCompact ? <PanelLeftOpen size={16} /> : <PanelLeftClose size={16} />}</button>
        </aside>

        <section className="editor-pane" id="builder">
          <div className="editor-scroll">
            <section className="form-card" id="personal-section">
              {sectionTitle("personal", UserRound, "Personal Information", "Tell us about yourself")}
              {!collapsed.personal && <div className="form-grid">
                {field("Full Name", "fullName", "Enter your full name", "text", true)}
                {field("Job Title", "jobTitle", "e.g. Java Developer", "text", true)}
                {field("Email", "email", "your.email@example.com", "email")}
                {field("Phone", "phone", "+91 9876543210", "tel")}
                {field("Location", "location", "e.g. Bhubaneswar, Odisha")}
              </div>}
            </section>

            <section className="form-card" id="summary-section">
              {sectionTitle("summary", FileText, "Professional Summary", "Write a brief summary about yourself")}
              {!collapsed.summary && <div className="summary-wrap"><textarea name="summary" value={resume.summary} onChange={change} rows="3" maxLength={500} placeholder="E.g. Motivated developer with experience in Java, React and building useful applications..." /><div className="character-count">{resume.summary.length}/500</div></div>}
            </section>

            <section className="form-card" id="skills-section">
              {sectionTitle("skills", Settings, "Skills", "Add your key skills (comma separated)")}
              {!collapsed.skills && <div className="skills-input-wrap"><input name="skills" value={resume.skills} onChange={change} placeholder="e.g. Java, Spring Boot, React, SQL, Git" /><small><Info size={13} /> Separate skills with commas</small></div>}
            </section>

            {repeatSection("education", GraduationCap, "Education", "Add your educational background", [
              { name: "degree", label: "Degree", placeholder: "e.g. B.Tech", required: true },
              { name: "institution", label: "Institution", placeholder: "e.g. XYZ University", required: true },
              { name: "year", label: "Year", placeholder: "e.g. 2026" },
              { name: "grade", label: "Percentage / Grade", placeholder: "e.g. 8.24 CGPA" },
            ], { degree: "", institution: "", year: "", grade: "" })}

            {repeatSection("workExperience", Briefcase, "Work Experience", "Add your work experience (optional)", [
              { name: "jobTitle", label: "Job Title", placeholder: "e.g. Software Developer" },
              { name: "company", label: "Company", placeholder: "Company name" },
              { name: "startDate", label: "Start Date", placeholder: "e.g. Jan 2024" },
              { name: "endDate", label: "End Date", placeholder: "e.g. Present" },
              { name: "description", label: "Description", placeholder: "Responsibilities and achievements...", multiline: true },
            ], { jobTitle: "", company: "", description: "", startDate: "", endDate: "" })}

            {repeatSection("projects", Code2, "Projects", "Showcase your best work", [
              { name: "name", label: "Project Name", placeholder: "e.g. ResumeCraft", required: true },
              { name: "description", label: "Description", placeholder: "Describe your project...", multiline: true, required: true },
            ], { name: "", description: "" })}

            {repeatSection("certifications", Award, "Certifications", "Add your achievements (optional)", [
              { name: "name", label: "Certification", placeholder: "Certification name" },
              { name: "organization", label: "Organization", placeholder: "Issuing organization" },
              { name: "issueDate", label: "Issue Date", placeholder: "e.g. Jan 2026" },
              { name: "credentialId", label: "Credential ID", placeholder: "Optional ID" },
              { name: "credentialUrl", label: "Credential URL", placeholder: "https://..." },
            ], { name: "", organization: "", issueDate: "", credentialId: "", credentialUrl: "" })}

            {repeatSection("languages", Languages, "Languages", "Languages you know", [
              { name: "name", label: "Language", placeholder: "e.g. English" },
              { name: "proficiency", label: "Proficiency", type: "select", options: ["Native", "Fluent", "Advanced", "Intermediate", "Basic", "Beginner"] },
            ], { name: "", proficiency: "" })}

            <section className="form-card tips-card" id="tips-card">
              {sectionTitle("tips", Lightbulb, "Quick Tips", "A few ways to strengthen your resume")}
              {!collapsed.tips && <ul className="tips-list"><li><CheckCircle2 size={16} /> Use a concise professional summary.</li><li><CheckCircle2 size={16} /> Keep skills relevant to the job.</li><li><CheckCircle2 size={16} /> Add measurable achievements where possible.</li><li><CheckCircle2 size={16} /> Check spelling before downloading.</li></ul>}
            </section>
          </div>
          <div className="editor-footer"><span><ShieldCheck size={15} /> Your details stay in this session until you leave.</span><button type="button" className="primary-button" onClick={() => { setPreviewMode(true); createPdfPreview(false); }}><Eye size={17} /> Preview Resume</button></div>
        </section>

        <section className={`preview-pane ${previewMode ? "preview-pane-focus" : ""}`}>
          <div className="preview-header">
            <div><h2><span className="preview-header-icon"><FileText size={19} /></span> Live Preview</h2><p>A4 · Matches your backend-generated PDF</p></div>
            <div className="preview-tools"><button type="button" onClick={() => setPreviewMode((v) => !v)} title={previewMode ? "Show editor" : "Expand preview"} aria-label="Toggle preview focus"><Maximize2 size={17} /></button><button type="button" onClick={() => setRefreshPreview((v) => v + 1)} title="Refresh preview" aria-label="Refresh preview"><RefreshCw size={16} /></button></div>
          </div>
          <div className="pdf-stage">
            {previewUrl ? <iframe key={previewUrl} className="pdf-frame" src={`${previewUrl}#toolbar=0&navpanes=0&view=FitH`} title="Live resume PDF preview" /> :
              <div className="preview-empty">
                {previewState === "loading" ? <><RefreshCw className="spin" size={34} /><h3>Creating live preview…</h3><p>Your actual backend PDF will appear here.</p></> : previewState === "error" ? <><FileText size={36} /><h3>PDF preview unavailable</h3><p>{previewError || "Start the Spring Boot backend on port 8080, then refresh."}</p><button className="primary-button" type="button" onClick={() => createPdfPreview(false)}><RefreshCw size={16} /> Try again</button></> : <><FileText size={42} /><h3>Your resume preview</h3><p>Start entering your details. The actual PDF generated by your backend will appear here and update as you edit.</p><button className="primary-button" type="button" onClick={() => createPdfPreview(false)}><Eye size={16} /> Generate preview</button></>}
              </div>}
          </div>
          <div className="preview-footer">
            <label className="email-recipient">
              <Mail size={16} />
              <input
                type="email"
                value={recipientEmail}
                onChange={(event) => setRecipientEmail(event.target.value)}
                placeholder="Recipient email address"
                aria-label="Recipient email address"
                autoComplete="email"
              />
            </label>
            <button type="button" className="secondary-button" onClick={() => { setPreviewMode(false); scrollTo("builder"); }}><ChevronDown className="back-arrow" size={17} /> Back to Edit</button>
            <button type="button" className="primary-button download-button" onClick={downloadResume} disabled={!!loading}><Download size={18} /> {loading === "pdf" ? "Generating…" : "Download PDF"}</button>
            <button type="button" className="email-button" onClick={emailResume} disabled={!!loading}><Mail size={17} /> {loading === "email" ? "Sending…" : "Email PDF"}</button>
          </div>
        </section>
      </main>

      {message && <div className={`status-message ${messageType}`} role="status">{messageType === "success" ? <CheckCircle2 size={19} /> : <Info size={19} />}<span>{message}</span><button type="button" onClick={() => setMessage("")} aria-label="Dismiss message">×</button></div>}
    </div>
  );
}

export default App;
