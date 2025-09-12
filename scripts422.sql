CREATE TABLE car (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 18),
    has_license BOOLEAN DEFAULT FALSE,
    car_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_person_car
        FOREIGN KEY (car_id)
        REFERENCES car(id)
        ON DELETE SET NULL
);


CREATE INDEX idx_person_name ON person(name);
CREATE INDEX idx_person_car_id ON person(car_id);
CREATE INDEX idx_car_brand_model ON car(brand, model);