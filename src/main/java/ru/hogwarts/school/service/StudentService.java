package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> editStudent(Long id, Student student) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(student.getName());
                    existingStudent.setAge(student.getAge());
                    existingStudent.setFaculty(student.getFaculty());
                    return studentRepository.save(existingStudent);
                });
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }
    public Integer getTotalCount() {
        return studentRepository.countAllStudents();
    }

    public Double getAverageAge() {
        return studentRepository.findAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        return studentRepository.findLastFiveStudents();
    }
}