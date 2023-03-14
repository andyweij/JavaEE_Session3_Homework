package com.training.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.training.formbean.GoodsOrderForm;
import com.training.model.Goods;
import com.training.model.Member;
import com.training.vo.BuyGoodsRtn;

public class FrontEndDao {
	private static FrontEndDao frontendDao = new FrontEndDao();
	private static BackEndDao backendDao = new BackEndDao();

	public static FrontEndDao getInstance() {
		return frontendDao;
	}
	public Member ByIdentificationNo(String identificationNo) {
		Member member = null;
		String querysql = "SELECT * FROM BEVERAGE_MEMBER WHERE identification_no = ?";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement pstmt = conn.prepareStatement(querysql)) {
			conn.setAutoCommit(false);
			pstmt.setString(1, identificationNo);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					member = new Member();
					member.setCustomerName(rs.getString("CUSTOMER_NAME"));
					member.setIdentificationNo(rs.getString("IDENTIFICATION_NO"));
					member.setPassword(rs.getString("PASSWORD"));
				}
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return member;
	}
	
	public BuyGoodsRtn BuyGoods(Map<Goods,Integer> goodsOrders,GoodsOrderForm goodsorderform) {
		BuyGoodsRtn buygoodsRtn=new BuyGoodsRtn();
		String updateSQL = "UPDATE beverage_goods SET QUANTITY = ? WHERE GOODS_ID = ? ";
		List<Goods> goods = backendDao.queryGoods();
		int total = 0;
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();) {
			// 設置交易不自動提交
			conn.setAutoCommit(false);
			try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
				StringBuffer sb=new StringBuffer();
				for (int i = 0; i < goodsorderform.getGoodsID().length; i++) {

					for (Goods querygood : goods) {
						int count = 1;
						if (querygood.getGoodsID().equals(goodsorderform.getGoodsID()[i])
								&& goodsorderform.getBuyQuantity()[i] > 0) {
							if (querygood.getGoodsQuantity() >= goodsorderform.getBuyQuantity()[i]) {
								total += goodsorderform.getBuyQuantity()[i]
										* querygood.getGoodsPrice();
								stmt.setInt(count++, querygood.getGoodsQuantity()
										- goodsorderform.getBuyQuantity()[i]);
								stmt.setString(count++, goodsorderform.getGoodsID()[i]);
								stmt.addBatch();
								sb.append("商品名稱："+querygood.getGoodsName()+" 商品金額:"+querygood.getGoodsPrice()+" 購買數量:"+goodsorderform.getBuyQuantity()[i]+"\n");
								
								break;
							} else if (goodsorderform.getBuyQuantity()[i] == 0) {
								continue;
							} else {
								total += querygood.getGoodsQuantity() * querygood.getGoodsPrice();
								stmt.setInt(count++, 0);
								stmt.setString(count++, goodsorderform.getGoodsID()[i]);
								stmt.addBatch();
								sb.append("商品名稱："+querygood.getGoodsName()+" 商品金額:"+querygood.getGoodsPrice()+" 購買數量:"+querygood.getGoodsQuantity()+"\n");
								break;
							}
						} else {
							continue;
						}
					}
				}
				int[] insertCounts = stmt.executeBatch();
				conn.commit();
				buygoodsRtn.setPayprice(goodsorderform.getPayPrice());
				buygoodsRtn.setTotalsprice(total);
				buygoodsRtn.setReturnprice(goodsorderform.getPayPrice()-total);
				buygoodsRtn.setGoodsinf(sb.toString());
				System.out.println(insertCounts);
//			for(int count : insertCounts ) {
//			System.out.println(count);
//			}
				System.out.println(total);
				
			} catch (SQLException e) {
				conn.rollback();
			}
		} catch (SQLException e) {

			e.getStackTrace();

		}

		return buygoodsRtn;
	}
	public Set<Goods> searchGoods(String searchKeyword, int startRowNo, int endRowNo) {
		Set<Goods> goods = new LinkedHashSet<>();
		String sk = "%" + searchKeyword + "%";
		String querysql = "SELECT * FROM (SELECT ROWNUM RM ,BG.* FROM BEVERAGE_GOODS BG WHERE LOWER(GOODS_NAME) LIKE ? )WHERE RM > ? AND RM < ? ";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement pstmt = conn.prepareStatement(querysql)) {
			conn.setAutoCommit(false);
			int count = 1;
			pstmt.setString(count++, sk);
			pstmt.setInt(count++, startRowNo);
			pstmt.setInt(count++, endRowNo);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Goods good = new Goods();
					good.setGoodsID(rs.getString("GOODS_ID"));
					good.setGoodsImageName(rs.getString("IMAGE_NAME"));
					good.setGoodsName(rs.getString("GOODS_NAME"));
					good.setGoodsPrice(rs.getInt("PRICE"));
					good.setGoodsQuantity(rs.getInt("QUANTITY"));
					good.setStatus(rs.getString("STATUS"));
					goods.add(good);
				}
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return goods;
	}
	public Map<String, Goods> queryBuyGoods(Set<String> goodsIDs) {
		// key:商品編號、value:商品
		Map<String, Goods> goods = new LinkedHashMap<>();
		String querysql = "SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID = ?";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement pstmt = conn.prepareStatement(querysql)) {
			conn.setAutoCommit(false);
			int count = 1;
			Iterator<String> it = goodsIDs.iterator();
			while (it.hasNext()) {
				pstmt.setString(count, it.next());
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						Goods good = new Goods();
						good.setGoodsID(rs.getString("GOODS_ID"));
						good.setGoodsImageName(rs.getString("IMAGE_NAME"));
						good.setGoodsName(rs.getString("GOODS_NAME"));
						good.setGoodsPrice(rs.getInt("PRICE"));
						good.setGoodsQuantity(rs.getInt("QUANTITY"));
						good.setStatus(rs.getString("STATUS"));
						goods.put(rs.getString("GOODS_ID") , good);
					}

				} catch (SQLException e) {
					conn.rollback();
					throw e;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return goods;
	}
	public boolean batchUpdateGoodsQuantity(Set<Goods> goods) {
		boolean updateSuccess = false;
		String updatesql = "UPDATE BEVERAGE_GOODS SET QUANTITY = ? WHERE GOODS_ID = ? ";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement pstmt = conn.prepareStatement(updatesql)) {
			for (Goods g : goods) {
				pstmt.setInt(1, g.getGoodsQuantity());
				pstmt.setString(2, g.getGoodsID());
				pstmt.addBatch();
			}
			int[] insertCounts = pstmt.executeBatch();

//			for (int c : insertCounts) {
//				System.out.println(c);
//			}
			conn.commit();
			updateSuccess = true;
		} catch (SQLException se) {
			se.getStackTrace();
		}
		return updateSuccess;
	}

	/**
	 * 建立訂單資料
	 * 
	 * @param customerID
	 * @param goodsOrders【訂單資料(key:購買商品、value:購買數量)】
	 * @return boolean
	 */
	public boolean batchCreateGoodsOrder(String customerID, Map<Goods, Integer> goodsOrders) {
		boolean insertSuccess = false;
		Set<Goods> goods=goodsOrders.keySet();
		int orderID = 0;
		String[] col = { "ORDER_ID" };
		String insertsql = "INSERT  INTO BEVERAGE_ORDER  VALUES (beverage_order_seq.nextval,SYSDATE,?,?,?,?)";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(insertsql, col)) {
				conn.setAutoCommit(false);
				for (Goods g : goods) {
					int count = 1;
					pstmt.setString(count++, customerID);
					pstmt.setString(count++, g.getGoodsID());
					pstmt.setInt(count++, g.getGoodsPrice());
					pstmt.setInt(count++, goodsOrders.get(g));
					pstmt.addBatch();
					int[] insertCounts = pstmt.executeBatch();
//					for (int c : insertCounts) {System.out.println(c);}
					ResultSet rsKeys = pstmt.getGeneratedKeys();
					ResultSetMetaData rsmd = rsKeys.getMetaData();
					int columnCount = rsmd.getColumnCount();
					while(rsKeys.next()) {
						for(int i=1;i<=columnCount;i++) {
					orderID = rsKeys.getInt(i);
					System.out.println("新增訂單編號:" + orderID);
					}
						}	
				}		
				conn.commit();
				insertSuccess = true;
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertSuccess;
	}
	public List<Goods> pageSerach(String searchKeyword,int pageNo){
		List<Goods> goodsList=new ArrayList<>();
		int startRowNo=(pageNo*3-2);
		int endRowNo=pageNo*3;
		if(searchKeyword==null) {
		searchKeyword="";
		}
		String sk = "%" + searchKeyword + "%";
		
		
		String querysql="SELECT * FROM ( SELECT ROWNUM ROW_NUM, bg.* FROM BEVERAGE_GOODS bg WHERE LOWER(GOODS_NAME) LIKE ? ) WHERE ROW_NUM >= ? AND ROW_NUM <= ? ";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement pstmt = conn.prepareStatement(querysql)) {
			conn.setAutoCommit(false);
			int count = 1;
			pstmt.setString(count++, sk);
			pstmt.setInt(count++, startRowNo);
			pstmt.setInt(count++, endRowNo);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Goods good = new Goods();
					good.setGoodsID(rs.getString("GOODS_ID"));
					good.setGoodsImageName(rs.getString("IMAGE_NAME"));
					good.setGoodsName(rs.getString("GOODS_NAME"));
					good.setGoodsPrice(rs.getInt("PRICE"));
					good.setGoodsQuantity(rs.getInt("QUANTITY"));
					good.setStatus(rs.getString("STATUS"));
					goodsList.add(good);
				}
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return goodsList;
	}
}
