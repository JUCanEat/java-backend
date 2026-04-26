package com.backend.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TagValue value;

    public TagType getTagType() {
        return value.getType();
    }

    public String getName() {
        return value.name();
    }


    @Getter
    public enum TagValue {
        // Cuisine
        ITALIAN(TagType.CUISINE),
        POLISH(TagType.CUISINE),
        ASIAN(TagType.CUISINE),
        FAST_FOOD(TagType.CUISINE),

        // Allergen
        NUTS(TagType.ALLERGEN),
        GLUTEN(TagType.ALLERGEN),
        LACTOSE(TagType.ALLERGEN),

        // Dietary
        VEGAN(TagType.DIETARY),
        VEGETARIAN(TagType.DIETARY);

        private final TagType type;

        TagValue(TagType type) {
            this.type = type;
        }
    }

    public enum TagType {
        CUISINE,
        ALLERGEN,
        DIETARY
    }
}

