package model;


public class RecModel {

	private String mContentID;
	
	private String mVisitorID;
	
	private String mContentName;
	
	private String mCategoryName;
	
	private int mView;
	
	private int mDownload;

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

	public int getmView() {
		return mView;
	}

	public void setmView(int mView) {
		this.mView = mView;
	}

	public int getmDownload() {
		return mDownload;
	}

	public void setmDownload(int mDownload) {
		this.mDownload = mDownload;
	}
	
	public RecModel(){
		
	}
	
	public RecModel(String mContentID,String mVisitorID,String mContentName,String mCategoryName,int mView,int mDownload){
		this.setmContentID(mContentID);
		this.setmVisitorID(mVisitorID);
		this.setmCategoryName(mCategoryName);
		this.setmContentName(mContentName);		
		this.setmView(mView);
		this.setmDownload(mDownload);
	}
}
