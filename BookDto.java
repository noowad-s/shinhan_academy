package project2;

import lombok.Data;

@Data
public class BookDto {
	private int bid;
	private int shopId;
	private String bookIntro;
	private String publisher;
	private String author;
	private String categoryName;
	private String state;
	private String price;
	private int bookCnt;
	private int tradeCnt;
	private String userName;
}