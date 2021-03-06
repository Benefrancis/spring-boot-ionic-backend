package com.benefrancis.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.benefrancis.cursomc.domain.Cidade;
import com.benefrancis.cursomc.domain.Cliente;
import com.benefrancis.cursomc.domain.Endereco;
import com.benefrancis.cursomc.domain.enums.Perfil;
import com.benefrancis.cursomc.domain.enums.TipoCliente;
import com.benefrancis.cursomc.dto.ClienteDTO;
import com.benefrancis.cursomc.dto.ClienteNewDTO;
import com.benefrancis.cursomc.repositories.CidadeRepository;
import com.benefrancis.cursomc.repositories.ClienteRepository;
import com.benefrancis.cursomc.repositories.EnderecoRepository;
import com.benefrancis.cursomc.security.UserSS;
import com.benefrancis.cursomc.services.exceptions.AuthorizationException;
import com.benefrancis.cursomc.services.exceptions.DataIntegrityException;
import com.benefrancis.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private BCryptPasswordEncoder pe;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private ImageService imageService;

	@Value("${img.prefix.client.profile}")
	private String prefix;

	/**
	 * Um cliente deve estar logado e só poderá consultar a ele mesmo, salvo se for
	 * Admin
	 * 
	 * @param id
	 * @return
	 */
	public Cliente findOne(Integer id) {

		UserSS user = UserService.authenticated();

		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}

		Cliente obj = repo.findOne(id);
		if (obj == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName());
		}
		return obj;
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.save(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = findOne(obj.getId()); // Pego o obj do banco de dados
		updateData(newObj, obj);// Atualizo os dados
		return repo.save(newObj);// Persisto o objeto que veio do BD.
	}

	public void delete(Integer id) {
		findOne(id);
		try {
			repo.delete(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionadas");
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
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDto) {

		Cliente c = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(),
				TipoCliente.toEnum(objDto.getTipo()), pe.encode(objDto.getSenha()));

		Cidade cid = cidadeRepository.findOne(objDto.getCidadeId());

		Endereco e = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(),
				objDto.getBairro(), objDto.getCep(), c, cid);

		c.getEnderecos().add(e);

		// O primeiro telefone é obrigatório
		c.getTelefones().add(objDto.getTelefone1());

		// O segundo telefone é opcional
		if (objDto.getTelefone2() != null)
			c.getTelefones().add(objDto.getTelefone2());
		// O terceiro telefone é opcional
		if (objDto.getTelefone3() != null)
			c.getTelefones().add(objDto.getTelefone3());

		return c;
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

	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();

		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}

		BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
		String fileName = prefix + user.getId() + ".jpg";
		return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
	}
}
