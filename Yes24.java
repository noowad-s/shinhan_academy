package project2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dto.Users;

public class Yes24 {
	static Users user = null; //<----- **** 이렇게 밖으로 빼기
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn = ConnectionUtils.getConnection();
		start(conn);
	}
	
	public static void start(Connection conn) {
		Scanner sc = new Scanner(System.in);
		welcomePage();
		while(true) {
			choosePage();
			int num = sc.nextInt();
			if(num == 0) { //로그인
				try {
					user = login(conn, sc);
					if(user != null) {
						System.out.println("로그인 성공!");
						System.out.println("login_id : " + user.getLogin_id() + " | " + "user_name : " + user.getUser_name());
					}
				} catch(SQLException e) {
					throw new RuntimeException("로그인 오류 발생", e);
				}
			}else if(num == 1) { //회원가입
				try {
					user = signUp(sc, conn);
					System.out.println("회원가입 성공!");
					System.out.println("login_id : " + user.getLogin_id() + " user_name : " + user.getUser_name());
				}catch(SQLException e) {
					throw new RuntimeException("회원가입 중 오류 발생", e); // 래핑해서 던짐 (권장)
				}
			}else if(num == 2) { //구매
				buy(sc,conn,user, 0);
				//로그인 체크, 
				if(user != null) { //회원이 없을 경우, 회원 가입이나 로그인을 해야 한다.
					lookUserPurcharseBook(conn, user);
				}
//				break;
			}else if(num == 3) {
				search(conn,sc);
			}else if(num == 4) {
				// 책 소개
				bookIntro(conn, sc);
			}else if(num == 5) {
				// 상점
				try {
					printShopReviews(sc, conn);
				} catch (SQLException e) {
					System.out.println("리뷰 확인 중 오류 발생: " + e.getMessage());
				}
			}else if(num == 6) { //나가기
				try {
					conn.close(); //connection 종료
					System.out.println("프로그램을 종료합니다!");
					break;
				}catch(SQLException e) {
					throw new RuntimeException("종료중 오류 발생", e);
				}
			}
		}
	}
	

	private static void search(Connection conn, Scanner sc) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println();
			System.out.println("" 
					+ "                .-'      `-.           \r\n"
					+ "              .'            `.         \r\n" 
					+ "             /                \\        \r\n"
					+ "            ;                 ;`       \r\n" 
					+ "            |                 |;       \r\n"
					+ "            ;                 ;|\r\n" 
					+ "            '\\               / ;       \r\n"
					+ "             \\`.           .' /        \r\n" 
					+ "              `.`-._____.-' .'         \r\n"
					+ "                / /`_____.-'           \r\n" 
					+ "               / / /                   \r\n"
					+ "              / / /\r\n" 
					+ "             / / /\r\n" 
					+ "            / / /\r\n"
					+ "           / / /\r\n" 
					+ "          / / /\r\n" 
					+ "         / / /\r\n" 
					+ "        / / /\r\n"
					+ "       / / /\r\n" 
					+ "      \\/_/");
			System.out.println(
					"___________________________________________________________________________________________");
			System.out.println(
					"                                      검색옵션을 선택해주세요                                       ");
			System.out.println();
			System.out.println("1. 전체 | 2. 제목 | 3. 출판사 | 4. 저자 | 5. 카테고리 | 6. 상점 이름 | 7. 10페이지씩 보기 | 8.나가기");
			System.out.println(
					"___________________________________________________________________________________________");
			System.out.print("메뉴 선택 : ");
			try {
				int select = Integer.parseInt(br.readLine());
				String query = null;
				StringBuilder sql = new StringBuilder();
				// 전체 조회
				if (select == 1) {
					printList(sql ,conn, br, sc);
					break;

				} else if (select == 2) { 
					// 제목
					System.out.println(
							"___________________________________________________________________________________________\n");
					System.out.print("검색어 입력 : ");
					query = br.readLine();
					System.out.println(
							"___________________________________________________________________________________________\n");
					printListMenu(sc,sql,conn,br,"book_intro",query);
					break;

				} else if (select == 3) {
					// 출판사
					System.out.println(
							"___________________________________________________________________________________________\n");
					System.out.print("검색어 입력 : ");
					query = br.readLine();					
					System.out.println(
							"___________________________________________________________________________________________\n");
					printListMenu(sc,sql,conn,br,"publisher",query);
					break;

				} else if (select == 4) {
					// 저자
					System.out.println(
							"___________________________________________________________________________________________\n");
					System.out.print("검색어 입력 : ");
					query = br.readLine();					
					System.out.println(
							"___________________________________________________________________________________________\n");
					printListMenu(sc,sql,conn,br,"author",query);
					break;

				} else if (select == 5) {
					// 카테고리
					printCategory(sql, conn);
					System.out.println(
							"___________________________________________________________________________________________\n");
					System.out.print("검색어 입력 : ");
					query = br.readLine();					
					System.out.println(
							"___________________________________________________________________________________________\n");	
					printListMenu(sc,sql,conn,br,"category_name ",query);
					break;

				} else if (select == 6) {
					// 상점 이름
					System.out.println(
							"___________________________________________________________________________________________\n");
					System.out.print("검색어 입력 : ");
					query = br.readLine();					
					System.out.println(
							"___________________________________________________________________________________________\n");
					printListMenu(sc,sql,conn,br,"user_name",query);
					break;

				} else if (select == 7) {
					// 상점 이름
					printpagingMenu(sc,1, sql, conn, br);
					break;

				} else {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.toString());
			}
		}
	}

	public static void printCategory(StringBuilder sql, Connection conn) {
	    try {
	        sql.append("SELECT ").append("* ")
	           .append("FROM ").append("Category");

	        PreparedStatement pstmt = conn.prepareStatement(sql.toString());
	        ResultSet rs = pstmt.executeQuery();

	        System.out.println();
	        System.out.println("╔══════════════════════════════════╗");
	        System.out.println("║           📚 카테고리 메뉴판         ║");
	        System.out.println("╠══════════════════════════════════╣");

	        int index = 1;
	        while (rs.next()) {
	            String line = String.format("║  %2d. %-27s║", index++, rs.getString("CATEGORY_NAME"));
	            System.out.println(line);
	        }

	        System.out.println("╚══════════════════════════════════╝");
	        System.out.println();
	        sql.setLength(0);

	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 로그 출력 (필요시)
	    }
	}

	
	public static void printList(StringBuilder sql, Connection conn, BufferedReader br, Scanner scanner) {
		try {
			sql.append("SELECT ").append(
					"bu.book_used_id AS id, book_intro, publisher, author, category_name, state, cell_price || '원' AS price, book_cnt, trade_cnt, user_name, s.shop_id AS sid ")
					.append("FROM ").append("book_origin bo ").append("JOIN ")
					.append("book_used bu ON bo.book_origin_id = bu.book_origin_id ").append("JOIN ")
					.append("shop s ON s.shop_id = bu.shop_id ").append("JOIN ")
					.append("users u ON s.user_id = u.user_id ").append("JOIN ")
					.append("inventory i ON bu.inven_id = i.inven_id ").append("JOIN ")
					.append("category c ON c.category_id = bo.category_id ")
					.append("ORDER BY bu.book_used_id");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			System.out.printf(" %-10s %-40s %-15s %-10s %-15s %-5s %-10s %-10s %-10s %-10s\n","책ID", "제목", "출판사", "저자", "카테고리", "상태",
					"중고가", "남은 수량", "팔린 수량", "판매자");
			System.out.println(
					"-------------------------------------------------------------------------------------------------------------------------------------------");
			ResultSet rs = pstmt.executeQuery();
			List<BookDto> blist = new ArrayList<>();
			while (rs.next()) {
				BookDto book = new BookDto();
				book.setBid(rs.getInt("id"));
				book.setShopId(rs.getInt("sid"));
				book.setBookIntro(rs.getString("book_intro"));
				book.setPublisher(rs.getString("publisher"));
				book.setAuthor(rs.getString("author"));
				book.setCategoryName(rs.getString("category_name"));
				book.setState(rs.getString("state"));
				book.setPrice(rs.getString("price"));
				book.setBookCnt(rs.getInt("book_cnt"));
				book.setTradeCnt(rs.getInt("trade_cnt"));
				book.setUserName(rs.getString("user_name"));
				blist.add(book);
				System.out.printf(" %-10s %-40s %-15s %-10s %-15s %-5s %-10s %-10s %-10s %-10s\n",book.getBid(), book.getBookIntro(),
						book.getPublisher(), book.getAuthor(), book.getCategoryName(), book.getState(), book.getPrice(),
						book.getBookCnt(), book.getTradeCnt(), book.getUserName());

			}
			System.out.println("\n");
			System.out.print("선택할 책 제목 입력 :");
			
			int selectTitle = Integer.parseInt(br.readLine());
			BookDto bd = blist.get(selectTitle-1);
			buy(scanner,conn, user,bd.getShopId());
			
			// 책 상세 페이지 이동
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	public static void printListMenu(Scanner sc, StringBuilder sql, Connection conn, BufferedReader br, String option, String query) {
		try {
			sql.append("SELECT ").append(
					"bu.book_used_id AS id, book_intro, publisher, author, category_name, state, cell_price || '원' AS price, book_cnt, trade_cnt, user_name, s.shop_id AS sid ")
					.append("FROM ").append("book_origin bo ").append("JOIN ")
					.append("book_used bu ON bo.book_origin_id = bu.book_origin_id ").append("JOIN ")
					.append("shop s ON s.shop_id = bu.shop_id ").append("JOIN ")
					.append("users u ON s.user_id = u.user_id ").append("JOIN ")
					.append("inventory i ON bu.inven_id = i.inven_id ").append("JOIN ")
					.append("category c ON c.category_id = bo.category_id ")
					.append("WHERE ").append(option + " ").append("LIKE ").append("'%").append(query).append("%' ")
					.append("ORDER BY bu.book_used_id");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			System.out.printf(" %-10s %-15s %-15s %-10s %-15s %-5s %-10s %-10s %-10s %-10s\n","책ID", "제목", "출판사", "저자", "카테고리", "상태",
					"중고가", "남은 수량", "팔린 수량", "판매자");
			System.out.println(
					"-------------------------------------------------------------------------------------------------------------------------------------------");
			ResultSet rs = pstmt.executeQuery();
			List<BookDto> blist = new ArrayList<>();
			while (rs.next()) {
				BookDto book = new BookDto();
				book.setBid(rs.getInt("id"));
				book.setShopId(rs.getInt("sid"));
				book.setBookIntro(rs.getString("book_intro"));
				book.setPublisher(rs.getString("publisher"));
				book.setAuthor(rs.getString("author"));
				book.setCategoryName(rs.getString("category_name"));
				book.setState(rs.getString("state"));
				book.setPrice(rs.getString("price"));
				book.setBookCnt(rs.getInt("book_cnt"));
				book.setTradeCnt(rs.getInt("trade_cnt"));
				book.setUserName(rs.getString("user_name"));
				blist.add(book);
				System.out.printf(" %-5s %-15s %-17s %-10s %-15s %-5s %-15s %-10s %-10s %-10s\n",book.getBid(), book.getBookIntro(),
						book.getPublisher(), book.getAuthor(), book.getCategoryName(), book.getState(), book.getPrice(),
						book.getBookCnt(), book.getTradeCnt(), book.getUserName());
				}
			System.out.println("\n");
			System.out.print("선택할 책 제목 입력 :");
			
			int selectTitle = Integer.parseInt(br.readLine());
			BookDto bd = blist.get(selectTitle-1);
			buy(sc,conn, user,bd.getShopId());
			
			// 책 상세 페이지 이동
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	
	public static void printpagingMenu(Scanner sc,int cur, StringBuilder sql, Connection conn, BufferedReader br ){
		try {
			sql.setLength(0);
			sql.append("SELECT ")
					.append("* ")
					.append("FROM ").append("(")
					.append("SELECT ")
					.append("ROW_NUMBER() OVER (ORDER BY cell_price DESC) AS rn, ")
					.append("bu.book_used_id AS id, book_intro, publisher, author, category_name, state, cell_price || '원' AS price, book_cnt, trade_cnt, user_name, s.shop_id AS sid ")
					.append("FROM ").append("book_origin bo ").append("JOIN ")
					.append("book_used bu ON bo.book_origin_id = bu.book_origin_id ").append("JOIN ")
					.append("shop s ON s.shop_id = bu.shop_id ").append("JOIN ")
					.append("users u ON s.user_id = u.user_id ").append("JOIN ")
					.append("inventory i ON bu.inven_id = i.inven_id ").append("JOIN ")
					.append("category c ON c.category_id = bo.category_id ")
					.append("ORDER BY bu.book_used_id").append(") ")
					.append("WHERE rn BETWEEN ").append("?").append(" AND ").append("?");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setInt(1,(cur - 1)*10 + 1);
			pstmt.setInt(2, cur*10);
			
			System.out.printf(" %-10s %-40s %-15s %-10s %-15s %-5s %-10s %-10s %-10s %-10s\n","책ID", "제목", "출판사", "저자", "카테고리", "상태",
					"중고가", "남은 수량", "팔린 수량", "판매자");
			System.out.println(
					"-------------------------------------------------------------------------------------------------------------------------------------------");
			ResultSet rs = pstmt.executeQuery();
			List<BookDto> blist = new ArrayList<>();

			while (rs.next()) {
				BookDto book = new BookDto();
				book.setBid(rs.getInt("id"));
				book.setShopId(rs.getInt("sid"));
				book.setBookIntro(rs.getString("book_intro"));
				book.setPublisher(rs.getString("publisher"));
				book.setAuthor(rs.getString("author"));
				book.setCategoryName(rs.getString("category_name"));
				book.setState(rs.getString("state"));
				book.setPrice(rs.getString("price"));
				book.setBookCnt(rs.getInt("book_cnt"));
				book.setTradeCnt(rs.getInt("trade_cnt"));
				book.setUserName(rs.getString("user_name"));
				blist.add(book);
				System.out.printf(" %-10s %-40s %-15s %-10s %-15s %-5s %-10s %-10s %-10s %-10s\n",book.getBid(), book.getBookIntro(),
						book.getPublisher(), book.getAuthor(), book.getCategoryName(), book.getState(), book.getPrice(),
						book.getBookCnt(), book.getTradeCnt(), book.getUserName());

			}
			System.out.println("\n");
			System.out.println("현재 페이지 : " + cur + "페이지");
			System.out.println("                                         |  < 1. 이전 페이지  |  2. 책 선택  |  3. 다음 페이지 >  |                                         ");
			String selectNum = br.readLine();
			if(selectNum.equals("1") && cur > 1) {
				printpagingMenu(sc,cur - 1, sql, conn, br);
			}
			else if(selectNum.equals("3")) {
				printpagingMenu(sc, cur + 1, sql, conn, br);
			}
			else if(selectNum.equals("2")){
				System.out.println("\n");
				System.out.print("선택할 책 제목 입력 :");
				
				int selectTitle = Integer.parseInt(br.readLine());
				BookDto bd = blist.get(selectTitle-1);
				buy(sc,conn, user,bd.getShopId());
				
				// 책 상세 페이지 이동
			}else {
				start(conn);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	
	
	public static void buy(Scanner sc, Connection conn, Users user, int input) {
		//로그인 체크, 
		if(user == null) { //회원이 없을 경우, 회원 가입이나 로그인을 해야 한다.
			loginAlertPage(); //구매 전에 반드시 회원 가입이나 로그인 로직 필요.
			return; //리턴해버리기
		}
		
		int shopId = 0;
		shopId = input;
		if(input == 0) {
			shopList(conn); //리스트 보여주기
			System.out.println("입장을 원하시는 상점 id를 입력해주세요 : "); //두번 눌러야 하는거 해결
			shopId = sc.nextInt();
		}
		shopBookList(shopId ,conn); //shopId로 상점 내부 책 조인으로 가져오기
		int book_used_id = sc.nextInt(); //책번호
		try{
			updateInvenAfterPurcharse(book_used_id, conn, user);
		}catch(SQLException e) {
			throw new RuntimeException("거래 중 오류 발생", e);
		}catch(NullPointerException e2) {
			start(conn);
		}
	}
	
	public static void lookUserPurcharseBook(Connection conn, Users user) {
		System.out.println("------------------------");
		System.out.println("구매하신 책의 목록을 보여드립니다!");
		System.out.println("------------------------");
		
		PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
	    try {
			//쿼리 날리기
	    	String sql = "SELECT " +
				    "U.user_name AS 구매자이름, " +
				    "BO.book_intro AS 책이름, " +
				    "BO.price AS 가격 " +
				    "FROM USERS U, USERS_OLD_BOOK UOB, BOOK_USED BU, BOOK_ORIGIN BO " +
				    "WHERE U.USER_ID = UOB.USER_ID " +
				    "AND UOB.BOOK_USED_ID = BU.BOOK_USED_ID " +
				    "AND BU.BOOK_ORIGIN_ID = BO.BOOK_ORIGIN_ID " +
				    "AND U.USER_ID = ?";  //preparestatement
			pstmt = conn.prepareStatement(sql); //prepareStatement 준비
			pstmt.setInt(1, user.getUser_id());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				System.out.println("------------------------");
				System.out.println("구매자이름 : " + rs.getString("구매자이름"));
				System.out.println("책이름 : " + rs.getString("책이름"));
				System.out.println("가격 : " + rs.getInt("가격"));
			}
	
			pstmt.close(); //pstmt close
		}catch(SQLException e) {
		    e.printStackTrace();
		} finally {
			try {
		        if (rs != null) rs.close();
		        if (pstmt != null) pstmt.close();
		        // conn은 외부에서 관리 → 여기서 닫지 않음
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
		
	}
	
	public static void updateInvenAfterPurcharse(int book_used_id, Connection conn, Users user) throws SQLException {
		//흐름 : invenId를 구한 다음에 그걸로 수량을 업데이트 한다.
		String selectSql = "SELECT inven_id FROM book_used WHERE book_used_id = ?";
	    String updateSql = "UPDATE inventory SET book_cnt = book_cnt - 1, trade_cnt = trade_cnt + 1 WHERE inven_id = ?";
	    String insertSql = "INSERT INTO users_old_book (user_id, book_used_id) " +
                "VALUES (?, ?)";
	    
	    PreparedStatement selectStmt = null;
	    PreparedStatement updateStmt = null;
	    PreparedStatement insertStmt = null;
	    ResultSet rs = null;
	    
	    try {
	    	conn.setAutoCommit(false); //트랜잭션 시작
	    	//inven_id 조회
	    	selectStmt = conn.prepareStatement(selectSql);
		
	    	selectStmt.setInt(1, book_used_id);
			rs = selectStmt.executeQuery();
			
			if(rs.next()) {
				int invenId = rs.getInt("inven_id");
				
				//2. 재고 업데이트
				updateStmt = conn.prepareStatement(updateSql);
				updateStmt.setInt(1, invenId);
				int rows = updateStmt.executeUpdate();
				
				if(rows <= 0) {
					System.out.println("재고 업데이트 실패!");
				}
				
				//3. 사용자 구매 목록에 저장
				insertStmt = conn.prepareStatement(insertSql);
				insertStmt.setInt(1, user.getUser_id());
				insertStmt.setInt(2, book_used_id);
				insertStmt.executeUpdate();
			}
			conn.commit();
	    } catch (SQLException e) {
	        conn.rollback(); // 에러 발생 시 롤백
	        throw e;
	    } finally {
	        if (rs != null) rs.close();
	        if (selectStmt != null) selectStmt.close();
	        if (updateStmt != null) updateStmt.close();
	        if (insertStmt != null) insertStmt.close();
	        conn.setAutoCommit(true); // 상태 원상복귀
	    }
	}
	
	public static void shopBookList(int shopId ,Connection conn) {
		System.out.println("------------------------");
		System.out.println("해당 샵의 전체 중고책 목록을 보여드립니다!");
		System.out.println("------------------------");
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
			//쿼리 날리기
			String sql = "SELECT " +
				    "S.shop_id AS 샵넘버, " +
				    "BO.book_intro AS 책이름, " +
				    "BU.book_used_id AS 책번호, " +
				    "C.category_name AS 분류, " +
				    "BO.price AS 가격, " +
				    "BU.state AS 책상태, " +
				    "BOOK_CNT AS 재고 " +
				    "FROM BOOK_ORIGIN BO, BOOK_USED BU, CATEGORY C, SHOP S, INVENTORY I " +
				    "WHERE BO.BOOK_ORIGIN_ID = BU.BOOK_ORIGIN_ID " +
				    "AND BU.SHOP_ID = S.SHOP_ID " +
				    "AND C.CATEGORY_ID = BO.CATEGORY_ID " +
				    "AND I.INVEN_ID = BU.INVEN_ID " +
				    "AND S.SHOP_ID = ?";  //preparestatement
			pstmt = conn.prepareStatement(sql); //prepareStatement 준비
			pstmt.setInt(1, shopId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				System.out.println("------------------------");
				System.out.println("책번호 : " + rs.getInt("책번호"));
				System.out.println("분류 : " + rs.getString("분류"));
				System.out.println("책이름 : " + rs.getString("책이름"));
				System.out.println("책상태 : " + rs.getString("책상태"));
				System.out.println("가격 : " + rs.getString("가격"));
				System.out.println("재고 : " + rs.getString("재고"));
			}
			
			System.out.println("------------------------");
			System.out.println("구매를 원하시는 책의 책번호를 입력해 주세요 : ");
			pstmt.close(); //pstmt close
		}catch(SQLException e) {
		    e.printStackTrace();
		} finally {
			try {
		        if (rs != null) rs.close();
		        if (pstmt != null) pstmt.close();
		        // conn은 외부에서 관리 → 여기서 닫지 않음
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static void shopList(Connection conn){
		System.out.println("------------------------");
		System.out.println("yes24의 전체 중고샵 목록을 보여드립니다!");
		System.out.println("------------------------");
		
		//상점 리스트를 보여줘야 한다.
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
			//쿼리 날리기
			String sql = "SELECT " +
		             "user_name AS 상점주이름, " +
		             "shop_id AS 샵넘버, " +
		             "NVL(shop_notice, '없음') AS 샵소개 " +
		             "FROM users u, shop s " +
		             "WHERE u.user_id = s.user_id";  //preparestatement
			pstmt = conn.prepareStatement(sql); //prepareStatement 준비
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				System.out.println("------------------------");
				System.out.println("샵넘버 : " + rs.getInt("샵넘버"));
				System.out.println("상점주 이름 : " + rs.getString("상점주이름"));
				System.out.println("샵소개 : " + rs.getString("샵소개"));
			}
			pstmt.close(); //pstmt close
		}catch(SQLException e) {
		    e.printStackTrace();
		} finally {
			try {
		        if (rs != null) rs.close();
		        if (pstmt != null) pstmt.close();
		        // conn은 외부에서 관리 → 여기서 닫지 않음
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static Users signUp(Scanner sc, Connection conn) throws SQLException {
		//유저 생성, 저장
		Users user = new Users();
		
		System.out.print("이름을 입력해 주세요 : ");
		String user_name = sc.next();
		System.out.print("아이디를 입력해 주세요 : ");
		String login_id = sc.next();
		System.out.print("비밀번호를 입력해 주세요 : ");
		String password = sc.next();
		System.out.print("이메일을 입력해 주세요 : ");
		String email = sc.next();
		
		user.setUser_name(user_name);
		user.setPassword(password);
		user.setLogin_id(login_id);
		user.setEmail(email);
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
			conn.setAutoCommit(false);
			//쿼리 날리기
			String sql = "" + 
					"INSERT INTO users (user_name, login_id, password, email)\r\n"
					+ "VALUES (?, ?, ?, ?)"; //preparestatement
			pstmt = conn.prepareStatement(sql); //prepareStatement 준비
			pstmt.setString(1,user_name); //user_name	
			pstmt.setString(2,login_id); //login_id	
			pstmt.setString(3,password); //password	
			pstmt.setString(4,email); //email	
			
			pstmt.executeUpdate(); //insert 회원
			//pstmt close
			pstmt.close();
			
			//select 사용해서 id 받아오기
			sql = "SELECT user_id FROM users WHERE login_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getLogin_id());
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				user.setUser_id(rs.getInt("user_id")); //select 컬럼명을 직접 쓰기
			}else {
				System.out.println("사용자 아이디가 존재하지 않음");
			}
			
			conn.commit(); // 성공하면 커밋
			return user;
		}catch(SQLException e) {
			try { //실패 할 경우 전부 다 롤백 해줘야 한다!!!
				conn.rollback(); 
			} catch (SQLException ex) {
				ex.printStackTrace(); 
			}
			
		    e.printStackTrace();
		    throw e; //상위로 예외 던지기
		} finally {
			try {
				conn.setAutoCommit(true); //다시 돌려놓기
				
		        if (rs != null) rs.close();
		        if (pstmt != null) pstmt.close();
		        // conn은 외부에서 관리 → 여기서 닫지 않음
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public static void printShopReviews(Scanner sc, Connection conn) throws SQLException {

		System.out.println(
			    "                       ______________________ \n"+
			    "    ┏━━━━━━━━━━┓      /\\        ______      \\\\ \n" +
			    "    ┃   SHOP   ┃     //_\\       \\    /\\      \\\\ \n" +
			    "    ┃  REVIEW  ┃    //___\\       \\__/  \\      \\\\ \n" +
			    "    ┃  SEARCH! ┃   //_____\\       \\ |[]|       \\\\ \n" +
			    "    ┗━━━━━━━━━━┛  //_______\\       \\|__|        \\\\ \n" +
			    "       ★★★★★    /XXXXXXXXXX\\                    \\\\ \n" +
			    "                /_I_II  I__I_\\____________________\\\\ \n" +
			    "                  I_I|  I__I_______[]_|_[]_______I \n" +
			    "                  I_II  I__I_______[]_|_[]_______I \n" +
			    "                  I II__I  I       XXXXXXX       I \n" +
			    "                  ~~~~\"   \"~~~~~~~~~~~~~~~~~~~~~~~~"
			);
		System.out.print("\n확인할 상점 ID를 입력하세요: ");
		int shopId = sc.nextInt();

		String avgSql = "SELECT review_avg FROM shop WHERE shop_id = ?";
		String reviewSql = "SELECT r.review_title, r.review_content, r.review_star, TO_CHAR(r.review_date, 'YYYY-MM-DD') AS review_date "
				+ "FROM review r WHERE r.shop_id = ? ORDER BY r.review_date DESC";

		PreparedStatement avgStmt = null;
		PreparedStatement reviewStmt = null;
		ResultSet avgRs = null;
		ResultSet reviewRs = null;

		try {
			avgStmt = conn.prepareStatement(avgSql);
			avgStmt.setInt(1, shopId);
			avgRs = avgStmt.executeQuery();

			if (avgRs.next()) {
				double avg = avgRs.getDouble("review_avg");
				System.out.printf("\n🏠︎ 상점 ID: %d | 평균 평점: %.1f점\n", shopId, avg);
			} else {
				System.out.println("존재하지 않는 상점 ID입니다.");
				return;
			}

			reviewStmt = conn.prepareStatement(reviewSql);
			reviewStmt.setInt(1, shopId);
			reviewRs = reviewStmt.executeQuery();

			int count = 0;
			while (reviewRs.next()) {
				count++;
				System.out.println("\n---------------------------------------------------------------------\n");
				System.out.printf("[%d] %s (%s)\n", count, reviewRs.getString("review_title"),
						reviewRs.getString("review_date"));

				int star = reviewRs.getInt("review_star");
				StringBuilder stars = new StringBuilder();
				for (int i = 0; i < 5; i++) {
					stars.append(i < star ? '★' : '☆');
				}
				System.out.printf("평점: %s (%d점)\n", stars.toString(), star);

				System.out.printf("내용: %s\n", reviewRs.getString("review_content"));
			}

			if (count == 0) {
				System.out.println("해당 상점에 등록된 리뷰가 없습니다.");
			}
		} finally {
			if (avgRs != null)
				avgRs.close();
			if (reviewRs != null)
				reviewRs.close();
			if (avgStmt != null)
				avgStmt.close();
			if (reviewStmt != null)
				reviewStmt.close();
		}
	}
	
	public static void bookIntro(Connection conn, Scanner scanner) {
        if (scanner.hasNextLine()) {
			scanner.nextLine();
		}
		 while (true) {
           System.out.println("\n-------------------------------------");
           System.out.println("📚 메뉴: 1. 책 리스트 | 2. 메인으로 돌아가기");
           System.out.println("-------------------------------------");
           System.out.print("메뉴 선택: ");
           String menuNo = scanner.nextLine();

           if (menuNo.equals("1")) {
               try {
                   String sql = "SELECT bo.book_origin_id, bo.book_intro, c.category_name " +
                                "FROM book_origin bo " +
                                "JOIN category c ON bo.category_id = c.category_id";
                   PreparedStatement pstmt = conn.prepareStatement(sql);
                   ResultSet rs = pstmt.executeQuery();

                   ArrayList<Integer> bookIdList = new ArrayList<>();
                   System.out.println("\n📚 책 리스트:");
                   int index = 1;
                   while (rs.next()) {
                       int bookId = rs.getInt("book_origin_id");
                       String title = rs.getString("book_intro");
                       String category = rs.getString("category_name");

                       bookIdList.add(bookId);
                       System.out.println(index + ". " + title + " [" + category + "]");
                       index++;
                   }

                   rs.close();
                   pstmt.close();

                   while (true) {
                       System.out.print("\n▶ 상세 정보를 볼 책 번호를 입력하세요 (종료하려면 exit): "); 
                       String input = scanner.nextLine();

                       if (input.equalsIgnoreCase("exit")) {
                           System.out.println("상세 정보 조회를 종료합니다.");
                           break;
                       }

                       int selectedNum;
                       try {
                           selectedNum = Integer.parseInt(input);
                       } catch (NumberFormatException e) {
                           System.out.println("숫자를 입력하거나 'exit'을 입력해 종료할 수 있습니다.");
                           continue;
                       }

                       if (selectedNum < 1 || selectedNum > bookIdList.size()) {
                           System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
                           continue;
                       }

                       int selectedBookId = bookIdList.get(selectedNum - 1);

                       String detailSql = "SELECT bo.book_intro, bo.author, bo.publisher, bo.publish_date, " +
                       				"c.category_name, bo.price, NVL(i.book_cnt,0) as book_cnt " +
                                          "FROM book_origin bo " +
                                          "JOIN category c ON bo.category_id = c.category_id " +
                                          "LEFT JOIN book_used bu ON bo.book_origin_id = bu.book_origin_id " +
                                          "LEFT JOIN inventory i ON bu.inven_id = i.inven_id " +
                                          "WHERE bo.book_origin_id = ?";
                       pstmt = conn.prepareStatement(detailSql);
                       pstmt.setInt(1, selectedBookId);
                       rs = pstmt.executeQuery();

                       if (rs.next()) {
                           System.out.println("\n📖 책 상세 정보");
                           System.out.println("제목       : " + rs.getString("book_intro"));
                           System.out.println("저자       : " + rs.getString("author"));
                           System.out.println("출판사     : " + rs.getString("publisher"));
                           System.out.println("출판일     : " + rs.getDate("publish_date"));
                           System.out.println("카테고리    : " + rs.getString("category_name"));
                           System.out.println("정가       : " + rs.getInt("price") + " 원");
                           System.out.println("재고현황    : " + rs.getInt("book_cnt") + " 권");
                       }

                       rs.close();
                       pstmt.close();
                   }

               } catch (SQLException e) {
                   e.printStackTrace();
               }

           } else if (menuNo.equals("2")) {
               System.out.println("메인으로 돌아갑니다.");
               return;
           } else {
               System.out.println("잘못된 입력입니다. 1 또는 2를 선택해주세요.");
           }
	    }
	}
	
	public static Users login(Connection conn, Scanner sc) throws SQLException{
		//유저 생성, 저장
		Users user = new Users();
		
		System.out.println("------------------------");
		System.out.println("로그인을 시작합니다!");
		System.out.println("------------------------");
		
		PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
		System.out.println("아이디를 입력해주세요 : ");
		String id = sc.next();
		System.out.println("비밀번호를 입력해주세요 : ");
		String password = sc.next();
	    
	    try {
			//쿼리 날리기
	    	String sql = "SELECT "
	    	           + "USER_NAME AS 이름, "
	    	           + "USER_ID AS 고유아이디, "
	    	           + "LOGIN_ID AS 아이디, "
	    	           + "PASSWORD AS 비밀번호, "
	    	           + "EMAIL AS 이메일 "
	    	           + "FROM USERS "
	    	           + "WHERE LOGIN_ID = ? "
	    	           + "AND PASSWORD = ?"; //preparestatement
			pstmt = conn.prepareStatement(sql); //prepareStatement 준비
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//user에 저장
				user.setUser_name(rs.getString("이름"));
				user.setUser_id(rs.getInt("고유아이디"));
				user.setLogin_id(rs.getString("아이디"));
				user.setPassword(rs.getString("비밀번호"));
				user.setEmail(rs.getString("이메일"));
			}else {
				System.out.println("로그인 에러! 아이디나 비번이 틀렸거나, 저장되지 않은 회원입니다.");
			}
	
			pstmt.close(); //pstmt close
			return user;
		}catch(SQLException e) {
		    e.printStackTrace();
		    throw e; //상위로 예외 던지기
		} finally {
			try {
		        if (rs != null) rs.close();
		        if (pstmt != null) pstmt.close();
		        // conn은 외부에서 관리 → 여기서 닫지 않음
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	//로그인 페이지
	public static void loginAlertPage() {
		System.out.println("_______________________________________________________");
		System.out.println("          구매 기능을 이용하기 위해서는 로그인이 필요합니다.         ");
		System.out.println("_______________________________________________________");
		System.out.println("        로그인 : 0번        |         회원가입 : 1번         ");
		System.out.println("_______________________________________________________");
	} 

	public static void welcomePage() {
		System.out.println(
				"                                      _.--._  _.--._\r\n" 
				+ "                               ,-=.-\":;:;:;\\':;:;:;\"-._\r\n"
				+ "                               \\\\\\:;:;:;:;:;\\:;:;:;:;:;\\\r\n" 
				+ "                                \\\\\\:;:;:;:;:;\\:;:;:;:;:;\\\r\n"
				+ "                                 \\\\\\:;:;:;:;:;\\:;:;:;:;:;\\\r\n" 
				+ "                                  \\\\\\:;:;:;:;:;\\:;::;:;:;:\\\r\n"
				+ "                                   \\\\\\;:;::;:;:;\\:;:;:;::;:\\\r\n" 
				+ "                                    \\\\\\;;:;:_:--:\\:_:--:_;:;\\\r\n"
				+ "                                     \\\\\\_.-\"      :      \"-._\\\r\n"
				+ "                                      \\`_..--\"\"--.;.--\"\"--.._=>");
		System.out.println();
		System.out.println("___________________________________________________________________________________________");
		System.out.println();
		System.out.println("                                 Welcome! this is fake yes24                                ");
		System.out.println("___________________________________________________________________________________________");

	}

	public static void choosePage() {
		System.out.println("___________________________________________________________________________________________");
		System.out.println();
		System.out.println("                                 원하시는 서비스를 선택해 주세요                                   ");
		System.out.println();
		System.out.println("         0. 로그인 | 1. 회원가입 | 2. 구매하기 | 3. 검색 결과 | 4. 책 소개 | 5. 상점 리뷰 확인 | 6. 나가기      ");		
		System.out.println("___________________________________________________________________________________________");
		System.out.println("메뉴 선택 : ");
	}
}