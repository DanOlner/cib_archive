import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class exploringchaos extends PApplet {

float x; 
float r = 0.9203831f;
int time = 100;

//how many values of x to show, spread across 0 < x < 1
//It's this minus 1
int xrange = 5, decimals = 6; 

//firstx: used to move the first x back and forth. 
//xadjust: for shifting the xs by a chosen decimal
float firstx = 0, xadjust = -0.2f;
float loopStep, initx, colnum;

float[] graphData = new float[time];

//we want a line from the first to the second point, second the third point etc
//so we need to remember the position of the last point
//Here's our vars for that
//gxt and gxt1 are x at time t and time t+1 - for the state space diagram
float gx,gy,lastx,lasty, gxt, gxt1;

//For cycling colour on state space
float cycleSize = 20, currentCycle = 1;

PFont font;

boolean blend, varchange;

//booleans for switching different graphs on and off
boolean map = true;

//0 = no state space, 1 = square, 2 = direct
int state = 0;


public void setup() {

  size(800,500, P3D);
  colorMode(RGB, 255);
  font = loadFont("Arial-Black-32.vlw"); 


}



public void draw() {


  if (r >=0 && r<=1) {


    if (blend) {

      fill(0,5);
      rect(0,0,width, height);

    } 
    else background(0);

    textFont(font, 12); 
    fill(255);

    fill(255);


    text("number of x values: " + (xrange - 1), width - 200, height - 10);

    text("r: " + r, 20, height - 10);

    text("sweep decimals: " + decimals, width - 600, height - 10);

    //add the new x to the array we're using to draw - we already have the first value from setup
    //give it 5 values for x
    //graphData =  getData();
    //To get our values from xrange
    //If xrange = 1 we want 1 x at the centre - at 0.5
    //If xrange = 2 we want 2 xs at equal points.
    //What this looks like: break 0 to 1 into xrange + 1 chunks
    //Discard the first and last, use the others

    loopStep = 1/PApplet.parseFloat(xrange);

    text("lowest x: " + ((loopStep - (loopStep * firstx))+xadjust),width - 400, height - 10);


    //for (float initx = loopStep * firstx; initx < loopStep*xrange; initx = initx + loopStep) {

    for (int m = 1; m < xrange; m++) {

      initx = ((loopStep*PApplet.parseFloat(m)) - (loopStep * firstx)) + xadjust;

      colnum = loopStep*PApplet.parseFloat(m);

      if(initx >= 0) setData(initx);  

      if(map) {

        //Draw the next map based on our point in time
        for (int n = 0; n < time; n++) {

          lastx = gx; 
          lasty = gy;

          gx = PApplet.parseFloat(n)*(width/PApplet.parseFloat(time));
          //height - : so that 0 appears at the bottom of the graph
          gy = height- (graphData[n]*height);

          stroke(colnum * 255,0,255-((colnum) * 255));

          if (n == 0) {
            fill(colnum * 255,0,255-((colnum) * 255)); 
            ellipse(gx, gy, 8,8);
          } 
          else {
            noFill(); 
            ellipse(gx, gy, 5,5);
          }

          //if n > 0, draw lines between this point and the last one

          if (n > 0) {

            stroke(0, colnum * 100,200-((colnum) * 100));
            line(lastx, lasty, gx, gy);

          }



        }// end for n loop

      }//end map draw test

      //Drawing state space?
      if(state==1) {

        //What we need to do here: plot xt against xt+1
        //We already have these vars from above. Treat both axes as 
        //between 0 and 1
        //Draw the next map based on our point in time

        //time -1 so we don't overrun the array
        for (int n = 0; n < time-1; n++) {

          lastx = gxt; 
          float lastx1 = height-(graphData[n]*height);

          gxt = graphData[n]*(width);

          //height - : so that 0 appears at the bottom of the graph
          gxt1 = height - (graphData[n+1]*height);

          stroke(0,255-((colnum) * 255), 255-((colnum) * 255));

          if (n == 0) {
            fill(colnum * 255,0,255-((colnum) * 255)); 
          } 
          else {
            noFill(); 

          }
          //if n > 0, draw lines between this point and the last one

          ellipse(gxt,height-(graphData[n]*height),5,5);
          ellipse(gxt,gxt1, 5,5);


          if (n > 0) {



            stroke(0, colnum * 100,200-((colnum) * 100));
            line(gxt, lastx1, lastx, lastx1);
            line(gxt, lastx1, gxt, gxt1);

            //Do the cycle along these lines
            stroke(colnum * 100,0,(colnum) * 100);


            //Position is x (or y...)  + (((x \u2013 nextx) * 1/cyclesize)
            line((gxt + ((lastx - gxt) * (currentCycle/cycleSize))), lastx1, (gxt + ((lastx - gxt) * ((currentCycle-1)/cycleSize))), lastx1);

            line(gxt, gxt1 + ((lastx1 - gxt1) * (currentCycle/cycleSize)), gxt, (gxt1 + ((lastx1 - gxt1) * ((currentCycle-1)/cycleSize))) );


            currentCycle-=0.0005f;
            if (currentCycle <= 0) currentCycle = cycleSize;

          }



        }// end for n loop




      }//end if state = 1

      else if (state==2) {

        //What we need to do here: plot xt against xt+1
        //We already have these vars from above. Treat both axes as 
        //between 0 and 1
        //Draw the next map based on our point in time

        //time -1 so we don't overrun the array
        for (int n = 0; n < time-1; n++) {

          lastx = gxt; 
          float lastx1 = height-(graphData[n]*height);

          gxt = graphData[n]*(width);

          //height - : so that 0 appears at the bottom of the graph
          gxt1 = height - (graphData[n+1]*height);

          stroke(0,255-((colnum) * 255), 255-((colnum) * 255));

          if (n == 0) {
            fill(colnum * 255,0,255-((colnum) * 255)); 
          } 
          else {
            noFill(); 

          }
          //if n > 0, draw lines between this point and the last one

          //ellipse(gxt,height-(graphData[n]*height),5,5);
          ellipse(gxt,gxt1, 5,5);


          if (n > 0) {



            stroke(0, colnum * 100,200-((colnum) * 100));
            //line(gxt, lastx1, lastx, lastx1);
            line(gxt, gxt1, lastx, lastx1);

            //Do the cycle along these lines
            stroke(colnum * 100,0,(colnum) * 100);


            //Position is x (or y...)  + (((x \u2013 nextx) * 1/cyclesize)
            //line((gxt + ((lastx - gxt) * (currentCycle/cycleSize))), lastx1, (gxt + ((lastx - gxt) * ((currentCycle-1)/cycleSize))), lastx1);

            line(gxt + ((lastx - gxt) * (currentCycle/cycleSize)), gxt1 + ((lastx1 - gxt1) * (currentCycle/cycleSize)), 
            gxt + ((lastx - gxt) * ((currentCycle-1)/cycleSize)), gxt1 + ((lastx1 - gxt1) * ((currentCycle-1)/cycleSize)) );

            //            line(gxt + ((lastx - gxt) * (currentCycle/cycleSize)), gxt1 + ((lastx1 - gxt1) * (currentCycle/cycleSize)), 
            //          gxt + ((lastx - gxt) * (currentCycle-1/cycleSize)), gxt1 + ((lastx1 - gxt1) * ((currentCycle-1)/cycleSize)) );

            currentCycle-=0.0019f;
            if (currentCycle <= 0) currentCycle = cycleSize;



          }


        }// end for n loop




      }//end state 2



    }


  }//end if...


}

public void setData(float initx) {

  x = initx;

  //set the first value of the graph...
  graphData[0] = initx;

  for (int t = 1; t < time; t++) {

    //update x
    x = 4 * r * x * (1 - x); 

    //stick into array
    graphData[t] = x;

  }//end for loop

}

public void mouseDragged() {

  //println("power - " + 1/pow(10, float(decimals)));

  //println("R = " + r);

  if (keyPressed) {

    if (key == ' ') 

      r += PApplet.parseFloat(pmouseY - mouseY)*(1/pow(10, PApplet.parseFloat(decimals)));


    else if (key == CODED) {

      if (keyCode == CONTROL) {

        //if(varchange) {

        //firstx -= float(pmouseY - mouseY)*(1/pow(10, float(decimals)));

        //} 
        xadjust += PApplet.parseFloat(pmouseY - mouseY)*(1/pow(10, PApplet.parseFloat(decimals)));

        //println("Firstx: " + firstx);

        if (firstx < 0) {
          firstx = 0;
        } 
        else if (firstx > 0.98f) {
          firstx = 0.98f;
        }

      }


    }

  }

  else  r += PApplet.parseFloat(pmouseY - mouseY)/height;

}



public void keyPressed() {

  if(key == CODED) {

    if (keyCode == UP) {

      xrange++;

    } 
    else if (keyCode == DOWN) {

      if (xrange > 1) xrange--; 

    } 
    else if (keyCode == LEFT) {

      if (decimals > 0) decimals--; 

    }
    else if (keyCode == RIGHT) {

      decimals++; 

    } 
    else if (keyCode == ALT) {

      if (blend) {
        blend = false;
      } 
      else blend = true;

    } 


  } 
  else if (key == 'a' || key == 'A') {

    r += (1/pow(10, PApplet.parseFloat(decimals)));

  } 
  else if (key == 'z' || key == 'Z') {

    r -= (1/pow(10, PApplet.parseFloat(decimals)));

  }

  else if (key == 'p' || key=='P') {

    if(varchange) {
      varchange = false;
    } 
    else varchange = true;

  }

  else if (key == 's' || key=='S') {

    xadjust += (1/pow(10, PApplet.parseFloat(decimals)));

  }

  else if (key == 'x' || key=='X') {

    xadjust -= (1/pow(10, PApplet.parseFloat(decimals)));

  }

  //For changing between different graphs
  //1: logistic map
  //2: state space
  else if (key == '1') {

    if (map) {
      map = false;
    } 
    else map = true;

  } 

  else if (key == '2') {

    if (state==0) {
      state = 1;
    } 
    else if (state==1) {
      state = 2;
    }

    else if (state==2) {
      state = 0;
    }

  } 





}




  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#D4D0C8", "exploringchaos" });
  }
}
