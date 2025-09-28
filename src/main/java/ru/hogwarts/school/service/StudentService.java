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

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void printStudentsParallel() {
        List<Student> students = getAllStudents();

        if (students.size() < 6) {
            System.out.println("Недостаточно студентов для демонстрации (нужно минимум 6)");
            return;
        }

        // Основной поток - первые два имени
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        // Первый параллельный поток - третий и четвертый студент
        Thread thread1 = new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        // Второй параллельный поток - пятый и шестой студент
        Thread thread2 = new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Поток был прерван");
        }
    }

    public void printStudentsSynchronized() {
        List<Student> students = getAllStudents();

        if (students.size() < 6) {
            System.out.println("Недостаточно студентов для демонстрации (нужно минимум 6)");
            return;
        }

        // Основной поток - первые два имени
        printStudentNameSync(students.get(0).getName());
        printStudentNameSync(students.get(1).getName());

        // Первый параллельный поток - третий и четвертый студент
        Thread thread1 = new Thread(() -> {
            printStudentNameSync(students.get(2).getName());
            printStudentNameSync(students.get(3).getName());
        });

        // Второй параллельный поток - пятый и шестой студент
        Thread thread2 = new Thread(() -> {
            printStudentNameSync(students.get(4).getName());
            printStudentNameSync(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Поток был прерван");
        }
    }

    // Синхронизированный метод для вывода имен
    synchronized void printStudentNameSync(String name) {
        System.out.println(name);
    }
}