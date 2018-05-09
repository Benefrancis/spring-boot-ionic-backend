package com.benefrancis.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.benefrancis.cursomc.security.UserSS;

@Service
public class UserService {
	/**
	 * Retorna o usuário Logado
	 * 
	 * @return
	 */
	public static UserSS authenticated() {
		try {
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		} catch (Exception e) {
			return null;
		}
	}
}
