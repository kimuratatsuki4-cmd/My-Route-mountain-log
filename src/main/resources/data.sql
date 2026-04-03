INSERT IGNORE INTO roles (role_id, name) VALUES (1, 'ROLE_GENERAL');

INSERT IGNORE INTO roles (role_id, name) VALUES (2, 'ROLE_PREMIUM');

INSERT IGNORE INTO roles (role_id, name) VALUES (3, 'ROLE_ADMIN');

INSERT IGNORE INTO
    users (
        username,
        email,
        password,
        address,
        birth_date,
        experience_level,
        role_id,
        enabled,
        stripe_id
    )
VALUES (
        '山田太郎',
        'yamada.tarou@gmail.com',
        '$2a$10$NerzKqjWlhSjEia.9QrpQ.9SuEq8mYLTYgIF7qsdbNP64kXkKVDLa', --password
        '東京都新宿区西新宿2-8-1',
        '1995-05-05',
        'INTERMEDIATE',
        1,
        true,
        null
    ),
    (
        '山田花子',
        'yamada.hanako@gmail.com',
        '$2a$10$NerzKqjWlhSjEia.9QrpQ.9SuEq8mYLTYgIF7qsdbNP64kXkKVDLa', --password
        '東京都新宿区西新宿2-8-1',
        '1995-07-19',
        'BEGINNER',
        2,
        true,
        null
    ),
    (
        'システム管理者',
        'admin@example.com',
        '$2a$10$NerzKqjWlhSjEia.9QrpQ.9SuEq8mYLTYgIF7qsdbNP64kXkKVDLa', --password
        '東京都新宿区',
        '1980-01-01',
        'ADVANCED',
        3,
        true,
        null
    );

-- =============================================
-- 日本百名山 初期データ
-- name: 山名, name_en: 英語名, name_kana: よみがな, elevation: 標高(m)
-- prefecture: 都道府県, latitude/longitude: 緯度/経度
-- typical_distance_km: 代表的なルートの距離(km)
-- typical_duration_minutes: 代表的な往復所要時間(分)
-- typical_elevation_gain: 代表的な累積標高(m)
-- difficulty: 難易度 (EASY/MODERATE/HARD/EXPERT)
-- is_hyakumeizan: 百名山フラグ
-- =============================================
INSERT IGNORE INTO
    mountains (
        name,
        name_en,
        name_kana,
        elevation,
        prefecture,
        latitude,
        longitude,
        typical_distance_km,
        typical_duration_minutes,
        typical_elevation_gain,
        difficulty,
        is_hyakumeizan
    )
