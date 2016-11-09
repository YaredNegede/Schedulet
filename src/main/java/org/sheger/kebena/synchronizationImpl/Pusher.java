package org.sheger.kebena.synchronizationImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoRemoteRepositoryException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.repos.Repo;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;

public class Pusher implements Task{

	private static Logger logger = Logger.getLogger(Pusher.class);

	private ReposImpl reposImpl;

	private Status status;

	@Override
	public ReposImpl getReposImpl() {
		return this.reposImpl;
	}

	@Override
	public void setReposImpl(ReposImpl reposImpl1) {
		this.reposImpl = reposImpl1;
	}

	@Override
	public void run(){

		for (final Repo repo : this.reposImpl.getRepos()) {

			Pusher.logger.debug("pushing "+repo.getRepoName());

			final List<Info> infos = repo.getInfo();

			if(infos == null) {

				Pusher.logger.error("infos is null"+infos);

				return;
			}

			for (final Info info : infos) {

				final Status st = new Status();

				st.setIspushed(false);

				Pusher.logger.debug("pushing to" + info.getRemote());

				try(Git git = new Git(repo.getRepository())){

					final CredentialsProvider credentialsProvider =new  UsernamePasswordCredentialsProvider(info.getUsername(),info.getPassword());

					Pusher.logger.debug("building repository for pushing "+repo.getRepoName());

					try {

						Pusher.logger.debug("is pulled "+info.getStatus());

						if(info.getStatus().isIspulled()) {

							Pusher.logger.debug("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");

							 git.push()
								.setReceivePack(RemoteConfig.DEFAULT_RECEIVE_PACK)
								.setPushTags()
								.setRemote(info.getRemote())
								.setPushAll()
								.setForce(true)
								.setCredentialsProvider(credentialsProvider)
								.call();

							 st.setIspushed(true);

							Pusher.logger.info("pushed "+info.getUrl()+"/"+info.getRepoName());

						}else{

							Pusher.logger.error("never been pulled before from "+info.getRemote() +" for "+info.getRepoName());

						}

						Pusher.logger.trace("pushed "+info.getUrl()+"/"+info.getRepoName());


					}  catch (final TransportException e) {

						info.getStatus().setIspushed(false);

						Pusher.logger.error(e.getLocalizedMessage());

					}catch (final Exception e){

						e.printStackTrace();

						if (e instanceof NoRemoteRepositoryException || e instanceof InvalidRemoteException )
						{
							Pusher.logger.error("Marking as new repository "+info.getRepoName());

							st.setIsnew(true);

							info.setWhere(info.getRemote());

							info.setStatus(st);

						}
					}
				}

			}
		}

	}

	@Override
	public void Cancel() {

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
