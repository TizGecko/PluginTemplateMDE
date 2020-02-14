/*********************************************************************
* Copyright (c) 2008 The University of York.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package templatemde.epsilon;

import java.io.File;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.emc.emf.EmfModel;

public class EpsilonMain {

	private static final int ENGINE_INDEX = 0;
	private static final int METAMODEL_INDEX = 1;
	private static final int MODEL_INDEX = 2;

	/**
	 * Epsilon Module Entry Point
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		EPackage.Registry.INSTANCE.put(test.TestPackage.eINSTANCE.getNsURI(), test.TestPackage.eINSTANCE);

		// Parse main.egx
		EgxModule module = new EgxModule(new EglFileGeneratingTemplateFactory());
		if (hasArguments(args)) {
			printInfo(args);

			module.parse(new File(args[ENGINE_INDEX]).getAbsoluteFile());

			if (!module.getParseProblems().isEmpty()) {
				System.err.println("Syntax errors found. Exiting.");
				for (ParseProblem p : module.getParseProblems()) {
					System.err.println(p);
				}
				return;
			}

			// Load the XML document
			EmfModel model = new EmfModel();
			model.setMetamodelFile(new File(args[METAMODEL_INDEX]).getAbsolutePath());
			model.setModelFile(new File(args[MODEL_INDEX]).getAbsolutePath());
			model.setReadOnLoad(true);
			model.setName("source");
			model.load();

			// Make the document visible to the EGX program
			module.getContext().getModelRepository().addModel(model);

		}

		// ... and execute
		module.execute();
	}

	private static boolean hasArguments(String[] args) {
		return args.length >= 3;
	}

	private static void printInfo(String[] args) {
		System.out.println("Engine: " + args[ENGINE_INDEX]);
		System.out.println("Metamodel: " + args[METAMODEL_INDEX]);
		System.out.println("Model: " + args[MODEL_INDEX]);
	}

}