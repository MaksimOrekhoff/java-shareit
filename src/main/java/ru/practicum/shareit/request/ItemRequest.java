package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item_request", schema = "public")
public class ItemRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "description")
    private String description;
    @Column(name = "requester_id")
    private long requester;
    @Column(name = "created")
    private LocalDateTime created;
}
