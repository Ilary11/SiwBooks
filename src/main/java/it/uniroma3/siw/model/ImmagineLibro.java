package it.uniroma3.siw.model;

	import jakarta.persistence.Entity;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne; // Per la relazione ManyToOne con Libro
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
	
	@Entity
	public class ImmagineLibro {

	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;

	    @NotBlank
	    private String url; // L'URL dell'immagine

	    @ManyToOne // Molte immagini possono appartenere a un solo libro
	    @JoinColumn(name = "libro_id")
	    private Libro libro; // Il libro a cui appartiene questa immagine

	    public ImmagineLibro() {}

	    // Costruttore per comodità
	    public ImmagineLibro(String url) {
	        this.url = url;
	    }

	    // --- Getter e Setter ---
	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getUrl() {
	        return url;
	    }

	    public void setUrl(String url) {
	        this.url = url;
	    }

	    public Libro getLibro() {
	        return libro;
	    }

	    public void setLibro(Libro libro) {
	        this.libro = libro;
	    }

	    // --- equals e hashCode (basati sull'ID) ---
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        ImmagineLibro immagine = (ImmagineLibro) o;
	        return id != null && Objects.equals(id, immagine.id);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id);
	    }
	}

