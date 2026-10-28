
package com.resumecraft.service;

import org.springframework.stereotype.Component;

@Component
public class ResumeEmailTemplate {


public String buildEmailHtml(String recipientName) {
    String name = escapeHtml(
            recipientName == null || recipientName.isBlank()
                    ? "there"
                    : recipientName.trim()
    );

    return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Your ResumeCraft Resume</title>
            </head>
            <body style="margin:0;padding:0;background:#f0f7f2;
                         font-family:Arial,Helvetica,sans-serif;color:#243b30;">

              <table role="presentation" width="100%%" cellspacing="0"
                     cellpadding="0" border="0"
                     style="background:#f0f7f2;padding:10px 8px;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="540" cellspacing="0"
                           cellpadding="0" border="0"
                           style="width:100%%;max-width:540px;background:#fff;
                                  border-radius:12px;overflow:hidden;">

                      <tr>
                        <td align="center"
                            style="padding:16px 12px;background:#087443;
                                   background-image:linear-gradient(135deg,#0b9661,#006a47);">
                          <div style="display:inline-block;padding:6px 10px;
                                      border-radius:8px;background:#fff;
                                      color:#087443;font-size:20px;">
                            &#128196;
                          </div>
                          <h1 style="margin:6px 0 3px;color:#fff;font-size:22px;">
                            ResumeCraft
                          </h1>
                          <p style="margin:0;color:#dcfce7;font-size:11px;">
                            Build &middot; Preview &middot; Download
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td align="center" style="padding:16px 20px 8px;">
                          <div style="font-size:28px;color:#16a34a;">
                            &#10003;
                          </div>
                          <h2 style="margin:5px 0;color:#173b2c;font-size:20px;">
                            Your resume is ready!
                          </h2>
                          <p style="margin:0;color:#64748b;font-size:13px;line-height:1.5;">
                            Hello %s,<br>
                            Your professional resume has been generated successfully.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:8px 20px 16px;">
                          <table role="presentation" width="100%%" cellspacing="0"
                                 cellpadding="0" border="0"
                                 style="background:#effaf2;border:1px solid #c7ead2;
                                        border-radius:9px;">
                            <tr>
                              <td style="padding:12px;font-size:22px;">
                                &#128196;
                              </td>
                              <td style="padding:12px 5px 12px 0;">
                                <strong style="display:block;color:#14532d;font-size:13px;">
                                  resume.pdf
                                </strong>
                                <span style="display:block;margin-top:3px;
                                             color:#64748b;font-size:11px;">
                                  PDF document &middot; Attached
                                </span>
                              </td>
                              <td align="right" style="padding:12px;">
                                <span style="color:#16a34a;font-size:19px;">
                                  &#10003;
                                </span>
                              </td>
                            </tr>
                          </table>

                          <p style="margin:12px 0 0;color:#475569;
                                    font-size:12px;line-height:1.5;">
                            Your PDF is attached and ready to download,
                            review, and share with employers.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td align="center"
                            style="padding:12px;background:#f7fbf8;
                                   border-top:1px solid #e0eee5;">
                          <p style="margin:0;color:#087443;font-size:12px;font-weight:bold;">
                            Made with ResumeCraft
                          </p>
                          <p style="margin:4px 0 0;color:#84958b;font-size:10px;">
                            Build your career, one resume at a time.
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(name);
}


    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
