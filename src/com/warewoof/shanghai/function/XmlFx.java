package com.warewoof.shanghai.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.warewoof.shanghai.GlVar;
import com.warewoof.shanghai.object.ChildAttribute;
import com.warewoof.shanghai.object.ListItem;
import com.warewoof.shanghai.object.MapItem;

@SuppressWarnings("unused")
public class XmlFx {
	private static final String TAG = "XmlFunc";
	
	public static XmlPullParser getCatalogHandle(Context mContext) {		
		return getFileHandle(mContext, GlVar.CATALOG_LOCAL_FILE_NAME, GlVar.XML_FILE);
	}
		
	public static XmlPullParser getFileHandle(Context mContext, String fileName, int defaultXML) {
		final String TAG = "GetFileHandle";
		
		XmlPullParser xpp = null;
		try {
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();		
			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			
			File file = null;
			if (GlVar.CATALOG_USE_INTERNAL_MEMORY) {
				file = mContext.getDir(fileName, Context.MODE_PRIVATE);
			} else {
				file = new File(GlVar.CATALOG_LOCAL_DIRECTORY + fileName);
			}
			if (file.exists()) {
				FileInputStream fis = null;
				if (GlVar.CATALOG_USE_INTERNAL_MEMORY) {  // using Internal Memory will throw error if file doesn't exist, "file.exists()" does not catch it
					try {
						fis = mContext.openFileInput(fileName);
					} catch (FileNotFoundException e) {
						LogFx.debug(TAG, "Exception:" + e.toString() + " file:" + fileName + " defualt:" + String.valueOf(defaultXML));
						return mContext.getResources().getXml(defaultXML);					
					}
				} else {
					fis = new FileInputStream(file);
				}
		    	xpp.setInput(new InputStreamReader(fis));
			} else {
				xpp=mContext.getResources().getXml(defaultXML);
			}
		} catch (Exception e) {
			LogFx.debug(TAG, "getFileHandle:" + e.toString() + " file:" + fileName + ", reading from defualt:" + String.valueOf(defaultXML));
			return mContext.getResources().getXml(defaultXML);
		} 
		return xpp;
	}
	
