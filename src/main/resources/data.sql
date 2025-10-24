INSERT INTO restaurant (id, description, photo_path, location_id)
VALUES
    ('b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'Cozy Italian restaurant serving pasta and pizza.', '/images/restaurants/bella_italia.jpg', NULL),
    ('e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'Modern sushi bar with fresh fish and minimalist decor.', '/images/restaurants/sakura_sushi.jpg', NULL);

-- La Bella Italia
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('2a1b0f8d-2d74-48f8-a5e4-2e6dcd42d501', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'MONDAY', '10:00', '22:00'),
    ('4a3b6f9c-24c2-4d9f-bbc6-6718949b8af9', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'TUESDAY', '10:00', '22:00'),
    ('7b4a3a2d-8a1b-478f-96b2-3c3b22f2ad40', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'SUNDAY', '12:00', '21:00');

-- Sakura Sushi
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('9f2b3a12-6d45-4a21-83b2-0a8b3b5f5f77', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'MONDAY', '11:00', '21:00'),
    ('a17b9c43-34ad-4fb1-9c2e-56e58e0b64e1', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'FRIDAY', '11:00', '23:00');

INSERT INTO daily_menu (id, date, status, restaurant_id)
VALUES
    ('c1111111-aaaa-bbbb-cccc-111111111111', '2025-10-24', 'ACTIVE', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1'),
    ('c2222222-bbbb-cccc-dddd-222222222222', '2025-10-24', 'ACTIVE', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8');

INSERT INTO dish (id, name, category, amount, currency, restaurant_id)
VALUES
    ('a1111111-1111-1111-1111-111111111111', 'Margherita Pizza', 'MAIN_COURSE', 28.50, 'PLN', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1'),
    ('a2222222-2222-2222-2222-222222222222', 'Tomato Soup', 'SOUP', 14.00, 'PLN', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1'),
    ('a3333333-3333-3333-3333-333333333333', 'Salmon Nigiri', 'MAIN_COURSE', 22.00, 'PLN', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8'),
    ('a4444444-4444-4444-4444-444444444444', 'Tuna Roll', 'MAIN_COURSE', 26.00, 'PLN', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8');

-- La Bella Italia – menu włoskie
INSERT INTO daily_menu_dishes (daily_menu_id, dish_id)
VALUES
    ('c1111111-aaaa-bbbb-cccc-111111111111', 'a1111111-1111-1111-1111-111111111111'),
    ('c1111111-aaaa-bbbb-cccc-111111111111', 'a2222222-2222-2222-2222-222222222222');

-- Sakura Sushi – menu japońskie
INSERT INTO daily_menu_dishes (daily_menu_id, dish_id)
VALUES
    ('c2222222-bbbb-cccc-dddd-222222222222', 'a3333333-3333-3333-3333-333333333333'),
    ('c2222222-bbbb-cccc-dddd-222222222222', 'a4444444-4444-4444-4444-444444444444');