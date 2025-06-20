package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Recensione {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String titolo;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer voto;

    @NotBlank
    private String testo;

    @ManyToOne(optional = false)
    private Utente utente;

    @ManyToOne(optional = false)
    private Libro libro;

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
}