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

package jp.xcoo.casmi.genome.view;

import jp.xcoo.casmi.genome.data.Exon;
import jp.xcoo.casmi.genome.data.Gene;
import casmi.graphics.color.ColorSet;
import casmi.graphics.color.RGBColor;
import casmi.graphics.element.Rect;

/**
 * element for drawing Exon and Gene
 *
 * @author K. Nishimura
 *
 */
public class GeneElement extends Rect {

	private static final double EXON_RECT_HEIGHT = 20;
	private static final double GENE_RECT_HEIGHT = 8;

	private String name;

	private double measure;

	private boolean selected = false;

	public GeneElement(Exon e, double measure) {
	    super(measure * e.getLength(), EXON_RECT_HEIGHT);

	    this.setStroke(false);

        switch( e.getType() ) {
        case KNOWN:
            this.setFillColor(new RGBColor(134/255.0, 186/255.0, 104/255.0));
            break;
        case REFERENCE_SEQUENCE:
            this.setFillColor(new RGBColor(134/255.0, 186/255.0, 204/255.0));
            break;
        case OTHER:
            this.setFillColor(ColorSet.WHITE);
            break;
        }

		this.measure = measure;

		this.name = "";
	}

	public GeneElement(Gene g, double measure){
	    super(measure * g.getLength(), GENE_RECT_HEIGHT);

        this.setStroke(false);

        switch( g.getType() ) {
        case KNOWN:
            this.setFillColor(new RGBColor(134/255.0, 186/255.0, 104/255.0));
            break;
        case REFERENCE_SEQUENCE:
            this.setFillColor(new RGBColor(134/255.0, 186/255.0, 204/255.0));
            break;
        case OTHER:
            this.setFillColor(ColorSet.WHITE);
            break;
        }

		this.measure = measure;
		this.name = g.getGroup();
	}

	public String getName() {
		return name;
	}

	public double getMeasure() {
		return measure;
	}

    public void setMeasure(double measure) {
		this.measure = measure;
	}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
