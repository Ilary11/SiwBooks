package it.uniroma3.siw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat; 
import jakarta.persistence.FetchType;

@Entity 
public class Autore {

    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO) // L'ID sarà generato automaticamente
    private Long id;

    @NotBlank
    private String nome;
    
    @NotBlank
    private String cognome;
    
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDiNascita;
    
    @Column(nullable = true) 
    private LocalDate dataDiMorte;
    
    @NotBlank
    private String nazionalita;
    
    private String urlFotografia; 
    
    @ManyToMany(mappedBy="autori", fetch = FetchType.EAGER)
    private Set<Libro> libriScritti;

    
    public Autore(String nome, String cognome, LocalDate dataDiNascita, LocalDate dataDiMorte, String nazionalita, String urlFotografia) {
    	this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.dataDiMorte = dataDiMorte;
        this.nazionalita = nazionalita;
        this.urlFotografia = urlFotografia;
    	this.libriScritti = new HashSet<>();
    }

    public Autore() {
        this.libriScritti = new HashSet<>();
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

	public LocalDate getDataDiMorte() {
		return dataDiMorte;
	}

	public void setDataDiMorte(LocalDate dataDiMorte) {
		this.dataDiMorte = dataDiMorte;
	}
	
	public String getNazionalita() {
		return nazionalita;
	}

	public void setNazionalita(String nazionalita) {
		this.nazionalita = nazionalita;
	}

	public String getUrlFotografia() {
		return urlFotografia;
	}

	public void setUrlFotografia(String urlFotografia) {
		this.urlFotografia = urlFotografia;
	}
	
	public LocalDate getDataDiNascita() {
		return dataDiNascita;
	}

	public void setDataDiNascita(LocalDate dataDiNascita) {
		this.dataDiNascita = dataDiNascita;
	}
	
	public Set<Libro> getLibriScritti() {
		return libriScritti;
	}

	public void setLibriScritti(Set<Libro> libriScritti) {
		this.libriScritti = libriScritti;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(nome, cognome);
	}

	@Override  //per confrontare ue artisti che sono uguali se hanno stesso nome e cognome
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Autore other = (Autore) obj;
		return Objects.equals(nome, other.nome) && Objects.equals(cognome, other.cognome);
	}

}