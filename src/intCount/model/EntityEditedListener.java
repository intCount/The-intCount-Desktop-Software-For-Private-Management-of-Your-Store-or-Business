/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.util.EventListener;

/**
 *
 * @author 
 */
public interface EntityEditedListener extends EventListener {
    public void onEntityEdited(EntityEditedEvent event);
}
