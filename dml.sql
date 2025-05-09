
use gymdb;


INSERT INTO Member (name, username, passwordd, is_premium) VALUES
('John Smith',       'johnsmith',    'john2025'   , FALSE),
('Emma Johnson',     'emmaj'   ,    'emma2025'   , TRUE),
('Michael Brown',    'michaelb',     'mikeBrown1' , FALSE),
('Olivia Davis',     'oliviad',      'oliviaD123' , FALSE),
('William Miller',   'williamm',     'willMiller1', TRUE),
('Ava Wilson',       'avaw'    ,    'avaWilson7' , FALSE),
('James Moore',      'jamesm'  ,    'jamesMoore8', TRUE),
('Isabella Taylor',  'isabellat',    'isabella01' , FALSE),
('Benjamin Anderson','benjamina',    'benAnderson', FALSE),
('Sophia Thomas',    'sophiat' ,     'sophiaThoma', TRUE);

INSERT INTO Instructor (name, passwordd) VALUES
('Alexander Lee',      'inst2025012'),
('Charlotte Martin',   'inst2025023'),
('Daniel Rodriguez',   'inst2025034'),
('Mia Thompson',       'inst2025045'),
('Christopher White',  'inst2025056'),
('Harper Lewis',       'inst2025067'),
('Matthew Walker',     'inst2025078'),
('Abigail Hall',       'inst2025089'),
('Joshua Allen',       'inst2025090'),
('Emily Young',        'inst2025101');

INSERT INTO Admin (name, passwordd) VALUES
('Ryan Baker',      'adm202501234'),
('Victoria Perez',  'adm202502345'),
('Anthony Gonzalez','adm202503456'),
('Natalie Roberts', 'adm202504567'),
('Nicholas Ramirez','adm202505678'),
('Samantha Morgan', 'adm202506789'),
('Jacob Cox',       'adm202507890'),
('Lily Brooks',     'adm202508901'),
('Brandon Kelly',   'adm202509012'),
('Zoe Foster',      'adm202510123');

INSERT INTO Class (timee, instructor_id, capacity) VALUES
('2025-06-01 08:00:00',  1, 20),
('2025-06-02 10:30:00',  2, 25),
('2025-06-04 14:00:00',  3, 18),
('2025-06-07 16:45:00',  4, 30),
('2025-06-10 09:15:00',  5, 22),
('2025-06-12 13:30:00',  6, 28),
('2025-06-15 11:00:00',  7, 16),
('2025-06-18 15:20:00',  8, 35),
('2025-07-01 12:00:00',  9, 24),
('2025-07-05 17:10:00', 10, 32);

INSERT INTO Payment (member_id, amount, paid_at) VALUES
(1,  49.99, '2025-05-02 10:15:00'),
(2,  99.00, '2025-05-05 14:30:00'),
(3,  25.50, '2025-05-07 09:45:00'),
(4,  75.75, '2025-05-10 16:00:00'),
(5, 120.00, '2025-05-12 11:20:00'),
(6,  60.25, '2025-05-15 13:50:00'),
(7,  85.00, '2025-05-18 08:30:00'),
(8,  45.10, '2025-05-20 17:15:00'),
(9, 150.00, '2025-06-01 12:00:00'),
(10, 30.00, '2025-06-03 15:40:00');

INSERT INTO GymStatus (member_id, is_active) VALUES
(1, TRUE),
(2, TRUE),
(3, FALSE),
(4, TRUE),
(5, FALSE),
(6, TRUE),
(7, TRUE),
(8, FALSE),
(9, TRUE),
(10, TRUE);

