package com.training.formbean;

public class GoodsOrderForm {
	private String[] goodsID;
	private int payPrice;
	private int[] buyQuantity;
	private String customerid;

	public String[] getGoodsID() {
		return goodsID;
	}
	public void setGoodsID(String[] goodsID) {
		this.goodsID = goodsID;
	}
	public int getPayPrice() {
		return payPrice;
	}
	public void setPayPrice(int payPrice) {
		this.payPrice = payPrice;
	}
	public int[] getBuyQuantity() {
		return buyQuantity;
	}
	public void setBuyQuantity(int[] buyQuantity) {
		this.buyQuantity = buyQuantity;
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	
	
	
	
	
}
