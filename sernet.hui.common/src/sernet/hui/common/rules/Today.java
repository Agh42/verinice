/*******************************************************************************
 * Copyright (c) 2009 Alexander Koderman <ak[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Alexander Koderman <ak[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.hui.common.rules;

import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;

public class Today implements IFillRule {

    private int changeField = 0;

    private int timeDifference = 0;

    private static Map<String, Integer> calendarFields = Map.of("YEAR", Calendar.YEAR, //$NON-NLS-1$
            "HOUR", Calendar.HOUR, //$NON-NLS-1$
            "MINUTE", Calendar.MINUTE, //$NON-NLS-1$
            "SECOND", Calendar.SECOND, //$NON-NLS-1$
            "MONTH", Calendar.MONTH, //$NON-NLS-1$
            "DAY", Calendar.DAY_OF_MONTH); //$NON-NLS-1$

    public String getValue() {
        if (timeDifference == 0) {
            return Long.toString(System.currentTimeMillis());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(changeField, timeDifference);
        return String.valueOf(calendar.getTimeInMillis());
    }

    public void init(String[] params) {
        try {
            if (params != null && params.length == 2) {
                changeField = calendarFields.get(params[0]);
                timeDifference = Integer.parseInt(params[1]);
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).debug(e);
        }

    }

    public boolean isMultiLanguage() {
        return false;
    }

}