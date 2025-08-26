package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    private String baseUrl;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
        studentRepository.deleteAll();
        testStudent = new Student();
        testStudent.setName("Гарри Поттер");
        testStudent.setAge(17);
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        ResponseEntity<Student> response = restTemplate.postForEntity(
                baseUrl, testStudent, Student.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Гарри Поттер", response.getBody().getName());
        assertEquals(17, response.getBody().getAge());
    }

    @Test
    void getStudent_shouldReturnStudentWhenExists() {
        Student savedStudent = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + savedStudent.getId(), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedStudent.getId(), response.getBody().getId());
    }

    @Test
    void getStudent_shouldReturnNotFoundWhenNotExists() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/999", Student.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        Student savedStudent = restTemplate.postForObject(baseUrl, testStudent, Student.class);
        savedStudent.setName("Гарри Джеймс Поттер");
        savedStudent.setAge(18);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> request = new HttpEntity<>(savedStudent, headers);

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/" + savedStudent.getId(),
                HttpMethod.PUT, request, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Гарри Джеймс Поттер", response.getBody().getName());
        assertEquals(18, response.getBody().getAge());
    }

    @Test
    void deleteStudent_shouldReturnOkWhenExists() {
        Student savedStudent = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + savedStudent.getId(),
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getStudentsByAge_shouldReturnStudentsList() {
        restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + "/filter/17", Student[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }
}