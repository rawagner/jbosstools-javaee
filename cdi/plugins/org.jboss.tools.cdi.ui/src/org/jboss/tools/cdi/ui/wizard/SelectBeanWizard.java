/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import java.util.ArrayList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SearchPattern;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.internal.core.refactoring.ValuedQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class SelectBeanWizard extends AbstractModifyInjectionPointWizard{
	private SelectBeanWizardPage selectBeanPage;
	private AddQualifiersToBeanWizardPage addQualifiersPage;
	private Text pattern;
	
	public SelectBeanWizard(ProcessorBasedRefactoring refactoring){
		super(refactoring);
		setWindowTitle(CDIUIMessages.SELECT_BEAN_WIZARD_TITLE);
		
		setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
	}
	
    @Override
	public void addUserInputPages() {
    	selectBeanPage = new SelectBeanWizardPage("");
    	addPage(selectBeanPage);
    	addQualifiersPage = new AddQualifiersToBeanWizardPage("");
    	addPage(addQualifiersPage);
    }
    
	public java.util.List<ValuedQualifier> getDeployedQualifiers(){
		return addQualifiersPage.getDeployedQualifiers();
	}
	
	public java.util.List<IQualifier> getAvailableQualifiers(){
		return addQualifiersPage.getAvailableQualifiers();
	}
	
	public void init(){
		addQualifiersPage.init();
	}
	
	public void deploy(ValuedQualifier qualifier){
		addQualifiersPage.deploy(qualifier);
	}
	
	public boolean checkBeans(){
		return addQualifiersPage.checkBeans();
	}
	
	class SelectBeanWizardPage extends UserInputWizardPage{
		TableViewer tableViewer;
		protected SelectBeanWizardPage(String pageName) {
			super(pageName);
			setTitle(CDIUIMessages.SELECT_BEAN_TITLE);
			setPageComplete(false);
		}

		@Override
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.horizontalSpacing = 4;
			layout.verticalSpacing = 10;
			layout.numColumns = 1;
			composite.setLayout(layout);
			composite.setFont(composite.getParent().getFont());
			
			Label label = new Label(composite, SWT.NONE);
			label.setText(CDIUIMessages.SELECT_BEAN_WIZARD_ENTER_BEAN_NAME);
			
			pattern = new Text(composite, SWT.BORDER);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			pattern.setLayoutData(data);
			pattern.addModifyListener(new ModifyListener(){
				@Override
				public void modifyText(ModifyEvent e){
					tableViewer.refresh();
				}
			});
			
			label = new Label(composite, SWT.NONE);
			label.setText(CDIUIMessages.SELECT_BEAN_WIZARD_SELECT_BEAN);
			
			Table availableList = new Table(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			data = new GridData(GridData.FILL_BOTH);
			data.heightHint = 200;
			data.widthHint = 150;
			availableList.setLayoutData(data);
			
			tableViewer = new TableViewer(availableList);
			final ILabelProvider labelProvider = new BeanListLabelProvider();
			tableViewer.setLabelProvider(labelProvider);
			IContentProvider contentProvider = new BeanListContentProvider();
			tableViewer.setContentProvider(contentProvider);
			tableViewer.setComparator(new ViewerComparator() {
				@Override
				public int compare(Viewer viewer, Object o1, Object o2) {
					if (o1 instanceof IBean && o2 instanceof IBean) {
						IBean b1 = (IBean) o1;
						IBean b2 = (IBean) o2;
						
						return labelProvider.getText(b1).compareToIgnoreCase(labelProvider.getText(b2));
					}
					
					return super.compare(viewer, o1, o2);
				}
			});
			tableViewer.setInput(getBeans());
			
			tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IBean bean = getSelection();
					if(bean != null){
						setSelectedBean(bean);
						addQualifiersPage.init();
						addQualifiersPage.setDeployedQualifiers(addQualifiersPage.getDeployedQualifiers());
						setPageComplete(true);
					}else
						setPageComplete(false);
				}
			});
			tableViewer.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					
				}
			});
			tableViewer.addFilter(new BeanFilter());
			
			setControl(composite);
		}
		
		public void setDefaultSelection(){
			
			if(getBeans().size() > 0){
				IBean defaultBean = (IBean)tableViewer.getTable().getItem(0).getData();
				tableViewer.setSelection(new StructuredSelection(defaultBean));
				tableViewer.getTable().select(0);
				setSelectedBean(defaultBean);
				addQualifiersPage.init();
				addQualifiersPage.setDeployedQualifiers(addQualifiersPage.getDeployedQualifiers());
				setPageComplete(true);
			}
			
		}
		
		protected IBean getSelection() {
			IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
			if (sel.isEmpty())
				return null;
				
			return (IBean)sel.getFirstElement();
		}
	}
	
	public class BeanFilter extends ViewerFilter {
		SearchPattern patternMatcher = new SearchPattern();
		public boolean isConsistentItem(Object item) {
			return true;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement,
	            Object element) {
			
			if (element instanceof IBean) {
				String beanTypeName = ((IBean)element).getBeanClass().getFullyQualifiedName();
				if(pattern.getText().isEmpty())
					patternMatcher.setPattern("*");
				else
					patternMatcher.setPattern(pattern.getText());
				boolean result = patternMatcher.matches(beanTypeName);
				if (!result) {
					String pattern = patternMatcher.getPattern();
					if (pattern.indexOf(".") < 0) {
						int lastIndex = beanTypeName.lastIndexOf(".");
						if (lastIndex >= 0
								&& (lastIndex + 1) < beanTypeName.length())
							return patternMatcher.matches(beanTypeName.substring(lastIndex + 1));
					}
				}
				return result;
			}
			return false;
		}
	}
	
	class BeanListLabelProvider implements ILabelProvider{

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			if(element instanceof IBean){
				return CDIImages.getImageByElement((IBean)element);
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if(element instanceof IBean){
				IBean bean = (IBean)element;
				String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
				String beanPackage = beanTypeName.substring(0,beanTypeName.lastIndexOf(CDIMarkerResolutionUtils.DOT));
				String name = bean.getElementName();

				return name+" - "+beanPackage;
			}
			return "";
		}
		
	}
	
	class BeanListContentProvider implements IStructuredContentProvider{

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ArrayList){
				return ((ArrayList)inputElement).toArray();
			}
			return new Object[]{};
		}
		
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		selectBeanPage.setDefaultSelection();
	}
}
