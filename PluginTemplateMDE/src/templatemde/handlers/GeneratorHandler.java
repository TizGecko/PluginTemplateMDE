package templatemde.handlers;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.handlers.HandlerUtil;
import templatemde.epsilon.EpsilonMain;
import templatemde.epsilon.EpsilonFiles;
//import userinput.api.UserInput;

import org.eclipse.jface.dialogs.MessageDialog;

public class GeneratorHandler extends ConcreteHandler {

	private static final String CMD_1 = "TemplateMDE.commands.cmd1"; //$NON-NLS-1$
	private static final String CMD_2 = "TemplateMDE.commands.cmd2"; //$NON-NLS-1$

	private final EpsilonFiles epsilon_files = new EpsilonFiles();

	public GeneratorHandler() {
		// Empty constructor
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// UserInput.window = this.window;
		Command command = event.getCommand();

		/* Comando dal Menu generale */
		if (command.getId().equals(CMD_1)) {
			MessageDialog.openInformation(this.window.getShell(), DIALOGS_TITLE, Messages.GeneratorHandler_2);
		}

		/* Comando dal Popup Menu */
		else if (command.getId().equals(CMD_2)) {
			// Get the project and file name from the initiating event if at all possible
			if (!extractProjectAndFileFromInitiatingEvent(event)) {
				return null;
			}

			// Paths for Epsilon engine
			loadResFiles(epsilon_files);

			String engine = getResFile(EpsilonFiles.MAIN_PROGRAM);
			String metamodel = getResFile(EpsilonFiles.METAMODEL);
			String model = getTargetFile();
			String workdir = getWorkDir();

			// Execute Epsilon Engine
			try {
				EpsilonMain.main(new String[] { engine, metamodel, model, workdir });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Refresh project explorer
			try {
				this.theProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/* Comando non valido */
		else {
			MessageDialog.openWarning(this.window.getShell(), DIALOGS_TITLE, Messages.GeneratorHandler_3);
		}

		return null;
	}

}
