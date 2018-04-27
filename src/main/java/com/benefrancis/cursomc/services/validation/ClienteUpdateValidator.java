package com.benefrancis.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.benefrancis.cursomc.domain.Cliente;
import com.benefrancis.cursomc.dto.ClienteDTO;
import com.benefrancis.cursomc.repositories.ClienteRepository;
import com.benefrancis.cursomc.resources.exceptons.FieldMessage;

public class ClienteUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO> {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(ClienteUpdate ann) {
	}

	@Override
	public boolean isValid(ClienteDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> list = new ArrayList<>();

		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Integer uriId = Integer.parseInt(map.get("id"));

		// inclua os testes aqui, inserindo erros na lista

		Cliente aux = repo.findByEmail(objDto.getEmail());
		// Não posso permitir que numa atualização 
		// um usuário receba o email de um outro
		// cliente já cadastrado.
		if (aux != null && !aux.getId().equals(uriId)) {
			list.add(new FieldMessage("email", "Já existente cliente cadastrado com este e-mail"));
		}

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