VALUES (
        '利尻山',
        'Mt. Rishiri',
        'りしりざん',
        1721,
        '北海道',
        45.1786,
        141.2408,
        12.0,
        600,
        1520,
        'HARD',
        TRUE
    ),
    (
        '羅臼岳',
        'Mt. Rausu',
        'らうすだけ',
        1661,
        '北海道',
        44.0750,
        145.1264,
        14.0,
        540,
        1400,
        'HARD',
        TRUE
    ),
    (
        '斜里岳',
        'Mt. Shari',
        'しゃりだけ',
        1547,
        '北海道',
        43.7656,
        144.7189,
        10.0,
        420,
        1100,
        'MODERATE',
        TRUE
    ),
    (
        '阿寒岳',
        'Mt. Akan',
        'あかんだけ',
        1499,
        '北海道',
        43.3864,
        144.0131,
        11.0,
        360,
        900,
        'MODERATE',
        TRUE
    ),
    (
        '大雪山',
        'Mt. Taisetsu',
        'たいせつざん',
        2291,
        '北海道',
        43.6625,
        142.8542,
        12.0,
        420,
        700,
        'MODERATE',
        TRUE
    ),
    (
        'トムラウシ山',
        'Mt. Tomuraushi',
        'とむらうしやま',
        2141,
        '北海道',
        43.5264,
        142.8472,
        18.0,
        600,
        1500,
        'EXPERT',
        TRUE
    ),
    (
        '十勝岳',
        'Mt. Tokachi',
        'とかちだけ',
        2077,
        '北海道',
        43.4181,
        142.6864,
        10.0,
        420,
        1200,
        'MODERATE',
        TRUE
    ),
    (
        '幌尻岳',
        'Mt. Poroshiri',
        'ぽろしりだけ',
        2052,
        '北海道',
        42.7547,
        142.7539,
        22.0,
        900,
        1700,
        'EXPERT',
        TRUE
    ),
    (
        '後方羊蹄山',
        'Mt. Yotei',
        'しりべしやま',
        1898,
        '北海道',
        42.8264,
        140.8114,
        10.0,
        540,
        1600,
        'HARD',
        TRUE
    ),
    (
        '岩木山',
        'Mt. Iwaki',
        'いわきさん',
        1625,
        '青森県',
        40.6558,
        140.3031,
        8.0,
        300,
        800,
        'EASY',
        TRUE
    ),
    (
        '八甲田山',
        'Mt. Hakkoda',
        'はっこうださん',
        1585,
        '青森県',
        40.6597,
        140.8775,
        8.5,
        300,
        700,
        'EASY',
        TRUE
    ),
    (
        '八幡平',
        'Mt. Hachimantai',
        'はちまんたい',
        1613,
        '岩手県/秋田県',
        39.9578,
        140.8544,
        5.0,
        180,
        200,
        'EASY',
        TRUE
    ),
    (
        '岩手山',
        'Mt. Iwate',
        'いわてさん',
        2038,
        '岩手県',
        39.8511,
        141.0011,
        10.0,
        480,
        1400,
        'HARD',
        TRUE
    ),
    (
        '早池峰山',
        'Mt. Hayachine',
        'はやちねさん',
        1917,
        '岩手県',
        39.5608,
        141.4869,
        5.5,
        300,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '鳥海山',
        'Mt. Chokai',
        'ちょうかいさん',
        2236,
        '秋田県/山形県',
        39.0997,
        140.0478,
        15.0,
        540,
        1300,
        'HARD',
        TRUE
    ),
    (
        '月山',
        'Mt. Gassan',
        'がっさん',
        1984,
        '山形県',
        38.5481,
        140.0278,
        8.0,
        300,
        600,
        'EASY',
        TRUE
    ),
    (
        '朝日岳',
        'Mt. Asahi',
        'あさひだけ',
        1871,
        '山形県/新潟県',
        38.2558,
        139.8711,
        20.0,
        720,
        1500,
        'HARD',
        TRUE
    ),
    (
        '蔵王山',
        'Mt. Zao',
        'ざおうさん',
        1841,
        '山形県/宮城県',
        38.1444,
        140.4406,
        6.0,
        240,
        500,
        'EASY',
        TRUE
    ),
    (
        '飯豊山',
        'Mt. Iide',
        'いいでさん',
        2105,
        '山形県/新潟県/福島県',
        37.8542,
        139.9694,
        22.0,
        840,
        1800,
        'EXPERT',
        TRUE
    ),
    (
        '吾妻山',
        'Mt. Azuma',
        'あづまやま',
        2035,
        '山形県/福島県',
        37.7353,
        140.2439,
        8.0,
        300,
        600,
        'EASY',
        TRUE
    ),
    (
        '安達太良山',
        'Mt. Adatara',
        'あだたらやま',
        1700,
        '福島県',
        37.6208,
        140.2886,
        9.0,
        300,
        700,
        'EASY',
        TRUE
    ),
    (
        '磐梯山',
        'Mt. Bandai',
        'ばんだいさん',
        1816,
        '福島県',
        37.6011,
        140.0714,
        8.0,
        300,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '会津駒ヶ岳',
        'Mt. Aizu-Komagatake',
        'あいづこまがたけ',
        2133,
        '福島県',
        36.9597,
        139.3361,
        14.0,
        480,
        1200,
        'MODERATE',
        TRUE
    ),
    (
        '那須岳',
        'Mt. Nasu',
        'なすだけ',
        1915,
        '栃木県',
        37.1253,
        139.9628,
        6.0,
        240,
        500,
        'EASY',
        TRUE
    ),
    (
        '越後駒ヶ岳',
        'Mt. Echigo-Komagatake',
        'えちごこまがたけ',
        2003,
        '新潟県',
        37.1322,
        139.1306,
        15.0,
        600,
        1200,
        'HARD',
        TRUE
    ),
    (
        '平ヶ岳',
        'Mt. Hiragadake',
        'ひらがたけ',
        2141,
        '新潟県/群馬県',
        36.9586,
        139.2211,
        22.0,
        720,
        1300,
        'HARD',
        TRUE
    ),
    (
        '巻機山',
        'Mt. Makihata',
        'まきはたやま',
        1967,
        '新潟県/群馬県',
        36.9350,
        139.0633,
        12.0,
        480,
        1200,
        'MODERATE',
        TRUE
    ),
    (
        '燧ヶ岳',
        'Mt. Hiuchi',
        'ひうちがたけ',
        2356,
        '福島県',
        36.9478,
        139.2878,
        10.0,
        420,
        1000,
        'MODERATE',
        TRUE
    ),
    (
        '至仏山',
        'Mt. Shibutsu',
        'しぶつさん',
        2228,
        '群馬県',
        36.9139,
        139.2042,
        10.0,
        360,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '谷川岳',
        'Mt. Tanigawa',
        'たにがわだけ',
        1977,
        '群馬県/新潟県',
        36.8389,
        138.9294,
        7.0,
        360,
        900,
        'MODERATE',
        TRUE
    ),
    (
        '雨飾山',
        'Mt. Amakazari',
        'あまかざりやま',
        1963,
        '新潟県/長野県',
        36.8972,
        137.9628,
        8.0,
        420,
        1100,
        'MODERATE',
        TRUE
    ),
    (
        '苗場山',
        'Mt. Naeba',
        'なえばさん',
        2145,
        '新潟県/長野県',
        36.8472,
        138.6861,
        14.0,
        540,
        1100,
        'MODERATE',
        TRUE
    ),
    (
        '妙高山',
        'Mt. Myoko',
        'みょうこうさん',
        2454,
        '新潟県',
        36.8906,
        138.1139,
        10.0,
        480,
        1300,
        'HARD',
        TRUE
    ),
    (
        '火打山',
        'Mt. Hiuchi',
        'ひうちやま',
        2462,
        '新潟県',
        36.9222,
        138.1217,
        15.0,
        600,
        1200,
        'HARD',
        TRUE
    ),
    (
        '高妻山',
        'Mt. Takatsuma',
        'たかつまやま',
        2353,
        '新潟県/長野県',
        36.8208,
        138.0611,
        11.0,
        540,
        1400,
        'HARD',
        TRUE
    ),
    (
        '男体山',
        'Mt. Nantai',
        'なんたいさん',
        2486,
        '栃木県',
        36.7653,
        139.4906,
        8.0,
        360,
        1200,
        'MODERATE',
        TRUE
    ),
    (
        '日光白根山',
        'Mt. Nikko-Shirane',
        'にっこうしらねさん',
        2578,
        '栃木県/群馬県',
        36.7994,
        139.3758,
        8.0,
        360,
        700,
        'MODERATE',
        TRUE
    ),
    (
        '皇海山',
        'Mt. Sukai',
        'すかいさん',
        2144,
        '栃木県/群馬県',
        36.7564,
        139.3294,
        12.0,
        600,
        1300,
        'HARD',
        TRUE
    ),
    (
        '武尊山',
        'Mt. Hotaka',
        'ほたかやま',
        2158,
        '群馬県',
        36.8261,
        139.1497,
        10.0,
        420,
        1000,
        'MODERATE',
        TRUE
    ),
    (
        '赤城山',
        'Mt. Akagi',
        'あかぎやま',
        1828,
        '群馬県',
        36.5608,
        139.1978,
        4.0,
        180,
        500,
        'EASY',
        TRUE
    ),
    (
        '草津白根山',
        'Mt. Kusatsu-Shirane',
        'くさつしらねさん',
        2160,
        '群馬県',
        36.6433,
        138.5339,
        4.0,
        180,
        400,
        'EASY',
        TRUE
    ),
    (
        '四阿山',
        'Mt. Azumaya',
        'あずまやさん',
        2354,
        '群馬県/長野県',
        36.5417,
        138.4114,
        10.0,
        360,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '浅間山',
        'Mt. Asama',
        'あさまやま',
        2568,
        '群馬県/長野県',
        36.4064,
        138.5231,
        11.0,
        420,
        1000,
        'MODERATE',
        TRUE
    ),
    (
        '筑波山',
        'Mt. Tsukuba',
        'つくばさん',
        877,
        '茨城県',
        36.2253,
        140.1006,
        5.0,
        180,
        600,
        'EASY',
        TRUE
    ),
    (
        '白馬岳',
        'Mt. Shirouma',
        'しろうまだけ',
        2932,
        '長野県/富山県',
        36.7581,
        137.7564,
        22.0,
        900,
        1800,
        'HARD',
        TRUE
    ),
    (
        '五竜岳',
        'Mt. Goryu',
        'ごりゅうだけ',
        2814,
        '長野県/富山県',
        36.7039,
        137.7528,
        15.0,
        720,
        1600,
        'EXPERT',
        TRUE
    ),
    (
        '鹿島槍ヶ岳',
        'Mt. Kashimayari',
        'かしまやりがたけ',
        2889,
        '長野県/富山県',
        36.6569,
        137.7458,
        20.0,
        840,
        1800,
        'EXPERT',
        TRUE
    ),
    (
        '剱岳',
        'Mt. Tsurugi',
        'つるぎだけ',
        2999,
        '富山県',
        36.6231,
        137.6172,
        14.0,
        720,
        1500,
        'EXPERT',
        TRUE
    ),
    (
        '立山',
        'Mt. Tateyama',
        'たてやま',
        3015,
        '富山県',
        36.5731,
        137.6167,
        8.0,
        360,
        700,
        'MODERATE',
        TRUE
    ),
    (
        '薬師岳',
        'Mt. Yakushi',
        'やくしだけ',
        2926,
        '富山県',
        36.4789,
        137.5456,
        22.0,
        840,
        1500,
        'HARD',
        TRUE
    ),
    (
        '黒部五郎岳',
        'Mt. Kurobegoro',
        'くろべごろうだけ',
        2840,
        '富山県/岐阜県',
        36.3981,
        137.5386,
        28.0,
        1080,
        1800,
        'EXPERT',
        TRUE
    ),
    (
        '水晶岳',
        'Mt. Suisho',
        'すいしょうだけ',
        2986,
        '富山県',
        36.4303,
        137.5975,
        30.0,
        1200,
        2000,
        'EXPERT',
        TRUE
    ),
    (
        '鷲羽岳',
        'Mt. Washiba',
        'わしばだけ',
        2924,
        '富山県/長野県',
        36.4083,
        137.6106,
        24.0,
        960,
        1700,
        'EXPERT',
        TRUE
    ),
    (
        '槍ヶ岳',
        'Mt. Yari',
        'やりがたけ',
        3180,
        '長野県/岐阜県',
        36.3428,
        137.6475,
        20.0,
        780,
        1700,
        'HARD',
        TRUE
    ),
    (
        '穂高岳',
        'Mt. Hotaka',
        'ほたかだけ',
        3190,
        '長野県/岐阜県',
        36.2889,
        137.6478,
        20.0,
        780,
        1700,
        'HARD',
        TRUE
    ),
    (
        '常念岳',
        'Mt. Jonen',
        'じょうねんだけ',
        2857,
        '長野県',
        36.3247,
        137.7231,
        12.0,
        600,
        1500,
        'HARD',
        TRUE
    ),
    (
        '笠ヶ岳',
        'Mt. Kasa',
        'かさがたけ',
        2898,
        '岐阜県',
        36.3286,
        137.5456,
        24.0,
        840,
        1700,
        'EXPERT',
        TRUE
    ),
    (
        '焼岳',
        'Mt. Yake',
        'やけだけ',
        2455,
        '長野県/岐阜県',
        36.2269,
        137.5878,
        7.0,
        360,
        900,
        'MODERATE',
        TRUE
    ),
    (
        '乗鞍岳',
        'Mt. Norikura',
        'のりくらだけ',
        3026,
        '長野県/岐阜県',
        36.1064,
        137.5547,
        3.0,
        120,
        300,
        'EASY',
        TRUE
    ),
    (
        '御嶽山',
        'Mt. Ontake',
        'おんたけさん',
        3067,
        '長野県/岐阜県',
        35.8931,
        137.4803,
        8.0,
        420,
        1000,
        'MODERATE',
        TRUE
    ),
    (
        '美ヶ原',
        'Utsukushigahara',
        'うつくしがはら',
        2034,
        '長野県',
        36.2303,
        138.1122,
        3.0,
        120,
        200,
        'EASY',
        TRUE
    ),
    (
        '霧ヶ峰',
        'Kirigamine',
        'きりがみね',
        1925,
        '長野県',
        36.1036,
        138.1736,
        4.0,
        150,
        300,
        'EASY',
        TRUE
    ),
    (
        '蓼科山',
        'Mt. Tateshina',
        'たてしなやま',
        2531,
        '長野県',
        36.1028,
        138.2953,
        6.0,
        300,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '八ヶ岳',
        'Mt. Yatsugatake',
        'やつがたけ',
        2899,
        '長野県/山梨県',
        35.9708,
        138.3703,
        14.0,
        600,
        1400,
        'HARD',
        TRUE
    ),
    (
        '両神山',
        'Mt. Ryokami',
        'りょうかみさん',
        1723,
        '埼玉県',
        36.0025,
        138.8753,
        6.0,
        300,
        900,
        'MODERATE',
        TRUE
    ),
    (
        '雲取山',
        'Mt. Kumotori',
        'くもとりやま',
        2017,
        '東京都/埼玉県/山梨県',
        35.8564,
        138.9428,
        20.0,
        600,
        1500,
        'MODERATE',
        TRUE
    ),
    (
        '甲武信ヶ岳',
        'Mt. Kobushi',
        'こぶしがたけ',
        2475,
        '山梨県/埼玉県/長野県',
        35.9153,
        138.7281,
        17.0,
        600,
        1300,
        'HARD',
        TRUE
    ),
    (
        '金峰山',
        'Mt. Kinpu',
        'きんぷさん',
        2599,
        '山梨県/長野県',
        35.8733,
        138.6267,
        10.0,
        420,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '瑞牆山',
        'Mt. Mizugaki',
        'みずがきやま',
        2230,
        '山梨県',
        35.8878,
        138.5944,
        5.0,
        300,
        700,
        'MODERATE',
        TRUE
    ),
    (
        '大菩薩嶺',
        'Mt. Daibosatsu',
        'だいぼさつれい',
        2057,
        '山梨県',
        35.7519,
        138.8531,
        7.0,
        240,
        500,
        'EASY',
        TRUE
    ),
    (
        '丹沢山',
        'Mt. Tanzawa',
        'たんざわさん',
        1567,
        '神奈川県',
        35.4744,
        139.1628,
        16.0,
        480,
        1400,
        'MODERATE',
        TRUE
    ),
    (
        '富士山',
        'Mt. Fuji',
        'ふじさん',
        3776,
        '静岡県/山梨県',
        35.3606,
        138.7274,
        14.0,
        600,
        1400,
        'HARD',
        TRUE
    ),
    (
        '天城山',
        'Mt. Amagi',
        'あまぎさん',
        1406,
        '静岡県',
        34.8672,
        139.0133,
        7.0,
        300,
        600,
        'EASY',
        TRUE
    ),
    (
        '木曽駒ヶ岳',
        'Mt. Kiso-Komagatake',
        'きそこまがたけ',
        2956,
        '長野県',
        35.7892,
        137.8031,
        4.0,
        240,
        500,
        'EASY',
        TRUE
    ),
    (
        '空木岳',
        'Mt. Utsugi',
        'うつぎだけ',
        2864,
        '長野県',
        35.7108,
        137.8225,
        18.0,
        720,
        1700,
        'HARD',
        TRUE
    ),
    (
        '恵那山',
        'Mt. Ena',
        'えなさん',
        2191,
        '長野県/岐阜県',
        35.4269,
        137.5958,
        12.0,
        420,
        1000,
        'MODERATE',
        TRUE
    ),
    (
        '甲斐駒ヶ岳',
        'Mt. Kai-Komagatake',
        'かいこまがたけ',
        2967,
        '山梨県/長野県',
        35.7578,
        138.2342,
        9.0,
        480,
        1100,
        'HARD',
        TRUE
    ),
    (
        '仙丈ヶ岳',
        'Mt. Senjo',
        'せんじょうがたけ',
        3033,
        '山梨県/長野県',
        35.7228,
        138.1847,
        10.0,
        480,
        1100,
        'MODERATE',
        TRUE
    ),
    (
        '鳳凰山',
        'Mt. Hoo',
        'ほうおうさん',
        2841,
        '山梨県',
        35.7056,
        138.2944,
        12.0,
        600,
        1500,
        'HARD',
        TRUE
    ),
    (
        '北岳',
        'Mt. Kita',
        'きただけ',
        3193,
        '山梨県',
        35.6744,
        138.2386,
        10.0,
        600,
        1700,
        'HARD',
        TRUE
    ),
    (
        '間ノ岳',
        'Mt. Aino',
        'あいのだけ',
        3190,
        '山梨県/静岡県',
        35.6506,
        138.2228,
        20.0,
        900,
        2100,
        'EXPERT',
        TRUE
    ),
    (
        '塩見岳',
        'Mt. Shiomi',
        'しおみだけ',
        3052,
        '長野県/静岡県',
        35.6139,
        138.1806,
        25.0,
        960,
        1800,
        'EXPERT',
        TRUE
    ),
    (
        '悪沢岳',
        'Mt. Warusawa',
        'わるさわだけ',
        3141,
        '静岡県',
        35.5503,
        138.1906,
        26.0,
        1080,
        2000,
        'EXPERT',
        TRUE
    ),
    (
        '赤石岳',
        'Mt. Akaishi',
        'あかいしだけ',
        3121,
        '長野県/静岡県',
        35.5261,
        138.1542,
        26.0,
        1080,
        2000,
        'EXPERT',
        TRUE
    ),
    (
        '聖岳',
        'Mt. Hijiri',
        'ひじりだけ',
        3013,
        '長野県/静岡県',
        35.4789,
        138.1450,
        26.0,
        1080,
        2200,
        'EXPERT',
        TRUE
    ),
    (
        '光岳',
        'Mt. Tekari',
        'てかりだけ',
        2592,
        '長野県/静岡県',
        35.4006,
        138.0897,
        26.0,
        1080,
        1800,
        'EXPERT',
        TRUE
    ),
    (
        '白山',
        'Mt. Hakusan',
        'はくさん',
        2702,
        '石川県/岐阜県',
        36.1533,
        136.7717,
        12.0,
        540,
        1500,
        'HARD',
        TRUE
    ),
    (
        '荒島岳',
        'Mt. Arashima',
        'あらしまだけ',
        1524,
        '福井県',
        35.9408,
        136.5978,
        9.0,
        360,
        1200,
        'MODERATE',
        TRUE
    ),
    (
        '伊吹山',
        'Mt. Ibuki',
        'いぶきやま',
        1377,
        '滋賀県/岐阜県',
        35.4167,
        136.4044,
        6.0,
        240,
        1000,
        'EASY',
        TRUE
    ),
    (
        '大台ヶ原山',
        'Mt. Odaigahara',
        'おおだいがはらやま',
        1695,
        '奈良県/三重県',
        34.1883,
        136.1039,
        8.0,
        240,
        400,
        'EASY',
        TRUE
    ),
    (
        '大峰山',
        'Mt. Omine',
        'おおみねさん',
        1915,
        '奈良県',
        34.1806,
        135.9550,
        10.0,
        480,
        1200,
        'HARD',
        TRUE
    ),
    (
        '大山',
        'Mt. Daisen',
        'だいせん',
        1729,
        '鳥取県',
        35.3711,
        133.5406,
        6.0,
        300,
        900,
        'MODERATE',
        TRUE
    ),
    (
        '剣山',
        'Mt. Tsurugi',
        'つるぎさん',
        1955,
        '徳島県',
        33.8544,
        134.0939,
        4.0,
        180,
        500,
        'EASY',
        TRUE
    ),
    (
        '石鎚山',
        'Mt. Ishizuchi',
        'いしづちさん',
        1982,
        '愛媛県',
        33.7728,
        133.1153,
        7.0,
        360,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '九重山',
        'Mt. Kuju',
        'くじゅうさん',
        1791,
        '大分県',
        33.0864,
        131.2433,
        10.0,
        360,
        700,
        'MODERATE',
        TRUE
    ),
    (
        '祖母山',
        'Mt. Sobo',
        'そぼさん',
        1756,
        '大分県/宮崎県',
        32.8261,
        131.3267,
        8.0,
        360,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '阿蘇山',
        'Mt. Aso',
        'あそさん',
        1592,
        '熊本県',
        32.8842,
        131.1044,
        4.0,
        180,
        300,
        'EASY',
        TRUE
    ),
    (
        '霧島山',
        'Mt. Kirishima',
        'きりしまやま',
        1700,
        '宮崎県/鹿児島県',
        31.9342,
        130.8644,
        5.0,
        240,
        500,
        'EASY',
        TRUE
    ),
    (
        '開聞岳',
        'Mt. Kaimon',
        'かいもんだけ',
        924,
        '鹿児島県',
        31.2089,
        130.5564,
        8.0,
        300,
        800,
        'MODERATE',
        TRUE
    ),
    (
        '宮之浦岳',
        'Mt. Miyanoura',
        'みやのうらだけ',
        1936,
        '鹿児島県',
        30.3372,
        130.5014,
        16.0,
        600,
        1200,
        'HARD',
        TRUE
    );

