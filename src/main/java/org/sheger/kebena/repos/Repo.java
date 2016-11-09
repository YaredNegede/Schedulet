package org.sheger.kebena.repos;

import java.util.List;

import org.sheger.kebena.repo.Infoimpl.Info;

public interface Repo {

	public String getRepoName() ;
	public void setRepoName(String repoName1);
	public org.eclipse.jgit.lib.Repository getRepository();

	public void setRepository(org.eclipse.jgit.lib.Repository repository) ;

	public List<Info> getInfo();

	public void setInfo(List<Info> info) ;
}
