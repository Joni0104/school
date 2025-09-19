SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name,
    f.color AS faculty_color
FROM student s
LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.name;


SELECT
    s.name AS student_name,
    s.age AS student_age,
    a.file_path AS avatar_path,
    a.file_size AS avatar_size
FROM student s
INNER JOIN avatar a ON s.id = a.student_id
ORDER BY s.name;