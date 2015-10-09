package hGitter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.util.GitDateFormatter;

public class HGitAuth {
	
	public class GitControl {
	 
	    private String localPath, remotePath;
	    private Repository localRepo;
	    private Git git;
	    private CredentialsProvider cp;
	    private String name;
	    private String password;
	    public Boolean hasChanges;
	 
	    public GitControl(Utils prop) throws IOException {
	    	this.name = prop.getValue("name");
	    	this.password = prop.getValue("password");
	        this.localPath = prop.getValue("localPath");
	        this.remotePath = prop.getValue("remotePath");
	        this.localRepo = new FileRepository(this.localPath + "\\.git");
	        cp = new UsernamePasswordCredentialsProvider(this.name, this.password);
	        git = new Git(localRepo);
	    }
	    
	    public void statusRepo() throws IOException, NoFilepatternException, GitAPIException {
	    	
	    	FileRepositoryBuilder builder = new FileRepositoryBuilder();
	    	File localpath = new File(localPath);
	    	
	    	Git git = Git.open(localpath);
	    	Repository repository = git.getRepository();

	    	System.out.println("Having repository: " + repository.getDirectory());
	    	
	    	
	    	
	    	Status status = new Git(repository).status().call();
	    	
	    	System.out.println("Repository: " + status.getModified());
	    	System.out.println("Repository2: " + git.fetch().isDryRun());
	    	
            System.out.println("Added: " + status.getAdded());
            System.out.println("Changed: " + status.getChanged());
            System.out.println("Conflicting: " + status.getConflicting());
            System.out.println("ConflictingStageState: " + status.getConflictingStageState());
            System.out.println("IgnoredNotInIndex: " + status.getIgnoredNotInIndex());
            System.out.println("Missing: " + status.getMissing());
            System.out.println("Modified: " + status.getModified());
            System.out.println("Removed: " + status.getRemoved());
            System.out.println("Untracked: " + status.getUntracked());
            System.out.println("UntrackedFolders: " + status.getUntrackedFolders());
            setHasChanges(status.getModified());
	        repository.close();
	    }
	    
	    public void setHasChanges(Set<String> set){
	    	if(set!= null){
	    		this.hasChanges = true;
	    	}else{
	    		this.hasChanges = false;
	    	}
	    }
	    
	    public void getRepoDiff() throws MissingObjectException, IncorrectObjectTypeException, IOException{
	    	
	    	DiffFormatter formatter = new DiffFormatter( System.out );
	        formatter.setRepository( git.getRepository() );
	        AbstractTreeIterator commitTreeIterator = prepareTreeParser( git.getRepository(),  Constants.HEAD );
	        FileTreeIterator workTreeIterator = new FileTreeIterator( git.getRepository() );
	        List<DiffEntry> diffEntries = formatter.scan( commitTreeIterator, workTreeIterator );

	        for( DiffEntry entry : diffEntries ) {
	          System.out.println( "Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId() );
	          formatter.format( entry );
	        }
	    }
	    
	    private AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException, MissingObjectException, IncorrectObjectTypeException {
		    // from the commit we can build the tree which allows us to construct the TreeParser
		    RevWalk walk = new RevWalk(repository);
		    RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
		    RevTree tree = walk.parseTree(commit.getTree().getId());
		
		    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		    try (ObjectReader oldReader = repository.newObjectReader()) {
		        oldTreeParser.reset(oldReader, tree.getId());
		    }
		    
		    walk.dispose();
		
		    return oldTreeParser;
}
	    
	    /*{
	     * 
	    public void cloneRepo() throws IOException, NoFilepatternException, GitAPIException {
	        Git.cloneRepository()
	                .setURI(remotePath)
	                .setDirectory(new File(localPath))
	                .call();
	    }
	 	*/
	    /*
	    public void addToRepo() throws IOException, NoFilepatternException, GitAPIException {
	        AddCommand add = git.add();
	        add.addFilepattern(".").call();
	    }
	 	*/
	    /*
	    public void commitToRepo(String message) throws IOException, NoHeadException,
	            NoMessageException, ConcurrentRefUpdateException,
	            JGitInternalException, WrongRepositoryStateException, GitAPIException {
	        git.commit().setMessage(message).call();
	    }
	 	*/
	    /*
	    public void pushToRepo() throws IOException, JGitInternalException,
	            InvalidRemoteException, GitAPIException {
	        PushCommand pc = git.push();
	        pc.setCredentialsProvider(cp)
	                .setForce(true)
	                .setPushAll();
	        try {
	            Iterator<PushResult> it = pc.call().iterator();
	            if (it.hasNext()) {
	                System.out.println(it.next().toString());
	            }
	        } catch (InvalidRemoteException e) {
	            e.printStackTrace();
	        }
	    }
	    */
	 	/*
	    public void pullFromRepo() throws IOException, WrongRepositoryStateException,
	            InvalidConfigurationException, DetachedHeadException,
	            InvalidRemoteException, CanceledException, RefNotFoundException,
	            NoHeadException, GitAPIException {
	        git.pull().call();
	    }
	    */
	}
}
