package com.example.tracker.service.interfaces;

import com.example.tracker.exceptions.ElementNotFoundException;

import java.util.List;

public interface CrudService<T,ID>{
    List<T> findAll();

    T findById(ID id) throws ElementNotFoundException;

    T save(T object);

    T update(T object) throws ElementNotFoundException;

    void delete(ID id) throws ElementNotFoundException;
}
