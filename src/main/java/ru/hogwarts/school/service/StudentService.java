package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student: {}", student);
        return studentRepository.save(student);
    }

    public Optional<Student> findStudent(Long id) {
        logger.info("Was invoked method for find student by id: {}", id);
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            logger.warn("Student with id {} not found", id);
        }
        return student;
    }

    public Optional<Student> editStudent(Long id, Student student) {
        logger.info("Was invoked method for edit student with id: {}", id);
        logger.debug("Editing student data: {}", student);

        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(student.getName());
                    existingStudent.setAge(student.getAge());
                    existingStudent.setFaculty(student.getFaculty());
                    Student savedStudent = studentRepository.save(existingStudent);
                    logger.debug("Student successfully updated: {}", savedStudent);
                    return savedStudent;
                });
    }

    public boolean deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with id: {}", id);
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            logger.info("Student with id {} successfully deleted", id);
            return true;
        }
        logger.warn("Attempt to delete non-existing student with id: {}", id);
        return false;
    }

    public List<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for get students by age: {}", age);
        List<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for get students by age between {} and {}", minAge, maxAge);
        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range {}-{}", students.size(), minAge, maxAge);
        return students;
    }

    public Integer getTotalCount() {
        logger.info("Was invoked method for get total students count");
        Integer count = studentRepository.countAllStudents();
        logger.debug("Total students count: {}", count);
        return count;
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        Double averageAge = studentRepository.findAverageAge();
        logger.debug("Average students age: {}", averageAge);
        return averageAge;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.findLastFiveStudents();
        logger.debug("Last five students: {}", students);
        return students;
    }
}