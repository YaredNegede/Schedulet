package org.sheger.kebena.repo.Infoimpl;

import org.sheger.kebena.reposImpl.Status;

public class Info  {

	private Status  status;

	private String username;

	private String password;

	private String url;

	private String remote;

	private String repoName;

	private String where;

	public String getRemote() {
		return this.remote;
	}

	public void setRemote(String remote1) {
		this.remote = remote1;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username1) {
		this.username = username1;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password1) {
		this.password = password1;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url1) {
		this.url = url1;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status1) {
		this.status = status1;
	}

	public String getRepoName() {
		return this.repoName;
	}

	public void setRepoName(String repoName1) {
		this.repoName = repoName1;
	}

	public String getWhere() {
		return this.where;
	}

	public void setWhere(String where1) {
		this.where = where1;
	}


}
