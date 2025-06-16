INSERT INTO files (id, name, original_file_name, path_file, resume_id) VALUES
(1, 'John_Tan_Resume.pdf', 'John_Tan_Resume.pdf', 'http://localhost:9001/browser/user-files/John_Tan_Resume.pdf', 1),
(2, 'Maria_Lopez_Resume.pdf', 'Maria_Lopez_Resume.pdf', 'http://localhost:9001/browser/user-files/Maria_Lopez_Resume.pdf', 2),
(3, 'Arif_Rahman_Resume.pdf', 'Arif_Rahman_Resume.pdf', 'http://localhost:9001/browser/user-files/Arif_Rahman_Resume.pdf', 3),
(4, 'Linh_Nguyen_Resume.pdf', 'Linh_Nguyen_Resume.pdf', 'http://localhost:9001/browser/user-files/Linh_Nguyen_Resume.pdf', 4),
(5, 'Dewi_Putri_Resume.pdf', 'Dewi_Putri_Resume.pdf', 'http://localhost:9001/browser/user-files/Dewi_Putri_Resume.pdf', 5),
(6, 'Samuel_Lim_Resume.pdf', 'Samuel_Lim_Resume.pdf', 'http://localhost:9001/browser/user-files/Samuel_Lim_Resume.pdf', 6),
(7, 'Anh_Tran_Resume.pdf', 'Anh_Tran_Resume.pdf', 'http://localhost:9001/browser/user-files/Anh_Tran_Resume.pdf', 7),
(8, 'Rizal_Ahmad_Resume.pdf', 'Rizal_Ahmad_Resume.pdf', 'http://localhost:9001/browser/user-files/Rizal_Ahmad_Resume.pdf', 8),
(9, 'Jennylyn_Cruz_Resume.pdf', 'Jennylyn_Cruz_Resume.pdf', 'http://localhost:9001/browser/user-files/Jennylyn_Cruz_Resume.pdf', 9),
(10, 'Budi_Santoso_Resume.pdf', 'Budi_Santoso_Resume.pdf', 'http://localhost:9001/browser/user-files/Budi_Santoso_Resume.pdf', 10),
(11, 'Nguyen_Phuong_Resume.pdf', 'Nguyen_Phuong_Resume.pdf', 'http://localhost:9001/browser/user-files/Nguyen_Phuong_Resume.pdf', 11),
(12, 'Kevin_Teo_Resume.pdf', 'Kevin_Teo_Resume.pdf', 'http://localhost:9001/browser/user-files/Kevin_Teo_Resume.pdf', 12),
(13, 'Fatimah_Yusuf_Resume.pdf', 'Fatimah_Yusuf_Resume.pdf', 'http://localhost:9001/browser/user-files/Fatimah_Yusuf_Resume.pdf', 13),
(14, 'Jomar_Santos_Resume.pdf', 'Jomar_Santos_Resume.pdf', 'http://localhost:9001/browser/user-files/Jomar_Santos_Resume.pdf', 14),
(15, 'Rina_Kartika_Resume.pdf', 'Rina_Kartika_Resume.pdf', 'http://localhost:9001/browser/user-files/Rina_Kartika_Resume.pdf', 15),
(16, 'Zhi_Hao_Resume.pdf', 'Zhi_Hao_Resume.pdf', 'http://localhost:9001/browser/user-files/Zhi_Hao_Resume.pdf', 16),
(17, 'Thu_Hoang_Resume.pdf', 'Thu_Hoang_Resume.pdf', 'http://localhost:9001/browser/user-files/Thu_Hoang_Resume.pdf', 17),
(18, 'Ahmad_Fahmi_Resume.pdf', 'Ahmad_Fahmi_Resume.pdf', 'http://localhost:9001/browser/user-files/Ahmad_Fahmi_Resume.pdf', 18),
(19, 'Ella_Reyes_Resume.pdf', 'Ella_Reyes_Resume.pdf', 'http://localhost:9001/browser/user-files/Ella_Reyes_Resume.pdf', 19),
(20, 'Joko_Pranoto_Resume.pdf', 'Joko_Pranoto_Resume.pdf', 'http://localhost:9001/browser/user-files/Joko_Pranoto_Resume.pdf', 20);

SELECT setval('file_id_seq', (SELECT MAX(id) from files));
