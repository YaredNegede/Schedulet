package org.sheger.kebena.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class StructureManager {

	private static Logger logger = Logger.getLogger(StructureManager.class);

	public Structure ListRepoTree(String path)
	{
		Structure structure = null;
		final File parent = new File(path);
		if(!parent.exists())
		{
			StructureManager.logger.error("path does not exist");
			return null;
		}
		if(parent.list().length < 1)
		{
			StructureManager.logger.error("there are no projects");
			return null;
		}
		StructureManager.logger.info(parent.getAbsolutePath());

		final File[] group =parent.listFiles();

		structure = new Structure();

		for (final File f : group)
		{

			final File[] groupList = f.listFiles();

			if(groupList != null) {

				StructureManager.logger.info(f.getName()+" "+groupList.length);

				for (final File repo : groupList)
				{
					if(repo.isDirectory()) {
						structure.getRepotree().put(repo.getName(), f.getName()+"/"+repo.getName());
					}
				}
			}
		}
		return structure;
	}

	public Map<String,String> FindDifference(String ParentPath,Structure structure){
		final Map<String,String> newRepos = new HashMap<>();
		final Map<String,String> tree  = structure.getRepotree();
		for (final String repo : tree.keySet())
		{
			final String repoFullPath = ParentPath+"/"+tree.get(repo);
			final File f = new File(repoFullPath);
			StructureManager.logger.info(" all \t\t"+repo+" \t\t "+f.exists());
			if(!f.exists())
			{	StructureManager.logger.info(" new \t\t"+repo);
			newRepos.put(repo, tree.get(repo));
			}
		}
		return newRepos;
	}

	public Map<String,String> FindDifference(Structure structure1,Structure structure2){
		Map<String,String> dc = new HashMap<>();
		if(structure1 == null){
			StructureManager.logger.error("wrongn input for first argument");
			return null;
		}
		if(structure1.getRepotree().size()<1 || structure1.getRepotree() ==null){
			StructureManager.logger.info("first argument has no items or null");
			return null;
		}
		if(structure2 == null){
			return structure1.getRepotree();
		}
		if(structure2.getRepotree().size() == 0||structure2.getRepotree() == null){
			dc = structure1.getRepotree();
			return dc;
		}
		for (final String s : structure1.getRepotree().keySet())
		{
			if(!structure2.getRepotree().containsKey(s))
			{
				dc.put(s, structure1.getRepotree().get(s));
			}
		}
		return dc;

	}

	public void updateStructureFile(String json,File f){
		try(FileWriter wr = new FileWriter(f)){
			wr.write(json);
			StructureManager.logger.debug(json);
		} catch (final IOException e) {
			StructureManager.logger.error("Unable to write to file");
		}
	}

	public String ReadStructureFile(File f)
			throws FileNotFoundException{
		final Reader r=new FileReader(f);
		try(BufferedReader rd = new BufferedReader(r)){
			String st;
			final StringBuffer sb = new StringBuffer();
			while((st = rd.readLine())!=null){
				sb.append(st);
			}
			final String rt = sb.toString();
			rt.replace("\n", "").replace("\r", "");
			return rt;
		} catch (final IOException e) {
			StructureManager.logger.error("Unable to write to file");
		}
		return null;
	}

	public Structure ReadStructureFile(String strPath) throws FileNotFoundException{
		final Gson g = new Gson();
		final File sf = new File(strPath);
		final String str = this.ReadStructureFile(sf );
		final Structure strc = g.fromJson(str, Structure.class);
		return strc;
	}
}
