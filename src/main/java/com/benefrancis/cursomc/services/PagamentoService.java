package com.benefrancis.cursomc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.benefrancis.cursomc.domain.Pagamento;
import com.benefrancis.cursomc.repositories.PagamentoRepository;
import com.benefrancis.cursomc.services.exceptions.ObjectNotFoundException;
@Service
public class PagamentoService {
	@Autowired
	private PagamentoRepository repo;

	
	public Pagamento find(Integer id) {
		Pagamento obj = repo.findOne(id);
		if (obj == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + id + ", Tipo: " + Pagamento.class.getName());
		}
		return obj;
	}

	public Pagamento insert(Pagamento obj) {
		obj.setId(null);
		return repo.save(obj);
	}
}
