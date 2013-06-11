/*
 *   gene View
 *   http://casmi.github.com/
 *   Copyright (C) 2011, Xcoo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package genome.view;


import genome.data.Exon;
import genome.data.Gene;
import genome.data.ViewScale;
import genome.net.GeneLoader;

import java.util.ArrayList;
import java.util.List;

import casmi.Applet;
import casmi.AppletRunner;
import casmi.KeyEvent;
import casmi.MouseButton;
import casmi.MouseEvent;
import casmi.graphics.color.ColorSet;
import casmi.graphics.element.Line;
import casmi.graphics.element.Text;
import casmi.graphics.font.Font;

/**
 * Class for Gene View
 * @author K. Nishimura
 *
 */
public class GeneView extends Applet
{
	private static final String REMOTE_DATA_URL = "http://genome.ucsc.edu/cgi-bin/das/hg19/features?segment=1:500000,900000;type=refGene;type=knownGene;";

	private static final double MIN_SCALE = 0.005;
	private static final double SCALE_STEP = 0.0001;

	private static final double GENE_ELEMENT_ORDER_STEP = 30;

	private static final double SCALE_HEIGHT = 20;
	private static final double HALF_SCALE_HEIGHT = SCALE_HEIGHT / 2;

	private static final int MOUSE_SCALING_THRESHOLD = 10;

	private static final double SCROLL_SPEED_EPS = 0.01;
	private static final double MOUSE_SCROLL_SPEED_FACTOR = 10.0;
	private static final double SCROLL_SPEED_DAMPING_FACTOR = 0.8;

	private static final String TITLE = "Gene View";
	private static final double FPS = 20.0;

	private double scale = 0.01;

	private double scroll = 0.0;
	private double scrollSpeed = 0.0;

    private List<GeneElement> geneElements = new ArrayList<GeneElement>();
    private Line scaleMainLine;
    private List<Line> scaleLines = new ArrayList<Line>();
    private List<Text> scaleTexts = new ArrayList<Text>();

    private Text titleText;
    private String annotation = "Annotation";
    private Text annotationText;

	private List<Gene> genes;
	private List<Exon> exons;
	private ViewScale viewScale;

//	@Override
//	public void draw(Graphics g) {
//		if(isMouseDragged()){
//			double diffX = getMouseX() - getPreMouseX();
//			double diffY = getMouseY() - getPreMouseY();
//
//			if(Math.abs(diffY) > MOUSE_SCALING_THRESHOLD) { // scaling
//
//				if( (diffY) > 0){
//					scale += (Math.abs(diffY) - MOUSE_SCALING_THRESHOLD) * SCALE_STEP;
//				}else{
//					scale -= (Math.abs(diffY) - MOUSE_SCALING_THRESHOLD) * SCALE_STEP;
//				}
//
//				if(scale < MIN_SCALE){
//					scale = MIN_SCALE;
//				}
//
//				update();
//
//			} else { // scrolling speed
//				scrollSpeed += diffX * MOUSE_SCROLL_SPEED_FACTOR;
//			}
//		}
//
//		// scrolling
//		{
//			scroll += scrollSpeed / FPS;
//
//			scrollSpeed *= SCROLL_SPEED_DAMPING_FACTOR;
//			if( Math.abs(scrollSpeed) < SCROLL_SPEED_EPS ) {
//				scrollSpeed = 0.0;
//			}
//		}
//
//		g.translate(getWidth()/2.0, getHeight()/2.0, 0);
//    	g.render(titleText);
//
//    	g.pushMatrix();
//    	{
//    		g.translate(scroll, 0);
//
//    		for(Line l: scaleLines){
//    			g.render(l);
//    		}
//
//    		for(Text t: scaleTexts){
//    			g.render(t);
//    		}
//
//    		g.render(scaleMainLine);
//
//    		for(GeneElement ge: geneElements){
//    			g.pushMatrix();
//    			g.translate(ge.getX(), ge.getY());
//    			if(ge.isMouseOver((int)(getMouseX() - getWidth() / 2.0 - scroll - ge.getX()), (int)(getMouseY() - getHeight() / 2.0 - ge.getY()))){
//    				annotationText.setText(ge.getName());
//    				g.render(annotationText);
//    			}
//    			g.render(ge.getRect());
//    			g.popMatrix();
//    		}
//    	}
//    	g.popMatrix();
//	}

	@Override
	public void setup() {

		// load gene info
		GeneLoader loader = new GeneLoader();
		loader.load(REMOTE_DATA_URL);

		genes = loader.getGenes();
		exons = loader.getExons();
		viewScale = loader.getViewScale();

		// initialize view
		update();

		setFPS(FPS);
		setSize(1024, 768);

		{
			Font f = new Font("San-Serif");
			f.setSize(20);

			titleText = new Text(TITLE, f, -100, 330);
			titleText.setStrokeColor(ColorSet.WHITE);
		}

		{
			Font f = new Font("Sans-Serif");
			f.setSize(14);

			annotationText = new Text(annotation, f, 0, 10);
			annotationText.setStrokeColor(ColorSet.WHITE);
		}
	}

	@Override
    public void update() {
		setupRefGene();
		setupScale();
	}

    @Override
    public void exit() {
    }

	private void setupRefGene(){
    	double posx, posy;
    	geneElements.clear();

    	for( Exon e: exons ){
    		GeneElement ge = new GeneElement(e, scale);

    		posx = ((e.getEnd() + e.getStart()) / 2.0 - viewScale.getStart() - viewScale.getLength() / 2.0) * scale;
    		posy = GENE_ELEMENT_ORDER_STEP * e.getOrder();

    		ge.setX(posx);
    		ge.setY(posy);

    		addObject(ge);

    		geneElements.add(ge);
    	}

    	for( Gene g: this.genes ){
    		GeneElement ge = new GeneElement(g, scale);

    		posx = ((g.getEnd() + g.getStart())/2.0 - viewScale.getStart() - viewScale.getLength() / 2.0) * scale;
    		posy = GENE_ELEMENT_ORDER_STEP * g.getOrder();

    		ge.setX(posx);
    		ge.setY(posy);

    		addObject(ge);

    		geneElements.add(ge);
    	}
	}

    private void setupScale(){
    	Font f = new Font("San-Serif");
        f.setSize(10);

        scaleLines.clear();
        scaleTexts.clear();

        final int length = viewScale.getLength();
        final int halfLength = length / 2;
    	final int digit = (int) Math.log10(length) + 1;
    	final double step = Math.pow(10, (digit - 2) );
    	final int numScales = (int) (length / step) + 1;

    	for( int i=0; i<numScales; i++ ) {
    		Line l = new Line( (i * step - halfLength )* scale, HALF_SCALE_HEIGHT,
					   		   (i * step - halfLength ) * scale, - HALF_SCALE_HEIGHT);
    		l.setStrokeColor(ColorSet.WHITE);
    		scaleLines.add(l);

    		Text t = new Text(Integer.toString((int)(i * step + viewScale.getStart())), f,
    						  (int)((i * step - halfLength ) * scale),
    						  (int)-SCALE_HEIGHT);
    		t.setStrokeColor(ColorSet.WHITE);
    		scaleTexts.add(t);
    	}

    	scaleMainLine = new Line(- scale * halfLength, 0, scale * halfLength, 0);
    	scaleMainLine.setStrokeColor(ColorSet.WHITE);
    }

    @Override
    public void mouseEvent(MouseEvent e, MouseButton b) {
    }

    @Override
    public void keyEvent(KeyEvent e) {
    }

    public static void main( String[] args )
    {
        AppletRunner.run("genome.view.GeneView", "Gene View");
    }
}


