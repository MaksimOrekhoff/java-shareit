package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " + "where (upper(i.name) like upper(concat('%', ?1, '%')) " + "   " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available is true ")
    List<Item> search(String text);

    List<Item> findAllByUserId(Long id, PageRequest pageRequest);

    @Query(value = "SELECT i FROM Item AS i " +
            "WHERE i.available IS TRUE AND :text <> '' " +
            "AND (upper(i.name) LIKE concat('%', upper(:text), '%') " +
            "OR upper(i.description) LIKE concat('%', upper(:text), '%'))")
    List<Item> searchItem(@Param("text") String text, Pageable pageable);
}
