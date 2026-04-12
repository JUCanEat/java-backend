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
