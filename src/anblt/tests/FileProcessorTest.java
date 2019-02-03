package anblt.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import anblt.fileproc.FileProcessor;
import anblt.fileproc.observer.FileContentDumper;
import anblt.fileproc.observer.FileListReader;
import anblt.fileproc.observer.JavaImportDeclarationInjector;
import anblt.fileproc.observer.SimpleDiff;
public class FileProcessorTest {
	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();
	File file;
	@Before
	public void setupMethod() throws IOException {
		File tmpdir = tempFolder.newFolder("testdir");
		File file1 = new File(tmpdir, "a.txt");
		File file2 = new File(tmpdir, "b.txt");
		assertTrue("a.txt couldn't be created.", file1.createNewFile());
		assertTrue("b.txt couldn't be created.", file2.createNewFile());
		File file3 = new File(tmpdir, "TestClass.java");
		PrintWriter javaout = new PrintWriter(new BufferedWriter(
				new FileWriter(file3)));
		javaout.println(
			"import java.util.List;\n" +
			"public class TestClass {\n" +
			"    List myList;\n" +
			"    public TestClass() {}\n" +
			"}\n"
		);
		javaout.close();
		file = new File(tmpdir, "filelist.csv");
		PrintWriter fileout = new PrintWriter(new BufferedWriter(
				new FileWriter(file)));
		fileout.println(file1.getAbsolutePath());
		fileout.println(file2.getAbsolutePath());
		fileout.println(file3.getAbsolutePath());
		fileout.close();
		String filecontent = new String();
		try {
			filecontent = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
		}
		assertFalse(filecontent.isEmpty());
	}
	@Test
	public void testFileProcessor1() throws IOException {
		FileListReader filelist = new FileListReader("filelist", ".*\\.java$", "^$");
		File tmpdir = tempFolder.newFolder("tempdir");
		File tmpfile = new File(tmpdir, "%s");
		FileContentDumper copy = new FileContentDumper("copy", tmpfile, "", "", ".*", "", false);
		JavaImportDeclarationInjector inject = new JavaImportDeclarationInjector("inject", null, JavaImportDeclarationInjector.JAVA_LANG_SYSTEM);
		SimpleDiff diff = new SimpleDiff("diff", "");
		FileContentDumper stderr = new FileContentDumper("dumper", java.lang.System.err, "> ", "", ".*", "", true);
		FileProcessor fileproc = new FileProcessor(filelist, inject, copy, diff, stderr);
		fileproc.addDependency(filelist, copy);
		fileproc.addDependency(filelist, inject);
		fileproc.addDependency(inject, diff);
		fileproc.addDependency(copy, diff);
		fileproc.addDependency(diff, stderr);
		fileproc.addItem(file);
		List<File> result = inject.getResult();
		assertEquals(1, result.size());
	}
}
