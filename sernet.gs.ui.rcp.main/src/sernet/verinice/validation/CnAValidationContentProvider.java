/*******************************************************************************
 * Copyright (c) 2012 Sebastian Hagedorn <sh@sernet.de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Sebastian Hagedorn <sh@sernet.de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.validation;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import sernet.gs.ui.rcp.main.common.model.PlaceHolder;
import sernet.verinice.model.bp.DefaultBpModelListener;
import sernet.verinice.model.bp.IBpModelListener;
import sernet.verinice.model.bp.elements.BpModel;
import sernet.verinice.model.bsi.BSIModel;
import sernet.verinice.model.bsi.IBSIModelListener;
import sernet.verinice.model.iso27k.IISO27KModelListener;
import sernet.verinice.model.iso27k.ISO27KModel;
import sernet.verinice.model.validation.CnAValidation;

/**
 *
 */
public class CnAValidationContentProvider extends DefaultBpModelListener implements
        IStructuredContentProvider, IBSIModelListener, IISO27KModelListener, IBpModelListener {

    CnAValidationView validationView;

    TableViewer viewer;

    public CnAValidationContentProvider(CnAValidationView view) {
        this.validationView = view;
    }

    /*
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // empty
    }

    /*
     * @see
     * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
     * .viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        this.viewer = (TableViewer) v;
        // validationView.setCurrentCnaElement((CnATreeElement)newInput);
        viewer.refresh();
    }

    /*
     * @see
     * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.
     * lang.Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof PlaceHolder) {
            return new Object[] { inputElement };
        }
        List<CnAValidation> validationList = (List<CnAValidation>) inputElement;
        return validationList.toArray(new Object[validationList.size()]);
    }

    /*
     * @see
     * sernet.verinice.model.iso27k.IISO27KModelListener#modelReload(sernet.
     * verinice.model.iso27k.ISO27KModel)
     */
    @Override
    public void modelReload(ISO27KModel newModel) {
        Display.getDefault().asyncExec(() -> viewer.refresh());
    }

    /*
     * @see sernet.verinice.model.bsi.IBSIModelListener#modelRefresh()
     */
    @Override
    public void modelRefresh() {
        Display.getDefault().asyncExec(() -> viewer.refresh());
    }

    /*
     * @see sernet.verinice.model.bsi.IBSIModelListener#modelRefresh(java.lang.
     * Object)
     */
    @Override
    public void modelRefresh(Object source) {
        Display.getDefault().asyncExec(() -> viewer.refresh());
    }

    /*
     * @see
     * sernet.verinice.model.bsi.IBSIModelListener#modelReload(sernet.verinice.
     * model.bsi.BSIModel)
     */
    @Override
    public void modelReload(BSIModel newModel) {
        viewer.refresh();
    }

    @Override
    public void validationAdded(Integer scopeId) {
        validationView.reloadAll();
    }

    @Override
    public void validationRemoved(Integer scopeId) {
        validationView.reloadAll();
    }

    @Override
    public void validationChanged(CnAValidation oldValidation, CnAValidation newValidation) {
        validationView.reloadAll();
    }

    @Override
    public void modelReload(BpModel newModel) {
        Display.getDefault().asyncExec(() -> viewer.refresh());
    }
}
