package com.beansense.notification_service.service;

import com.beansense.notification_service.dto.AccountRequestDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${notification.admin-email}")
    private String adminEmail;

    @Value("${notification.from-email}")
    private String fromEmail;

    /**
     * Kirim email notifikasi request akun ke admin.
     */
    public void sendAccountRequest(AccountRequestDto dto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("[BeanSense] Permintaan Pembuatan Akun Baru");

            String waktu = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            String html = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e0d6cc; border-radius: 12px;">
                        <div style="text-align: center; margin-bottom: 24px;">
                            <h2 style="color: #3b1f0e; margin: 0;">☕ BeanSense</h2>
                            <p style="color: #7a6652; margin: 4px 0 0;">Sistem Penyortiran Kopi IoT</p>
                        </div>

                        <h3 style="color: #3b1f0e; border-bottom: 2px solid #e0d6cc; padding-bottom: 8px;">
                            Permintaan Pembuatan Akun Baru
                        </h3>

                        <p style="color: #4a3728;">Seseorang meminta untuk dibuatkan akun di sistem BeanSense. Berikut detailnya:</p>

                        <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                            <tr style="background: #f9f4ef;">
                                <td style="padding: 12px 16px; font-weight: bold; color: #3b1f0e; width: 35%%;">Nama Lengkap</td>
                                <td style="padding: 12px 16px; color: #4a3728;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 16px; font-weight: bold; color: #3b1f0e;">Username</td>
                                <td style="padding: 12px 16px; color: #4a3728;">%s</td>
                            </tr>
                            <tr style="background: #f9f4ef;">
                                <td style="padding: 12px 16px; font-weight: bold; color: #3b1f0e;">Email</td>
                                <td style="padding: 12px 16px; color: #4a3728;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 16px; font-weight: bold; color: #3b1f0e;">Waktu Request</td>
                                <td style="padding: 12px 16px; color: #4a3728;">%s</td>
                            </tr>
                        </table>

                        <div style="background: #fff8f0; border-left: 4px solid #c8860a; padding: 12px 16px; border-radius: 4px; margin: 16px 0;">
                            <p style="margin: 0; color: #856404; font-size: 0.9em;">
                                ⚠️ Silakan login ke sistem BeanSense dan buka halaman <strong>User Management</strong> untuk membuat akun tersebut.
                            </p>
                        </div>

                        <p style="color: #7a6652; font-size: 0.85em; text-align: center; margin-top: 24px; border-top: 1px solid #e0d6cc; padding-top: 16px;">
                            Email ini dikirim otomatis oleh sistem BeanSense. Jangan balas email ini.
                        </p>
                    </div>
                    """.formatted(dto.getNama(), dto.getUsername(), dto.getEmail(), waktu);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Email notifikasi request akun berhasil dikirim ke {} untuk user '{}'",
                    adminEmail, dto.getUsername());

        } catch (Exception e) {
            log.error("Gagal mengirim email notifikasi: {}", e.getMessage());
            throw new RuntimeException("Gagal mengirim email. Coba lagi nanti.");
        }
    }
}
