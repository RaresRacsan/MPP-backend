package com.example.demo.controller;

import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MainController {
    private final MovieRepository movieRepository;

    public MainController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/health")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/main")
    @CrossOrigin(origins = "http://localhost:3000")
    public Page<Movie> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/movie/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Movie> getMovieById(@PathVariable Integer id) {
        return movieRepository.findById(id)
                .map(movie -> ResponseEntity.ok().body(movie))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addMovie(@RequestBody Movie movie) {
        Map<String, String> errors = validateMovie(movie);
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            movieRepository.save(movie);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @CrossOrigin(origins = "http://localhost::3000")
    public ResponseEntity<Void> deleteMovie(@PathVariable Integer id) {
        try {
            movieRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMovie(@PathVariable Integer id, @RequestBody Movie updatedMovie) {
        Map<String, String> errors = validateMovie(updatedMovie);
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Optional<Movie> existingMovie = movieRepository.findById(id);
        if (existingMovie.isPresent()) {
            Movie movie = existingMovie.get();
            movie.setTitle(updatedMovie.getTitle());
            movie.setRating(updatedMovie.getRating());
            movie.setDescription(updatedMovie.getDescription());
            movie.setCategory(updatedMovie.getCategory());
            movieRepository.save(movie);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/movies/sort")
    @CrossOrigin(origins = "http://localhost:3000")
    public Page<Movie> sortMovies(
            @RequestParam String field,
            @RequestParam String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, field));
        return movieRepository.findAll(pageable);
    }

    @GetMapping("/movies/filter")
    @CrossOrigin(origins = "http://localhost:3000")
    public Page<Movie> filterMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isEmpty()) {
            if (categories != null && !categories.isEmpty()) {
                if (rating != null) {
                    return movieRepository.findByTitleContainingIgnoreCaseAndCategoryInAndRatingGreaterThanEqual(
                            search, categories, rating, pageable);
                } else {
                    return movieRepository.findByTitleContainingIgnoreCaseAndCategoryIn(
                            search, categories, pageable);
                }
            } else {
                if (rating != null) {
                    return movieRepository.findByTitleContainingIgnoreCaseAndRatingGreaterThanEqual(
                            search, rating, pageable);
                } else {
                    return movieRepository.findByTitleContainingIgnoreCase(search, pageable);
                }
            }
        } else {
            if (categories != null && !categories.isEmpty()) {
                if (rating != null) {
                    return movieRepository.findByCategoryInAndRatingGreaterThanEqual(
                            categories, rating, pageable);
                } else {
                    return movieRepository.findByCategoryIn(categories, pageable);
                }
            } else {
                if (rating != null) {
                    return movieRepository.findByRatingGreaterThanEqual(rating, pageable);
                } else {
                    return movieRepository.findAll(pageable);
                }
            }
        }
    }

    private Map<String, String> validateMovie(Movie movie) {
        Map<String, String> errors = new HashMap<>();

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            errors.put("title", "Title is required and cannot be empty.");
        } else if (movie.getTitle().length() > 100) {
            errors.put("title", "Title must not exceed 100 characters.");
        }

        if (movie.getRating() < 1 || movie.getRating() > 5) {
            errors.put("rating", "Rating must be between 1 and 5.");
        }

        if (movie.getDescription() == null || movie.getDescription().trim().isEmpty()) {
            errors.put("description", "Description is required and cannot be empty.");
        } else if (movie.getDescription().length() > 500) {
            errors.put("description", "Description must not exceed 500 characters.");
        }

        if (movie.getCategory() == null || movie.getCategory().trim().isEmpty()) {
            errors.put("category", "Category is required and cannot be empty.");
        }

        return errors;
    }
}
