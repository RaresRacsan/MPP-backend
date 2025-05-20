package com.example.demo.repository;

import com.example.demo.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitleContainingIgnoreCase(String title, Sort sort);
    List<Movie> findByTitleContainingIgnoreCaseAndCategoryIn(String title, List<String> categories, Sort sort);
    List<Movie> findByTitleContainingIgnoreCaseAndRatingGreaterThanEqual(String title, Integer rating, Sort sort);
    List<Movie> findByTitleContainingIgnoreCaseAndCategoryInAndRatingGreaterThanEqual(String title, List<String> categories, Integer rating, Sort sort);
    List<Movie> findByCategoryIn(List<String> categories, Sort sort);
    List<Movie> findByCategoryInAndRatingGreaterThanEqual(List<String> categories, Integer rating, Sort sort);
    List<Movie> findByRatingGreaterThanEqual(Integer rating, Sort sort);
    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByTitleContainingIgnoreCaseAndCategoryIn(String title, List<String> categories);

    List<Movie> findByTitleContainingIgnoreCaseAndRatingGreaterThanEqual(String title, Integer rating);

    List<Movie> findByTitleContainingIgnoreCaseAndCategoryInAndRatingGreaterThanEqual(String title, List<String> categories, Integer rating);

    List<Movie> findByCategoryIn(List<String> categories);

    List<Movie> findByCategoryInAndRatingGreaterThanEqual(List<String> categories, Integer rating);

    List<Movie> findByRatingGreaterThanEqual(Integer rating);

    // New methods for paginated results
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Movie> findByTitleContainingIgnoreCaseAndCategoryIn(String title, List<String> categories, Pageable pageable);
    Page<Movie> findByTitleContainingIgnoreCaseAndRatingGreaterThanEqual(String title, Integer rating, Pageable pageable);
    Page<Movie> findByTitleContainingIgnoreCaseAndCategoryInAndRatingGreaterThanEqual(
            String title, List<String> categories, Integer rating, Pageable pageable);
    Page<Movie> findByCategoryIn(List<String> categories, Pageable pageable);
    Page<Movie> findByCategoryInAndRatingGreaterThanEqual(List<String> categories, Integer rating, Pageable pageable);
    Page<Movie> findByRatingGreaterThanEqual(Integer rating, Pageable pageable);

    @Query("SELECT m.category, COUNT(m) FROM Movie m GROUP BY m.category")
    List<Object[]> countMoviesByCategory();

    @Query("SELECT m.category, AVG(m.rating) FROM Movie m GROUP BY m.category")
    List<Object[]> avgRatingByCategory();
}
