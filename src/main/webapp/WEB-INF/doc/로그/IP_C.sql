DROP TABLE JOIN_IP;

CREATE TABLE JOIN_IP (
	ip_ip NUMBER(10) NOT NULL PRIMARY KEY,
	ip_address VARCHAR2(200) NOT NULL,
	ip_country_name VARCHAR2(200),
	ip_country_code VARCHAR2(10),
	ip_region_name VARCHAR2(200),
	ip_region_code VARCHAR2(10),
	ip_city_name VARCHAR2(200),
	ip_isp VARCHAR2(100),
	ip_rdate DATE DEFAULT SYSDATE NOT NULL,
	ip_mdate DATE DEFAULT SYSDATE NOT NULL,
	ip_is_block VARCHAR2(1) DEFAULT 'N' NOT NULL,
	ip_is_mobile VARCHAR2(1) DEFAULT 'N' NOT NULL
);

DROP SEQUENCE join_ip_seq;

CREATE SEQUENCE join_ip_seq
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지
