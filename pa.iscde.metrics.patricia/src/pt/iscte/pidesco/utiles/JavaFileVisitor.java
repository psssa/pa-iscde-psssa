package pt.iscte.pidesco.utiles;

public interface JavaFileVisitor {
	
	/**
	 * Executed when a package is visited (before processing its children)
	 * If true is returned (default), the scanning process enters the package contents.
	 * If false is returned, the package contents will not be visited.
	 */
	default boolean visitPackage(String packageName) {
		return true;
	}
	
	/**
	 *	Executed when a class is visited. 
	 */
	default void visit(Class<?> clazz) {

	}
	
	default boolean visitFolder(String folderName) {
		return true;
	}

}
