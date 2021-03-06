/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;

import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * Represents an initializer method (a method annotated @Inject)
 * Each parameter of this method is an injection point.
 * 
 * @author Alexey Kazakov
 */
public interface IInitializerMethod extends IBeanMethod {

	/**
	 * Returns the @Inject annotation declaration
	 * 
	 * @return the @Inject annotation declaration.
	 */
	IAnnotationDeclaration getInjectAnnotation();
}