package com.lenaevd.advertisements.exception;

import com.lenaevd.advertisements.model.EntityName;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(int id, EntityName entityName) {
        super("Couldn't find " + entityName.toString().toLowerCase() + " with id=" + id);
    }
}
