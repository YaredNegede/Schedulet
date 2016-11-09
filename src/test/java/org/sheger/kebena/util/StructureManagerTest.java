//package org.sheger.kebena.util;
//
//import java.io.File;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import junit.framework.Assert;
//
//public class StructureManagerTest {
//
//	private static Logger logger = Logger.getLogger(StructureManagerTest.class);
//
//	final StructureManager sm = new StructureManager();
//	final File f = new  File("/target");
//
//	@Before
//	public void init() throws IllegalStateException, GitAPIException {
//		this.f.mkdirs();
//		final String r1 = "repo1";
//		final String r2 = "repo2";
//		final String r3 = "repo3";
//		final File repo1 = new File(this.f.getAbsolutePath()+"/git/"+r1 );
//		repo1.mkdirs();
//		final File repo2 = new File(this.f.getAbsolutePath()+"/git/"+r2 );
//		repo2.mkdirs();
//		final File repo3 = new File(this.f.getAbsolutePath()+"/git/"+r3 );
//		repo3.mkdirs();
//		Git.init().setDirectory(repo1).setBare(false).call();
//		Git.init().setDirectory(repo2).setBare(false).call();
//		Git.init().setDirectory(repo3).setBare(false).call();
//	}
//
//	@Test
//	public void testListRepoTree() {
//
//		StructureManagerTest.logger.info("====================================================");
//
//		final Structure st = this.sm.ListRepoTree(this.f.getAbsolutePath());
//
//		final Structure st2 = new Structure();
//
//		st2.repotree.put("repo1", "git/repo1");
//
//		st2.repotree.put("repo2", "git/repo2");
//
//		final Map<String,String> dc = this.sm.FindDifference(st,st2);
//
//		Assert.assertTrue("git/repo3".equals(dc.get("repo3")));
//
//	}
//
//	@After
//	public void finalizeit(){
//		this.f.isDirectory();
//	}
//}
