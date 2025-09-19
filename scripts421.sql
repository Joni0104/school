ALTER TABLE student ADD CONSTRAINT age_check CHECK (age >= 16);


ALTER TABLE student
    ADD CONSTRAINT unique_name UNIQUE (name),
    ALTER COLUMN name SET NOT NULL;


ALTER TABLE faculty
    ADD CONSTRAINT unique_name_color UNIQUE (name, color);


ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;

CREATE TABLE IF NOT EXISTS avatar (
     id BIGSERIAL PRIMARY KEY,
     file_path VARCHAR(255) NOT NULL,
     file_size BIGINT NOT NULL,
     media_type VARCHAR(50) NOT NULL,
     data BYTEA,
     student_id BIGINT UNIQUE,

     CONSTRAINT fk_avatar_student
         FOREIGN KEY (student_id)
         REFERENCES student(id)
         ON DELETE CASCADE
 );