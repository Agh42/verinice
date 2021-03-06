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
package sernet.verinice.service.parser;

import java.util.List;

import org.apache.log4j.Logger;

import sernet.gs.model.Baustein;
import sernet.gs.service.RuntimeCommandException;
import sernet.verinice.interfaces.GenericCommand;
import sernet.verinice.interfaces.INoAccessControl;
import sernet.verinice.interfaces.IProgress;

@SuppressWarnings("serial")
public class LoadBausteine extends GenericCommand implements INoAccessControl {

    private static final Logger log = Logger.getLogger(LoadBausteine.class);

    private List<Baustein> bausteine;

    public void execute() {
        try {
            bausteine = GSScraperUtil.getInstance().getModel().loadBausteine(new IProgress() {

                public void beginTask(String name, int totalWork) {
                    // empty
                }
                public void done() {
                    // empty
                }
                public void setTaskName(String string) {
                    // empty
                }
                public void subTask(String string) {
                    // empty

                }
                public void worked(int work) {
                    // empty
                }
            });
        } catch (Exception e) {
            log.error("Error while loading bausteine: ", e);
            throw new RuntimeCommandException(e);
        }
    }

    public List<Baustein> getBausteine() {
        return bausteine;
    }

}
