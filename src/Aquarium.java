import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class Aquarium extends Frame implements Runnable{
   
	//how MediaTracker is set up to make sure the images are loaded correctly
	MediaTracker tracker;
	Thread thread;
	
	//stores the fish and background images in objects of the awt.Image class
	Image aquariumImage, memoryImage;
	Graphics memoryGraphics;
	Image[] fishImages = new Image[2];
	
	int numberFish = 12;
	int sleepTime = 110;
	Vector<Fish> fishes = new Vector<Fish>();
	boolean runOK = true;
	
	Aquarium(){
		
		//set the title of the frame
		setTitle("The Aquarium");
	
		tracker = new MediaTracker(this);
		
		//to load in the images, we can use the getImage method of the AWT's toolkit. 
		//Toolkit class is abstract and cannot be instantiated directly
		//but we can use the static method getDefaultToolkit to get a working toolkit object
		fishImages[0] = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/fish1.gif")).getScaledInstance(30, 30,Image.SCALE_DEFAULT);
		tracker.addImage(fishImages[0], 1);
		
		fishImages[1] = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/fish2.gif")).getScaledInstance(30, 30, Image.SCALE_DEFAULT);
		tracker.addImage(fishImages[1], 1);
		
		aquariumImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bubbles.gif")).getScaledInstance(800, 600, Image.SCALE_DEFAULT);
		tracker.addImage(aquariumImage, 0);
		
		
		try {
			tracker.waitForID(0);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		setSize(aquariumImage.getWidth(this), aquariumImage.getHeight(this));
		setResizable(false);
		setVisible(true);
		
		//this is how it will looks like the fish is moving
		//
		memoryImage = createImage(getSize().width, getSize().height);
		memoryGraphics = memoryImage.getGraphics();
		
		//while the new thread draws the sprites and moves them around 
		//the main thread can be doing other things- such as closing the app
		thread = new Thread(this);
		thread.start();
	
		//this take care of the action of closing the window.
		// AWT uses listener classes to catch events such as this one
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(
				WindowEvent windowEvent) {
				runOK = false;
				System.exit(0);
			}
		}		
		);
		
	}
	
	@Override
	public void run() {
		Rectangle edges = new Rectangle(0 + getInsets().left, 0 
				+ getInsets().top, getSize().width - (getInsets().left)
				+ getInsets().right, getSize().height - (getInsets().top 
				+ getInsets().bottom));
		
		
		for(int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
			fishes.add(new Fish(fishImages[0], fishImages[1], edges,this));
			try {
				Thread.sleep(20);
			}catch(Exception exp) {
				System.out.println(exp.getMessage());
			}
		}
		
		
		Fish fish;
		
		while(runOK) {
			for(int loopIndex = 0; loopIndex < numberFish; loopIndex ++) {
				fish = (Fish)fishes.elementAt(loopIndex);
				fish.swim();
			}
		}
		
		try {
			Thread.sleep(sleepTime);
		}catch(Exception exp) {
			System.out.println(exp.getMessage());
		}
		
		repaint();
	}
	
	
	//the repaint method calls the update method
	public void update(Graphics g) {
		memoryGraphics.drawImage(aquariumImage, 0, 0, this);
		
		for( int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
			((Fish)fishes.elementAt(loopIndex)).drawFishImage(memoryGraphics);
		}
		
		g.drawImage(memoryImage, 0, 0, this);
	}
	
	
	// it's a good idea to create a new aquarium object in main, because 
	// the new object will not suffer the restrictions comes with static method
	public static void main(String[] args) {
		new Aquarium();
	}

}
