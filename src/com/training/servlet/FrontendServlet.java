package com.training.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.training.formbean.GoodsOrderForm;
import com.training.model.Goods;
import com.training.service.FrontendService;
import com.training.vo.BuyGoodsRtn;

/**
 * Servlet implementation class FrontendServlet
 */
public class FrontendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FrontendService frontendservice = FrontendService.getInstance();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FrontendServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGetAndPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGetAndPost(request, response);
	}

	protected void doGetAndPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 解决 POST请求中文亂碼問題
		req.setCharacterEncoding("UTF-8");

		String action = req.getParameter("action");
		switch (action) {

		case "buyGoods":
			buyGoods(req, resp);
			break;

		case "searchGoods":
			searchGoods(req, resp);
			break;
		}
	}

	private void buyGoods(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		GoodsOrderForm goodsorderform = new GoodsOrderForm();
		goodsorderform.setGoodsID(req.getParameterValues("goodsID"));
		goodsorderform.setBuyQuantity(Stream.of(req.getParameterValues("buyQuantity")).mapToInt(Integer::parseInt).toArray());
		goodsorderform.setPayPrice(Integer.parseInt(req.getParameter("inputMoney")));
		goodsorderform.setCustomerid(req.getParameter("customerID"));
		//取得網頁參數
		
		
		Map<String, Goods> queryBuyGoods = frontendservice.queryBuyGoods(goodsorderform);// 查詢購買品項資料庫資訊
		
		
		BuyGoodsRtn buygoodsRtn = frontendservice.priceCalc(goodsorderform, queryBuyGoods);//檢查金額是否足夠
		
		
		if (buygoodsRtn.getPayprice()>=buygoodsRtn.getTotalsprice()) {			
			Set<Goods> goodsOrders = frontendservice.createGoodsOrder(goodsorderform, queryBuyGoods); //建立訂單
			boolean updateresult = frontendservice.buyGoods(goodsOrders); //更新商品庫存
			System.out.println(buygoodsRtn.toString());
		} else {

			System.out.println(buygoodsRtn.toString());
		}

		resp.sendRedirect("VendingMachine.html");
	}

	private void searchGoods(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pageNo = req.getParameter("pageNo");
		String searchKeyword = req.getParameter("searchKeyword");
		List<Goods> pagesearch = frontendservice.pageSearch(searchKeyword, Integer.parseInt(pageNo));
		pagesearch.stream().forEach(p -> System.out.println(p.toString()));
		resp.sendRedirect("VendingMachine.html");
	}

}
