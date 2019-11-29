import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.lang.*;
import java.io.*;
class OvalPrimitive extends JComponent{
	OvalPrimitive(int index){
		setOpaque(true);
		setForeground(Color.white);
		this.nodeindex = index;
		SidesNumbers = new ArrayList<>();
	}
	public ArrayList <Integer> SidesNumbers;
	private int nodeindex;
	private int parentnode;
	private boolean connected;
	public int getParentNode(){
		return parentnode;
	}
	public void setParentNode(int p){
		this.parentnode = p;
	}

	public boolean getConnected(){
		return connected;
	}
	public void setConnected(boolean p){
		this.connected = p;
	}
	//to up, to left, to down, to right
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(5f));
		for(Integer sidesn : SidesNumbers){
			switch(sidesn){
				case (0):
					g2d.drawLine(getWidth()/2,getHeight()/2,getWidth()/2,0);
					break;
				case (2):
					g2d.drawLine(getWidth()/2,getHeight()/2,getWidth()/2,getHeight());
					break;
				case (1):
					g2d.drawLine(0,getHeight()/2,getWidth()/2,getHeight()/2);
					break;
				case (3):
					g2d.drawLine(getWidth()/2,getHeight()/2,getWidth(),getHeight()/2);
					break;
				default:
					break;
			}
		}
		g2d.fillOval(getWidth()/4,getHeight()/4,getWidth()/2,getHeight()/2);
			// if(getWidth()<getHeight()){//if you want circles
			// 	g2d.fillOval(getWidth()/4,getHeight()/4,getWidth()/2,getWidth()/2);
			// }
			// else{
			// 	g2d.fillOval(getWidth()/4,getHeight()/4,getHeight()/2,getHeight()/2);
			// }

	}
}

class NodesPanel extends JPanel{
	public NodesPanel(){
		setLayout(new GridLayout(graph.n,graph.n));
		OvalPrimitive rectan;
		for(int i = 0;i<graph.n*graph.n;i++){
			rectan = new OvalPrimitive(i);
		  graph.nodes.add(rectan);
			add(rectan);
		}
		setOpaque(false);
	}
}

class ShapeFrame extends JFrame{

	public ShapeFrame(){
		super("MazeBuilder");
		add(graph.nodespanel);
		setSize(graph.WidthSize,graph.HeightSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		getContentPane().setBackground(Color.black);
	}
}

class RandomSide extends Random{
	private void logtotxt(){
		try (FileWriter writer = new FileWriter("./log.txt", false))
        {
            for (Integer backs:backsides)
            {
                String s = Integer.toString(backs);
                writer.write(s);
                writer.write(System.lineSeparator());
            }
						writer.close();
        }
        catch(IOException e) {
						File logfile =	new File("./log.txt");
            System.out.println("log file created");
						logtotxt();
        }
	}
	private ArrayList<Integer> sides = new ArrayList<>();
	private ArrayList<Integer> backsides = new ArrayList<>();
	private ArrayList<Integer> backsidesaround = new ArrayList<>();
	private boolean backsided = false;
	private int newLastPointIndex;
	public boolean backsided(){
			if(backsided){
				backsided = false;
				return true;
			}
			else{
				return false;
			}
		}
		public int getnewLastPointIndex(){
			return newLastPointIndex;
		}
	private int nextSideIndex;

