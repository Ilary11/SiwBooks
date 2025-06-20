package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column; // Per @Column(unique=true)
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "{utente.nome.notblank}")
    private String nome;

    @NotBlank(message = "{utente.cognome.notblank}")
    private String cognome;

    @NotBlank(message = "{utente.email.notblank}")
    @Email(message = "{utente.email.invalid}")
    @Column(unique = true) // Assicura che l'email sia unica nel DB
    private String email;

  

    // Costruttore no-arg (necessario per JPA)
    public Utente() {}

    // Costruttore con campi obbligatori
    public Utente(String nome, String cognome, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        
    }

    // --- Metodi Getter e Setter ---
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    

    // --- equals e hashCode ---
    @Override
    public int hashCode() {
        // Se l'ID è presente, usalo per l'hash; altrimenti, usa i campi unici/significativi.
        // Ho incluso la password nell'hashing se l'ID non è disponibile, assumendo che
        // sia un campo significativo per distinguere gli utenti in fase di creazione/senza ID.
        return id != null ? Objects.hash(id) : Objects.hash(nome, cognome, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utente other = (Utente) obj;
        // Se entrambi gli ID sono presenti, confronta solo gli ID (sono la chiave primaria).
        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }
        // Altrimenti, confronta tutti i campi rilevanti per l'uguaglianza logica.
        // Ho incluso la password qui, dato che fa parte delle proprietà distintive dell'utente.
        return Objects.equals(nome, other.nome) &&
               Objects.equals(cognome, other.cognome) &&
               Objects.equals(email, other.email);
    }
}