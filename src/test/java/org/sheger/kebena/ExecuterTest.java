package org.sheger.kebena;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.reposImpl.Initialize;
import org.sheger.kebena.reposImpl.RepoImpl;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;
import org.sheger.kebena.synchronizationImpl.CreateOnGitblit;
import org.sheger.kebena.synchronizationImpl.CreaterOnBitBucket;
import org.sheger.kebena.synchronizationImpl.Puller;
import org.sheger.kebena.synchronizationImpl.Pusher;
import org.sheger.kebena.util.Structure;
import org.sheger.kebena.util.StructureManager;
import org.sheger.kebena.util.remotes;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.Assert;
import mockit.Mocked;

public class ExecuterTest {
	private static Logger logger = Logger.getLogger(ExecuterTest.class);

	Pusher pusher = new Pusher();
	Puller puller = new Puller();

	CreaterOnBitBucket bbCreate;
	CreateOnGitblit ggCreate;

	Executer executer = new Executer();

	final StructureManager sm = new StructureManager();
	final File f = new  File("/target");

	Structure st;
	Structure st2 ;

	@Before
	public void init() throws IllegalStateException, GitAPIException {
		this.f.mkdirs();
		final String r1 = "repo1/";
		final String r2 = "repo2/";
		final String r3 = "repo3/";

		final File repo1 = new File(this.f.getAbsolutePath()+"/git/"+r1 );
		repo1.mkdirs();
		final File repo2 = new File(this.f.getAbsolutePath()+"/git/"+r2 );
		repo2.mkdirs();
		final File repo3 = new File(this.f.getAbsolutePath()+"/git/"+r3 );
		repo3.mkdirs();

		Git.init().setDirectory(repo1).setBare(false).call();
		Git.init().setDirectory(repo2).setBare(false).call();
		Git.init().setDirectory(repo3).setBare(false).call();

		this.st = this.sm.ListRepoTree(this.f.getAbsolutePath());

		this.st2 = new Structure();

		this.st2.getRepotree().put("repo1", "git/repo1/");

		this.st2.getRepotree().put("repo2", "git/repo2/");

		final Map<String,String> dc = this.sm.FindDifference(this.st,this.st2);

		Assert.assertTrue("git/repo3".equals(dc.get("repo3")));

	}

	@Test
	public void testExecute(
			@Mocked PullCommand pc,
			@Mocked PushCommand ppc,
			@Mocked CloneCommand cc
			) throws IOException, ParseException {

		final FileRepositoryBuilder rb = new FileRepositoryBuilder();
		ExecuterTest.logger.info(this.st2.getRepotree().get("repo1"));

		final File f1 = new File(this.f.getAbsolutePath()+"/"+this.st2.getRepotree().get("repo1"));
		final File f2 = new File(this.f.getAbsolutePath()+"/"+this.st2.getRepotree().get("repo2"));

		ExecuterTest.logger.info(f1.getAbsolutePath());
		ExecuterTest.logger.info(f2.getAbsolutePath());

		final Repository repository1 = rb.setGitDir(f1).build();
		final Repository repository2 = rb.setGitDir(f2).build();

		final ReposImpl reposImpl = new ReposImpl();

		ExecuterTest.logger.debug("Executing tasks started -|<->|<><><><><><><><><><><><><><><>|<+>|<><><><><><><><><><><><><><><>|<->|-");

		final List<RepoImpl> repos=  new ArrayList<>();

		final RepoImpl repo1 = new RepoImpl();
		final RepoImpl repo2 = new RepoImpl();

		repo1.setRepoBuilder(rb);
		repo1.setRepository(repository1);

		repo2.setRepoBuilder(rb);
		repo2.setRepository(repository2);
		repo2.setRepoBuilder(rb);

		repos.add(repo1);
		repos.add(repo2);

		if(repos != null){

			ExecuterTest.logger.debug("repositoires found are..."+repos.size());

			reposImpl.setRepos(repos);

			final List<Info> infos = new ArrayList<>();

			final Info gitblit = new Info();
			gitblit.setUrl("http://yared.negede@gitblit.dventus.com/r/Projects/server/");
			gitblit.setPassword("s3cret");
			gitblit.setRemote("gitblit");
			gitblit.setUsername("yared.negede");
			gitblit.setRepoName("Server");
			final Status status = new Status();
			status.setIspulled(true);
			gitblit.setStatus(status);

			final Info bucketinfo = new Info();
			bucketinfo.setUrl("https://grumDventus@bitbucket.org/grumDventus/");
			bucketinfo.setPassword("dventus01");
			bucketinfo.setRemote("bitbucket");
			bucketinfo.setUsername("grumDventus");
			bucketinfo.setRepoName("Server");
			bucketinfo.setStatus(status);
			infos.add(gitblit);
			infos.add(bucketinfo);

			for (final RepoImpl r : repos) {

				ExecuterTest.logger.debug("=======================Setting information====================");

				r.setInfo(infos);

			}

			reposImpl.Prepare();

			this.puller = new Puller();
			this.pusher = new Pusher();

			this.bbCreate = new CreaterOnBitBucket();
			this.ggCreate = new CreateOnGitblit();

			final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
			final Initialize initializer =(Initialize) applicationContext.getBean("initializer");
			executer.setApplicationContext(applicationContext);
			initializer.setHomeDir(this.f.getAbsolutePath());
			this.ggCreate.setApplicationContext(applicationContext);

			this.puller.setReposImpl(reposImpl);
			this.pusher.setReposImpl(reposImpl);

			this.bbCreate.setReposImpl(reposImpl);
			this.ggCreate.setReposImpl(reposImpl);

			List<Task> tasks = new ArrayList<>();

			tasks.add(this.puller);
			this.executer.setTasks(tasks);
			ExecuterTest.logger.info("pulling-------------------------------");
			this.executer.Execute();

			for (final RepoImpl repoii : this.pusher.getReposImpl().getRepos()) {
				final Status bbstatus = new Status();
				bbstatus.setIspulled(true);
				repoii.getInfo().get(1).setStatus(bbstatus );
				repoii.getInfo().get(0).setStatus(bbstatus );
			}

			tasks.add(this.pusher);
			this.executer.setTasks(tasks);
			ExecuterTest.logger.info("pushing-------------------------------");
			this.executer.Execute();

			tasks = new ArrayList<>();

			for (final RepoImpl repoii : this.ggCreate.getReposImpl().getRepos()) {
				final Status bbstatus = new Status();
				bbstatus.setIsnew(true);
				repoii.getInfo().get(0).setWhere(remotes.gitblit.name());
				repoii.getInfo().get(0).setStatus(bbstatus );
			}
			ExecuterTest.logger.info("create on bitbucket-------------------------------");
			tasks.add(this.bbCreate);

			this.executer.setTasks(tasks);
			this.executer.Execute();

			tasks = new ArrayList<>();

			for (final RepoImpl repoii : this.bbCreate.getReposImpl().getRepos()) {
				final Status bbstatus = new Status();
				bbstatus.setIsnew(true);
				repoii.getInfo().get(1).setWhere(remotes.bitbucket.name());
				repoii.getInfo().get(1).setStatus(bbstatus );
			}
			ExecuterTest.logger.info("create on gitblit-------------------------------");
			tasks.add(this.ggCreate);
			this.executer.setTasks(tasks);
			this.executer.Execute();

		}

		ExecuterTest.logger.debug("Executing tasks completed -|<->|<><><><><><><><><><><><><><><>|<+>|<><><><><><><><><><><><><><><>|<->|-");

	}

	@After
	public void finalizeit(){
		this.f.isDirectory();
	}
}