	public int nextRandomSide(int now){
		fillavaliable(sides, now);
		while(true){
			try{
			nextSideIndex = super.nextInt(sides.size());
			}
			catch(IllegalArgumentException ex){System.out.println("sides.size = " + sides);}
			if(graph.nodes.get(sides.get(nextSideIndex)).getConnected() == true){
				sides.remove(nextSideIndex);
				System.out.println("Removed "+nextSideIndex);
				System.out.println("reg "+sides);
				if(sides.size() == 0){
				backsides.add(now);
				try{
					return nextRandomSide(backRandomSide());
				}
				catch(StackOverflowError er){System.out.println("StackOverflowError, backsides "+backsides); System.exit(0);}
				}
			}
			else{
				backsides.add(now);
				return sides.get(nextSideIndex);
			}
		}
	}
	private int backRandomSide(){
		int saved = 0;
		System.out.println("Entered Got back");
		while(true){
			while(true){
				if(backsidesaround.size()!=0)
					break;
				System.out.println("BackSides "+ backsides);
				//logtotxt(); //log backsides
				fillavaliable(backsidesaround, backsides.get(backsides.size()-1));
				saved = backsides.get(backsides.size()-1);
				backsides.remove(backsides.size()-1);
			}
			int backSideIndexAround = super.nextInt(backsidesaround.size());
			if(graph.nodes.get(backsidesaround.get(backSideIndexAround)).getConnected() == true){
				backsidesaround.remove(backSideIndexAround);
				System.out.println("BackRemoved "+backSideIndexAround);
				System.out.println("Backreg "+backsidesaround);
			}
			else{
					System.out.println("Got back with index "+ saved);
					backsidesaround.clear();
					backsided = true;
					newLastPointIndex = saved;
					return saved;
			}
		}
	}
	private void fillavaliable(ArrayList<Integer> array, int now){
		array.clear();
		array.add(now-graph.n);
		array.add(now-1);
		array.add(now+graph.n);
		array.add(now+1);
		array.removeIf(i-> i<0 || i>graph.nodes.size()-1 || (i==(now-1)&&((i+1)%graph.n)==0) || (i==(now+1)&&((i+1)%graph.n)==1));
		System.out.println(array);
	}
}
class MazeAlgo{
		private void connectedlog(){
			int counter = 0;
			try (FileWriter writer = new FileWriter("./alllog.txt", false))
					{
							for (OvalPrimitive node:graph.nodes)
							{
									String s = Integer.toString(counter)+" "+node.getConnected();
									writer.write(s);
									writer.write(System.lineSeparator());
									counter++;
							}
							writer.close();
					}
					catch(IOException e) {
							File logfile =	new File("./alllog.txt");
							System.out.println("alllog file created");
							connectedlog();
					}
		}

		private int nowPointIndex = graph.startPoint;
		private int lastPointIndex = nowPointIndex;
		RandomSide randomside = new RandomSide();
		private int counter = 0;
		private ArrayList<Integer> tails = new ArrayList<>();
	public void BuildMaze(){
		while(counter<graph.n*graph.n){
			graph.nodes.get(nowPointIndex).setParentNode(lastPointIndex);
			graph.nodes.get(nowPointIndex).setConnected(true);
			graph.nodes.get(nowPointIndex).setForeground(Color.green);
			connectnodes(lastPointIndex, nowPointIndex);

			if(graph.nodes.get(lastPointIndex).getForeground() == Color.red)
				graph.nodes.get(lastPointIndex).setForeground(Color.yellow);
			else{
				if(graph.nodes.get(lastPointIndex).getForeground() == Color.yellow)
					graph.nodes.get(lastPointIndex).setForeground(Color.white);
				graph.nodes.get(lastPointIndex).setForeground(Color.red);
				}

		//	connectedlog(); //log connected nodes
			lastPointIndex = nowPointIndex;
			if(graph.n*graph.n-counter!=1)
				nowPointIndex = randomside.nextRandomSide(nowPointIndex);
			if(randomside.backsided()){
				tails.add(lastPointIndex);
				lastPointIndex = randomside.getnewLastPointIndex();
			}
			counter++;
			//try{Thread.sleep(graph.AnimationSpeed);} catch(InterruptedException ex){} //uncomment to change AnimationSpeed
		}
		graph.nodes.get(graph.startPoint).setForeground(Color.blue);
		graph.nodes.get(tails.get(new Random().nextInt(tails.size()))).setForeground(Color.blue);
		System.out.println("Ticks "+ counter);
	}

	public void connectnodes(int last, int now){
		System.out.println("POINTS "+last+" "+now);
		switch(now-last){
			case(1):
				graph.nodes.get(now).SidesNumbers.add(1);
				graph.nodes.get(last).SidesNumbers.add(3);
				break;
			case(-1):
				graph.nodes.get(now).SidesNumbers.add(3);
				graph.nodes.get(last).SidesNumbers.add(1);
				break;
			case(graph.n):
				graph.nodes.get(now).SidesNumbers.add(0);
				graph.nodes.get(last).SidesNumbers.add(2);
				break;
			case(-graph.n):
				graph.nodes.get(now).SidesNumbers.add(2);
				graph.nodes.get(last).SidesNumbers.add(0);
				break;
		}

	}
}
//green color - tails; yellow - fork; red - just maze;
public class graph{
	public static int WidthSize = 500;//Window size
	public static int HeightSize = 400;
	public static int AnimationSpeed = 0;//Drawing speed, Uncomment in code if needed
	public static ArrayList<OvalPrimitive> nodes = new ArrayList<>();
	public static final int n = 30;//It`s a square maze, so it`s length of one side
	public static int startPoint = new Random().nextInt(n*n);//Maze start point. End point generates randomly choosing from tails.
	public static int nowPoint = startPoint;
	public static NodesPanel nodespanel = new NodesPanel();
	public static ShapeFrame shframe;//Window gui

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
				shframe = new ShapeFrame();
      }
    });
		MazeAlgo maze = new MazeAlgo();
		maze.BuildMaze();
	}
}
