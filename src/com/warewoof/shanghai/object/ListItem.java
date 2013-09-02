package com.warewoof.shanghai.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ListItem implements Serializable {

	 private String name;
	 private String node;
	 private String mParent;
	 private int mNumChildren = 0;
	 private int color;
	 private boolean colorIsSet = false;
	 
	 public ListItem(String name, String parent, String node) {
	   this.name = name;
	   this.mParent = parent;
	   this.node = node;
	 }
	 
	 public ListItem(String name, String parent, String node, int color) {
		   this.name = name;
		   this.mParent = parent;
		   this.node = node;
		   this.color = color;
		   this.colorIsSet = true;
		 }
	 
	 public ListItem(String name, String parent, String node, int color, int childrenCount) {
		   this.name = name;
		   this.mParent = parent;
		   this.node = node;
		   this.color = color;
		   this.colorIsSet = true;
		   this.mNumChildren = childrenCount;
		 }
	 
	 public String getItemName() {
		 return this.name;
	 }
	 
	 public String getItemParent() {
		 return this.mParent;
	 }
	 
	 public void setItemParent(String name) {
		 this.mParent = name.trim();
	 }
	 
	 public String getItemNode() {
		 return this.node;
	 }
	 
	 public int getItemColor() {
		 return this.color;
	 }
	 
	 public void setItemColor(int color) {
		 this.color = color;
		 this.colorIsSet = true;
	 }
	 
	 public boolean colorIsSet() {
		 return this.colorIsSet;
	 }
	 
	 public int getNumChildren() {
		 return this.mNumChildren;
	 }
	 
	
	
}