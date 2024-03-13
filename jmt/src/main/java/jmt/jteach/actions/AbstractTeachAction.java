/**
 * Copyright (C) 2016, Laboratorio di Valutazione delle Prestazioni - Politecnico di Milano

* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
*/

package jmt.jteach.actions;

import jmt.framework.gui.listeners.AbstractJMTAction;
import jmt.gui.common.JMTImageLoader;
import jmt.jteach.MediatorTeach;

/**
 * Defines the Abstract action of this application, it is connected to the
 * MediatorTeach which has the responsibility to react to it.
 * It's the same class of the AbstractJmodelAction but with MediatorTeach

 * @author Lorenzo Torri
 * Date: 11-mar-2024
 * Time: 09.40

 */
public abstract class AbstractTeachAction extends AbstractJMTAction {

    private static final long serialVersionUID = 1L;
    protected MediatorTeach mediator;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public AbstractTeachAction(String name, MediatorTeach mediator) {
        this.setName(name);
        this.mediator = mediator;
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public AbstractTeachAction(String name, String iconName, MediatorTeach mediator) {
        this(name, mediator);
        this.setIcon(iconName, JMTImageLoader.getImageLoader());
    }
}

