package com.training.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.training.model.Goods;
import com.training.service.BackendService;
import com.training.vo.SalesReport;
@MultipartConfig
public class BackendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BackendService backendservice = BackendService.getInstance();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGetAndPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGetAndPost(request,response);
	}

	protected void doGetAndPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 解决 POST请求中文亂碼問題
		req.setCharacterEncoding("UTF-8");

		String action = req.getParameter("action");
		switch (action) {
		case "queryGoods":
			// 帳戶列表
			queryGoods(req, resp);
			break;
		case "updateGoods":
			// 帳戶修改
			updateGoods(req, resp);
			break;
		case "addGoods":
			// 帳戶新增
			addGoods(req, resp);
			break;
		case "querySalesReport":
			// 訂單查詢
			querySalesReport(req, resp);
			break;
		}
	}

	private void queryGoods(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<Goods> goods = backendservice.queryGoods();
		goods.stream().forEach(a -> System.out.println(a.toString()));

		// Redirect to view
		resp.sendRedirect("VM_Backend_GoodsList.html");
	}

	private void updateGoods(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 將表單資料轉換儲存資料物件
		Goods good = new Goods();	
		good.setGoodsID(req.getParameter("goodsID"));
		good.setGoodsPrice(Integer.parseInt(req.getParameter("goodsPrice")));
		good.setGoodsQuantity(Integer.parseInt(req.getParameter("goodsQuantity")));
		good.setStatus(req.getParameter("status"));
		boolean modifyResult = backendservice.modifyGood(good);
		String message = modifyResult ? "帳戶資料修改成功！" : "帳戶資料修改失敗！";
		System.out.println(message);
		// Redirect to view
		resp.sendRedirect("VM_Backend_GoodsReplenishment.html");
	}

	private void addGoods(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 將表單資料轉換儲存資料物件		
		try {
			String goodsImgPath = getInitParameter("GoodsImgPath");
			String serverGoodsImgPath = getServletContext().getRealPath(goodsImgPath);
			Part filePart = req.getPart("goodsImage");
			String fileName = filePart.getSubmittedFileName();
			Path serverImgPath = Paths.get(serverGoodsImgPath).resolve(fileName);
			try (InputStream fileContent = filePart.getInputStream();){
		        Files.copy(fileContent, serverImgPath, StandardCopyOption.REPLACE_EXISTING);
		    }
		Goods good = new Goods();
		good.setGoodsName(req.getParameter("goodsName"));
		good.setGoodsPrice(Integer.parseInt(req.getParameter("goodsPrice")));
		good.setGoodsQuantity(Integer.parseInt(req.getParameter("goodsQuantity")));
		good.setGoodsImageName(fileName);
		good.setStatus(req.getParameter("status"));
		boolean createResult = backendservice.createGood(good);
		String message = createResult ? "商品新增成功！" : "商品新增失敗！";
		System.out.println(message);
		}catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		// Redirect to view
		resp.sendRedirect("VM_Backend_GoodsCreate.html");
	}

	private void querySalesReport(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String queryStartDate = req.getParameter("queryStartDate");
		String queryEndDate = req.getParameter("queryEndDate");
		Set<SalesReport> salesreport = backendservice.querySalesReport(queryStartDate,queryEndDate);
		salesreport.stream().forEach(a -> System.out.println(a.toString()));

		// Redirect to view
		resp.sendRedirect("VM_Backend_GoodsSaleReport.html");
	}

}
