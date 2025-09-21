package ru.hogwarts.school.integration;

import antlr.collections.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/student";
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        // given
        Student student = new Student(null, "Harry Potter", 17);

        // when
        ResponseEntity<Student> response = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Harry Potter", response.getBody().getName());
        assertEquals(17, response.getBody().getAge());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void getStudent_shouldReturnStudentWhenExists() {
        // given
        Student student = new Student(null, "Hermione Granger", 17);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long createdId = createResponse.getBody().getId();

        // when
        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdId, Student.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hermione Granger", response.getBody().getName());
        assertEquals(17, response.getBody().getAge());
    }

    @Test
    void getStudent_shouldReturnNotFoundWhenNotExists() {
        // when
        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/999", Student.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        // given
        Student student = new Student(null, "Ron Weasley", 16);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long createdId = createResponse.getBody().getId();

        Student updatedStudent = new Student(createdId, "Ronald Weasley", 17);

        // when
        ResponseEntity<Student> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                Student.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ronald Weasley", response.getBody().getName());
        assertEquals(17, response.getBody().getAge());
    }

    @Test
    void deleteStudent_shouldReturnOkWhenExists() {
        // given
        Student student = new Student(null, "Neville Longbottom", 16);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
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
    void getStudentsByAge_shouldReturnFilteredStudents() {
        // given
        Student student1 = new Student(null, "Draco Malfoy", 16);
        Student student2 = new Student(null, "Luna Lovegood", 15);

        restTemplate.postForEntity(getBaseUrl(), student1, Student.class);
        restTemplate.postForEntity(getBaseUrl(), student2, Student.class);

        // when
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/filter/16", Student[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Draco Malfoy", response.getBody()[0].getName());
        assertEquals(16, response.getBody()[0].getAge());
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnStudentsInRange() {
        // given
        Student student1 = new Student(null, "Ginny Weasley", 14);
        Student student2 = new Student(null, "Fred Weasley", 18);
        Student student3 = new Student(null, "George Weasley", 17);

        restTemplate.postForEntity(getBaseUrl(), student1, Student.class);
        restTemplate.postForEntity(getBaseUrl(), student2, Student.class);
        restTemplate.postForEntity(getBaseUrl(), student3, Student.class);

        // when
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/filter?min=15&max=17", Student[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("George Weasley", response.getBody()[0].getName());
    }

    @Test
    void getStudentFaculty_shouldReturnFacultyWhenExists() {
        // given
        Student student = new Student(null, "Harry Potter", 17);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);
        Long createdId = createResponse.getBody().getId();

        // when
        ResponseEntity<Object> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdId + "/faculty", Object.class);

        // then
        assertTrue(response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    void getTotalCount_shouldReturnStudentCount() {
        // when
        ResponseEntity<Integer> response = restTemplate.getForEntity(
                getBaseUrl() + "/count", Integer.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAverageAge_shouldReturnAverageAge() {
        // when
        ResponseEntity<Double> response = restTemplate.getForEntity(
                getBaseUrl() + "/average-age", Double.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getLastFiveStudents_shouldReturnLastFiveStudents() {
        // when
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/last-five", Student[].class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    @Test
    void getStudentsNamesStartingWithA_shouldReturnFilteredNames() {
        // given
        Student student1 = new Student(null, "Анна", 20);
        Student student2 = new Student(null, "Борис", 21);

        restTemplate.postForEntity(getBaseUrl(), student1, Student.class);
        restTemplate.postForEntity(getBaseUrl(), student2, Student.class);

        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                getBaseUrl() + "/names-starting-with-a", List.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAverageAgeStream_shouldReturnAverageAge() {
        // when
        ResponseEntity<Double> response = restTemplate.getForEntity(
                getBaseUrl() + "/average-age-stream", Double.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void calculateSum_shouldReturnSum() {
        // when
        ResponseEntity<Integer> response = restTemplate.getForEntity(
                getBaseUrl() + "/sum", Integer.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() > 0);
    }
}