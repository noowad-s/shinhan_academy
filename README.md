## yes24 중고서점 ERD 설계 및 구현

<details>
<summary>0. 실습 목표</summary>

- 25.04.29 ~ 25.05.01  
- 자유 주제  
- 4인 1조  
- ERD 설계와 JAVA 구현(ui 없이 console로 작동)

</details>

---

<details>
<summary>1. 요구사항 분석</summary>

- 주제 선정 : yes24 중고서점  
- 이유 : 짧은 시간 안에 진행해야 하는 과제이기에 이미 있는 웹 애플리케이션의 UI를 하나의 기획서로 삼기로 함.  
- 담당 파트 : 상점 & 리뷰  
  - **판매자 상점**  
    - 판매 중인 책, 판매자 정보, 리뷰, 별점 평균, 상점 공지사항, 배송 정보(배송가격, 무료 배송 정책)  
    - 판매 중인 책, 판매자 정보, 리뷰는 외부에서 가져올 것  
  - **리뷰**  
    - 작성자 아이디, 제목, 내용, 평점, 등록일  
    - 작성자 아이디는 외부에서 가져올 것

**🔑 Entity, Attribute 키워드 뽑아내기**  

| Entity | Attribute | 담당자 |
|:-|:-|:-:|
| job | 🔑직업 아이디 <br>📄직업 이름 <br>🔗유저 아이디 | 선정 |
| seller | 🔑셀러 아이디 <br> 📄판매자 닉네임, 고객상담 전화번호, 발송 예정일, 배송 방법, <br>배송비 정책, 도서산간 배송비 정책, 발송지 주소, 반품지 주소 <br>🔗유저 아이디 | 선정 |
| address | 🔑주소 아이디 <br>📄시도, 시군구, 동, 우편번호, 상세주소 | 선정 |
| user | 🔑사용자 아이디 <br>📄이름, 아이디, 비밀번호, 이메일주소 | 하진 |
| shop | 🔑상점 아이디 <br>📄리뷰 평점, 상점 공지, 배송 정책 <br> 🔗셀러 아이디, 🔗중고책 아이디 | **다운** |
| review | 🔑리뷰 아이디 <br>📄리뷰 제목, 리뷰 내용, 리뷰 별점, 리뷰 작성 날짜<br> 🔗상점 아이디, 🔗사용자 아이디 | **다운** |
| book_origin | 🔑원본 책 아이디 <br>📄책 소개, 출판 날짜, 정가, 출판사, 저자<br> 🔗카테고리 아이디 | 지원 |
| book_used | 🔑중고 책 아이디 <br>📄상태, 판매가<br> 🔗원본 책 아이디, 🔗재고 아이디, 🔗상점 아이디 | 지원 |
| category | 🔑1차 카테고리 아이디 <br>📄1차 카테고리 | 지원 |
| inventory | 🔑재고 아이디 <br>📄잔여 도서 권수, 거래량 | 지원 |

</details>

---

<details>
<summary>2. ERD 설계</summary>

**📌 관계 도출 요약**

| 관계 | 유형 | 설명 | 종속 관계 |
|------|------|------|-----------|
| User ↔ Seller | 1:1 | 유저 1명은 하나의 셀러 | Seller는 User에 종속 |
| User ↔ Job | 1:N | 유저는 하나의 직업 | Job은 User에 종속 |
| User ↔ Address | 1:1 | 유저는 주소 1개 | Address는 User에 종속 |
| User ↔ BookUsed | N:M | 유저는 여러 중고책 구매 가능 | 매핑 테이블로 관리 |
| Shop ↔ BookUsed | 1:N | 상점 하나가 여러 책 보유 | BookUsed는 Shop에 종속 |
| Review ↔ User | 1:N | 유저는 여러 리뷰 작성 | Review는 User에 종속 |
| Shop ↔ Review | 1:N | 상점은 여러 리뷰 보유 | Review는 Shop에 종속 |
| BookOrigin ↔ 출판사, 저자, 카테고리 | N:1 | 각 책은 하나의 출판사/저자/카테고리 | BookOrigin이 종속 |
| BookOrigin ↔ BookUsed | 1:N | 원본책 → 중고책 | BookUsed가 종속 |
| Inventory ↔ BookUsed | 1:N | 재고 → 중고책 | BookUsed가 종속 |

