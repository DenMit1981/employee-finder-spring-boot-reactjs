INSERT INTO companies(id, company_name, reg_number) VALUES
(1, 'NovaTech Solutions', 'NT-983274'),
(2, 'GlobalEdge Consulting', 'GE-457892'),
(3, 'Skyline Innovations', 'SI-239817'),
(4, 'Quantum Systems Ltd.', 'QS-128374'),
(5, 'BrightCore Technologies', 'BC-765432'),
(6, 'GreenSphere Enterprises', 'GS-346728'),
(7, 'Vertex Dynamics', 'VD-897234'),
(8, 'Orion Business Group', 'OB-230945'),
(9, 'PulseWave Industries', 'PW-556718'),
(10, 'BluePeak Logistics', 'BP-119320'),
(11, 'Empire Solutions', 'RM-777777');

SELECT setval('company_id_seq', (SELECT MAX(id) from companies));

