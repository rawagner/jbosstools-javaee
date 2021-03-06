/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.vpe.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.vpe.base.test.VpeTestSetup;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Class for testing all Seam components
 * 
 * @author dsakovich@exadel.com
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	SeamAllImportantTests.class,
	SeamComponentContentTest.class
})
public class SeamAllTests {
	public static final String IMPORT_PROJECT_NAME = "SeamTest"; //$NON-NLS-1$
}
