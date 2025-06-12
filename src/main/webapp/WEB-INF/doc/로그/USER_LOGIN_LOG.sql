DROP TABLE user_login_log;

CREATE TABLE user_login_log (
  login_log_id       NUMBER(10)    NOT NULL,
  memberno           number(10)    NOT NULL,
  login_ip           VARCHAR(20)   NOT NULL,
  login_log_date     DATE          DEFAULT SYSDATE NOT NULL, -- 등록 날짜
  logout_log_date    DATE   DEFAULT NULL,
  PRIMARY KEY (login_log_id),
  FOREIGN KEY (memberno) REFERENCES member (memberno)
);

DROP SEQUENCE login_log_seq;

CREATE SEQUENCE login_log_seq
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지