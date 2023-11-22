/*******************************************************************************
 * Copyright (c) 2012 Daniel Murygin.
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.rcp;

import org.eclipse.core.commands.AbstractHandler;

import sernet.gs.ui.rcp.main.Activator;
import sernet.hui.common.VeriniceContext;
import sernet.springclient.RightsServiceClient;
import sernet.verinice.interfaces.IInternalServerStartListener;
import sernet.verinice.interfaces.RightEnabledUserInteraction;

/**
 * Handler extending this class are conected to the rights service. Subclasses
 * must implement getRightsID.
 * 
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public abstract class RightsEnabledHandler extends AbstractHandler
        implements RightEnabledUserInteraction {

    protected RightsEnabledHandler() {
        this(true);
    }

    protected RightsEnabledHandler(boolean enable) {
        super();
        if (enable) {
            enableAccordingToUserRights();
        }
    }

    private void enableAccordingToUserRights() {
        if (Activator.getDefault().isStandalone()
                && !Activator.getDefault().getInternalServer().isRunning()) {
            IInternalServerStartListener listener = e -> {
                if (e.isStarted()) {
                    setBaseEnabled(checkRights());
                }
            };
            Activator.getDefault().getInternalServer().addInternalServerStatusListener(listener);
        } else {
            setBaseEnabled(checkRights());
        }
    }

    /*
     * @see sernet.verinice.interfaces.RightEnabledUserInteraction#checkRights()
     */
    @Override
    public boolean checkRights() {
        Activator.inheritVeriniceContextState();
        RightsServiceClient service = (RightsServiceClient) VeriniceContext
                .get(VeriniceContext.RIGHTS_SERVICE);
        return service.isEnabled(getRightID());
    }

}
