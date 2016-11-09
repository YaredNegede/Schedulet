package org.sheger.kebena.reposImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.repos.Repo;
import org.sheger.kebena.repos.Repos;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

public class ReposImpl implements Repos {

	private static Logger logger = Logger.getLogger(ReposImpl.class);

	private List<RepoImpl> repos;

	public void Prepare() throws IOException {

		if(this.repos == null){
			ReposImpl.logger.error("no reposisory set");
			return;
		}

		for (final Repo repo : this.repos) {


			ReposImpl.logger.info(repo.getRepoName());

			for (final Info info : repo.getInfo()) {

				ReposImpl.logger.info("Configuring git repository with remote, url, and remote-name for "+info.getRepoName());

				final String reponame = info.getRepoName();

				if(reponame !=null){

					if(repo.getRepository() != null){

						final StoredConfig storedConfig = repo.getRepository().getConfig();

						final String GIT_REMOTE_REF_SPEC = "+refs/heads/*:refs/remotes/origin/*";

						final RefSpec REMOTE_REF_SPEC = new RefSpec(GIT_REMOTE_REF_SPEC);

						try {

							final RemoteConfig remoteConfig = new RemoteConfig(storedConfig, info.getRemote());
							remoteConfig.addFetchRefSpec(REMOTE_REF_SPEC);
							remoteConfig.addURI(new URIish(info.getUrl() + info.getRepoName() + ".git"));
							remoteConfig.update(storedConfig);
							storedConfig.save();
							
						} catch (final Exception e) {

							logger.info(e.getMessage());

						}

						
						
					}else{
						logger.error("There is no git reposotory");
					}

				}else{
					throw new ValueException("repository has no name");
				}
			}

		}

	}

	public List<RepoImpl> getRepos() {
		return this.repos;
	}

	public void setRepos(List<RepoImpl> repos1) {
		this.repos = repos1;
	}
}