-- =============================================
-- その他の人気山 (百名山以外)
-- =============================================
INSERT IGNORE INTO
    mountains (
        name,
        name_en,
        name_kana,
        elevation,
        prefecture,
        latitude,
        longitude,
        typical_distance_km,
        typical_duration_minutes,
        typical_elevation_gain,
        difficulty,
        is_hyakumeizan
    )
VALUES (
        '高尾山',
        'Mt. Takao',
        'たかおさん',
        599,
        '東京都',
        35.6250,
        139.2430,
        7.0,
        240,
        600,
        'EASY',
        FALSE
    ),
    (
        '陣馬山',
        'Mt. Jinba',
        'じんばさん',
        855,
        '東京都/神奈川県',
        35.6525,
        139.1689,
        9.0,
        270,
        760,
        'MODERATE',
        FALSE
    ),
    (
        '摩耶山',
        'Mt. Maya',
        'まやさん',
        702,
        '兵庫県',
        34.7336,
        135.2047,
        8.0,
        240,
        700,
        'MODERATE',
        FALSE
    ),
    (
        '鳴虫山',
        'Mt. Nakimushi',
        'なきむしやま',
        1103,
        '栃木県',
        36.7306,
        139.5964,
        7.0,
        240,
        700,
        'MODERATE',
        FALSE
    ),
    (
        '鋸山',
        'Mt. Nokogiri',
        'のこぎりやま',
        329,
        '千葉県',
        35.1600,
        139.8410,
        7.0,
        240,
        560,
        'EASY',
        FALSE
    ),
    (
        '大山',
        'Mt. Oyama',
        'おおやま',
        1252,
        '神奈川県',
        35.4408,
        139.2311,
        8.0,
        240,
        1000,
        'MODERATE',
        FALSE
    ),
    (
        '御在所岳',
        'Mt. Gozaisho',
        'ございしょだけ',
        1212,
        '三重県',
        35.0204,
        136.4185,
        5.0,
        270,
        760,
        'MODERATE',
        FALSE
    ),
    (
        '金剛山',
        'Mt. Kongo',
        'こんごうさん',
        1125,
        '大阪府/奈良県',
        34.4170,
        135.6770,
        7.0,
        210,
        660,
        'EASY',
        FALSE
    ),
    (
        '宝満山',
        'Mt. Homan',
        'ほうまんざん',
        829,
        '福岡県',
        33.5397,
        130.5690,
        6.0,
        240,
        920,
        'MODERATE',
        FALSE
    ),
    (
        '由布岳',
        'Mt. Yufu',
        'ゆふだけ',
        1583,
        '大分県',
        33.2820,
        131.3900,
        7.5,
        270,
        900,
        'MODERATE',
        FALSE
    ),
    (
        '塔ノ岳',
        'Mt. Tonodake',
        'とうのだけ',
        1491,
        '神奈川県',
        35.4540,
        139.1650,
        14.0,
        420,
        1200,
        'HARD',
        FALSE
    );

