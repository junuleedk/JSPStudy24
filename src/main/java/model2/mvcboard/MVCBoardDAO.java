package model2.mvcboard;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import common.DBConnPool;
import jakarta.servlet.ServletContext;
import model1.board.BoardDTO;

//MVC 게시판은 DBCP(커넥션풀)를 통해 DB에 연결한다.
public class MVCBoardDAO extends DBConnPool {
	
	//기본생성자 호출로 커넥션풀을 사용한다.
	public MVCBoardDAO() {
		super();
	}
	//게시물의 개수 카운트
	public int selectCount(Map<String, Object>map) {
		int totalCount = 0;
		String query = "SELECT COUNT(*) FROM mvcboard ";
		if(map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " "
				   + " LIKE '%" + map.get("searchWord") + "%'";
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			rs.next();
			totalCount = rs.getInt(1);
		}
		catch(Exception e) {
			System.out.println("게시물 카운트 중 예외발생");
			e.printStackTrace();
		}
		return totalCount;
	}
	
	//목록에 출력할 실제 게시물을 인출(페이징 기능 추가)
	public List<MVCBoardDTO> selectListPage(Map<String, Object>map) {
		/*
		 모델1에서 사용했던 테이블이 board에서 mvcboard로 변경되었으므로 
		 DTO객체와 컬럼명에 대한 수정을 해야한다. 
		 */
		List<MVCBoardDTO> board = new Vector<MVCBoardDTO>();
		
		/*
		 레코드 인출을 위한 select 쿼리문 작성. 최근 게시물이 상단에 출력해야하므로
		 일련번호의 내림차순으로 정렬한다.  
		 */
		String query = " "
					 + "SELECT * FROM ( "
				     + "    SELECT Tb.*, ROWNUM rNum FROM ( "
					 + "        SELECT * FROM mvcboard ";
		
		if(map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " "
					   + " LIKE '%" + map.get("searchWord") + "%' ";
		}
		//정렬을 위한 컬럼명은 num -> idx로 변경된다.
		query += "           ORDER BY idx DESC "
			   + "    ) Tb "
			   + " ) "
			   + " WHERE rNum BETWEEN ? AND ?";
		
		try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, map.get("start").toString());
			psmt.setString(2, map.get("end").toString());
			rs = psmt.executeQuery();
			//반환된 ResultSet의 개수만큼 반복한다. 
			while (rs.next()) {
				//하나의 레코드를 저장할 수 있는 DTO 인스턴스 생성
				MVCBoardDTO dto = new MVCBoardDTO();
				
				//테이블이 mvcboard로 변경되므로 setter에 대한 수정이 필요하다.
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				//날짜이므로 getDate()사용
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				//숫자이므로 getInt()사용
				dto.setDowncount(rs.getInt(8));
				dto.setPass(rs.getString(9));
				//숫자이므로 getInt()사용
				dto.setVisitcount(rs.getInt(10));
				
				board.add(dto);
			}
		}
		catch(Exception e) {
			System.out.println("게시물 조회 중 예외발생");
		}
		return board;
	}
	
	//글쓰기 처리
	public int insertWrite(MVCBoardDTO dto) {
		int result = 0;
		
		try {
			/*
			 쿼리문의 일련번호는 모델1 게시판에서 생성한 시퀀스를 그대로 사용한다.
			 나머지 값들은 컨트롤러(서블릿)에서 받은 후 모델 (DAO)로 전달한다.
			 */
			String query = "INSERT INTO mvcboard ( "
						 + " idx, name, title, content, ofile, sfile, pass) "
						 + " VALUES ( "
						 + " seq_board_num.NEXTVAL, ?, ?, ?, ?, ?, ?)";
			/*
			 동적쿼리문이므로 prepared 인스턴스를 생성한 후 순서대로 인파라미터를 설정한다.
			 */
			psmt = con.prepareStatement(query);
			psmt.setString(1,  dto.getName());
			psmt.setString(2,  dto.getTitle());
			psmt.setString(3,  dto.getContent());
			psmt.setString(4,  dto.getOfile());
			psmt.setString(5,  dto.getSfile());
			psmt.setString(6,  dto.getPass());
			//쿼리문을 실행하여 입력처리한 후 결과값은 정수로 반환받는다. 
			result = psmt.executeUpdate();
		}
		catch(Exception e) {
			System.out.println("게시물 입력 중 예외발생");
			e.printStackTrace();
		}
		return result;
	}
	
	//내용보기
	public MVCBoardDTO selecView(String idx) {
		
		MVCBoardDTO dto = new MVCBoardDTO();
		
		/*
		 일련번호와 일치하는 게시물 1개 인출
		 */
		String query = "SELECT * FROM mvcboard WHERE idx=?";
		try {
			//쿼리문의 인파라미터를 설정한 후 실행
			psmt = con.prepareStatement(query);
			psmt.setString(1, idx);
			rs = psmt.executeQuery();
			//인출된 레코드가 있다면 DTO에 각 컬럼의 값을 저장
			if(rs.next()) {
				/*각 컬럼의 값을 추출할 때 1부터 시작하는 인덱스와 
				  컬럼명을 둘 다 사용할 수 있다. 
				*/
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				dto.setDowncount(rs.getInt(8));
				dto.setPass(rs.getString(9));
				dto.setVisitcount(rs.getInt(10));
			}
		}
		catch(Exception e) {
			System.out.println("게시물 상세보기 중 예외발생");
			e.printStackTrace();
		}
		return dto;
	}
	
	//조회수 증가
	public void updateVisitCount(String idx) {
		
		/* 
		   게시물의 일련번호를 통해 visitcount 컬럼의 값을 1증가시킨다.
		   number타입인 경우 사칙연산을 수행할 수 있다. 
		*/
		String query = "UPDATE mvcboard SET "
					 + " visitcount=visitcount + 1 "
					 + " WHERE idx=?";
		try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, idx);
			rs = psmt.executeQuery();
		}
		catch(Exception e) {
			System.out.println("게시물 조회수 증가 중 예외발생");
			e.printStackTrace();
		}
	}
	
	//게시물 수정
	public int updatePost(MVCBoardDTO dto) {
		int result = 0;
		
		try {
			/*
			 비회원제 게시판은 패스워드를 통해 검증을 진행한 후 수정이나 삭제를 해야한다.
			 따라서 아래와 같이 where절에는 idx와 pass까지 조건을 추가하는 것이 좋다. 
			 */
			String query = "UPDATE mvcboard"
						 + " SET title=?, name=?, content=?, ofile=?, sfile=? "
						 + " WHERE idx=? and pass=?";
			//인파라미터 설정
			psmt = con.prepareStatement(query);
			psmt.setString(1,  dto.getTitle());
			psmt.setString(2,  dto.getName());
			psmt.setString(3,  dto.getContent());
			psmt.setString(4,  dto.getOfile());
			psmt.setString(5,  dto.getSfile());
			psmt.setString(6,  dto.getIdx());
			psmt.setString(7,  dto.getPass());
			//쿼리문을 실행
			result = psmt.executeUpdate();
		}
		catch(Exception e) {
			System.out.println("게시물 수정 중 예외발생");
			e.printStackTrace();
		}
		return result;
	}
	
	//패스워드 검증
	public boolean confirmPassword(String pass, String idx) {
		boolean isCorr = true;
		try {
			//일련번호와 패스워드의 조건에 만족하는 레코드가 있는지 확인
			String sql = "SELECT COUNT(*) FROM mvcboard "
					   + " WHERE pass=? AND idx=?";
			psmt = con.prepareStatement(sql);
			psmt.setString(1, pass);
			psmt.setString(2, idx);
			rs = psmt.executeQuery();
			//count()함수는 반드시 결과값이 있으므로 if문 없이 next()를 호출한다.
			//조건에 만족하지 않으면 0, 만족하면 1을 반환한다.
			rs.next();
			
			if(rs.getInt(1) == 0) {
				//조건에 만족하는 레코드가 없는 경우..
				isCorr = false;
			}
		}
		catch(Exception e) {
			//쿼리문 실행도중 예외가 발생되면 catch절로 
			//넘어오므로 이 경우에도 false 설정해야한다.
			isCorr = false;
			e.printStackTrace();
		}
		return isCorr;
	}
	
	//게시물 삭제
	public int deletePost(String idx) {
		int result = 0;
		
		try {
			//일련번호에 해당하는 게시물 1개 삭제
			//인파라미터가 있는 delete쿼리문 작성
			String query = "DELETE FROM mvcboard WHERE idx=?";
			psmt = con.prepareStatement(query);
			psmt.setString(1,  idx);
			result = psmt.executeUpdate();
		}
		catch(Exception e) {
			System.out.println("게시물 삭제 중 예외발생");
			e.printStackTrace();
		}
		return result;
	}
	
	//파일 다운로드 수 증가
	public void downCountPlus(String idx) {
		String sql = "UPDATE mvcboard SET "
				   + " downcount=downcount+1 "
				   + " WHERE idx=? ";
		try {
			psmt = con.prepareStatement(sql);
			psmt.setString(1, idx);
			psmt.executeUpdate();
		}
		catch (Exception e) {}
	}
}
