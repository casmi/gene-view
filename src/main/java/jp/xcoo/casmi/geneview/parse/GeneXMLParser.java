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

package jp.xcoo.casmi.geneview.parse;


import java.util.ArrayList;
import java.util.List;

import jp.xcoo.casmi.geneview.data.Exon;
import jp.xcoo.casmi.geneview.data.ViewScale;
import jp.xcoo.casmi.geneview.data.type.GeneOrientaion;
import jp.xcoo.casmi.geneview.data.type.GeneType;

import casmi.io.parser.XMLElement;

/**
 * RefGene Data XML parser
 *
 * @author K. Nishimura
 *
 */
public class GeneXMLParser {

	private List<Exon> exons = null;
	private ViewScale viewScale = null;

	public List<Exon> getExons() {
		return exons;
	}

	public ViewScale getViewScale() {
		return viewScale;
	}

	public void parse(XMLElement root) {
		XMLElement segmentNode = searchSegmentNode(root);

		if( segmentNode != null ) {
			parseSegment(segmentNode);
		}
	}

	private void parseSegment(XMLElement segmentNode) {

		final String FEATURE_TAG_NAME = "FEATURE";

		final String ID_ATTRIBUTE_NAME = "id";
    	final String START_ATTRIBUTE_NAME = "start";
    	final String STOP_ATTRIBUTE_NAME = "stop";

    	String chrName = segmentNode.getAttribute(ID_ATTRIBUTE_NAME);
    	int chrStart = Integer.parseInt(segmentNode.getAttribute(START_ATTRIBUTE_NAME));
    	int chrStop = Integer.parseInt(segmentNode.getAttribute(STOP_ATTRIBUTE_NAME));

    	this.viewScale = new ViewScale(chrName, chrStart, chrStop);

		this.exons = new ArrayList<Exon>();

		// If this element does not have children, return method.
        if (!segmentNode.hasChildren()) {
            return;
        }

        for (XMLElement child : segmentNode.getChildren()) {
        	if(child.getName().equalsIgnoreCase(FEATURE_TAG_NAME)){
        		Exon e = parseFeature(child, chrName);

        		if( e != null ) {
        			this.exons.add(e);
        		}
        	}
        }
	}

	private static XMLElement searchSegmentNode(XMLElement root) {

		final String SEGMENT_TAG_NAME = "SEGMENT";

		if (!root.hasChildren()) {
            return null;
        }

        for (XMLElement child : root.getChildren()) {
        	if(child.getName().equalsIgnoreCase(SEGMENT_TAG_NAME)){
        		return child;
        	} else {
        		XMLElement result = searchSegmentNode(child);

        		if( result != null ) {
        			return result;
        		}
        	}
        }

        return null;
	}

	private static Exon parseFeature(XMLElement featureNode, String chrName) {

		final String TYPE_TAG_NAME = "TYPE";

    	final String START_TAG_NAME = "START";
    	final String END_TAG_NAME = "END";
    	final String GROUP_TAG_NAME = "GROUP";
    	final String ORIENTATION_TAG_NAME ="ORIENTATION";
    	final String LINK_TAG_NAME = "LINK";

    	final String ID_ATTRIBUTE_NAME = "id";
    	final String HREF_ATTRIBUTE_NAME = "href";

    	String featureID = featureNode.getAttribute(ID_ATTRIBUTE_NAME);

    	if (!featureNode.hasChildren()) {
            return null;
        }

    	String featureType = "", featureOrientation = "", featureGroup = "", featureRef = "";
    	int featureStart = 0, featureEnd = 0;

        for (XMLElement child : featureNode.getChildren()) {
        	if(child.getName().equalsIgnoreCase(TYPE_TAG_NAME)){
        		featureType = child.getAttribute(ID_ATTRIBUTE_NAME);
        	}

        	if(child.getName().equalsIgnoreCase(START_TAG_NAME)){
        		if(child.hasContent()){
        			featureStart = Integer.parseInt(child.getContent());
        		}
        	}

        	if(child.getName().equalsIgnoreCase(END_TAG_NAME)){
        		if(child.hasContent()){
        			featureEnd = Integer.parseInt(child.getContent());
        		}
        	}

        	if(child.getName().equalsIgnoreCase(ORIENTATION_TAG_NAME)){
        		if(child.hasContent()){
        			featureOrientation = child.getContent();
        		}
        	}

        	if(child.getName().equalsIgnoreCase(GROUP_TAG_NAME)){
        		featureGroup = child.getAttribute(ID_ATTRIBUTE_NAME);
        	}

        	if(child.getName().equalsIgnoreCase(LINK_TAG_NAME)){
        		featureRef = child.getAttribute(HREF_ATTRIBUTE_NAME);
        	}
        }

        return new Exon(featureID, chrName, featureStart, featureEnd,
        				convertGeneOrientation(featureOrientation), featureRef, featureGroup, converGeneType(featureType));
	}

	private static GeneOrientaion convertGeneOrientation(String orientation) {
		final String PLUS_ORIENTATION = "+";
		final String MINUS_ORIENTATION = "-";

		if( orientation.equalsIgnoreCase(PLUS_ORIENTATION) ) {
			return GeneOrientaion.OrientationPlus;
		} else if( orientation.equalsIgnoreCase(MINUS_ORIENTATION) ) {
			return GeneOrientaion.OrientationMinus;
		} else {
			return GeneOrientaion.OrientationUnknown;
		}
	}

	private static GeneType converGeneType(String type) {
		final String KNOWN_GENE_TYPE = "knownGene";
		final String REF_SEQ_GENE_TYPE = "refGene";

		if( type.equalsIgnoreCase(KNOWN_GENE_TYPE) ) {
			return GeneType.KNOWN;
		} else if( type.equalsIgnoreCase(REF_SEQ_GENE_TYPE) ) {
			return GeneType.REFERENCE_SEQUENCE;
		} else {
			return GeneType.OTHER;
		}
	}
}
