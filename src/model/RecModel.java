package model;


public class RecModel {

	private String mVisitorID;
	
	private String mContentID;
	
	private String mContentName;
	
	private String mCategoryName;
	
	private String mView;
	
	private String mDownload;

	public String getmContentID() {
		return mContentID;
	}

	public void setmContentID(String mContentID) {
		this.mContentID = mContentID;
	}

	public String getmVisitorID() {
		return mVisitorID;
	}

	public void setmVisitorID(String mVisitorID) {
		this.mVisitorID = mVisitorID;
	}

	public String getmContentName() {
		return mContentName;
	}

	public void setmContentName(String mContentName) {
		this.mContentName = mContentName;
	}

	public String getmCategoryName() {
		return mCategoryName;
	}

	public void setmCategoryName(String mCategoryName) {
		this.mCategoryName = mCategoryName;
	}

	public String getmView() {
		return mView;
	}

	public void setmView(String mView) {
		this.mView = mView;
	}

	public String getmDownload() {
		return mDownload;
	}

	public void setmDownload(String mDownload) {
		this.mDownload = mDownload;
	}
	
	public RecModel(){
		
	}
	
	public RecModel(String mVisitorID,String mContentID,String mContentName,String mCategoryName,String mView,String mDownload){
		this.setmVisitorID(mVisitorID);
		this.setmContentID(mContentID);
		this.setmCategoryName(mCategoryName);
		this.setmContentName(mContentName);		
		this.setmView(mView);
		this.setmDownload(mDownload);
	}
}
