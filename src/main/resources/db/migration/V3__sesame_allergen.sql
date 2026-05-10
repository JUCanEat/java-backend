ALTER TABLE dish_allergens
    DROP CONSTRAINT IF EXISTS dish_allergens_allergen_check;

ALTER TABLE dish_allergens
    ADD CONSTRAINT dish_allergens_allergen_check
    CHECK (allergen IN ('NUTS', 'GLUTEN', 'MEAT', 'LACTOSE', 'SESAME'));