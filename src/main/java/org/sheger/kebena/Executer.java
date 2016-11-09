package org.sheger.kebena;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.sheger.kebena.repo.Infoimpl.Info;
import org.sheger.kebena.reposImpl.Initialize;
import org.sheger.kebena.reposImpl.RepoImpl;
import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;
import org.sheger.kebena.synchronization.Task;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
public class Executer implements ApplicationContextAware{

	private static Logger logger = Logger.getLogger(Executer.class);

	private List<Task> tasks;

	private Initialize initializer;

	private ApplicationContext springContext;

	public void Init()
			throws IOException{
		//Note: get all repositoires using initilizer

		Executer.logger.debug("initializing ...");
		final ReposImpl repos  = new ReposImpl();
		final List<RepoImpl> repolist = this.getInitializer().init();

		if(repolist == null){

			throw new NullPointerException("no repoImpl listing found");

		}
		//Note : get all remotes configured and set them to repoImpl

		final List<String> remotes = (List<String>) this.springContext.getBean("remotes");

		for (final RepoImpl repoImpl : repolist) {

			repoImpl.setInfo(new ArrayList<>());

			for (final String rm : remotes) {

				final Info info = (Info) this.springContext.getBean(rm);
				info.setRepoName(repoImpl.getRepoName());
				repoImpl.getInfo().add(info);

			}
		}

		//Note : set all repoImpl to reposImpl

		repos.setRepos(repolist);

		Executer.logger.debug("number of found repositories are "+repolist.size());

		//Note : get all tasks(pull,push createonbitbucket, createOngitblit) and set reposImpl to them

		final List<String> allTasksKey = (List<String>) this.springContext.getBean("allTasks");

		if(allTasksKey == null){

			throw new NullPointerException("no task listing found");

		}

		final List<Task> ls = new ArrayList<>();

		this.setTasks(ls);

		for (final String taskkey : allTasksKey) {

			final Task task = (Task) this.springContext.getBean(taskkey);

			if(task == null){
				throw new NullPointerException("this task is not found");
			}

			Executer.logger.debug("Task found is "+task.getClass().getName());

			task.setReposImpl(repos);

			ls.add(task);

		}

		repos.Prepare();

		this.getTasks().addAll(ls);
	}

	public void Execute()
			throws ParseException{

		final GregorianCalendar gc = new GregorianCalendar();

		final long start = gc.getTimeInMillis();

		System.out.println("======-----======---=======----=======----====----=====---=====---======---======");

		if(this.getTasks() == null){

			throw new NullPointerException("there are not tasks");

		}

		int NumberOfTasks = this.getTasks().size();

		Executer.logger.debug("\n task found "+NumberOfTasks);

		Executer.logger.info("\n.....................Starting.........................");



		for (final Task task : this.tasks) {

			Executer.logger.info(" task remianing "+--NumberOfTasks);
			final Status status = new Status();

			try {

				task.run();

			} catch (Exception e) {

				logger.error(e.getMessage());

			}

			status.setIsdirty(false);
			task.SetStatus(status);

		}

		Executer.logger.info("\n.....................Completed.........................");

		final long end = gc.getTimeInMillis();

		long requiredTime = (end - start)/3600*2;

		if(requiredTime < 0){

			requiredTime = 1;

		}

		if(springContext != null){
			final org.quartz.impl.triggers.CronTriggerImpl trigger = (org.quartz.impl.triggers.CronTriggerImpl) this.springContext.getBean("jobTrigger");

			trigger.setCronExpression("0 0/"+requiredTime+" 0-23 * * ?");
		
		}else{
			
			throw new NullPointerException("spring applicationcontext not initialized");
			 
		}
	}

	public void Refresh(){

		final Status status = new Status();
		status.setIsdirty(false);

		for (final Task task : this.getTasks()) {

			task.SetStatus(status);

		}
	}

	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks1) {
		this.tasks = tasks1;
	}

	public Initialize getInitializer() {
		return this.initializer;
	}

	public void setInitializer(Initialize initializer1) {
		this.initializer = initializer1;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext1) throws BeansException {
		this.springContext = applicationContext1;
	}

	public ApplicationContext getApplicationContext() throws BeansException {
		return this.springContext;
	}
}
