
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    int numStars = 20;
    Image rocketImage;
    Image rocketAnimImage;
    int rocketXPos;
    int starXPos[];
    int starYPos[];
    boolean starActive[] = new boolean [numStars];
    int starWidth[] = new int[numStars];
    int starHeight[] = new int[numStars];
    boolean starAlive[] = new boolean[numStars];
    int rocketYPos;
    int rocketSpeed;
    int speed;
    boolean rocketRight;

    int score;
    int highScore;
    boolean gameOver;
    int health;

    int whichStarHit;
    
    Missile missile[];
   


    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                
                if (gameOver)
                        return;
                
                if (e.VK_UP == e.getKeyCode()) {
                    rocketSpeed ++;
                    if (rocketSpeed >= 20)
                        rocketSpeed = 20;       
                } 
                else if (e.VK_DOWN == e.getKeyCode()) {
                    rocketSpeed--;
                    if (rocketSpeed <= -20)
                        rocketSpeed = -20;
                } 
                else if (e.VK_LEFT == e.getKeyCode()) {
                    speed ++;
                    if (speed >= 20)
                        speed = 20;
                } 
                else if (e.VK_RIGHT == e.getKeyCode()) {
                    speed --;
                    if (speed <= -20)
                        speed = -20;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                if (e.VK_SPACE == e.getKeyCode())
                {
                    Missile.current++;
                    if (Missile.current >= Missile.numMissiles)
                        Missile.current = 0;
                    
                    if (rocketRight)
                    {
                        missile[Missile.current].xpos = rocketXPos+10;
                        missile[Missile.current].active = true;
                        missile[Missile.current].ypos = rocketYPos;
                        missile[Missile.current].right = true;
                    }
                    else
                    {
                        missile[Missile.current].xpos = rocketXPos-10;
                        missile[Missile.current].active = true;
                        missile[Missile.current].ypos = rocketYPos;
                        missile[Missile.current].right = false;
                    }
                }
                if (e.VK_B == e.getKeyCode())
                {
                    //bomb explodes
                }
                
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
        for (int index=0;index<numStars;index++)
        {
            if (starAlive[index])
            drawCircle(getX(starXPos[index]),getYNormal(starYPos[index]),0.0,0.5,0.5);
        }
        
        
        if (rocketRight && speed == 0)
        {
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        else if (!rocketRight && speed == 0)
        {
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        }
        
        
        if (rocketRight && speed != 0)
        {
        drawRocket(rocketAnimImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        else if (!rocketRight && speed != 0)
        {
        drawRocket(rocketAnimImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        }
       
       
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Score: " + score , 30, 45);
        
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Lives: " + health , 120, 45);
        
        if (gameOver)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Impact",Font.BOLD,60));
            g.drawString("Game Over", 60, 200);

        }
        
        
        for (int index=0;index<Missile.numMissiles;index++)
        {
            if (missile[index].active)
            {
                g.setColor((Color.black));
                drawMissile(getX(missile[index].xpos),
                getYNormal(missile[index].ypos),0,1,1);
            }      
        }
        
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("High Score: " + highScore, 220, 45); 
        
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

       for (int index=0;index<numStars;index++)
        {
        g.setColor(Color.yellow);
        g.fillOval(-starWidth[index]/2,-starWidth[index]/2,starWidth[index],starWidth[index]);
        }

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    public void drawMissile(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

       
        g.setColor(Color.red);
        g.fillRect(-4,-3,8,6);
        

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        speed = 0;
        starXPos = new int[numStars];
        starYPos = new int[numStars];
        for (int index=0;index<numStars;index++)
        {
            starXPos[index] = (int)(Math.random()*getWidth2());
            starYPos[index] = (int)(Math.random()*getHeight2());
            starActive[index] = true;
            starWidth[index] = 20;
            starHeight[index] = 20;
            starAlive[index] = true;
        }
        rocketSpeed = 0;
        rocketRight = true;
        score = 0;
        health = 5;
        gameOver = false;
        whichStarHit = -1;
        
        
        missile = new Missile[Missile.numMissiles];
        Missile.current = 0;
        for (int index=0;index<Missile.numMissiles;index++)
        {
            missile[index] = new Missile();
        }
        

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            rocketAnimImage = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            reset();
            bgSound = new sound("starwars.wav"); 
            highScore = 0;
        }
        
        if (gameOver)
           {
               if (highScore < score)
                   highScore = score;
                   return;
           }
      
        if (bgSound.donePlaying)
        {
            bgSound = new sound("starwars.wav");
        }
        for (int index=0;index<numStars;index++)
        {
        starXPos[index]+=speed;
        }
        rocketYPos += rocketSpeed;
        if (rocketYPos >= getHeight2())
        {
            rocketSpeed = 0;
            rocketYPos = getHeight2();
        }
        else if (rocketYPos <= 0)
        {
            rocketSpeed = 0;
            rocketYPos = 0;
        }
        
        for (int index=0;index<numStars;index++)
        {
            if (starXPos[index] >= getWidth2()+20)
            {
                starXPos[index] = 0;
                starYPos[index] = (int)(Math.random()*getHeight2());
                starActive[index] = true;
                starAlive[index]= true;
            }
            else if (starXPos[index] <= -20)
            {
                starXPos[index] = getWidth2();
                starYPos[index] = (int)(Math.random()*getHeight2());
                starActive[index] = true;
                starAlive[index]= true;
            }
        }
        for (int index=0;index<numStars;index++)
        {
            if (rocketXPos+10>starXPos[index]-10 &&
                rocketXPos-10<starXPos[index]+10 &&  
                rocketYPos+10>starYPos[index]-10 &&
                rocketYPos-10<starYPos[index]+10 && index != whichStarHit)
            {
                zsound = new sound("ouch.wav");
                whichStarHit = index;
                health--;
            }
        }
        
        
            if (speed > 0)
            {
                rocketRight = false;
            }
            if (speed < 0)
            {
                rocketRight = true;
            }
        
        for (int index=0;index<Missile.numMissiles;index++)
        {
            if (missile[index].active && missile[index].right)
            {
                missile[index].xpos+=7;          
                if (missile[index].xpos >= getHeight2())
                    missile[index].active = false;
            }
            else if (missile[index].active && !missile[index].right)
            {
                missile[index].xpos-=7;          
                if (missile[index].xpos <= 0)
                    missile[index].active = false;
            }
        }
        
        for (int index=0;index<Missile.numMissiles;index++)
        {
            for (int index2=0;index2<numStars;index2++)
            {
                    if (missile[index].xpos>starXPos[index2]-starWidth[index2]/2 &&
                        missile[index].xpos<starXPos[index2]+starWidth[index2]/2 &&  
                        missile[index].ypos>starYPos[index2]-starHeight[index2]/2 &&
                        missile[index].ypos<starYPos[index2]+starHeight[index2]/2 &&
                        starAlive[index2] && missile[index].active)
                    {              
                        missile[index].active = false;
                        starAlive[index2] = false;
                        score++;
                    }
                
            }
        }
        if (health <= 0)
        {
            gameOver = true;
        }

        

    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
                
                
            }
            in.close();
        } catch (IOException ioe) {
        }
    }


}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

class Missile
{
    public static int current;
    public static int numMissiles = 60;
    
    public int xpos;
    public int ypos;
    public boolean active;
    public boolean right;
    
    Missile()
    {
        active = false;
        right = true;
    }
    
}