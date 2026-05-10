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
import lombok.EqualsAndHashCode;
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
//@EqualsAndHashCode(of = "id")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_value", columnDefinition = "varchar(50)")
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
        ITALIAN(TagType.CUISINE, "Włoska", "Italian"),
        POLISH(TagType.CUISINE, "Polska", "Polish"),
        ASIAN(TagType.CUISINE, "Azjatycka", "Asian"),
        FAST_FOOD(TagType.CUISINE, "Fast food", "Fast food"),

        // Allergen
        NUTS(TagType.ALLERGEN, "Orzechy", "Nuts"),
        GLUTEN(TagType.ALLERGEN, "Gluten", "Gluten"),
        LACTOSE(TagType.ALLERGEN, "Laktoza", "Lactose"),
        SESAME(TagType.ALLERGEN, "Sezam", "Sesame"),

        // Dietary
        VEGAN(TagType.DIETARY, "Wegańskie", "Vegan"),
        VEGETARIAN(TagType.DIETARY, "Wegetariańskie", "Vegetarian");

        private final TagType type;
        private final String labelPl;
        private final String labelEn;

        TagValue(TagType type, String labelPl, String labelEn) {
            this.type = type;
            this.labelPl = labelPl;
            this.labelEn = labelEn;
        }

        public String getLabel(DishTranslation.Language language) {
            return switch (language) {
                case PL -> labelPl;
                case EN -> labelEn;
            };
        }
    }

    public enum TagType {
        CUISINE,
        ALLERGEN,
        DIETARY
    }
}

