//package org.sheger.kebena.reposImpl;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//import org.sheger.kebena.Executer;
//import org.sheger.kebena.repo.Infoimpl.Info;
//import org.sheger.kebena.synchronization.Task;
//import org.sheger.kebena.synchronizationImpl.Puller;
//import org.sheger.kebena.synchronizationImpl.Pusher;
//
//public class InitializeTest {
//
//	Initialize initialize = new Initialize();
//	private final String homeDir = "f:/repo/";
//	private final List<RepoImpl> repos = new ArrayList<>();
//
//	@Test
//	public void testInitialize()
//			throws IOException {
//
//		this.initialize.setHomeDir(this.homeDir);
//		this.initialize.setRepos(this.repos);
//
//		final Info info = new Info();
//		info.setPassword("pwd");
//		info.setRemote("gitblit");
//		info.setUrl("F:/repo");
//
//		if(this.initialize.initialize()){
//			final List<RepoImpl> rep = this.initialize.getRepos();
//			final ReposImpl reposImpl = new ReposImpl();
//			reposImpl.setRepos(rep);
//			final Puller puller = new Puller();
//			final Pusher pusher = new Pusher();
//			puller.setReposImpl(reposImpl);
//			pusher.setReposImpl(reposImpl);
//			final Executer executer  = new Executer();
//			final List<Task> tasks = new ArrayList<>();
//			executer.setTasks(tasks);
//			tasks.add(puller);
//			tasks.add(pusher);
//			executer.Execute();
//		}
//
//	}
//
//}
