package com.benefrancis.cursomc.resources;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.benefrancis.cursomc.security.JWTUtil;
import com.benefrancis.cursomc.security.UserSS;
import com.benefrancis.cursomc.services.UserService;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {

	@Autowired
	private JWTUtil jwtUtil;

	/**
	 * Gera um novo token a partir de um token que ainda está válido.
	 * 
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/refresh_token", method = RequestMethod.POST)
	public ResponseEntity<Void> refreshToken(HttpServletResponse response) {
		UserSS user = UserService.authenticated();
		String token = jwtUtil.generateToken(user.getUsername());
		response.addHeader("Authorization", "Bearer " + token);
		return ResponseEntity.noContent().build();
	}
}