	/** This returns a sub-list for from the TOC index
	 *  
	 * @param parentName The parent of the list
	 * @param searchName The list name
	 * @return The list of items found under searchName
	 */
	public static ArrayList<ListItem> parseList(Context mContext, String parentName, String searchName) {
		final String TAG = "parseList";
	    boolean xmlSearchFinished = false;
	    ArrayList<ListItem> listItems = new ArrayList<ListItem>();
	    long startTime;
	    long endTime;
	    int childrenCount = 0;
	    int spacerColor = GlVar.SPACER_COLOR;
	    String attValue = "";
	    String nodeName = "";
	    
	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    
	    try {
	    	
	      XmlPullParser xpp = getCatalogHandle(mContext);
	      
	      while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	        if (xpp.getEventType()==XmlPullParser.START_TAG) {
	        	
	        	if (xpp.getAttributeValue(null, "name").equals(parentName)) {
		        	LogFx.debug(TAG, "parentName found: " + xpp.getName());
		        	
	        		xpp.next();
	        		
	        		
	        		while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	        	        if (xpp.getEventType()==XmlPullParser.START_TAG) {
	        	        	
				        	if (xpp.getAttributeValue(null, "name").equals(searchName)) {
				        		LogFx.debug(TAG, "searchName found: " + xpp.getName());
				        		
				        		
				        		int itemLevel = xpp.getDepth();
				        		int listLevel = itemLevel + 1;
				        		xpp.next();
				        		
				        		while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
				        	        if (xpp.getEventType()==XmlPullParser.START_TAG) {
				        	        	
				        	        	if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
				        	        			//xpp.getName().equalsIgnoreCase(GlVar.CALENDAR_LIST_EXCEPTION)) {
				        	        		childrenCount = childrenCount + 1;
				        	        	}
				        	        	if (xpp.getDepth() == listLevel) {
				        	        		attValue = xpp.getAttributeValue(null, "name");
				        	        		nodeName = xpp.getName();
				        	        		if (xpp.getAttributeValue(null, "color") != null) {
				        	        			spacerColor = Color.parseColor(xpp.getAttributeValue(null,  "color"));
				        	        		} else {
				        	        			spacerColor = GlVar.SPACER_COLOR;
				        	        		}
				        	        	}
				        	        }
				        	        if (xpp.getEventType()==XmlPullParser.END_TAG) {
				        	        	if (xpp.getDepth() == listLevel) {
				        	        		listItems.add(new ListItem(attValue, searchName, nodeName, spacerColor, childrenCount));
				        	        		childrenCount = 0;
				        	        	}
				        	        	if (xpp.getDepth() < listLevel) {
				        	        		LogFx.debug(TAG, "breaking loop, depth is < listLevel");
				        	        		
				        	        		xmlSearchFinished = true;
				        	        		break;
				        	        	}
				        	        }   
				        	        xpp.next();
				        	        
				        		}	     		
				        	}
	        	        }
	        	        if (xmlSearchFinished) {
	        	        	break;
	        	        }
	        	        xpp.next();
	        		}
	        	}
	        }
	        if (xmlSearchFinished) {
	        	break;
	        }
	        xpp.next();
	      }
	    }
	    catch (Exception t) {
	      Toast.makeText(mContext, "Error on creating list, you may need to redownload the directory in Preferences", Toast.LENGTH_LONG).show();
	    }
	    endTime = android.os.SystemClock.uptimeMillis();
	    LogFx.debug(TAG, "Excution time: "+(endTime-startTime)+" ms, for "+searchName);
	    
	    return listItems;
	}
	
	
	public static ArrayList<ChildAttribute> parseChild(Context mContext, String parentName, String searchName) {
		
		final String TAG = "parseChild";
	    boolean xmlSearchFinished = false;
	    int childLevel;
	    int attributeLevel;
	    String attributeName;
	    ArrayList<ChildAttribute> childAtts = new ArrayList<ChildAttribute>();
	    long startTime;
	    long endTime;

	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    try {
	      //XmlPullParser xpp=getResources().getXml(XmlProp.XML_FILE);
	    	XmlPullParser xpp = getCatalogHandle(mContext);
	      while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	        if (xpp.getEventType()==XmlPullParser.START_TAG) {
	        	
	        	if (xpp.getName().equals(GlVar.CHILDREN_SECTION)) {
		        	LogFx.debug("XmlFunc", "Children section found: " + xpp.getName());
	        		xpp.next();
	        		
	        		while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	        			
	        	        if (xpp.getEventType()==XmlPullParser.START_TAG) {	 
	        	        	if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
					        	if (xpp.getAttributeValue(null, "name").equalsIgnoreCase(searchName)) { 
					        		if (checkParent(xpp.getAttributeValue(null, "parent"), parentName)) {	
						        		LogFx.debug("XmlFunc", "child found: " + xpp.getName());
						        		
						        		childLevel = xpp.getDepth();
						        		attributeLevel = childLevel+1;
						        		xpp.next();			        		
						        		while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
						        	        if (xpp.getEventType()==XmlPullParser.START_TAG) {

						        	        	if (xpp.getDepth() == attributeLevel) {
						        	        		int attCount = xpp.getAttributeCount();
						        	        		ChildAttribute childAtt = new ChildAttribute();
						        	        		//childAtt.setNodeType(xpp.getName());
						        	        		for (int i=0; i<attCount; i++) {
							        	        		attributeName = xpp.getAttributeName(i);
							        	        		
								        	        	if (attributeName.equals("name")) {
								        	        		childAtt.setName(xpp.getAttributeValue(i));
								        	        	} else if (attributeName.equals("value")) {
								        	        		childAtt.setValue(xpp.getAttributeValue(i));
								        	        	} else if (attributeName.equals("options")) {
								        	        		childAtt.setOptions(xpp.getAttributeValue(i));
								        	        	} else if (attributeName.equals("link")) {
								        	        		childAtt.setLink(xpp.getAttributeValue(i));
								        	        	}
						        	        		}
						        	        		childAtt.setChildName(searchName);
						        	        		childAtt.setParentName(parentName);
						        	        		childAtts.add(childAtt);
						        	        	} else {
						        	        		xmlSearchFinished = true;
						        	        		break;
						        	        	}
						        	        }
						        	        xpp.next();
						        		}
					        		}
					        	}
	        	        	}
	        	        }
	        	        if (xmlSearchFinished) {
	        	        	break;
	        	        }
	        	        xpp.next();
	        		}
	        	}
	        }
	        if (xmlSearchFinished) {
	        	break;
	        }
	        xpp.next();
	      }
	    }
	    catch (Exception t) {
	      Toast.makeText(mContext, "parseChild: "+t.toString(), Toast.LENGTH_LONG).show();
	    }
	    endTime = android.os.SystemClock.uptimeMillis();
	    LogFx.debug("XmlFunc", "Excution time: "+(endTime-startTime)+" ms, for "+searchName);
	    
	    return childAtts;
	}
	
	public static ArrayList<ListItem> filterSearchResults(Context mContext, String searchSectionParent, String searchSection, ArrayList<ListItem> listItems) {
		final String TAG = "filterSearchResults";
		long startTime;
	    long endTime;
	    int sectionDepth = 0;
	    ArrayList<ListItem> filterList = new ArrayList<ListItem>();
	    boolean filterSearchFinished = false;
	    ArrayList<String> tempParent = null;
	    int tempDepth = 0;
	    LogFx.debug("SearchXml", "Begin creating filter list for section " + searchSection + ", parent " + searchSectionParent);
	    
	    
	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    // First, build a list of items that fall under the search section and parent
	    try {
	    	XmlPullParser xpp = getCatalogHandle(mContext);
	    	while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	    		if (xpp.getEventType()==XmlPullParser.START_TAG) {
	    			if (xpp.getAttributeValue(null, "name").equals(searchSectionParent)) {
	    				LogFx.debug("SearchXml", "FilterSearchResults SectionParent found");
	    				xpp.next();
	    				
	    				while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	    					if (xpp.getEventType()==XmlPullParser.START_TAG) {
	    						if (xpp.getAttributeValue(null, "name").equals(searchSection)) {
	    							LogFx.debug("SearchXml", "FilterSearchResults Section found");
	    							sectionDepth = xpp.getDepth();
	    							xpp.next();
	    							
	    							//String tempParent = searchSection;
	    							tempParent = new ArrayList<String>();
	    							tempParent.add(searchSection);
	    							tempDepth = sectionDepth;
	    							while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	    								if (xpp.getEventType()==XmlPullParser.START_TAG) {
				    						if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
				    							if (xpp.getAttributeValue(null, "color") != null) {
				    								//filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent, XmlProp.CHILD_NODE_TAG, Color.parseColor(xpp.getAttributeValue(null, "color"))));
				    								filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent.get(tempParent.size()-1), GlVar.CHILD_NODE_TAG, Color.parseColor(xpp.getAttributeValue(null, "color"))));
					        	        		} else {
					        	        			//filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent, XmlProp.CHILD_NODE_TAG, XmlProp.SPACER_COLOR));
					        	        			filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent.get(tempParent.size()-1), GlVar.CHILD_NODE_TAG, GlVar.SPACER_COLOR));
					        	        		}
				    						} else {
				    							//tempParent = xpp.getAttributeValue(null, "name");
				    							tempParent.add(xpp.getAttributeValue(null, "name"));
				    							//LogFx.debug("filterSearchResults", "parent list" +PrintArray(tempParent));
				    							tempDepth = xpp.getDepth();
				    						}
	    								} else if (xpp.getEventType()==XmlPullParser.END_TAG) {
	    									if (xpp.getDepth() <=tempDepth) {
	    										tempParent.remove(tempParent.size()-1);
	    										tempDepth--;
	    										//LogFx.debug("filterSearchResults", "parent list" +PrintArray(tempParent));
	    									}
	    									if (xpp.getDepth()<=sectionDepth) {  // search traverse is exiting (or has exited) the search section
	    										LogFx.debug("SearchXml", "Breaking from loop, section is finished");
	    										filterSearchFinished = true;
	    					    				break;
	    					    			}
	    								}
	    								xpp.next();
	    							}
	    						}
	    					}
	    					if (filterSearchFinished) {
	    						break;
	    					}
	    					xpp.next();
	    				}	
	    			}	
	    		} else if (xpp.getEventType()==XmlPullParser.END_TAG) {
	    			if (xpp.getName().equals(GlVar.CHILDREN_SECTION)) {  // sanity check
	    				filterSearchFinished = true;
	    				LogFx.debug("SearchXml", "Breaking from loop, children section reached");
	    				break;
	    			}
	    		}
	    		if (filterSearchFinished) {
					break;
				}
	    		xpp.next();				    		
	    	}
				 
	    } 
	    catch (Exception t) {
	    	Toast.makeText(mContext, "filterSearchResults: "+t.toString(), Toast.LENGTH_LONG).show();
	    }
	    
	    //Second, filter original search results against approved items list
	    LogFx.debug("SearchXml", "filterList size = " + filterList.size());
	    LogFx.debug("SearchXml", "listItems size = " + listItems.size());
	    
	    ArrayList<ListItem> tempList = new ArrayList<ListItem>();
	    for (int j=0; j<filterList.size(); j++) {
	    	for (int i=0; i<listItems.size(); i++) {
	    	
	    		//if (filterList.get(j).getItemName().equals(listItems.get(i).getItemName()) && 
	    			//filterList.get(j).getItemParent().equals(listItems.get(i).getItemParent())) {
	    		if (filterList.get(j).getItemName().equals(listItems.get(i).getItemName()) && 
		    		checkParent(listItems.get(i).getItemParent(), filterList.get(j).getItemParent())) {
	    			
	    				tempList.add(filterList.get(j));
	    				break;
	    				//listItems.get(i).setItemParent(filterList.get(j).getItemParent());
	    				//listItems.get(i).setItemColor(filterList.get(j).getItemColor());
		    			//tempList.add(listItems.get(i));
		 //   			break;
		    			//LogFx.debug("SearchXml", "Adding to temp list=>" + filterList.get(j).getItemName());
		    			//LogFx.debug("SearchXml", filterList.get(j).getItemName() + " " + listItems.get(i).getItemName());
		    			//LogFx.debug("SearchXml", filterList.get(j).getItemParent() + " " + listItems.get(i).getItemParent());
		    	
	    		}
	    	}
	    
	    }
	    LogFx.debug("SearchXml", "returned list size = " + tempList.size());
	    
	    
	    
	    endTime = android.os.SystemClock.uptimeMillis();
	    LogFx.debug("FilterSearchResults", "Excution time: "+(endTime-startTime)+" ms");
	    return tempList;
	}
	
	
	public static ArrayList<ListItem> searchAll(Context mContext, String searchString) {
		final String TAG = "searchAll";
	    long startTime;
	    long endTime;
	    ArrayList<ListItem> listItems = new ArrayList<ListItem>();
	    String tempParent = "";
	    String tempChild = "";
	    
	    searchString = searchString.trim().toLowerCase(Locale.US).replace(" ", "");
	    LogFx.debug("searchAll", "Begin search for " + searchString);
	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    try {
	      //XmlPullParser xpp=getResources().getXml(XmlProp.XML_FILE);
	    	XmlPullParser xpp = getCatalogHandle(mContext);
	      while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	        if (xpp.getEventType()==XmlPullParser.START_TAG) {
	        	
	        	if (xpp.getName().equals(GlVar.CHILDREN_SECTION)) {
		        	
	        		xpp.next();	        		
	        		while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {	        			
	        	        if (xpp.getEventType()==XmlPullParser.START_TAG) {	 
	        	        	if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
	        	        		// store child and parent name for possible search result insertion
	        	        		tempChild = xpp.getAttributeValue(null, "name");
	        	        		tempParent = xpp.getAttributeValue(null, "parent");
	        	        
	        	        	} else if (xpp.getAttributeValue(null, "value") != null) {
	        	        		String compString = xpp.getAttributeValue(null, "value").toLowerCase(Locale.US).replace(" ", "");
	        	        		String compNameString = xpp.getAttributeValue(null, "name").toLowerCase(Locale.US).replace(" ", "");
	        	        		if (compString.contains(searchString) || compNameString.contains(searchString)) { //string comparison here
	        	        			listItems.add(new ListItem(tempChild, tempParent, GlVar.CHILD_NODE_TAG));
	        	        			//skip to next iteration	        	        			
	        	        		}
	        	        	}
	        	        } else if (xpp.getEventType()==XmlPullParser.END_TAG) {
	        	        	if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
	        	        		//optionally store with more query results
	        	        	}
						}
	        	        xpp.next();
	        		}
	        	}
	        }
	        xpp.next();
	      }
	    }
	    catch (Exception t) {
	      Toast.makeText(mContext, "searchAll: "+t.toString(), Toast.LENGTH_LONG).show();
	    }
	
	    endTime = android.os.SystemClock.uptimeMillis();
	    LogFx.debug("searchAll", "Search All Excution time: "+(endTime-startTime)+" ms");
	    LogFx.debug("searchAll", "Number of listItems found: " + String.valueOf(listItems.size()));
	    return listItems;
	}
	
	

	public static ArrayList<MapItem> getAllGeoLinks(Context mContext) {
		final String TAG = "getAllGeoLinks";
	    long startTime;
	    long endTime;
	    String tempParent = "";
	    String tempChild = "";
	    ArrayList<MapItem> mapItems = new ArrayList<MapItem>();

	    //LogFx.debug("getAllGeoLinks", "Begin search for geo");
	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    try {
	      //XmlPullParser xpp=getResources().getXml(XmlProp.XML_FILE);
	    	XmlPullParser xpp = getCatalogHandle(mContext);
	      while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	        if (xpp.getEventType()==XmlPullParser.START_TAG) {
	        	
	        	if (xpp.getName().equals(GlVar.CHILDREN_SECTION)) {
		        	
	        		xpp.next();	        		
	        		while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {	        			
	        	        if (xpp.getEventType()==XmlPullParser.START_TAG) {	 
	        	        	if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
	        	        		// store child and parent name for possible search result insertion
	        	        		tempChild = xpp.getAttributeValue(null, "name");
	        	        		tempParent = xpp.getAttributeValue(null, "parent");
	        	        
	        	        	} else if (xpp.getAttributeValue(null, "link") != null) {
	        	        		if (xpp.getAttributeValue(null, "link").toLowerCase(Locale.US).trim().startsWith("geo:")) {
	        	        			//listItems.add(new ListItem(tempChild, tempParent, XmlProp.CHILD_NODE_TAG));
	        	        			mapItems.add(new MapItem(xpp.getAttributeValue(null, "link"), //geo: string 
	        	        									xpp.getAttributeValue(null, "name"),  //Address name
	        	        									xpp.getAttributeValue(null, "value"), //Street address
	        	        									tempParent, 
	        	        									tempChild));
	        	        		}
	        	        	} 
						}
	        	        xpp.next();
	        		}
	        	}
	        }
	        xpp.next();
	      }
	    }
	    catch (Exception t) {
	      Toast.makeText(mContext, "getAllGeoLinks: "+t.toString(), Toast.LENGTH_LONG).show();
	    }
	
	    endTime = android.os.SystemClock.uptimeMillis();
	    //LogFx.debug("getAllGeoLinks", "Search All Excution time: "+(endTime-startTime)+" ms return list size: "+String.valueOf(mapItems.size()));
	    //LogFx.debug("getAllGeoLinks", "return list size: "+String.valueOf(mapItems.size()));
	    return mapItems;
	}
	

	public static ArrayList<MapItem> filterGeoResults(Context mContext, String searchSectionParent, String searchSection, ArrayList<MapItem> mapItems) {
		final String TAG = "filterGeoResults";
		long startTime;
	    long endTime;
	    int sectionDepth = 0;
	    ArrayList<ListItem> filterList = new ArrayList<ListItem>();
	    boolean filterSearchFinished = false;
	    ArrayList<String> tempParent = null;
	    int tempDepth = 0;
	    //LogFx.debug("filterGeoResults", "Begin creating filter list for section " + searchSection + ", parent " + searchSectionParent);
	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    // First, build a list of allowed children
	    try {
	    	XmlPullParser xpp = getCatalogHandle(mContext);
	    	while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	    		if (xpp.getEventType()==XmlPullParser.START_TAG) {
	    			if (xpp.getAttributeValue(null, "name").equals(searchSectionParent)) {
	    				//LogFx.debug("filterGeoResults", "FilterSearchResults SectionParent found");
	    				xpp.next();
	    				
	    				while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	    					if (xpp.getEventType()==XmlPullParser.START_TAG) {
	    						if (xpp.getAttributeValue(null, "name").equals(searchSection)) {
	    							//LogFx.debug("filterGeoResults", "FilterSearchResults Section found");
	    							sectionDepth = xpp.getDepth();
	    							xpp.next();
	    							
	    							//String tempParent = searchSection;
	    							tempParent = new ArrayList<String>();
	    							tempParent.add(searchSection);
	    							tempDepth = sectionDepth;
	    							while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
	    								if (xpp.getEventType()==XmlPullParser.START_TAG) {
				    						if (xpp.getName().equals(GlVar.CHILD_NODE_TAG)) {
				    							if (xpp.getAttributeValue(null, "color") != null) {
				    								//filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent, XmlProp.CHILD_NODE_TAG, Color.parseColor(xpp.getAttributeValue(null, "color"))));
				    								filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent.get(tempParent.size()-1), GlVar.CHILD_NODE_TAG, Color.parseColor(xpp.getAttributeValue(null, "color"))));
					        	        		} else {
					        	        			//filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent, XmlProp.CHILD_NODE_TAG, XmlProp.SPACER_COLOR));
					        	        			filterList.add(new ListItem(xpp.getAttributeValue(null, "name"), tempParent.get(tempParent.size()-1), GlVar.CHILD_NODE_TAG, GlVar.SPACER_COLOR));
					        	        		}
				    						} else {
				    							//tempParent = xpp.getAttributeValue(null, "name");
				    							tempParent.add(xpp.getAttributeValue(null, "name"));
				    							//LogFx.debug("filterGeoResults", "parent list" +PrintArray(tempParent));
				    							tempDepth = xpp.getDepth();
				    						}
	    								} else if (xpp.getEventType()==XmlPullParser.END_TAG) {
	    									if (xpp.getDepth() <=tempDepth) {
	    										tempParent.remove(tempParent.size()-1);
	    										tempDepth--;
	    										//LogFx.debug("filterGeoResults", "parent list" +PrintArray(tempParent));
	    									}
	    									if (xpp.getDepth()<=sectionDepth) {  // search traverse is exiting (or has exited) the search section
	    										//LogFx.debug("filterGeoResults", "Breaking from loop, section is finished");
	    										filterSearchFinished = true;
	    					    				break;
	    					    			}
	    								}
	    								xpp.next();
	    							}
	    						}
	    					}
	    					if (filterSearchFinished) {
	    						break;
	    					}
	    					xpp.next();
	    				}	
	    			}	
	    		} else if (xpp.getEventType()==XmlPullParser.END_TAG) {
	    			if (xpp.getName().equals(GlVar.CHILDREN_SECTION)) {  // sanity check
	    				filterSearchFinished = true;
	    				//LogFx.debug("filterGeoResults", "Breaking from loop, children section reached");
	    				break;
	    			}
	    		}
	    		if (filterSearchFinished) {
					break;
				}
	    		xpp.next();				    		
	    	}				 
	    } 
	    catch (Exception t) {
	    	Toast.makeText(mContext, "filterGeoResults: "+t.toString(), Toast.LENGTH_LONG).show();
	    	LogFx.debug("filterGeoResults", "exception parent list" + StringFx.PrintArray(tempParent));
	    	LogFx.debug("filterGeoResults", "exception tempDepth:" + String.valueOf(tempDepth));
	    }
	    
	    //Second, filter original search results against approved children list
	    //LogFx.debug("filterGeoResults", "filterList size = " + filterList.size());
	    //LogFx.debug("filterGeoResults", "mapItems size = " + mapItems.size());
	    
	    ArrayList<MapItem> tempList = new ArrayList<MapItem>();
	    for (int j=0; j<filterList.size(); j++) {
	    	for (int i=0; i<mapItems.size(); i++) {
	    	
	    		//if (filterList.get(j).getItemName().equals(mapItems.get(i).GetChildName()) && 
	    			//filterList.get(j).getItemParent().equals(mapItems.get(i).GetParentName())) {
	    		if (filterList.get(j).getItemName().equals(mapItems.get(i).GetChildName()) && 
	    			checkParent(mapItems.get(i).GetParentName(), filterList.get(j).getItemParent())) {
	    			mapItems.get(i).SetParentName(filterList.get(j).getItemParent());
	    			mapItems.get(i).SetTabColor(filterList.get(j).getItemColor());
	    			tempList.add(mapItems.get(i));	    			
	    		}
	    	}	    
	    }
	    //LogFx.debug("filterGeoResults", "returned list size = " + tempList.size());
	    
	    endTime = android.os.SystemClock.uptimeMillis();
	    //LogFx.debug("filterGeoResults", "Excution time: "+(endTime-startTime)+" ms returned list size: " + tempList.size());
	    return tempList;
	}
	
	public static ArrayList<MapItem> filterGeoResultsFromList(ArrayList<ListItem> filterList, ArrayList<MapItem> mapItems) {
		final String TAG = "filterGeoResultsFromList";
		long startTime;
	    long endTime;
	    int sectionDepth = 0;
	    boolean filterSearchFinished = false;
	    
	    startTime = android.os.SystemClock.uptimeMillis();
	    
	    
	    //Second, filter original search results against approved children list
	    LogFx.debug("filterMapResults", "filterList size = " + filterList.size());
	    LogFx.debug("filterMapResults", "mapItems size = " + mapItems.size());
	    
	    ArrayList<MapItem> tempList = new ArrayList<MapItem>();
	    for (int j=0; j<filterList.size(); j++) {
	    	for (int i=0; i<mapItems.size(); i++) {
	    	
	    		//if ((filterList.get(j)).getItemName().equals(mapItems.get(i).GetChildName()) && 
	    			//filterList.get(j).getItemParent().equals(mapItems.get(i).GetParentName())) {
	    		if ((filterList.get(j)).getItemName().equals(mapItems.get(i).GetChildName()) && 
		    		checkParent(mapItems.get(i).GetParentName(),filterList.get(j).getItemParent())) {
	    				mapItems.get(i).SetParentName(filterList.get(j).getItemParent());
		    			mapItems.get(i).SetTabColor(filterList.get(j).getItemColor());
		    			tempList.add(mapItems.get(i));
	    			
	    		}
	    	}	    
	    }
	    LogFx.debug("filterMapResults", "returned list size = " + tempList.size());
	    endTime = android.os.SystemClock.uptimeMillis();
	    LogFx.debug("filterMapResults", "Excution time: "+(endTime-startTime)+" ms");
	    return tempList;
	}
	
	private static boolean checkParent(String sequence, String parent) {
		List<String> items = Arrays.asList(sequence.split("\\s*,\\s*"));
		for (int i=0; i < items.size(); i++) {
			if (items.get(i).trim().equalsIgnoreCase(parent)) {
				return true;
			}
		}
		return false;		
	}
	
	
}