**🧩 ERD 이미지**
- 1차 ERD  
  ![1차 ERD](https://github.com/user-attachments/assets/a6001929-9b85-4845-80b0-a86cf8ab1b41)
  
- 2차 ERD  
  ![2차 ERD](https://github.com/user-attachments/assets/ea282d83-48dd-4982-bedb-2c3f86cf7a57)

</details>

---


<details>
<summary>3. 서비스 로직 구현</summary>

**1. 회원 가입 (선정)**
  - 회원 가입 로직의 순서는 다음과 같다.
    - user 정보를 java 콘솔로 입력받는다.
    - 이 정보를 insert 쿼리로 db에 저장한다.
    - select를 통해 시퀀스로 자동 생성된 user_id를 가져온다.
    - 자바 user에 set으로 user_id를 세팅한다. 회원 가입이 완료되고, 자바 dto에 새 유저가 생성된다.<br>

**2. 구매 (선정)**
  - 구매의 경우, 단순 구매 보다는 사용자의 사용성을 고려하여 다음과 같이 추가 기능을 더하여, 플로우를 구성하였다.
    - 원하는 상점을 선택한다. (단일 트랜잭션)
    - 상점에서 가진 책의 리스트를 도출한다. (단일 트랜잭션)
    - 구매할 책을 고른다. (수량은 일단 1개로 고정)
    - 상점의 경우, INVEN의 책 수량을 줄인다. (4,5번은 **반드시!!!**같은 트랜잭션 안에서 실행한다. 안그러면 인벤 수량은 줄고 리스트엔 없는 참사가 발생한다… 그래서 **반드시!!!!!** 같은 트랜잭션으로 묶고 exception 발생시 rollback으로 되돌리도록 한다.)
    - 구매자의 경우, 해당 책을 구매 LIST에 추가한다.
    - 마지막에 구매자가 지금까지 구매한 책 list를 출력해준다.<br>
   
**3. 검색 결과 (지원)**
  - 검색의 경우 전체 검색 기능과, 각 속성 별 선택 후 검색 기능, 페이징 기능으로 구성되어있다.
    - 리스트 (전체 출력)
      - 정해진 포맷에 맞추어 전체 메뉴 출력
      - 테이블 조인 후 필요한 내용들 SELECT로 선별해서 출력
    
    - 사용자 맞춤형 검색 및 리스트 출력
      - LIKE 와 검색할 항목을 매개변수로 받아 중복되는 코드를 줄임
      - 겹치는 쿼리는 최대한 함수로 분리
    - 페이징 처리
      - 이전, 다음, 책 선택으로 분기
      - 현재 페이지를 매개 변수로 받아 페이징 공식에 대입하여 이전, 다음 페이지 넘겨가며 조회 가능
      - 자바에서 sql쿼리를 한 번 수행하면 수정할 수 없기 때문에 ‘?’를 사용해 바뀌는 값들을 따로 새로 지정 후 처리해주어야 함
        ```
        페이징 공식
        -- P : 현재 페이지, S : 시작 페이지, E : 마지막 페이지
        -- S = P - 1 * 10 + 1
        -- E = P * 10
        ```
     - 모든 리스트에서 실행 후 해당 중고책 아이디와 조인으로 연결되어 있는 상점 아이디를 통해 구매 페이지로 넘어가게 된다. <br>

**4. 책 소개 (하진)**
  - 책 리스트를 보여주고, 해당 책에 대한 상세 정보를 출력.
    - 책 리스트(1)를 선택하여 책 제목 목록들을 보여준다.
    - 리스트를 보고 상세 정보를 볼 책의 인덱스를 입력 받아서 ‘제목, 저자, 출판사, 출판일, 카테고리, 정가, 재고현황’ 을 보여준다. <br>
      (상세 정보를 더 이상 보고 싶지 않다면 exit를 입력 해 상위메뉴로 나갈 수 있다.)
    - 만약 인덱스 이외의 숫자 혹은 문자를 입력한다면 아래와 같은 경고가 뜬다.<br>
      - 문자 입력 시<br>
        `"숫자를 입력하거나 'exit'을 입력해 종료할 수 있습니다.”`<br>
      - 인덱스 이외의 숫자 입력<br>
        `"잘못된 번호입니다. 다시 입력해주세요.”`<br>
    - 메인으로 돌아가기(2)을 누르면 ‘회원가입, 구매, 검색결과, 책 소개, 상점 리뷰 리스트’가 있는 상위메뉴로 나갈 수 있다.
      - 만약 ‘1’ 또는 ‘2’ 이외의 값을 입력한다면 아래와 같다.<br>
        `"잘못된 입력입니다. 1 또는 2를 선택해주세요.”`<br>

        
**5. 상점 리뷰 리스트 (다운)**
  - 상점 리뷰를 확인하는 로직은 다음과 같다.
    - 상점 id를 입력 받는다. 
    - 해당하는 상점의 평균 평점을 출력한다.
      - 입력 받은 상점 id에 해당하는 상점이 존재하지 않을 경우 오류 메세지를 출력한다.<br>
        `"존재하지 않는 상점 ID입니다.”`
    - 해당하는 상점의 리뷰를 모두 출력한다.
      - review_id, review_title, review_date, review_star, review_content를 모두 출력!
      - review_star의 경우 정수로만 저장되어있으니 별 5개 중 해당하는 만큼 채워진 별로 출력!
      - 상점에 작성된 리뷰가 없을 경우 오류 메세지를  출력한다.<br>
        `"해당 상점에 등록된 리뷰가 없습니다.”`<br>

</details>

---


<details>
<summary>4. 구현 결과</summary>



</details>

---



<details>
<summary>5. 회고</summary>

<br>
ERD도, JAVA도 모두 처음 공부해서 어려운 점이 많았지만 해당 주제를 통해 로직과 구현 방법을 익힐 수 있었다.<br>
가장 어려웠던 부분은 ERD 설계 과정에서 관계를 정리하는 부분! 특히 shop과 old book의 관계를 정의할 때에 “old book안에 shop이 없을 수 있음”에 대해 많이 헷갈렸는데 해당 부분을 다른 팀원들도 고민하고 있어서 같이 이야기를 나눠 해결할 수 있었다. <br>
<br>
shop과 review 테이블을 연결하는 과정에서 리뷰를 남길 때에 어떤 상점에 대한 리뷰인지를 알아야하기 때문에 리뷰에 상점 아이디가 FK로 들어가야 하는데 ERD 설계하는 과정에서 이를 실수로 반대 방향으로 연결하는 문제가 있었다. <br>
FK가 잘못 생성되는 바람에 테이블에 더미 데이터를 넣을 때 오류를 겪은 뒤 문제를 알아차렸고. 문제를 해결하면서 테이블 간 관계의 중요성을 다시 느낄 수 있었다.<br> 
<br>
처음 설계한 ERD 1차 초안을 모두 JAVA로 구현할 수 있었다면 더 좋았을텐데 시간 관계 상 축소된 ERD로 구현한 점과 console에서 진행해서 가독성이 떨어지는 부분이 아쉬웠다. <br>
console에 아스키아트를 추가하는 부분도 재밌었고 궁금한 부분과 막히는 부분을 팀원들의 답변과 피드백으로 모두 해결할 수 있어서 많이 배울 수 있는 시간이었다.
