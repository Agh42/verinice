/*******************************************************************************
 * Copyright (c) 2020 Finn Westendorf
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
 ******************************************************************************/
package sernet.verinice.bp.rcp.consolidator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import sernet.verinice.model.bp.elements.BpRequirement;
import sernet.verinice.model.bp.elements.BpThreat;
import sernet.verinice.model.bp.elements.Safeguard;

/**
 * This is a page of the consolidator to select which data to consolidate.
 * <p>
 * This collects a list of property group ids. To represent the general info
 * that isn't in any group, it uses the pseudo-IDs *_general.
 */
public class DataSelectionPage extends WizardPage {
    private static final String PROP_GRP_BP_REQUIREMENT_GROUP_GENERAL = "bp_requirement_group_general"; //$NON-NLS-1$
    private static final String PROP_GRP_BP_REQUIREMENT_GENERAL = "bp_requirement_general"; //$NON-NLS-1$
    private static final String PROP_GRP_BP_SAFEGUARD_GENERAL = "bp_safeguard_general"; //$NON-NLS-1$
    private static final String PROP_GRP_BP_THREAT_GENERAL = "bp_threat_general"; //$NON-NLS-1$

    ConsolidatorWizard wizard;
    Composite composite;

    OptionListener selectionListener = new OptionListener();

    private final class OptionListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Button b = (Button) e.getSource();
            String option = (String) b.getData();
            if (b.getSelection()) {
                wizard.getSelectedPropertyGroups().add(option);
            } else {
                wizard.getSelectedPropertyGroups().remove(option);
            }
        }
    }

    public DataSelectionPage(@NonNull ConsolidatorWizard wizard) {
        super("wizardPage");
        setTitle(Messages.dataSelection);
        setDescription(Messages.selectTheDataToBeConsolidated);
        this.wizard = wizard;
    }

    private Group createGroup(String title) {
        Group g = new Group(composite, SWT.NONE);
        g.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        g.setText(title);
        g.setLayout(new GridLayout(1, false));
        return g;
    }

    private void createCheckbox(Group group, String title, String propertyId, boolean selected) {
        Button b = new Button(group, SWT.CHECK);
        b.addSelectionListener(selectionListener);
        b.setText(title);
        b.setData(propertyId);
        if (selected) {
            b.setSelection(true);
            wizard.getSelectedPropertyGroups().add(propertyId);
        }
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        setControl(container);
        container.setLayout(new GridLayout(1, false));

        ScrolledComposite scrolledComposite = new ScrolledComposite(container,
                SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        Group moduleGrp = createGroup(Messages.module);
        createCheckbox(moduleGrp, Messages.general, PROP_GRP_BP_REQUIREMENT_GROUP_GENERAL, true);

        Group requirementGrp = createGroup(Messages.requirement);
        createCheckbox(requirementGrp, Messages.general, PROP_GRP_BP_REQUIREMENT_GENERAL, true);
        createCheckbox(requirementGrp, Messages.implementation,
                BpRequirement.PROP_GRP_IMPLEMENTATION, true);
        createCheckbox(requirementGrp, Messages.costs, BpRequirement.PROP_GRP_KOSTEN, false);
        createCheckbox(requirementGrp, Messages.dataProtection,
                BpRequirement.PROP_GRP_DATA_PROTECTION_OBJECTIVES_EUGDPR, false);
        createCheckbox(requirementGrp, Messages.kix, BpRequirement.PROP_GRP_KIX, false);
        createCheckbox(requirementGrp, Messages.audit, BpRequirement.PROP_GRP_AUDIT, false);
        createCheckbox(requirementGrp, Messages.revision, BpRequirement.PROP_GRP_REVISION, false);

        Group threatGrp = createGroup(Messages.threat);
        createCheckbox(threatGrp, Messages.general, PROP_GRP_BP_THREAT_GENERAL, true);
        createCheckbox(threatGrp, Messages.riskWithout, BpThreat.PROP_GRP_RISK_WITHOUT_SAFEGUARDS,
                false);
        createCheckbox(threatGrp, Messages.riskWithoudAdditional,
                BpThreat.PROP_GRP_RISK_WITHOUT_ADDITIONAL_SAFEGUARDS, true);
        createCheckbox(threatGrp, Messages.riskTreatment,
                BpThreat.PROP_GRP_RISK_TREATMENT_OPTION_GROUP, true);
        createCheckbox(threatGrp, Messages.riskWithAdditional,
                BpThreat.PROP_GRP_RISK_WITH_ADDITIONAL_SAFEGUARDS, true);

        Group safeguardGrp = createGroup(Messages.safeguard);
        createCheckbox(safeguardGrp, Messages.general, PROP_GRP_BP_SAFEGUARD_GENERAL, true);
        createCheckbox(safeguardGrp, Messages.implementation, Safeguard.PROP_GRP_IMPLEMENTATION,
                true);
        createCheckbox(safeguardGrp, Messages.costs, Safeguard.PROP_GRP_GROUP_KOSTEN, false);
        createCheckbox(safeguardGrp, Messages.dataProtection,
                Safeguard.PROP_GRP_DATA_PROTECTION_OBJECTIVES_EUGDPR, false);
        createCheckbox(safeguardGrp, Messages.kix, Safeguard.PROP_GRP_KIX, false);
        createCheckbox(safeguardGrp, Messages.revision, Safeguard.PROP_GRP_REVISION, false);

        scrolledComposite.setContent(composite);
        scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
}