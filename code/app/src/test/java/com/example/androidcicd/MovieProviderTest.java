package com.example.androidcicd;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.androidcicd.movie.Movie;
import com.example.androidcicd.movie.MovieProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MovieProviderTest {
    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockMovieCollection;

    @Mock
    private DocumentReference mockDocRef;

    private MovieProvider movieProvider;

    @Before
    public void setUp() {
        // Setup mocks
        MockitoAnnotations.openMocks(this);

        // Define behavior
        when(mockFirestore.collection("movies")).thenReturn(mockMovieCollection);
        when(mockMovieCollection.document()).thenReturn(mockDocRef);
        when(mockMovieCollection.document(anyString())).thenReturn(mockDocRef);

        // Setup movie provider
        MovieProvider.setInstanceForTesting(mockFirestore);
        movieProvider = MovieProvider.getInstance(mockFirestore);
    }

    @Test
    public void testAddMovieSetsId() {
        // Movie to add
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);

        // Define movie ID
        when(mockDocRef.getId()).thenReturn("123");


        // Add movie and assert expected ID
        movieProvider.addMovie(movie);
        assertEquals("Movie was not updated with correct id.", "123", movie.getId());

        // Verify set method was called
        verify(mockDocRef).set(movie);
    }

    @Test
    public void testDeleteMovie() {
        // Create movie and set id
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
        movie.setId("123");

        // Delete movie and verify firebase delete method called.
        movieProvider.deleteMovie(movie);
        verify(mockDocRef).delete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMovieShouldThrowErrorForDifferentIds() {
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
        movie.setId("1");

        // Set different ID for docref
        when(mockDocRef.getId()).thenReturn("123");

        // Update movie such that error is thrown for different IDs
        movieProvider.updateMovie(movie, "Another Title", "Another Genre", 2026);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMovieShouldThrowErrorForEmptyName() {
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
        movie.setId("123");
        when(mockDocRef.getId()).thenReturn("123");

        // Update movie such that error is thrown for empty name
        movieProvider.updateMovie(movie, "", "Another Genre", 2026);
    }
}
