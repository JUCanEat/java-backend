------------------------------------------------------------
-- RESTAURANTS AND COORDINATES
------------------------------------------------------------

INSERT INTO location (id, latitude, longitude)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 50.030511999856294, 19.90718833360244),
    ('550e8400-e29b-41d4-a716-446655440002', 50.030170829430276, 19.908160572569297),
    ('550e8400-e29b-41d4-a716-446655440003', 50.027354360288484, 19.900980407813968),
    ('550e8400-e29b-41d4-a716-446655440004', 50.03000205209573, 19.905736283744663),
    ('550e8400-e29b-41d4-a716-446655440005', 50.02902509906889, 19.907067338269396)
ON CONFLICT (id) DO NOTHING;

INSERT INTO facility (id, description, photo_path, location_id, facility_type, name)
VALUES
    ('b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', '', '/images/restaurants/bistro_swietlica.jpg', '550e8400-e29b-41d4-a716-446655440001', 'RESTAURANT', 'Bistro Świetlica'),
    ('e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', '', '/images/restaurants/bistro_by_jelonek.jpg', '550e8400-e29b-41d4-a716-446655440002', 'RESTAURANT', 'Bistro by Jelonek'),
    ('4a2a13c4-34be-4bef-82c3-a879c91bba5b', '', '/images/restaurants/bistro4mat.jpg', '550e8400-e29b-41d4-a716-446655440003', 'RESTAURANT', 'Bistro4mat'),
    ('76e4647b-cb56-460a-9f30-a6b3482fb93a', '', '/images/restaurants/bistro_11.jpg', '550e8400-e29b-41d4-a716-446655440004', 'RESTAURANT', 'Bistro 11'),
    ('1cbbef85-cac6-4eb4-afd5-feb2f88e5866', '', '/images/restaurants/neon_bistro.jpg', '550e8400-e29b-41d4-a716-446655440005', 'RESTAURANT', 'Neon Bistro')
ON CONFLICT (id) DO NOTHING;

------------------------------------------------------------
-- OPENING HOURS
------------------------------------------------------------

INSERT INTO opening_hours (id, restaurant_id, day_of_week, open_time, close_time)
VALUES
    ('2a1b0f8d-2d74-48f8-a5e4-2e6dcd42d501', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'MONDAY', '9:00', '16:00'),
    ('4a3b6f9c-24c2-4d9f-bbc6-6718949b8af9', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'TUESDAY', '9:00', '16:00'),
    ('7b4a3a2d-8a1b-478f-96b2-3c3b22f2ad40', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'WEDNESDAY', '9:00', '16:00'),
    ('c3d5d2ae-a634-4fb2-a548-b1b65aa9c03f', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'THURSDAY', '9:00', '16:00'),
    ('6d884176-31ea-451f-91c7-81a89d484611', 'b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1', 'FRIDAY', '9:00', '16:00'),
    ('9f2b3a12-6d45-4a21-83b2-0a8b3b5f5f77', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'MONDAY', '7:00', '17:00'),
    ('fa484b16-2651-49c4-99db-e6e5f8e34e46', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'TUESDAY', '7:00', '17:00'),
    ('e96b5d50-562e-48cf-93a0-cd3b1144d186', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'WEDNESDAY', '7:00', '17:00'),
    ('b5c4d3f2-946c-4007-9e87-91d86c40a07b', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'THURSDAY', '7:00', '17:00'),
    ('a17b9c43-34ad-4fb1-9c2e-56e58e0b64e1', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'FRIDAY', '7:00', '17:00'),
    ('cf98d8c5-3555-44e5-8dfd-5cbdb9c22f2b', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'SATURDAY', '7:00', '17:00'),
    ('a3ac00c2-ab4a-42ff-b914-a636629afbbb', 'e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8', 'SUNDAY', '7:00', '17:00'),
    ('57ae2be7-0b21-4108-bdee-803e29d6dee4', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'MONDAY', '9:00', '15:30'),
    ('83bea995-3313-4b0f-b237-18ea7d5b5644', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'TUESDAY', '9:00', '15:30'),
    ('a65e28fb-470b-4d6d-9ef5-fc31984dcd39', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'WEDNESDAY', '9:00', '15:30'),
    ('b9dc7047-128b-4854-acb6-7949ef0a7e8c', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'THURSDAY', '9:00', '15:30'),
    ('18840544-29da-45e7-a531-b99c8e059263', '4a2a13c4-34be-4bef-82c3-a879c91bba5b', 'FRIDAY', '9:00', '15:30'),
    ('da3b1bbc-c793-476a-bd6f-04b13ca4b47f', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'MONDAY', '8:00', '16:00'),
    ('9077ec6a-fecf-4b1d-8e2f-c1ef0260d4b6', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'TUESDAY', '8:00', '16:00'),
    ('a2ac6e3d-dfd2-492b-91f7-e631a952f256', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'WEDNESDAY', '8:00', '16:00'),
    ('1a6ade0c-a184-4028-93a4-2dd9d75f0420', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'THURSDAY', '8:00', '16:00'),
    ('e0ee6006-3a0f-4645-8fa1-2f533b72b18b', '76e4647b-cb56-460a-9f30-a6b3482fb93a', 'FRIDAY', '8:00', '16:00'),
    ('79120ac2-032b-4610-bc62-340e1dacd896', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'MONDAY', '8:00', '17:00'),
    ('c1e2dd8f-acf3-47a2-90a8-243523b96e3b', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'TUESDAY', '8:00', '17:00'),
    ('0fd40668-1834-4351-bf6f-040836e2b82e', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'WEDNESDAY', '8:00', '17:00'),
    ('47bbb917-4d97-4955-84f4-3dad31b81816', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'THURSDAY', '8:00', '17:00'),
    ('416845b4-ee0e-4cb0-897c-110d05bba7b3', '1cbbef85-cac6-4eb4-afd5-feb2f88e5866', 'FRIDAY', '8:00', '17:00')
ON CONFLICT (id) DO NOTHING;

------------------------------------------------------------
-- VENDING MACHINES
------------------------------------------------------------

INSERT INTO location (id, latitude, longitude)
VALUES
    ('550e8400-e29b-41d4-a716-446655440006', 50.03085967660663, 19.9076330797032),
    ('550e8400-e29b-41d4-a716-446655440007', 50.0292711417798, 19.905728510392944),
    ('550e8400-e29b-41d4-a716-446655440008', 50.0299355043595, 19.907602360960713)
ON CONFLICT (id) DO NOTHING;

INSERT INTO facility (id, description, photo_path, location_id, facility_type, type)
VALUES
    ('c3b6a5ef-9f40-5d4f-b62f-9d638df8f2b2', 'Snack vending machine with chips, cookies, and candy bars.', '/images/vending/snacks_01.jpg', '550e8400-e29b-41d4-a716-446655440006', 'VENDING_MACHINE', 'SNACKS'),
    ('d4c7b6f0-0a51-6e5f-c73f-0e749ef9f3c3', 'Coffee machine with espresso, cappuccino, and hot chocolate.', '/images/vending/coffee_01.jpg', '550e8400-e29b-41d4-a716-446655440007', 'VENDING_MACHINE', 'COFFEE'),
    ('e5d8c7f1-1b62-7f6f-d84f-1f850ff0f4d4', 'Lunch vending machine with sandwiches, salads, and wraps.', '/images/vending/lunch_01.jpg', '550e8400-e29b-41d4-a716-446655440008', 'VENDING_MACHINE', 'LUNCH')
ON CONFLICT (id) DO NOTHING;
