package ru.hogwarts.school.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {
        // given
        Faculty faculty = new Faculty(null, "Gryffindor", "Red");

        // when
        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                getBaseUrl(), faculty, Faculty.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gryffindor", response.getBody().getName());
        assertEquals("Red", response.getBody().getColor());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void getFaculty_shouldReturnFacultyWhenExists() {
        // given
        Faculty faculty = new Faculty(null, "Slytherin", "Green");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(), faculty, Faculty.class);
        Long createdId = createResponse.getBody().getId();

        // when
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdId, Faculty.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Slytherin", response.getBody().getName());
        assertEquals("Green", response.getBody().getColor());
    }

    @Test
    void getFaculty_shouldReturnNotFoundWhenNotExists() {
        // when
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/999", Faculty.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {
        // given
        Faculty faculty = new Faculty(null, "Hufflepuff", "Yellow");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(), faculty, Faculty.class);
        Long createdId = createResponse.getBody().getId();

        Faculty updatedFaculty = new Faculty(createdId, "Hufflepuff", "Gold");

        // when
        ResponseEntity<Faculty> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedFaculty),
                Faculty.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gold", response.getBody().getColor());
    }

    @Test
    void deleteFaculty_shouldReturnOkWhenExists() {
        // given
        Faculty faculty = new Faculty(null, "Ravenclaw", "Blue");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(), faculty, Faculty.class);
        Long createdId = createResponse.getBody().getId();

        // when
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdId,
                HttpMethod.DELETE,
                null,
                Void.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getFacultiesByColor_shouldReturnFilteredFaculties() {
        // given
        Faculty faculty1 = new Faculty(null, "Ravenclaw", "Blue");
        Faculty faculty2 = new Faculty(null, "Gryffindor", "Red");

        restTemplate.postForEntity(getBaseUrl(), faculty1, Faculty.class);
        restTemplate.postForEntity(getBaseUrl(), faculty2, Faculty.class);

        // when
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/filter/Red", Faculty[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Gryffindor", response.getBody()[0].getName());
        assertEquals("Red", response.getBody()[0].getColor());
    }

    @Test
    void searchFaculties_shouldReturnMatchingFaculties() {
        // given
        Faculty faculty1 = new Faculty(null, "Gryffindor", "Red");
        Faculty faculty2 = new Faculty(null, "Ravenclaw", "Blue");

        restTemplate.postForEntity(getBaseUrl(), faculty1, Faculty.class);
        restTemplate.postForEntity(getBaseUrl(), faculty2, Faculty.class);

        // when
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/search?search=GRYFFINDOR", Faculty[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Gryffindor", response.getBody()[0].getName());
    }

    @Test
    void searchFaculties_shouldReturnEmptyWhenNoMatches() {
        // given
        Faculty faculty = new Faculty(null, "Gryffindor", "Red");
        restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class);

        // when
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/search?search=Hufflepuff", Faculty[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    @Test
    void getFacultyStudents_shouldReturnStudentsList() {
        // given
        Faculty faculty = new Faculty(null, "Gryffindor", "Red");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(), faculty, Faculty.class);
        Long createdId = createResponse.getBody().getId();

        // when
        ResponseEntity<Object> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdId + "/students", Object.class);

        // then
        assertTrue(response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
}