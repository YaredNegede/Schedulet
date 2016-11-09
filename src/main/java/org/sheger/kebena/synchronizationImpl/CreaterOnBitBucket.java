package org.sheger.kebena.synchronizationImpl;

import org.apache.log4j.Logger;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.repos.Repo;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;
import org.sheger.kebena.util.remotes;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class CreaterOnBitBucket implements Task {

	private static Logger logger = Logger.getLogger(CreaterOnBitBucket.class);

	private ReposImpl reposImpl;

	private String bitbucketCreateURL;

	private Status status;

	@Override
	public void Cancel() {

	}

	@Override
	public void run(){

		CreaterOnBitBucket.logger.debug("................................................................");

		for (final Repo repo : this.reposImpl.getRepos()) {

			for (final Info info : repo.getInfo()) {

				if(info.getStatus().isIsnew()&&info.getWhere().equals(remotes.bitbucket.name())) {

						CreaterOnBitBucket.logger.debug("create running for bitbucket");

						this.Create(info);

						CreaterOnBitBucket.logger.trace(info.getUrl()+"\t"+info.getUsername()+"\t"+info.getPassword());

				}

			}

		}

	}

	public  int Create(Info info) {

		CreaterOnBitBucket.logger.debug("runnig create on gitblit");

		final String sessionid = this.Login(info.getUrl(), info.getUsername(), info.getPassword());

		final Response responce = RestAssured.given()
											.header("Cookie", sessionid)
											.contentType("application/json")
											.when()
											.post(this.getBitbucketCreateURL()+"/"+info.getRepoName()+".git");

		CreaterOnBitBucket.logger.debug("running "+this.getBitbucketCreateURL()+"/"+info.getRepoName()+".git");

		return responce.statusCode();

	}

	public String  Login(String url,String username, String password){

		CreaterOnBitBucket.logger.debug("authenticating on bitbucket");

		final com.jayway.restassured.response.Response a = RestAssured.given().auth().basic(username, password).post(url);

		final String sessionId = a.getHeader("Set-Cookie");

		return sessionId;

	}

	@Override
	public ReposImpl getReposImpl() {

		return this.reposImpl;

	}

	@Override
	public void setReposImpl(ReposImpl reposImpl1) {

		this.reposImpl = reposImpl1;

	}

	public String getBitbucketCreateURL() {
		return this.bitbucketCreateURL;
	}

	public void setBitbucketCreateURL(String bitbucketCreateURL1) {
		this.bitbucketCreateURL = bitbucketCreateURL1;
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
