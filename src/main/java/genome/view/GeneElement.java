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
import casmi.graphics.color.Color;
import casmi.graphics.color.ColorSet;
import casmi.graphics.element.MouseOver;
import casmi.graphics.element.Rect;

/**
 * element for drawing Exon and Gene 
 * 
 * @author K. Nishimura
 *
 */
public class GeneElement {
	
	private static final double EXON_RECT_HEIGHT = 20;
	private static final double GENE_RECT_HEIGHT = 8;
	
	private Rect rect;
	private MouseOver mrect;
	
	private double x;
	private double y;
	
	private String name;
	
	private double scale;
	
	public GeneElement(Exon rge, double scale) {
		this.scale = scale;
		
		this.name = ""; //rge.getID();

		this.x = 0;
		this.y = 0;
		
		this.rect = makeRect(rge);
		this.mrect = new MouseOver(rect);
	}
	
	public GeneElement(Gene rgg, double scale){
		this.scale = scale;
		
		this.name = rgg.getGroup();

		this.x = 0;
		this.y = 0;
		
		this.rect = makeRect(rgg);
		this.mrect = new MouseOver(rect);
	}

	/*
     * to set up hit rect
     */
    private Rect makeRect(Exon e){
    	Rect r = new Rect(getScale() * e.getLength(), EXON_RECT_HEIGHT);
    	r.setStroke(false);
    	
    	switch( e.getType() ) {
		case KNOWN:
			r.setFillColor(new Color(134, 186, 104));
			break;
		case REFERENCE_SEQUENCE:
			r.setFillColor(new Color(134, 186, 204));
			break;
		case OTHER:
			r.setFillColor(Color.color(ColorSet.WHITE));
			break;
		}
    	
    	return r;
    }
    
	/*
     * to set up hit rect
     */
    private Rect makeRect(Gene g){
		Rect r= new Rect(getScale() * g.getLength(), GENE_RECT_HEIGHT);
		
		r.setStroke(false);
		
		switch( g.getType() ) {
		case KNOWN:
			r.setFillColor(new Color(134, 186, 104));
			break;
		case REFERENCE_SEQUENCE:
			r.setFillColor(new Color(134, 186, 204));
			break;
		case OTHER:
			r.setFillColor(Color.color(ColorSet.WHITE));
			break;
		}

    	return r;
    }	    
  
	public Rect getRect() {
		return rect;
	}
	
	public void setRect(Rect rect) {
		this.rect = rect;
	}
	
	public boolean isMouseOver(int x, int y) {
		return this.mrect.isMouseOver(x, y); 
	}

	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public String getName() {
		return name;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
