/*******************************************************************************
 * Copyright (c) 2010 Daniel Murygin.
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
package sernet.verinice.samt.rcp;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import sernet.gs.service.VeriniceCharset;
import sernet.gs.ui.rcp.main.CnAWorkspace;

/**
 * Extension of {@link sernet.gs.ui.rcp.main.CnAWorkspace} from verinice core.
 * 
 * Copies the self-assessment CSV-file from this bundle jar to the workspace of
 * verinice.
 * 
 * @author Daniel Murygin <dm@sernet.de>
 */
public final class SamtWorkspace extends CnAWorkspace {

    private static final Logger LOG = Logger.getLogger(SamtWorkspace.class);

    private static volatile SamtWorkspace instance;

    /**
     * Class loader-relative path to the resources folder of this bundle without
     * a path separator in the beginning and in the end
     */
    public static final String RESOURCES_PATH = "resources"; //$NON-NLS-1$

    /**
     * Workspace-relative path to the verinice conf folder without a path
     * separator in the beginning and in the end
     */
    public static final String CONF_PATH = "conf"; //$NON-NLS-1$

    /**
     * File name of the self-assessment CSV catalog is externalized to resource
     * bundle so that the right file is used for each locale
     */
    public static final String SAMT_CATALOG_FILE_NAME = Messages.SamtWorkspace_0;
    // VN-2875
    public static final String SAMT_CATALOG_PREVIOUS_FILE_NAME = Messages.SamtCatalogPreviousFileName;

    private SamtWorkspace() {
        // use getInstance
    }

    public static SamtWorkspace getInstance() {
        if (instance == null) {
            instance = new SamtWorkspace();
        }
        return instance;
    }

    /**
     * Copies the self-assessment CSV-file from this bundle jar to the verinice
     * workspace.
     * 
     * This method is called on startup of the application. See
     * {@link Activator}.start().
     * 
     * @throws NullPointerException
     * @throws IOException
     */
    public synchronized void createSelfAssessmemtCatalog(String fileName) throws IOException {
        final String inputPath = RESOURCES_PATH + File.separatorChar + fileName;
        final String ouputPath = CONF_PATH + File.separatorChar + fileName;

        try {
            createTextFile(inputPath, VeriniceCharset.CHARSET_UTF_8, getWorkdir(), ouputPath, null);
        } catch (RuntimeException e) {
            LOG.error("Error while saving samt catalog file in conf dir. Input path: " + inputPath //$NON-NLS-1$
                    + ", output path: " + ouputPath, e); //$NON-NLS-1$
            throw e;
        } catch (Exception e) {
            LOG.error("Error while saving samt catalog file in conf dir. Input path: " + inputPath //$NON-NLS-1$
                    + ", output path: " + ouputPath, e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
    }
}
