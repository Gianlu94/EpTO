import akka.actor.ActorRef;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by gianluke on 29/08/17.
 */
public class Utils {

	//This method is responsible to select k node from a given node map
	//TODO: to implement checks on parameters (k <= nodeView)
	public static HashMap<Integer, ActorRef> getRandomNodes(HashMap<Integer,ActorRef> nodeView, int k){
		ArrayList<Integer> keyNodes = new ArrayList<Integer>(nodeView.keySet());
		Collections.shuffle(keyNodes);

		HashMap<Integer,ActorRef> selectedRandomNodes = new HashMap<Integer, ActorRef>();
		//System.out.println("size of view : "+nodeView.size()+ " -- K: "+k );

		for (int i = 0; i < k; i++) {
			int key = keyNodes.get(i);
			selectedRandomNodes.put(key, nodeView.get(key));
		}

		return selectedRandomNodes;
	}

	public static void createFile (String pathToFile){
		File f = new File(pathToFile);
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Wrapper to write into a file
	public static void writeOnAFile (String file, String content){
		try{
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);

			out.println(content);
			out.flush();
		}
		catch (IOException e){
			System.err.println("ERROR : An error occured while writing into file " + file);
		}
	}
}
