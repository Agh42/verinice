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
 *     Robert Schuster <r.schuster@tarent.de> - configurablity through Spring
 ******************************************************************************/
package sernet.verinice.service.gstoolimport;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import sernet.verinice.interfaces.IBSIConfig;
import sernet.verinice.interfaces.IVeriniceConstants;

public class BSIConfigurationServer implements IBSIConfig {

    private static final Logger LOG = Logger.getLogger(BSIConfigurationServer.class);

    private Resource grundschutzKataloge;

    private URL getGrundschutzKatalogeURL() {
        try {
            return grundschutzKataloge.getURL();
        } catch (IOException e) {
            LOG.error("accessing the URL for the Grundschutz catalog failed.");
            throw new RuntimeException(e);
        }
    }

    public String getCacheDir() {
        String property = System.getProperty(IVeriniceConstants.JAVA_IO_TMPDIR) + File.separator
                + "gscache";
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting temp dir to " + property);
        }
        return property;
    }

    public String getGsPath() {
        return getGrundschutzKatalogeURL().toString();
    }

    public boolean isFromZipFile() {
        try {
            return grundschutzKataloge.getFile().isFile();
        } catch (IOException e) {
            LOG.error("Error in isFromZipFile(), returning false", e);
            return false;
        }
    }

    public void setGrundschutzKataloge(Resource grundschutzKataloge) {
        this.grundschutzKataloge = grundschutzKataloge;
    }

    public Resource getGrundschutzKataloge() {
        return grundschutzKataloge;
    }

}
