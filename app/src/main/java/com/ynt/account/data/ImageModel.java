package com.ynt.account.data;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class ImageModel {
	/**
	 * 按钮类型
	 */
	public static final int ITEMKIND_BUTTON = 0;
	/**
	 * 图片类型
	 */
	public static final int ITEMKIND_IMAGE = 1;
	/**
	 * 图片状态-新增
	 */
	public static final int ITEMSTATE_NEW = 0;
	/**
	 * 图片状态-删除
	 */
	public static final int ITEMSTATE_DEL = 1;
	/**
	 * 图片状态-未改变
	 */
	public static final int ITEMSTATE_UNCHANGED = 2;
	
	public static class ImageItem {
		private int itemKind;  // 类型：分为按钮和图片两种
		private int itemState;  // 图片状态
		private String imageUri;  // 图片路径
		private String imageName;  // 图片名称
		private boolean isChecked = false;
		private File imageFile = null;
		
		protected ImageItem(){
			itemState = ITEMSTATE_UNCHANGED;
		}
		
		public int getItemKind() {
			return itemKind;
		}
		void setItemKind(int itemKind) {
			this.itemKind = itemKind;
		}
		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public int getItemState() {
			return itemState;
		}

		void setItemState(int itemState) {
			this.itemState = itemState;
		}

		public String getImageUri() {
			return imageUri;
		}
		void setImageUri(String imageUri) {
			this.imageUri = imageUri;
		}
		public File getImageFile() {
			return imageFile;
		}

		public void setImageFile(File imageFile) {
			this.imageFile = imageFile;
		}

		public String getImageName() {
			return imageName;
		}
		void setImageName(String imageName) {
			this.imageName = imageName;
		}
		
		public ImageItem clone(){
			ImageItem cloneItem = new ImageItem();
			cloneItem.setImageName(imageName);
			cloneItem.setImageUri(imageUri);
			cloneItem.setItemKind(itemKind);
			cloneItem.setItemState(itemState);
			return cloneItem;
		}
	}
	
	public static class ImageGroup{
		private int groupNum;
		private boolean isChecked = false;
		private String groupName;      // 组名
		private String busidate;
		private Vector<ImageItem> imageItems;   // 图片列表
		private Vector<ImageItem> effiectiveItems;  // 有效的图片列表
		
		protected ImageGroup(){
			imageItems = new Vector<ImageItem>();
			effiectiveItems = new Vector<ImageItem>();
		}
		
		public String getGroupName() {
			return groupName;
		}
		void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		void setGroupName(Date date, int groupNum){
			String sDate = toDateString(date);
			setGroupName(String.format("%s-第%d组", sDate, groupNum));
		}
		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
		public String getBusidate() {
			return busidate;
		}

		void setBusidate(String date) {
			this.busidate = date;
		}
		
		void setBusidate(Date date){
			setBusidate(toDateString(date));
		}

		public int getGroupNum() {
			return groupNum;
		}

		void setGroupNum(int groupNum) {
			this.groupNum = groupNum;
		}
		@Override
		public int hashCode() {
			return groupName.hashCode();
		}
		
		@Override
		public String toString() {
			return groupName;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null) return false;
			if(o instanceof ImageGroup){
				return o.hashCode() == hashCode();
			} else {
				return false;
			}
		}
		
		public boolean equalDate(Date date){
			return getBusidate().equals(toDateString(date));
		}
		
		public int size(){
			return effiectiveItems.size();
		}
		
		public ImageItem getImage(int index){
			return effiectiveItems.get(index);
		}
		
		public void acceptChanged(){
			for(int i = imageItems.size() - 1; i>=0; i--){
				ImageItem item = imageItems.get(i);
				if(item.getItemState() == ITEMSTATE_UNCHANGED) continue;
				// 如果已经删除
				if(item.getItemState() == ITEMSTATE_DEL)
					removeImage(item);
				else if(item.getItemState() == ITEMSTATE_NEW)
					item.setItemState(ITEMSTATE_UNCHANGED);
			}
		}
		
		public ImageItem findItem(String imageUri){
			for(ImageItem item: effiectiveItems){
				if(item.imageUri.equals(imageUri))
					return item;
			}
			return null;
		}
		
		public Vector<ImageItem> getChangedItems(){
			Vector<ImageItem> changedItems = new Vector<ImageItem>();
			for(ImageItem item:imageItems){
				if(item.getItemState() == ITEMSTATE_UNCHANGED) continue;
				changedItems.add(item);
			}
			return changedItems;
		}
		
		public ImageItem addButton(){
			ImageItem item = findItem("");
			if(item == null){
				item = new ImageItem();
				item.setImageName("");
				item.setImageUri("");
				item.setItemState(ITEMSTATE_UNCHANGED);
				item.setItemKind(ITEMKIND_BUTTON);
				// 放到列表的第一个位置
				imageItems.insertElementAt(item, 0);
				effiectiveItems.insertElementAt(item, 0);
			}
			return item;
		}
		
		public void addImage(ImageItem newItem){
			if(newItem == null) return;
			imageItems.add(newItem);
			effiectiveItems.add(newItem);
		}
		
		public ImageItem addImage(String imageUri) {
			ImageItem item = findItem(imageUri);
			if(item == null){
				item = new ImageItem();
				item.setItemKind(ITEMKIND_IMAGE);
				item.setImageUri(imageUri);
				item.setItemState(ITEMSTATE_NEW);
				item.setImageName(String.format("%s%04d-%03d.jpg", busidate, groupNum, imageItems.size()+1));
				addImage(item);
			}
			return item;
		}
		
		public void deleteImage(String imageUri){
			ImageItem item = findItem(imageUri);
			if(item != null){
				item.setItemState(ITEMSTATE_DEL);
				effiectiveItems.remove(item);
			}
		}
		
		public void removeImage(ImageItem item){
			if(item == null) return;
			imageItems.remove(item);
			effiectiveItems.remove(item);
		}
		
		public void removeImage(String imageUri){
			ImageItem item = findItem(imageUri);
			removeImage(item);
		}
		
		public void removeImages(String[] imageUris){
			if(imageUris == null || imageUris.length == 0) return;
			for(String imageUri: imageUris)
				removeImage(imageUri);
		}
		
		public ImageGroup clone(){
			ImageGroup group = new ImageGroup();
			group.setGroupNum(groupNum);
			group.setGroupName(groupName);
			group.setBusidate(busidate);
			for(ImageItem item: effiectiveItems){
				group.addImage(item.clone());
			}
			return group;
		}
	}
	
	private Vector<ImageGroup> groups = new Vector<ImageGroup>();
	private String uploadlot;	   // 上传批次
	private String uploadOn;	   // 上传时间
	private String uploadBy;   // 上传人
	private File file;
	
	private static String toDateString(Date date){
		DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		return format.format(date);
	}
	
	public String getUploadlot() {
		return uploadlot;
	}
	public void setUploadlot(String uploadlot) {
		this.uploadlot = uploadlot;
	}
	public String getUploadOn() {
		return uploadOn;
	}
	public void setUploadOn(String uploadOn) {
		this.uploadOn = uploadOn;
	}
	public void setUploadOn(Date uploadOn) {
		setUploadOn(toDateString(uploadOn));
	}
	public String getUploadBy() {
		return uploadBy;
	}
	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int size(){
		return groups.size();
	}
	
	public ImageGroup get(int index){
		return groups.get(index);
	}
	
	public void addImage(int groupIndex, String imageUri) {
		ImageGroup group = get(groupIndex);
		if(group != null){
			group.addImage(imageUri);
		}
	}
	
	private int getMaxGroupNum(Date date){
		int maxGroupNum = 0;
		for(ImageGroup group: groups){
			if(!group.equalDate(date)) continue;
			if(group.getGroupNum()>maxGroupNum)
				maxGroupNum = group.getGroupNum();
		}
		return maxGroupNum;
	}
	
	public ImageGroup newGroup(){
		return newGroup(new Date());
	}
	
	public ImageGroup newGroup(Date date){
		ImageGroup group = new ImageGroup();
		int groupNum = getMaxGroupNum(date) + 1;
		group.setGroupNum(groupNum);
		group.setBusidate(date);
		group.setGroupName(date, groupNum);
		
		return group;
	}
	
	public void addGroup(ImageGroup newGroup) throws Exception{
		for(ImageGroup group: groups){
			if(group.equals(newGroup))
				throw new Exception("图片组[" + newGroup.toString() + "]在列表中已经存在，不允许插入重复图片组" );
		}
		
		groups.add(newGroup);
	}
	
	public int indexOfGroup(ImageGroup group){
		for(int i = 0; i<groups.size(); i++){
			if(groups.get(i).equals(group))
				return i;
		}
		return -1;
	}
	
	public void removeGroup(ImageGroup group){
		groups.remove(group);
	}
	
	public void clear(){
		groups.clear();
	}
	
	public void copyto(ImageModel model){
		if(model == null) return;
		model.groups.clear();
		model.setUploadlot(uploadlot);
		model.setUploadBy(uploadBy);
		model.setUploadOn(uploadOn);
		for(ImageGroup group: groups){
			model.groups.add(group.clone());
		}
	}
	
	public void acceptChanged(){
		for(ImageGroup group: groups){
			group.acceptChanged();
		}
	}
	
}
