-- V5: Reference data — categories and brands
-- =============================================================

-- ---------------------------------------------------------------
-- Categories (hierarchical)
-- ---------------------------------------------------------------
INSERT INTO inv.categories (id, name, parent_id, description) VALUES
-- L1 root
(1,  'Electronics',          NULL, 'All electronic products'),

-- L2 under Electronics
(2,  'Desktop Components',   1,    'PC building components'),
(3,  'Notebooks',            1,    'Laptops and mobile workstations'),
(4,  'Accessories',          1,    'Peripheral devices and add-ons'),
(5,  'Games',                1,    'Video games for all platforms'),
(6,  'Software',             1,    'Operating systems, productivity and utilities'),

-- L3 under Desktop Components
(7,  'CPUs',                 2,    'Processors for desktop PCs'),
(8,  'GPUs',                 2,    'Graphics cards'),
(9,  'Motherboards',         2,    'Desktop motherboards'),
(10, 'RAM',                  2,    'System memory modules'),
(11, 'Storage',              2,    'Hard drives and solid-state drives'),
(12, 'Cases',                2,    'PC chassis and enclosures'),
(13, 'Power Supplies',       2,    'ATX and SFX power supply units'),
(14, 'CPU Coolers',          2,    'Air and liquid cooling for CPUs'),

-- L3 under Accessories
(15, 'Keyboards',            4,    'Mechanical and membrane keyboards'),
(16, 'Mice',                 4,    'Gaming and productivity mice'),
(17, 'Headsets',             4,    'Gaming and audio headsets'),
(18, 'Webcams & Streaming',  4,    'Cameras and capture cards'),
(19, 'Controllers',          4,    'Gamepads and joysticks'),
(20, 'Monitors',             4,    'Displays and monitors');

-- Advance the sequence past the manually inserted IDs
SELECT setval('inv.sq_categories_id', 20, true);

-- ---------------------------------------------------------------
-- Brands
-- ---------------------------------------------------------------
INSERT INTO inv.brands (id, name, website, country_code, founded_year) VALUES
(1,  'Intel',           'https://www.intel.com',        'US', 1968),
(2,  'AMD',             'https://www.amd.com',           'US', 1969),
(3,  'NVIDIA',          'https://www.nvidia.com',        'US', 1993),
(4,  'ASUS',            'https://www.asus.com',          'TW', 1989),
(5,  'MSI',             'https://www.msi.com',           'TW', 1986),
(6,  'Gigabyte',        'https://www.gigabyte.com',      'TW', 1986),
(7,  'Corsair',         'https://www.corsair.com',       'US', 1994),
(8,  'Kingston',        'https://www.kingston.com',      'US', 1987),
(9,  'G.Skill',         'https://www.gskill.com',        'TW', 1989),
(10, 'Samsung',         'https://www.samsung.com',       'KR', 1969),
(11, 'Western Digital', 'https://www.westerndigital.com','US', 1970),
(12, 'Seagate',         'https://www.seagate.com',       'US', 1978),
(13, 'Crucial',         'https://www.crucial.com',       'US', 1996),
(14, 'Noctua',          'https://noctua.at',             'AT', 2005),
(15, 'be quiet!',       'https://www.bequiet.com',       'DE', 2001),
(16, 'Fractal Design',  'https://www.fractal-design.com','SE', 2007),
(17, 'Lian Li',         'https://www.lian-li.com',       'TW', 1983),
(18, 'Logitech',        'https://www.logitech.com',      'CH', 1981),
(19, 'Razer',           'https://www.razer.com',         'US', 2005),
(20, 'SteelSeries',     'https://steelseries.com',       'US', 2001),
(21, 'Lenovo',          'https://www.lenovo.com',        'CN', 1984),
(22, 'Dell',            'https://www.dell.com',          'US', 1984),
(23, 'HP',              'https://www.hp.com',            'US', 1939),
(24, 'Acer',            'https://www.acer.com',          'TW', 1976),
(25, 'LG',              'https://www.lg.com',            'KR', 1958),
(26, 'BenQ',            'https://www.benq.com',          'TW', 2001),
(27, 'Electronic Arts', 'https://www.ea.com',            'US', 1982),
(28, 'Ubisoft',         'https://www.ubisoft.com',       'FR', 1986),
(29, 'Microsoft',       'https://www.microsoft.com',     'US', 1975),
(30, 'CD Projekt',      'https://www.cdprojekt.com',     'PL', 1994),
(31, 'Valve',           'https://www.valvesoftware.com', 'US', 1996),
(32, 'Thermaltake',     'https://www.thermaltake.com',   'TW', 1999),
(33, 'EVGA',            'https://www.evga.com',          'US', 1999),
(34, 'Zotac',           'https://www.zotac.com',         'HK', 2006),
(35, 'Sapphire',        'https://www.sapphiretech.com',  'HK', 2001);

SELECT setval('inv.sq_brands_id', 35, true);
