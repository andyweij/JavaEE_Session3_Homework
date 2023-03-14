package com.training.vo;

import com.training.model.Goods;

public class BuyGoodsRtn {
	private int payprice;
	private int totalsprice;
	private int returnprice;
	private String goodsinf;
	private Goods[] goods;

	public Goods[] getGoods() {
		return goods;
	}

	public void setGoods(Goods[] goods) {
		this.goods = goods;
	}

	public int getPayprice() {
		return payprice;
	}

	public void setPayprice(int payprice) {
		this.payprice = payprice;
	}

	public int getTotalsprice() {
		return totalsprice;
	}

	public void setTotalsprice(int totalsprice) {
		this.totalsprice = totalsprice;
	}

	public int getReturnprice() {
		return returnprice;
	}

	public void setReturnprice(int returnprice) {
		this.returnprice = returnprice;
	}

	public String getGoodsinf() {
		return goodsinf;
	}

	public void setGoodsinf(String string) {
		this.goodsinf = string;
	}

	@Override
	public String toString() {
		return "投入金額:" + payprice + "\n購買金額:" + totalsprice + "\n找零金額:" + returnprice + "\n" + goodsinf ;
	}
}
