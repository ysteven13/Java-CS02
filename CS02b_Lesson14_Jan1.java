// Welcome back to CS02b!

// please share screens

// today's topic: Graphs (Graph Theory)

import processing.core.*;  // brings in Processing core classes
import java.util.*;

// Graphs: group of interconnected nodes
//  examples: cities w/connections b/w them (roads)
//    people w/friendships
// vertexes/vertices: nodes
// edges: connections to "neighboring" nodes/vertexes
//traversal: access all the nodes, in a particular order
//search: traversing until finding a node (often, keeping track of path you
//took to find it)
//deoth-first search (DFS); recursive pattern where you expand as deep as possible
//down any pathway until you can't go further, then back up to previous nodes
//breadth-first search (BFS): expand evenly, one distance unit at a time







public class CS02b_Lesson14_Jan1 extends PApplet {
  ArrayList<Node> nodes = new ArrayList<>();
  Node selected = null;

  int[][] edges = new int[1000][1000];
  // instead of having an object for each vertex, we could simply
  //   store edges in a grid
  // row index: index of a node with an outgoing connection
  // col index: index of node with the incoming connection
  // value: strength (or presence) of connection from row node to col node

  // weighted graphs: nodes are connected with a weight/strength instead of T/F
  // directed graphs: edges have a direction, may be one way or two way

  int mode = 0;
  int REGULAR = 0;//selection cursor, nothing else
  int BFS = 1; //expand evenly in each direction, coloring as we go
  int DFS = 2;// expand as far down each branch as possible
  

  
  
  public void setup() {
    size(800, 600);
    //ellipse(20, 40, 50, 60);
    colorMode(HSB, 360, 100, 100);
    //hue saturation brightness
  }

  // overriding setup/draw/etc funcs, and in PApplet those are public
  //   we're not allowed to "reduce visibility" from public to non-public
  public void draw() {
    background(0);  // black
    for (Node n : nodes) {
      // no index access to AL when using for-each loop, but still in order
      n.display();
    }

    if (selected != null) {
      stroke(0,100, 100);  // red
      strokeWeight(3);
      selected.display();
      stroke(0);         // black
      strokeWeight(1);  
      
      if(mode == BFS) bfsColor(selected, 7);
      Set<Integer> visited = new HashSet<>();
      if (mode == DFS) dfsColor(selected.id, 0, 3,visited);
    }
  }
  
  void bfsColor(Node n,int depthLimit) {
	  Set<Node> v = new HashSet<>();
	// FIFO - first in, first out - queue - BFS - .add, .remove
	    // LIFO - last in, first out - stack - DFS - .push, .pop

	  Deque<DN> queue = new ArrayDeque<>();
	  queue.add(new DN(0,n));
	  while(!queue.isEmpty()) {
		  DN current = queue.remove();
		  
		  if(v.contains(current.n)) {
			  continue;
		  }
		  if (current.d >= depthLimit) continue;
		  v.add(current.n);
		  int hue = 360 * current.d / depthLimit;
	      System.out.println(n + ":" + hue);
	      stroke(hue, 100, 100);
	      strokeWeight(10);
	      current.n.display();
	      stroke(0);         // black
	      strokeWeight(1);
	      
	      for(Node neighbor: current.n.neighbors) {
	    	  queue.add(new DN(current.d+1, neighbor));
	      }
	    }

	  }
	  
  
  
  static class DN {
	  int d;
	  Node n;
	  DN(int d, Node n) {
		  this.d = d;
		  this.n = n;
	  }
  }
  
//recursive function that colors in n based on curDepth, then recursively
 //   visits all neighbors of n, up to depthLimit

  void dfsColor(int n, int curDepth, int depthLimit, Set<Integer> v) {
	 if(v.contains(n)) {//don't color anything already visited
		 return;
	 }
	// write a base case that stops once we're at the depth limit
	 if(curDepth >= depthLimit) {
		 return;
	 }
	 
	 v.add(n);
	 int hue = 360*curDepth / depthLimit;
	 System.out.println(n + ":" + hue);
	 stroke(hue,100, 100);  // red
     strokeWeight(10);
     nodes.get(n).display();
     stroke(0);         // black
     strokeWeight(1);  
	 //go through neighbors and color them
     //in an adjacency matrix 
    //   0 1 2
    // 0 0 1 1
    // 1 1 0 0
    // 2 1 0 0

     for(int c =  0; c < 1000; c++) {
    	 if(edges[n][c] != 0 ) {
    		 dfsColor(c,curDepth+1,depthLimit, v);
    	 }
     }
	 
  }
  






  public void keyPressed() {
	    if (key == 'n') nodes.add(new Node(nodes.size()));
	    // ex: 0, 1, 2   (length is 3, next new node will have id=3)
	    else mode = (mode + 1) % 3;
	  }

    // ex: 0, 1, 2   (length is 3, next new node will have id=3)
  

  public void mousePressed() {
    if (mouseButton == LEFT) {
      selected = findNode(mouseX, mouseY);
    }
    if (mouseButton == RIGHT && selected != null) {
      // right click dis/connects selected and clicked nodes
      Node clicked = findNode(mouseX, mouseY);

      if (clicked != null) {
//        if (clicked.neighbors.contains(selected)) {
        if (edges[clicked.id][selected.id] != 0) {
          // these are already connected: remove edges
          clicked.neighbors.remove(selected);  // 
          selected.neighbors.remove(clicked);

          edges[clicked.id][selected.id] = 0;
          edges[selected.id][clicked.id] = 0;
        }
        else {
          // were not previously connected; connect them
          clicked.neighbors.add(selected);
          selected.neighbors.add(clicked);

          edges[clicked.id][selected.id] = 1;  // equivalent statements using
          edges[selected.id][clicked.id] = 1;  //    grid
        }
      }
    }
  }
  
  public void mouseDragged() {
	  if (selected != null && mouseButton  == LEFT) {//selected is a node reference
		  selected.x = mouseX;
		  selected.y = mouseY;
	  }
  }

  Node findNode(float px, float py) {
    Node result = null;
    for (Node n : nodes) {
      if (n.inside(px, py)) result = n;
    }
    return result;
  }

  class Node {
    float x, y;  // data
    // what data structure could store other Nodes we have connections to
    Set<Node> neighbors = new HashSet<>();

    int id;  // index within Nodes array
    
    Node(int id) {
      x = random(0, width);
      y = random(0, height);
      // random, width, and height are inherited from PApplet

      this.id = id;
    }

    void display() {
      ellipse(x, y, 30, 30);
      stroke (0,0,100);   // white
      // for-each loop: n will be set equal to each neighbor one by one
      for (Node n : neighbors) {
        line(x, y, n.x, n.y);
      }
      stroke(0);  // black
    }

    boolean inside(float px, float py) {  return dist(px,py,x,y) <= 15; }
  }
  
}

// homework
// 4. connections b/w graph nodes
// 3 nodes: 3*2 / 2  (each of 3 pairs with 2 others, but we double counted)
//     3
// 4 nodes: 4*3 / 2  (each of 4 nodes pair with 3 others, but don't double)
//     6
// n nodes: n*(n-1) / 2



