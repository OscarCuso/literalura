package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Datos;
import com.alura.literalura.model.DatosLibro;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoApi;
import com.alura.literalura.service.ConvierteDatos;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE ="https://gutendex.com/books/?search=";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private final LibroRepository repositorio;
    private final AutorRepository autorRepositorio;
    public Principal(LibroRepository repository, AutorRepository autorRepositorio){
        this.repositorio = repository;
        this.autorRepositorio = autorRepositorio;
    }
    public void mostrarMenu(){
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    ---------------
                    Elija la opcion a traves de su numero: 
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    0- Salir
                    
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    librosRegistrados();
                    break;
                case 3:
                    autoresRegistrados();
                    break;
                case 4:
                    autoresVivos();
                    break;
                case 5:
                    librosPorIdioma();
                    break;
                case 0:
                    System.out.println("\nSaliendo de la aplicacion...");
                    break;
            }
        }
    }

    private void librosPorIdioma() {
        System.out.println("""
                Ingrese el Idioma para buscar los libros:
                es- español
                en- ingles
                fr- frances
                pt- portugués
                """);
        var idioma = teclado.nextLine();
        List<Libro> idiomaLibro = repositorio.buscarIdioma(idioma);
        idiomaLibro.forEach(System.out::println);
    }

    private void autoresVivos() {
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar");
        var fecha = teclado.nextInt();
        List<Autor> autores = autorRepositorio.autoresVivos(fecha);
        autores.forEach(System.out::println);
    }

    private void autoresRegistrados() {
        List<Autor> autores = autorRepositorio.findAll();
        autores.forEach(System.out::println);
    }

    private void librosRegistrados() {
        List<Libro> libros = repositorio.findAll();
        libros.forEach(System.out::println);
    }

    private void buscarLibro() {
        DatosLibro datos = getDatosLibro();
        if(datos != null){
            Autor autor = new Autor(datos.autor().get(0).nombre(), datos.autor().get(0).fechaNacimiento(),
                    datos.autor().get(0).fechaFallecimiento());
            Libro libro = new Libro(datos.titulo(),autor, datos.idioma().get(0), datos.numeroDescargas());
            Optional<Libro> libroExixtente = repositorio.findByTitulo(libro.getTitulo());
            if(libroExixtente.isPresent()){
                System.out.println("No se puede registrar el mismo libro mas de una vez");
            }else{
                System.out.println(libro);
                Optional<Autor> autorExistente = autorRepositorio.findByNombre(autor.getNombre());
                Autor nuevoAutor = autorExistente.orElseGet(() -> {
                    return autorRepositorio.save(new Autor(autor.getNombre(), autor.getFechaNacimiento(),
                            autor.getFechaFallecimiento()));
                });
                libro.setAutor(nuevoAutor);
                repositorio.save(libro);
            }
        }

    }
    private DatosLibro getDatosLibro(){
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var nombreLibro = teclado.nextLine();
       try{
           var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "%20"));
           Datos datos = convierteDatos.obtenerDatos(json, Datos.class);
           return datos.datosLibro().get(0);
       } catch (Exception e) {
           System.out.println("\nLibro no encontrado");
       }
       return null;
    }
}
