package com.training.service;

import java.util.List;
import java.util.Set;

import com.training.dao.BackEndDao;
import com.training.model.Goods;
import com.training.vo.SalesReport;

public class BackendService {

	private static BackendService backendservice = new BackendService();
	private static BackEndDao backenddao = BackEndDao.getInstance();

	private BackendService() {
	}

	public static BackendService getInstance() {
		return backendservice;

	}

	public List<Goods> queryGoods() {

		return backenddao.queryGoods();
	}

	public boolean modifyGood(Goods good) {

		return backenddao.updateGood(good);
	}

	public boolean createGood(Goods good) {

		return backenddao.createGood(good);
	}

	public Set<SalesReport> querySalesReport(String queryStartDate,String queryEndDate) {

		return backenddao.querySalesReport(queryStartDate,queryEndDate);
	}
}
