
- **사용자 관리 기능**
    - [x]  로그인 / 회원가입 기능
    - [x]  카카오 소셜로그인
    - [x]  사용자 정보 수정 기능
    - [x]  회원 삭제 기능
- **보드 관리 기능**
    - [ ]  보드 생성
    - [ ]  보드 수정
        - 보드 이름
        - 배경 색상
        - 설명
    - [ ]  보드 삭제
        - 생성한 사용자만 삭제를 할 수 있습니다.
    - [ ]  보드 초대
        - 특정 사용자들을 해당 보드에 초대시켜 협업을 할 수 있어야 합니다.
- **컬럼 관리 기능**
    - [ ]  컬럼 생성
        - 보드 내부에 컬럼을 생성할 수 있어야 합니다.
        - 컬럼이란 위 사진에서 Backlog, In Progress와 같은 것을 의미해요.
    - [ ]  컬럼 이름 수정
    - [ ]  컬럼 삭제
    - [x]  컬럼 순서 이동
        - 컬럼 순서는 자유롭게 변경될 수 있어야 합니다.
            - e.g. Backlog, In Progress, Done → Backlog, Done, In Progress
    - [x] 카드 개수 제한 ( 이동, 수정, 삭제 시 개수 확인 로직 추가 )
- **카드 관리 기능**
    - [ ]  카드 생성
        - 컬럼 내부에 카드를 생성할 수 있어야 합니다.
    - [ ]  카드 수정
        - 카드 이름
        - 카드 설명
        - 카드 색상
        - 작업자 할당
        - 작업자 변경
    - [ ]  카드 삭제
- **카드 상세 기능**
    - [ ]  댓글 달기
        - 협업하는 사람들끼리 카드에 대한 토론이 이루어질 수 있어야 합니다.
    - [ ]  날짜 지정
        - 카드에 마감일을 설정하고 관리할 수 있어야 합니다.
- **동시성 제어**
    - [x] @Version 애터테이션으로 낙관적 락 적용
    - [x] Redis 를 활용한 분산락 적용
    - [x] 낙관적 락, 비관적 락, 분산락의 장단점 토론해보기
