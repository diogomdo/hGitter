package hGitter;

import hGitter.HGitAuth.GitControl;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public class Test {
	
	public static void main(String[] args) throws IOException, GitAPIException {
        
		Utils util = new Utils();
		util.loadProps();
        
        GitControl gc = new HGitAuth().new GitControl(util);
        //Status repository
        gc.statusRepo();
        
        gc.getRepoDiff();
        
        RepoManager rm = new RepoManager(gc);
        //Clone repository
        //gc.cloneRepo();
        //Add files to repository
        //gc.addToRepo();
        //Commit with a custom message
        //gc.commitToRepo("Modified testfile.txt");
        //Push commits
        //gc.pushToRepo();
        //Pull
        //gc.pullFromRepo();
    }

}
