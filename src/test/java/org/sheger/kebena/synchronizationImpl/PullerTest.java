//package org.sheger.kebena.synchronizationImpl;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//import org.sheger.kebena.repo.Infoimpl.Info;
//import org.sheger.kebena.reposImpl.RepoImpl;
//import org.sheger.kebena.reposImpl.ReposImpl;
//import org.sheger.kebena.reposImpl.Status;
//
//public class PullerTest {
//
//	Puller puller = new Puller();
//
//	@Test
//	public void testRun() throws IOException {
//
//		final ReposImpl reposImpl = new ReposImpl();
//
//		final List<RepoImpl> repos = new ArrayList<>();
//
//		final RepoImpl repo = new RepoImpl();
//		final List<Info> infos = new ArrayList<>();
//		final Info info = new Info();
//
//		info.setUrl("F:/repo/servers");
//		info.setPassword("pwd");
//		info.setRemote("gitblit");
//		info.setUsername("usn");
//		final Status status = new Status();
//		status.setIspulled(true);
//		info.setStatus(status );
//		infos.add(info );
//		infos.add(info );
//
//		repo.setRepoName("AradaAPI");
//		repo.setInfo(infos );
//		repo.setPath("F:/repo/servers/AradaAPI/.git");
//		repo.init();
//		repos.add(repo );
//
//		reposImpl.setRepos(repos);
//		reposImpl.Prepare();
//
//		this.puller.setReposImpl(reposImpl);
//		this.puller.run();
//
//	}
//
//}
