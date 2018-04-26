package com.benefrancis.cursomc.dto;

import java.io.Serializable;

import com.benefrancis.cursomc.domain.Categoria;

public class CategoriaDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nome;

	public CategoriaDTO() {
		super();
	}

	public CategoriaDTO(Categoria c) {
		this.id = c.getId();
		this.nome = c.getNome();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
