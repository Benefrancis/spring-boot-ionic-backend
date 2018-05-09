package com.benefrancis.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.benefrancis.cursomc.domain.Cliente;
import com.benefrancis.cursomc.repositories.ClienteRepository;
import com.benefrancis.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	private BCryptPasswordEncoder pe;

	@Autowired
	EmailService emailService;

	private Random rand = new Random();

	public void sendNewPassword(String email) {
		Cliente cliente = clienteRepository.findByEmail(email);
		if (cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}

		String newPass = newPassword();
		cliente.setSenha(pe.encode(newPass));
		clienteRepository.save(cliente);
		emailService.sendNewPasswordEmail(cliente, newPass);
	}

	private String newPassword() {
		char[] vet = new char[10];
		for (int i = 0; i < vet.length; i++) {
			vet[i] = randonChar();
		}
		return new String(vet);
	}

	/**
	 * Por meio do código unicode gero um caracter aleatório que poderá ser número,
	 * letra minuscula ou letra maiuscula
	 * 
	 *  https://unicode-table.com/pt/
	 * 
	 * @return
	 */
	private char randonChar() {

		Integer opt = rand.nextInt(3);

		if (opt == 0) {
			// gera Letra Número de 0 a 9.
			// 48 é o código unicode do número 0. O número 9 é unicode 57. Por
			// isso somarei o número randomico menor que 10 com o menor código
			// unicode de números.
			return (char) (rand.nextInt(10) + 48);

		}

		if (opt == 1) {
			// gera Letra maiuscula
			return (char) (rand.nextInt(26) + 65);
		} else {
			// gera letra minuscula
			return (char) (rand.nextInt(26) + 97);
		}

	}
}
