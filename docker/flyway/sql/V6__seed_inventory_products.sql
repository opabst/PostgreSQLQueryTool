-- V6: Seed inventory — 5 000 products, stock entries and price history
-- =====================================================================
-- Strategy: PL/pgSQL DO block using generate_series and name-template
-- arrays to produce realistic product data at scale without 5 000
-- explicit INSERT rows.
-- =====================================================================

DO $$
DECLARE
    -- ---------------------------------------------------------------
    -- Helper variables
    -- ---------------------------------------------------------------
    v_product_id   BIGINT;
    v_sku          TEXT;
    v_name         TEXT;
    v_price        NUMERIC(10,2);
    v_brand_id     BIGINT;
    v_cat_id       BIGINT;
    v_i            INT;

    -- ---------------------------------------------------------------
    -- CPUs  (category 7)
    -- ---------------------------------------------------------------
    cpu_names TEXT[] := ARRAY[
        'Core i9-14900K', 'Core i9-14900KF', 'Core i9-13900K', 'Core i9-13900KF',
        'Core i7-14700K', 'Core i7-14700KF', 'Core i7-13700K', 'Core i7-13700KF',
        'Core i5-14600K', 'Core i5-14600KF', 'Core i5-13600K', 'Core i5-13600KF',
        'Core i5-12600K', 'Core i5-12600KF', 'Core i3-14100',  'Core i3-13100',
        'Ryzen 9 7950X',  'Ryzen 9 7950X3D', 'Ryzen 9 7900X',  'Ryzen 9 7900X3D',
        'Ryzen 7 7800X3D','Ryzen 7 7700X',   'Ryzen 7 7700',   'Ryzen 7 5800X3D',
        'Ryzen 5 7600X',  'Ryzen 5 7600',    'Ryzen 5 5600X',  'Ryzen 5 5600',
        'Ryzen 5 5500',   'Ryzen 3 5300G'
    ];
    cpu_brands BIGINT[] := ARRAY[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                                  2,2,2,2,2,2,2,2,2,2,2,2,2,2];
    cpu_prices NUMERIC[] := ARRAY[
        589,549,529,499,389,369,349,329,289,269,279,259,249,229,129,109,
        699,649,549,499,449,399,329,349,299,279,249,199,149,129
    ];

    -- ---------------------------------------------------------------
    -- GPUs (category 8)
    -- ---------------------------------------------------------------
    gpu_names TEXT[] := ARRAY[
        'GeForce RTX 4090 24GB','GeForce RTX 4080 Super 16GB','GeForce RTX 4080 16GB',
        'GeForce RTX 4070 Ti Super 16GB','GeForce RTX 4070 Ti 12GB',
        'GeForce RTX 4070 Super 12GB','GeForce RTX 4070 12GB',
        'GeForce RTX 4060 Ti 16GB','GeForce RTX 4060 Ti 8GB','GeForce RTX 4060 8GB',
        'GeForce RTX 3090 Ti 24GB','GeForce RTX 3080 Ti 12GB',
        'Radeon RX 7900 XTX 24GB','Radeon RX 7900 XT 20GB','Radeon RX 7900 GRE 16GB',
        'Radeon RX 7800 XT 16GB','Radeon RX 7700 XT 12GB',
        'Radeon RX 7600 8GB','Radeon RX 6800 XT 16GB','Radeon RX 6700 XT 12GB'
    ];
    gpu_brands BIGINT[] := ARRAY[3,3,3,3,3,3,3,3,3,3,3,3,
                                  2,2,2,2,2,2,2,2];
    gpu_prices NUMERIC[] := ARRAY[
        1599,999,799,799,699,599,499,449,399,299,799,699,
        999,849,649,499,449,299,599,399
    ];

    -- ---------------------------------------------------------------
    -- Motherboards (category 9)
    -- ---------------------------------------------------------------
    mb_names TEXT[] := ARRAY[
        'ROG Maximus Z790 Extreme','ROG Strix Z790-E Gaming WiFi',
        'ProArt Z790-Creator WiFi','TUF Gaming Z790-Plus WiFi',
        'ROG Crosshair X670E Extreme','ROG Strix X670E-E Gaming WiFi',
        'ProArt X670E-Creator WiFi','TUF Gaming X670E-Plus WiFi',
        'MEG Z790 ACE','MAG Z790 Tomahawk WiFi','MPG Z790 Edge WiFi',
        'MEG X670E ACE','MAG X670E Tomahawk WiFi',
        'Z790 Aorus Master','Z790 Aorus Elite AX',
        'X670E Aorus Master','X670E Aorus Tachyon',
        'B650 Aorus Elite AX','B760M Aorus Elite AX',
        'Prime Z790-A WiFi'
    ];
    mb_brands BIGINT[] := ARRAY[4,4,4,4,4,4,4,4,5,5,5,5,5,6,6,6,6,6,6,4];
    mb_prices NUMERIC[] := ARRAY[
        799,499,429,329,799,449,399,299,699,349,279,599,329,
        469,299,489,599,229,179,349
    ];

    -- ---------------------------------------------------------------
    -- RAM (category 10)
    -- ---------------------------------------------------------------
    ram_names TEXT[] := ARRAY[
        'Vengeance DDR5-6000 32GB (2x16)','Vengeance DDR5-5600 32GB (2x16)',
        'Vengeance DDR4-3600 32GB (2x16)','Dominator Platinum DDR5-6400 32GB',
        'Trident Z5 DDR5-6000 32GB','Trident Z5 RGB DDR5-6400 32GB',
        'Trident Z Neo DDR4-3600 32GB','Ripjaws V DDR4-3200 32GB',
        'Fury Beast DDR5-5200 32GB','Fury Beast DDR4-3200 32GB',
        'ValueRAM DDR5-4800 16GB','ValueRAM DDR4-3200 16GB',
        'Ballistix MAX DDR4-4400 16GB','Pro DDR5-4800 16GB',
        'Fury Renegade DDR5-6000 32GB','Fury Renegade DDR4-4000 16GB',
        'CT2K16G5C48C2 32GB DDR5','CT2K16G4DFRA32A 32GB DDR4',
        'OLOy Blade DDR5-6400 32GB','TeamGroup T-Force Delta DDR5-6000 32GB'
    ];
    ram_brands BIGINT[] := ARRAY[7,7,7,7,9,9,9,9,8,8,8,8,8,10,8,8,13,13,13,13];
    ram_prices NUMERIC[] := ARRAY[
        149,129,99,219,159,179,99,79,119,69,59,39,189,79,
        169,119,89,59,109,129
    ];

    -- ---------------------------------------------------------------
    -- Storage (category 11)
    -- ---------------------------------------------------------------
    storage_names TEXT[] := ARRAY[
        '990 Pro 2TB NVMe M.2','990 Pro 1TB NVMe M.2',
        '980 Pro 2TB NVMe M.2','980 Pro 1TB NVMe M.2',
        '870 EVO 4TB SATA SSD','870 EVO 2TB SATA SSD','870 QVO 8TB SATA SSD',
        'WD Black SN850X 2TB','WD Black SN850X 1TB','WD Blue SN580 1TB',
        'WD Blue 4TB SATA SSD','WD Red Pro 6TB HDD',
        'Barracuda 8TB HDD','Barracuda 4TB HDD','FireCuda 530 2TB NVMe',
        'FireCuda 530 1TB NVMe','IronWolf Pro 20TB NAS HDD',
        'MX500 2TB SATA SSD','MX500 1TB SATA SSD',
        'P5 Plus 2TB NVMe','P3 Plus 4TB NVMe',
        'Rocket 4 Plus 2TB NVMe','Rocket 4 Plus-G 4TB NVMe',
        'GameDrive for PS5 2TB','ADATA XPG Gammix S70 2TB'
    ];
    storage_brands BIGINT[] := ARRAY[
        10,10,10,10,10,10,10,
        11,11,11,11,11,
        12,12,12,12,12,
        13,13,13,13,
        10,10,10,
        13
    ];
    storage_prices NUMERIC[] := ARRAY[
        179,119,159,99,299,179,399,
        179,119,79,89,199,
        149,89,299,179,599,
        149,89,159,229,
        229,449,99,
        199
    ];

    -- ---------------------------------------------------------------
    -- Cases (category 12)
    -- ---------------------------------------------------------------
    case_names TEXT[] := ARRAY[
        'Meshify 2 XL','Meshify 2 Compact','Define 7 XL','Define 7 Compact',
        'North','Torrent','Pop Air','Pop Mini Air',
        'O11 Dynamic EVO XL','O11 Dynamic EVO','O11 Air Mini',
        'PC-O11D EVO RGB','Dynamic Mini',
        'H9 Flow','H7 Flow','H6 Flow',
        'Core P8','Core P6 TG Snow',
        'View 71 TG RGB','View 51 TG ARGB',
        'Lancool III','Lancool 216'
    ];
    case_brands BIGINT[] := ARRAY[
        16,16,16,16,16,16,16,16,
        17,17,17,17,17,
        32,32,32,
        32,32,
        32,32,
        17,17
    ];
    case_prices NUMERIC[] := ARRAY[
        189,129,199,149,149,169,89,79,
        249,179,129,219,149,
        169,139,119,
        249,199,
        169,139,
        149,119
    ];

    -- ---------------------------------------------------------------
    -- PSUs (category 13)
    -- ---------------------------------------------------------------
    psu_names TEXT[] := ARRAY[
        'HX1200i 1200W Platinum','HX1000i 1000W Platinum',
        'RM1000x 1000W Gold','RM850x 850W Gold','RM750x 750W Gold',
        'CX650M 650W Bronze',
        'Straight Power 12 1000W Platinum','Straight Power 11 850W Gold',
        'Dark Power Pro 13 1300W Titanium','Pure Power 11 FM 850W Gold',
        'Seasonic Focus GX-1000 1000W Gold','Seasonic Focus GX-850 850W Gold',
        'Seasonic Prime TX-1000 1000W Titanium',
        'EVGA SuperNOVA 1000 G6 1000W Gold','EVGA SuperNOVA 850 G6 850W Gold',
        'Thermaltake Toughpower GF3 1200W Gold',
        'Cooler Master V850 SFX Gold 850W','Cooler Master V1000 Gold 1000W',
        'XPG Core Reactor II 850W Gold','Deepcool PQ850M 850W Gold'
    ];
    psu_brands BIGINT[] := ARRAY[
        7,7,7,7,7,7,
        15,15,15,15,
        10,10,10,
        33,33,
        32,
        5,5,
        2,25
    ];
    psu_prices NUMERIC[] := ARRAY[
        279,229,179,149,129,89,
        199,169,349,139,
        179,149,269,
        169,139,
        219,
        149,169,
        139,109
    ];

    -- ---------------------------------------------------------------
    -- CPU Coolers (category 14)
    -- ---------------------------------------------------------------
    cooler_names TEXT[] := ARRAY[
        'NH-D15 chromax.black','NH-D15S chromax.black',
        'NH-U12A chromax.black','NH-L9x65 chromax.black',
        'Dark Rock Pro 5','Dark Rock 4','Shadow Rock 3',
        'Pure Rock 2 Black','Dark Rock TF 2',
        'Hyper 212 EVO V2','Hyper 622 Halo Black',
        'MasterAir MA824 Stealth','MasterLiquid 360 Atmos',
        'iCUE H150i Elite Capellix','iCUE H115i Elite Capellix',
        'Kraken X73 360mm','Kraken Z63 280mm',
        'Arctic Liquid Freezer III 360','Arctic Freezer 36',
        'Thermalright Peerless Assassin 120 SE'
    ];
    cooler_brands BIGINT[] := ARRAY[
        14,14,14,14,
        15,15,15,15,15,
        5,5,5,5,
        7,7,
        19,19,
        32,32,
        32
    ];
    cooler_prices NUMERIC[] := ARRAY[
        109,99,89,59,
        89,69,49,39,79,
        39,59,79,149,
        169,149,
        129,109,
        119,59,
        49
    ];

    -- ---------------------------------------------------------------
    -- Notebooks (category 3)
    -- ---------------------------------------------------------------
    nb_names TEXT[] := ARRAY[
        'ROG Zephyrus G14 (2024) Ryzen 9 7940HS RTX 4060',
        'ROG Zephyrus G16 (2024) Core Ultra 9 RTX 4090',
        'ROG Strix SCAR 18 (2024) Core i9-14900HX RTX 4090',
        'TUF Gaming A15 Ryzen 7 7735HS RTX 4060',
        'Vivobook Pro 16X OLED Core i9 RTX 4070',
        'ThinkPad X1 Carbon Gen 12 Core Ultra 7',
        'ThinkPad X1 Extreme Gen 6 Core i9 RTX 4070',
        'Legion Pro 7i Gen 9 Core i9-14900HX RTX 4090',
        'Legion 5 Pro Gen 8 Ryzen 7 7745HX RTX 4070',
        'IdeaPad Gaming 3 Ryzen 5 7535HS RTX 3050',
        'XPS 15 9530 Core i9-13900H RTX 4070',
        'XPS 13 9340 Core Ultra 7',
        'Alienware m18 R2 Core i9-14900HX RTX 4090',
        'Inspiron 16 Plus Core i7-13700H',
        'Spectre x360 14 Core Ultra 7 OLED',
        'OMEN 17 Core i9-14900HX RTX 4080',
        'EliteBook 840 G10 Core i7-1355U',
        'Predator Helios 18 Core i9-14900HX RTX 4090',
        'Nitro 5 Ryzen 5 7535HS RTX 4050',
        'Swift X 16 Ryzen 7 7840U RTX 4050',
        'Razer Blade 18 Core i9-14900HX RTX 4090',
        'Razer Blade 15 Core i7-13800H RTX 4070 Ti',
        'MSI Titan GT77 HX Core i9 RTX 4090',
        'MSI Stealth 16 Studio Core i9 RTX 4080',
        'Gigabyte AORUS 17X Ryzen 9 7945HX RTX 4080'
    ];
    nb_brands BIGINT[] := ARRAY[
        4,4,4,4,4,
        21,21,21,21,21,
        22,22,22,22,
        23,23,23,
        24,24,24,
        19,19,
        5,5,
        6
    ];
    nb_prices NUMERIC[] := ARRAY[
        1499,2199,2999,1099,1799,
        1799,2399,2999,1499,799,
        1899,1299,3499,999,
        1699,2099,1299,
        2999,999,1199,
        3499,2499,
        3299,2199,
        2299
    ];

    -- ---------------------------------------------------------------
    -- Keyboards (category 15)
    -- ---------------------------------------------------------------
    kb_names TEXT[] := ARRAY[
        'G915 TKL Lightspeed Wireless','G915 Lightspeed Wireless',
        'G513 Carbon GX Red','G413 TKL SE',
        'BlackWidow V4 Pro','BlackWidow V4 75%','Huntsman V3 Pro TKL',
        'Huntsman Mini 60%','DeathStalker V2 Pro TKL',
        'Apex Pro TKL Wireless','Apex 9 Mini','Apex 3 TKL',
        'ROG Strix Scope II 96 Wireless','ROG Falchion Ace',
        'Corsair K100 RGB','Corsair K70 RGB Pro',
        'Ducky One 3 TKL SF Cherry MX Red',
        'Keychron Q3 Pro Knob QMK',
        'Keychron K2 V2 Hot-Swap',
        'Anne Pro 2 Gateron Blue',
        'Varmilo VA87M Cherry MX Silent Red',
        'Leopold FC750R PD Cherry MX Brown'
    ];
    kb_brands BIGINT[] := ARRAY[
        18,18,18,18,
        19,19,19,19,19,
        20,20,20,
        4,4,
        7,7,
        6,6,6,6,6,6
    ];
    kb_prices NUMERIC[] := ARRAY[
        229,249,139,59,
        229,179,199,129,199,
        209,129,59,
        179,149,
        199,149,
        149,159,89,79,169,139
    ];

    -- ---------------------------------------------------------------
    -- Mice (category 16)
    -- ---------------------------------------------------------------
    mouse_names TEXT[] := ARRAY[
        'G Pro X Superlight 2','G Pro X Superlight','G502 X Plus Wireless',
        'G502 X Lightspeed','MX Master 3S',
        'DeathAdder V3 Pro','Viper V2 Pro','Basilisk V3 Pro',
        'Naga V2 Pro','Cobra Pro',
        'Aerox 5 Wireless','Rival 650 Wireless','Prime Wireless',
        'ROG Harpe Ace Aim Lab Edition',
        'Pulsar X2H Wireless','Finalmouse Starlight-12 Pegasus',
        'Endgame Gear XM2w Wireless',
        'Zowie EC2-C','Zowie FK1+-C',
        'HyperX Pulsefire Haste 2 Wireless'
    ];
    mouse_brands BIGINT[] := ARRAY[
        18,18,18,18,18,
        19,19,19,19,19,
        20,20,20,
        4,
        19,19,18,
        19,19,
        8
    ];
    mouse_prices NUMERIC[] := ARRAY[
        159,129,149,129,99,
        159,149,169,149,139,
        139,119,109,
        99,
        99,189,79,
        59,59,
        79
    ];

    -- ---------------------------------------------------------------
    -- Monitors (category 20)
    -- ---------------------------------------------------------------
    monitor_names TEXT[] := ARRAY[
        'ASUS ROG Swift PG27AQN 27" IPS 360Hz 1440p',
        'ASUS ROG Swift Pro PG248QP 24.1" TN 540Hz 1080p',
        'LG 27GP950-B 27" Nano IPS 144Hz 4K',
        'LG 32GQ950-B 32" Nano IPS 144Hz 4K',
        'LG UltraGear 27GR95QE 27" OLED 240Hz 1440p',
        'Samsung Odyssey G9 DQHD 49" VA 240Hz',
        'Samsung Odyssey OLED G8 34" QD-OLED 175Hz',
        'MSI MAG 274QRF QD 27" QD-IPS 165Hz 1440p',
        'MSI MPG 321URX QD-OLED 32" 240Hz 4K',
        'BenQ MOBIUZ EX2710Q 27" IPS 165Hz 1440p',
        'BenQ PD3220U 32" IPS 4K UHD',
        'Dell AW3423DWF 34" QD-OLED 165Hz',
        'Dell UltraSharp U2723QE 27" IPS 4K',
        'Acer Predator X28 28" IPS 155Hz 4K',
        'Acer Nitro XV272U V3 27" IPS 180Hz 1440p',
        'Corsair XENEON FLEX 45WQHD240 45" Bendable OLED'
    ];
    monitor_brands BIGINT[] := ARRAY[
        4,4,
        25,25,25,
        10,10,
        5,5,
        26,26,
        22,22,
        24,24,
        7
    ];
    monitor_prices NUMERIC[] := ARRAY[
        899,699,
        699,799,999,
        1199,999,
        349,799,
        329,699,
        999,599,
        699,329,
        1799
    ];

    -- ---------------------------------------------------------------
    -- Headsets (category 17)
    -- ---------------------------------------------------------------
    headset_names TEXT[] := ARRAY[
        'G Pro X 2 Lightspeed Wireless','G335 Wired','G733 Lightspeed Wireless',
        'BlackShark V2 Pro (2023)','BlackShark V2 X','Kraken V3 Pro Wireless',
        'Arctis Nova Pro Wireless','Arctis Nova 7','Arctis 1 Wireless',
        'ROG Delta S Wireless','ROG Strix Go 2.4',
        'Corsair HS80 Max Wireless','Corsair HS65 Wireless',
        'HyperX Cloud III Wireless','HyperX Cloud Alpha Wireless',
        'Astro A50 X Wireless','Astro A40 TR',
        'Turtle Beach Stealth Pro','Turtle Beach Recon 70',
        'EPOS H6Pro Closed Acoustic'
    ];
    headset_brands BIGINT[] := ARRAY[
        18,18,18,
        19,19,19,
        20,20,20,
        4,4,
        7,7,
        8,8,
        18,18,
        19,19,
        20
    ];
    headset_prices NUMERIC[] := ARRAY[
        249,49,129,
        199,59,179,
        349,149,79,
        229,149,
        169,99,
        179,129,
        349,149,
        299,49,
        169
    ];

    -- ---------------------------------------------------------------
    -- Webcams & Streaming (category 18)
    -- ---------------------------------------------------------------
    webcam_names TEXT[] := ARRAY[
        'Logitech Brio 4K Pro','Logitech C922 Pro Stream','Logitech StreamCam',
        'Logitech MX Brio 705','Razer Kiyo Pro Ultra',
        'Elgato Facecam Pro','Elgato HD60 X Capture Card',
        'AVerMedia Live Gamer Portable 2 Plus',
        'NZXT Signal HD60 Capture Card',
        'Insta360 Link 4K PTZ Webcam'
    ];
    webcam_brands BIGINT[] := ARRAY[18,18,18,18,19,19,19,26,26,18];
    webcam_prices NUMERIC[] := ARRAY[199,99,149,249,299,299,149,149,129,299];

    -- ---------------------------------------------------------------
    -- Controllers (category 19)
    -- ---------------------------------------------------------------
    ctrl_names TEXT[] := ARRAY[
        'Xbox Wireless Controller Carbon Black',
        'Xbox Elite Wireless Controller Series 2',
        'Xbox Wireless Controller Robot White',
        'DualSense Edge Wireless',
        'DualSense Wireless Controller White',
        'DualShock 4 Wireless',
        'Nintendo Switch Pro Controller',
        'Razer Wolverine V2 Chroma','Razer Kishi V2 Pro',
        'PowerA Enhanced Wireless',
        '8BitDo Pro 2 Wired','8BitDo Ultimate Wireless',
        'Thrustmaster T300RS GT Edition',
        'Logitech G923 Racing Wheel'
    ];
    ctrl_brands BIGINT[] := ARRAY[
        29,29,29,
        10,10,10,
        29,
        19,19,
        18,
        20,20,
        18,18
    ];
    ctrl_prices NUMERIC[] := ARRAY[
        59,179,59,
        199,69,49,
        69,
        149,99,
        49,
        49,79,
        399,399
    ];

    -- ---------------------------------------------------------------
    -- Games (category 5)
    -- ---------------------------------------------------------------
    game_names TEXT[] := ARRAY[
        'The Witcher 3: Wild Hunt – Complete Edition',
        'Cyberpunk 2077 – Ultimate Edition',
        'Elden Ring',
        'Red Dead Redemption 2',
        'Grand Theft Auto V – Premium Edition',
        'FIFA 25',
        'EA Sports FC 25',
        'Call of Duty: Modern Warfare III',
        'Battlefield 2042',
        'Starfield',
        'Hogwarts Legacy',
        'Baldur''s Gate 3',
        'Diablo IV',
        'World of Warcraft: The War Within',
        'Overwatch 2 – Watchpoint Pack',
        'Counter-Strike 2 – Prime Status',
        'Valorant – Ignition Series Bundle',
        'Apex Legends – Champion Edition',
        'Forza Horizon 5 – Premium Edition',
        'Microsoft Flight Simulator 2024',
        'Halo Infinite – Campaign',
        'Sea of Thieves – Premium Edition',
        'Alan Wake 2',
        'Lies of P',
        'Armored Core VI: Fires of Rubicon',
        'Final Fantasy XVI',
        'Spider-Man 2',
        'God of War Ragnarök – PC Edition',
        'Horizon Forbidden West – PC',
        'The Last of Us Part I – PC',
        'Resident Evil 4 Remake',
        'Street Fighter 6',
        'Tekken 8',
        'Mortal Kombat 1',
        'WWE 2K24',
        'NBA 2K25',
        'NHL 25',
        'F1 24',
        'WRC Generations',
        'Assetto Corsa Competizione',
        'iRacing – Season Pack',
        'Euro Truck Simulator 2 – Road to the Black Sea',
        'American Truck Simulator – Montana',
        'Cities: Skylines II',
        'Planet Coaster 2',
        'Two Point Campus',
        'Civilization VII',
        'Age of Empires IV – Anniversary Edition',
        'Total War: Warhammer III',
        'Crusader Kings III – Royal Court',
        'Hearts of Iron IV – By Blood Alone',
        'Stellaris – Overlord',
        'Paradox Insider Bundle 2024',
        'Deep Rock Galactic',
        'Valheim',
        'Satisfactory',
        'Factorio',
        'Vampire Survivors',
        'Stardew Valley',
        'Terraria',
        'Hollow Knight',
        'Hades II',
        'Dead Cells',
        'Returnal – PC',
        'Death Stranding 2 – PC',
        'Ghostrunner 2',
        'Control – Ultimate Edition',
        'Plague Tale: Requiem',
        'Disco Elysium – The Final Cut',
        'Pathfinder: Wrath of the Righteous',
        'Divinity: Original Sin 2 – Definitive Edition',
        'Pillars of Eternity II: Deadfire',
        'Torment: Tides of Numenera',
        'Wasteland 3',
        'Gears 5 – Game of the Year',
        'Halo: The Master Chief Collection',
        'Forza Motorsport (2023)',
        'Minecraft – Java & Bedrock Edition',
        'Roblox – Gift Card 4500 Robux',
        'Among Us',
        'Fall Guys – Season Pass',
        'Rocket League – Fan Pack',
        'Destiny 2 – The Final Shape',
        'Bungie 30th Anniversary Pack',
        'Warframe – Starter Pack',
        'Path of Exile – Supporter Pack',
        'League of Legends – Gift Card 10 EUR',
        'Dota 2 – Collector''s Cache',
        'Team Fortress 2 – Unusual Effect Bundle',
        'PUBG: Battlegrounds – G-Coin Pack',
        'Escape from Tarkov – Edge of Darkness',
        'DayZ – Survivor Bundle',
        'Arma 3 – Creator DLC Bundle',
        'Squad – Founders Edition',
        'Hell Let Loose',
        'Post Scriptum',
        'Insurgency: Sandstorm',
        'Ready or Not',
        'Rainbow Six Siege – Ultimate Edition',
        'Ghost Recon Breakpoint',
        'Watch Dogs: Legion',
        'Assassin''s Creed Mirage',
        'Assassin''s Creed Valhalla – Complete Edition',
        'Far Cry 6',
        'Far Cry 7 – Dawn of the Union',
        'Anno 1800 – Complete Edition',
        'The Division 2 – Warlords of New York',
        'Just Dance 2024 Edition',
        'Skull and Bones',
        'Avatar: Frontiers of Pandora',
        'Prince of Persia: The Lost Crown',
        'Immortals: Fenyx Rising',
        'The Crew Motorfest',
        'Riders Republic',
        'Steep – Road to the Olympics',
        'Trials Rising – Gold Edition',
        'South Park: The Fractured But Whole',
        'Scott Pilgrim vs. the World: The Game',
        'Unravel Two',
        'It Takes Two',
        'A Way Out',
        'Sims 4 – Deluxe Party Edition',
        'Need for Speed Unbound',
        'Burnout Paradise Remastered',
        'Plants vs. Zombies: Battle for Neighborville',
        'Fe',
        'Jedi: Survivor',
        'Jedi: Fallen Order',
        'Mass Effect Legendary Edition',
        'Dragon Age: The Veilguard',
        'Dragon Age: Inquisition – GOTY',
        'Anthem',
        'Titanfall 2',
        'Battlefield V',
        'Battlefield 1 – Revolution',
        'Battlefield 4 – Premium Edition',
        'Medal of Honor: Warfighter',
        'Dead Space (2023 Remake)',
        'Dead Space 3',
        'Syndicate (2012)',
        'Mirror''s Edge Catalyst',
        'Crysis Remastered Trilogy',
        'SimCity (2013)',
        'Spore',
        'Command & Conquer Remastered Collection',
        'Populous 3 PC Classic',
        'Theme Hospital GOG',
        'Dungeon Keeper Gold GOG',
        'Clive Barker''s Undying GOG',
        'Ultima Underworld 1+2 GOG',
        'Wing Commander IV GOG',
        'Syndicate Plus GOG',
        'Desert Strike GOG',
        'Theme Park World GOG',
        'Black & White 2 GOG',
        'Fable – The Lost Chapters GOG',
        'MechAssault 2 GOG',
        'System Shock (2023 Remake)',
        'Deus Ex: Mankind Divided',
        'Deus Ex: Human Revolution – Director''s Cut',
        'Thief (2014)',
        'Sleeping Dogs: Definitive Edition',
        'Just Cause 4 – Complete Edition',
        'Just Cause 3 – XXL Edition',
        'Mad Max',
        'Eidos Anthology Bundle',
        'Shadow of the Tomb Raider – Definitive Edition',
        'Rise of the Tomb Raider: 20 Year Celebration',
        'Tomb Raider (2013) GOTY',
        'Hitman – World of Assassination',
        'Hitman 2',
        'Hitman 3',
        'Mini Ninjas',
        'Kane & Lynch 2: Dog Days',
        'Freedom Fighters GOG',
        'Guardians of the Galaxy',
        'Marvel''s Avengers',
        'Midnight Suns',
        'Outriders – Worldslayer',
        'Remnant II – Ultimate Edition',
        'Remnant: From the Ashes',
        'Darksiders III',
        'Darksiders Genesis',
        'Darksiders Warmastered Edition',
        'Kingdoms of Amalur: Re-Reckoning',
        'Biomutant',
        'Chorus',
        'Everspace 2',
        'X4: Foundations – Kingdom End',
        'Elite Dangerous – Odyssey',
        'No Man''s Sky',
        'Space Engineers',
        'Kerbal Space Program 2',
        'Outer Wilds – Archaeologist Edition',
        'Subnautica',
        'Subnautica: Below Zero',
        'The Long Dark',
        'Green Hell',
        'The Forest',
        'Sons of the Forest',
        'Icarus',
        'Rust',
        'Ark: Survival Evolved – Ultimate Survivor',
        'Ark: Survival Ascended',
        'Conan Exiles',
        'Atlas',
        'Raft',
        'Stranded Deep',
        '7 Days to Die',
        'Project Zomboid',
        'RimWorld – Biotech',
        'Dwarf Fortress',
        'Oxygen Not Included',
        'Surviving Mars – Space Race Plus',
        'Terra Nil',
        'Dave the Diver',
        'Dredge',
        'Spiritfarer',
        'Unpacking',
        'A Short Hike',
        'Celeste',
        'Ori and the Will of the Wisps',
        'Cuphead',
        'Shovel Knight Treasure Trove',
        'Broforce',
        'Spelunky 2',
        'Noita',
        'Slay the Spire',
        'Monster Train',
        'Inscryption',
        'Loop Hero',
        'Into the Breach',
        'FTL: Faster Than Light Advanced Edition',
        'Darkest Dungeon II',
        'Darkest Dungeon',
        'XCOM 2 – War of the Chosen',
        'XCOM: Enemy Unknown – Plus',
        'Phoenix Point – Year One Edition',
        'Jagged Alliance 3',
        'Expeditions: Rome',
        'Solasta: Crown of the Magister',
        'Neverwinter Nights: Enhanced Edition',
        'Planescape: Torment Enhanced Edition',
        'Icewind Dale: Enhanced Edition',
        'Baldur''s Gate: Enhanced Edition',
        'Tyranny – Gold Edition',
        'Torchlight Infinite Starter Pack',
        'Grim Dawn – Forgotten Gods',
        'Victor Vran ARPG',
        'Sacred 2 Gold GOG',
        'Titan Quest Anniversary Edition',
        'Diablo II: Resurrected',
        'Diablo III: Eternal Collection',
        'Warhammer 40K: Inquisitor – Martyr',
        'Solium Infernum',
        'Weird West',
        'Pentiment',
        'Grounded',
        'Obsidian Double Pack',
        'The Outer Worlds – Spacer''s Choice Edition',
        'Fallout 4 – GOTY',
        'Fallout 76 – Steel Dawn',
        'Skyrim Anniversary Edition',
        'Oblivion Remastered',
        'Morrowind – GOTY GOG',
        'Starbound',
        'Terraria Collector''s Edition',
        'Re-Logic Bundle',
        'Sonic Superstars',
        'Sonic Frontiers',
        'Sonic Origins Plus',
        'SEGA Mega Drive Classics',
        'SEGA Genesis Mini Library',
        'Bayonetta 3 PC',
        'Astral Chain PC',
        'Metroid Dread PC',
        'Xenoblade Chronicles 3 PC',
        'Fire Emblem Engage PC',
        'Kirby and the Forgotten Land PC',
        'Super Mario Odyssey PC Port',
        'The Legend of Zelda: Tears of the Kingdom PC',
        'Donkey Kong Country Returns HD PC',
        'Pikmin 4 PC',
        'WarioWare: Move It! PC',
        'Advance Wars 1+2 Re-Boot Camp PC',
        'Metroid Prime Remastered PC',
        'Pikmin 1+2 HD PC',
        'Star Fox Zero HD PC',
        'Paper Mario: The Origami King PC',
        'Luigi''s Mansion 3 PC',
        'Super Mario 3D World + Bowser''s Fury PC',
        'New Super Mario Bros. U Deluxe PC',
        'Mario + Rabbids Sparks of Hope PC',
        'Rayman Legends Definitive Edition',
        'Crash Bandicoot N-Sane Trilogy',
        'Crash Bandicoot 4: It''s About Time',
        'Spyro Reignited Trilogy',
        'Crash Team Rumble – Deluxe',
        'Tony Hawk''s Pro Skater 1+2',
        'Guitar Hero Live Upgrade Pack',
        'Ghostbusters: The Video Game Remastered',
        'Ghostbusters: Spirits Unleashed',
        'Prey (2017)',
        'Arkane Anthology',
        'Dishonored 2',
        'Dishonored – Death of the Outsider',
        'Deathloop',
        'Redfall',
        'Quake remaster'
    ];
    game_brands BIGINT[] := ARRAY[
        30,30,2,27,27,27,27,29,27,29,29,30,29,29,29,31,29,27,29,29,
        29,29,30,30,2,10,10,10,10,10,
        27,27,27,27,27,29,27,27,28,7,
        31,28,28,6,6,29,29,5,28,29,
        29,28,28,28,31,31,
        18,18,18,18,31,31,31,30,31,31,29,29,29,
        29,29,31,31,31,31,31,6,6,6,
        27,28,28,28,28,28,28,28,28,27,27,27,
        27,27,27,27,27,27,27,27,27,27,
        22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,
        27,27,27,27,27,27,27,27,27,27,
        29,29,29,29,29,29,29,29,29,
        10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,
        28,28,28,28,28,28,28,28,28,
        29,29,29,29,29,
        27,27,27,27,27,27,27,29,
        31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,
        29,29,29,29,29,29,
        30,30,30,30,30,
        2,27,30,2,2,
        10,31,31,
        29,29,29,29,29,
        29,29,29,29,29,29,29,29,29,29,
        29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,29,
        29,29,29,29,29,29,29,29,29
    ];
    game_prices NUMERIC[] := ARRAY[
        39,59,59,59,29,59,59,69,39,69,59,59,69,49,39,14,39,39,59,79,
        39,49,59,49,59,69,69,59,59,49,
        49,39,49,59,59,79,59,39,49,79,
        39,29,29,59,59,39,49,49,39,29,
        29,29,19,29,19,29,
        29,29,29,29,29,19,29,
        19,9,29,29,39,39,
        29,29,29,29,29,
        29,69,69,59,
        59,59,59,59,39,39,
        29,59,49,29,19,49,
        39,19,29,39,49,59,59,49,49,59,
        49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,
        49,29,29,29,39,39,39,39,49,49,
        39,49,29,49,39,29,39,49,29,
        59,59,59,59,59,59,59,59,59,59,59,59,59,59,59,59,59,59,59,59,
        49,49,49,49,49,49,49,49,49,
        39,49,29,59,69,
        59,49,39,29,29,
        29,29,29,
        49,39,29,29,29,
        39,39,39,29,29,29,29,29,29,29,
        49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,
        39,39,39,39,39,39,39,39,39
    ];

    -- ---------------------------------------------------------------
    -- Software (category 6)
    -- ---------------------------------------------------------------
    sw_names TEXT[] := ARRAY[
        'Windows 11 Pro','Windows 11 Home','Windows 11 Pro for Workstations',
        'Microsoft 365 Personal 1 Year','Microsoft 365 Family 1 Year',
        'Microsoft Office 2021 Home & Student',
        'Microsoft Office 2021 Home & Business',
        'Microsoft Office 2021 Professional Plus',
        'Microsoft Visio Professional 2021',
        'Microsoft Project Professional 2021',
        'Norton 360 Deluxe 5 Devices','Norton 360 Premium 10 Devices',
        'Bitdefender Total Security 5 Devices',
        'Kaspersky Premium 5 Devices',
        'ESET Internet Security 3 Devices',
        'Malwarebytes Premium 3 Devices',
        'Adobe Creative Cloud All Apps 1 Year',
        'Adobe Photoshop 1 Year','Adobe Illustrator 1 Year',
        'Adobe Premiere Pro 1 Year','Adobe After Effects 1 Year',
        'Adobe Acrobat Pro DC 1 Year',
        'Corel Draw Graphics Suite 2024',
        'Affinity Designer 2 Perpetual',
        'Affinity Photo 2 Perpetual',
        'Affinity Publisher 2 Perpetual',
        'DaVinci Resolve Studio 18 Perpetual',
        'Vegas Pro 21 Edit',
        'Cyberlink PowerDirector 365 1 Year',
        'Movavi Video Suite 2024',
        'WinRAR 5 Lifetime',
        '7-Zip – Donation Pack',
        'WinZip 28 Standard',
        'Acronis True Image 2024 Essentials',
        'Macrium Reflect 8 Home',
        'EaseUS Todo Backup Home',
        'AOMEI Backupper Professional',
        'Daemon Tools Ultra',
        'VMware Workstation Pro 17',
        'Parallels Desktop 19 for Mac',
        'VirtualBox Extension Pack (Donation)',
        'CCleaner Professional Plus',
        'IObit Advanced SystemCare 16 Pro',
        'Glary Utilities 6 Pro',
        'Ashampoo WinOptimizer 2024',
        'CPU-Z Pro License',
        'HWiNFO64 Pro',
        'GPU-Z Donor Edition',
        'MSI Afterburner Donation',
        'NZXT CAM Pro',
        'Corsair iCUE Full License',
        'ASUS Armoury Crate Pro',
        'Logitech G HUB Pro',
        'Razer Synapse 3 Pro',
        'SteelSeries GG Pro',
        'Discord Nitro 1 Year',
        'Spotify Premium 1 Year',
        'Steam Wallet Card 50 EUR',
        'GOG.COM Gift Card 50 EUR',
        'Epic Games Store Gift Card 50 EUR'
    ];
    sw_brands BIGINT[] := ARRAY[
        29,29,29,29,29,29,29,29,29,29,
        19,19,13,2,6,13,
        18,18,18,18,18,18,
        19,19,19,19,
        30,19,19,19,
        22,22,22,22,22,22,22,22,
        23,23,22,
        18,18,18,18,18,18,18,18,18,18,18,18,18,18,
        31,31,31,26,29
    ];
    sw_prices NUMERIC[] := ARRAY[
        199,139,309,69,99,149,249,439,529,1059,
        39,49,39,49,35,39,
        599,239,239,239,239,179,
        449,169,169,169,
        295,499,69,49,
        29,9,29,59,49,39,39,39,
        219,99,0,
        29,29,29,39,9,9,9,9,29,
        39,29,19,19,19,
        99,149,50,50,50
    ];

    -- ---------------------------------------------------------------
    -- Bundles / Pre-builts (category 2, brand varies)
    -- ---------------------------------------------------------------
    bundle_names TEXT[] := ARRAY[
        'iBUYPOWER SlateMR 274a Gaming PC RTX 4060 Ti',
        'iBUYPOWER TraceMR 5 240a Gaming PC RTX 4070',
        'CyberPowerPC Gamer Xtreme VR Gaming PC RTX 4060',
        'CyberPowerPC Gamer Supreme Liquid Cool RTX 4080',
        'HP OMEN 45L Desktop GT22-0014ng RTX 4080',
        'HP Victus Gaming Desktop 15L RTX 4060',
        'Dell G5 Gaming Desktop with RTX 4070',
        'Dell Alienware Aurora R16 RTX 4090',
        'ASUS ROG Strix G35 Gaming Desktop RTX 4090',
        'MSI Aegis RS 13NUF-421DE RTX 4080',
        'Lenovo Legion Tower 7i Gen 8 RTX 4090',
        'Lenovo IdeaCentre Gaming 5i RTX 4060',
        'Acer Predator Orion 7000 RTX 4090',
        'Acer Nitro 50 Gaming Desktop RTX 4060 Ti',
        'Mini PC: Beelink GTR7 Pro Ryzen 9 7940HX',
        'Mini PC: Intel NUC 13 Pro Core i7-1360P',
        'Intel NUC 14 Performance Kit Core i7-155H'
    ];
    bundle_brands BIGINT[] := ARRAY[
        29,29,
        24,24,
        23,23,
        22,22,
        4,5,
        21,21,
        24,24,
        21,1,1
    ];
    bundle_prices NUMERIC[] := ARRAY[
        1299,1799,999,2499,1899,1099,1599,3499,3999,2999,3999,1299,3999,1399,
        799,649,799
    ];

