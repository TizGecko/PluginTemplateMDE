package templatemde.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.Bundle;

import templatemde.epsilon.EpsilonFiles;

public abstract class ConcreteHandler extends AbstractHandler {

	protected static final String DIALOGS_TITLE = Messages.ConcreteHandler_0;  

	protected IWorkbenchWindow window;
	private IWorkbenchPage activePage;

	protected IProject theProject;
	private IResource theResource;
	private IFile theFile;

	private String workspaceName;
	private String projectName;
	private String fileName;
	private String filePath;

	/**
	 * Get Path of a Resource File
	 * 
	 * @param path
	 * @return
	 */
	protected String getResFile(String path) {
		Bundle bundle = Platform.getBundle(EpsilonFiles.BUNDLE_NAME);
		URL url = FileLocator.find(bundle, new Path(path), null);
		File file = null;
		try {
			url = FileLocator.toFileURL(url);
			file = URIUtil.toFile(URIUtil.toURI(url));
		} catch (URISyntaxException e1) {
			MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, Messages.ConcreteHandler_1 + url.toString());
			e1.printStackTrace();
		} catch (IOException e1) {
			MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, Messages.ConcreteHandler_2);
			e1.printStackTrace();
		}
		return file.getAbsolutePath();
	}

	/**
	 * Copy all required files in temp directory
	 * 
	 * @param files
	 */
	protected void loadResFiles(EpsilonFiles files) {
		for (String folder : files.keySet())
			for (String file : files.get(folder))
				getResFile(folder + file);
	}

	/**
	 * Get Path of a Target File
	 * 
	 * @return
	 */
	protected String getTargetFile() {
		return this.workspaceName + this.filePath;
	}

	/**
	 * Get Working directory for Generated Sources
	 * 
	 * @return
	 */
	protected String getWorkDir() {
		return this.workspaceName + "/" + this.projectName;
	}

	/**
	 * Get Workspace Path
	 * 
	 * @return
	 */
	protected String getWorkspaceName() {
		return workspaceName;
	}

	/**
	 * Get Project Name
	 * 
	 * @return
	 */
	protected String getProjectName() {
		return projectName;
	}

	/**
	 * Get Target File Name
	 * 
	 * @return
	 */
	protected String getFileName() {
		return fileName;
	}

	protected boolean extractProjectAndFileFromInitiatingEvent(ExecutionEvent event) {
		// ============================================================================================================
		// The execute method of the handler is invoked to handle the event. As we only
		// contribute to Explorer
		// Navigator views we expect to get a selection tree event
		// ============================================================================================================

		// Get the active WorkbenchPage
		this.activePage = this.window.getActivePage();

		// Get the Selection from the active WorkbenchPage page
		ISelection selection = this.activePage.getSelection();
		if (selection instanceof ITreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			try {
				TreePath[] treePaths = treeSelection.getPaths();
				TreePath treePath = treePaths[0];

				// The TreePath contains a series of segments in our usage:
				// o The first segment is usually a project
				// o The last segment generally refers to the file

				// The first segment should be a IProject
				Object firstSegmentObj = treePath.getFirstSegment();
				this.theProject = ((IAdaptable) firstSegmentObj).getAdapter(IProject.class);
				if (this.theProject == null) {
					MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, getClassHierarchyAsMsg(
							Messages.ConcreteHandler_5, "Make sure to directly select a file.", firstSegmentObj));   //$NON-NLS-2$
					return false;
				}

				// The last segment should be an IResource
				Object lastSegmentObj = treePath.getLastSegment();
				this.theResource = ((IAdaptable) lastSegmentObj).getAdapter(IResource.class);
				if (this.theResource == null) {
					MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, getClassHierarchyAsMsg(
							Messages.ConcreteHandler_6, "Make sure to directly select a file.", firstSegmentObj));   //$NON-NLS-2$
					return false;
				}

				// As the last segment is an IResource we should be able to get an IFile
				// reference from it
				this.theFile = ((IAdaptable) lastSegmentObj).getAdapter(IFile.class);

				// Extract additional information from the IResource and IProject
				this.workspaceName = this.theResource.getWorkspace().getRoot().getLocation().toOSString();
				this.projectName = this.theProject.getName();
				this.fileName = this.theResource.getName();
				this.filePath = this.theFile.getFullPath().toString();

			} catch (ArrayIndexOutOfBoundsException e) {
				MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, Messages.ConcreteHandler_7);  
				return false;

			} catch (Exception ignored) {
				ignored.printStackTrace();
				MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, Messages.ConcreteHandler_8);  
				return false;

			}

			return true;
		} else {
			String selectionClass = selection.getClass().getSimpleName();
			MessageDialog.openError(this.window.getShell(), Messages.ConcreteHandler_9,  
					String.format(Messages.ConcreteHandler_10, selectionClass));  
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	private static String getClassHierarchyAsMsg(String msgHeader, String msgTrailer, Object theObj) {
		String msg = msgHeader + "\n\n"; //$NON-NLS-1$

		Class theClass = theObj.getClass();
		while (theClass != null) {
			msg = msg + String.format("Class=%s\n", theClass.getName()); //$NON-NLS-1$
			Class[] interfaces = theClass.getInterfaces();
			for (Class theInterface : interfaces) {
				msg = msg + String.format("    Interface=%s\n", theInterface.getName()); //$NON-NLS-1$
			}
			theClass = theClass.getSuperclass();
		}

		msg = msg + "\n" + msgTrailer; //$NON-NLS-1$

		return msg;
	}
}
