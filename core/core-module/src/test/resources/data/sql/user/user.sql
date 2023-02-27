INSERT INTO users(id, password, first_name, last_name, phone, email, status, is_deleted, role)
VALUES (1, 'password', 'Wade', 'Smith', '+380453345777', 'wade.smith@gmail.com', 'EMAIL_CONFIRMATION', false, null),
       (2, 'password', 'Boris', 'Johnson', '+380453345778', 'boris.johnson@gmail.com', 'ACTIVE', false, 'ROLE_ADMIN'),
       (3, 'password', 'Harvey', 'Williams', '+380453345779', 'harvey.williams@gmail.com', 'ACTIVE', true, 'ROLE_USER'),
       (4, 'password', 'Roberto', 'Brown', '+380453345780', 'roberto.brown@gmail.com', 'ACTIVE', false, 'ROLE_USER'),
       (5, 'password', 'David', 'Miller', '+380453345781', 'david.miller@gmail.com', 'WAITING_FOR_APPROVING', false, null),
       (6, 'password', 'Luke', 'Skywalker', '+380453345782', 'luke.skywalker@gmail.com', 'DECLINED', false, null),
       (7, 'password', 'Darth', 'Vader', '+380453345783', 'darth.vader@gmail.com', 'ACTIVE', false, 'ROLE_USER');
