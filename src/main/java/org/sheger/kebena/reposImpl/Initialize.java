package org.sheger.kebena.reposImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.sheger.kebena.util.Structure;
import org.sheger.kebena.util.StructureManager;

public class Initialize {

	private static Logger logger = Logger.getLogger(Initialize.class);

	private String homeDir = null;

	private String pathStrPath;

	private Structure structure;

	public Initialize(String hDir){
		this.homeDir = hDir;
	}

	public Initialize( ){ }

	public List<RepoImpl> init() throws IOException{

		Initialize.logger.debug("initilizing repositories from files "+this.homeDir);

		final List<RepoImpl> repos = new ArrayList<>();


		final StructureManager structureManager = new StructureManager();

		final Structure st = structureManager.ReadStructureFile(this.getPathStrPath());

		if(st == null){

			throw new NullPointerException("Structure is null ");
		}


		for (final String repoPath :st.getRepotree().keySet()) {

			Initialize.logger.debug("initilizing repositories from files from "+repoPath);

			final RepoImpl repo = new RepoImpl();

			final String p=this.getHomeDir()+"/"+st.getRepotree().get(repoPath)+"/.git";

			Initialize.logger.info("found repo path is "+p);

			repo.setPath(p);

			repo.setRepoName(repoPath);

			repo.init();

			repos.add(repo);

			Initialize.logger.debug("initilizing repositories from files from "+repoPath+" completed");
		}


		Initialize.logger.debug("bonsoure");


		Initialize.logger.debug("initilizing repositories from files conpleted");


		return repos;
	}

	public String getHomeDir() {
		return this.homeDir;
	}

	public void setHomeDir(String homeDir1) {
		this.homeDir = homeDir1;
	}

	public Structure getStructure() {
		return this.structure;
	}

	public void setStructure(Structure structure1) {
		this.structure = structure1;
	}

	public String getPathStrPath() {
		return this.pathStrPath;
	}

	public void setPathStrPath(String pathStrPath1) {
		this.pathStrPath = pathStrPath1;
	}

}
