------------------------------------------------------------
-- RESTAURANTS AND COORDINATES
------------------------------------------------------------

INSERT INTO location (id, latitude, longitude)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 50.030511999856294, 19.90718833360244),  -- Bistro Świetlica, WMiI
    ('550e8400-e29b-41d4-a716-446655440002', 50.030170829430276, 19.908160572569297),  -- Bistro by Jelonek, WZiKS
    ('550e8400-e29b-41d4-a716-446655440003', 50.027354360288484, 19.900980407813968), -- Bistro4mat, IZ
    ('550e8400-e29b-41d4-a716-446655440004', 50.03000205209573, 19.905736283744663),  -- Bistro 11, WFAIS
    ('550e8400-e29b-41d4-a716-446655440005', 50.02902509906889, 19.907067338269396);  -- Neon Bistro, WCh

INSERT INTO restaurant (id, name, description, photo_path, location_id)
VALUES
    ('b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'Bistro Świetlica', 'Favourite place for CS students to eat', '/images/restaurants/bistro_swietlica.jpg', '550e8400-e29b-41d4-a716-446655440001'),
    ('e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'Bistro by Jelonek', 'Some food at WZiKS', '/images/restaurants/bistro_by_jelonek.jpg', '550e8400-e29b-41d4-a716-446655440002'),
    ('4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'Bistro4mat', 'Some food at IZ', '/images/restaurants/bistro4mat.jpg', '550e8400-e29b-41d4-a716-446655440003'),
    ('76e4647b-cb56-460a-9f30-a6b3482fb93a', 'Bistro 11', 'Some food at WFAIS', '/images/restaurants/bistro_11.jpg', '550e8400-e29b-41d4-a716-446655440004'),
    ('1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'Neon Bistro', 'Some food at WCh', '/images/restaurants/neon_bistro.jpg', '550e8400-e29b-41d4-a716-446655440005');

------------------------------------------------------------
-- OPENING HOURS
------------------------------------------------------------

-- Bistro Świetlica
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('2a1b0f8d-2d74-48f8-a5e4-2e6dcd42d501', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'MONDAY', '9:00', '16:00'),
    ('4a3b6f9c-24c2-4d9f-bbc6-6718949b8af9', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'TUESDAY', '9:00', '16:00'),
    ('7b4a3a2d-8a1b-478f-96b2-3c3b22f2ad40', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'WEDNESDAY', '9:00', '16:00'),
    ('c3d5d2ae-a634-4fb2-a548-b1b65aa9c03f', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'THURSDAY', '9:00', '16:00'),
    ('6d884176-31ea-451f-91c7-81a89d484611', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'FRIDAY', '9:00', '16:00');

-- Bistro by Jelonek
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('9f2b3a12-6d45-4a21-83b2-0a8b3b5f5f77', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'MONDAY', '7:00', '17:00'),
    ('fa484b16-2651-49c4-99db-e6e5f8e34e46', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'TUESDAY', '7:00', '17:00'),
    ('e96b5d50-562e-48cf-93a0-cd3b1144d186', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'WEDNESDAY', '7:00', '17:00'),
    ('b5c4d3f2-946c-4007-9e87-91d86c40a07b', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'THURSDAY', '7:00', '17:00'),
    ('a17b9c43-34ad-4fb1-9c2e-56e58e0b64e1', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'FRIDAY', '7:00', '17:00'),
    ('cf98d8c5-3555-44e5-8dfd-5cbdb9c22f2b', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'SATURDAY', '7:00', '17:00'),
    ('a3ac00c2-ab4a-42ff-b914-a636629afbbb', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'SUNDAY', '7:00', '17:00');

-- Bistro4mat
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
     ('57ae2be7-0b21-4108-bdee-803e29d6dee4', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'MONDAY', '9:00', '15:30'),
     ('83bea995-3313-4b0f-b237-18ea7d5b5644', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'TUESDAY', '9:00', '15:30'),
     ('a65e28fb-470b-4d6d-9ef5-fc31984dcd39', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'WEDNESDAY', '9:00', '15:30'),
     ('b9dc7047-128b-4854-acb6-7949ef0a7e8c', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'THURSDAY', '9:00', '15:30'),
     ('18840544-29da-45e7-a531-b99c8e059263', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'FRIDAY', '9:00', '15:30');

-- Bistro 11
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('da3b1bbc-c793-476a-bd6f-04b13ca4b47f', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'MONDAY', '8:00', '16:00'),
    ('9077ec6a-fecf-4b1d-8e2f-c1ef0260d4b6', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'TUESDAY', '8:00', '16:00'),
    ('a2ac6e3d-dfd2-492b-91f7-e631a952f256', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'WEDNESDAY', '8:00', '16:00'),
    ('1a6ade0c-a184-4028-93a4-2dd9d75f0420', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'THURSDAY', '8:00', '16:00'),
    ('e0ee6006-3a0f-4645-8fa1-2f533b72b18b', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'FRIDAY', '8:00', '16:00');

-- Neon Bistro
INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('79120ac2-032b-4610-bc62-340e1dacd896', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'MONDAY', '8:00', '17:00'),
    ('c1e2dd8f-acf3-47a2-90a8-243523b96e3b', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'TUESDAY', '8:00', '17:00'),
    ('0fd40668-1834-4351-bf6f-040836e2b82e', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'WEDNESDAY', '8:00', '17:00'),
    ('47bbb917-4d97-4955-84f4-3dad31b81816', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'THURSDAY', '8:00', '17:00'),
    ('416845b4-ee0e-4cb0-897c-110d05bba7b3', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'FRIDAY', '8:00', '17:00');

------------------------------------------------------------
-- ACTIVE MENUS FOR EVERY RESTAURANT
------------------------------------------------------------

INSERT INTO daily_menu (id, date, status, restaurant_id)
VALUES
    ('c1111111-aaaa-bbbb-cccc-111111111111', '2025-10-24', 'ACTIVE', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1'),
    ('c2222222-aaaa-bbbb-cccc-222222222222', '2025-10-24', 'ACTIVE', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8'),
    ('c3333333-aaaa-bbbb-cccc-333333333333', '2025-10-24', 'ACTIVE', '4a2a13c4-34be-4bef-82c3-a879c91bba5b'),
    ('c4444444-aaaa-bbbb-cccc-444444444444', '2025-10-24', 'ACTIVE', '76e4647b-cb56-460a-9f30-a6b3482fb93a'),
    ('c5555555-aaaa-bbbb-cccc-555555555555', '2025-10-24', 'ACTIVE', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866');

------------------------------------------------------------
-- DISHES (test menus)
------------------------------------------------------------

-- Bistro Świetlica
INSERT INTO dish (id, name, category, amount, currency, restaurant_id) VALUES
('d1111111-aaaa-bbbb-cccc-000000000001', 'Potato Dumplings', 'MAIN_COURSE', 19.50, 'PLN', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1'),
('d1111111-aaaa-bbbb-cccc-000000000002', 'Red Beet Soup', 'MAIN_COURSE', 8.00, 'PLN', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1'),
('d1111111-aaaa-bbbb-cccc-000000000003', 'Pumpkin Cream Soup', 'MAIN_COURSE', 12.00, 'PLN', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1');

INSERT INTO daily_menu_dishes (daily_menu_id, dish_id) VALUES
('c1111111-aaaa-bbbb-cccc-111111111111', 'd1111111-aaaa-bbbb-cccc-000000000001'),
('c1111111-aaaa-bbbb-cccc-111111111111', 'd1111111-aaaa-bbbb-cccc-000000000002'),
('c1111111-aaaa-bbbb-cccc-111111111111', 'd1111111-aaaa-bbbb-cccc-000000000003');

-- Bistro by Jelonek
INSERT INTO dish (id, name, category, amount, currency, restaurant_id) VALUES
('d2222222-aaaa-bbbb-cccc-000000000001', 'Chicken Teriyaki Bowl', 'MAIN_COURSE', 24.00, 'PLN', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8'),
('d2222222-aaaa-bbbb-cccc-000000000002', 'Miso Soup', 'MAIN_COURSE', 10.00, 'PLN', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8'),
('d2222222-aaaa-bbbb-cccc-000000000003', 'Salmon Rice Bowl', 'MAIN_COURSE', 9.50, 'PLN', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8');

INSERT INTO daily_menu_dishes (daily_menu_id, dish_id) VALUES
('c2222222-aaaa-bbbb-cccc-222222222222', 'd2222222-aaaa-bbbb-cccc-000000000001'),
('c2222222-aaaa-bbbb-cccc-222222222222', 'd2222222-aaaa-bbbb-cccc-000000000002'),
('c2222222-aaaa-bbbb-cccc-222222222222', 'd2222222-aaaa-bbbb-cccc-000000000003');

-- Bistro4mat
INSERT INTO dish (id, name, category, amount, currency, restaurant_id) VALUES
('d3333333-aaaa-bbbb-cccc-000000000001', 'Chicken Wrap', 'MAIN_COURSE', 18.00, 'PLN', '4a2a13c4-34be-4bef-82c3-a879c91bba5b'),
('d3333333-aaaa-bbbb-cccc-000000000002', 'Tomato Cream Soup', 'MAIN_COURSE', 11.00, 'PLN', '4a2a13c4-34be-4bef-82c3-a879c91bba5b'),
('d3333333-aaaa-bbbb-cccc-000000000003', 'Greek Salad', 'MAIN_COURSE', 14.50, 'PLN', '4a2a13c4-34be-4bef-82c3-a879c91bba5b');

INSERT INTO daily_menu_dishes (daily_menu_id, dish_id) VALUES
('c3333333-aaaa-bbbb-cccc-333333333333', 'd3333333-aaaa-bbbb-cccc-000000000001'),
('c3333333-aaaa-bbbb-cccc-333333333333', 'd3333333-aaaa-bbbb-cccc-000000000002'),
('c3333333-aaaa-bbbb-cccc-333333333333', 'd3333333-aaaa-bbbb-cccc-000000000003');

-- Bistro 11
INSERT INTO dish (id, name, category, amount, currency, restaurant_id) VALUES
('d4444444-aaaa-bbbb-cccc-000000000001', 'Pork Chop', 'MAIN_COURSE', 23.00, 'PLN', '76e4647b-cb56-460a-9f30-a6b3482fb93a'),
('d4444444-aaaa-bbbb-cccc-000000000002', 'Chicken Broth Soup', 'MAIN_COURSE', 9.00, 'PLN', '76e4647b-cb56-460a-9f30-a6b3482fb93a'),
('d4444444-aaaa-bbbb-cccc-000000000003', 'Boiled Potatoes', 'MAIN_COURSE', 6.00, 'PLN', '76e4647b-cb56-460a-9f30-a6b3482fb93a');

INSERT INTO daily_menu_dishes (daily_menu_id, dish_id) VALUES
('c4444444-aaaa-bbbb-cccc-444444444444', 'd4444444-aaaa-bbbb-cccc-000000000001'),
('c4444444-aaaa-bbbb-cccc-444444444444', 'd4444444-aaaa-bbbb-cccc-000000000002'),
('c4444444-aaaa-bbbb-cccc-444444444444', 'd4444444-aaaa-bbbb-cccc-000000000003');

-- Neon Bistro
INSERT INTO dish (id, name, category, amount, currency, restaurant_id) VALUES
('d5555555-aaaa-bbbb-cccc-000000000001', 'Caesar Salad', 'MAIN_COURSE', 21.00, 'PLN', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866'),
('d5555555-aaaa-bbbb-cccc-000000000002', 'Broccoli Cream Soup', 'MAIN_COURSE', 11.00, 'PLN', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866'),
('d5555555-aaaa-bbbb-cccc-000000000003', 'Tuna Roll', 'MAIN_COURSE', 17.50, 'PLN', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866');

INSERT INTO daily_menu_dishes (daily_menu_id, dish_id) VALUES
('c5555555-aaaa-bbbb-cccc-555555555555', 'd5555555-aaaa-bbbb-cccc-000000000001'),
('c5555555-aaaa-bbbb-cccc-555555555555', 'd5555555-aaaa-bbbb-cccc-000000000002'),
('c5555555-aaaa-bbbb-cccc-555555555555', 'd5555555-aaaa-bbbb-cccc-000000000003');

------------------------------------------------------------
-- VENDING MACHINES' LOCATIONS (might require more detailed in person mapping)
------------------------------------------------------------

INSERT INTO location (id, latitude, longitude)
VALUES
    ('550e8400-e29b-41d4-a716-446655440006', 50.03085967660663, 19.9076330797032),
    ('550e8400-e29b-41d4-a716-446655440007', 50.0292711417798, 19.905728510392944),
    ('550e8400-e29b-41d4-a716-446655440008', 50.0299355043595, 19.907602360960713);

------------------------------------------------------------
-- VENDING MACHINES' INFO
------------------------------------------------------------

INSERT INTO vending_machine (id, description, photo_path, location_id, type)
VALUES
    ('c3b6a5ef-9f40-5d4f-b62f-9d638df8f2b2', 'Snack vending machine with chips, cookies, and candy bars.', '/images/vending/snacks_01.jpg', '550e8400-e29b-41d4-a716-446655440003', 'SNACKS'),
    ('d4c7b6f0-0a51-6e5f-c73f-0e749ef9f3c3', 'Coffee machine with espresso, cappuccino, and hot chocolate.', '/images/vending/coffee_01.jpg', '550e8400-e29b-41d4-a716-446655440004', 'COFFEE'),
    ('e5d8c7f1-1b62-7f6f-d84f-1f850ff0f4d4', 'Lunch vending machine with sandwiches, salads, and wraps.', '/images/vending/lunch_01.jpg', '550e8400-e29b-41d4-a716-446655440005', 'LUNCH');

------------------------------------------------------------
-- KEYCLOAK
------------------------------------------------------------

INSERT INTO keycloak_user (id)
VALUES (
    'e8efd725-8397-4aea-979f-3c096470d23b'  -- This is subject from JWT Keycloak, with owner role!!!
);

------------------------------------------------------------
-- RESTAURANT OWNERS' - USERS
------------------------------------------------------------

INSERT INTO restaurant_owners (restaurant_id, user_id)
VALUES (
    'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1',  -- ID of Bistro Świetlica restaurant
    'e8efd725-8397-4aea-979f-3c096470d23b'  -- ID of owner from users
);