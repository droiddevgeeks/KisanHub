package datafetcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utility {
	/**
	 * This method is used to create folder name files to store files inside that
	 * folder
	 * 
	 * @return
	 */
	public static String createMyFilesDirectory() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		File theDir = new File(s + "/files/");
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
			} catch (SecurityException se) {
				System.out.println("Error in creating files folder");
			}

		}

		return theDir.getAbsolutePath().toString();

	}

}
