/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.test.international;

import java.util.Set;

import org.jboss.tools.cdi.seam.core.international.BundleModelFactory;
import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.cdi.seam.core.international.IBundleModel;
import org.jboss.tools.cdi.seam.core.international.ILocalizedValue;
import org.jboss.tools.cdi.seam.core.international.IProperty;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.cdi.seam.solder.core.test.GenericBeanValidationTest;

public class BundleModelTest extends SeamCoreTest {

	public void testBundleModel() throws Exception {
		IBundleModel bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(bundleModel);

		Set<String> bundles = bundleModel.getAllAvailableBundles();
		assertTrue(bundles.contains("messages"));

		IBundle bundle = bundleModel.getBundle("messages");
		assertNotNull(bundle);

		IProperty property = bundle.getProperty("home_header1");
		assertNotNull(property);

		ILocalizedValue value = property.getValue("de");
		assertNotNull(value);
		assertEquals("Über dieses Beispiel-Anwendung", value.getValue());
		
		value = property.getValue();
		assertNotNull(value);
		assertEquals("About this example application", value.getValue());
	}

	public void testIncrementalBuildAtJarModification() throws Exception {
		IBundleModel bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(bundleModel);

		Set<String> bundles = bundleModel.getAllAvailableBundles();
		assertTrue(bundles.contains("test1.xxm"));

		String path = "WebContent/WEB-INF/lib/a.jar";
		String empty = "WebContent/WEB-INF/lib/a.empty";
		
		GenericBeanValidationTest.writeFile(getTestProject(), empty, path);

		bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(bundleModel);
		bundles = bundleModel.getAllAvailableBundles();
		assertFalse(bundles.contains("test1.xxm"));
	}

	public void testIncrementalBuildAtAddRemoveExtension() throws Exception {
		IBundleModel originalBundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(originalBundleModel);

		Set<String> originalBundles = originalBundleModel.getAllAvailableBundles();
		assertFalse(originalBundles.isEmpty());

		String path = "WebContent/WEB-INF/lib/seam-international-empty.jar";
		String original = "WebContent/WEB-INF/lib/seam-international-empty.original";

		GenericBeanValidationTest.writeFile(getTestProject(), path, original);
		GenericBeanValidationTest.removeFile(getTestProject(), path);
		
		IBundleModel bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNull(bundleModel);

		GenericBeanValidationTest.writeFile(getTestProject(), original, path);
		GenericBeanValidationTest.removeFile(getTestProject(), original);
		bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(bundleModel);
		Set<String> bundles = bundleModel.getAllAvailableBundles();
		for (String string : originalBundles) {
			assertTrue(bundles.contains(string));
		}
	}

}