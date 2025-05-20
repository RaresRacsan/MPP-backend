package com.example.demo.testing;

import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import com.example.demo.controller.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MovieControllerTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MainController movieController;

    private List<Movie> movieList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        movieList = new ArrayList<>();
        movieList.add(new Movie("Movie2", 1.0, "film cacao", "comedie"));
        movieList.add(new Movie("Movie1", 5.0, "film smeker", "drama"));
        movieList.add(new Movie("Movie3", 4.2, "film bun", "horror"));
        movieList.add(new Movie("AddedMovie", 2.0, "yes", "NewMovie"));
        movieList.add(new Movie("no", 1.0, "no", "no"));
    }

    @Test
    public void testFilterMoviesByTitle() {
        String searchQuery = "Movie";
        List<Movie> filteredMovies = List.of(movieList.get(0), movieList.get(2));

        when(movieRepository.findByTitleContainingIgnoreCase(searchQuery, Sort.unsorted()))
                .thenReturn(filteredMovies);

        List<Movie> response = movieController.getAllMovies(null, searchQuery, null, null, null);

        assertEquals(2, response.size());
        assertEquals(filteredMovies, response);
    }

    @Test
    public void testFilterMoviesByCategory() {
        List<String> categories = List.of("drama", "horror");
        List<Movie> filteredMovies = List.of(movieList.get(1), movieList.get(2));

        when(movieRepository.findByCategoryIn(categories, Sort.unsorted()))
                .thenReturn(filteredMovies);

        List<Movie> response = movieController.getAllMovies(null, null, categories, null, null);

        assertEquals(2, response.size());
        assertEquals(filteredMovies, response);
    }

    @Test
    public void testFilterMoviesByRating() {
        int minRating = 4;
        List<Movie> filteredMovies = List.of(movieList.get(1), movieList.get(2));

        when(movieRepository.findByRatingGreaterThanEqual(minRating, Sort.unsorted()))
                .thenReturn(filteredMovies);

        List<Movie> response = movieController.getAllMovies(null, null, null, minRating, null);

        assertEquals(2, response.size());
        assertEquals(filteredMovies, response);
    }

    @Test
    public void testSortMoviesByRating() {
        List<Movie> sortedMovies = List.of(movieList.get(1), movieList.get(2), movieList.get(3), movieList.get(0), movieList.get(4));

        when(movieRepository.findAll(Sort.by(Sort.Direction.DESC, "rating")))
                .thenReturn(sortedMovies);

        List<Movie> response = movieController.getAllMovies("desc", null, null, null, null);

        assertEquals(5, response.size());
        assertEquals(sortedMovies, response);
    }

    @Test
    public void testSortMoviesAlphabetically() {
        List<Movie> sortedMovies = List.of(movieList.get(3), movieList.get(1), movieList.get(0), movieList.get(2), movieList.get(4));

        when(movieRepository.findAll(Sort.by(Sort.Direction.ASC, "title")))
                .thenReturn(sortedMovies);

        List<Movie> response = movieController.getAllMovies(null, null, null, null, "asc");

        assertEquals(5, response.size());
        assertEquals(sortedMovies, response);
    }
}