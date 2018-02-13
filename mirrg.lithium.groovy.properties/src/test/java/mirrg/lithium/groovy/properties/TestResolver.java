package mirrg.lithium.groovy.properties;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class TestResolver
{

	@Test
	public void test_PathResolverFileSystem() throws IOException
	{
		IPathResolver pr = new PathResolverFileSystem(new File("."));
		assertEquals(new File("abc").toURI().toURL(), pr.getResource("abc"));
		try {
			pr.getResource("/abc");
			fail();
		} catch (Exception e) {

		}
	}

	@Test
	public void test_PathResolverURL() throws IOException
	{
		IPathResolver pr = new PathResolverURL(new URL("http://a.com/a/b.html"));
		assertEquals(new URL("http://a.com/a/abc.html"), pr.getResource("abc.html"));
		assertEquals(new URL("http://a.com/abc.html"), pr.getResource("../abc.html"));
		assertEquals(new URL("http://a.com/a/c/abc.html"), pr.getResource("c/abc.html"));
	}

	@Test
	public void test_PathResolverClass() throws IOException
	{
		IPathResolver pr = new PathResolverClass(TestResolver.class);
		assertEquals(TestResolver.class.getResource("header.groovy"), pr.getResource("header.groovy"));
	}

	@Test
	public void test_ResourceResolver() throws IOException
	{
		ResourceResolver rr = new ResourceResolver(new PathResolverFileSystem(new File(".")));
		rr.setPathResolver("assets", new PathResolverClass(TestResolver.class));
		rr.setPathResolver("assets2", new PathResolverURL(new URL("http://a.com/a/b.html")));

		assertEquals(new File("abc").toURI().toURL(), rr.getResource("abc"));
		assertEquals(TestResolver.class.getResource("header.groovy"), rr.getResource("assets://header.groovy"));
		assertEquals(new URL("http://a.com/a/abc.html"), rr.getResource("assets2://abc.html"));
	}

}
