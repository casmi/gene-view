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

package genome.net;

import genome.data.Exon;
import genome.data.Gene;
import genome.data.ViewScale;
import genome.parse.GeneXMLParser;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import casmi.exception.ParserException;
import casmi.io.Reader;
import casmi.net.HTTP;
import casmi.parser.XML;
import casmi.util.FileUtil;

/**
 * class for Loading RefGene Data from UCSC DAS server
 * 
 * @author K. Nishimura
 *
 */
public class GeneLoader {

	private static final String CACHE_DIRECTORY_PATH = casmi.util.SystemUtil.JAVA_TMP_PATH;
	
	private GeneXMLParser parser;
	private List<Gene> genes = null;

	public GeneLoader() {
		this.parser = new GeneXMLParser();
	}
	
	public ViewScale getViewScale() {
		return this.parser.getViewScale();
	}
	
	public List<Exon> getExons() {
		return this.parser.getExons();
	}

	public List<Gene> getGenes() {
		return genes;
	}
	
    public void load(String url){
    	final String cacheFilePath = CACHE_DIRECTORY_PATH + createDigest(url) + ".xml";
    	
    	if( FileUtil.exist(cacheFilePath) ) {
    		readFromCache(cacheFilePath);
    	} else {
    		readFromServer(url, cacheFilePath);
    	}
    	
    	this.genes = buildGenes(this.getExons());
    }
    
    private static String createDigest(String source) {
    	try {
    		MessageDigest md = MessageDigest.getInstance("MD5");

    		byte[] data = source.getBytes();
    		md.update(data);

    		byte[] digest = md.digest();

    		StringBuilder sb = new StringBuilder();
    		for (int i = 0; i < digest.length; i++) {
    			sb.append(Integer.toHexString(0xff & digest[i]));
    		}
    		return sb.toString();
    	} catch (Exception e) {
    		return null;
    	}
    }
    
    /*
	 * Read RefGene Data from cache
	 */
    private void readFromCache(String cachePath) {
    	XML xml = new XML();
    	
    	try {
    		xml.parseFile(new File(cachePath));
    	} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	parser.parse(xml);
    }
    
	/*
	 * Read RefGene Data to connect DAS server using type=refGene
	 */
	private void readFromServer(String url, String cachePath) {

		HTTP http = null;
		Reader reader = null;
		XML xml = new XML();

		try {
			http = new HTTP(url);
			reader = http.requestGet();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("can not download file");
		}
		
		try {
			xml.parseReader(reader);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			http.disconnect();
			reader.close();
		}
		
		try {
			xml.save(new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parser.parse(xml);
	} 

	private static Gene searchGene(List<Gene> genes, String group) {
		for( Gene g : genes ) {
			if( g.getGroup().equalsIgnoreCase(group) ) {
				return g;
			}
		}
		
		return null;
	}
	
    private static List<Gene> buildGenes(List<Exon> exons) {
    	
    	// create genes
    	
    	List<Gene> result = new ArrayList<Gene>();
    	
    	for(Exon e: exons){
    		Gene g = searchGene(result, e.getGroup());
    		
    		if( g == null ) {
    			
    			// add new gene
    			g = new Gene(e.getGroup(), e.getChr(), e.getStart(), e.getEnd(), e.getOrientation(), e.getType());
    			result.add(g);
    			
    		} else {
    			
    			// update gene start and stop
    			if(g.getStart() > e.getStart() ){
    				g.setStart( e.getStart() );
    			}
    			
    			if(g.getEnd() < e.getEnd() ){
    				g.setEnd( e.getEnd() );
    			}
    			
    		}
    	}
    	
    	
    	// setup ordering in accordance with +/- orientation and genes overlapping

    	List<Gene> tmpGenes = new ArrayList<Gene>();
    	
    	for(Gene g: result){
    		
    		int order = 0;
    		
    		switch( g.getOrientation() ) {
    		case OrientationPlus:
    			order ++;
    			break;
    		case OrientationMinus:
    			order --;
    			break;
    		}

    		for(Gene tg: tmpGenes) {
    			
    			if( g != tg && g.getOrientation() == tg.getOrientation() ) {
    				
    				if( g.checkOverlap(tg) ) {
    					switch( g.getOrientation() ) {
    		    		case OrientationPlus:
    		    			order ++;
    		    			break;
    		    		case OrientationMinus:
    		    			order --;
    		    			break;
    		    		}
    				}
    			}
    		}
    		    		
    		g.setOrder(order);
    		
    		tmpGenes.add(g);
    		
    		// setup order of exons
    		for(Exon e: exons){
    			if(e.getGroup().equalsIgnoreCase(g.getGroup())){
    				e.setOrder(order);
    			}
    		}
    	}
    	
    	return result;
    }
}