-- 画像URLの更新
UPDATE mountains
SET
    image_url = 'https://images.unsplash.com/photo-1578271887552-5ac3a72752bc?auto=format&fit=crop&w=800&q=80',
    image_citation = 'Photo by David Emrich on Unsplash'
WHERE
    name = '富士山';

UPDATE mountains
SET
    image_url = '/images/高尾山.jpeg',
    image_citation = 'Gemini 3 Pro'
WHERE
    name = '高尾山';

UPDATE mountains
SET
    image_url = 'https://images.unsplash.com/photo-1589553416260-f586c8f1514f?auto=format&fit=crop&w=800&q=80',
    image_citation = 'Photo by Unsplash'
WHERE
    name = '武尊山';

UPDATE mountains
SET
    image_url = '/images/槍ヶ岳.png',
    image_citation = 'Gemini 3 Pro'
WHERE
    name = '槍ヶ岳';

-- =============================================
-- 初期アクティビティデータ (ユーザーID=1: 山田太郎)
-- =============================================
-- 1. 高尾山 (2025-01-01)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 1, 1, '2025-01-01', '初日の出登山', '東京都', '素晴らしい初日の出でした。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '高尾山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        1,
        1,
        7.0,
        200,
        600,
        599,
        'ゆっくりペース'
    );

