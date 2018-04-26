package com.benefrancis.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.benefrancis.cursomc.domain.Cliente;
import com.benefrancis.cursomc.dto.ClienteDTO;
import com.benefrancis.cursomc.repositories.ClienteRepository;
import com.benefrancis.cursomc.services.exceptions.DataIntegrityException;
import com.benefrancis.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	public Cliente find(Integer id) {
		Cliente obj = repo.findOne(id);
		if (obj == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName());
		}
		return obj;
	}

	public Cliente update(Cliente obj) {

		Cliente newObj = find(obj.getId()); // Pego o obj do banco de dados
		updateData(newObj, obj);// Atualizo as informações
		return repo.save(newObj);// Persisto o objeto que veio do BD.
	}

	/**
	 * 
	 * Para evitar que se perca informações chamamos o método updateData passando
	 * como parâmetro o objeto que está no banco de dados (newObj) e o atualizamos
	 * com os dados que vieram no Method PUT (obj)
	 * 
	 * @param newObj
	 * @param obj
	 */
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.delete(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}

	}

	public List<Cliente> findAll() {
		// Usa o padrão DTO
		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}

}
