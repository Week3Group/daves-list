package com.groupassignment2.daveslist;

import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room,Long> {
    Iterable <Room> findAllByListTypeContainingIgnoreCase(String listType);
}
