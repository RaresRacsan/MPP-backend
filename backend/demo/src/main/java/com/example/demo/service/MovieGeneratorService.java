package com.example.demo.service;

import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@EnableAsync
public class MovieGeneratorService {

    private final MovieRepository movieRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    private final List<String> categories = Arrays.asList("Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance", "Thriller");

    @Autowired
    public MovieGeneratorService(MovieRepository movieRepository, SimpMessagingTemplate messagingTemplate) {
        this.movieRepository = movieRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    @Scheduled(fixedRate = 30000) // Generate a new movie every 30 seconds
    public void generateRandomMovie() {
        Movie movie = createRandomMovie();
        Movie savedMovie = movieRepository.save(movie);

        // Send the new movie to all subscribers
        messagingTemplate.convertAndSend("/topic/movies", savedMovie);

        // Also send updated statistics for charts
        sendUpdatedChartData();
    }

    private Movie createRandomMovie() {
        String title = "Auto-Generated Movie " + UUID.randomUUID().toString().substring(0, 8);
        double rating = 1 + random.nextDouble() * 4; // Random rating between 1.0 and 5.0
        String description = "This is an auto-generated movie for demonstration purposes.";
        String category = categories.get(random.nextInt(categories.size()));

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setCategory(category);
        return movie;
    }

    private void sendUpdatedChartData() {
        // Get counts per category for the category chart
        List<Object[]> categoryCounts = movieRepository.countMoviesByCategory();

        // Get average ratings per category for the rating chart
        List<Object[]> categoryRatings = movieRepository.avgRatingByCategory();

        // Send both sets of data to the charts topic
        messagingTemplate.convertAndSend("/topic/charts/categories", categoryCounts);
        messagingTemplate.convertAndSend("/topic/charts/ratings", categoryRatings);
    }
}