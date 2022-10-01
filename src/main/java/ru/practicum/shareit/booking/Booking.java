package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date_time")
    private LocalDateTime start;
    @Column(name = "end_date_time")
    private LocalDateTime end;
    @Column(name = "item_id")
    private Long itemId;
    @Column(name = "booker_id")
    private Long booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "approved")
    private StatusItem status;

}