-- 2. 陣馬山 (2023-12-10)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 2, 1, '2023-12-10', '陣馬山ハイキング', '東京都/神奈川県', '頂上の白馬像を見てきました。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '陣馬山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        2,
        2,
        9.5,
        300,
        800,
        855,
        '標準タイム'
    );

-- 3. 塔ノ岳 (2024-03-15)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 3, 1, '2024-03-15', 'バカ尾根へ挑戦', '神奈川県', '階段がきつかったですが、景色は最高でした。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '塔ノ岳';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        3,
        3,
        14.2,
        430,
        1250,
        1491,
        'きつかった'
    );

-- 4. 丹沢山 (2024-05-04)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 4, 1, '2024-05-04', '丹沢主脈縦走旅でした', '神奈川県', '百名山ゲット！', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '丹沢山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        4,
        4,
        16.0,
        500,
        1500,
        1567,
        '標準より少し早め'
    );

-- 5. 富士山 (2024-07-20)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 5, 1, '2024-07-20', '初富士山', '静岡県/山梨県', 'ご来光に感動。酸素が薄かった。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '富士山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        5,
        5,
        14.5,
        480,
        1400,
        3776,
        '高山病に注意してゆっくり走りましたが、結局高山病になりました。'
    );

-- 6. 鳴虫山 (2023-11-23)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 6, 1, '2023-11-23', '紅葉の鳴虫山', '栃木県', '紅葉が綺麗でした。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '鳴虫山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        6,
        6,
        7.2,
        250,
        720,
        1103,
        'のんびり'
    );

