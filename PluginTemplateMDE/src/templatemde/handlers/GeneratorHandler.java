package templatemde.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;

import templatemde.epsilon.EpsilonMain;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;

public class GeneratorHandler extends AbstractHandler {

	private static final String DIALOGS_TITLE = Messages.GeneratorHandler_0;

	private static final String CMD_1 = Messages.GeneratorHandler_1;
	private static final String CMD_2 = Messages.GeneratorHandler_2;

	private IWorkbenchWindow window;
	private IWorkbenchPage activePage;

	private IProject theProject;
	private IResource theResource;
	private IFile theFile;

	private String workspaceName;
	private String projectName;
	private String fileName;
	private String filePath;

	public GeneratorHandler() {
		// Empty constructor
	}

	/**
	 * Get Path of a Resource File
	 * 
	 * @param path
	 * @return
	 */
	protected String getResFile(String path) {
		Bundle bundle = Platform.getBundle(Messages.GeneratorHandler_3);
		URL fileURL = bundle.getEntry(path);
		File file = null;
		try {
			file = new File(FileLocator.resolve(fileURL).toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return file.getAbsolutePath();
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

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		Command command = event.getCommand();

		if (command.getId().equals(CMD_1)) {
			/*
			 * Comando dal Menu generale
			 */
			MessageDialog.openInformation(this.window.getShell(), DIALOGS_TITLE, Messages.GeneratorHandler_4);

		} else if (command.getId().equals(CMD_2)) {
			/*
			 * Comando dal Popup Menu
			 */

			// Get the project and file name from the initiating event if at all possible
			if (!extractProjectAndFileFromInitiatingEvent(event)) {
				return null;
			}

			// CODE THAT USES THE FILE GOES HERE
			String engine = getResFile(Messages.GeneratorHandler_5);
			String metamodel = getResFile(Messages.GeneratorHandler_6);
			String model = getTargetFile();

			try {
				EpsilonMain.main(new String[] { engine, metamodel, model });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			/*
			 * Comando non valido
			 */
			MessageDialog.openWarning(this.window.getShell(), DIALOGS_TITLE, Messages.GeneratorHandler_7);
		}

		return null;
	}

	private boolean extractProjectAndFileFromInitiatingEvent(ExecutionEvent event) {
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
							Messages.GeneratorHandler_8, Messages.GeneratorHandler_9, firstSegmentObj));
					return false;
				}

				// The last segment should be an IResource
				Object lastSegmentObj = treePath.getLastSegment();
				this.theResource = ((IAdaptable) lastSegmentObj).getAdapter(IResource.class);
				if (this.theResource == null) {
					MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, getClassHierarchyAsMsg(
							Messages.GeneratorHandler_10, Messages.GeneratorHandler_11, firstSegmentObj));
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
				MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, Messages.GeneratorHandler_12);
				return false;

			} catch (Exception ignored) {
				ignored.printStackTrace();
				MessageDialog.openError(this.window.getShell(), DIALOGS_TITLE, Messages.GeneratorHandler_13);
				return false;

			}

			return true;
		} else {
			String selectionClass = selection.getClass().getSimpleName();
			MessageDialog.openError(this.window.getShell(), Messages.GeneratorHandler_14,
					String.format(Messages.GeneratorHandler_15, selectionClass));
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