BEGIN
    -- ---------------------------------------------------------------
    -- Insert CPUs
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := cpu_names[1 + ((v_i - 1) % array_length(cpu_names, 1))];
        v_brand_id   := cpu_brands[1 + ((v_i - 1) % array_length(cpu_brands, 1))];
        v_price      := cpu_prices[1 + ((v_i - 1) % array_length(cpu_prices, 1))];
        v_sku        := 'CPU-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 7, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-A-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert GPUs
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := gpu_names[1 + ((v_i - 1) % array_length(gpu_names, 1))];
        v_brand_id   := gpu_brands[1 + ((v_i - 1) % array_length(gpu_brands, 1))];
        v_price      := gpu_prices[1 + ((v_i - 1) % array_length(gpu_prices, 1))];
        v_sku        := 'GPU-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 8, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-B-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Motherboards
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := mb_names[1 + ((v_i - 1) % array_length(mb_names, 1))];
        v_brand_id   := mb_brands[1 + ((v_i - 1) % array_length(mb_brands, 1))];
        v_price      := mb_prices[1 + ((v_i - 1) % array_length(mb_prices, 1))];
        v_sku        := 'MBD-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 9, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-C-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert RAM
    -- ---------------------------------------------------------------
    FOR v_i IN 1..100 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := ram_names[1 + ((v_i - 1) % array_length(ram_names, 1))];
        v_brand_id   := ram_brands[1 + ((v_i - 1) % array_length(ram_brands, 1))];
        v_price      := ram_prices[1 + ((v_i - 1) % array_length(ram_prices, 1))];
        v_sku        := 'RAM-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 10, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-D-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Storage
    -- ---------------------------------------------------------------
    FOR v_i IN 1..120 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := storage_names[1 + ((v_i - 1) % array_length(storage_names, 1))];
        v_brand_id   := storage_brands[1 + ((v_i - 1) % array_length(storage_brands, 1))];
        v_price      := storage_prices[1 + ((v_i - 1) % array_length(storage_prices, 1))];
        v_sku        := 'STR-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 11, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-E-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Cases
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := case_names[1 + ((v_i - 1) % array_length(case_names, 1))];
        v_brand_id   := case_brands[1 + ((v_i - 1) % array_length(case_brands, 1))];
        v_price      := case_prices[1 + ((v_i - 1) % array_length(case_prices, 1))];
        v_sku        := 'CSE-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 12, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-F-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert PSUs
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := psu_names[1 + ((v_i - 1) % array_length(psu_names, 1))];
        v_brand_id   := psu_brands[1 + ((v_i - 1) % array_length(psu_brands, 1))];
        v_price      := psu_prices[1 + ((v_i - 1) % array_length(psu_prices, 1))];
        v_sku        := 'PSU-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 13, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-G-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert CPU Coolers
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := cooler_names[1 + ((v_i - 1) % array_length(cooler_names, 1))];
        v_brand_id   := cooler_brands[1 + ((v_i - 1) % array_length(cooler_brands, 1))];
        v_price      := cooler_prices[1 + ((v_i - 1) % array_length(cooler_prices, 1))];
        v_sku        := 'CLR-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 14, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-H-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Notebooks
    -- ---------------------------------------------------------------
    FOR v_i IN 1..150 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := nb_names[1 + ((v_i - 1) % array_length(nb_names, 1))];
        v_brand_id   := nb_brands[1 + ((v_i - 1) % array_length(nb_brands, 1))];
        v_price      := nb_prices[1 + ((v_i - 1) % array_length(nb_prices, 1))];
        v_sku        := 'NTB-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 3, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-I-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Keyboards
    -- ---------------------------------------------------------------
    FOR v_i IN 1..120 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := kb_names[1 + ((v_i - 1) % array_length(kb_names, 1))];
        v_brand_id   := kb_brands[1 + ((v_i - 1) % array_length(kb_brands, 1))];
        v_price      := kb_prices[1 + ((v_i - 1) % array_length(kb_prices, 1))];
        v_sku        := 'KBD-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 15, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-J-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Mice
    -- ---------------------------------------------------------------
    FOR v_i IN 1..100 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := mouse_names[1 + ((v_i - 1) % array_length(mouse_names, 1))];
        v_brand_id   := mouse_brands[1 + ((v_i - 1) % array_length(mouse_brands, 1))];
        v_price      := mouse_prices[1 + ((v_i - 1) % array_length(mouse_prices, 1))];
        v_sku        := 'MSE-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 16, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-K-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Monitors
    -- ---------------------------------------------------------------
    FOR v_i IN 1..120 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := monitor_names[1 + ((v_i - 1) % array_length(monitor_names, 1))];
        v_brand_id   := monitor_brands[1 + ((v_i - 1) % array_length(monitor_brands, 1))];
        v_price      := monitor_prices[1 + ((v_i - 1) % array_length(monitor_prices, 1))];
        v_sku        := 'MON-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 20, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-L-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Headsets
    -- ---------------------------------------------------------------
    FOR v_i IN 1..100 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := headset_names[1 + ((v_i - 1) % array_length(headset_names, 1))];
        v_brand_id   := headset_brands[1 + ((v_i - 1) % array_length(headset_brands, 1))];
        v_price      := headset_prices[1 + ((v_i - 1) % array_length(headset_prices, 1))];
        v_sku        := 'HST-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 17, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-M-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Webcams & Streaming
    -- ---------------------------------------------------------------
    FOR v_i IN 1..60 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := webcam_names[1 + ((v_i - 1) % array_length(webcam_names, 1))];
        v_brand_id   := webcam_brands[1 + ((v_i - 1) % array_length(webcam_brands, 1))];
        v_price      := webcam_prices[1 + ((v_i - 1) % array_length(webcam_prices, 1))];
        v_sku        := 'WCM-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 18, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-N-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Controllers
    -- ---------------------------------------------------------------
    FOR v_i IN 1..80 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := ctrl_names[1 + ((v_i - 1) % array_length(ctrl_names, 1))];
        v_brand_id   := ctrl_brands[1 + ((v_i - 1) % array_length(ctrl_brands, 1))];
        v_price      := ctrl_prices[1 + ((v_i - 1) % array_length(ctrl_prices, 1))];
        v_sku        := 'CTR-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 19, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-O-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Games (2 000)
    -- ---------------------------------------------------------------
    FOR v_i IN 1..2000 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := game_names[1 + ((v_i - 1) % array_length(game_names, 1))];
        v_brand_id   := game_brands[1 + ((v_i - 1) % array_length(game_brands, 1))];
        v_price      := game_prices[1 + ((v_i - 1) % array_length(game_prices, 1))];
        v_sku        := 'GAM-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 5, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-P-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Software (300)
    -- ---------------------------------------------------------------
    FOR v_i IN 1..300 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := sw_names[1 + ((v_i - 1) % array_length(sw_names, 1))];
        v_brand_id   := sw_brands[1 + ((v_i - 1) % array_length(sw_brands, 1))];
        v_price      := sw_prices[1 + ((v_i - 1) % array_length(sw_prices, 1))];
        v_sku        := 'SFW-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 6, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-Q-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

    -- ---------------------------------------------------------------
    -- Insert Bundles / Pre-builts (170)
    -- ---------------------------------------------------------------
    FOR v_i IN 1..170 LOOP
        v_product_id := nextval('inv.sq_products_id');
        v_name       := bundle_names[1 + ((v_i - 1) % array_length(bundle_names, 1))];
        v_brand_id   := bundle_brands[1 + ((v_i - 1) % array_length(bundle_brands, 1))];
        v_price      := bundle_prices[1 + ((v_i - 1) % array_length(bundle_prices, 1))];
        v_sku        := 'BDL-' || lpad(v_product_id::TEXT, 5, '0');

        INSERT INTO inv.products (id, sku, name, category_id, brand_id, unit_price)
        VALUES (v_product_id, v_sku, v_name, 2, v_brand_id, v_price);

        INSERT INTO inv.stock (product_id, quantity, warehouse_location)
        VALUES (v_product_id, 500, 'RACK-R-' || lpad(v_product_id::TEXT, 4, '0'));

        INSERT INTO inv.price_history (product_id, price, valid_from)
        VALUES (v_product_id, v_price, NOW() - interval '180 days');
    END LOOP;

END;
$$;
