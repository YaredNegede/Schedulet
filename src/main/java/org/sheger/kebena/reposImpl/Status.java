package org.sheger.kebena.reposImpl;

public class Status implements org.sheger.kebena.repos.Status {

	private boolean ispushed;
	
	private boolean ispulled;
	
	private boolean isnew;
	
	private boolean isdirty;
	
	public boolean isIspushed() {
		return ispushed;
	}
	
	public void setIspushed(boolean ispushed) {
		this.ispushed = ispushed;
	}
	
	public boolean isIspulled() {
		return ispulled;
	}
	
	public void setIspulled(boolean ispulled) {
		this.ispulled = ispulled;
	}
	
	public boolean isIsnew() {
		return isnew;
	}
	
	public void setIsnew(boolean isnew) {
		this.isnew = isnew;
	}
	
	public boolean isIsdirty() {
		return isdirty;
	}
	
	public void setIsdirty(boolean isdirty) {
		this.isdirty = isdirty;
	}
	 
}
