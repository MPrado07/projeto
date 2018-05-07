package model;

public class Usuário {
	
	public String nome;
	public char sexo;
	public double dataNascimento;
	public double cpf;
	public double rg;
	public double celular;
	
	public Usuário() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public Usuário(String nome, char sexo, double dataNascimento, double cpf, double rg, double celular) {
		
		this.nome= nome;
		this.sexo = sexo;
		this.dataNascimento = dataNascimento;
		this.cpf = cpf;
		this.rg = rg;
		this.celular = celular;
		
		
	}
	
	// My getts and Setters
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public char getSexo() {
		return sexo;
	}
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}
	public double getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(double dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public double getCpf() {
		return cpf;
	}
	public void setCpf(double cpf) {
		this.cpf = cpf;
	}
	public double getRg() {
		return rg;
	}
	public void setRg(double rg) {
		this.rg = rg;
	}
	public double getCelular() {
		return celular;
	}
	public void setCelular(double celular) {
		this.celular = celular;
	}
	
	

	
	
		
	
}
