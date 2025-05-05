# 축잘알 테스트 


> ⚽ 축구를 좋아하는 사람들을 위한 랜덤 퀴즈 앱!
> 
> 사용자는 다양한 축구 지식을 테스트하고, 자신의 점수를 기록할 수 있습니다.



### 아이콘

![Image](https://github.com/user-attachments/assets/9a7ae74d-a500-497d-9473-5f60c3be3665)

### 앱 화면

<img src="https://github.com/user-attachments/assets/725e41c9-d657-4e06-ba7c-34fbd39240d0" width="230" height="400"/>ㅤㅤㅤㅤ<img src="https://github.com/user-attachments/assets/9977647f-3406-472c-8759-228acaf482bc" width="230" height="400"/>ㅤㅤㅤㅤ<img src="https://github.com/user-attachments/assets/cdb2f928-c789-4498-b015-06f83d489973" width="230" height="400"/>

<img src="https://github.com/user-attachments/assets/90ca8d40-8c95-4c5c-832c-28208107bae4" width="230" height="400"/>ㅤㅤㅤㅤ<img src="https://github.com/user-attachments/assets/9ccee2d5-8456-4fef-bd4d-6a3576fba610" width="230" height="400"/>ㅤㅤㅤㅤ<img src="https://github.com/user-attachments/assets/9b135d97-2616-4eef-893b-ab55ed931414" width="230" height="400"/>



---

## 📌 주요 기능
- **랜덤 퀴즈 생성** : 매번 다른 문제로 재미있는 풀이 경험 제공
- **정답률 기반 랭킹 시스템** : Redis 캐시를 활용해 빠른 응답성 확보
- **퀴즈 결과 비동기 저장** : RabbitMQ를 활용한 비동기 처리로 성능 병목 제거
- **관리자용 퀴즈 등록 기능** : 새로운 문제를 쉽게 추가 가능
---

## 🛠️ 기술 스택

| 분야 | 기술 |
|:----|:----|
| 백엔드 | Java 21, Spring Boot |
| 데이터베이스 | MySQL, Redis |
| 메시지 큐 | RabbitMQ |
| 클라우드 | AWS EC2, S3 |
| 배포 | Docker, Nginx |
| 테스트 도구 | JMeter (부하 테스트) |
| 템플릿 엔진 | Thymeleaf |
| 프론트엔드 | HTML5, Bootstrap 5, JQuery |


---

## 📁 폴더 구조

├── config # 보안, MVC 설정 등 스프링 설정 파일  
├── controller # HTTP 요청을 처리하는 API 엔드포인트  
├── domain # JPA 엔티티 (DB 테이블 매핑)  
├── dto # 요청/응답 데이터 전송 객체 (DTO)  
├── infra  
│   ├── rabbitmq # RabbitMQ 설정 및 프로듀서/컨슈머  
│   ├── redis # Redis 설정 및 캐시 동기화  
│   └── s3 # AWS S3 업로드 기능  
├── repository # 데이터베이스 접근 레이어 (JPA Repository)  
├── service # 핵심 비즈니스 로직 처리  
├── validate # 커스텀 유효성 검사 로직  
├── QuizGameApplication.java # 스프링 부트 메인 실행 파일  
└── resources  
    ├── static # 정적 리소스 (CSS, JS 등)  
    └── templates # Thymeleaf HTML 템플릿  


---

## ✅ 주요 성과

배포 중 접속 끊김 이슈를 해결하기 위해 Docker + Nginx 기반 포트 스위칭 방식 무중단 배포 환경 구축

랜덤 퀴즈 데이터 조회 지연을 해결하기 위해 Redis 캐싱 도입으로 응답 속도 3s → 1s, (약 68% 개선)

랭킹 데이터 조회 지연을 개선하고자 Redis Sorted Set 적용으로 평균 속도 6s → 2s, (약 67% 개선)

반복 이미지 요청으로 인한 비용 증가 문제를 클라이언트 캐싱 적용으로 S3 접근 절감

퀴즈 결과 저장 API의 정합성 이슈와 성능 병목을 해결하기 위해 RabbitMQ 기반 비동기 구조로 리팩토링

    -응답 시간 6,570ms → 1ms, 처리량 242/sec → 1,333/sec, 데이터 누락 0건
    
🔗 **기술 블로그 :**  [무중단 배포 구축기](https://evga7.tistory.com/155) / [퀴즈 캐싱 성능 개선](https://evga7.tistory.com/153) / [랭킹 조회 개선](https://evga7.tistory.com/154) / [RabbitMQ 비동기 리팩토링](https://evga7.tistory.com/159)

---

## 주요 설계 및 기술 도입 이유

### 1. Redis 도입 이유

사용자 입장에서 앱을 사용해보면서 느낀 가장 큰 불편함은 '퀴즈 랭킹'이나 '퀴즈 조회' 시 응답이 늦다는 점이었습니다.

EC2 무료 서버를 사용하다보니 리소스가 제한적이었고, 이를 고려해 최대한 빠른 조회 속도를 확보해야 했습니다.

Redis는 메모리 기반 인메모리 데이터 저장소로, 디스크 기반 DB보다 데이터 조회 속도가 빠르고

데이터 정합성만 크게 문제가 되지 않는 퀴즈 캐싱/랭킹 조회 같은 영역에서는 MySQL 대신 Redis를 사용하는 것이 효율적이라고 판단했습니다.

대체 기술로는 Memcached도 고려할 수 있었지만, Redis는 단순 Key-Value 저장 외에도 Sorted Set 등을 활용해 랭킹 기능을 쉽게 구현할 수 있었기 때문에 선택했습니다.

---


### 2. RabbitMQ 도입 이유

만약 앱의 사용자가 폭증하게 된다면, 결과 저장 요청이 동시에 몰릴 수 있다고 예상했습니다.

이 경우, 동기 방식(요청 → 저장 완료 → 응답)이면 서버 부하가 심해지고 정합성 이슈나 서버 다운이 발생할 가능성이 있었습니다.

이를 대비해 비동기 메시지 큐 방식을 적용하기로 했습니다.

Spring Boot와의 연동이 쉬운 AMQP 기반 솔루션 중에서 RabbitMQ를 선택했습니다.

다른 대체 기술로 Kafka도 고려할 수 있었지만, Kafka는 설치 및 운영 복잡도가 상대적으로 높아 신속한 개발에는 RabbitMQ가 더 적합하다고 판단했습니다.

### 3. Nginx 도입 이유

서비스 운영 중에는 버전 업데이트나 롤백 등 배포 작업이 필수적으로 발생하는데, 이 과정에서 서버가 잠깐이라도 끊기는 경험은 사용자에게 매우 불편할 수 있습니다.

이를 해결하기 위해, Nginx를 리버스 프록시 서버로 두어 무중단 배포 환경을 구성했습니다.

Docker 컨테이너의 포트 스위칭 방식과 연결하여, 배포 중에도 서비스 연결을 끊지 않고 유지할 수 있도록 했습니다.

추가로, Nginx는 SSL 인증서(HTTPS) 자동 갱신과 적용(Let's Encrypt)도 매우 쉽게 설정할 수 있어, 보안 강화를 위한 HTTPS 적용도 간편하게 구현할 수 있었습니다.

부가적으로, 정적 파일 제공(예: 서비스 소개 페이지 등)이나 요청 라우팅에도 유연하게 대응할 수 있어 관리 편의성도 향상되었습니다.

---

## 📈 아키텍처 구조
![Image](https://github.com/user-attachments/assets/1237e766-5d19-484b-b25e-146c3090c038)

## 📈 ERD

![Image](https://github.com/user-attachments/assets/feece0ff-0765-4f4f-9c95-a9bb396ab770)

---