-- 7. 摩耶山 (2024-09-15)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 7, 1, '2024-09-15', '摩耶山', '兵庫県', '記念すべき初登山、意外に体力がありました。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '摩耶山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        7,
        7,
        8.5,
        260,
        750,
        702,
        '標準'
    );

-- 8. 筑波山 (2023-04-15)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 8, 2, '2023-04-15', '春の筑波山', '茨城県', '初めての登山で筑波山へ。歩きやすくて楽しかったです。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '筑波山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        8,
        8,
        5.0,
        180,
        600,
        877,
        '標準より少しゆっくり'
    );

-- 9. 赤城山 (2023-05-20)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 9, 2, '2023-05-20', '赤城山へ', '群馬県', '風が心地よく、景色も最高でした。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '赤城山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        9,
        9,
        4.0,
        180,
        500,
        1828,
        '無理のないペースで'
    );

-- 10. 安達太良山 (2023-07-10)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 10, 2, '2023-07-10', '安達太良山の絶景', '福島県', '青空が広がり、火口の景色が素晴らしかった。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '安達太良山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        10,
        10,
        9.0,
        300,
        700,
        1700,
        '標準ペース'
    );

-- 11. 月山 (2023-08-05)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 11, 2, '2023-08-05', '月山登拝', '山形県', 'なだらかな山容と高山植物が綺麗でした。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '月山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        11,
        11,
        8.0,
        300,
        600,
        1984,
        '写真撮影で立ち止まりつつ'
    );

