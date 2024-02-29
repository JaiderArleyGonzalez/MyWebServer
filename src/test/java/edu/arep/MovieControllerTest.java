package edu.arep;
import org.junit.jupiter.api.Test;

import edu.arep.controller.MovieController;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
public class MovieControllerTest {

    @Test
    public void testGetByName() {
        String movieName = "Titanic";
        try {
            String result = MovieController.getByName(movieName);
            assertNotNull(result);
            assertTrue(result.contains("Titanic"));
        } catch (IOException e) {
            fail("IOException occurred");
        }
    }

    @Test
    public void testSaveFavorite() {
        String movieName = "The Godfather";
        try {
            String result = MovieController.saveFavorite(movieName);
            assertNotNull(result);
            assertTrue(result.contains("Se ha añadido la película"));
            assertTrue(result.contains("The Godfather"));
            assertTrue(MovieController.favorites.size() > 0);
        } catch (IOException e) {
            fail("IOException occurred");
        }
    }
}