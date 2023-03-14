package com.training.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.training.dao.FrontEndDao;
import com.training.formbean.GoodsOrderForm;
import com.training.model.Goods;
import com.training.vo.BuyGoodsRtn;

public class FrontendService {
	private static FrontendService frontendservice = new FrontendService();
	private static FrontEndDao frontenddao = FrontEndDao.getInstance();

	private FrontendService() {
	}

	public static FrontendService getInstance() {
		return frontendservice;
	}

	public Map<String, Goods> queryBuyGoods(GoodsOrderForm goodsorderform) {
		Set<String> goodsIDs = new HashSet<>();
		for (int i = 0; i < goodsorderform.getGoodsID().length; i++) {
			if (goodsorderform.getBuyQuantity()[i] > 0) {
				goodsIDs.add(goodsorderform.getGoodsID()[i]);
			}
		}
		return frontenddao.queryBuyGoods(goodsIDs);
	}

	public boolean buyGoods(Set<Goods> goodsOrders) {

		boolean updateSuccess = frontenddao.batchUpdateGoodsQuantity(goodsOrders);
		if (updateSuccess) {
			System.out.println("商品庫存更新成功!");
		}
		return updateSuccess;
	}

	public BuyGoodsRtn priceCalc(GoodsOrderForm goodsorderform, Map<String, Goods> queryBuyGoods) {
		int total = 0;
		BuyGoodsRtn buygoodsRtn = new BuyGoodsRtn();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < goodsorderform.getGoodsID().length; i++) {
			if (goodsorderform.getBuyQuantity()[i] > 0 && queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsQuantity() >= goodsorderform.getBuyQuantity()[i]) {
				total += queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsPrice()
						* goodsorderform.getBuyQuantity()[i];
				sb.append("商品名稱：" + queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsName() + " 商品金額:"
					+ queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsPrice() + " 購買數量:" +	goodsorderform.getBuyQuantity()[i] + "\n");
			} else if (goodsorderform.getBuyQuantity()[i] > 0 && queryBuyGoods.get(goodsorderform.getGoodsID()[i])
					.getGoodsQuantity() < goodsorderform.getBuyQuantity()[i]) {
				total += queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsPrice()
						* queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsQuantity();
			sb.append("商品名稱：" + queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsName() + " 商品金額:"
					+ queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsPrice() + " 購買數量:" +	queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsQuantity() + "\n");
		}else if(goodsorderform.getBuyQuantity()[i] > 0 && queryBuyGoods.get(goodsorderform.getGoodsID()[i])
					.getGoodsQuantity() ==0){
			total+=0;
			sb.append("商品名稱：" + queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsName() + " 商品金額:"
					+ queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsPrice() + " 購買數量:" +	queryBuyGoods.get(goodsorderform.getGoodsID()[i]).getGoodsQuantity() + "\n");
			}
		}
		if(goodsorderform.getPayPrice()>=total) {
		buygoodsRtn.setPayprice(goodsorderform.getPayPrice());
		buygoodsRtn.setTotalsprice(total);
		buygoodsRtn.setReturnprice(goodsorderform.getPayPrice()-total);
		buygoodsRtn.setGoodsinf(sb.toString());
		}else {
			buygoodsRtn.setPayprice(goodsorderform.getPayPrice());
			buygoodsRtn.setTotalsprice(total);
			buygoodsRtn.setReturnprice(goodsorderform.getPayPrice());
			buygoodsRtn.setGoodsinf("金額不足，無法購買");
		}
		return buygoodsRtn;
	}

	public Set<Goods> createGoodsOrder(GoodsOrderForm goodsorderform, Map<String, Goods> buyGoods) {
		boolean createResult = false;
		Set<String> goodsIDs = new HashSet<>();
		Map<String, Integer> buyGoodsquantity = new HashMap<>();
		for (int i = 0; i < goodsorderform.getGoodsID().length; i++) {
			if (goodsorderform.getBuyQuantity()[i] > 0) {
				goodsIDs.add(goodsorderform.getGoodsID()[i]);
				buyGoodsquantity.put(goodsorderform.getGoodsID()[i], goodsorderform.getBuyQuantity()[i]);
			}
		}
		Map<Goods, Integer> goodsOrders = new HashMap<>();
		goodsIDs.stream().forEach(goodsID -> {
			Goods g = buyGoods.get(goodsID);
			if (g.getGoodsQuantity() >= buyGoodsquantity.get(g.getGoodsID())) {
				goodsOrders.put(g, buyGoodsquantity.get(g.getGoodsID()));
			} else if(g.getGoodsQuantity()==0){}else {
				goodsOrders.put(g, g.getGoodsQuantity());
			}
		});
		Set<Goods> goods = goodsOrders.keySet();
		for (Goods good : goods) {
			good.setGoodsQuantity(good.getGoodsQuantity() - goodsOrders.get(good));
		}
		// 建立訂單
		createResult = frontenddao.batchCreateGoodsOrder(goodsorderform.getCustomerid(), goodsOrders);
		if (createResult) {
			System.out.println("建立訂單成功!");
		}
		return goods;
	}

	public BuyGoodsRtn BuyGoodsRtn(GoodsOrderForm goodsorderform, int totalprice, Map<String, Goods> queryBuyGoods) {
		BuyGoodsRtn buygoodsRtn = new BuyGoodsRtn();
//		if (goodsorderform.getPayPrice() >= totalprice) {
//			buygoodsRtn.setPayprice(goodsorderform.getPayPrice());
//			buygoodsRtn.setTotalsprice(totalprice);
//			buygoodsRtn.setReturnprice(goodsorderform.getPayPrice() - totalprice);
//			StringBuffer sb = new StringBuffer();
//			
//			queryBuyGoods.values().stream().forEach(g ->
//			sb.append("商品名稱：" + g.getGoodsName() + " 商品金額:"
//					+ g.getGoodsPrice() + " 購買數量:" +	g.getGoodsQuantity() + "\n"));
//			
//			buygoodsRtn.setGoodsinf(sb.toString());
//		} else {
//			buygoodsRtn.setPayprice(goodsorderform.getPayPrice());
//			buygoodsRtn.setTotalsprice(totalprice);
//			buygoodsRtn.setReturnprice(goodsorderform.getPayPrice());
//			buygoodsRtn.setGoodsinf("");
//		}
		return buygoodsRtn;
	}

	public List<Goods> pageSearch(String searchKeyword, int pageNo) {

		return frontenddao.pageSerach(searchKeyword, pageNo);
	}
}
