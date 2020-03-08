package templatemde.epsilon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EpsilonFiles extends HashMap<String, List<String>> {

	// All paths are relatives to the root of the project
	public static final String MAIN_PROGRAM = "epsilon/main.egx";
	public static final String METAMODEL = "models/Test.ecore";

	// Other specifics
	public static final String BUNDLE_NAME = "TemplateMDE";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{
		// Subfolder specific
		put("epsilon/subfolder", Arrays.asList(""));

		// General utilities
		put("epsilon/", Arrays.asList(""));
	}
}
