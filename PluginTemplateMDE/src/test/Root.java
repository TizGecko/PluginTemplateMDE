/**
 */
package test;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link test.Root#getList <em>List</em>}</li>
 * </ul>
 *
 * @see test.TestPackage#getRoot()
 * @model
 * @generated
 */
public interface Root extends EObject {
	/**
	 * Returns the value of the '<em><b>List</b></em>' containment reference list.
	 * The list contents are of type {@link test.C1}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>List</em>' containment reference list.
	 * @see test.TestPackage#getRoot_List()
	 * @model containment="true"
	 * @generated
	 */
	EList<C1> getList();

} // Root
