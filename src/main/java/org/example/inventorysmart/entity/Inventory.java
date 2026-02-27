package org.example.inventorysmart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Inventory {

    @Id
    Long productId;

    Integer availableQuantity;

    Integer reservedQuantity;

    //tranh hien tuong 2 ng click cung luc
    @Version
    Long version;
}
