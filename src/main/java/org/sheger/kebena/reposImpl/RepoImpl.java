package org.sheger.kebena.reposImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.repos.Repo;


public class RepoImpl implements Repo {

	private static Logger logger = Logger.getLogger(RepoImpl.class);

	private List<Info> info;

	private String path;

	private String repoName;

	FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();

	public void init() throws IOException{

		RepoImpl.logger.debug("\n\n"+this.getPath()+"\n\n");

		final File f = new File(this.getPath());

		if(f.exists()){

			  this.repository = this.getRepoBuilder()
									.setGitDir(f)
									.readEnvironment()
									.findGitDir()
									.build();

			this.repository.getDirectory();

		}else{

			RepoImpl.logger.error("file not found "+f.getAbsolutePath());

		}


	}

	private org.eclipse.jgit.lib.Repository repository;

	@Override
	public org.eclipse.jgit.lib.Repository getRepository() {

		return this.repository;

	}

	@Override
	public void setRepository(org.eclipse.jgit.lib.Repository repository1) {

		this.repository = repository1;

	}

	@Override
	public List<Info> getInfo() {

		return this.info;

	}

	@Override
	public void setInfo(List<Info> info1) {
		this.info = info1;
	}

	public String getPath() {

		return this.path;

	}

	public void setPath(String path1) {

		this.path = path1;

	}

	@Override
	public String getRepoName() {
		return this.repoName;
	}

	@Override
	public void setRepoName(String repoName1) {
		this.repoName = repoName1;
	}

	public FileRepositoryBuilder getRepoBuilder() {
		return this.repoBuilder;
	}
	public void setRepoBuilder(FileRepositoryBuilder rb) {
		this.repoBuilder= rb;
	}


}
