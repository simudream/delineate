/*
 * Command.java
 *
 * Copyright (C) 2003 Robert McKinnon
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sf.delineate.command;

import net.sf.delineate.command.Parameter;

import java.util.Arrays;

/**
 * Represents Autotrace command.
 * @author robmckinnon@users.sourceforge.net
 */
public class Command {

    private CommandChangeListener changeListener;
    private Parameter[] parameters;
    int parameterCount = 0;

    public Command(int totalParameterCount, CommandChangeListener listener) {
        parameters = new Parameter[totalParameterCount];
        changeListener = listener;
    }

    public void addParameter(String name, boolean enabled, String value) {
        if(parameterCount == parameters.length) {
            throw new IllegalStateException("Command can only hold " + parameters.length + " parameters.");
        }

        Parameter parameter = new Parameter(name, enabled, value);
        parameters[parameterCount] = parameter;
        parameterCount++;

        if(parameterCount == parameters.length) {
            Arrays.sort(parameters);
            changeListener.commandChanged(getCommand());
        }
    }

    public void setParameterEnabled(String name, boolean enabled) {
        Parameter parameter = getParameter(name);

        if(parameter.enabled != enabled) {
            parameter.enabled = enabled;
            changeListener.commandChanged(getCommand());
        }
    }

    public void setParameterValue(String name, String value) {
        Parameter parameter = getParameter(name);

        if(!parameter.value.equals(value)) {
            parameter.value = value;
            changeListener.commandChanged(getCommand());
        }
    }

    private String getCommand() {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < parameters.length; buffer.append(parameters[i++].paramValue())) ;
        String command = "autotrace " + buffer.toString();
        return command;
    }

    private Parameter getParameter(String name) {
        if(name.equals("input-file")) {
            return parameters[parameters.length -1];
        } else {
            int index = Arrays.binarySearch(parameters, name);
            Parameter parameter = parameters[index];
            return parameter;
        }
    }

    public String getParameterValue(String name) {
        return getParameter(name).value;
    }

    /**
     * For listening to command changes.
     */
    public interface CommandChangeListener {
        void commandChanged(String commandText);
    }

}
