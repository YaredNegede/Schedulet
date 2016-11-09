package org.sheger.kebena.synchronizationImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;
import org.sheger.kebena.util.Structure;
import org.sheger.kebena.util.StructureManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.gson.Gson;

public class CloneRemote implements Task,ApplicationContextAware  {

	private static Logger logger = Logger.getLogger(CloneRemote.class);
	private ReposImpl reposImpl;
	private Status status;
	private ApplicationContext applicationContext;
	private String gitblitBareHomeDir;
	private String gitblitWorkingHomeDir;
	private String structureFilePath;


	@Override
	public void Cancel() {

	}

	@Override
	public void run() {

		final StructureManager structureManager = new StructureManager();
		final Structure listing = structureManager.ListRepoTree(this.getGitblitWorkingHomeDir());
		Structure stConf = null;

		final File stFl = new File(this.getStructureFilePath());

		try {

			final String  stlisting = structureManager.ReadStructureFile(stFl);
			final Gson gson = new Gson();
			stConf=gson.fromJson(stlisting, Structure.class);

		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		final Map<String,String> difference = structureManager.FindDifference(stConf, listing);

		if(difference == null){

			throw new NullPointerException("no new repo found");

		}

		final Info bbInfo = (Info) this.getApplicationContext().getBean("gitblitInfo");

		for (final String repo : difference.keySet()) {

			final String url = bbInfo.getUrl()+difference.get(repo)+".git";			

			this.cloneRemoteRepo(url ,
								bbInfo.getUsername(),
								bbInfo.getPassword(),
								this.getGitblitWorkingHomeDir()+"/"+difference.get(repo), false);

		}
	}

	public void cloneRemoteRepo(String url,String username, String password,String destination,boolean bare){

		final File gitblitWorkingRepoPath = new File(destination);
		
		if(!gitblitWorkingRepoPath.exists()) {

		  try (Git git = Git.cloneRepository()
							.setURI(url)
							.setBare(bare)
							.setDirectory(gitblitWorkingRepoPath)
							.setCloneAllBranches(true)
							.setCloneSubmodules(true)
							.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
							.call())
			{

// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
			  
				CloneRemote.logger.info("Having repository: " + git.getRepository().getDirectory());
				CloneRemote.logger.info(url);
				logger.info("has clonned from "+url+" to "+gitblitWorkingRepoPath.getAbsolutePath());
				
			} catch (final GitAPIException e) {

				try {

					FileUtils.delete(gitblitWorkingRepoPath, FileUtils.RECURSIVE);
					FileUtils.delete(gitblitWorkingRepoPath);
					logger.error("removed empty directory");
				} catch (final IOException e1) {

					logger.error("cannot delete empty repo "+gitblitWorkingRepoPath.getName());
				}
				CloneRemote.logger.error("error");

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


	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext1) {
		this.applicationContext = applicationContext1;
	}

	public String getGitblitBareHomeDir() {
		return this.gitblitBareHomeDir;
	}

	public void setGitblitBareHomeDir(String gitblitBareHomeDir1) {
		this.gitblitBareHomeDir = gitblitBareHomeDir1;
	}

	public String getGitblitWorkingHomeDir() {
		return this.gitblitWorkingHomeDir;
	}

	public void setGitblitWorkingHomeDir(String gitblitWorkingHomeDir1) {
		this.gitblitWorkingHomeDir = gitblitWorkingHomeDir1;
	}

	public String getStructureFilePath() {
		return this.structureFilePath;
	}

	public void setStructureFilePath(String structureFilePath1) {
		this.structureFilePath = structureFilePath1;
	}


	public Status getStatus() {
		return this.status;
	}


	public void setStatus(Status status1) {
		this.status = status1;
	}

}
