package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
        facultyRepository.deleteAll();
        testFaculty = new Faculty();
        testFaculty.setName("Гриффиндор");
        testFaculty.setColor("красный");
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {
        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl, testFaculty, Faculty.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Гриффиндор", response.getBody().getName());
        assertEquals("красный", response.getBody().getColor());
    }

    @Test
    void getFaculty_shouldReturnFacultyWhenExists() {
        Faculty savedFaculty = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + savedFaculty.getId(), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedFaculty.getId(), response.getBody().getId());
    }

    @Test
    void getFaculty_shouldReturnNotFoundWhenNotExists() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/999", Faculty.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {
        Faculty savedFaculty = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);
        savedFaculty.setName("Слизерин");
        savedFaculty.setColor("зеленый");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(savedFaculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/" + savedFaculty.getId(),
                HttpMethod.PUT, request, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Слизерин", response.getBody().getName());
        assertEquals("зеленый", response.getBody().getColor());
    }

    @Test
    void deleteFaculty_shouldReturnOkWhenExists() {
        Faculty savedFaculty = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + savedFaculty.getId(),
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getFacultiesByColor_shouldReturnFacultiesList() {
        restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                baseUrl + "/filter/красный", Faculty[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void searchFaculties_shouldReturnFacultiesList() {
        restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                baseUrl + "/search?search=Гриффиндор", Faculty[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }
}