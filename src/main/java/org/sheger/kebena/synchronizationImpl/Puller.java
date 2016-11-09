package org.sheger.kebena.synchronizationImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.repos.Repo;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;

public class Puller implements Task{

	private static Logger logger = Logger.getLogger(Puller.class);

	private ReposImpl reposImpl;

	private Status status;

	private PullCommand pullCommand;

	@Override
	public void run(){

		for (final Repo repo : this.getReposImpl().getRepos()) {

			final List<Info> infos=repo.getInfo();

			if(infos == null) {

				Puller.logger.error("infos is null"+infos);

				return;
			}

			boolean sts = false;
			final Status st = new Status();
			st.setIspulled(sts);

			for (final Info info : infos) {

				if(info.getUsername() == null || info.getPassword() == null){
					throw new NullPointerException("username or password is null");
				}
				try(Git git = new Git(repo.getRepository())){

					final CredentialsProvider credentialsProvider = new  UsernamePasswordCredentialsProvider(info.getUsername(),info.getPassword());

					this.getPullCommand(info, credentialsProvider, git);

					try {

						Puller.logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

						sts = this.pullCommand.call().isSuccessful();

						Puller.logger.debug("pulled "+info.getUrl()+"/"+info.getRepoName());

						st.setIspulled(sts);

						info.setStatus(st);

						Puller.logger.debug("pulled "+info.getUrl() +"\t status is\t"+info.getStatus().isIspulled());

					}
					catch (final TransportException e) {

						info.setStatus(st);

						e.printStackTrace();

						Puller.logger.info("cause is "+e.getCause().getClass().getName()+" message of cause "+e.getCause().getMessage());

						Puller.logger.error(e.getLocalizedMessage());

					}catch (final Exception e){

						e.printStackTrace();

						if (e instanceof NoRemoteRepositoryException || e instanceof InvalidRemoteException ){

							Puller.logger.error("marking as new repositories "+info.getRepoName());

							st.setIsnew(true);

							info.setStatus(st);

							info.setWhere(info.getRemote());

						}
					}
				}

			}
		}
	}

	@Override
	public void Cancel() {

	}

	public PullCommand getPullCommand(Info info,CredentialsProvider credentialsProvider,Git git) {

		this.pullCommand = git.pull()
				.setRemoteBranchName(Constants.MASTER)
				.setStrategy(MergeStrategy.THEIRS)
				.setRemote(info.getRemote())
				.setCredentialsProvider(credentialsProvider);

		return this.pullCommand;
	}

	public void setPullCommand(PullCommand pullCommand1) {
		this.pullCommand = pullCommand1;
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

}
