package com.benefrancis.cursomc.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

import com.benefrancis.cursomc.domain.Pedido;

public interface EmailService {
	public void sendOrderConfirmationEmail(Pedido obj);

	public void sendEmail(SimpleMailMessage msg);

	void sendOrderConfirmationHtmlEmail(Pedido obj);

	void sendHtmlEmail(MimeMessage msg);
}
