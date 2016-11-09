package org.sheger.kebena.synchronization;

import org.sheger.kebena.reposImpl.ReposImpl;
import org.sheger.kebena.reposImpl.Status;

public interface Task  {

	public void Cancel();

	public void SetStatus(Status status);

	public Status GetStatus();

	public void run() ;

	public ReposImpl getReposImpl();

	public void setReposImpl(ReposImpl reposImpl1);
}
