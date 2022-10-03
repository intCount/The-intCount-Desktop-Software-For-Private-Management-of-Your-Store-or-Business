/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

/**
 * @author
 */
public class EntityEditedEvent extends java.util.EventObject {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Object entityEdited;

    public EntityEditedEvent(Object source, Object entityEdited) {
        super(source);
        this.entityEdited = entityEdited;
    }

    public Object getEntityEdited() {
        return entityEdited;
    }

}
