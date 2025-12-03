INSERT INTO location (id, latitude, longitude)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 52.2297, 21.0122),  -- La Bella Italia (przykładowa lokalizacja Warszawa)
    ('550e8400-e29b-41d4-a716-446655440002', 52.2356, 21.0138);  -- Sakura Sushi

INSERT INTO restaurant (id, name, description, photo_path, location_id)
VALUES
    ('b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'La Bella Italia', 'Cozy Italian restaurant serving pasta and pizza.', '/images/restaurants/bella_italia.jpg', '550e8400-e29b-41d4-a716-446655440001'),
    ('e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'Sakura Sushi', 'Modern sushi bar with fresh fish and minimalist decor.', '/images/restaurants/sakura_sushi.jpg', '550e8400-e29b-41d4-a716-446655440002');

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

-- Lokacje dla vending machines
INSERT INTO location (id, latitude, longitude)
VALUES
    ('550e8400-e29b-41d4-a716-446655440003', 52.2310, 21.0125),
    ('550e8400-e29b-41d4-a716-446655440004', 52.2315, 21.0130),
    ('550e8400-e29b-41d4-a716-446655440005', 52.2320, 21.0135);

-- Vending machines
INSERT INTO vending_machine (id, description, photo_path, location_id, type)
VALUES
    ('c3b6a5ef-9f40-5d4f-b62f-9d638df8f2b2', 'Snack vending machine with chips, cookies, and candy bars.', '/images/vending/snacks_01.jpg', '550e8400-e29b-41d4-a716-446655440003', 'SNACKS'),
    ('d4c7b6f0-0a51-6e5f-c73f-0e749ef9f3c3', 'Coffee machine with espresso, cappuccino, and hot chocolate.', '/images/vending/coffee_01.jpg', '550e8400-e29b-41d4-a716-446655440004', 'COFFEE'),
    ('e5d8c7f1-1b62-7f6f-d84f-1f850ff0f4d4', 'Lunch vending machine with sandwiches, salads, and wraps.', '/images/vending/lunch_01.jpg', '550e8400-e29b-41d4-a716-446655440005', 'LUNCH');

INSERT INTO keycloak_user (id)
VALUES (
    'e8efd725-8397-4aea-979f-3c096470d23b'  -- To jest subject z JWT Keycloak koneicznie z rola owner!!
);
