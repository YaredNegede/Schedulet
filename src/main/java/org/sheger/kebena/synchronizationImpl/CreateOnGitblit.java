package org.sheger.kebena.synchronizationImpl;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.repos.Repo;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;
import org.sheger.kebena.util.remotes;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CreateOnGitblit implements Task,ApplicationContextAware {

	private static Logger logger = Logger.getLogger(CreateOnGitblit.class);

	private ReposImpl reposImpl;

	private Status status;

	private String gitblitBareHomeDir;

	private ApplicationContext applicationContext;

	@Override
	public void Cancel() {

	}

	@Override
	public void run() {

		for (final Repo repo : this.getReposImpl().getRepos()) {

			final List<Info> infos=repo.getInfo();

			if(infos == null) {

				CreateOnGitblit.logger.error("infos is null");

				return;
			}

			final Status st = new Status();

			final List<String> ls = (List<String>) this.getApplicationContext().getBean("gitblitBareHomeDir");

			this.setGitblitBareHomeDir(ls.get(0));

			for (final Info info : infos) {

				if(info.getStatus().isIsnew()&&remotes.gitblit.name().equals(info.getWhere())) {

					this.setGitblitBareHomeDir(ls.get(0));

					final File fl = new File(this.getGitblitBareHomeDir()+"/"+info.getRepoName());

					try(Git git = new Git(repo.getRepository())){

						final CredentialsProvider credentialsProvider = new  UsernamePasswordCredentialsProvider(info.getUsername(),info.getPassword());

						final CloneCommand cloneCommand = Git.cloneRepository()
								.setDirectory(fl)
								.setURI(info.getUrl())
								.setCredentialsProvider(credentialsProvider);

						try {

							CreateOnGitblit.logger.debug("<-<-<-<-<-<-<-<-<-<-<-<-<--<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<--");

							cloneCommand.call();

							CreateOnGitblit.logger.trace("pulled "+info.getUrl()+"/"+info.getRepoName());

							st.setIsnew(false);

							info.setStatus(st);

							CreateOnGitblit.logger.debug("pulled "+info.getUrl() +"\t status is\t"+info.getStatus().isIspulled());

						}catch (final InvalidRemoteException e) {

							CreateOnGitblit.logger.error(e.getLocalizedMessage());


						} catch (final TransportException e) {

							CreateOnGitblit.logger.info("cause is "+e.getCause().getClass().getName()+" message of cause "+e.getCause().getMessage());


						} catch (final GitAPIException e) {

							CreateOnGitblit.logger.info("cause is "+e.getCause().getClass().getName()+" message of cause "+e.getCause().getMessage());

						}
					}
				}

			}
		}

	}

	@Override
	public ReposImpl getReposImpl() {
		return this.reposImpl;
	}

	@Override
	public void setReposImpl(ReposImpl reposImpl1) {
		this.reposImpl = reposImpl1;
	}

	@Override
	public void SetStatus(Status status1) {
		this.status = status1;
	}

	@Override
	public Status GetStatus() {
		return this.status;
	}

	public String getGitblitBareHomeDir() {
		return this.gitblitBareHomeDir;
	}

	public void setGitblitBareHomeDir(String gitblitBareHomeDir1) {
		this.gitblitBareHomeDir = gitblitBareHomeDir1;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext1) throws BeansException {
		this.applicationContext = applicationContext1;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}


}
