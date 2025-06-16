INSERT INTO users(id, name, company_id, email, password, job_title, role) VALUES
(1, 'Alice Johnson', 2, 'alice.johnson@yopmail.com', '$2b$12$1gppOw6Tq3xXnbM5Q9bGPuW9RM992WrXLytQ6VPGQSNhJOVDFs3Fe', 'HR_MANAGER', 'ROLE_USER'),
(2, 'Bob Smith', 4, 'bob.smith@yopmail.com', '$2b$12$2WNzh33QrpTvQA/mqkRIGeFn/t2Qit8edy/KekafDoSjnyUBplvEG', 'RECRUITMENT_SPECIALIST', 'ROLE_USER'),
(3, 'Clara Lee', 2, 'clara.lee@yopmail.com', '$2b$12$ztsU5Ua.DXq2k4e.mbQvB.e/zZLN/9wpoufOAc9yFpU5qx0aEhg5W', 'TECHNICAL_RECRUITER', 'ROLE_USER'),
(4, 'David Wright', 10, 'david.wright@yopmail.com', '$2b$12$t1ePwPMEeMINO4tAYLSL/eVH1y7cCTk8HkJJm93TReeh7ehEaprCC', 'PROJECT_HIRING_COORDINATOR', 'ROLE_USER'),
(5, 'Eva Martinez', 8, 'eva.martinez@yopmail.com', '$2b$12$CHiRMKyZRatRYb5lGNRM0uQpu6vjdT/dJPy08xzfMy7YvnVWuHu8G', 'TALENT_ACQUISITION_LEAD', 'ROLE_USER'),
(6, 'Wally Rouda', 11, 'wally@yopmail.com', '$2a$10$SienIUSu0QwXuFsxTDUEmejkQfgSZjzAq/h4qZC05Yzg0b5c9IRhW', 'SUPERADMIN', 'ROLE_SUPERADMIN'),
(7, 'Boss', 11, 'empire@yopmail.com', '$2a$10$KaV5Z0k.eQeGkvW.naouxu9CFH69SkATVkK0YzKfe7YP8I3AMOQf6', 'ADMIN', 'ROLE_ADMIN'),
(8, 'Denis', 11, 'den@yopmail.com', '$2a$10$CN4h1QRrC6gCOCdTgzF5kuYVYDYE/.RMjMv5CDQSMzylnvNa65vl6', 'ADMIN', 'ROLE_ADMIN');

SELECT setval('user_id_seq', (SELECT MAX(id) from users));
