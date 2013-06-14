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

package jp.xcoo.casmi.genome.data;

import jp.xcoo.casmi.genome.data.type.GeneOrientaion;
import jp.xcoo.casmi.genome.data.type.GeneType;

/**
 * Exon in Gene
 * 
 * @author K. Nishimura
 *
 */
public class Exon {

	private String chr;
	private int start;
	private int end;
	private String id;
	private GeneOrientaion orientation;
	private String ref;
	private String group;
	private GeneType type;
	private int order;

	public Exon(String id, String chr, int start, int end, GeneOrientaion orientation, String ref, String group, GeneType type){
		this.id = id;
		this.chr = chr;
		this.start = start;
		this.end = end;
		this.orientation = orientation;
		this.ref = ref;
		this.group = group;
		this.type = type;
		this.order = 0;
	}
	
	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getLength() {
		return Math.abs(this.end - this.start);
	}

	public String getID() {
		return id;
	}

	public void setID(String featureid) {
		this.id = featureid;
	}

	public GeneOrientaion getOrientation() {
		return orientation;
	}

	public void setOrientation(GeneOrientaion orientation) {
		this.orientation = orientation;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public GeneType getType() {
		return type;
	}

	public void setType(GeneType type) {
		this.type = type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
