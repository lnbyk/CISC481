import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Project_0 {
	private static boolean flag = false;
	static Map<String, List<List<String>>> map = new HashMap<>();
	static Map<String, List<List<String>>> map2 = new HashMap<>();
	
	// Star generator : used to divided answers;
	public void star(String s) {
		for(int i = 0; i < 40; i++) {
			if(i == 20)
				System.out.print(s);
			System.out.print("*");
		}
		System.out.println();
	}

	// problem 1
	// Consumes: a yard which type is lists of list of Integer and a state which type is Lists of list of String
	// Produce List<string>: all possible_action can be performed by engine at current state 
	public static List<String> possible_action(List<List<Integer>> yard, List<List<String>> state){
		List<String> res = new ArrayList<>();
		int head = 0;
		for(int i = 0; i < state.size(); i++)
			if(state.get(i).contains("*"))
				head = i + 1;
		for(List<Integer> l : yard) {
			if(l.get(0) == head){
				if(state.get(l.get(1) - 1).size() > 0) 
					res.add("left " + l.get(1) + " " + head);
				res.add("right " + head + " " + l.get(1));
			}
			else if(l.get(1) == head) {
				if((state.get(l.get(0) - 1).size() > 0))
					res.add("right " + l.get(0) + " " + head);
				res.add("left " + head + " " + " " + l.get(0));
			}
		}
		
		return res;
	}
	
	// problem 2
	// Consumes : String action
	//			  List<List<String> curState
	// Produces : a List<List<String>> this is the next state can be reached by curState by performing this action
	public static List<List<String>> result(String action, List<List<String>> state){
		String[] strs = action.split("\\s+"); 
		List<List<String>> next = new ArrayList<>();
		for(List<String> l : state)
			next.add(new ArrayList<>(l));
		int i = Integer.parseInt(strs[1]) - 1;
		int j = Integer.parseInt(strs[2]) - 1;
		if(strs[0].equals("right") && next.get(i).size() >= 1) {
			String cur = next.get(i).remove(next.get(i).size() - 1);
			next.get(j).add(0, cur);
		}
		else {
			if(next.get(i).size() > 0) {
				String cur = next.get(i).remove(0);
				next.get(j).add(cur);
			}
		}
		return next;
	}
	
	// problem 3
	// Consumes : a list of actions, a state which typed is List<List<String>>
	// Produces : a list of String which are all the state can be reached by curState by performing these actions
	public static List<String> expand(List<String> action, List<List<String>> state){
		List<String> res = new ArrayList<>();
		for(String s : action) {
			res.add(result(s, state).toString());
		}
		
		return res;
	}
	
	// Problem 4
	// Consumes: a yard, a initial state, a final state, and a empty List used to record the path and a empty Set used to record visited state
	
	public static List<String> printPath(List<List<Integer>> yard, List<List<String>> initial, List<List<String>> destination) {
		Set<String> visited = new HashSet<>();
		dfs(yard, initial, destination, visited);
		List<String> actions = new ArrayList<>();
		while(destination != null) {
			List<List<String>> cur = map.get(destination.toString());
			if(cur != null)
				for(String s : possible_action(yard, cur)){
					if(result(s, cur).toString().equals(destination.toString()))
						actions.add(0, s);}
			destination = cur;
		}
		return actions;
		
	}
	public static void dfs(List<List<Integer>> yard, List<List<String>> initial, List<List<String>> destination,Set<String> visited) {
		if(!flag) {
			visited.add(initial.toString());
			List<String> actions = possible_action(yard, initial);
			int size = actions.size();
			for(int i = 0; i < size; i++) {
				List<List<String>> next =result(actions.get(i), initial);
				if(!next.toString().equals(destination.toString()) && !visited.contains(next.toString())) {
					visited.add(next.toString());
					map.put(next.toString(), initial);
					dfs(yard, next, destination,visited);
				}
				else if(next.toString().equals(destination.toString())){
					map.put(next.toString(), initial);
					flag = true;
				}
			}
		}
		
		visited.remove(initial.toString());
	}
	
	
	// problem 6
	public static int cost(List<List<String>> initial, Map<String, Integer> end) {
		int cost = 0;
		for(int i = 0; i < initial.size(); i++) {
			for(int j = 0; j < initial.get(i).size(); j++)
				if(end.containsKey(initial.get(i).get(j)))
					cost += Math.abs(end.get(initial.get(i).get(j)) - (i * 10 + j));
		}	
		return cost;
	}
	
	public static boolean pathTo(List<List<Integer>> yard, List<List<String>> initial, List<List<String>> end) {
		int res = 0;
		Map<String, Integer> map = new HashMap<>();
		for(int i = 0; i < end.size(); i++) {
			for(int j = 0; j < end.get(i).size(); j++) {
				map.put(end.get(i).get(j), 10 * i + j);
			}
		}
		List<List<String>> cur = initial;
		Set<String> visited = new HashSet<>();
		PriorityQueue<List<List<String>>> queue = new PriorityQueue<>((a, b) -> (cost(a, map) - cost(b, map)));
		queue.offer(cur);
		
		while(!queue.isEmpty()) {
			int size = queue.size();
			for(int i = 0; i < size; i++) {
				List<List<String>> tmp = queue.poll();
				res++;
				visited.add(tmp.toString());
				if(tmp.toString().equals(end.toString())) {
					System.out.println("total states traveled " + res);
					return true;}
				for(String s : Project_0.possible_action(yard, tmp)) {
					List<List<String>> next = Project_0.result(s, tmp);
					if(!visited.contains(next.toString())) {
						map2.put(next.toString(), tmp);
						queue.offer(next);
					}
				}
			}
		}
		return false;
	}
	
	public static List<String> printBFS(List<List<Integer>> yard, List<List<String>> initial, List<List<String>> destination){
		pathTo(yard, initial, destination);
		List<String> actions = new ArrayList<>();
		while(destination != null) {
			List<List<String>> cur = map2.get(destination.toString());
			if(cur != null)
				for(String s : Project_0.possible_action(yard, cur)){
					if(Project_0.result(s, cur).toString().equals(destination.toString()))
						actions.add(0, s);
				}
			destination = cur;
		}
		return actions;
	}
}
