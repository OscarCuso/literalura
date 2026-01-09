package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Autor> buscarAutores(@Param("nombre") String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento < :fecha AND a.fechaFallecimiento > :fecha")
    List<Autor> autoresVivos(@Param("fecha") Integer fecha);

    @Query("SELECT a FROM Autor a WHERE a.fechaFallecimiento IS NULL OR (a.fechaNacimiento + :edad) <= a.fechaFallecimiento")
    List<Autor> autoresVivosConCiertaEdad(@Param("edad")Integer edad);
}
