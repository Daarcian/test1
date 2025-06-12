DROP TABLE reply;

CREATE TABLE reply(
        replyno                                NUMBER(10)         NOT NULL         PRIMARY KEY,
        contentsno                           NUMBER(10)    NOT     NULL ,
        memberno                            NUMBER(6)         NOT NULL ,
        content                               VARCHAR2(1000)         NOT NULL,
        rdate                              DATE NOT NULL,
        visible                            VARCHAR(1) DEFAULT 'Y' NOT NULL ,
  FOREIGN KEY (contentsno) REFERENCES contents (contentsno),
  FOREIGN KEY (memberno) REFERENCES member (memberno)
);

COMMENT ON TABLE reply is '댓글';
COMMENT ON COLUMN reply.replyno is '댓글번호';
COMMENT ON COLUMN reply.contentsno is '컨텐츠번호';
COMMENT ON COLUMN reply.memberno is '회원 번호';
COMMENT ON COLUMN reply.content is '내용';
COMMENT ON COLUMN reply.rdate is '등록일';
COMMENT ON COLUMN reply.visible is '출력 여부';

DROP SEQUENCE reply_seq;
CREATE SEQUENCE reply_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1          -- 증가값
  MAXVALUE 9999999999 -- 최대값: 9999999999
  CACHE 2                     -- 2번은 메모리에서만 계산
  NOCYCLE;                   -- 다시 1부터 생성되는 것을 방지
  
    SELECT user.user_id, rep.replyno, rep.contentsno, rep.user_uuid, rep.content, rep.rdate
    FROM chess_user user,  reply rep
    WHERE (user.user_uuid = rep.user_uuid) AND rep.contentsno=1
    ORDER BY rep.replyno DESC
