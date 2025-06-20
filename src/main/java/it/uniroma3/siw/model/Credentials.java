package it.uniroma3.siw.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity //tabella nel db per le credenziali 
public class Credentials {

	//queste costanti rappresentano i tipi di ruolo che un utente può avere (static final: globali e immutabili) 
	public static final String DEFAULT_ROLE = "DEFAULT"; // di default è un utente normale
	public static final String ADMIN_ROLE = "ADMIN"; //altrimenti è un amministratore
	
	@Id //chiave primaria
    @GeneratedValue(strategy = GenerationType.AUTO) //generata in automatico
    private Long id;
	private String username;
	private String password;
	private String role;

	@OneToOne(cascade = CascadeType.ALL) //un utente può avere solo un insieme di credenziali 
	private Utente utente; //cascade significa che tutte le operazioni crud si propagano anche all'oggetto user 
	//ad es se viene eliminato oggetto Credentials viene elimianto anche l'oggetto User collegato
	
	//metodi getter e setter
	public String getUsername() {
		return username;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Utente getUser() {
		return utente;
	}

	public void setUser(Utente utente) {
		this.utente = utente;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

}