-- 12. 磐梯山 (2023-09-12)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 12, 2, '2023-09-12', '磐梯山からの猪苗代湖', '福島県', '山頂からの猪苗代湖の眺めが抜群！秋の気配を感じました。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '磐梯山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        12,
        12,
        8.0,
        300,
        800,
        1816,
        'すこし早歩き'
    );

-- 13. 男体山 (2023-10-21)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 13, 2, '2023-10-21', '紅葉の男体山', '栃木県', '中禅寺湖を見下ろす絶景。登りは急でしたが達成感がありました。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '男体山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        13,
        13,
        8.0,
        360,
        1200,
        2486,
        '息を整えながら'
    );

-- 14. 四阿山 (2024-05-18)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 14, 2, '2024-05-18', '新緑の四阿山', '群馬県/長野県', '根子岳への縦走も楽しめました。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '四阿山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        14,
        14,
        10.0,
        360,
        800,
        2354,
        '標準タイム通り'
    );

-- 15. 谷川岳 (2024-07-28)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 15, 2, '2024-07-28', '双耳峰の谷川岳へ', '群馬県/新潟県', 'ロープウェイを利用して天神尾根から。トマの耳、オキの耳を制覇！', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '谷川岳';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        15,
        15,
        7.0,
        360,
        900,
        1977,
        '岩場は慎重に'
    );

-- 16. 妙高山 (2024-09-08)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 16, 2, '2024-09-08', '越後富士・妙高山', '新潟県', 'かなり険しい道のりでしたが、無事登頂できました。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '妙高山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        16,
        16,
        10.0,
        480,
        1300,
        2454,
        '少し遅れ気味だったが完歩'
    );

-- 17. 日光白根山 (2024-10-14)
INSERT IGNORE INTO
    activities (
        activity_id,
        user_id,
        activity_date,
        title,
        location,
        description,
        mountain_id,
        created_at,
        updated_at
    )
SELECT 17, 2, '2024-10-14', '関東以北最高峰・日光白根山', '栃木県/群馬県', '五色沼の緑が美しく、山頂は360度の大パノラマでした。', m.mountain_id, NOW(), NOW()
FROM mountains m
WHERE
    m.name = '日光白根山';

INSERT IGNORE INTO
    activity_details (
        detail_id,
        activity_id,
        distance_km,
        duration_minutes,
        elevation_gain,
        max_elevation,
        pace_notes
    )
VALUES (
        17,
        17,
        8.0,
        360,
        700,
        2578,
        '快調なペース'
    );